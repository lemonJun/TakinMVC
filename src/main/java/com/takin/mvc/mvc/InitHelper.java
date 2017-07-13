package com.takin.mvc.mvc;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.Module;
import com.takin.mvc.mvc.annotation.Controller;
import com.takin.mvc.mvc.annotation.Init;
import com.takin.mvc.mvc.exception.WFException;
import com.takin.mvc.mvc.inject.MVCDI;
import com.takin.mvc.mvc.inject.UserModule;
import com.takin.mvc.mvc.inject.WFModule;
import com.takin.mvc.util.ClassUtils;
import com.takin.mvc.util.OnlyOnceCondition;
import com.takin.mvc.util2.AnnotationUtils;

public class InitHelper {
    private static final Logger logger = LoggerFactory.getLogger(InitHelper.class);

    private InitHelper() {
    }

    public static final InitHelper instance = new InitHelper();

    private ServletContext servletContext;

    private Set<Class<? extends MVCController>> controllerClasses;

    private File currentFolder;

    private final List<IInit> initers = Lists.newArrayList();;

    private final OnlyOnceCondition onlyOnce = OnlyOnceCondition.create("WF has been initialized.");

    public void init(ServletContext servletContext) {
        try {
            onlyOnce.check();
            this.currentFolder = innerCurrentFolder();
            this.servletContext = servletContext;

            List<Module> modules = Lists.newArrayList();
            modules.add(new WFModule(this));

            MVCDI.createInjector(modules);
            logger.info("guice init ..");

            this.controllerClasses = parseControllers("");

            modules = Lists.newArrayList();
            modules.add(new UserModule());
            MVCDI.createChildInjector(modules);
            logger.info("guice init user module");
            for (IInit initer : initers) {
                initer.init();
            }
            logger.info("WF initialized");
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    //初始化所d
    public Executor commonExecutor() {
        return MVCDI.getInstance(Executor.class);
    }

    public ServletContext servletContext() {
        return this.servletContext;
    }

    public Set<Class<? extends MVCController>> getControllerClasses() {
        return controllerClasses;
    }

    private File innerCurrentFolder() {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            URL url = cl.getResource(".");
            return new File(url.toURI());
        } catch (URISyntaxException e) {
            throw WFException.raise(e);
        }
    }

    public File currentFolder() {
        return currentFolder;
    }

    @SuppressWarnings("unchecked")
    private Set<Class<? extends MVCController>> parseControllers(String packagePrefix) {
        logger.info("start load all class");
        Set<Class<?>> classSet = ClassUtils.getClasses(packagePrefix);
        logger.info("end   load all class");

        ImmutableSet.Builder<Class<? extends MVCController>> builder = ImmutableSet.builder();

        for (Class<?> clazz : classSet) {
            if (AnnotationUtils.isClassAnnotationed(clazz, Controller.class)) {
                logger.info("add class:" + clazz.getName());
                builder.add((Class<? extends MVCController>) clazz).build();
            }
            if (AnnotationUtils.isClassAnnotationed(clazz, Init.class)) {
                initers.add((IInit) MVCDI.getInstance(clazz));
            }
        }
        return builder.build();
    }

}
