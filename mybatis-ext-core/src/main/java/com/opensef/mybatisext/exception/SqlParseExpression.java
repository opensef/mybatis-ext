package com.opensef.mybatisext.exception;

/**
 * sql解析异常
 */
public class SqlParseExpression extends RuntimeException {

    public SqlParseExpression(String message) {
        super(message);
    }

}
