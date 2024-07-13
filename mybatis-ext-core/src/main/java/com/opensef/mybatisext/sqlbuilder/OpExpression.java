package com.opensef.mybatisext.sqlbuilder;

import java.util.Collection;
import java.util.Map;

/**
 * 运算表达式接口
 *
 * @param <T>          返回值类型
 * @param <ColumnType> 字段类型，支持String和Function两种类型
 */
public interface OpExpression<T, ColumnType> {

    /**
     * 等于
     *
     * @param column 列名
     * @param value  值
     * @return 泛型T
     */
    T eq(ColumnType column, Object value);

    /**
     * 等于
     *
     * @param column 列名
     * @param select Select对象
     * @return 泛型T
     */
    T eq(ColumnType column, PlainSelect select);

    /**
     * 等于
     *
     * @param condition 条件
     * @param column    列名
     * @param value     值
     * @return 泛型T
     */
    T eq(boolean condition, ColumnType column, Object value);

    /**
     * 等于
     *
     * @param condition 条件
     * @param column    列名
     * @param select    Select对象
     * @return 泛型T
     */
    T eq(boolean condition, ColumnType column, PlainSelect select);

    /**
     * 不等于
     *
     * @param column 列名
     * @param value  值
     * @return 泛型T
     */
    T notEq(ColumnType column, Object value);

    /**
     * 不等于
     *
     * @param column 列名
     * @param select Select对象
     * @return 泛型T
     */
    T notEq(ColumnType column, PlainSelect select);

    /**
     * 不等于
     *
     * @param condition 条件
     * @param column    列名
     * @param value     值
     * @return 泛型T
     */
    T notEq(boolean condition, ColumnType column, Object value);

    /**
     * 不等于
     *
     * @param condition 条件
     * @param column    列名
     * @param select    Select对象
     * @return 泛型T
     */
    T notEq(boolean condition, ColumnType column, PlainSelect select);

    /**
     * 大于
     *
     * @param column 列名
     * @param value  值
     * @return 泛型T
     */
    T gt(ColumnType column, Object value);

    /**
     * 大于
     *
     * @param column 列名
     * @param select Select对象
     * @return 泛型T
     */
    T gt(ColumnType column, PlainSelect select);

    /**
     * 大于
     *
     * @param condition 条件
     * @param column    列名
     * @param value     值
     * @return 泛型T
     */
    T gt(boolean condition, ColumnType column, Object value);

    /**
     * 大于
     *
     * @param condition 条件
     * @param column    列名
     * @param select    Select对象
     * @return 泛型T
     */
    T gt(boolean condition, ColumnType column, PlainSelect select);

    /**
     * 大于等于
     *
     * @param column 列名
     * @param value  值
     * @return 泛型T
     */
    T gte(ColumnType column, Object value);

    /**
     * 大于等于
     *
     * @param column 列名
     * @param select Select对象
     * @return 泛型T
     */
    T gte(ColumnType column, PlainSelect select);

    /**
     * 大于等于
     *
     * @param condition 条件
     * @param column    列名
     * @param value     值
     * @return 泛型T
     */
    T gte(boolean condition, ColumnType column, Object value);

    /**
     * 大于等于
     *
     * @param condition 条件
     * @param column    列名
     * @param select    Select对象
     * @return 泛型T
     */
    T gte(boolean condition, ColumnType column, PlainSelect select);

    /**
     * 小于
     *
     * @param column 列名
     * @param value  值
     * @return 泛型T
     */
    T lt(ColumnType column, Object value);

    /**
     * 小于
     *
     * @param column 列名
     * @param select Select对象
     * @return 泛型T
     */
    T lt(ColumnType column, PlainSelect select);

    /**
     * 小于
     *
     * @param condition 条件
     * @param column    列名
     * @param value     值
     * @return 泛型T
     */
    T lt(boolean condition, ColumnType column, Object value);

    /**
     * 小于
     *
     * @param condition 条件
     * @param column    列名
     * @param select    Select对象
     * @return 泛型T
     */
    T lt(boolean condition, ColumnType column, PlainSelect select);

    /**
     * 小于等于
     *
     * @param column 列名
     * @param value  值
     * @return 泛型T
     */
    T lte(ColumnType column, Object value);

    /**
     * 小于等于
     *
     * @param column 列名
     * @param select Select对象
     * @return 泛型T
     */
    T lte(ColumnType column, PlainSelect select);

    /**
     * 小于等于
     *
     * @param condition 条件
     * @param column    列名
     * @param value     值
     * @return 泛型T
     */
    T lte(boolean condition, ColumnType column, Object value);

