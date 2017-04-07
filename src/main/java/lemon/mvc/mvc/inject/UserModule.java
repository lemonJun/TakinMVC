package lemon.mvc.mvc.inject;

import com.google.inject.AbstractModule;

import lemon.mvc.mvc.InitHelper;
import lemon.mvc.mvc.WFController;

public class UserModule extends AbstractModule {

    @Override
    protected void configure() {
        for (Class<? extends WFController> clazz : InitHelper.instance.getControllerClasses()) {
            //            bind(clazz).in(Singleton.class);
        }
    }
}
