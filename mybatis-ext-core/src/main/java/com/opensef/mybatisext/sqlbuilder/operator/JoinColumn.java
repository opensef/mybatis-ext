package com.opensef.mybatisext.sqlbuilder.operator;

import com.opensef.mybatisext.sqlbuilder.Params;
import com.opensef.mybatisext.sqlbuilder.SqlOperatorUtil;

public class JoinColumn implements Operator {

    private final String leftColumn;

    private final String rightColumn;

    public JoinColumn(String leftColumn, String rightColumn) {
        this.leftColumn = leftColumn;
        this.rightColumn = rightColumn;
    }

    @Override
    public String toSql(Params params) {
        return SqlOperatorUtil.joinColumn(leftColumn, rightColumn);
    }

}
