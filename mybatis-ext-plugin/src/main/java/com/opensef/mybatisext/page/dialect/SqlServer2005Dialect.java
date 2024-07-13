package com.opensef.mybatisext.page.dialect;

import com.opensef.mybatisext.exception.MybatisExtException;
import com.opensef.mybatisext.parser.SqlParserEngine;
import com.opensef.mybatisext.util.ExtStringUtil;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.MetaObject;

import java.util.ArrayList;
import java.util.List;

public class SqlServer2005Dialect implements Dialect {

    private static final String FIRST_PARAM_NAME = "_pageStartRow";
    private static final String SECOND_PARAM_NAME = "_pageEndRow";

    @Override
    public String toPageSql(String originalSql) {
        String originalSqlStr = originalSql.toLowerCase();
        int insertIndex = 0;
        if (originalSqlStr.startsWith("select")) {
            insertIndex = 6;
            if (originalSqlStr.startsWith("select distinct")) {
                insertIndex = 15;
            }
        }

        String orderBySql = getOrderBySql(originalSql);
        if (ExtStringUtil.hasLength(orderBySql)) {
            orderBySql = "order by current_timestamp";
        }

        StringBuilder pageSql = new StringBuilder(originalSql);
        pageSql.append("select _pageTemp.row_number_page_temp, * from (");
        pageSql.insert(insertIndex, "ROW_NUMBER() over(" + orderBySql + ") as row_number_page_temp, ");
        pageSql.append(") _pageTemp where _pageTemp.row_number_page_temp between ? and ?");
        return pageSql.toString();
    }

    @Override
    public void setSqlAndParams(MetaObject metaObject, MappedStatement mappedStatement, BoundSql boundSql, long pageNum, long pageSize) {
        List<ParameterMapping> newParameterMappings = new ArrayList<>(boundSql.getParameterMappings());
        newParameterMappings.add(new ParameterMapping.Builder(mappedStatement.getConfiguration(), FIRST_PARAM_NAME, Object.class).build());
        newParameterMappings.add(new ParameterMapping.Builder(mappedStatement.getConfiguration(), SECOND_PARAM_NAME, Object.class).build());

        boundSql.setAdditionalParameter(FIRST_PARAM_NAME, getStartRow(pageNum, pageSize));
        boundSql.setAdditionalParameter(SECOND_PARAM_NAME, getEndRow(pageNum, pageSize));
        metaObject.setValue("sql", toPageSql(boundSql.getSql()));
        metaObject.setValue("parameterMappings", newParameterMappings);
    }

    /**
     * 从原始字符串中获取最后一个order by sql
     *
     * @param originalSql 原始sql
     * @return order by sql
     */
    private String getOrderBySql(String originalSql) {
        try {
            Statement statement = SqlParserEngine.getStatement(originalSql);
            if (statement instanceof Select) {
                Select select = (Select) statement;
                SelectBody selectBody = select.getSelectBody();
                List<OrderByElement> orderBy = SqlParserEngine.getOrderBy(selectBody);
                return PlainSelect.orderByToString(orderBy);
            }
        } catch (JSQLParserException e) {
            throw new MybatisExtException("解析sql出错");
        }

        return null;
    }

    private Long getStartRow(long pageNum, long pageSize) {
        // (pageNum - 1) * pageSize + 1
        return pageNum > 0 ? (pageNum - 1) * pageSize + 1 : 0;
    }

    private Long getEndRow(long pageNum, long pageSize) {
        // pageNum * pageSize
        return pageNum > 0 ? (pageNum * pageSize) : 0;
    }

}
