package com.takin.mvc.mvc.internal;

//package com.bj58.wf.mvc.internal;
//
//import javax.inject.Inject;
//
//import com.bj58.wf.mvc.log.Logger;
//
//public class DefaultLogger implements Logger {
//
//    private final org.slf4j.Logger logger;
//
//    @Inject
//    public DefaultLogger(org.slf4j.Logger logger) {
//        this.logger = logger;
//    }
//
//    @Override
//    public void debug(String msg) {
//        logger.debug(msg);
//    }
//
//    @Override
//    public void debug(String format, Object arg) {
//        logger.debug(format, arg);
//    }
//
//    @Override
//    public void debug(String msg, Throwable t) {
//        logger.debug(msg, t);
//    }
//
//    @Override
//    public void info(String msg) {
//        logger.info(msg);
//    }
//
//    @Override
//    public void info(String format, Object arg) {
//        logger.info(format, arg);
//    }
//
//    @Override
//    public void info(String msg, Throwable t) {
//        logger.info(msg, t);
//    }
//
//    @Override
//    public void error(String msg) {
//        logger.error(msg);
//    }
//
//    @Override
//    public void error(String msg, Throwable t) {
//        logger.error(msg, t);
//    }
//
//    @Override
//    public void error(String format, Object arg) {
//
//        logger.error(format, arg);
//    }
//
//    @Override
//    public String toString() {
//
//        return logger.toString();
//    }
//
//    @Override
//    public boolean isDebugEnabled() {
//
//        return logger.isDebugEnabled();
//    }
//
//    @Override
//    public boolean isInfoEnabled() {
//
//        return logger.isInfoEnabled();
//    }
//
//    @Override
//    public boolean isWarnEnabled() {
//
//        return logger.isWarnEnabled();
//    }
//
//    @Override
//    public void warn(String msg) {
//
//        logger.warn(msg);
//
//    }
//
//    @Override
//    public void warn(String msg, Throwable t) {
//
//        logger.warn(msg, t);
//    }
//
//    @Override
//    public boolean isErrorEnabled() {
//
//        return logger.isErrorEnabled();
//    }
//
//    @Override
//    public String getName() {
//
//        return logger.getName();
//    }
//
//    @Override
//    public void warn(String format, Object arg) {
//        logger.warn(format, arg);
//    }
//}
