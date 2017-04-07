package com.takin.mvc.mvc;

import com.google.inject.ImplementedBy;
import com.takin.mvc.mvc.internal.DefaultModel;

import java.util.Map;

/**
 * @author lemon
 */
@ImplementedBy(DefaultModel.class)
public interface Model {

    /**
     * 增加一个属性
     * 
     * @param attributeValue 属性值
     */

    Model add(Object attributeValue);

    /**
     * 增加一个属性
     * @param attributeName 属性名称
     * @param attributeValue 属性值
     */
    Model add(String attributeName, Object attributeValue);

    /**
     * 根据属性名得到属性值
     * @param attributeName 属性名称
     * @return 对应的属性值
     */
    Object get(String attributeName);

    /**
     * Return the model map. Never returns <code>null</code>.
     * To be called by application code for modifying the model.
     */
    Map<String, Object> getModel();

    /**
     * 批量增加属性
     * @param attributes
     */
    Model addAll(Map<String, ?> attributes);

    /**
     * 判断是否包含属性名
     * @param attributeName 需要查找的属性
     * @return
     */
    boolean contains(String attributeName);

    /**
     * 合并属性
     * @param attributes
     */
    Model merge(Map<String, ?> attributes);
}
