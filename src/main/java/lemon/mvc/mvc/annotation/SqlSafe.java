package lemon.mvc.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于表示，类或方法是Sql安全的，不用进行Sql注入检查
 * 如果没有表示@SqlSafe， 默认需要进行Sql注入检查处理
 * @author renjun
 *
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SqlSafe {

    /**
     * 是否是Sql安全
     * true， Sql安全
     * false， 需要Sql注入检查
     * @return
     */
    boolean value() default true;

}
