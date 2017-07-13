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
package com.takin.mvc.mvc.server;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.takin.mvc.util2.Assert;
import com.takin.mvc.util2.LinkedMultiValueMap;
import com.takin.mvc.util2.MultiValueMap;
import com.takin.mvc.util2.StringUtils;

/**
 * 文件上传的解析类，基于Jakarta Commons FileUpload
 * 主要功能用于保存临时文件等。
 * 
 * @author lemon
 */
public class CommonsMultipartResolver {

    private boolean resolveLazily = false;
    /**
     * Standard Servlet spec context attribute that specifies a temporary
     * directory for the current web application, of type <code>java.io.File</code>.
     */
    public static final String TEMP_DIR_CONTEXT_ATTRIBUTE = "javax.servlet.context.tempdir";

    private static final Logger logger = LoggerFactory.getLogger(CommonsMultipartResolver.class);

    private final DiskFileItemFactory fileItemFactory;

    private final FileUpload fileUpload;

    private boolean uploadTempDirSpecified = false;

    private static final String DEFAULT_CHARACTER_ENCODING = "ISO-8859-1";

    /**
     * Constructor for use as bean. Determines the servlet container's
     * temporary directory via the ServletContext passed in as through the
     * ServletContextAware interface (typically by a WebApplicationContext).
     * @see #setServletContext
     * @see org.springframework.web.context.ServletContextAware
     * @see org.springframework.web.context.WebApplicationContext
     */

    public CommonsMultipartResolver() {

        this.fileItemFactory = newFileItemFactory();
        this.fileUpload = newFileUpload(getFileItemFactory());
    }

    /**
     * Constructor for standalone usage. Determines the servlet container's
     * temporary directory via the given ServletContext.
     * @param servletContext the ServletContext to use
     */
    public CommonsMultipartResolver(ServletContext servletContext) {
        this();
        setServletContext(servletContext);
    }

    /**
     * Set whether to resolve the multipart request lazily at the time of
     * file or parameter access.
     * <p>Default is "false", resolving the multipart elements immediately, throwing
     * corresponding exceptions at the time of the {@link #resolveMultipart} call.
     * Switch this to "true" for lazy multipart parsing, throwing parse exceptions
     * once the application attempts to obtain multipart files or parameters.
     */
    public void setResolveLazily(boolean resolveLazily) {
        this.resolveLazily = resolveLazily;
    }

    /**
     * Initialize the underlying <code>org.apache.commons.fileupload.servlet.ServletFileUpload</code>
     * instance. Can be overridden to use a custom subclass, e.g. for testing purposes.
     * @param fileItemFactory the Commons FileItemFactory to use
     * @return the new ServletFileUpload instance
     */

    protected FileUpload newFileUpload(FileItemFactory fileItemFactory) {
        return new ServletFileUpload(fileItemFactory);
    }

    public void setServletContext(ServletContext servletContext) {
        if (!isUploadTempDirSpecified()) {
            getFileItemFactory().setRepository(getTempDir(servletContext));
        }
    }

    public boolean isMultipart(HttpServletRequest request) {
        return (request != null && ServletFileUpload.isMultipartContent(request));
    }

    public CommonsMultipartHttpServletRequest resolveMultipart(final HttpServletRequest request) throws Exception {
        Assert.notNull(request, "Request must not be null");
        if (this.resolveLazily) {
            return new CommonsMultipartHttpServletRequest(request) {
                @Override
                protected void initializeMultipart() {
                    MultipartParsingResult parsingResult;
                    try {
                        parsingResult = parseRequest(request);
                        setMultipartFiles(parsingResult.getMultipartFiles());
                        setMultipartParameters(parsingResult.getMultipartParameters());
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        logger.error("Could not parse multipart servlet request", e);

                    }

                }
            };
        } else {
            MultipartParsingResult parsingResult = parseRequest(request);
            return new CommonsMultipartHttpServletRequest(request, parsingResult.getMultipartFiles(), parsingResult.getMultipartParameters());
        }
    }

    /**
     * Parse the given servlet request, resolving its multipart elements.
     * @param request the request to parse
     * @return the parsing result
     * @throws MultipartException if multipart resolution failed.
     */
    @SuppressWarnings("unchecked")
    protected MultipartParsingResult parseRequest(HttpServletRequest request) throws Exception {
        String encoding = determineEncoding(request);
        FileUpload fileUpload = prepareFileUpload(encoding);
        try {
            List<FileItem> fileItems = ((ServletFileUpload) fileUpload).parseRequest(request);
            return parseFileItems(fileItems, encoding);
        } catch (Exception ex) {
            throw new Exception("Could not parse multipart servlet request", ex);
        }
    }

    /**
     * Determine the encoding for the given request.
     * Can be overridden in subclasses.
     * <p>The default implementation checks the request encoding,
     * falling back to the default encoding specified for this resolver.
     * @param request current HTTP request
     * @return the encoding for the request (never <code>null</code>)
     * @see javax.servlet.ServletRequest#getCharacterEncoding
     * @see #setDefaultEncoding
     */
    protected String determineEncoding(HttpServletRequest request) {
        String encoding = request.getCharacterEncoding();
        if (encoding == null) {
            encoding = getDefaultEncoding();
        }
        return encoding;
    }

