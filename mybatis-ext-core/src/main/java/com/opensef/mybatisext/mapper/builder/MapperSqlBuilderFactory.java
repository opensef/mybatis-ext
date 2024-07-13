package com.opensef.mybatisext.mapper.builder;

import com.opensef.mybatisext.exception.MybatisExtException;
import com.opensef.mybatisext.mapper.ProviderContextUtil;
import org.apache.ibatis.builder.annotation.ProviderContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MapperSqlBuilderFactory {

    /**
     * BaseMapper中的方法名称对应的sql构建器
     * key：注册器名称（可对应SqlProvider中的方法）
     * value：sql构建器
     */
    private final Map<String, MapperSqlBuilder> methodSqlBuilderMap = new HashMap<>();

    public MapperSqlBuilderFactory() {
        initRegisterSqlBuilder();
    }

    /**
     * 初始化Repository对应的sql构建器
     */
    private void initRegisterSqlBuilder() {
        register(SqlProviderMethod.insert.getValue(), new InsertMapperSqlBuilder());
        register(SqlProviderMethod.insertBatch.getValue(), new InsertBatchMapperSqlBuilder());
        register(SqlProviderMethod.update.getValue(), new UpdateMapperSqlBuilder());
        register(SqlProviderMethod.updateBatch.getValue(), new UpdateBatchMapperSqlBuilder());
        register(SqlProviderMethod.updateAll.getValue(), new UpdateAllMapperSqlBuilder());
        register(SqlProviderMethod.updateAllBatch.getValue(), new UpdateAllBatchMapperSqlBuilder());
        register(SqlProviderMethod.delete.getValue(), new DeleteMapperSqlBuilder());
        register(SqlProviderMethod.deleteBatch.getValue(), new DeleteBatchMapperSqlBuilder());
        register(SqlProviderMethod.deleteById.getValue(), new DeleteByIdMapperSqlBuilder());
        register(SqlProviderMethod.deleteBatchByIds.getValue(), new DeleteBatchByIdsMapperSqlBuilder());
        register(SqlProviderMethod.findById.getValue(), new FindByIdMapperSqlBuilder());
        register(SqlProviderMethod.findByLambdaQuery.getValue(), new FindByLambdaQueryMapperSqlBuilder());
        register(SqlProviderMethod.findPage.getValue(), new FindByLambdaQueryMapperSqlBuilder());
        register(SqlProviderMethod.count.getValue(), new CountByLambdaQueryMapperSqlBuilder());
    }

    /**
     * 注册sql构建器
     *
     * @param name             注册名称<br>
     *                         方法名称用字符串类型，是为了更好的支持自定义扩展Mapper
     * @param mapperSqlBuilder sql构建器
     */
    public void register(String name, MapperSqlBuilder mapperSqlBuilder) {
        methodSqlBuilderMap.put(name, mapperSqlBuilder);
    }

    public MapperSql build(ProviderContext providerContext, String name, Object... args) {
        MapperSqlBuilder sqlBuilder = methodSqlBuilderMap.get(name);
        if (Objects.isNull(sqlBuilder)) {
            throw new MybatisExtException("方法未注册，MapperSqlBuilderFactory的注册器中未找到" + name + "SQL构建器");
        }

        return sqlBuilder.build(ProviderContextUtil.getEntityClass(providerContext), args);
    }

}
