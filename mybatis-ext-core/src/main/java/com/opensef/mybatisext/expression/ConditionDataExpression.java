package com.opensef.mybatisext.expression;

/**
 * 表达式，需要根据字段名、条件、值组装成sql
 */
public class ConditionDataExpression implements DataExpression {

    /**
     * 字段名
     */
    private String tableColumn;

    /**
     * 条件
     */
    private Condition condition;

    /**
     * 值，带参数的值或具体的值，例如：${userId}或5000
     */
    private String value;

    public String getTableColumn() {
        return tableColumn;
    }

    public void setTableColumn(String tableColumn) {
        this.tableColumn = tableColumn;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String accept(DataExpressionVisitor visitor) {
        return visitor.visit(this);
    }

}
