package com.opensef.mybatisext.mapper.builder;

import com.opensef.mybatisext.mapper.EntityInfo;
import com.opensef.mybatisext.mapper.EntityManager;
import com.opensef.mybatisext.sqlbuilder.LambdaQuery;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.util.stream.Collectors;

public class FindByLambdaQueryMapperSqlBuilder implements MapperSqlBuilder {

    @Override
    public MapperSql build(Class<?> entityClass, Object... args) {
        LambdaQuery<?> lambdaQuery = (LambdaQuery<?>) args[0];
        EntityInfo entityInfo = EntityManager.getEntityInfo(entityClass);
        String selectColumns = entityInfo.getFieldInfoList().stream()
                .map(fieldInfo -> {
                    // 设置了@TableColumn注解，且没有指定typeHandler时，才加AS注解；指定了typeHandler时，会走ResultMap插件
                    if (fieldInfo.getTableColumn() && fieldInfo.getTypeHandler().equals(UnknownTypeHandler.class)) {
                        return fieldInfo.getColumnName() + " AS " + fieldInfo.getFieldName();
                    } else {
                        return fieldInfo.getColumnName();
                    }
                })
                .collect(Collectors.joining(", "));
        String sql = String.format("SELECT %s FROM %s <where>%s</where>%s", selectColumns, entityInfo.getTableName(), lambdaQuery.toQuerySql(), lambdaQuery.toOrderBySql());
        MapperSql mapperSql = new MapperSql();
        mapperSql.setSql(script(sql));
        return mapperSql;
    }

}
