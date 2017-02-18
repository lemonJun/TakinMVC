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
package lemon.mvc.mvc.route;

import lemon.mvc.mvc.BeatContext;
import lemon.mvc.util.PathUtils;

/**
 * @author lemon
 */
public class RouteBag {

    public static RouteBag create(BeatContext beat) {
        return new RouteBag(beat);
    }

    private final BeatContext beat;
    private final boolean isGet;
    private final boolean isPost;
    private final String path;
    private final String simplyPath;

    private RouteBag(BeatContext beat) {
        this.beat = beat;

        path = beat.getClient().getRelativeUrl();
        simplyPath = PathUtils.simplyWithoutSuffix(path);

        String requestMethod = beat.getRequest().getMethod().toUpperCase();
        isPost = "POST".equals(requestMethod);
        isGet = !isPost;
    }

    public BeatContext getBeat() {
        return beat;
    }

    public boolean isGet() {
        return isGet;
    }

    public boolean isPost() {
        return isPost;
    }

    public String getPath() {
        return path;
    }

    public String getSimplyPath() {
        return simplyPath;
    }
}
