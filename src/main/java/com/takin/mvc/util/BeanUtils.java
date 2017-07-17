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
package com.takin.mvc.util;

import java.lang.reflect.Constructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.takin.emmet.reflect.ReflectionUtils;
import com.takin.emmet.util.Assert;

/**
 * Bean工具类
 * 
 * @author lemon
 */
public class BeanUtils {

    private static final Logger log = LoggerFactory.getLogger(BeanUtils.class);

    public static <T> T instantiate(Class<T> clazz) {

        Assert.notNull(clazz, "Class must not be null");
        if (clazz.isInterface()) {
            log.error("Specified class is an interface");

        }
        try {
            return clazz.newInstance();
        } catch (Exception ex) {
            log.error("Is the constructor accessible?");

        }
        return null;
    }

    /**
     * Convenience method to instantiate a class using its no-arg constructor.
     * As this method doesn't try to load classes by name, it should avoid
     * class-loading issues.
     * <p>Note that this method tries to set the constructor accessible
     * if given a non-accessible (that is, non-public) constructor.
     * @param clazz class to instantiate
     * @return the new instance
     * @throws BeanInstantiationException if the bean cannot be instantiated
     */
    public static <T> T instantiateClass(Class<T> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        if (clazz.isInterface()) {
            log.error("Specified class is an interface");
        }
        try {
            return instantiateClass(clazz.getDeclaredConstructor());
        } catch (NoSuchMethodException ex) {
            log.error("No default constructor found", ex);
        }
        return null;
    }

    /**
     * Convenience method to instantiate a class using the given constructor.
     * As this method doesn't try to load classes by name, it should avoid
     * class-loading issues.
     * <p>Note that this method tries to set the constructor accessible
     * if given a non-accessible (that is, non-public) constructor.
     * @param ctor the constructor to instantiate
     * @param args the constructor arguments to apply
     * @return the new instance
     * @throws BeanInstantiationException if the bean cannot be instantiated
     */
    public static <T> T instantiateClass(Constructor<T> ctor, Object... args) {
        Assert.notNull(ctor, "Constructor must not be null");
        try {
            ReflectionUtils.makeAccessible(ctor);
            return ctor.newInstance(args);
        } catch (Exception ex) {
            log.error("instantiateClass error", ex);
        }
        return null;
    }
}
