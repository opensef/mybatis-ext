package com.opensef.mybatisext.sqlbuilder;


import com.opensef.mybatisext.mapper.FunctionReflectionUtil;
import com.opensef.mybatisext.mapper.SerializableFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 查询条件
 *
 * @param <T> 实体类型
 */
public class LambdaQuery<T> extends LambdaExpression<T> {

    private final StringBuilder orderBySql = new StringBuilder();

    private final List<String> columns = new ArrayList<>();

    @SafeVarargs
    public final LambdaQuery<T> columns(SerializableFunction<T, ?>... columns) {
        List<String> columnList = Arrays.stream(columns).map(FunctionReflectionUtil::getTableColumnName).collect(Collectors.toList());
        this.columns.addAll(columnList);
        return this;
    }

    /**
     * 全部列正序
     *
     * @param columns 列名
     * @return this
     */
    @SafeVarargs
    public final LambdaQuery<T> orderBy(SerializableFunction<T, ?>... columns) {
        List<String> columnList = Arrays.stream(columns).map(FunctionReflectionUtil::getTableColumnName).collect(Collectors.toList());
        orderBySql.append(" ORDER BY ").append(String.join(",", columnList));
        return this;
    }

    /**
     * 全部列倒序
     *
     * @param columns 列名
     * @return this
     */
    @SafeVarargs
    public final LambdaQuery<T> orderByDesc(SerializableFunction<T, ?>... columns) {
        List<String> columnList = Arrays.stream(columns).map(column -> FunctionReflectionUtil.getTableColumnName(column) + " DESC").collect(Collectors.toList());
        orderBySql.append(" ORDER BY ").append(String.join(",", columnList));
        return this;
    }

    /**
     * 函数式排序（正序/倒序）
     *
     * @return thsi
     */
    public final LambdaQuery<T> orderBy(Consumer<OrderBy<T>> consumer) {
        orderBySql.append(" ORDER BY ");
        OrderBy<T> orderBy = new OrderBy<>();
        consumer.accept(orderBy);
        orderBySql.append(orderBy.toSql());
        return this;
    }

    public String toOrderBySql() {
        return orderBySql.toString();
    }

    public List<String> getColumns() {
        return this.columns;
    }

    public static class OrderBy<T> {

        private final StringBuilder sql = new StringBuilder();

        public OrderBy<T> asc(SerializableFunction<T, ?> column) {
            if (sql.length() > 0) {
                sql.append(", ");
            }
            sql.append(FunctionReflectionUtil.getTableColumnName(column));
            return this;
        }

        public OrderBy<T> desc(SerializableFunction<T, ?> column) {
            if (sql.length() > 0) {
                sql.append(", ");
            }
            sql.append(FunctionReflectionUtil.getTableColumnName(column)).append(" DESC");
            return this;
        }

        private String toSql() {
            return sql.toString();
        }

    }

    @Override
    public String toSql() {
        return super.toSql() + " " + orderBySql;
    }

    /**
     * 生成查询sql，不包含order by
     *
     * @return 生成查询sql
     */
    public String toQuerySql() {
        return super.toSql();
    }

}
