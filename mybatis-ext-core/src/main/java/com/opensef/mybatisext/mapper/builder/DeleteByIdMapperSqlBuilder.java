package com.opensef.mybatisext.mapper.builder;

import com.opensef.mybatisext.mapper.BaseMapperParamConstant;
import com.opensef.mybatisext.mapper.EntityInfo;
import com.opensef.mybatisext.mapper.EntityManager;

import java.util.ArrayList;
import java.util.List;

public class DeleteByIdMapperSqlBuilder implements MapperSqlBuilder {

    @Override
    public MapperSql build(Class<?> entityClass, Object... args) {
        EntityInfo entityInfo = EntityManager.getEntityInfo(entityClass);

        String sql;
        // 如果是逻辑删除，实际执行修改操作
        if (null != entityInfo.getLogicDelete() && entityInfo.getLogicDelete()) {
            sql = makeLogicDeleteSql(entityInfo);
        } else {
            sql = makeDeleteSql(entityInfo);
        }

        MapperSql mapperSql = new MapperSql();
        mapperSql.setSql(script(sql));
        return mapperSql;
    }

    /**
     * 物理删除
     *
     * @param entityInfo 实体信息
     * @return sql
     */
    private String makeDeleteSql(EntityInfo entityInfo) {
        return String.format("DELETE FROM %s WHERE %s", entityInfo.getTableName(), condition(entityInfo.getColumnIdName(), BaseMapperParamConstant.ID));
    }

    /**
     * 逻辑删除
     *
     * @param entityInfo 实体信息
     * @return sql
     */
    private String makeLogicDeleteSql(EntityInfo entityInfo) {
        List<String> setConditions = new ArrayList<>();

        // 逻辑删除自动填充的属性信息
        if (null != entityInfo.getAutoFillLogicDeleteFieldInfoList() && entityInfo.getAutoFillLogicDeleteFieldInfoList().size() > 0) {
            for (EntityInfo.FieldInfo fieldInfo : entityInfo.getAutoFillLogicDeleteFieldInfoList()) {
                if (fieldInfo.getAutoFillLogicDelete()) {
                    setConditions.add(condition(fieldInfo.getColumnName(), fieldInfo.getFieldName()));
                }
            }
        }

        setConditions.add(condition(entityInfo.getColumnDeletedName(), entityInfo.getFieldDeletedName()));

        return String.format("UPDATE %s SET %s WHERE %s", entityInfo.getTableName(),
                String.join(",", setConditions),
                condition(entityInfo.getColumnIdName(), BaseMapperParamConstant.ID));
    }

}
