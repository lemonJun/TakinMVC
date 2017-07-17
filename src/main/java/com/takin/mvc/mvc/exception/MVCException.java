package com.takin.mvc.mvc.exception;

import java.io.IOException;

public class MVCException extends RuntimeException {
    public MVCException(String message) {
        super(message);
    }

    public MVCException(String message, Throwable e) {
        super(message, e);
    }

    public MVCException(String format, Object... args) {
        super(String.format(format, args));
    }

    public MVCException(Throwable e, String format, Object... args) {
        super(String.format(format, args), e);
    }

    public MVCException(IOException e) {
        super(e);
    }

    public MVCException(Throwable e) {
        super(e);
    }
}