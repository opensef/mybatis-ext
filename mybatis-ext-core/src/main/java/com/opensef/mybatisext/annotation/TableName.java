package com.opensef.mybatisext.annotation;

import java.lang.annotation.*;

/**
 * 表名注解
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TableName {

    String value();

}
