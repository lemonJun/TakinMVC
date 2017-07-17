package com.takin.mvc.mvc.internal;

import java.util.Map;

import com.takin.mvc.mvc.Model;
import com.takin.mvc.util.ModelMap;

/**
 * MVC 中的Model, 以key,value形式存放，可以由Controller传个View
 * @author renjun
 *
 */
public class DefaultModel implements Model {

    /** Model Map */
    private ModelMap model;

    /**
     * 增加一个属性
     * @param attributeName 属性名称
     * @param attributeValue 属性值
     */
    @Override
    public Model add(String attributeName, Object attributeValue) {
        getModelMap().put(attributeName, attributeValue);
        return this;
    }

    /**
     * 根据属性名得到属性值
     * @param attributeName 属性名称
     * @return 对应的属性值
     */
    @Override
    public Object get(String attributeName) {
        return getModelMap().get(attributeName);
    }

    /**
     * Return the model map. Never returns <code>null</code>.
     * To be called by application code for modifying the model.
     */
    @Override
    public Map<String, Object> getModel() {
        return getModelMap();
    }

    /**
     * 批量增加属性
     * @param attributes
     */
    @Override
    public Model addAll(Map<String, ?> attributes) {
        getModelMap().putAll(attributes);
        return this;
    }

    /**
     * 判断是否包含属性名
     * @param attributeName 需要查找的属性
     * @return
     */
    @Override
    public boolean contains(String attributeName) {
        return getModelMap().containsKey(attributeName);
    }

    @Override

    public Model add(Object attributeValue) {

        getModelMap().addAttribute(attributeValue);
        return this;

    }

    /**
     * 合并属性
     * @param attributes
     */
    @Override

    public Model merge(Map<String, ?> attributes) {

        getModelMap().mergeAttributes(attributes);
        return this;

    }

    /**
     * Return the underlying <code>ModelMap</code> instance (never <code>null</code>).
     */
    private ModelMap getModelMap() {
        if (this.model == null) {
            this.model = new ModelMap();
        }
        return this.model;
    }

}
