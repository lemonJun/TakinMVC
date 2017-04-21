package com.takin.mvc.mvc;

import com.takin.mvc.mvc.inject.MVCDI;
import com.takin.mvc.mvc.view.RedirectResult;
import com.takin.mvc.mvc.view.ViewFactory;

/**
 * 所有Action的返回结果
 *
 * @author lemon
 */
public abstract class ActionResult {

    public final static ActionResult NULL = null;

    /**
     * 用于生成显示页面
     *
     * @param beatContext 需要渲染的上下文
     */
    public abstract void render(BeatContext beatContext) throws Exception;

    public static ActionResult redirect(String url) {

        return new RedirectResult(url);
    }

    public static ActionResult view(String viewName) {

        ViewFactory view = MVCDI.getInstance(ViewFactory.class);

        return view.view(viewName);
    }

}
