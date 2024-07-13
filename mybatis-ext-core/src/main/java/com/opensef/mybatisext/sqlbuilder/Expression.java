package com.opensef.mybatisext.sqlbuilder;


import com.opensef.mybatisext.sqlbuilder.operator.Operator;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 表达式
 */
public class Expression extends AbstractExpression<String, Expression, String> {

    public Expression and(Operator... operators) {
        return analysisExpression(Arrays.stream(operators).filter(Objects::nonNull).collect(Collectors.toList()), SqlLogicOperator.AND);
    }

    public Expression and(Expression expression) {
        if (sql.length() > 0) {
            sql.append(SqlLogicOperator.AND.getCode());
        }
        sql.append("(");
        sql.append(expression.toSql());
        sql.append(")");
        params.addParams(expression.getParams().getParamMap());
        return this;
    }

    public Expression and(Consumer<Expression> consumer) {
        Expression expression = new Expression();
        consumer.accept(expression);
        return and(expression);
    }

    public Expression or(Consumer<Expression> consumer) {
        Expression expression = new Expression();
        consumer.accept(expression);
        return or(expression);
    }

    public Expression or(Operator... operators) {
        return analysisExpression(Arrays.stream(operators).filter(Objects::nonNull).collect(Collectors.toList()), SqlLogicOperator.OR);
    }

    public Expression or(Expression expression) {
        if (sql.length() > 0) {
            sql.append(SqlLogicOperator.OR.getCode());
        }
        sql.append("(");
        sql.append(expression.toSql());
        sql.append(")");
        params.addParams(expression.getParams().getParamMap());
        return this;
    }

}
