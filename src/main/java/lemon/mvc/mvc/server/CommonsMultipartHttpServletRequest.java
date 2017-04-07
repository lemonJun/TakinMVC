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
package lemon.mvc.mvc.server;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import lemon.mvc.spring.LinkedMultiValueMap;
import lemon.mvc.spring.MultiValueMap;

/**
 * 用于做文件上传时的参数处理
 * 
 * @author lemon
 */
public class CommonsMultipartHttpServletRequest extends HttpServletRequestWrapper implements HttpServletRequest {

    private Map<String, String[]> multipartParameters;

    private MultiValueMap<String, CommonsMultipartFile> multipartFiles;

    /**
     * Wrap the given HttpServletRequest in a MultipartHttpServletRequest.
     * @param request the servlet request to wrap
     * @param mpFiles a map of the multipart files
     * @param mpParams a map of the parameters to expose,
     * with Strings as keys and String arrays as values
     */
    public CommonsMultipartHttpServletRequest(HttpServletRequest request, MultiValueMap<String, CommonsMultipartFile> mpFiles, Map<String, String[]> mpParams) {

        super(request);
        setMultipartFiles(mpFiles);
        setMultipartParameters(mpParams);
    }

    /**
     * Wrap the given HttpServletRequest in a MultipartHttpServletRequest.
     * @param request the servlet request to wrap
     */
    public CommonsMultipartHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        Set<String> paramNames = new HashSet<String>();
        Enumeration paramEnum = super.getParameterNames();
        while (paramEnum.hasMoreElements()) {
            paramNames.add((String) paramEnum.nextElement());
        }
        paramNames.addAll(getMultipartParameters().keySet());
        return Collections.enumeration(paramNames);
    }

    @Override
    public String getParameter(String name) {
        String[] values = getMultipartParameters().get(name);
        if (values != null) {
            return (values.length > 0 ? values[0] : null);
        }
        return super.getParameter(name);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = getMultipartParameters().get(name);
        if (values != null) {
            return values;
        }
        return super.getParameterValues(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> paramMap = new HashMap<String, String[]>();
        paramMap.putAll(super.getParameterMap());
        paramMap.putAll(getMultipartParameters());
        return paramMap;
    }

    /**
     * Set a Map with parameter names as keys and String array objects as values.
     * To be invoked by subclasses on initialization.
     */
    protected final void setMultipartParameters(Map<String, String[]> multipartParameters) {
        this.multipartParameters = multipartParameters;
    }

    /**
     * Obtain the multipart parameter Map for retrieval,
     * lazily initializing it if necessary.
     * @see #initializeMultipart()
     */
    protected Map<String, String[]> getMultipartParameters() {
        if (this.multipartParameters == null) {
            initializeMultipart();
        }
        return this.multipartParameters;
    }

    public Iterator<String> getFileNames() {
        return getMultipartFiles().keySet().iterator();
    }

    public CommonsMultipartFile getFile(String name) {
        return getMultipartFiles().getFirst(name);
    }

    public List<CommonsMultipartFile> getFiles(String name) {
        List<CommonsMultipartFile> multipartFiles = getMultipartFiles().get(name);
        if (multipartFiles != null) {
            return multipartFiles;
        } else {
            return Collections.emptyList();
        }
    }

    public Map<String, CommonsMultipartFile> getFileMap() {
        return getMultipartFiles().toSingleValueMap();
    }

    public MultiValueMap<String, CommonsMultipartFile> getMultiFileMap() {
        return getMultipartFiles();
    }

    /**
     * Set a Map with parameter names as keys and list of MultipartFile objects as values.
     * To be invoked by subclasses on initialization.
     */
    protected final void setMultipartFiles(MultiValueMap<String, CommonsMultipartFile> multipartFiles) {
        this.multipartFiles = new LinkedMultiValueMap<String, CommonsMultipartFile>(Collections.unmodifiableMap(multipartFiles));
    }

    /**
     * Obtain the MultipartFile Map for retrieval,
     * lazily initializing it if necessary.
     * @see #initializeMultipart()
     */
    protected MultiValueMap<String, CommonsMultipartFile> getMultipartFiles() {
        if (this.multipartFiles == null) {
            initializeMultipart();
        }
        return this.multipartFiles;
    }

    /**
     * Lazily initialize the multipart request, if possible.
     * Only called if not already eagerly initialized.
     */
    protected void initializeMultipart() {
        throw new IllegalStateException("Multipart request not initialized");
    }
}
