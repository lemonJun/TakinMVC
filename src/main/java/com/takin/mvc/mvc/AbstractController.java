package com.takin.mvc.mvc;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.takin.mvc.mvc.context.BeatContextBean;
import com.takin.mvc.mvc.inject.GuiceDI;
import com.takin.mvc.mvc.internal.actionresult.statuscode.ActionResults;
import com.takin.mvc.mvc.view.ViewFactory;

public abstract class AbstractController implements MVCController {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractController.class);

    /**
     * 在一个请求过程中的上下文
     * 
     */
    protected BeatContext beat = new BeatContextBean();

    @Inject
    protected InitHelper wfGod;

    @Inject
    protected ViewFactory view;

    /**
     * 返回一个view的ActionResult
     * 系统默认采用velocity实现。<br/>
     * viewName + .html存放的目录在 maven项目的resources/views下
     * 编译后存放在classes/views下
     *
     * @param viewName view的名字
     * @return 合适ActionResult
     */
    protected ActionResult view(String viewName) {
        return view.view(viewName);
    }

    /**
     * 跳转到一个新页面
     *
     * @param redirectUrl 调整页面的url
     * @return Http 302 跳转
     */
    protected ActionResult redirect(String redirectUrl) {
        return ActionResults.redirect(redirectUrl);
    }

    /**
     * 301永久跳转到一个新页面
     *
     * @param redirectUrl 调整页面的url
     * @return Http 1 跳转
     */
    protected ActionResult redirect301(String redirectUrl) {
        return ActionResults.redirect301(redirectUrl);
    }

    protected Model model() {
        return beat().getModel();
    }

    protected BeatContext beat() {
        return GuiceDI.getInstance(Dispatcher.class).currentBeatContext();
    }

    protected HttpServletRequest request() {

        return beat().getRequest();
    }

    protected HttpServletResponse response() {

        return beat().getResponse();
    }

}
