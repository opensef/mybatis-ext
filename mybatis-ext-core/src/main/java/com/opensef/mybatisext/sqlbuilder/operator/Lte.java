package com.opensef.mybatisext.sqlbuilder.operator;

import com.opensef.mybatisext.sqlbuilder.Params;
import com.opensef.mybatisext.sqlbuilder.PlainSelect;
import com.opensef.mybatisext.sqlbuilder.SqlOperatorUtil;

public class Lte implements Operator {

    private final String column;

    private final Object value;

    public Lte(String column, Object value) {
        this.column = column;
        this.value = value;
    }

    public Lte(String column, PlainSelect select) {
        this.column = column;
        this.value = select;
    }

    @Override
    public String toSql(Params params) {
        if (value instanceof PlainSelect) {
            PlainSelect select = (PlainSelect) value;
            return SqlOperatorUtil.lte(column, select, params.getParamMap());
        }
        return SqlOperatorUtil.lte(column, value, params.getParamMap());
    }

}