    /**
     * 小于等于
     *
     * @param condition 条件
     * @param column    列名
     * @param select    Select对象
     * @return 泛型T
     */
    T lte(boolean condition, ColumnType column, PlainSelect select);

    /**
     * 包含
     *
     * @param column 列名
     * @param value  值
     * @return 泛型T
     */
    T in(ColumnType column, Collection<?> value);

    /**
     * 包含
     *
     * @param column 列名
     * @param select Select对象
     * @return 泛型T
     */
    T in(ColumnType column, PlainSelect select);

    /**
     * 包含
     *
     * @param condition 条件
     * @param column    列名
     * @param value     值
     * @return 泛型T
     */
    T in(boolean condition, ColumnType column, Collection<?> value);

    /**
     * 包含
     *
     * @param condition 条件
     * @param column    列名
     * @param select    Select对象
     * @return 泛型T
     */
    T in(boolean condition, ColumnType column, PlainSelect select);

    /**
     * 不包含
     *
     * @param column 列名
     * @param value  值
     * @return 泛型T
     */
    T notIn(ColumnType column, Collection<?> value);

    /**
     * 不包含
     *
     * @param column 列名
     * @param select Select对象
     * @return 泛型T
     */
    T notIn(ColumnType column, PlainSelect select);

    /**
     * 不包含
     *
     * @param condition 条件
     * @param column    列名
     * @param value     值
     * @return 泛型T
     */
    T notIn(boolean condition, ColumnType column, Collection<?> value);

    /**
     * 不包含
     *
     * @param condition 条件
     * @param column    列名
     * @param select    Select对象
     * @return 泛型T
     */
    T notIn(boolean condition, ColumnType column, PlainSelect select);

    /**
     * 模糊
     *
     * @param column 列名
     * @param value  值
     * @return 泛型T
     */
    T like(ColumnType column, Object value);

    /**
     * 模糊
     *
     * @param column 列名
     * @param select Select对象
     * @return 泛型T
     */
    T like(ColumnType column, PlainSelect select);

    /**
     * 模糊
     *
     * @param condition 条件
     * @param column    列名
     * @param value     值
     * @return 泛型T
     */
    T like(boolean condition, ColumnType column, Object value);

    /**
     * 模糊
     *
     * @param condition 条件
     * @param column    列名
     * @param select    Select对象
     * @return 泛型T
     */
    T like(boolean condition, ColumnType column, PlainSelect select);

    /**
     * 非模糊
     *
     * @param column 列名
     * @param value  值
     * @return 泛型T
     */
    T notLike(ColumnType column, Object value);

    /**
     * 非模糊
     *
     * @param column 列名
     * @param select Select对象
     * @return 泛型T
     */
    T notLike(ColumnType column, PlainSelect select);

    /**
     * 非模糊
     *
     * @param condition 条件
     * @param column    列名
     * @param value     值
     * @return 泛型T
     */
    T notLike(boolean condition, ColumnType column, Object value);

    /**
     * 非模糊
     *
     * @param condition 条件
     * @param column    列名
     * @param select    Select对象
     * @return 泛型T
     */
    T notLike(boolean condition, ColumnType column, PlainSelect select);

    /**
     * 左模糊
     *
     * @param column 列名
     * @param value  值
     * @return 泛型T
     */
    T leftLike(ColumnType column, Object value);

    /**
     * 左模糊
     *
     * @param column 列名
     * @param select Select对象
     * @return 泛型T
     */
    T leftLike(ColumnType column, PlainSelect select);

    /**
     * 左模糊
     *
     * @param condition 条件
     * @param column    列名
     * @param value     值
     * @return 泛型T
     */
    T leftLike(boolean condition, ColumnType column, Object value);

    /**
     * 左模糊
     *
     * @param condition 条件
     * @param column    列名
     * @param select    Select对象
     * @return 泛型T
     */
    T leftLike(boolean condition, ColumnType column, PlainSelect select);

    /**
     * 非左模糊
     *
     * @param column 列名
     * @param value  值
     * @return 泛型T
     */
    T notLeftLike(ColumnType column, Object value);

    /**
     * 非左模糊
     *
     * @param column 列名
     * @param select Select对象
     * @return 泛型T
     */
    T notLeftLike(ColumnType column, PlainSelect select);

    /**
     * 非左模糊
     *
     * @param condition 条件
     * @param column    列名
     * @param value     值
     * @return 泛型T
     */
    T notLeftLike(boolean condition, ColumnType column, Object value);

