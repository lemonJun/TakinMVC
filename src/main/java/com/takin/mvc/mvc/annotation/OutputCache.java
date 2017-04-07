package com.takin.mvc.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.takin.mvc.mvc.cache.OutputCacheInterceptor;

/**
 * 设置一个拦截器
 * @author renjun
 *
 */
@Interceptor(OutputCacheInterceptor.class)
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented

public @interface OutputCache {

    /**
     * 可以定义拦截器的的优先级，0，1，2,...
     * @return
     */
    int order() default 1;

    /**
     * 定义缓存的秒，Gets or sets the cache duration, in seconds.
     * @return
     */
    int duration() default 60;
}
