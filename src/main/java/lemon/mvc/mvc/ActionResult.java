package lemon.mvc.mvc;

import lemon.mvc.mvc.inject.GuiceDI;
import lemon.mvc.mvc.view.RedirectResult;
import lemon.mvc.mvc.view.ViewFactory;

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

        ViewFactory view = GuiceDI.getInstance(ViewFactory.class);

        return view.view(viewName);
    }

}
