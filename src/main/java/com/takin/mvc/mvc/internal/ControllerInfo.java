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
package com.takin.mvc.mvc.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.takin.mvc.mvc.ActionResult;
import com.takin.mvc.mvc.InitHelper;
import com.takin.mvc.mvc.MVCController;
import com.takin.mvc.mvc.annotation.GET;
import com.takin.mvc.mvc.annotation.POST;
import com.takin.mvc.mvc.annotation.Path;
import com.takin.mvc.util2.AnnotationUtils;

/**
 * @author lemon
 */
public class ControllerInfo {

    final MVCController controller;
    final Class<? extends MVCController> clazz;
    final Path path;
    final boolean isGet;
    final boolean isPost;
    final String[] pathUrl;

    final Set<Annotation> annotations;

    public ControllerInfo(MVCController controller) {
        this.controller = controller;
        clazz = controller.getClass();
        this.path = AnnotationUtils.findAnnotation(clazz, Path.class);

        boolean iisGet = AnnotationUtils.findAnnotation(clazz, GET.class) != null;
        boolean iisPost = AnnotationUtils.findAnnotation(clazz, POST.class) != null;

        if (!iisGet && !iisPost) {
            iisGet = true;
            iisPost = true;
        }

        this.isGet = iisGet;
        this.isPost = iisPost;

        this.annotations = ImmutableSet.copyOf(clazz.getAnnotations());

        String[] pathUrls = path == null ? new String[] { "/" } : path.value();

        for (String pathUrl : pathUrls) {
            if (pathUrl.length() == 0 || pathUrl.charAt(0) != '/')
                pathUrl = '/' + pathUrl;
        }

        this.pathUrl = pathUrls;

    }

    public List<ActionInfo> analyze() {
        List<ActionInfo> actions = Lists.newArrayList();

        Set<Method> sets = Sets.filter(Sets.newHashSet(clazz.getDeclaredMethods()), methodFilter);
        PathInfo pathInfo;
        for (int i = 0; i < pathUrl.length; i++) {

            pathInfo = new PathInfo();
            pathInfo.setTypeAnn(path);
            pathInfo.setTypePath(pathUrl[i]);
            if (path == null)
                pathInfo.setTypeOrder(1000);
            else
                pathInfo.setTypeOrder(path.order());

            for (Method method : sets) {

                Path pathAnnotation = AnnotationUtils.findAnnotation(method, Path.class);
                String[] paths = pathAnnotation.value();
                int order = pathAnnotation.order();

                for (int j = 0; j < paths.length; j++) {
                    pathInfo.setMethodAnn(pathAnnotation);
                    pathInfo.setMethodPath(paths[j]);
                    pathInfo.setMethodOrder(order);
                    actions.add(new ActionInfo(this, method, InitHelper.instance, pathInfo));
                }
            }
        }

        return actions;

    }

    public MVCController getController() {
        return controller;
    }

    public Class<? extends MVCController> getClazz() {
        return clazz;
    }

    public Path getPath() {
        return path;
    }

    public boolean isGet() {
        return isGet;
    }

    public boolean isPost() {
        return isPost;
    }

    public String[] getPathUrl() {
        return pathUrl;
    }

    public Set<Annotation> getAnnotations() {
        return annotations;
    }

    public static Predicate<Method> getMethodFilter() {
        return methodFilter;
    }

    private static final Predicate<Method> methodFilter = new Predicate<Method>() {
        @Override
        public boolean apply(Method method) {
            // if (AnnotationUtils.findAnnotation(method, Ignored.class) !=
            // null) return false;

            // TODO : 新增类别校验，如果不包含Path则不加载到ActionInfo 中
            if (AnnotationUtils.findAnnotation(method, Path.class) == null)
                return false;
            Class<?> returnType = method.getReturnType();
            return returnType != null && ActionResult.class.isAssignableFrom(returnType) && (!method.isBridge() // TODO: 是否需要处理
                            && method.getDeclaringClass() != Object.class && Modifier.isPublic(method.getModifiers()));
        }
    };

}
