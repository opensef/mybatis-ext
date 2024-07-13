package com.opensef.mybatisext.exception;

public class MybatisExtException extends RuntimeException {

    public MybatisExtException() {
        super();
    }

    public MybatisExtException(String message) {
        super(message);
    }

    public MybatisExtException(Throwable cause) {
        super(cause);
    }

}
