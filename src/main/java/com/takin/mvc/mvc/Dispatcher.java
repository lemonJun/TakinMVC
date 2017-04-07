/*
*  Copyright Beijing 58 Information Technology Co.,Ltd.
*
*  Licensed to the Apache Software Foundation (ASF) under one
*  or more contributor license agreements.  See the NOTICE file
*  distributed with this work for additional information
*  regarding copyright ownership.  The ASF licenses this file
*  to you under the Apache License, Version 2.0 (the
*  "License"); you may not use this file except in compliance
*  with the License.  You may obtain a copy of the License at
*
*        http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package com.takin.mvc.mvc;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Key;
import com.google.inject.OutOfScopeException;
import com.takin.mvc.mvc.annotation.Async;
import com.takin.mvc.mvc.client.UploadRequest;
import com.takin.mvc.mvc.inject.GuiceDI;
import com.takin.mvc.mvc.inject.WFSystem;
import com.takin.mvc.mvc.internal.actionresult.StatusCodeActionResult;
import com.takin.mvc.mvc.monitor.ActionTimeMonitor;
import com.takin.mvc.mvc.route.Router;
import com.takin.mvc.util.OnlyOnceCondition;

/**
 *
 * 用于处理Rest请求调度的核心类
 * 此处是否应该启用线程池啊  要不加这个threadlocal的类也没啥用处啊
 * 或是后期改成AKKA 否则此处的性能要怎么提升   后面可以参考一下jfinnal的实现  
 * 已测试 在单线程下性能确实很差   为啥当时会这样呢
 */
@Singleton
public class Dispatcher {

    private final Key<BeatContext> defaultBeatContextKey = Key.get(BeatContext.class, WFSystem.class);

    private static final Logger logger = LoggerFactory.getLogger(Dispatcher.class);

    final ThreadLocal<Context> localContext = new ThreadLocal<Context>();

    public void init() {
        GuiceDI.getInstance(Router.class);
    }

    public void service(HttpServletRequest request, HttpServletResponse response) {
        BeatContext beat = bindBeatContext(request, response);
        route(beat);
    }

    private BeatContext bindBeatContext(HttpServletRequest request, HttpServletResponse response) {

        request = UploadRequest.wrapper(request);

        Context context = new Context(request, response);
        localContext.set(context);

        BeatContext beat = GuiceDI.getInstance(defaultBeatContextKey);
        context.setBeat(beat);
        // 设置系统性变量
        beat.getModel().add("__beat", beat);
        return beat;

    }

    //
    private void route(BeatContext beat) {
        ActionTimeMonitor actionTimeMonitor = ActionTimeMonitor.Factory.create();
        try {
            logger.info("mvc thread:" + Thread.currentThread().getName() + " is running");
            ActionResult result = GuiceDI.getInstance(Router.class).route(beat);
            
            if (ActionResult.NULL == result)
                result = GuiceDI.getInstance(StatusCodeActionResult.class).getSc404();
            
            // 判断当前请求的ActionResult是否为异步请求
            boolean isAsyncRequest = beat.getAction() != null && beat.getAction().getActionMethod().isAnnotationPresent(Async.class);

            if (!isAsyncRequest)
                result.render(beat);

        } catch (Exception e) {
            GuiceDI.getInstance(StatusCodeActionResult.class).render405(beat);
            logger.error(String.format("fail to route. url:%s", beat.getClient().getRelativeUrl()), e);
        } finally {
            //是否发送  应该设置一个注解的开关的
            //actionTimeMonitor.post();
            localContext.remove();
        }
    }

    public HttpServletRequest currentRequest() {
        return getContext().getRequest();
    }

    public HttpServletResponse currentResponse() {
        return getContext().getResponse();
    }

    public BeatContext currentBeatContext() {
        return getContext().getBeat();
    }

    private Context getContext() {
        Context context = localContext.get();
        if (context == null) {
            throw new OutOfScopeException("Cannot access scoped object. Either we" + " are not currently inside an HTTP Servlet currentRequest, or you may" + " have forgotten to apply " + Dispatcher.class.getName() + " as a servlet filter for this currentRequest.");
        }
        return context;
    }

    private static class Context {

        final HttpServletRequest request;
        final HttpServletResponse response;

        BeatContext beat;

        OnlyOnceCondition onlyOnce = OnlyOnceCondition.create("The current beat has been created.");

        Context(HttpServletRequest request, HttpServletResponse response) {
            this.request = request;
            this.response = response;
        }

        HttpServletRequest getRequest() {
            return request;
        }

        HttpServletResponse getResponse() {
            return response;
        }

        BeatContext getBeat() {
            return beat;
        }

        void setBeat(BeatContext beat) {
            onlyOnce.check();
            this.beat = beat;
        }
    }

}
