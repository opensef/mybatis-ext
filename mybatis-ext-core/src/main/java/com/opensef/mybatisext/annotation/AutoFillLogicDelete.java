package com.opensef.mybatisext.annotation;

import java.lang.annotation.*;

/**
 * 逻辑删除时自动填充注解
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFillLogicDelete {

}
