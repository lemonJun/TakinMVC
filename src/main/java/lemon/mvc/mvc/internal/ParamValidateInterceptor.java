package lemon.mvc.mvc.internal;

import java.lang.reflect.Method;
import java.util.Map;

import com.google.common.collect.Maps;

import lemon.mvc.mvc.ActionResult;
import lemon.mvc.mvc.BeatContext;
import lemon.mvc.mvc.annotation.ParamWithoutValidate;
import lemon.mvc.mvc.interceptor.ActionInterceptor;
import lemon.mvc.spring.AnnotationUtils;

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
