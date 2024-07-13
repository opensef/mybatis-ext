package com.opensef.mybatisext.parser;

import com.opensef.mybatisext.expression.ConditionDataExpression;
import com.opensef.mybatisext.expression.DataExpressionVisitor;
import com.opensef.mybatisext.expression.SqlDataExpression;
import net.sf.jsqlparser.statement.Statement;

public class DataExpressionParser implements DataExpressionVisitor {

    // 解析sql后的Statement对象
    private final Statement statement;

    public DataExpressionParser(Statement statement) {
        this.statement = statement;
    }

    @Override
    public String visit(ConditionDataExpression conditionDataExpression) {
        return SqlParserEngine.parse(statement, conditionDataExpression.getCondition(), conditionDataExpression.getTableColumn(), conditionDataExpression.getValue());
    }

    @Override
    public String visit(SqlDataExpression sqlDataScopeExpression) {
        return SqlParserEngine.parse(statement, sqlDataScopeExpression.getSql());
    }

}
