package com.takin.mvc.mvc.bind;

import java.util.ArrayList;
import java.util.List;

/**
 * 存储绑定结果
 * @author lemon
 *
 */
public class ObjectBindResult {

    private Object target;
    private List<CheckedError> errors = new ArrayList<CheckedError>();

    ObjectBindResult(BindingResult result) {
        List<ObjectError> bindErrors = result.getErrors();
        for (ObjectError springError : bindErrors) {
            // TODO: 错误值是多少？
            CheckedError error = new CheckedError(CheckedError.ErrorType.BIND, springError.getObjectName(), springError.getErrorMessage());
            errors.add(error);
        }

        this.target = result.getTarget();
    }

    public ObjectBindResult(Object target, List<CheckedError> errors) {
        super();
        this.target = target;
        this.errors = errors;
    }

    /**
     * 绑定目标
     * @return
     */
    public Object getTarget() {
        return this.target;
    }

    /**
     * 绑定错误
     * @return
     */
    public List<CheckedError> getErrors() {
        return errors;
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
    public void merge(ObjectBindResult result) {
        this.errors.addAll(result.getErrors());
    }
}
