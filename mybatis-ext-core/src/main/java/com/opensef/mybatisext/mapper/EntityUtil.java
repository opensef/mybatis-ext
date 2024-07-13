package com.opensef.mybatisext.mapper;


import com.opensef.mybatisext.annotation.TableColumn;
import com.opensef.mybatisext.annotation.TableName;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 实体工具类
 */
public class EntityUtil {

    /**
     * 根据实体Class获取表名
     *
     * @param entityClass 实体Class
     * @return 表名
     */
    public static String getTableName(Class<?> entityClass) {
        // 表名
        String tableName;

        // 自定义表名
        TableName declaredAnnotation = entityClass.getDeclaredAnnotation(TableName.class);
        if (null != declaredAnnotation) {
            tableName = declaredAnnotation.value();
        } else {
            // 默认表名
            tableName = camelToUnderline(entityClass.getSimpleName());
        }
        return tableName;
    }

    /**
     * 根据field获取实体属性信息
     *
     * @param field Field
     * @return 实体属性信息
     */
    public static EntityPropertyInfo getEntityPropertyInfo(Field field) {
        // 列名注解
        TableColumn tableColumn = field.getDeclaredAnnotation(TableColumn.class);

        EntityPropertyInfo entityPropertyInfo = new EntityPropertyInfo();
        if (null == tableColumn) {
            entityPropertyInfo.setPropertyName(field.getName());
            entityPropertyInfo.setColumnName(camelToUnderline(field.getName()));
            entityPropertyInfo.setIgnore(false);
            return entityPropertyInfo;
        }

        // 如果属性不被忽略
        if (!tableColumn.ignore()) {
            if (tableColumn.value().trim().equals("")) {
                entityPropertyInfo.setPropertyName(field.getName());
                entityPropertyInfo.setTypeHandler(tableColumn.typeHandler());
                entityPropertyInfo.setColumnName(camelToUnderline(field.getName()));
                entityPropertyInfo.setIgnore(false);
            } else {
                entityPropertyInfo.setPropertyName(field.getName());
                entityPropertyInfo.setTypeHandler(tableColumn.typeHandler());
                entityPropertyInfo.setColumnName(tableColumn.value());
                entityPropertyInfo.setIgnore(tableColumn.ignore());
            }
        } else {
            entityPropertyInfo.setIgnore(true);
        }
        return entityPropertyInfo;
    }

    /**
     * 驼峰格式字符串转换为下划线格式字符串
     *
     * @param param 待转换属性
     * @return 下划线字符串
     */
    public static String camelToUnderline(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        // 首字母转换为小写
        sb.append(Character.toLowerCase(param.charAt(0)));
        for (int i = 1; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append("_");
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 下划线格式字符串转换为驼峰格式字符串
     *
     * @param param 待转换属性
     * @return 驼峰格式字符串
     */
    public static String underlineToCamel(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (c != '_') {
                if (i > 0 && param.charAt(i - 1) == '_') {
                    sb.append(Character.toUpperCase(c));
                } else {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 列转行
     * 转换前：[[id0, id1, id2],[name0, name1, name2]]
     * 转换后：[[id0, name0],[id1, name1],[id2, name2]]
     *
     * @return 转行后的集合
     */
    public static <T> List<List<T>> columnToRow(List<List<T>> values) {
        // key：转换后行索引，对应转换前列索引
        Map<Integer, List<T>> map = new HashMap<>();

        for (List<T> colList : values) {
            for (int j = 0; j < colList.size(); j++) {
                List<T> afterColList = map.get(j);
                if (Objects.isNull(afterColList)) {
                    afterColList = new ArrayList<>();
                    map.put(j, afterColList);
                }
                afterColList.add(colList.get(j));
            }
        }
        return new ArrayList<>(map.values());
    }

}
