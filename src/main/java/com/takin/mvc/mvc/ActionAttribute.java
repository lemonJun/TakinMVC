package com.takin.mvc.mvc;

import java.lang.reflect.Method;

/**
 * 用户获得Action的属性
 * @author renjun
 *
 */
public interface ActionAttribute {

    /**
     * 得到Action方法
     * @return
     */
    public Method getActionMethod();

}
