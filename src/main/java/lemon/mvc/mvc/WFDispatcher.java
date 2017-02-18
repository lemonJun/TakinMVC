///*
//*  Copyright Beijing 58 Information Technology Co.,Ltd.
//*
//*  Licensed to the Apache Software Foundation (ASF) under one
//*  or more contributor license agreements.  See the NOTICE file
//*  distributed with this work for additional information
//*  regarding copyright ownership.  The ASF licenses this file
//*  to you under the Apache License, Version 2.0 (the
//*  "License"); you may not use this file except in compliance
//*  with the License.  You may obtain a copy of the License at
//*
//*        http://www.apache.org/licenses/LICENSE-2.0
//*
//*  Unless required by applicable law or agreed to in writing,
//*  software distributed under the License is distributed on an
//*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
//*  KIND, either express or implied.  See the License for the
//*  specific language governing permissions and limitations
//*  under the License.
//*/
//package lemon.mvc.mvc;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import com.google.inject.ImplementedBy;
//
//import lemon.mvc.mvc.internal.DefaultWFDispatcher;
//
///**
// * filter类用于初始化，此类应该只专心于resq业务的处理
// * 用于处理Rest请求调度的核心类
// * 
// */
//@ImplementedBy(DefaultWFDispatcher.class)
//public interface WFDispatcher {
//    
//    void service(HttpServletRequest request, HttpServletResponse response);
//
//    public HttpServletRequest currentRequest();
//
//    public HttpServletResponse currentResponse();
//    
//    BeatContext currentBeatContext();
//
//}
