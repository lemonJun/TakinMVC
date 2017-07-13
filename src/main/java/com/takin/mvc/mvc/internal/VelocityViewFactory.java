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
package com.takin.mvc.mvc.internal;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.io.VelocityWriter;

import com.takin.mvc.mvc.ActionResult;
import com.takin.mvc.mvc.BeatContext;
import com.takin.mvc.mvc.InitHelper;
import com.takin.mvc.mvc.cache.PageCache;
import com.takin.mvc.mvc.exception.WFException;
import com.takin.mvc.mvc.view.ViewFactory;

/**
 * @author lemon
 */
@Singleton
public class VelocityViewFactory implements ViewFactory {

    @Inject
    public VelocityViewFactory(ServletContext sc) {

        //    	String viewFolder = viewFolderPath();
        String webAppPath = sc.getRealPath("/");

        Velocity.setProperty("resource.loader", "file");
        Velocity.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
        Velocity.setProperty("file.resource.loader.path", webAppPath);
        Velocity.setProperty("file.resource.loader.cache", "false");
        Velocity.setProperty("file.resource.loader.modificationCheckInterval", "2");
        Velocity.setProperty("input.encoding", "UTF-8");
        Velocity.setProperty("output.encoding", "UTF-8");
        Velocity.setProperty("default.contentType", "text/html; charset=UTF-8");
        Velocity.setProperty("velocimarco.library.autoreload", "true");
        Velocity.setProperty("runtime.log.error.stacktrace", "false");
        Velocity.setProperty("runtime.log.warn.stacktrace", "false");
        Velocity.setProperty("runtime.log.info.stacktrace", "false");
        Velocity.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
        Velocity.setProperty("runtime.log.logsystem.log4j.category", "velocity_log");

        //Velocity.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.Log4JLogChute" );

        try {
            Velocity.init();
        } catch (Exception e) {
            throw WFException.raise(e);
        }
    }

    @Override
    public ActionResult view(String viewName) {
        return new VelocityViewResult(viewName);
    }

    private static class VelocityViewResult extends ActionResult {

        private final String suffix = ".html";

        private final String viewName;

        private VelocityViewResult(String viewName) {
            this.viewName = viewName;
        }

        @Override
        public void render(BeatContext beat) {
            String path = "views" + "\\" + viewName + suffix;
            //            String path = "" + "\\" + viewName + suffix;

            Template template = Velocity.getTemplate(path);
            HttpServletResponse response;
            if (beat.getModel().get("needcache") != null && beat.getModel().get("__TRACEINFO") == null)

                //TODO : check me 这样处理缓存是否合理。            
                response = PageCache.cacheResponseWrapper();
            else
                response = beat.getResponse();

            response.setContentType("text/html;charset=\"UTF-8\"");
            response.setCharacterEncoding("UTF-8");
            // init context:
            Context context = new VelocityContext(beat.getModel().getModel());
            // render:
            VelocityWriter vw = null;
            try {
                vw = new VelocityWriter(response.getWriter());
                template.merge(context, vw);
                vw.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                vw.recycle(null);
            }

            if (beat.getModel().get("needcache") != null && beat.getModel().get("__TRACEINFO") == null) {
                PageCache.setCacheResult(response);
            }
        }
    }

    private String viewFolderPath() {
        File parent = InitHelper.instance.currentFolder();
        return new File(parent, "views").getAbsolutePath();
    }
}
