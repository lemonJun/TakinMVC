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

import java.util.Map;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.ImplementedBy;
import com.takin.mvc.mvc.bind.BeatBindResults;
import com.takin.mvc.mvc.client.ClientContext;
import com.takin.mvc.mvc.internal.DefaultBeatContext;
import com.takin.mvc.mvc.server.ServerContext;

/**
 * 管理一个客户端请求的生命周期
 *
 */
@ImplementedBy(DefaultBeatContext.class)
public interface BeatContext {

    /**
     * MVC 中的Model, 以key,value形式存放，可以由Controller传个View
     *
     * @return
     */
    public Model getModel();

    /**
     * 返回本次调用的 {@link HttpServletRequest}对象
     *
     * @return
     */
    public HttpServletRequest getRequest();

    /**
     * 返回本次调用的 {@link HttpServletResponse}对象
     *
     * @return
     */
    HttpServletResponse getResponse();

    /**
     * 得到ServletContext信息
     *
     * @return
     */
    ServletContext getServletContext();

    /**
     * 获得客户端的信息
     *
     * @return 客户端信息
     */
    ClientContext getClient();

    /**
     * 兼容
     * @return
     */
    ServerContext getServer();

    ActionAttribute getAction();

    BeatBindResults getBindResults();

    String getRelativeUrl();

    /**
     * 
     * MVC 中的Model, 以key,value形式存放，可以由Controller传个View
     * 
     * @author liuzw@58.com
     *
     */

    public class Model implements com.takin.mvc.mvc.Model {

        /** Model Map */
        @Inject
        private com.takin.mvc.mvc.Model model;

        /**
         * 已过期，请参考{@link #add(Object)}
         * 增加一个属性
         * @param attributeValue 属性值
         */
        @Deprecated
        public com.takin.mvc.mvc.Model addAttribute(Object attributeValue) {
            return add(attributeValue);

        }

        /**
         * 增加一个属性
         * @param attributeValue 属性值
         */
        public com.takin.mvc.mvc.Model add(Object attributeValue) {

            return model.add(attributeValue);
        }

        /**
         * 已过期，请参考{@link #add(String, Object)}
         * 增加一个属性
         * @param attributeName 属性名称
         * @param attributeValue 属性值
         */
        @Deprecated
        public com.takin.mvc.mvc.Model addAttribute(String attributeName, Object attributeValue) {
            return add(attributeName, attributeValue);

        }

        /**
         * 
         * 增加一个属性
         * @param attributeName 属性名称
         * @param attributeValue 属性值
         */
        public com.takin.mvc.mvc.Model add(String attributeName, Object attributeValue) {
            return model.add(attributeName, attributeValue);

        }

        /**
         * 已过期，请参考{@link #get(String)}
         * 
         * 根据属性名得到属性值
         * @param attributeName 属性名称
         * @return 对应的属性值
         */
        @Deprecated
        public Object getAttributeValue(String attributeName) {
            return get(attributeName);
        }

        /**
         * 根据属性名得到属性值
         * @param attributeName 属性名称
         * @return 对应的属性值
         */

        public Object get(String attributeName) {
            return model.get(attributeName);
        }

        /**
         * Return the model map. Never returns <code>null</code>.
         * To be called by application code for modifying the model.
         */
        public Map<String, Object> getModel() {
            return model.getModel();
        }

        /**
         * 已过期，请参考{@link #addAll(Map)}
         * 批量增加属性
         * @param attributes
         */
        @Deprecated
        public com.takin.mvc.mvc.Model addAllAttributes(Map<String, ?> attributes) {
            return addAll(attributes);
        }

        /**
         * 批量增加属性
         * @param attributes
         */
        public com.takin.mvc.mvc.Model addAll(Map<String, ?> attributes) {
            return model.addAll(attributes);
        }

        /**
         * 已过期，请参考{@link contains(Object)}
         * 判断是否包含属性名
         * @param attributeName 需要查找的属性
         * @return
         */
        @Deprecated
        public boolean containsAttribute(String attributeName) {
            return contains(attributeName);
        }

        /**
         * 判断是否包含属性名
         * @param attributeName 需要查找的属性
         * @return
         */
        public boolean contains(String attributeName) {
            return model.contains(attributeName);
        }

        /**
         * 合并属性
         * @param attributes
         */
        public com.takin.mvc.mvc.Model merge(Map<String, ?> attributes) {

            return model.merge(attributes);
        }

    }
}