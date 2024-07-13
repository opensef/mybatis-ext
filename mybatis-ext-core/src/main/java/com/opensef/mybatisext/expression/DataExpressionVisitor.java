package com.opensef.mybatisext.expression;

public interface DataExpressionVisitor {

    String visit(ConditionDataExpression conditionDataExpression);

    String visit(SqlDataExpression sqlDataScopeExpression);

}
