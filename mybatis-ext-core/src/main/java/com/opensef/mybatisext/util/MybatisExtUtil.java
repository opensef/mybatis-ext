package com.opensef.mybatisext.util;

import com.opensef.mybatisext.PageRequest;
import com.opensef.mybatisext.exception.MybatisExtException;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.reflection.MetaObject;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MybatisExtUtil {

    /**
     * 设置参数并生成最终可执行的sql
     *
     * @param metaObject      MetaObject
     * @param mappedStatement MappedStatement
     * @param boundSql        BoundSql
     * @param newSql          添加表达式后的sql 例如：select * from user where gender = 1 and tenant_id = #{tenantId} ，其中tenant_id = #{tenantId}为租户插件自动添加的表达式
     * @param paramValueMap   参数-值Map集合
     */
    public static void setParameterAndGenSqlForExpression(MetaObject metaObject,
                                                          MappedStatement mappedStatement,
                                                          BoundSql boundSql,
                                                          String newSql,
                                                          Map<String, List<?>> paramValueMap) {
        /*------------------判断修改后的sql哪些位置增加了参数，在这个位置添加ParameterMapping-------------------*/
        // ParameterMapping必须和占位符参数的前后顺序位置保持一致
        List<ParameterMapping> newParameterMappings = new ArrayList<>(boundSql.getParameterMappings());

        // 提取全部以${开头，以}结尾的字符串
        Pattern pattern = Pattern.compile("\\$\\{[^}]+}");
        Matcher matcher = pattern.matcher(newSql);

        while (matcher.find()) {
            // 解析出的字符串，例如：${orgIds}
            String group = matcher.group();
            // 参数名称
            String paramName = group.substring(2, group.length() - 1).trim();

            // 计算字符串?占位符出现的次数
            int count = ExtStringUtil.countMatches(newSql.substring(0, matcher.start()), "?");

            for (int i = 0; i < paramValueMap.get(paramName).size(); i++) {
                // 根据?出现的次数，设置参数占位符。前面出现n次，则新添加的"?"数组索引应该为n（因为索引是从0开始的）
                newParameterMappings.add(count + i, new ParameterMapping.Builder(mappedStatement.getConfiguration(), paramName + "_" + i, Object.class).build());

                boundSql.setAdditionalParameter(paramName + "_" + i, paramValueMap.get(paramName).get(i));
            }

            // 替换为占位符sql
            newSql = newSql.replace(group, getPlaceholder(paramValueMap.get(paramName).size()));

            // 设置参数值，如果参数值是1个，表示等于、大于等二元表达式，参数值肯定只能有一个，如果参数值为2个及以上，说明是in、between表达式，参数值肯定是一个集合
            /*if (paramValueMap.get(paramName).size() == 1) {
                boundSql.setAdditionalParameter(paramName, paramValueMap.get(paramName).get(0));
            } else if (paramValueMap.get(paramName).size() > 1) {
                boundSql.setAdditionalParameter(paramName, paramValueMap.get(paramName));
            }*/
        }

        // 将预处理参数和参数值绑定到metaObject中
        metaObject.setValue("delegate.boundSql.parameterMappings", newParameterMappings);
        metaObject.setValue("delegate.boundSql.sql", newSql);
    }

    // 当前执行的方法Map key:mappedStatement的id，value:Method
    private static final Map<String, Method> MAPPED_STATEMENT_METHOD_MAP = new ConcurrentHashMap<>();
    // 当前Mapper的Class
    private static final Map<String, Class<?>> MAPPED_STATEMENT_CLASS_MAP = new ConcurrentHashMap<>();

    /**
     * 根据MappedStatement获取当前的方法Method
     *
     * @param mappedStatement MappedStatement
     * @return 当前执行的Method 一定不为空
     */
    public static Method getCurrentMethod(MappedStatement mappedStatement) {
        Method currentMethod = MAPPED_STATEMENT_METHOD_MAP.get(mappedStatement.getId());
        if (null != currentMethod) {
            return currentMethod;
        }

        Class<?> aClass = getCurrentClass(mappedStatement);
        for (Method method : aClass.getMethods()) {
            if (method.getName().equals(mappedStatement.getId().substring(mappedStatement.getId().lastIndexOf(".") + 1))) {
                currentMethod = method;
                break;
            }
        }
        if (null != currentMethod) {
            MAPPED_STATEMENT_METHOD_MAP.put(mappedStatement.getId(), currentMethod);
        }
        return currentMethod;

    }

    /**
     * 获取当前Mapper的Class
     *
     * @param mappedStatement MappedStatement
     * @return 当前Mapper的Class
     */
    public static Class<?> getCurrentClass(MappedStatement mappedStatement) {
        Class<?> aClass = MAPPED_STATEMENT_CLASS_MAP.get(mappedStatement.getId());
        if (null != aClass) {
            return aClass;
        }
        try {
            aClass = ExtClassUtil.getClass(mappedStatement.getId().substring(0, mappedStatement.getId().lastIndexOf(".")));
            MAPPED_STATEMENT_CLASS_MAP.put(mappedStatement.getId(), aClass);
        } catch (ClassNotFoundException e) {
            throw new MybatisExtException(e);
        }
        return aClass;
    }

    /**
     * 根据value的个数生成?占位符
     *
     * @param num 要生成的?占位符数量
     * @return 占位符字符串，多个用英文逗号分隔
     */
    private static String getPlaceholder(int num) {
        List<String> placeholders = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            placeholders.add("?");
        }
        return String.join(",", placeholders);
    }

    /**
     * 获取包含字符的个数
     *
     * @param str         字符串
     * @param containChar 包含的字符
     * @return 包含字符的数量
     */
    public static Integer getContainCharCount(String str, char containChar) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == containChar) {
                count++;
            }
        }
        return count;
    }

    /**
     * 获取包含字符串的个数
     *
     * @param str        字符串
     * @param containStr 包含的字符串
     * @return 包含某个字符串的数量
     */
    public static Integer getContainStrCount(String str, String containStr) {
        int count = 0;
        int index;
        while ((index = str.indexOf(containStr)) != -1) {
            str = str.substring(index + 1);
            count++;
        }
        return count;
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
     * PageRequest参数名称，Mapper的当方法参数中有PageRequest对象时，实现自动分页，使用此默认参数名
     */
    public static String pageRequestParam;

    static {
        String simpleName = PageRequest.class.getSimpleName();
        StringBuilder paramName = new StringBuilder();
        paramName.append(Character.toLowerCase(simpleName.charAt(0)));
        for (int i = 1; i < simpleName.length(); i++) {
            paramName.append(simpleName.charAt(i));
        }
        pageRequestParam = paramName.toString();
    }

    /**
     * 获取真实代理对象
     *
     * @param target 源对象
     * @param <T>    泛型
     * @return 真实代理对象
     */
    @SuppressWarnings("unchecked")
    /*public static <T> T realTarget(Object target) {
        if (Proxy.isProxyClass(target.getClass())) {
            MetaObject metaObject = SystemMetaObject.forObject(target);
            return realTarget(metaObject.getValue("h.target"));
        }
        return (T) target;
    }*/
    public static <T> T realTarget(Object target) {
        while (Proxy.isProxyClass(target.getClass())) {
            Proxy proxy = (Proxy) target;
            Plugin plugin = (Plugin) Proxy.getInvocationHandler(proxy);
            Field targetFiled;
            try {
                targetFiled = plugin.getClass().getDeclaredField("target");
                targetFiled.setAccessible(true);
                target = targetFiled.get(plugin);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new MybatisExtException(e);
            }
        }
        return (T) target;
    }

    /**
     * 获取接口上的泛型
     *
     * @param typeClass Class
     * @return 泛型Class
     */
    public static Class<?> getGenericInterface(Class<?> typeClass) {
        Class<?> entityClass = null;
        for (Type type : typeClass.getGenericInterfaces()) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                entityClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];
            } else {
                entityClass = getGenericInterface((Class<?>) type);
            }

        }
        return entityClass;
    }

}
