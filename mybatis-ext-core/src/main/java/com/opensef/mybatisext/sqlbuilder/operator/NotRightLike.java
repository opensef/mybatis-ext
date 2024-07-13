package com.opensef.mybatisext.sqlbuilder.operator;

import com.opensef.mybatisext.sqlbuilder.Params;
import com.opensef.mybatisext.sqlbuilder.PlainSelect;
import com.opensef.mybatisext.sqlbuilder.SqlOperatorUtil;

public class NotRightLike implements Operator {

    private final String column;

    private final Object value;

    public NotRightLike(String column, Object value) {
        this.column = column;
        this.value = value;
    }

    public NotRightLike(String column, PlainSelect select) {
        this.column = column;
        this.value = select;
    }

    @Override
    public String toSql(Params params) {
        if (value instanceof PlainSelect) {
            PlainSelect select = (PlainSelect) value;
            return SqlOperatorUtil.notRightLike(column, select, params.getParamMap());
        }
        return SqlOperatorUtil.notRightLike(column, value, params.getParamMap());
    }

}
