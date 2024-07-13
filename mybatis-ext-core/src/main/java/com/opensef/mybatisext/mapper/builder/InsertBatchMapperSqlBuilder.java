package com.opensef.mybatisext.mapper.builder;

import com.opensef.mybatisext.mapper.BaseMapperParamConstant;
import com.opensef.mybatisext.mapper.EntityInfo;
import com.opensef.mybatisext.mapper.EntityManager;

import java.util.ArrayList;
import java.util.List;

public class InsertBatchMapperSqlBuilder implements MapperSqlBuilder {

    @Override
    public MapperSql build(Class<?> entityClass, Object... args) {
        EntityInfo entityInfo = EntityManager.getEntityInfo(entityClass);
        List<String> columns = new ArrayList<>();
        List<String> values = new ArrayList<>();

        for (EntityInfo.FieldInfo fieldInfo : entityInfo.getFieldInfoList()) {
            columns.add(fieldInfo.getColumnName());
            values.add(paramPlaceholder("entity." + fieldInfo.getFieldName(), fieldInfo.getTypeHandler()));
        }

        String sql = String.format("INSERT INTO %s<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">%s</trim> VALUES " +
                        "<foreach collection='" + BaseMapperParamConstant.ENTITY_LIST + "' item='entity' separator=','>" +
                        "<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">%s</trim></foreach>", entityInfo.getTableName(),
                String.join(",", columns),
                String.join(",", values));
        MapperSql mapperSql = new MapperSql();
        mapperSql.setSql(script(sql));
        return mapperSql;
    }

}
