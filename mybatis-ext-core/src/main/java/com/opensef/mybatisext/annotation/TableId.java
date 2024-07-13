package com.opensef.mybatisext.annotation;


import com.opensef.mybatisext.idhandler.IdGeneratorNone;
import com.opensef.mybatisext.idhandler.IdHandler;
import com.opensef.mybatisext.idhandler.IdType;

import java.lang.annotation.*;

/**
 * ID标识注解
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TableId {

    /**
     * ID生成方式，默认为程序自动赋值
     *
     * @return IdType
     */
    IdType type() default IdType.AUTO;

    /**
     * 自定义ID生成器，type为IdType.CUSTOM时生效<br>
     * 除了使用公共配置的自定义id生成器外，还可以单独为实体指定ID生成器
     *
     * @return Class
     */
    Class<? extends IdHandler<?>> idHandler() default IdGeneratorNone.class;

}
