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
package com.takin.mvc.mvc.client;

import java.net.InetAddress;

import javax.inject.Inject;
import javax.inject.Scope;
import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Preconditions;
import com.google.common.net.InetAddresses;
import com.google.inject.ImplementedBy;
import com.takin.mvc.mvc.Dispatcher;
import com.takin.mvc.mvc.ReverseProxy;
import com.takin.mvc.mvc.inject.MVCDI;

/**
 * 获得客户端信息
 * @author lemon
 *
 */
@ImplementedBy(ClientContext.DefaultClientContext.class)
public interface ClientContext {

    /**
     * 获得远程用户cookies
     * @return cookies
     */
    com.takin.mvc.mvc.client.CookieHandler getCookies();

    /**
     * 得到当前请求的url,不包括参数
     * @return 当前请求的url
     */
    String getRelativeUrl();

    /**
     * 获得上传文件信息
     * @return 上传文件信息
     */
    UploadRequest getUploads();

    boolean isUpload();

    /**
     * 获得用户ip
     * @return 用户ip
     */
    InetAddress getAddress();

    //每次请求都会产生一个  此处不该用@Singleton注解啊
    public static class DefaultClientContext implements ClientContext {

        private final HttpServletRequest request;
        private final ReverseProxy reverseProxy;

        private CookieHandler cookies = null;
        private String relativeUrl = null;
        private UploadRequest uploads = null;
        private InetAddress address = null;

        @Inject
        public DefaultClientContext(ReverseProxy reverseProxy) {
            this.request = MVCDI.getInstance(Dispatcher.class).currentRequest();
            Preconditions.checkNotNull(request);
            this.reverseProxy = reverseProxy;

        }

        @Override
        public com.takin.mvc.mvc.client.CookieHandler getCookies() {
            if (cookies != null)
                return cookies;

            return cookies = MVCDI.getInstance(com.takin.mvc.mvc.client.CookieHandler.class);
            //             Cookie[] cks = request.getCookies();
            //             cookies = WFGod.instance.getInstance(com.bj58.wf.mvc.client.CookieHandler.class);
            //             
            //             if(cks == null ||cks.length == 0)
            //              return cookies;
            //             
            //             for (Cookie ck : cks) {
            //                 cookies.add(ck);
            //             }
            //             return cookies;
        }

        @Override
        public String getRelativeUrl() {
            if (relativeUrl != null)
                return relativeUrl;

            String uri = request.getRequestURI();
            String contextPath = request.getContextPath();
            relativeUrl = uri.substring(contextPath.length());

            return relativeUrl;
        }

        @Override
        public UploadRequest getUploads() {

            return (request instanceof UploadRequest) ? (UploadRequest) request : null;
        }

        @Override
        public InetAddress getAddress() {
            if (address != null)
                return address;

            //TODO:synchronized
            address = gerRemoteAddress();
            if (!reverseProxy.isCluster(address))
                return address;

            return reverseProxy.getRemoteAddress(request);
        }

        protected InetAddress gerRemoteAddress() {
            return InetAddresses.forString(request.getRemoteAddr());
        }

        @Override
        public boolean isUpload() {
            return getUploads() == null ? false : true;
        }
    }

}
