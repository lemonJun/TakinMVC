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
package com.takin.mvc.mvc.monitor;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lemon
 */
public class RequestCounter {

    public static RequestCounter instance() {
        return SingletonHolder.instance;
    }

    protected RequestCounter() {
    }

    private final AtomicInteger counter = new AtomicInteger(0);

    private volatile long totalTime = 0; //总时间
    private volatile int totalCount = 0; //总请求数

    public int increment() {
        return counter.incrementAndGet();
    }

    public RequestStats decrement(long time) {

        return new RequestStats(counter.decrementAndGet(), totalTime += time, totalCount++);
    }

    public RequestStats getCurrentState() {
        return new RequestStats(counter.get(), totalTime, totalCount);
    }

    static class SingletonHolder {
        static RequestCounter instance = new RequestCounter();
    }

}
