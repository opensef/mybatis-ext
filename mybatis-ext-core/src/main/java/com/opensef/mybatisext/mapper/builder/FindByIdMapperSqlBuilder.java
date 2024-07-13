package com.opensef.mybatisext.mapper.builder;

import com.opensef.mybatisext.mapper.BaseMapperParamConstant;
import com.opensef.mybatisext.mapper.EntityInfo;
import com.opensef.mybatisext.mapper.EntityManager;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.util.stream.Collectors;

public class FindByIdMapperSqlBuilder implements MapperSqlBuilder {

    @Override
    public MapperSql build(Class<?> entityClass, Object... args) {
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
        String sql = String.format("SELECT %s FROM %s WHERE %s", selectColumns, entityInfo.getTableName(),
                ifCondition(entityInfo.getColumnIdName(), BaseMapperParamConstant.ID));
        MapperSql mapperSql = new MapperSql();
        mapperSql.setSql(script(sql));
        return mapperSql;
    }

}
