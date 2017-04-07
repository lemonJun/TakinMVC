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

import com.google.common.net.InetAddresses;
import com.google.inject.ImplementedBy;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;

/**
 * 
 * 
 */
@ImplementedBy(ReverseProxy.DefaultReverseProxy.class)
public interface ReverseProxy {

    /**
     * a判断请求的ip是否是当前集群ip
     * @param address 需要判断的ip地址
     * @return true:是代理服务器地址; false:不是代理服务器地址，用户真实地址
     */
    boolean isCluster(InetAddress address);

    /**
     * 从当前request头中获得用户的ip
     *
     * @param request 当前request
     * @return ip
     */
    InetAddress getRemoteAddress(HttpServletRequest request);

    public static class DefaultReverseProxy implements ReverseProxy {

        @Override
        public boolean isCluster(InetAddress address) {
            return !address.isSiteLocalAddress();

        }

        @Override
        public InetAddress getRemoteAddress(HttpServletRequest request) {
            return InetAddresses.forString(getRemoteAddressByRequest(request));
        }

        protected String getRemoteAddressByRequest(HttpServletRequest request) {
            return request.getHeader("x-forwarded-for");

            //            if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            //                ip = request.getRemoteAddr();
            //            }
            //            return ip;
        }
    }

}
