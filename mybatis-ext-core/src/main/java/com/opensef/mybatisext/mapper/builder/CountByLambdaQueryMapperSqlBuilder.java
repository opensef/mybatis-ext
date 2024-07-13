package com.opensef.mybatisext.mapper.builder;

import com.opensef.mybatisext.mapper.EntityInfo;
import com.opensef.mybatisext.mapper.EntityManager;
import com.opensef.mybatisext.sqlbuilder.LambdaQuery;

public class CountByLambdaQueryMapperSqlBuilder implements MapperSqlBuilder {

    @Override
    public MapperSql build(Class<?> entityClass, Object... args) {
        LambdaQuery<?> lambdaQuery = (LambdaQuery<?>) args[0];
        EntityInfo entityInfo = EntityManager.getEntityInfo(entityClass);
        String sql = String.format("SELECT count(*) FROM %s <where>%s</where>", entityInfo.getTableName(), lambdaQuery.toSql());
        MapperSql mapperSql = new MapperSql();
        mapperSql.setSql(script(sql));
        return mapperSql;
    }

}
