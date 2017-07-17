package com.takin.mvc.mvc.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.takin.emmet.annotation.AnnotationUtils;
import com.takin.emmet.util.CollectionUtil;
import com.takin.mvc.mvc.ActionAttribute;
import com.takin.mvc.mvc.ActionResult;
import com.takin.mvc.mvc.BeatContext;
import com.takin.mvc.mvc.InitHelper;
import com.takin.mvc.mvc.MVCController;
import com.takin.mvc.mvc.annotation.GET;
import com.takin.mvc.mvc.annotation.Interceptor;
import com.takin.mvc.mvc.annotation.POST;
import com.takin.mvc.mvc.annotation.Path;
import com.takin.mvc.mvc.bind.BindAndValidate;
import com.takin.mvc.mvc.converter.ConverterFactory;
import com.takin.mvc.mvc.exception.WFException;
import com.takin.mvc.mvc.inject.MVCDI;
import com.takin.mvc.mvc.interceptor.ActionInterceptor;
import com.takin.mvc.util.ClassUtils;
import com.takin.mvc.util.Pair;
import com.takin.mvc.util2.AntPathMatcher;
import com.takin.mvc.util2.PathMatcher;

/**
 * @author lemon
 */
public class ActionInfo implements ActionAttribute {

    private static final Logger logger = LoggerFactory.getLogger(ActionInfo.class);

    private final ControllerInfo controllerInfo;
    private final Method method;

    public Method getMethod() {
        return method;
    }

    private final InitHelper initHelper;

    /**
     * path匹配模式，联合了Controller上path，并去除后置"/",
     */
    private final String pathPattern;

    /**
     * Http Method GET
     */
    private final boolean isGet;

    /**
     * http method POST
     */
    private final boolean isPost;

    /**
     * 方法上所有参数名，按顺序排列
     */
    private final List<String> paramNames;

    /**
     * 方法上所有参数类型，按顺序排列
     */
    private final List<Class<?>> paramTypes;

    /**
     * 所有annotation，包括并覆盖controller上的annotation，
     */
    private final Set<Annotation> annotations;

    /**
     * 所有前置拦截器,按拦截器的order升序排列
     */
    private final List<ActionInterceptor> preInterceptors;

    /**
     * 所有后置拦截器，按拦截器的order降序排列
     */
    private final List<ActionInterceptor> postInterceptors;

    /**
     * 匹配的优先级
     */
    private final int order;

    /**
     * 是否是模版匹配
     */
    private final boolean isFuzzy;

    /**
     * 利用Ant匹配模型处理url
     */
    private final PathMatcher pathMatcher = new AntPathMatcher();

    private final ConverterFactory converter = new ConverterFactory();

    public ActionInfo(ControllerInfo controllerInfo, Method method, InitHelper wfGod, PathInfo pathInfo) {
        this.controllerInfo = controllerInfo;
        this.method = method;
        this.initHelper = wfGod;
        Path path = AnnotationUtils.findAnnotation(method, Path.class);
        this.order = path.order();

        this.pathPattern = simplyPathPattern(pathInfo.getTypePath(), pathInfo.getMethodPath());

        this.paramTypes = ImmutableList.copyOf(method.getParameterTypes());
        this.paramNames = ImmutableList.copyOf(ClassUtils.getMethodParamNames(controllerInfo.getClazz(), method));

        // 计算匹配的优先级,精确匹配还是模版匹配
        isFuzzy = pathMatcher.isPattern(pathPattern) || CollectionUtil.isNotEmpty(paramTypes);

        Pair<Boolean, Boolean> httpMethodPair = pickupHttpMethod(controllerInfo, method);
        this.isGet = httpMethodPair.getKey();
        this.isPost = httpMethodPair.getValue();

        annotations = collectAnnotations(controllerInfo, method);

        // 拦截器
        List<InterceptorInfo> interceptorInfoList = findInterceptors();
        preInterceptors = getPreInterceptorList(interceptorInfoList);
        postInterceptors = getPostInterceptorList(interceptorInfoList);

    }

    public String getPathPattern() {
        return pathPattern;
    }

