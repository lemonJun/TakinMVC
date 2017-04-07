package com.takin.mvc.mvc.bind;

import com.takin.mvc.mvc.BeatContext;
import com.takin.mvc.mvc.Dispatcher;
import com.takin.mvc.mvc.inject.GuiceDI;
import com.takin.mvc.spring.BeanUtils;

/**
 * 用于初始化绑定和校验工具并且完成绑定操作。
 * 目前使用的校验工具为JSR303的参考实现Hibernate Validator。
 * 
 * @author lemon
 *
 */
public class BindAndValidate {

    public static ObjectBindResult bindAndValidate(Class<?> clazz) {

        BeatContext beat = GuiceDI.getInstance(Dispatcher.class).currentBeatContext();

        ObjectBindResult obr = bind(clazz, beat);

        //		validate(obr.getTarget());

        beat.getBindResults().add(obr);

        return obr;

    }

    /**
     * 根据beat信息绑定一个目标对象
     */
    private static ObjectBindResult bind(Object target, BeatContext beat) {
        RequestBinder binder = new RequestBinder(target);
        binder.bind(beat.getRequest());
        BindingResult r = binder.getBindingResult();
        return new ObjectBindResult(r);
    }

    /**
     * 根据beat信息,和目标对象的类型，绑定一个目标对象
     * @param targetType
     * @param beat
     * @return
     * @throws Exception
     */
    private static ObjectBindResult bind(Class<?> targetType, BeatContext beat) {

        Object target = BeanUtils.instantiateClass(targetType);
        return bind(target, beat);

    }

    // private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    //	/**
    //	 * 校验一个目标对象
    //	 * @param <T>
    //	 * @param target
    //	 * @return
    //	 */
    //	public static <T> ObjectBindResult validate(T target){;
    //		//javax.validation.ValidatorFactory.
    //		
    //		Set<ConstraintViolation<T>> constraintViolations = validator.validate(target);
    //		List<CheckedError> errors = new ArrayList<CheckedError>();
    //		for(ConstraintViolation<T> constraintViolation : constraintViolations) {
    //			CheckedError error = new CheckedError(CheckedError.ErrorType.VALIDATE,
    //					constraintViolation.getPropertyPath().toString(), 
    //					constraintViolation.getMessage());
    //			errors.add(error);
    //		}	
    //		return new ObjectBindResult(target, errors);
    //	}

}
