package com.opensef.mybatisext.sqlbuilder.operator;

import com.opensef.mybatisext.sqlbuilder.Params;
import com.opensef.mybatisext.sqlbuilder.PlainSelect;
import com.opensef.mybatisext.sqlbuilder.SqlOperatorUtil;

public class NotExists implements Operator {

    private final PlainSelect select;

    public NotExists(PlainSelect select) {
        this.select = select;
    }

    @Override
    public String toSql(Params params) {
        return SqlOperatorUtil.notExists(select, params.getParamMap());
    }

}