    /**
     * 非左模糊
     *
     * @param condition 条件
     * @param column    列名
     * @param select    Select对象
     * @return 泛型T
     */
    T notLeftLike(boolean condition, ColumnType column, PlainSelect select);

    /**
     * 右模糊
     *
     * @param column 列名
     * @param value  值
     * @return 泛型T
     */
    T rightLike(ColumnType column, Object value);

    /**
     * 右模糊
     *
     * @param column 列名
     * @param select Select对象
     * @return 泛型T
     */
    T rightLike(ColumnType column, PlainSelect select);

    /**
     * 右模糊
     *
     * @param condition 条件
     * @param column    列名
     * @param value     值
     * @return 泛型T
     */
    T rightLike(boolean condition, ColumnType column, Object value);

    /**
     * 右模糊
     *
     * @param condition 条件
     * @param column    列名
     * @param select    Select对象
     * @return 泛型T
     */
    T rightLike(boolean condition, ColumnType column, PlainSelect select);

    /**
     * 非右模糊
     *
     * @param column 列名
     * @param value  值
     * @return 泛型T
     */
    T notRightLike(ColumnType column, Object value);

    /**
     * 非右模糊
     *
     * @param column 列名
     * @param select 值
     * @return 泛型T
     */
    T notRightLike(ColumnType column, PlainSelect select);

    /**
     * 非右模糊
     *
     * @param condition 条件
     * @param column    列名
     * @param value     值
     * @return 泛型T
     */
    T notRightLike(boolean condition, ColumnType column, Object value);

    /**
     * 非右模糊
     *
     * @param condition 条件
     * @param column    列名
     * @param select    Select对象
     * @return 泛型T
     */
    T notRightLike(boolean condition, ColumnType column, PlainSelect select);

    /**
     * 为空
     *
     * @param column 列名
     * @return 泛型T
     */
    T isNull(ColumnType column);

    /**
     * 为空
     *
     * @param condition 条件
     * @param column    列名
     * @return 泛型T
     */
    T isNull(boolean condition, ColumnType column);

    /**
     * 不为空
     *
     * @param column 列名
     * @return 泛型T
     */
    T isNotNull(ColumnType column);

    /**
     * 不为空
     *
     * @param condition 条件
     * @param column    列名
     * @return 泛型T
     */
    T isNotNull(boolean condition, ColumnType column);

    /**
     * 在xxx之间
     *
     * @param column     列名
     * @param startValue 开始值
     * @param endValue   结束值
     * @return 泛型T
     */
    T between(ColumnType column, Object startValue, Object endValue);

    /**
     * 在xxx之间
     *
     * @param condition  条件
     * @param column     列名
     * @param startValue 开始值
     * @param endValue   结束值
     * @return 泛型T
     */
    T between(boolean condition, ColumnType column, Object startValue, Object endValue);

    /**
     * 不在xxx之间
     *
     * @param column     列名
     * @param startValue 开始值
     * @param endValue   结束值
     * @return 泛型T
     */
    T notBetween(ColumnType column, Object startValue, Object endValue);

    /**
     * 不在xxx之间
     *
     * @param condition  条件
     * @param column     列名
     * @param startValue 开始值
     * @param endValue   结束值
     * @return 泛型T
     */
    T notBetween(boolean condition, ColumnType column, Object startValue, Object endValue);

    /**
     * 存在
     *
     * @param select Select对象
     * @return 泛型T
     */
    T exists(PlainSelect select);

    /**
     * 存在
     *
     * @param condition 条件
     * @param select    Select对象
     * @return 泛型T
     */
    T exists(boolean condition, PlainSelect select);

    /**
     * 不存在
     *
     * @param select Select对象
     * @return 泛型T
     */
    T notExists(PlainSelect select);

    /**
     * 不存在
     *
     * @param condition 条件
     * @param select    Select对象
     * @return 泛型T
     */
    T notExists(boolean condition, PlainSelect select);

    /**
     * not
     *
     * @return 泛型T
     */
    T not();

    /**
     * join查询连接条件
     *
     * @param leftColumn  左侧列名
     * @param rightColumn 右侧列名
     * @return 泛型T
     */
    T joinColumn(String leftColumn, String rightColumn);

    /**
     * 可添加任何sql
     *
     * @param sql sql语句
     * @return T
     */
    T sql(String sql);

    /**
     * 可添加任何sql
     *
     * @param sql    sql语句
     * @param params 参数
     * @return T
     */
    T sql(String sql, Map<String, Object> params);

}
