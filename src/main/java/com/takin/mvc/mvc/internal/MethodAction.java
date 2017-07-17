package com.takin.mvc.mvc.internal;

import java.text.DecimalFormat;
import java.util.Map;

import com.google.common.collect.Maps;
import com.takin.mvc.mvc.ActionResult;
import com.takin.mvc.mvc.Dispatcher;
import com.takin.mvc.mvc.inject.MVCDI;
import com.takin.mvc.mvc.interceptor.ActionInterceptor;

/**
 * @author lemon
 */
public class MethodAction implements Action {

    public static MethodAction create(ActionInfo actionInfo) {
        return new MethodAction(actionInfo);
    }

    private final ActionInfo actionInfo;

    private final int order;
    
    private final int base = 1000;

    public static final DecimalFormat doubleFormat = new DecimalFormat("#.00");

    private MethodAction(ActionInfo actionInfo) {
        this.actionInfo = actionInfo;
        double order1 = (double) (base - actionInfo.getPathPattern().length()) / base + (actionInfo.isPattern() ? 0.5d : 0d);
        int order2 = Integer.parseInt(new java.text.DecimalFormat("0").format(Math.ceil(Math.ceil(order1 * 1000d))));
        order = actionInfo.getOrder() - order2;
    }

    public ActionInfo getActionInfo() {
        return actionInfo;
    }

    public int order() {
        return order;
    }

    @Override
    public RouteResult matchAndInvoke(RouteBag bag) {
        if (!actionInfo.matchHttpMethod(bag))//判断是否是get or post请求
            return RouteResult.unMatch();
        MVCDI.getInstance(Dispatcher.class).currentBeatContext().getModel().add("actionMethod", actionInfo);
        Map<String, String> uriTemplateVariables = Maps.newHashMap();

        //判断是否与path匹配
        boolean match = actionInfo.match(bag, uriTemplateVariables);
        if (!match)
            return RouteResult.unMatch();

        // PreIntercept
        for (ActionInterceptor preInterceptor : actionInfo.getPreInterceptors()) {
            ActionResult actionResult = preInterceptor.preExecute(bag.getBeat());
            if (ActionResult.NULL != actionResult)
                return RouteResult.invoked(actionResult);
        }
        //
        //把beat传进去  就可以封装成对象了
        ActionResult actionResult = actionInfo.invoke(bag.getBeat(), uriTemplateVariables);

        // PostIntercept
        for (ActionInterceptor postInterceptor : actionInfo.getPostInterceptors()) {
            actionResult = postInterceptor.preExecute(bag.getBeat());
            //            actionResult = postInterceptor.postExecute(bag.getBeat(), actionResult);
        }

        return RouteResult.invoked(actionResult);
    }

    public String path() {
        return actionInfo.getPathPattern();
    }

}
