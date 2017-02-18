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
package lemon.mvc.mvc.monitor;

/**
 * @author lemon
 *用于处理一个请求时间
 */
public interface ActionTimeMonitor {

    public static final ActionTimeMonitor NULL = new ActionTimeMonitor() {

        @Override
        public void post() {
        }
    };

    public void post();

    public abstract class Factory {
        private static Factory factory = new Factory() {

            @Override
            public ActionTimeMonitor build() {
                return new DefaultActionTimeMonitor();
            }
        };

        public static void set(Factory factory) {
            if (factory == null)
                throw new NullPointerException();

            Factory.factory = factory;
        }

        public static ActionTimeMonitor create() {
            return factory.build();
        }

        public abstract ActionTimeMonitor build();
    }
}
