package com.opensef.mybatisext.annotation;

import java.lang.annotation.*;

/**
 * 删除标识注解
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Deleted {

    boolean logicDelete() default false;

}
