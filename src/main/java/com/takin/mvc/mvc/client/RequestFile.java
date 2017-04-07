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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.takin.mvc.mvc.server.CommonsMultipartFile;

/**
 * 
 * multipart request的封装实现，用于实现文件的上传功能。
 * <p>文件内容可以存储于内存或者临时目录。需要自己实现存储。临时目录会清除。
 * 
 *
 * @author lemon
 *
 */
public class RequestFile {

    CommonsMultipartFile mFile;

    RequestFile(CommonsMultipartFile mFile) {
        super();
        this.mFile = mFile;
    }

    /**
     * Return the name of the parameter in the multipart form.
     * @return the name of the parameter (never <code>null</code> or empty)
     */
    public String getName() {
        return mFile.getName();
    }

    /**
     * Return the original filename in the client's filesystem.
     * <p>This may contain path information depending on the browser used,
     * but it typically will not with any other than Opera.
     * @return the original filename, or the empty String if no file
     * has been chosen in the multipart form
     */
    public String getOriginalFilename() {
        return mFile.getOriginalFilename();
    }

    /**
     * Return the content type of the file.
     * @return the content type, or <code>null</code> if not defined
     * (or no file has been chosen in the multipart form)
     */
    public String getContentType() {
        return mFile.getContentType();
    }

    /**
     * Return whether the uploaded file is empty, that is, either no file has
     * been chosen in the multipart form or the chosen file has no content.
     */
    public boolean isEmpty() {
        return mFile.isEmpty();
    }

    /**
     * Return the size of the file in bytes.
     * @return the size of the file, or 0 if empty
     */
    public long getSize() {
        return mFile.getSize();
    }

    /**
     * Return the contents of the file as an array of bytes.
     * @return the contents of the file as bytes, or an empty byte array if empty
     * @throws IOException in case of access errors (if the temporary store fails)
     */
    public byte[] getBytes() throws IOException {
        return mFile.getBytes();
    }

    /**
     * Return an InputStream to read the contents of the file from.
     * The user is responsible for closing the stream.
     * @return the contents of the file as stream, or an empty stream if empty
     * @throws IOException in case of access errors (if the temporary store fails)
     */
    public InputStream getInputStream() throws IOException {
        return mFile.getInputStream();
    }

    /**
     * Transfer the received file to the given destination file.
     * <p>This may either move the file in the filesystem, copy the file in the
     * filesystem, or save memory-held contents to the destination file.
     * If the destination file already exists, it will be deleted first.
     * <p>If the file has been moved in the filesystem, this operation cannot
     * be invoked again. Therefore, call this method just once to be able to
     * work with any storage mechanism.
     * @param dest the destination file
     * @throws IOException in case of reading or writing errors
     * @throws IllegalStateException if the file has already been moved
     * in the filesystem and is not available anymore for another transfer
     */
    public void transferTo(File dest) throws IOException, IllegalStateException {
        mFile.transferTo(dest);
    }

}
