package com.opensef.mybatisext.annotation;

import java.lang.annotation.*;

/**
 * 数据权限注解
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataScope {

    /**
     * 功能编码
     *
     * @return 功能编码
     */
    String functionCode();

}
