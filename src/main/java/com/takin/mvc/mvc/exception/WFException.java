package com.takin.mvc.mvc.exception;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * 异常处理类
 */
public class WFException extends RuntimeException {

    public static WFExceptionBuilder newBuilder(String message) {
        return newBuilder(message, null);
    }

    public static WFExceptionBuilder newBuilder(Throwable cause) {
        return newBuilder("", cause);
    }

    public static WFExceptionBuilder newBuilder() {
        return newBuilder("", null);
    }

    public static WFExceptionBuilder newBuilder(String message, Throwable cause) {
        return new WFExceptionBuilder(message, cause);
    }

    private static final long serialVersionUID = 5099827279044223975L;

    WFException() {
        super();
    }

    WFException(String message) {
        super(message);
    }

    WFException(Throwable cause) {
        super(cause);
    }

    WFException(String message, Throwable cause) {
        super(message, cause);
    }

    public static WFException raise(String message) {
        return new WFException(message);
    }

    public static WFException raise(Throwable cause) {
        return new WFException(cause);
    }

    public static WFException raise(String message, Throwable cause) {
        return new WFException(message, cause);
    }

    public static class WFExceptionBuilder {
        private final Map<String, Object> contextInfos = Maps.newLinkedHashMap();

        private final Throwable cause;

        private final String currentMessage;

        WFExceptionBuilder(String message, Throwable cause) {
            this.currentMessage = message;
            this.cause = cause;
        }

        WFExceptionBuilder(Throwable cause) {
            this("", cause);
        }

        WFExceptionBuilder(String message) {
            this(message, null);
        }

        /**
         * 给异常增加上下文变量信息。
         * @param name 变量名
         * @param value 变量值
         * @return 自身
         */
        public WFExceptionBuilder addContextVariable(String name, Object value) {
            contextInfos.put(name, value);
            return this;
        }

        public WFExceptionBuilder addContextVariables(Map<?, ?> variables) {
            for (Map.Entry entry : variables.entrySet())
                addContextVariable(entry.toString(), entry.getValue());

            return this;
        }

        /**
         * 创建一个WFException
         */
        public WFException build() {
            return new WFException(getContextInfo(), cause);
        }

        /**
         * throw
         * @param clazz
         * @param <T>
         * @return
         */
        public <T> T raise(Class<T> clazz) {
            throw build();
        }

        private String getContextInfo() {
            return this.currentMessage + (contextInfos.size() > 0 ? "\ncontext: " + contextInfos.toString() : "");
        }
    }
}
