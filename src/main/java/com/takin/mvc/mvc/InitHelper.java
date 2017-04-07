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
import com.takin.mvc.mvc.context.WFApplicationContext;
import com.takin.mvc.mvc.exception.WFException;
import com.takin.mvc.mvc.inject.GuiceDI;
import com.takin.mvc.mvc.inject.UserModule;
import com.takin.mvc.mvc.inject.WFModule;
import com.takin.mvc.spring.AnnotationUtils;
import com.takin.mvc.util.ClassUtils;
import com.takin.mvc.util.OnlyOnceCondition;

public class InitHelper {

    private InitHelper() {
    }

    public static final InitHelper instance = new InitHelper();

    private ServletContext servletContext;

    private Set<Class<? extends WFController>> controllerClasses;

    private File currentFolder;

    private static final Logger logger = LoggerFactory.getLogger(InitHelper.class);

    private final OnlyOnceCondition onlyOnce = OnlyOnceCondition.create("WF has been initialized.");

    public void init(ServletContext servletContext) {
        try {
            onlyOnce.check();
            servletContext.log("WF initing...");

            this.currentFolder = innerCurrentFolder();
            this.servletContext = servletContext;

            List<Module> modules = Lists.newArrayList();
            modules.add(new WFModule(this));

            GuiceDI.createInjector(modules);
            logger.info("guice init wf module");

            try {
                Module beanModule = WFApplicationContext.getModule();
                GuiceDI.createChildInjector(beanModule);
                logger.info("guice init bean module");
            } catch (Exception e) {
                logger.error("", e);
            }

            this.controllerClasses = parseControllers("");

            modules = Lists.newArrayList();
            modules.add(new UserModule());
            GuiceDI.createChildInjector(modules);
            logger.info("guice init user module");

            customInit();
            logger.info("WF initialized");
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    //初始化所有IInit的实现类
    private void customInit() {

    }

    public Executor commonExecutor() {
        return GuiceDI.getInstance(Executor.class);
    }

    public ServletContext servletContext() {
        return this.servletContext;
    }

    public Set<Class<? extends WFController>> getControllerClasses() {
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
    private Set<Class<? extends WFController>> parseControllers(String packagePrefix) {
        logger.info("start load all class");
        Set<Class<?>> classSet = ClassUtils.getClasses(packagePrefix);
        logger.info("end   load all class");

        ImmutableSet.Builder<Class<? extends WFController>> builder = ImmutableSet.builder();

        for (Class<?> clazz : classSet) {
            if (AnnotationUtils.isClassAnnotationed(clazz, Controller.class)) {
                logger.info("add class:" + clazz.getName());
                builder.add((Class<? extends WFController>) clazz).build();
            }
        }

        return builder.build();
    }

}