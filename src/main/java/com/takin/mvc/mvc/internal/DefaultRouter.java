package com.takin.mvc.mvc.internal;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.takin.mvc.mvc.ActionResult;
import com.takin.mvc.mvc.BeatContext;
import com.takin.mvc.mvc.InitHelper;
import com.takin.mvc.mvc.MVCController;
import com.takin.mvc.mvc.inject.MVCDI;
import com.takin.mvc.mvc.route.Action;
import com.takin.mvc.mvc.route.RouteBag;
import com.takin.mvc.mvc.route.RouteResult;
import com.takin.mvc.mvc.route.Router;
import com.takin.mvc.mvc.route.StaticActionAnnotation;

@Singleton
public class DefaultRouter implements Router {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRouter.class);

    private final List<Action> actions;
    private final Map<String, Action> urlAction = Maps.newHashMap();

    @Inject
    public DefaultRouter(@StaticActionAnnotation Action staticAction) {
        this.actions = buildActions(InitHelper.instance.getControllerClasses(), staticAction);
    }

    @Override
    public ActionResult route(BeatContext beat) {

        RouteBag bag = RouteBag.create(beat);
        Action ac = urlAction.get(beat.getRelativeUrl());
        if (ac != null) {
            RouteResult routeResult = ac.matchAndInvoke(bag);
            if (routeResult.isSuccess())
                return routeResult.getResult();
        }
        for (Action action : actions) {
            RouteResult routeResult = action.matchAndInvoke(bag);
            if (routeResult.isSuccess())
                return routeResult.getResult();
        }

        return ActionResult.NULL;
    }

    List<Action> buildActions(Set<Class<? extends MVCController>> controllerClasses, Action staticAction) {
        Set<MVCController> controllers = getControllerInstances(controllerClasses);
        return buildActionss(controllers, staticAction);
    }

    private Set<MVCController> getControllerInstances(Set<Class<? extends MVCController>> controllerClasses) {
        Iterable<MVCController> sets = Iterables.transform(controllerClasses, new Function<Class<? extends MVCController>, MVCController>() {
            @Override
            public MVCController apply(Class<? extends MVCController> clazz) {
                return MVCDI.getInstance(clazz);
            }
        });

        return ImmutableSet.copyOf(sets);
    }

    /**
     * @param controllers
     * @param staticAction
     * @return
     */
    List<Action> buildActionss(Set<MVCController> controllers, Action staticAction) {

        List<Action> actions = Lists.newArrayList();
        actions.add(staticAction);

        List<MethodAction> mactions = Lists.newArrayList();
        for (MVCController controller : controllers) {
            logger.info(controller.getClass().getName());
            ControllerInfo controllerInfo = new ControllerInfo(controller);
            List<ActionInfo> subActions = controllerInfo.analyze();

            for (ActionInfo newAction : subActions) {
                merge(mactions, MethodAction.create(newAction));
            }
        }

        logger.info("--------------Path Map-----------------");
        for (MethodAction newAction : mactions) {
            logger.info(String.format(">>mappedPatterns:%s.%s", newAction.getActionInfo().getMethod().getDeclaringClass().getName(), newAction.getActionInfo().getMethod().getName()));
            String path = newAction.path();
            logger.info(String.format(".....%s", path));
            if (path.indexOf('.') == -1 && !path.endsWith("/")) {
                logger.info(String.format(".....%s.*", path));
                logger.info(String.format(".....%s/", path));
            }

            logger.info("");
        }
        logger.info("--------------Path Map End-----------------");

        actions.addAll(mactions);
        return ImmutableList.copyOf(actions);
    }

    void merge(List<MethodAction> actions, MethodAction newAction) {

        for (int index = 0; index < actions.size(); index++) {
            MethodAction action = actions.get(index);
            if (action.order() < newAction.order()) {
                actions.add(index, newAction);
                addMap(newAction);
                return;
            }
        }
        addMap(newAction);
        actions.add(newAction);
    }

    private void addMap(MethodAction newAction) {
        //对不包含?*{的path此处再增加一个ulr到action的映射 
        if (!newAction.path().contains("*") && !newAction.path().contains("?") && !newAction.path().contains("{")) {
            urlAction.put(newAction.path(), newAction);
            logger.info(newAction.path() + " into map");
        }
    }

}