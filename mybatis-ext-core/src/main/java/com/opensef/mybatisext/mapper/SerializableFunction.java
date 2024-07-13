package com.opensef.mybatisext.mapper;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 可序列化的函数式接口
 *
 * @param <T> 实体类型
 * @param <R> 方法返回类型
 */
public interface SerializableFunction<T, R> extends Function<T, R>, Serializable {

}
