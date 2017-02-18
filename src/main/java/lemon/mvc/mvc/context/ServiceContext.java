package lemon.mvc.mvc.context;

public interface ServiceContext {

    Object getBean(String name);

    //	<T> T getBean(String name, Class<T> requiredType) ;
    //
    //	<T> T getBean(Class<T> requiredType) ;
    //
    //	Object getBean(String name, Object... args) ;

    boolean containsBean(String name);

    boolean isSingleton(String name);
}