    public void cleanupMultipart(CommonsMultipartHttpServletRequest request) {
        if (request != null) {
            try {
                cleanupFileItems(request.getMultiFileMap());
            } catch (Throwable ex) {
                logger.warn("Failed to perform multipart cleanup for servlet request", ex);
            }
        }
    }

    /**
    * Return the temporary directory for the current web application,
    * as provided by the servlet container.
    * @param servletContext the servlet context of the web application
    * @return the File representing the temporary directory
    */
    private static File getTempDir(ServletContext servletContext) {
        Assert.notNull(servletContext, "ServletContext must not be null");
        return (File) servletContext.getAttribute(TEMP_DIR_CONTEXT_ATTRIBUTE);
    }

    /**
     * Return the underlying <code>org.apache.commons.fileupload.disk.DiskFileItemFactory</code>
     * instance. There is hardly any need to access this.
     * @return the underlying DiskFileItemFactory instance
     */
    public DiskFileItemFactory getFileItemFactory() {
        return this.fileItemFactory;
    }

    /**
     * Return the underlying <code>org.apache.commons.fileupload.FileUpload</code>
     * instance. There is hardly any need to access this.
     * @return the underlying FileUpload instance
     */
    public FileUpload getFileUpload() {
        return this.fileUpload;
    }

    /**
     * Set the maximum allowed size (in bytes) before uploads are refused.
     * -1 indicates no limit (the default).
     * @param maxUploadSize the maximum upload size allowed
     * @see org.apache.commons.fileupload.FileUploadBase#setSizeMax
     */
    public void setMaxUploadSize(long maxUploadSize) {
        this.fileUpload.setSizeMax(maxUploadSize);
    }

    /**
     * Set the maximum allowed size (in bytes) before uploads are written to disk.
     * Uploaded files will still be received past this amount, but they will not be
     * stored in memory. Default is 10240, according to Commons FileUpload.
     * @param maxInMemorySize the maximum in memory size allowed
     * @see org.apache.commons.fileupload.disk.DiskFileItemFactory#setSizeThreshold
     */
    public void setMaxInMemorySize(int maxInMemorySize) {
        this.fileItemFactory.setSizeThreshold(maxInMemorySize);
    }

    /**
     * Set the default character encoding to use for parsing requests,
     * to be applied to headers of individual parts and to form fields.
     * Default is ISO-8859-1, according to the Servlet spec.
     * <p>If the request specifies a character encoding itself, the request
     * encoding will override this setting. This also allows for generically
     * overriding the character encoding in a filter that invokes the
     * <code>ServletRequest.setCharacterEncoding</code> method.
     * @param defaultEncoding the character encoding to use
     * @see javax.servlet.ServletRequest#getCharacterEncoding
     * @see javax.servlet.ServletRequest#setCharacterEncoding
     * @see WebUtils#DEFAULT_CHARACTER_ENCODING
     * @see org.apache.commons.fileupload.FileUploadBase#setHeaderEncoding
     */
    public void setDefaultEncoding(String defaultEncoding) {
        this.fileUpload.setHeaderEncoding(defaultEncoding);
    }

    protected String getDefaultEncoding() {
        String encoding = getFileUpload().getHeaderEncoding();
        if (encoding == null) {
            encoding = DEFAULT_CHARACTER_ENCODING;
        }
        return encoding;
    }

    /**
     * Set the temporary directory where uploaded files get stored.
     * Default is the servlet container's temporary directory for the web application.
     * @see org.springframework.web.util.WebUtils#TEMP_DIR_CONTEXT_ATTRIBUTE
     */
    public void setUploadTempDir(File uploadTempDir) throws IOException {
        if (!uploadTempDir.exists() && !uploadTempDir.mkdirs()) {
            throw new IllegalArgumentException("Given uploadTempDir [" + uploadTempDir + "] could not be created");
        }
        this.fileItemFactory.setRepository(uploadTempDir);
        this.uploadTempDirSpecified = true;
    }

    protected boolean isUploadTempDirSpecified() {
        return this.uploadTempDirSpecified;
    }

    /**
     * Factory method for a Commons DiskFileItemFactory instance.
     * <p>Default implementation returns a standard DiskFileItemFactory.
     * Can be overridden to use a custom subclass, e.g. for testing purposes.
     * @return the new DiskFileItemFactory instance
     */
    protected DiskFileItemFactory newFileItemFactory() {
        return new DiskFileItemFactory();
    }

    /**
     * Determine an appropriate FileUpload instance for the given encoding.
     * <p>Default implementation returns the shared FileUpload instance
     * if the encoding matches, else creates a new FileUpload instance
     * with the same configuration other than the desired encoding.
     * @param encoding the character encoding to use
     * @return an appropriate FileUpload instance.
     */
    protected FileUpload prepareFileUpload(String encoding) {
        FileUpload fileUpload = getFileUpload();
        FileUpload actualFileUpload = fileUpload;

        // Use new temporary FileUpload instance if the request specifies
        // its own encoding that does not match the default encoding.
        if (encoding != null && !encoding.equals(fileUpload.getHeaderEncoding())) {
            actualFileUpload = newFileUpload(getFileItemFactory());
            actualFileUpload.setSizeMax(fileUpload.getSizeMax());
            actualFileUpload.setHeaderEncoding(encoding);
        }

        return actualFileUpload;
    }

