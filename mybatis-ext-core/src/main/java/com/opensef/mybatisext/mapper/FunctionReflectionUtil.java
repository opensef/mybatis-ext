package com.opensef.mybatisext.mapper;

import com.opensef.mybatisext.util.ExtClassUtil;

import java.beans.Introspector;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 函数式类方法解析
 */
public class FunctionReflectionUtil {

    /**
     * Getter方法对应的Field缓存
     */
    private static final Map<SerializableFunction<?, ?>, Field> cache = new ConcurrentHashMap<>();

    /**
     * 根据Getter方法获取属性对应数据库表的字段名称
     *
     * @param function Getter方法
     * @param <T>      Getter方法所在类
     * @param <R>      返回值类型
     * @return 数据库表字段名称
     */
    public static <T, R> String getTableColumnName(SerializableFunction<T, R> function) {
        EntityPropertyInfo entityPropertyInfo = EntityManager.getEntityPropertyInfo(getField(function));
        return entityPropertyInfo.getColumnName();
    }

    /**
     * 根据Getter方法获取属性名称
     *
     * @param function Getter方法
     * @param <T>      Getter方法所在类
     * @param <R>      返回值类型
     * @return 属性名称
     */
    public static <T, R> String getFieldName(SerializableFunction<T, R> function) {
        Field field = getField(function);
        return field.getName();
    }

    /**
     * 根据Getter方法获取属性Field
     *
     * @param function Getter方法
     * @param <T>      Getter方法所在类
     * @param <R>      返回值类型
     * @return 属性Field
     */
    public static <T, R> Field getField(SerializableFunction<T, R> function) {
        Field field = cache.get(function);
        if (field == null) {
            field = findField(function);
            cache.put(function, field);
        }
        return field;
    }

    /**
     * 根据Getter方法获取属性名
     *
     * @param function Getter方法
     * @param <T>      Getter方法所在类
     * @param <R>      返回值类型
     * @return 属性名称
     */
    private static <T, R> Field findField(SerializableFunction<T, R> function) {
        Field field = null;
        String fieldName = null;
        try {
            // 1.获取SerializedLambda
            Method method = function.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(function);

            // 2.获取implMethodName 即为Field对应的Getter方法名
            String implMethodName = serializedLambda.getImplMethodName();
            if (implMethodName.startsWith("get") && implMethodName.length() > 3) {
                fieldName = Introspector.decapitalize(implMethodName.substring(3));
            } else if (implMethodName.startsWith("is") && implMethodName.length() > 2) {
                fieldName = Introspector.decapitalize(implMethodName.substring(2));
            } else if (implMethodName.startsWith("lambda$")) {
                throw new IllegalArgumentException("SerializableFunction不能传递lambda表达式,只能使用方法引用");
            } else {
                throw new IllegalArgumentException(implMethodName + "不是Getter方法引用");
            }

            // 3.获取的Class是字符串，并且包名是“/”分割，需要替换成“.”，才能获取到对应的Class对象
            String declaredClass = serializedLambda.getImplClass().replace("/", ".");
            Class<?> aClass = ExtClassUtil.getClass(declaredClass);

            // 4.获取Class中定义的Field
            field = ExtClassUtil.getField(aClass, fieldName, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 5.如果没有找到对应的字段应该抛出异常
        if (field != null) {
            return field;
        }
        throw new NoSuchFieldError(fieldName);
    }

}
