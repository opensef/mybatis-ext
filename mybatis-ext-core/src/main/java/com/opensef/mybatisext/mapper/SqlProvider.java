package com.opensef.mybatisext.mapper;

import com.opensef.mybatisext.mapper.builder.SqlProviderMethod;
import com.opensef.mybatisext.sqlbuilder.LambdaQuery;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.builder.annotation.ProviderContext;

public class SqlProvider {

    /**
     * 新增
     *
     * @param providerContext ProviderContext
     * @return sql
     */
    public static String insert(ProviderContext providerContext) {
        return MapperManager.getSqlUseCache(providerContext, SqlProviderMethod.insert.getValue());
    }

    /**
     * 批量新增
     *
     * @param providerContext ProviderContext
     * @return sql
     */
    public static String insertBatch(ProviderContext providerContext) {
        return MapperManager.getSqlUseCache(providerContext, SqlProviderMethod.insertBatch.getValue());
    }

    /**
     * 修改
     *
     * @param providerContext ProviderContext
     * @return sql
     */
    public static String update(ProviderContext providerContext) {
        return MapperManager.getSqlUseCache(providerContext, SqlProviderMethod.update.getValue());
    }

    /**
     * 批量修改
     *
     * @param providerContext ProviderContext
     * @return sql
     */
    public static String updateBatch(ProviderContext providerContext) {
        return MapperManager.getSqlUseCache(providerContext, SqlProviderMethod.updateBatch.getValue());
    }

    /**
     * 修改全部属性
     *
     * @param providerContext ProviderContext
     * @return sql
     */
    public static String updateAll(ProviderContext providerContext) {
        return MapperManager.getSqlUseCache(providerContext, SqlProviderMethod.updateAll.getValue());
    }

    /**
     * 批量修改全部属性
     *
     * @param providerContext ProviderContext
     * @return sql
     */
    public static String updateAllBatch(ProviderContext providerContext) {
        return MapperManager.getSqlUseCache(providerContext, SqlProviderMethod.updateAllBatch.getValue());
    }

    /**
     * 删除
     *
     * @param providerContext ProviderContext
     * @return sql
     */
    public static String delete(ProviderContext providerContext) {
        return MapperManager.getSqlUseCache(providerContext, SqlProviderMethod.delete.getValue());
    }

    /**
     * 批量删除
     *
     * @param providerContext ProviderContext
     * @return sql
     */
    public static String deleteBatch(ProviderContext providerContext) {
        return MapperManager.getSqlUseCache(providerContext, SqlProviderMethod.deleteBatch.getValue());
    }

    /**
     * 根据ID删除
     *
     * @param providerContext ProviderContext
     * @return sql
     */
    public static String deleteById(ProviderContext providerContext) {
        return MapperManager.getSqlUseCache(providerContext, SqlProviderMethod.deleteById.getValue());
    }

    /**
     * 根据ID批量删除
     *
     * @param providerContext ProviderContext
     * @return sql
     */
    public static String deleteBatchByIds(ProviderContext providerContext) {
        return MapperManager.getSqlUseCache(providerContext, SqlProviderMethod.deleteBatchByIds.getValue());
    }

    /**
     * 根据ID查询
     *
     * @param providerContext ProviderContext
     * @return sql
     */
    public static String findById(ProviderContext providerContext) {
        return MapperManager.getSqlUseCache(providerContext, SqlProviderMethod.findById.getValue());
    }

    /**
     * 根据查询对象查询
     *
     * @param providerContext ProviderContext
     * @param lambdaQuery     查询对象
     * @return sql
     */
    public static String findByLambdaQuery(ProviderContext providerContext, @Param(BaseMapperParamConstant.QUERY) LambdaQuery<?> lambdaQuery) {
        return MapperManager.getSql(providerContext, SqlProviderMethod.findByLambdaQuery.getValue(), lambdaQuery);
    }

    /**
     * 根据查询对象查询总数量
     *
     * @param providerContext ProviderContext
     * @param lambdaQuery     查询对象
     * @return sql
     */
    public static String countByLambdaQuery(ProviderContext providerContext, @Param(BaseMapperParamConstant.QUERY) LambdaQuery<?> lambdaQuery) {
        return MapperManager.getSql(providerContext, SqlProviderMethod.count.getValue(), lambdaQuery);
    }

}
