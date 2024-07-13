package com.opensef.mybatisext.sqlbuilder.operator;

import com.opensef.mybatisext.sqlbuilder.Params;
import com.opensef.mybatisext.sqlbuilder.PlainSelect;
import com.opensef.mybatisext.sqlbuilder.SqlOperatorUtil;

import java.util.Collection;

public class In implements Operator {

    private final String column;

    private final Object value;

    public In(String column, Collection<?> value) {
        this.column = column;
        this.value = value;
    }

    public In(String column, PlainSelect select) {
        this.column = column;
        this.value = select;
    }

    @Override
    public String toSql(Params params) {
        if (value instanceof PlainSelect) {
            PlainSelect select = (PlainSelect) value;
            return SqlOperatorUtil.in(column, select, params.getParamMap());
        }
        return SqlOperatorUtil.in(column, (Collection<?>) value, params.getParamMap());
    }

}
