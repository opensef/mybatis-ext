package com.opensef.mybatisext.mapper.builder;

import com.opensef.mybatisext.mapper.EntityInfo;
import com.opensef.mybatisext.mapper.EntityManager;

import java.util.ArrayList;
import java.util.List;

public class InsertMapperSqlBuilder implements MapperSqlBuilder {

    @Override
    public MapperSql build(Class<?> entityClass, Object... args) {
        EntityInfo entityInfo = EntityManager.getEntityInfo(entityClass);
        List<String> columns = new ArrayList<>();
        List<String> values = new ArrayList<>();

        for (EntityInfo.FieldInfo fieldInfo : entityInfo.getFieldInfoList()) {
            columns.add(fieldInfo.getColumnName());
            values.add(paramPlaceholder(fieldInfo.getFieldName(), fieldInfo.getTypeHandler()));
        }

        String sql = String.format("INSERT INTO %s(%s) VALUES(%s)", entityInfo.getTableName(),
                String.join(",", columns),
                String.join(",", values));
        MapperSql mapperSql = new MapperSql();
        mapperSql.setSql(script(sql));
        return mapperSql;
    }

}
