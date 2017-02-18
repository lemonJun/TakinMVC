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
package lemon.mvc.mvc.client;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lemon.mvc.mvc.multipart.commons.CommonsMultipartFile;
import lemon.mvc.mvc.multipart.commons.CommonsMultipartHttpServletRequest;
import lemon.mvc.mvc.multipart.commons.CommonsMultipartResolver;

/**
 * 
 * 上传的请求封装类。
 * 
 *
 * @author lemon
 *
 */
public class UploadRequest extends HttpServletRequestWrapper {

    CommonsMultipartHttpServletRequest springRquest;

    private static final Logger logger = LoggerFactory.getLogger(UploadRequest.class);

    public UploadRequest(HttpServletRequest request) {
        super(request);

        CommonsMultipartResolver commonsMultipartResolver;
        // 创建一个通用的多部分解析器.
        commonsMultipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        commonsMultipartResolver.setDefaultEncoding("utf-8");

        // 转换成多部分request
        try {
            springRquest = commonsMultipartResolver.resolveMultipart(request);
        } catch (Exception e) {
            logger.error("can not init UploadRequest");
        }

        //springRquest = new DefaultMultipartHttpServletRequest(request);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return springRquest.getParameterNames();
    }

    @Override
    public String getParameter(String name) {
        return springRquest.getParameter(name);
    }

    @Override
    public String[] getParameterValues(String name) {
        return springRquest.getParameterValues(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return springRquest.getParameterMap();
    }

    public Iterator<String> getFileNames() {
        return springRquest.getFileNames();
    }

    public RequestFile getFile(String name) {
        return new RequestFile(springRquest.getFile(name));
    }

    public List<RequestFile> getFiles(String name) {
        List<RequestFile> result = new ArrayList<RequestFile>();
        for (CommonsMultipartFile f : springRquest.getFiles(name)) {
            result.add(new RequestFile(f));
        }

        return result;
    }

    public static boolean isMultipart(HttpServletRequest request) {
        return (request != null && ServletFileUpload.isMultipartContent(request));
    }

    public static HttpServletRequest wrapper(HttpServletRequest request) {
        return isMultipart(request) ? new UploadRequest(request) : request;
    }

}
