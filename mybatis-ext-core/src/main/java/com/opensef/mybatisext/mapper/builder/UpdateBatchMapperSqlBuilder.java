package com.opensef.mybatisext.mapper.builder;

import com.opensef.mybatisext.mapper.BaseMapperParamConstant;
import com.opensef.mybatisext.mapper.EntityInfo;
import com.opensef.mybatisext.mapper.EntityManager;

import java.util.ArrayList;
import java.util.List;

public class UpdateBatchMapperSqlBuilder implements MapperSqlBuilder {

    @Override
    public MapperSql build(Class<?> entityClass, Object... args) {
        EntityInfo entityInfo = EntityManager.getEntityInfo(entityClass);
        List<String> setConditions = new ArrayList<>();

        for (EntityInfo.FieldInfo fieldInfo : entityInfo.getFieldInfoList()) {
            if (!fieldInfo.getIdField()) {
                setConditions.add(ifCondition(fieldInfo.getColumnName(), "entity." + fieldInfo.getFieldName(), fieldInfo.getTypeHandler(), ","));
            }
        }

        String sql = String.format("<foreach collection='" + BaseMapperParamConstant.ENTITY_LIST + "' item='entity' separator=';'> UPDATE %s <set><trim suffixOverrides=','> %s </trim></set> WHERE %s </foreach>", entityInfo.getTableName(),
                String.join("\n", setConditions),
                condition(entityInfo.getColumnIdName(), "entity." + entityInfo.getFieldIdName())
        );
        MapperSql mapperSql = new MapperSql();
        mapperSql.setSql(script(sql));
        return mapperSql;
    }

}
