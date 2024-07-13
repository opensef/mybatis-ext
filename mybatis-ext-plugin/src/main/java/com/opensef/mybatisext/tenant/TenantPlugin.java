package com.opensef.mybatisext.tenant;

import com.opensef.mybatisext.parser.DataExpressionParser;
import com.opensef.mybatisext.parser.SqlParserEngine;
import com.opensef.mybatisext.util.MybatisExtUtil;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.Connection;
import java.util.*;

/**
 * 多租户
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class TenantPlugin implements Interceptor {

    private final TenantHandler<?> tenant;

    public TenantPlugin(TenantHandler<?> tenant) {
        this.tenant = tenant;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = MybatisExtUtil.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);

        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");


        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();

        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");

        if (sqlCommandType.equals(SqlCommandType.INSERT)) {
            // 如果是保存操作，直接返回向下执行
            return invocation.proceed();
        } else if (sqlCommandType.equals(SqlCommandType.UPDATE) || sqlCommandType.equals(SqlCommandType.DELETE)) {
            String sql = boundSql.getSql();
            // 包含分号，说明是批量处理sql
            if (sql.contains(";")) {
                // 批量处理添加租户
                addBatchTenant(metaObject, mappedStatement, boundSql);
            } else {
                // 租户
                addTenant(metaObject, mappedStatement, boundSql);
            }
        } else if (sqlCommandType.equals(SqlCommandType.SELECT)) {
            // 租户
            addTenant(metaObject, mappedStatement, boundSql);
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    // 添加租户
    private void addTenant(MetaObject metaObject, MappedStatement mappedStatement, BoundSql boundSql) throws JSQLParserException {
        if (null == tenant || tenant.getTenantColumnName() == null || tenant.getTenantColumnName().trim().isEmpty()) {
            return;
        }
        if (tenant.isNeedFilter(mappedStatement.getId())) {
            return;
        }
        // 解析sql后的Statement对象
        Statement statement = SqlParserEngine.getStatement(boundSql.getSql());
        String newSql = tenant.getExpression().accept(new DataExpressionParser(statement));
        // 设置最终参数信息
        MybatisExtUtil.setParameterAndGenSqlForExpression(metaObject, mappedStatement, boundSql, newSql, getParamValueMap());
    }

    // 批量修改添加租户
    private void addBatchTenant(MetaObject metaObject, MappedStatement mappedStatement, BoundSql boundSql) throws JSQLParserException {
        if (null == tenant || tenant.getTenantColumnName() == null || tenant.getTenantColumnName().trim().isEmpty()) {
            return;
        }
        if (tenant.isNeedFilter(mappedStatement.getId())) {
            return;
        }

        String oldSql = boundSql.getSql();
        List<String> newSqlList = new ArrayList<>();

        Statements statements = CCJSqlParserUtil.parseStatements(oldSql);
        for (Statement statement : statements.getStatements()) {
            String newSql = tenant.getExpression().accept(new DataExpressionParser(statement));
            newSqlList.add(newSql);
        }

        String newBatchSql = String.join(";", newSqlList);

        // 设置最终参数信息
        MybatisExtUtil.setParameterAndGenSqlForExpression(metaObject, mappedStatement, boundSql, newBatchSql, getParamValueMap());
    }

    /**
     * 获取参数-值
     *
     * @return 参数-值
     */
    public Map<String, List<?>> getParamValueMap() {
        Map<String, List<?>> paramValue = new HashMap<>();
        paramValue.put(tenant.getTenantColumnName(), List.of(tenant.getTenantColumnValue()));
        return paramValue;
    }

}
