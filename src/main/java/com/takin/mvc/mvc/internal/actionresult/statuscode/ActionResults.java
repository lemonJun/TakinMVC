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
package com.takin.mvc.mvc.internal.actionresult.statuscode;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.takin.mvc.mvc.ActionResult;
import com.takin.mvc.mvc.BeatContext;
import com.takin.mvc.mvc.exception.WFException;

/**
 * @author lemon
 */
public class ActionResults {

    private ActionResults() {
    }

    public static ActionResult redirect(final String url) {
        return new ActionResult() {
            @Override
            public void render(BeatContext beatContext) {
                try {
                    beatContext.getResponse().sendRedirect(url);
                } catch (IOException e) {

                    throw WFException.newBuilder(e).addContextVariable("redirect url:", url).build();
                }
            }
        };
    }

    public static ActionResult redirect301(final String url) {
        return new ActionResult() {
            @Override
            public void render(BeatContext beatContext) {
                try {
                    //fixMe: 需要判断是否是同一个schema等因素
                    HttpServletResponse response = beatContext.getResponse();
                    response.setStatus(301);
                    response.sendRedirect(url);
                } catch (IOException e) {
                    throw WFException.newBuilder(e).addContextVariable("redirect url:", url).build();
                }
            }
        };
    }
}
