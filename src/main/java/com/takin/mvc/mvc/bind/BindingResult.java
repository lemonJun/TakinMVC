package com.takin.mvc.mvc.bind;

import java.util.List;

/**
 * 自实现的绑定结果
 * @author lemon
 *
 */
public class BindingResult {

    private Object target;

    private List<ObjectError> errors;

    /**
     * 绑定目标
     * @return
     */
    public Object getTarget() {
        return this.target;
    }

    public void setTarget(Object object) {
        this.target = object;
    }

    /**
     * 绑定错误
     * @return
     */
    public List<ObjectError> getErrors() {
        return errors;
    }

    /**
     * 绑定错误
     * @return
     */
    public void setErrors(List<ObjectError> objectErrors) {

        errors = objectErrors;
    }

    /**
     * 得到错误的数量
     * @return
     */
    public int getErrorCount() {
        return errors.size();
    }

    /**
     * 合并
     * @param result
     */
    public void merge(BindingResult result) {
        this.errors.addAll(result.getErrors());
    }
}
