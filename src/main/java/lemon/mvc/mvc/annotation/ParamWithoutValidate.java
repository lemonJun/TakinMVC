package lemon.mvc.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import lemon.mvc.mvc.internal.ParamValidateInterceptor;

@Interceptor(ParamValidateInterceptor.class)
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ParamWithoutValidate {

    String[] value() default {};

}
