package com.opensef.mybatisext.page;

import com.opensef.mybatisext.Page;
import com.opensef.mybatisext.PageRequest;
import com.opensef.mybatisext.exception.MybatisExtException;
import com.opensef.mybatisext.page.dialect.Dialect;
import com.opensef.mybatisext.page.dialect.DialectFactory;
import com.opensef.mybatisext.util.MybatisExtUtil;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分页插件
 */
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
})
public class PagePlugin implements Interceptor {

    // count查询MappedStatement缓存，key:MappedStatement的id，value:MappedStatement
    private static final Map<String, MappedStatement> countMappedStatementMap = new ConcurrentHashMap<>();

    @SuppressWarnings({"rawtypes"})
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        // 获取参数
        Object parameter = invocation.getArgs()[1];
        RowBounds rowBounds = (RowBounds) invocation.getArgs()[2];
        ResultHandler resultHandler = (ResultHandler) invocation.getArgs()[3];
        Executor executor = (Executor) invocation.getTarget();

        CacheKey cacheKey;
        BoundSql boundSql;
        if (invocation.getArgs().length == 4) {
            boundSql = mappedStatement.getBoundSql(parameter);
            cacheKey = executor.createCacheKey(mappedStatement, parameter, rowBounds, boundSql);
        } else {
            cacheKey = (CacheKey) invocation.getArgs()[4];
            boundSql = (BoundSql) invocation.getArgs()[5];
        }

        // 如果不分页，直接向下执行
        if (!isPage(mappedStatement)) {
            return invocation.proceed();
        }

        /*-------------------------开始执行分页-------------------------*/
        PageRequest pageRequest = getPageRequest(boundSql);
        MetaObject metaObject = SystemMetaObject.forObject(boundSql);

        // 分页查询可以返回Page对象，也可以返回集合对象。如果返回值不是Page，则不需要执行count查询
        Class<?> returnType = getReturnType(mappedStatement);

        if (!returnType.getName().equals(Page.class.getName())) {
            return pageQuery(executor, mappedStatement, parameter, rowBounds, resultHandler, boundSql, metaObject, cacheKey, pageRequest);
        }

        // 生成count sql并执行count查询
        Long total = count(executor, mappedStatement, parameter, rowBounds, resultHandler, boundSql, metaObject);
        if (total == 0) {
            PageList pageList = new PageList(pageRequest.getPageNum(), pageRequest.getPageSize());
            pageList.setTotal(0);
            pageList.setPages(0);
            return pageList;
        }

        // 执行分页查询
        List<Object> list = pageQuery(executor, mappedStatement, parameter, rowBounds, resultHandler, boundSql, metaObject, cacheKey, pageRequest);

        PageList pageList = new PageList(pageRequest.getPageNum(), pageRequest.getPageSize());
        pageList.setTotal(total);
        pageList.setPages(PageUtil.getTotalPage(pageRequest.getPageSize(), total));
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

    // 返回值类型Map集合 key:MappedStatement的id，value:返回值类型
    private final Map<String, Class<?>> RETURN_TYPE_MAP = new ConcurrentHashMap<>();

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


    private Class<?> getReturnType(MappedStatement mappedStatement) {
        Class<?> returnType = RETURN_TYPE_MAP.get(mappedStatement.getId());
        if (null != returnType) {
            return returnType;
        }
        Method currentMethod = MybatisExtUtil.getCurrentMethod(mappedStatement);
        returnType = currentMethod.getReturnType();
        RETURN_TYPE_MAP.put(mappedStatement.getId(), currentMethod.getReturnType());
        return returnType;
    }

    @SuppressWarnings("unchecked")
    private PageRequest getPageRequest(BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        if (parameterObject instanceof PageRequest) {
            return (PageRequest) parameterObject;
        } else if (parameterObject instanceof Map) {
            Map<String, Object> paramMap = (Map<String, Object>) parameterObject;
            for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                if (entry.getValue() instanceof PageRequest) {
                    return (PageRequest) entry.getValue();
                }
            }
        }
        throw new MybatisExtException("无法获取分页参数信息");
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Long count(Executor executor, MappedStatement mappedStatement, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql, MetaObject metaObject) throws SQLException {
        // 如果pageSize等于0，则直接返回空集合
        String countSql = "SELECT count(*) FROM (" + boundSql.getSql() + ") _t_count";
        BoundSql countBoundSql = new BoundSql(mappedStatement.getConfiguration(), countSql, boundSql.getParameterMappings(), parameter);
        // 原BoundSql中的附加参数添加到count查询的BoundSql中
        Map<String, Object> additionalParameters = (Map<String, Object>) metaObject.getValue("additionalParameters");
        additionalParameters.forEach(countBoundSql::setAdditionalParameter);

        String countMappedStatementId = mappedStatement.getId() + "_count";
        MappedStatement countMappedStatement = countMappedStatementMap.get(countMappedStatementId);
        if (null == countMappedStatement) {
            countMappedStatement = makeCountMappedStatement(mappedStatement, countMappedStatementId);
            countMappedStatementMap.put(countMappedStatementId, countMappedStatement);
        }
        // 创建count查询的缓存key
        CacheKey countCacheKey = executor.createCacheKey(countMappedStatement, parameter, RowBounds.DEFAULT, boundSql);
        List<Object> countResult = executor.query(countMappedStatement, parameter, rowBounds, resultHandler, countCacheKey, countBoundSql);
        long total = 0;
        if (null != countResult && countResult.size() > 0) {
            if (null != countResult.get(0)) {
                total = (long) countResult.get(0);
            }
        }
        return total;
    }

    private List<Object> pageQuery(Executor executor, MappedStatement mappedStatement, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql, MetaObject metaObject, CacheKey cacheKey, PageRequest pageRequest) throws SQLException {
        DataSource dataSource = mappedStatement.getConfiguration().getEnvironment().getDataSource();
        Dialect dialect = DialectFactory.get(dataSource);
        dialect.setSqlAndParams(metaObject, mappedStatement, boundSql, pageRequest.getPageNum(), pageRequest.getPageSize());

        // 执行分页查询
        return executor.query(mappedStatement, parameter, rowBounds, resultHandler, cacheKey, boundSql);
    }

    private MappedStatement makeCountMappedStatement(MappedStatement mappedStatement, String newMappedStatementId) {
        MappedStatement.Builder builder = new MappedStatement.Builder(mappedStatement.getConfiguration(), newMappedStatementId,
                mappedStatement.getSqlSource(), mappedStatement.getSqlCommandType());
        builder.keyGenerator(mappedStatement.getKeyGenerator());
        builder.resource(mappedStatement.getResource());
        builder.parameterMap(mappedStatement.getParameterMap());
        // 返回Long类型数据，select_count为自定义的ResultMap的id，可随意指定
        builder.resultMaps(Collections.singletonList(new ResultMap.Builder(mappedStatement.getConfiguration(), "select_count", Long.class, Collections.emptyList()).build()));
        builder.resultOrdered(mappedStatement.isResultOrdered());
        builder.resultSetType(mappedStatement.getResultSetType());
        builder.timeout(mappedStatement.getTimeout());
        builder.statementType(mappedStatement.getStatementType());
        builder.useCache(mappedStatement.isUseCache());
        builder.cache(mappedStatement.getCache());
        builder.databaseId(mappedStatement.getDatabaseId());
        builder.fetchSize(mappedStatement.getFetchSize());
        builder.flushCacheRequired(mappedStatement.isFlushCacheRequired());
        return builder.build();
    }

}
