package com.takin.mvc.mvc;

import java.lang.reflect.Method;

/**
 * 用户获得Action的属性
 * @author renjun
 *
 */
public interface ActionAttribute {
    /**
     * 得到所有的注解
     * @return
     * TODO: 暂时不提供
     */
    //public Set<Annotation> getAnnotations();

    /**
     * 得到Action方法
     * @return
     */
    public Method getActionMethod();

    //public <T extends Annotation> T getAnnotation(Class<T> annotationClass);

}
