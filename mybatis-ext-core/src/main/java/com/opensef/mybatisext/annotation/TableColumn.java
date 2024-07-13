package com.opensef.mybatisext.annotation;

import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.lang.annotation.*;

/**
 * 列名注解
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TableColumn {

    /**
     * 值
     *
     * @return 列名
     */
    String value() default "";

    /**
     * 是否忽略书偶像
     *
     * @return true/false
     */
    boolean ignore() default false;

    Class<? extends TypeHandler<?>> typeHandler() default UnknownTypeHandler.class;

}