    MVCController controller() {
        return controllerInfo.getController();
    }

    Method method() {
        return method;
    }

    public ControllerInfo getControllerInfo() {
        return controllerInfo;
    }

    public boolean isGet() {
        return isGet;
    }

    public boolean isPost() {
        return isPost;
    }

    public List<Class<?>> getParamTypes() {
        return paramTypes;
    }

    public List<String> getParamNames() {
        return paramNames;
    }

    public Set<Annotation> annotations() {
        return annotations;
    }

    public boolean isPattern() {
        return isFuzzy;
    }

    public int getOrder() {
        return order;
    }

    public List<ActionInterceptor> getPreInterceptors() {
        return preInterceptors;
    }

    public List<ActionInterceptor> getPostInterceptors() {
        return postInterceptors;
    }

    PathMatcher getPathMatcher() {
        return pathMatcher;
    }

    InitHelper getWfGod() {
        return initHelper;
    }

    ConverterFactory getConverter() {
        return converter;
    }

    boolean match(RouteBag bag, Map<String, String> uriTemplateVariables) {
        return getPathMatcher().doMatch(getPathPattern(), bag.getSimplyPath(), true, uriTemplateVariables);
    }

    boolean matchHttpMethod(RouteBag bag) {
        boolean sisGet = bag.isGet() && isGet();
        boolean sisPost = bag.isPost() && isPost();
        return sisGet || sisPost;
    }

    /**
     * 解析URL中的路径  转换成方法参数进行调用
     * 
     * @param urlParams  key:value
     * @return
     */
    ActionResult invoke(BeatContext beat, Map<String, String> urlParams) {
        Object[] param = new Object[paramTypes.size()];
        for (int i = 0; i < paramNames.size(); i++) {
            String paramName = paramNames.get(i);
            Class<?> clazz = paramTypes.get(i);

            String v = urlParams.get(paramName);
            //普通类型直接bind
            if (v != null && converter.canConvert(clazz)) {
                param[i] = converter.convert(clazz, v);
                continue;
            }

            if (converter.canConvert(clazz))
                continue;
            param[i] = BindAndValidate.bindAndValidate(clazz).getTarget();

            /**绑定数据
            //   ObjectBindResult br = BindUtils.bind(clazz, beat);
            //   beat.getBindResults().add(br);
            //   param[i] = br.getTarget();
            //    
            //   // 校验
            //   beat.getBindResults().add(BindAndValidate.Singleton().validate(param[i]));     
             */
        }

        try {
            Object result = method().invoke(controller(), param);
            return ActionResult.class.cast(result);
        } catch (Exception e) {
            throw WFException.newBuilder("invoke exception.", e).addContextVariables(urlParams).build();
        }
    }

    String simplyPathPattern(String typePath, String methodPath) {
        String originPathPattern = combinePathPattern(typePath, methodPath);
        logger.info(String.format("originPath:%s", originPathPattern));
        return simplyPathPattern(originPathPattern);
    }

    /**
    *收集方法上所有Annotation，包括Controller上标志
    * @param controllerInfo controller信息
    * @param method 方法
    * @return 方法上所有Annotation，包括Controller
    */
    ImmutableSet<Annotation> collectAnnotations(ControllerInfo controllerInfo, Method method) {
        return ImmutableSet.<Annotation> builder().add(method.getAnnotations()).addAll(controllerInfo.getAnnotations()).build();

    }

    Pair<Boolean, Boolean> pickupHttpMethod(ControllerInfo controllerInfo, Method method) {
        boolean sisGet = AnnotationUtils.findAnnotation(method, GET.class) != null;
        boolean sisPost = AnnotationUtils.findAnnotation(method, POST.class) != null;

        if (!isGet && !isPost) {
            sisGet = controllerInfo.isGet();
            sisPost = controllerInfo.isPost();
        }

        return Pair.build(sisGet, sisPost);

    }

    private String simplyPathPattern(String combinedPattern) {
        String ncombinedPattern = combinedPattern;
        if (combinedPattern.length() > 1 && combinedPattern.endsWith("/")) {
            ncombinedPattern = combinedPattern.substring(0, combinedPattern.length() - 2);
        }
        return ncombinedPattern;
    }

