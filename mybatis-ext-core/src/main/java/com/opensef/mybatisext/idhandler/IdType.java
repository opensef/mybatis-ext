package com.opensef.mybatisext.idhandler;

/**
 * ID生成方式
 */
public enum IdType {

    /**
     * 数据库自增
     */
    DB_AUTO,

    /**
     * 程序自动赋值（雪花算法）
     */
    AUTO,

    /**
     * UUID算法
     */
    UUID,

    /**
     * 自定义
     */
    CUSTOM,

}
