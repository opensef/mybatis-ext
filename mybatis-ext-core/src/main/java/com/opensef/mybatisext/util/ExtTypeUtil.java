package com.opensef.mybatisext.util;

import com.opensef.mybatisext.mapper.EntityManager;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 类型相关工具类
 */
public class ExtTypeUtil {

    /**
     * 内置类型集合 key:Class value:true
     */
    private static final Map<Class<?>, Boolean> BUILT_IN_TYPE_MAP = new HashMap<>();

    /**
     * 数字类型集合
     */
    private static final Map<Class<?>, Boolean> NUMBER_TYPE_MAP = new HashMap<>();

    /**
     * 布尔类型集合
     */
    private static final Map<Class<?>, Boolean> BOOLEAN_TYPE_MAP = new HashMap<>();

    static {
        BUILT_IN_TYPE_MAP.put(byte.class, true);
        BUILT_IN_TYPE_MAP.put(short.class, true);
        BUILT_IN_TYPE_MAP.put(int.class, true);
        BUILT_IN_TYPE_MAP.put(long.class, true);
        BUILT_IN_TYPE_MAP.put(float.class, true);
        BUILT_IN_TYPE_MAP.put(double.class, true);
        BUILT_IN_TYPE_MAP.put(Byte.class, true);
        BUILT_IN_TYPE_MAP.put(Short.class, true);
        BUILT_IN_TYPE_MAP.put(Integer.class, true);
        BUILT_IN_TYPE_MAP.put(Long.class, true);
        BUILT_IN_TYPE_MAP.put(Float.class, true);
        BUILT_IN_TYPE_MAP.put(Double.class, true);
        BUILT_IN_TYPE_MAP.put(BigInteger.class, true);
        BUILT_IN_TYPE_MAP.put(BigDecimal.class, true);
        BUILT_IN_TYPE_MAP.put(String.class, true);
        BUILT_IN_TYPE_MAP.put(char.class, true);
        BUILT_IN_TYPE_MAP.put(Character.class, true);
        BUILT_IN_TYPE_MAP.put(boolean.class, true);
        BUILT_IN_TYPE_MAP.put(Boolean.class, true);
        BUILT_IN_TYPE_MAP.put(Enum.class, true);
        BUILT_IN_TYPE_MAP.put(LocalDate.class, true);
        BUILT_IN_TYPE_MAP.put(LocalDateTime.class, true);
        BUILT_IN_TYPE_MAP.put(Instant.class, true);
        BUILT_IN_TYPE_MAP.put(Date.class, true);
    }

    static {
        NUMBER_TYPE_MAP.put(short.class, true);
        NUMBER_TYPE_MAP.put(int.class, true);
        NUMBER_TYPE_MAP.put(long.class, true);
        NUMBER_TYPE_MAP.put(float.class, true);
        NUMBER_TYPE_MAP.put(double.class, true);
        NUMBER_TYPE_MAP.put(Byte.class, true);
        NUMBER_TYPE_MAP.put(Short.class, true);
        NUMBER_TYPE_MAP.put(Integer.class, true);
        NUMBER_TYPE_MAP.put(Long.class, true);
        NUMBER_TYPE_MAP.put(Float.class, true);
        NUMBER_TYPE_MAP.put(Double.class, true);
        NUMBER_TYPE_MAP.put(BigInteger.class, true);
        NUMBER_TYPE_MAP.put(BigDecimal.class, true);
    }

    static {
        BOOLEAN_TYPE_MAP.put(boolean.class, true);
        BOOLEAN_TYPE_MAP.put(Boolean.class, true);
    }

    /**
     * 是否是内置类型
     *
     * @param typeClass Class
     * @return true/false
     */
    public static boolean isBuiltInType(Class<?> typeClass) {
        return Optional.ofNullable(BUILT_IN_TYPE_MAP.get(typeClass)).orElse(typeClass.isEnum());
    }

    /**
     * 是否是内置类型
     *
     * @param object 对象
     * @return true/false
     */
    public static boolean isBuiltInType(Object object) {
        return null != object && Optional.ofNullable(BUILT_IN_TYPE_MAP.get(object.getClass())).orElse(object.getClass().isEnum());
    }

    /**
     * 是否是数字类型
     *
     * @param object 对象
     * @return true/false
     */
    public static boolean isNumber(Object object) {
        return null != object && Optional.ofNullable(NUMBER_TYPE_MAP.get(object.getClass())).orElse(false);
    }

    /**
     * 是否是布尔类型
     *
     * @param object 对象
     * @return true/false
     */
    public static boolean isBoolean(Object object) {
        return null != object && Optional.ofNullable(BOOLEAN_TYPE_MAP.get(object.getClass())).orElse(false);
    }

    /**
     * 对象是否是集合
     *
     * @param object 对象
     * @return true/false
     */
    public static boolean isCollection(Object object) {
        return object instanceof Collection;
    }

    /**
     * 对象是否是数组
     *
     * @param object 对象
     * @return true/false
     */
    public static boolean isArray(Object object) {
        return object != null && object.getClass().isArray();
    }

    /**
     * 是否是集合类型
     *
     * @param typeClass Class
     * @return true/false
     */
    public static boolean isCollection(Class<?> typeClass) {
        return Collection.class.isAssignableFrom(typeClass);
    }

    /**
     * 是否是数组类型
     *
     * @param typeClass Class
     * @return true/false
     */
    public static boolean isArray(Class<?> typeClass) {
        return typeClass.isArray();
    }

    /**
     * 是否是集合或数组类型
     *
     * @param typeClass Class
     * @return true/false
     */
    public static boolean isCollectionOrArray(Class<?> typeClass) {
        return isCollection(typeClass) || isArray(typeClass);
    }

    /**
     * 是否是集合或数组类型
     *
     * @param object 对象
     * @return true/false
     */
    public static boolean isCollectionOrArray(Object object) {
        return isCollection(object) || isArray(object);
    }

    /**
     * 对象转Map
     *
     * @param obj 对象
     * @return Map
     */
    public static Map<String, Object> objectToMap(Object obj) {
        if (obj == null) {
            return null;
        }

        Field[] fields = EntityManager.getFields(obj.getClass());
        return objectToMap(obj, fields);
    }

    /**
     * 对象转Map
     *
     * @param obj    对象
     * @param fields 对象Field
     * @return Map
     */
    public static Map<String, Object> objectToMap(Object obj, Field[] fields) {
        if (obj == null) {
            return null;
        }

        Map<String, Object> map = new HashMap<>();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (null != value) {
                    map.put(field.getName(), field.get(obj));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

}
