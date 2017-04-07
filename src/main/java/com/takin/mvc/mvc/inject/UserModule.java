package com.takin.mvc.mvc.inject;

import com.google.inject.AbstractModule;
import com.takin.mvc.mvc.InitHelper;
import com.takin.mvc.mvc.WFController;

public class UserModule extends AbstractModule {

    @Override
    protected void configure() {
        for (Class<? extends WFController> clazz : InitHelper.instance.getControllerClasses()) {
            //            bind(clazz).in(Singleton.class);
        }
    }
}
