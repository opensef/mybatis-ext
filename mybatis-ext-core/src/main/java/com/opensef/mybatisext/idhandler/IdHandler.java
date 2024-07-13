package com.opensef.mybatisext.idhandler;

/**
 * id生成处理器
 *
 * @param <T> id的类型
 */
public interface IdHandler<T> {

    T getId();

}