    private String combinePathPattern(String typePath, String methodPath) {
        logger.info(String.format("%s-%s", typePath, methodPath));
        return getPathMatcher().combine(typePath, methodPath);
    }

    List<ActionInterceptor> getPreInterceptorList(List<InterceptorInfo> interceptorInfoList) {

        ImmutableList.Builder<ActionInterceptor> builder = ImmutableList.builder();

        for (InterceptorInfo interceptorInfo : interceptorInfoList) {
            ActionInterceptor preInterceptor = interceptorInfo.getPreInterceptor();
            if (preInterceptor != null)
                builder.add(preInterceptor);
        }

        return builder.build();
    }

    List<ActionInterceptor> getPostInterceptorList(List<InterceptorInfo> interceptorInfoList) {

        ImmutableList.Builder<ActionInterceptor> builder = ImmutableList.builder();

        for (InterceptorInfo interceptorInfo : interceptorInfoList) {
            ActionInterceptor postInterceptor = interceptorInfo.getPostInterceptor();
            if (postInterceptor != null)
                builder.add(postInterceptor);
        }

        //反转，对于post先执行排序高的，再执行排序低的
        return builder.build().reverse();
    }

    List<InterceptorInfo> findInterceptors() {

        List<InterceptorInfo> interceptorInfoList = Lists.newArrayList();

        for (Annotation ann : this.annotations()) {

            InterceptorInfo interceptorInfo = findInterceptorInfo(ann);
            if (interceptorInfo == null)
                continue;

            interceptorInfoList = merge(interceptorInfoList, interceptorInfo);
        }

        return interceptorInfoList;
    }

    private InterceptorInfo findInterceptorInfo(Annotation ann) {
        Interceptor preA = AnnotationUtils.findAnnotation(ann.getClass(), Interceptor.class);
        if (preA == null)
            return null;

        Object orderObject = AnnotationUtils.getValue(ann, "order");

        int sorder = orderObject == null ? 100 : (Integer) orderObject; // xxx: maybe throw exception.

        ActionInterceptor preInterceptor = (preA.type() == Interceptor.InterceptorType.ACTION ? MVCDI.getInstance(preA.value()) : null);

        ActionInterceptor postInterceptor = (preA.type() == Interceptor.InterceptorType.RESULT ? MVCDI.getInstance(preA.value()) : null);

        return new InterceptorInfo(ann, sorder, preInterceptor, postInterceptor);
    }

    private List<InterceptorInfo> merge(List<InterceptorInfo> interceptorInfoList, InterceptorInfo interceptorInfo) {

        int position = interceptorInfoList.size();

        for (int index = 0; index < interceptorInfoList.size(); index++) {
            InterceptorInfo item = interceptorInfoList.get(index);
            // 如果annotation已存在，则忽略（先处理方法的Annotation，再处理类的Annotation）
            if (item.sample(interceptorInfo))
                return interceptorInfoList;

            if (item.getOrder() > interceptorInfo.getOrder()) {
                position = index;
            }
        }

        interceptorInfoList.add(position, interceptorInfo);
        return interceptorInfoList;
    }

    private class InterceptorInfo {
        private final Annotation annotation;
        private final ActionInterceptor preInterceptor;
        private final ActionInterceptor postInterceptor;
        private final int order;

        private InterceptorInfo(Annotation annotation, int order, ActionInterceptor preInterceptor, ActionInterceptor postInterceptor) {
            this.annotation = annotation;
            this.order = order;
            this.preInterceptor = preInterceptor;
            this.postInterceptor = postInterceptor;
        }

        private Annotation getAnnotation() {
            return annotation;
        }

        public ActionInterceptor getPreInterceptor() {
            return preInterceptor;
        }

        public ActionInterceptor getPostInterceptor() {
            return postInterceptor;
        }

        public int getOrder() {
            return order;
        }

        public boolean sample(InterceptorInfo other) {
            return this.getAnnotation().annotationType() == other.getAnnotation().annotationType();
        }
    }

    @Override
    public Method getActionMethod() {
        return method();
    }

}
