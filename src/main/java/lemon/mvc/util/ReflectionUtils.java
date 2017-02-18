package lemon.mvc.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lemon.mvc.mvc.exception.BaseRuntimeException;
import lemon.mvc.mvc.exception.ExceptionUtils;

/**
 * Reflection related utility class.
 * 
 * @author howsun (zjh@58.com)
 * @version v0.1  (2010-11-10)
 */
public abstract class ReflectionUtils {

    /**Empty class array*/
    public static final Class<?>[] EMPTY = new Class<?>[0];

    /**Logger instance*/
    private static final Logger logger = LoggerFactory.getLogger(ReflectionUtils.class);

    /**
     * Gets current thread classloader.
     * <p>
     * This class loader works that is explained in the
     * Java EE specifications.
     * </p>
     * @return current thread classloader
     */
    public static ClassLoader getThreadClassLoader() {
        ClassLoader loader;

        loader = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {

            @Override
            public ClassLoader run() {
                try {
                    return Thread.currentThread().getContextClassLoader();

                } catch (SecurityException e) {
                    return null;
                }
            }
        });

        return loader;
    }

    /**
     * Gets class instance.
     * @param <T> class type
     * @param clazz class of the object
     * @return the new instance of class
     */
    public static <T> Object getNewObject(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            String error = "InstantiationException exception is thrown while " + "creating instance of class : " + clazz;
            logger.error(error, e);
            throw ExceptionUtils.makeThrow(error, e);

        } catch (IllegalAccessException e) {
            String error = "IllegalAccessException exception is thrown while " + "creating instance of class : " + clazz;
            throw ExceptionUtils.makeThrow(error, e);
        }
    }

    /**
     * Gets class constructor with the given parameters..
     * @param <T> class type
     * @param clazz class of the object
     * @param arguments contructor arguments
     * @return class constructor
     */
    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> getConstructor(Class<T> clazz, List<Class<?>> arguments) {
        AssertUtil.notNull(clazz);
        AssertUtil.notEmpty(arguments);
        Constructor<?> constr = null;
        try {
            constr = clazz.getDeclaredConstructor(arguments.toArray(EMPTY));
        } catch (SecurityException e) {
            String error = "SecurityException exception is thrown by getting constructor";
            logger.error(error, e);
            throw ExceptionUtils.makeThrow(error, e);

        } catch (NoSuchMethodException e) {
            String error = "NoSuchMethodException exception is thrown by getting constructor";
            logger.error(error, e);
            throw ExceptionUtils.makeThrow(error, e);
        }
        return (Constructor<T>) constr;
    }

    /**
     * Gets system class loader.
     * @return system class loader
     */
    public static ClassLoader getSystemClassLoader() {
        return ClassLoader.getSystemClassLoader();
    }

    /**
     * Calls the given object's method with supplied arguments.
     * @param instance object instance
     * @param method object method
     * @param arguments method arguments
     * @return the final result
     * @throws DefneException if any runtime exception occurs
     */
    public static Object invokeMethod(Object instance, Method method, Object[] arguments) {
        Object result = null;
        try {
            result = method.invoke(instance, arguments);
        } catch (IllegalArgumentException e) {
            String error = "IllegalArgument exception is thrown by calling method " + method + " on instance " + instance + " with arguments " + arguments;
            logger.error(error, e);
            throw ExceptionUtils.makeThrow(error, e);
        } catch (IllegalAccessException e) {
            String error = "IllegalAccessException exception is thrown by calling method " + method + " on instance " + instance + " with arguments " + arguments;
            logger.error(error, e);
            throw ExceptionUtils.makeThrow(error, e);
        } catch (InvocationTargetException e) {
            String error = "InvocationTargetException exception is thrown by calling method " + method + " on instance " + instance + " with arguments " + arguments;
            Throwable e1 = ExceptionUtils.getTargetException(e);
            logger.error(error, e1);
            throw ExceptionUtils.makeThrow(error, e1);
        }
        return result;
    }

    /**
     * Load class with the given name and returns it.
     * If not succesfull, return null.
     * @param className class name
     * @return the class with the given name
     */
    public static Class<?> getClassFromName(String className) {
        AssertUtil.notNull(className);
        ClassLoader loader = getThreadClassLoader();
        try {
            if (loader == null) {
                return Class.forName(className);
            } else {
                return Class.forName(className, true, loader);
            }
        } catch (ClassNotFoundException e) {
            String error = "Class not found exception is thrown while " + "getting class : " + className;
            logger.error(error, e);
            throw ExceptionUtils.makeThrow(error, e);
        }
    }

    public static Object callConstructor(Constructor<?> constructor, Object[] arguments) {
        try {
            return constructor.newInstance(arguments);
        } catch (IllegalArgumentException e) {
            String error = "IllegalArgumentException exception is thrown by calling constructor " + constructor + " with arguments " + arguments;
            Throwable e1 = ExceptionUtils.getTargetException(e);
            logger.error(error, e1);
            throw ExceptionUtils.makeThrow(error, e1);
        } catch (InstantiationException e) {
            String error = "InstantiationException exception is thrown by calling constructor " + constructor + " with arguments " + arguments;
            Throwable e1 = ExceptionUtils.getTargetException(e);
            logger.error(error, e1);
            throw ExceptionUtils.makeThrow(error, e1);
        } catch (IllegalAccessException e) {
            String error = "IllegalAccessException exception is thrown by calling constructor " + constructor + " with arguments " + arguments;
            Throwable e1 = ExceptionUtils.getTargetException(e);
            logger.error(error, e1);
            throw ExceptionUtils.makeThrow(error, e1);
        } catch (InvocationTargetException e) {
            String error = "InvocationTargetException exception is thrown by calling constructor " + constructor + " with arguments " + arguments;
            Throwable e1 = ExceptionUtils.getTargetException(e);
            logger.error(error, e1);
            throw ExceptionUtils.makeThrow(error, e1);
        }
    }

    public static void throwDefneExceptionFromPrivilege(PrivilegedActionException e) {
        BaseRuntimeException exception = null;
        if (e.getCause() instanceof BaseRuntimeException) {
            exception = (BaseRuntimeException) e.getCause();
        } else {
            exception = ExceptionUtils.makeThrow("", e.getCause());
        }
        throw exception;
    }

    public List<Method> findAllMethods(final Class<?> clazz) {
        final Method[] declaredMethods = clazz.getDeclaredMethods();
        final List<Method> methods = new ArrayList<Method>();

        for (final Method method : declaredMethods) {
            methods.add(method);
        }

        if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class)) {
            methods.addAll(findAllMethods(clazz.getSuperclass()));
        }

        return methods;
    }

    /**
     * Gets all members from a class and its superclasses.
     *
     * <p>The members are sorted from superclass to subclass, with fields
     * and methods in the superclass first, then fields and methods in subclasses.</p>
     *
     * @param clazz The class to find the members in.
     * @return List with all the members found.
     */
    public List<Member> findAllMembers(final Class<?> clazz) {
        final List<Member> members = new ArrayList<Member>();

        final Field[] declaredFields = clazz.getDeclaredFields();

        for (final Field field : declaredFields) {
            members.add(field);
        }

        final Method[] declaredMethods = clazz.getDeclaredMethods();

        for (final Method method : declaredMethods) {
            members.add(method);
        }

        if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class)) {
            members.addAll(0, findAllMembers(clazz.getSuperclass()));
        }

        return members;
    }

    public boolean isOverridden(final Method method, final List<Method> candidates) {
        for (final Method candidate : candidates) {
            if (isOverridden(method, candidate)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if a method has been overridden by a candidate.
     *
     * <p>Rules for overriding:</p>
     * <ul>
     *   <li>Candidate must be in a subclass of the class with method.</li>
     *   <li>The method names must be the same.</li>
     *   <li>The parameters must be the same.</li>
     *   <li>The return type must be the same or a subclass.</li>
     *   <li>Access modifier must be same or less restrictive.</li>
     *   <li>Method can not be private, final or static.</li>
     *   <li>Candidate can not be private or static.</li>
     *   <li>Candidate must be in the same package as a method with default access.</li>
     * </ul>
     *
     * @param method The method that will be checked if it's been overridden.
     * @param candidate The method to check if it's overriding.
     * @return If method has been overridden by candidate.
     */
    public boolean isOverridden(final Method method, final Method candidate) {
        if (!hasOverridableAccessModifiers(method, candidate)) {
            return false;
        }

        if (!isSubClassOf(candidate.getDeclaringClass(), method.getDeclaringClass())) {
            return false;
        }

        if (!hasTheSameName(method, candidate)) {
            return false;
        }

        if (!hasTheSameParameters(method, candidate)) {
            return false;
        }

        return true;
    }

    public boolean hasOverridableAccessModifiers(final Method method, final Method candidate) {
        if (isFinal(method) || isPrivate(method) || isStatic(method) || isPrivate(candidate) || isStatic(candidate)) {
            return false;
        }

        if (isDefault(method)) {
            return isInTheSamePackage(method, candidate);
        }

        return true;
    }

    public boolean isSubClassOf(final Class<?> subclass, final Class<?> superclass) {
        if (subclass.getSuperclass() != null) {
            if (subclass.getSuperclass().equals(superclass)) {
                return true;
            }

            return isSubClassOf(subclass.getSuperclass(), superclass);
        }

        return false;
    }

    public boolean hasTheSameName(final Method method, final Method candidate) {
        return method.getName().equals(candidate.getName());
    }

    public boolean hasTheSameParameters(final Method method, final Method candidate) {
        final Class<?>[] methodParameters = method.getParameterTypes();
        final Class<?>[] candidateParameters = candidate.getParameterTypes();

        if (methodParameters.length != candidateParameters.length) {
            return false;
        }

        for (int i = 0; i < methodParameters.length; i++) {
            final Class<?> methodParameter = methodParameters[i];
            final Class<?> candidateParameter = candidateParameters[i];

            if (!methodParameter.equals(candidateParameter)) {
                return false;
            }
        }

        return true;
    }

    public boolean isInTheSamePackage(final Method method, final Method candidate) {
        final Package methodPackage = method.getDeclaringClass().getPackage();
        final Package candidatePackage = candidate.getDeclaringClass().getPackage();

        return methodPackage.equals(candidatePackage);
    }

    public boolean isStatic(final Member member) {
        return Modifier.isStatic(member.getModifiers());
    }

    public boolean isFinal(final Member member) {
        return Modifier.isFinal(member.getModifiers());
    }

    public boolean isPrivate(final Member member) {
        return Modifier.isPrivate(member.getModifiers());
    }

    public boolean isDefault(final Member member) {
        return !isPublic(member) && !isProtected(member) && !isPrivate(member);
    }

    public boolean isProtected(final Member member) {
        return Modifier.isProtected(member.getModifiers());
    }

    public boolean isPublic(final Member member) {
        return Modifier.isPublic(member.getModifiers());
    }

    public boolean isAbstract(final Class<?> clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    public boolean isNormalClass(final Class<?> clazz) {
        return !clazz.isAnonymousClass() && !clazz.isMemberClass() && !clazz.isSynthetic() && !clazz.isAnnotation() && !clazz.isEnum() && !clazz.isInterface() && !isAbstract(clazz);
    }

}
