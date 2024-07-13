package com.opensef.mybatisext.sqlbuilder;


import com.opensef.mybatisext.mapper.SerializableFunction;
import com.opensef.mybatisext.sqlbuilder.operator.JoinColumn;
import com.opensef.mybatisext.sqlbuilder.operator.Operator;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Lambda形式的表达式
 *
 * @param <T> 实体类型
 */
public class LambdaExpression<T> extends AbstractExpression<T, LambdaExpression<T>, SerializableFunction<T, ?>> implements IExpression {

    public LambdaExpression() {
    }

    public LambdaExpression(Params params) {
        super(params);
    }

    /**
     * and
     *
     * @param operators 运算符
     * @return 表达式
     * @apiNote 例：Op.eq("username", "test")
     */
    public LambdaExpression<T> and(Operator... operators) {
        return analysisExpression(Arrays.stream(operators).filter(Objects::nonNull).collect(Collectors.toList()), SqlLogicOperator.AND);
    }

    /**
     * and
     *
     * @param expression 表达式
     * @return 表达式
     */
    private LambdaExpression<T> and(LambdaExpression<T> expression) {
        if (sql.length() > 0) {
            sql.append(SqlLogicOperator.AND.getCode());
        }
        sql.append("(");
        sql.append(expression.toSql());
        sql.append(")");
        params.addParams(expression.getParams().getParamMap());
        return this;
    }

    /**
     * and
     *
     * @param consumer 函数式表达式
     * @return 表达式
     */
    public LambdaExpression<T> and(Consumer<LambdaExpression<T>> consumer) {
        LambdaExpression<T> expression = new LambdaExpression<>(params);
        consumer.accept(expression);
        return and(expression);
    }

    /**
     * or
     *
     * @param operators 运算符
     * @return 表达式
     */
    public LambdaExpression<T> or(Operator... operators) {
        return analysisExpression(Arrays.stream(operators).filter(Objects::nonNull).collect(Collectors.toList()), SqlLogicOperator.OR);
    }

    /**
     * or
     *
     * @param expression 表达式
     * @return 表达式
     */
    private LambdaExpression<T> or(LambdaExpression<T> expression) {
        if (sql.length() > 0) {
            sql.append(SqlLogicOperator.OR.getCode());
        }
        sql.append("(");
        sql.append(expression.toSql());
        sql.append(")");
        params.addParams(expression.getParams().getParamMap());
        return this;
    }

    /**
     * or
     *
     * @param consumer 函数式表达式
     * @return 表达式
     */
    public LambdaExpression<T> or(Consumer<LambdaExpression<T>> consumer) {
        LambdaExpression<T> expression = new LambdaExpression<>(params);
        consumer.accept(expression);
        return or(expression);
    }

    /**
     * 连接符
     *
     * @param leftColumn  左侧列名
     * @param rightColumn 右侧列名
     * @return 表达式
     * @apiNote 用于join查询，连接on条件。 例：user.role_id = role.id
     */
    public LambdaExpression<T> joinColumn(String leftColumn, String rightColumn) {
        sql.append(new JoinColumn(leftColumn, rightColumn).toSql(params));
        return this;
    }

}
