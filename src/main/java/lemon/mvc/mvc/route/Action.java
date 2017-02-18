package lemon.mvc.mvc.route;

/**
 * @author lemon
 */
public interface Action {
    /**
     * 去确定优先级
     * @return 优先级
     */
    int order();

    /**
     * 匹配并且执行
     * @param bag 当前路由信息
     * @return 匹配或执行的结果
     */
    RouteResult matchAndInvoke(RouteBag bag);

    String path();

}