    /**
     * Parse the given List of Commons FileItems into a Spring MultipartParsingResult,
     * containing Spring MultipartFile instances and a Map of multipart parameter.
     * @param fileItems the Commons FileIterms to parse
     * @param encoding the encoding to use for form fields
     * @return the Spring MultipartParsingResult
     * @see CommonsMultipartFile#CommonsMultipartFile(org.apache.commons.fileupload.FileItem)
     */
    protected MultipartParsingResult parseFileItems(List<FileItem> fileItems, String encoding) {
        MultiValueMap<String, CommonsMultipartFile> multipartFiles = new LinkedMultiValueMap<String, CommonsMultipartFile>();
        Map<String, String[]> multipartParameters = new HashMap<String, String[]>();

        // Extract multipart files and multipart parameters.
        for (FileItem fileItem : fileItems) {
            if (fileItem.isFormField()) {
                String value;
                String partEncoding = determineEncoding(fileItem.getContentType(), encoding);
                if (partEncoding != null) {
                    try {
                        value = fileItem.getString(partEncoding);
                    } catch (UnsupportedEncodingException ex) {
                        if (logger.isWarnEnabled()) {
                            logger.warn("Could not decode multipart item '" + fileItem.getFieldName() + "' with encoding '" + partEncoding + "': using platform default");
                        }
                        value = fileItem.getString();
                    }
                } else {
                    value = fileItem.getString();
                }
                String[] curParam = multipartParameters.get(fileItem.getFieldName());
                if (curParam == null) {
                    // simple form field
                    multipartParameters.put(fileItem.getFieldName(), new String[] { value });
                } else {
                    // array of simple form fields
                    String[] newParam = StringUtils.addStringToArray(curParam, value);
                    multipartParameters.put(fileItem.getFieldName(), newParam);
                }
            } else {
                // multipart file field
                CommonsMultipartFile file = new CommonsMultipartFile(fileItem);
                multipartFiles.add(file.getName(), file);
                if (logger.isDebugEnabled()) {
                    logger.debug("Found multipart file [" + file.getName() + "] of size " + file.getSize() + " bytes with original filename [" + file.getOriginalFilename() + "], stored " + file.getStorageDescription());
                }
            }
        }
        return new MultipartParsingResult(multipartFiles, multipartParameters);
    }

    /**
     * Cleanup the Spring MultipartFiles created during multipart parsing,
     * potentially holding temporary data on disk.
     * <p>Deletes the underlying Commons FileItem instances.
     * @param multipartFiles Collection of MultipartFile instances
     * @see org.apache.commons.fileupload.FileItem#delete()
     */
    protected void cleanupFileItems(MultiValueMap<String, CommonsMultipartFile> multipartFiles) {
        for (List<CommonsMultipartFile> files : multipartFiles.values()) {
            for (CommonsMultipartFile file : files) {
                if (file instanceof CommonsMultipartFile) {
                    CommonsMultipartFile cmf = (CommonsMultipartFile) file;
                    cmf.getFileItem().delete();
                    if (logger.isDebugEnabled()) {
                        logger.debug("Cleaning up multipart file [" + cmf.getName() + "] with original filename [" + cmf.getOriginalFilename() + "], stored " + cmf.getStorageDescription());
                    }
                }
            }
        }
    }

    private String determineEncoding(String contentTypeHeader, String defaultEncoding) {
        if (!StringUtils.hasText(contentTypeHeader)) {
            return defaultEncoding;
        }
        MediaType contentType = MediaType.parseMediaType(contentTypeHeader);
        Charset charset = contentType.getCharSet();
        return charset != null ? charset.name() : defaultEncoding;
    }

    /**
     * Holder for a Map of Spring MultipartFiles and a Map of
     * multipart parameters.
     */
    protected static class MultipartParsingResult {

        private final MultiValueMap<String, CommonsMultipartFile> multipartFiles;

        private final Map<String, String[]> multipartParameters;

        /**
         * Create a new MultipartParsingResult.
         * @param mpFiles Map of field name to MultipartFile instance
         * @param mpParams Map of field name to form field String value
         */
        public MultipartParsingResult(MultiValueMap<String, CommonsMultipartFile> mpFiles, Map<String, String[]> mpParams) {
            this.multipartFiles = mpFiles;
            this.multipartParameters = mpParams;
        }

        /**
         * Return the multipart files as Map of field name to MultipartFile instance.
         */
        public MultiValueMap<String, CommonsMultipartFile> getMultipartFiles() {
            return this.multipartFiles;
        }

        /**
         * Return the multipart parameters as Map of field name to form field String value.
         */
        public Map<String, String[]> getMultipartParameters() {
            return this.multipartParameters;
        }
    }

}
