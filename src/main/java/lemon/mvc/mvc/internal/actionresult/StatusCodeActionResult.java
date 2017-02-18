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
package lemon.mvc.mvc.internal.actionresult;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;

import lemon.mvc.mvc.ActionResult;
import lemon.mvc.mvc.BeatContext;

import java.io.IOException;

/**
 * @author lemon
 */
@Singleton
public class StatusCodeActionResult {

    public final static ActionResult defaultSc404 = new ActionResult() {
        @Override
        public void render(BeatContext beatContext) {

            HttpServletResponse response = beatContext.getResponse();
            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (IOException e) {
                //TODO:log
                e.printStackTrace();
            }

        }
    };

    public final static ActionResult defaultSc405 = new ActionResult() {
        @Override
        public void render(BeatContext beatContext) {

            HttpServletResponse response = beatContext.getResponse();
            try {
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            } catch (IOException e) {
                //TODO:log
                e.printStackTrace();
            }
        }
    };

    @Inject
    @Named("HTTP_STATUS=404")
    ActionResult sc404;

    @Inject
    @Named("HTTP_STATUS=405")
    ActionResult sc405;

    public void render404(BeatContext beat) {
        try {
            sc404.render(beat);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void render405(BeatContext beat) {
        try {
            sc405.render(beat);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public ActionResult getSc404() {
        return sc404;
    }

    public ActionResult getSc405() {
        return sc405;
    }
}
