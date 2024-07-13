package com.opensef.mybatisext.mapper.builder;

import com.opensef.mybatisext.mapper.EntityInfo;
import com.opensef.mybatisext.mapper.EntityManager;

import java.util.ArrayList;
import java.util.List;

public class UpdateAllMapperSqlBuilder implements MapperSqlBuilder {

    @Override
    public MapperSql build(Class<?> entityClass, Object... args) {
        EntityInfo entityInfo = EntityManager.getEntityInfo(entityClass);
        List<String> setConditions = new ArrayList<>();

        for (EntityInfo.FieldInfo fieldInfo : entityInfo.getFieldInfoList()) {
            setConditions.add(condition(fieldInfo.getColumnName(), fieldInfo.getFieldName(), fieldInfo.getTypeHandler()));
        }

        String sql = String.format("UPDATE %s SET %s WHERE %s", entityInfo.getTableName(),
                String.join(",", setConditions),
                condition(entityInfo.getColumnIdName(), entityInfo.getFieldIdName())
        );
        MapperSql mapperSql = new MapperSql();
        mapperSql.setSql(script(sql));
        return mapperSql;
    }

}
