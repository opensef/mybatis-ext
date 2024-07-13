package com.opensef.mybatisext.expression;

/**
 * 自定义sql，直接使用，无需组装
 */
public class SqlDataExpression implements DataExpression {

    /**
     * sql，例如：可以查看自己的单据和合同额小于5万元的单据（amount <= 50000 or created_user_id = #userId）
     */
    private String sql;


    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }


    @Override
    public String accept(DataExpressionVisitor visitor) {
        return visitor.visit(this);
    }

}
