package com.takin.mvc.mvc.bind;

import java.util.ArrayList;
import java.util.List;

import com.takin.emmet.util.CollectionUtil;

/**
 * 用于管理在一个beat过程中所有绑定和校验信息
 * @author lemon
 *
 */
public class BeatBindResults {

    /**
     * 绑定结果列表
     */
    private List<ObjectBindResult> results = new ArrayList<>();

    /**
     * 根据目标对象获得绑定信息
     * @param target
     * @return
     */
    public ObjectBindResult get(Object target) {
        for (ObjectBindResult result : results)
            if (result.getTarget().equals(target))
                return result;
        return null;
    }

    /**
     * 增加绑定信息
     * 如果目标对象已存在，则合并，否则增加
     * @param other
     */
    public void add(ObjectBindResult other) {
        for (ObjectBindResult result : results)
            if (result.getTarget().equals(other.getTarget())) {
                result.merge(other);
                return;
            }

        results.add(other);
    }

    /**
     * 得到所有有错误的绑定信息
     * @return
     */
    public List<ObjectBindResult> getErrorBindResults() {
        List<ObjectBindResult> errorResults = new ArrayList<>();

        for (ObjectBindResult result : results)
            if (result.getErrorCount() > 0)
                errorResults.add(result);

        return errorResults;
    }

    /**
     * 是否有错误
     * @return
     */
    public boolean hasError() {
        return (CollectionUtil.isNotEmpty(getErrorBindResults())) ? true : false;
    }

    /**
     * 所有校验和数据绑定错误
     * @return
     */
    public CheckedError[] getErrors() {
        List<CheckedError> errors = new ArrayList<>();
        for (ObjectBindResult br : getErrorBindResults()) {
            for (CheckedError e : br.getErrors())
                errors.add(e);
        }
        return errors.toArray(new CheckedError[errors.size()]);
    }

}
