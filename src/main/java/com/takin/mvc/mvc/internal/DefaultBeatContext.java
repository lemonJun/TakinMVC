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

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.takin.mvc.mvc.ActionAttribute;
import com.takin.mvc.mvc.BeatContext;
import com.takin.mvc.mvc.Dispatcher;
import com.takin.mvc.mvc.WFHttpServletRequestWrapper;
import com.takin.mvc.mvc.bind.BeatBindResults;
import com.takin.mvc.mvc.client.ClientContext;
import com.takin.mvc.mvc.inject.GuiceDI;
import com.takin.mvc.mvc.inject.WFSystem;
import com.takin.mvc.mvc.server.ServerContext;

/**
 * 
 */
@WFSystem
public class DefaultBeatContext implements BeatContext {

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    private final Model model;

    private final ClientContext clientContext;

    private final ServletContext servletContext;

    private final BeatBindResults beatBindResults;

    private final String relativeUrl;

    //    private final CacheContext cache;

    @Inject
    public DefaultBeatContext(Model model, ClientContext clientContext, ServletContext servletContext, BeatBindResults beatBindResults) {
        this.request = new WFHttpServletRequestWrapper(GuiceDI.getInstance(Dispatcher.class).currentRequest());
        this.response = GuiceDI.getInstance(Dispatcher.class).currentResponse();
        this.model = model;
        this.clientContext = clientContext;
        this.servletContext = servletContext;
        this.beatBindResults = beatBindResults;

        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        relativeUrl = uri.substring(contextPath.length());
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public HttpServletRequest getRequest() {

        return request;
    }

    @Override
    public HttpServletResponse getResponse() {
        return response;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public ClientContext getClient() {
        return clientContext;
    }

    @Override

    public ServerContext getServer() {

        return GuiceDI.getInstance(ServerContext.class);
    }

    @Override
    public ActionAttribute getAction() {
        return (ActionAttribute) GuiceDI.getInstance(Dispatcher.class).currentBeatContext().getModel().get("actionMethod");
    }

    @Override
    public String getRelativeUrl() {

        return relativeUrl;
    }

    @Override
    public BeatBindResults getBindResults() {

        return beatBindResults;
    }

}
