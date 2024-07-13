package com.opensef.mybatisext.page;

import com.opensef.mybatisext.PageRequest;
import com.opensef.mybatisext.util.MybatisExtUtil;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分页插件
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class PagePlugin2 implements Interceptor {

    @SuppressWarnings("unchecked")
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = MybatisExtUtil.realTarget(invocation.getTarget());

        // Object parameterObject = statementHandler.getParameterHandler().getParameterObject();
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);

        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        // 如果不分页，直接向下执行
        if (!isPage(mappedStatement)) {
            return invocation.proceed();
        }

        /*-------------------------开始执行分页-------------------------*/
        // Map类型参数
        Map<String, Object> paramMap;
        Object parameterObject = metaObject.getValue("delegate.boundSql.parameterObject");
        // 如果是一个PageRequest对象，则将其转为Map
        if (parameterObject instanceof PageRequest) {
            // BoundSql boundSql22 = (BoundSql) metaObject.getValue("delegate.boundSql");
            // boundSql22.setAdditionalParameter(MybatisExtUtil.pageRequestParam, parameterObject);
            Map<String, Object> map = new HashMap<>();
            map.put(MybatisExtUtil.pageRequestParam, parameterObject);
            metaObject.setValue("delegate.boundSql.parameterObject", map);
            paramMap = map;
        } else {
            paramMap = (Map<String, Object>) parameterObject;
        }

        // 如果pageSize等于0，则直接返回空集合

        // 生成count sql


        // 执行count查询

        // 生成分页sql

        // 执行分页查询

        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");

        //------------
        List<ParameterMapping> newParameterMappings = new ArrayList<>(boundSql.getParameterMappings());
        newParameterMappings.add(new ParameterMapping.Builder(mappedStatement.getConfiguration(), "bb.b1", Object.class).build());
        // boundSql.setAdditionalParameter("limit", 22);
        // boundSql.setAdditionalParameter("_page._pageNum", 101);
        metaObject.setValue("delegate.boundSql.parameterMappings", newParameterMappings);
        metaObject.setValue("delegate.boundSql.sql", "select * from sys_user limit ?");
        Map<String, Object> additionalParameters = (Map<String, Object>) metaObject.getValue("delegate.boundSql.additionalParameters");
        additionalParameters.put("aa", 1);
        additionalParameters.put("bb.b1", 19);

        boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        //------------


        Object parameterObject1 = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();

        DataSource dataSource = mappedStatement.getConfiguration().getEnvironment().getDataSource();
        Object[] args = invocation.getArgs();


        List<Object> list = (List<Object>) invocation.proceed();

        PageList pageList = new PageList(1, 10);
        pageList.setPageNum(1);
        pageList.setPageSize(10);
        pageList.setTotal(100);
        pageList.setPages(20);
        pageList.addAll(list);

        return pageList;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    // 是否分页Map集合 key:MappedStatement的id，value:true/false
    private final Map<String, Boolean> IS_PAGE_MAP = new ConcurrentHashMap<>();

    private boolean isPage(MappedStatement mappedStatement) {
        Boolean isPage = IS_PAGE_MAP.get(mappedStatement.getId());
        if (null != isPage) {
            return isPage;
        }

        isPage = false;
        Method currentMethod = MybatisExtUtil.getCurrentMethod(mappedStatement);
        for (Class<?> parameterType : currentMethod.getParameterTypes()) {
            if (parameterType.equals(PageRequest.class)) {
                isPage = true;
                break;
            }
        }
        IS_PAGE_MAP.put(mappedStatement.getId(), isPage);
        return isPage;
    }

    @SuppressWarnings("unchecked")
    private PageRequest getPageRequest(BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        if (parameterObject instanceof PageRequest) {
            return (PageRequest) parameterObject;
        } else if (parameterObject instanceof Map) {
            Map<String, Object> paramMap = (Map<String, Object>) parameterObject;
            return (PageRequest) paramMap.get(MybatisExtUtil.pageRequestParam);
        }
        return null;
    }

}
