package lemon.mvc.mvc.annotation;

import java.lang.annotation.*;

import lemon.mvc.mvc.interceptor.ActionInterceptor;

/**
 * 设置拦截器
 * @author renjun
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Interceptor {
    Class<? extends ActionInterceptor> value();

    InterceptorType type() default InterceptorType.ACTION;

    public enum InterceptorType {
        ACTION, EXECEPTION, RESULT
    }
}