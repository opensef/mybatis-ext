package com.opensef.mybatisext.mapper;

import com.opensef.mybatisext.annotation.AutoFillLogicDelete;
import com.opensef.mybatisext.annotation.Deleted;
import com.opensef.mybatisext.annotation.TableColumn;
import com.opensef.mybatisext.annotation.TableId;
import com.opensef.mybatisext.exception.MybatisExtException;
import com.opensef.mybatisext.util.ExtClassUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实体管理器
 */
public class EntityManager {

    /**
     * 反射生成的实体-属性缓存
     */
    public static Map<Class<?>, Field[]> fieldMap = new ConcurrentHashMap<>();

    /**
     * 反射生成的实体-表名缓存
     */
    public static Map<Class<?>, String> tableNameMap = new ConcurrentHashMap<>();

    /**
     * 属性字段-实体信息缓存
     */
    public static Map<Field, EntityPropertyInfo> fieldEntityPropertyInfoMap = new ConcurrentHashMap<>();

    /**
     * 属性字段-实体信息缓存 key:实体Class value:Map<属性名称，属性信息>
     */
    public static Map<Class<?>, Map<String, EntityPropertyInfo>> classFieldNameMap = new ConcurrentHashMap<>();

    /**
     * 实体信息缓存 key:实体Class value:EntityInfo
     */
    public static Map<Class<?>, EntityInfo> entityInfoMap = new ConcurrentHashMap<>();

    /**
     * 获取实体类的属性
     *
     * @param entityClass 实体类Class
     * @return 属性数组
     */
    public static Field[] getFields(Class<?> entityClass) {
        Field[] fields = fieldMap.get(entityClass);
        if (fields == null) {
            fields = ExtClassUtil.getAllFields(entityClass);
            fieldMap.put(entityClass, fields);
        }
        return fields;
    }

    /**
     * 获取实体属性信息Map集合 key:属性名称 value:属性信息实体
     *
     * @param entityClass 实体类型
     * @return 实体属性信息Map集合
     */
    public static Map<String, EntityPropertyInfo> getFieldNamesMap(Class<?> entityClass) {
        Map<String, EntityPropertyInfo> fieldNameEntityPropertyInfoMap = classFieldNameMap.get(entityClass);
        if (fieldNameEntityPropertyInfoMap == null) {
            fieldNameEntityPropertyInfoMap = new HashMap<>();
            Field[] fields = ExtClassUtil.getAllFields(entityClass);
            for (Field field : fields) {
                fieldNameEntityPropertyInfoMap.put(field.getName(), EntityUtil.getEntityPropertyInfo(field));
            }
            classFieldNameMap.put(entityClass, fieldNameEntityPropertyInfoMap);
        }
        return fieldNameEntityPropertyInfoMap;
    }

    /**
     * 获取表名
     *
     * @param entityClass 实体Class
     * @return 表名
     */
    public static String getTableName(Class<?> entityClass) {
        String tableName = tableNameMap.get(entityClass);
        if (tableName == null) {
            tableName = EntityUtil.getTableName(entityClass);
            tableNameMap.put(entityClass, tableName);
        }
        return tableName;
    }

    public static EntityInfo getEntityInfo(Class<?> entityClass) {
        EntityInfo entityInfo = entityInfoMap.get(entityClass);
        if (null != entityInfo) {
            return entityInfo;
        }

        entityInfo = new EntityInfo();
        entityInfo.setTableName(getTableName(entityClass));
        List<EntityInfo.FieldInfo> fieldInfoList = new ArrayList<>();
        entityInfo.setFieldInfoList(fieldInfoList);
        entityInfo.setAutoFillLogicDeleteFieldInfoList(new ArrayList<>());

        Field[] fields = getFields(entityClass);
        for (Field field : fields) {
            EntityInfo.FieldInfo fieldInfo = new EntityInfo.FieldInfo();
            EntityPropertyInfo entityPropertyInfo = getEntityPropertyInfo(field);
            // 如果该属性不需要持久化，则不将其添加进属性字段集合
            if (entityPropertyInfo.getIgnore()) {
                continue;
            }

            fieldInfo.setFieldName(entityPropertyInfo.getPropertyName());
            fieldInfo.setTypeHandler(entityPropertyInfo.getTypeHandler());
            fieldInfo.setColumnName(entityPropertyInfo.getColumnName());
            fieldInfo.setIgnore(entityPropertyInfo.getIgnore());
            // 默认设置诶false
            fieldInfo.setIdField(false);
            fieldInfo.setLogicDelete(false);
            fieldInfo.setAutoFillLogicDelete(false);
            fieldInfo.setTableColumn(false);

            if (field.isAnnotationPresent(TableId.class)) {
                fieldInfo.setIdField(true);
                entityInfo.setFieldIdName(entityPropertyInfo.getPropertyName());
                entityInfo.setColumnIdName(entityPropertyInfo.getColumnName());
            } else if (field.isAnnotationPresent(Deleted.class)) {
                Deleted deleted = field.getDeclaredAnnotation(Deleted.class);
                entityInfo.setLogicDelete(deleted.logicDelete());
                fieldInfo.setLogicDelete(deleted.logicDelete());
                entityInfo.setFieldDeletedName(entityPropertyInfo.getPropertyName());
                entityInfo.setColumnDeletedName(entityPropertyInfo.getColumnName());
            } else if (field.isAnnotationPresent(AutoFillLogicDelete.class)) {
                fieldInfo.setAutoFillLogicDelete(true);
                entityInfo.getAutoFillLogicDeleteFieldInfoList().add(fieldInfo);
            }
            if (field.isAnnotationPresent(TableColumn.class)) {
                fieldInfo.setTableColumn(true);
            }

            fieldInfoList.add(fieldInfo);
        }

        entityInfoMap.put(entityClass, entityInfo);
        return entityInfo;
    }

    /**
     * 获取实体属性信息
     *
     * @param field Field
     * @return 属性信息
     */
    public static EntityPropertyInfo getEntityPropertyInfo(Field field) {
        EntityPropertyInfo entityPropertyInfo = fieldEntityPropertyInfoMap.get(field);
        if (entityPropertyInfo == null) {
            entityPropertyInfo = EntityUtil.getEntityPropertyInfo(field);
            fieldEntityPropertyInfoMap.put(field, entityPropertyInfo);
        }
        return entityPropertyInfo;
    }

    /**
     * 获取实体属性值
     *
     * @param field  Field
     * @param entity 实体
     * @return 实体属性值
     */
    public static Object getPropertyValue(Field field, Object entity) {
        Object propertyValue;
        try {
            field.setAccessible(true);
            propertyValue = field.get(entity);
        } catch (IllegalAccessException e) {
            throw new MybatisExtException(e);
        }
        return propertyValue;
    }

}
