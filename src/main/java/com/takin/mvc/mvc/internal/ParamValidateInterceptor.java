package com.takin.mvc.mvc.internal;

import java.lang.reflect.Method;
import java.util.Map;

import com.google.common.collect.Maps;
import com.takin.mvc.mvc.ActionResult;
import com.takin.mvc.mvc.BeatContext;
import com.takin.mvc.mvc.annotation.ParamWithoutValidate;
import com.takin.mvc.mvc.interceptor.ActionInterceptor;
import com.takin.mvc.util2.AnnotationUtils;

public class ParamValidateInterceptor implements ActionInterceptor {

    private final static Map<Method, String[]> withoutValidate = Maps.newHashMap();

    @Override
    public ActionResult preExecute(BeatContext beat) {

        Method method = beat.getAction().getActionMethod();
        String[] strs = withoutValidate.get(method);

        if (strs != null) {

            beat.getModel().add("__PARAMSWITHOUTVALIDATE", strs);
            return null;
        }

        ParamWithoutValidate nci = AnnotationUtils.findAnnotation(method, ParamWithoutValidate.class);
        if (nci != null) {
            String[] paramsWithoutValidate = nci.value();
            //			beat.setParamWithoutValidate(paramsWithoutValidate);
            withoutValidate.put(method, paramsWithoutValidate);
        } else {
            withoutValidate.put(method, new String[0]);
        }

        return null;
    }

}
