package com.opensef.mybatisext.sqlbuilder.operator;

import com.opensef.mybatisext.sqlbuilder.Params;
import com.opensef.mybatisext.sqlbuilder.PlainSelect;
import com.opensef.mybatisext.sqlbuilder.SqlOperatorUtil;

public class Gt implements Operator {

    private final String column;

    private final Object value;

    public Gt(String column, Object value) {
        this.column = column;
        this.value = value;
    }

    public Gt(String column, PlainSelect select) {
        this.column = column;
        this.value = select;
    }

    @Override
    public String toSql(Params params) {
        if (value instanceof PlainSelect) {
            PlainSelect select = (PlainSelect) value;
            return SqlOperatorUtil.gt(column, select, params.getParamMap());
        }
        return SqlOperatorUtil.gt(column, value, params.getParamMap());
    }

}
