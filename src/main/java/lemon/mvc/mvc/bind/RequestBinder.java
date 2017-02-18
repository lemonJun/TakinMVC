package lemon.mvc.mvc.bind;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lemon.mvc.mvc.converter.ConverterFactory;

/**
 * WF自实现的错误对象实体
 * @author lemon
 *
 */
public class RequestBinder {

    private static final Logger logger = LoggerFactory.getLogger(RequestBinder.class);

    private Object target;

    private List<ObjectError> objectErrors = new ArrayList<ObjectError>();

    private final ConverterFactory converter = new ConverterFactory();

    public RequestBinder(Object target) {
        this.target = target;
    }

    public void bind(HttpServletRequest httpServletRequest) {

        Field[] fields = target.getClass().getDeclaredFields();

        for (Field field : fields) {

            Class<?> clazzClass = field.getType();
            String reqParaValue = httpServletRequest.getParameter(field.getName());
            if (converter.canConvert(clazzClass) && reqParaValue != null && !"".equals(reqParaValue)) {
                try {

                    Object object = converter.convert(clazzClass, reqParaValue);
                    Method m = (Method) target.getClass().getMethod("set" + getMethodName(field.getName()), field.getType());
                    m.invoke(target, object);
                } catch (Exception e) {
                    logger.error("", e);
                    ObjectError error = new ObjectError();
                    error.setObjectName(target.getClass().getName() + " : " + field.getName());
                    error.setErrorMessage("Param Bind Error : " + field.getName());
                    objectErrors.add(error);
                }
            }

        }
    }

    public BindingResult getBindingResult() {

        BindingResult br = new BindingResult();
        br.setTarget(target);
        br.setErrors(objectErrors);

        return br;
    }

    private static String getMethodName(String fildeName) throws Exception {
        byte[] items = fildeName.getBytes();
        items[0] = (byte) ((char) items[0] - 'a' + 'A');
        return new String(items);
    }
}
