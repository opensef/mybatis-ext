package com.opensef.mybatisext.datascope;

import com.opensef.mybatisext.expression.DataExpression;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 数据权限控制
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class DataScopePlugin implements Interceptor {

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
            if (sql.contains(";")) {
                // 批量处理添加数据权限
                addBatchDataScope(metaObject, mappedStatement, boundSql);
            } else {
                // 数据权限
                addDataScope(metaObject, mappedStatement, boundSql);
            }
        } else if (sqlCommandType.equals(SqlCommandType.SELECT)) {
            // 数据权限
            addDataScope(metaObject, mappedStatement, boundSql);
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

    // 添加数据权限
    private void addDataScope(MetaObject metaObject, MappedStatement mappedStatement, BoundSql boundSql) throws JSQLParserException {
        DataScopeInfo dataScopeInfo = DataScopeUtil.get();
        // 如果没有数据权限，则不再向下执行
        if (dataScopeInfo == null || dataScopeInfo.getDataScopeExpressions() == null || dataScopeInfo.getDataScopeExpressions().isEmpty()) {
            return;
        }

        String newSql = genExpressionSql(dataScopeInfo.getDataScopeExpressions(), SqlParserEngine.getStatement(boundSql.getSql()));

        // 设置最终参数信息
        MybatisExtUtil.setParameterAndGenSqlForExpression(metaObject, mappedStatement, boundSql, newSql, dataScopeInfo.getParamValueMap());
    }

    // 批量添加数据权限
    private void addBatchDataScope(MetaObject metaObject, MappedStatement mappedStatement, BoundSql boundSql) throws JSQLParserException {
        DataScopeInfo dataScopeInfo = DataScopeUtil.get();
        // 如果没有数据权限，则不再向下执行
        if (dataScopeInfo == null || dataScopeInfo.getDataScopeExpressions() == null || dataScopeInfo.getDataScopeExpressions().isEmpty()) {
            return;
        }
        String oldSql = boundSql.getSql();
        List<String> newSqlList = new ArrayList<>();

        Statements statements = CCJSqlParserUtil.parseStatements(oldSql);

        for (Statement statement : statements.getStatements()) {
            String newSql = genExpressionSql(dataScopeInfo.getDataScopeExpressions(), statement);
            newSqlList.add(newSql);
        }


        String newBatchSql = String.join(";", newSqlList);

        // 设置最终参数信息
        MybatisExtUtil.setParameterAndGenSqlForExpression(metaObject, mappedStatement, boundSql, newBatchSql, dataScopeInfo.getParamValueMap());
    }

    /**
     * 解析旧sql，生成添加表达式后的新sql，此时还不是可执行的sql，还需做进一步处理（参考setParameterAndGenSql方法）
     * <p>例如：select * from sys_user where org_id in (#orgIds)</p>
     *
     * @param dataExpressions 数据权限表达式
     * @param statement       原始sql对应的statement
     * @return 新sql语句
     */
    private String genExpressionSql(List<DataExpression> dataExpressions, Statement statement) {
        String newSql = statement.toString();
        for (DataExpression dataExpression : dataExpressions) {
            newSql = dataExpression.accept(new DataExpressionParser(statement));
        }
        return newSql;
    }

}
