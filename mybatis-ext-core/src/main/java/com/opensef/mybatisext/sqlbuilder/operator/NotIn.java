package com.opensef.mybatisext.sqlbuilder.operator;

import com.opensef.mybatisext.sqlbuilder.Params;
import com.opensef.mybatisext.sqlbuilder.PlainSelect;
import com.opensef.mybatisext.sqlbuilder.SqlOperatorUtil;

import java.util.Collection;

public class NotIn implements Operator {

    private final String column;

    private final Object value;

    public NotIn(String column, Collection<?> value) {
        this.column = column;
        this.value = value;
    }

    public NotIn(String column, PlainSelect select) {
        this.column = column;
        this.value = select;
    }

    @Override
    public String toSql(Params params) {
        if (value instanceof PlainSelect) {
            PlainSelect select = (PlainSelect) value;
            return SqlOperatorUtil.notIn(column, select, params.getParamMap());
        }
        return SqlOperatorUtil.notIn(column, (Collection<?>) value, params.getParamMap());
    }

}
