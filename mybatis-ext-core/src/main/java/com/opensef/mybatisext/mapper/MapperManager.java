package com.opensef.mybatisext.mapper;

import com.opensef.mybatisext.mapper.builder.MapperSql;
import com.opensef.mybatisext.mapper.builder.MapperSqlBuilder;
import com.opensef.mybatisext.mapper.builder.MapperSqlBuilderFactory;
import org.apache.ibatis.builder.annotation.ProviderContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通用Mapper管理器
 */
public class MapperManager {

    public static final MapperSqlBuilderFactory MAPPER_SQL_BUILDER_FACTORY = new MapperSqlBuilderFactory();

    /**
     * sql缓存
     */
    private static final Map<String, MapperSql> CACHE_SQL = new ConcurrentHashMap<>(1024);

    /**
     * 获取sql
     * 先从缓存获取，缓存中不存在则使用工厂里的sql构建器生成
     *
     * @param providerContext ProviderContext
     * @param method          SqlProvider中的方法名称
     * @return sql
     */
    public static String getSqlUseCache(ProviderContext providerContext, String method) {
        String cacheKey = getCacheKey(providerContext);
        MapperSql mapperSql = CACHE_SQL.get(cacheKey);
        if (null != mapperSql) {
            return mapperSql.getSql();
        }
        mapperSql = MAPPER_SQL_BUILDER_FACTORY.build(providerContext, method);
        CACHE_SQL.put(cacheKey, mapperSql);
        return mapperSql.getSql();
    }

    /**
     * 获取sql
     * 直接使用工厂里的sql构建器生成
     * 用于不能缓存sql的场景，例如：通过查询对象动态构建查询语句
     *
     * @param providerContext ProviderContext
     * @param method          SqlProvider中的方法名称
     * @return sql
     */
    public static String getSql(ProviderContext providerContext, String method, Object... args) {
        return MAPPER_SQL_BUILDER_FACTORY.build(providerContext, method, args).getSql();
    }

    /**
     * 注册自定义sql构建器
     *
     * @param methodName       SqlProvider中的方法名称
     * @param mapperSqlBuilder sql构建器
     */
    public static void registerSqlBuilder(String methodName, MapperSqlBuilder mapperSqlBuilder) {
        MAPPER_SQL_BUILDER_FACTORY.register(methodName, mapperSqlBuilder);
    }

    /**
     * 生成缓存key
     *
     * @param providerContext ProviderContext
     * @return 缓存key
     */
    private static String getCacheKey(ProviderContext providerContext) {
        return providerContext.getMapperType().getName() + "." + providerContext.getMapperMethod().getName();
    }

}
