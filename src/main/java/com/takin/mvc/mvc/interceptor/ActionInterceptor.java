package com.takin.mvc.mvc.interceptor;

import com.takin.mvc.mvc.ActionResult;
import com.takin.mvc.mvc.BeatContext;

/**
 * @author lemon
 */
//TODO:拦截器
public interface ActionInterceptor {

    /**
     * 拦截当前请求
     * @param beat 当前请求的上下文
     * @return
     * null，进入下一个拦截或执行Action
     * <BR/>
     * 非空，直接显示，不进入下一个拦截或执行Action
     */
    public ActionResult preExecute(BeatContext beat);
}
