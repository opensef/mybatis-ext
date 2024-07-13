package com.opensef.mybatisext.page.dialect;

import com.opensef.mybatisext.page.PageUtil;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.MetaObject;

import java.util.ArrayList;
import java.util.List;

public class OracleDialect implements Dialect {

    private static final String FIRST_PARAM_NAME = "_pageEndRow";
    private static final String SECOND_PARAM_NAME = "_pageStartRow";

    @Override
    public String toPageSql(String originalSql) {
        return "select * from (select pageTemp.*, rownum rn from (" +
                originalSql +
                ") pageTemp where ruwnum <= ?) where rn > ?";
    }

    @Override
    public void setSqlAndParams(MetaObject metaObject, MappedStatement mappedStatement, BoundSql boundSql, long pageNum, long pageSize) {
        List<ParameterMapping> newParameterMappings = new ArrayList<>(boundSql.getParameterMappings());
        newParameterMappings.add(new ParameterMapping.Builder(mappedStatement.getConfiguration(), FIRST_PARAM_NAME, Object.class).build());
        newParameterMappings.add(new ParameterMapping.Builder(mappedStatement.getConfiguration(), SECOND_PARAM_NAME, Object.class).build());

        long startRow = PageUtil.getStartRow(pageNum, pageSize);
        long endRow = pageSize > 0 ? startRow + pageSize : 0;

        boundSql.setAdditionalParameter(FIRST_PARAM_NAME, startRow);
        boundSql.setAdditionalParameter(SECOND_PARAM_NAME, endRow);
        metaObject.setValue("sql", toPageSql(boundSql.getSql()));
        metaObject.setValue("parameterMappings", newParameterMappings);
    }

}
