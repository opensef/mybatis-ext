package com.opensef.mybatisext.sqlbuilder;

import com.opensef.mybatisext.mapper.BaseMapperParamConstant;

import java.util.Collection;
import java.util.Map;

/**
 * sql运算符生成sql工具类
 */
public class SqlOperatorUtil {

    public static String eq(String column, Object value, Map<String, Object> params) {
        PlaceholderParam placeholderParam = getPlaceholderParamName(column, params);
        params.put(placeholderParam.getPlaceholderParamName(), value);
        // 参数占位符:column
        return column + " = " + placeholderParam.getPlaceholder();
    }

    public static String eq(String column, PlainSelect select, Map<String, Object> params) {
        params.putAll(select.getParams().getParamMap());
        return column + " = " + "(" + select.toSql() + ")";
    }

    public static String notEq(String column, Object value, Map<String, Object> params) {
        PlaceholderParam placeholderParam = getPlaceholderParamName(column, params);
        params.put(placeholderParam.getPlaceholderParamName(), value);
        // 参数占位符:column
        return column + " != " + placeholderParam.getPlaceholder();
    }

    public static String notEq(String column, PlainSelect select, Map<String, Object> params) {
        params.putAll(select.getParams().getParamMap());
        return column + " != " + "(" + select.toSql() + ")";
    }

    public static String gt(String column, Object value, Map<String, Object> params) {
        PlaceholderParam placeholderParam = getPlaceholderParamName(column, params);
        params.put(placeholderParam.getPlaceholderParamName(), value);
        // 参数占位符:column
        return column + " &gt; " + placeholderParam.getPlaceholder();
    }

    public static String gt(String column, PlainSelect select, Map<String, Object> params) {
        params.putAll(select.getParams().getParamMap());
        return column + " &gt; " + "(" + select.toSql() + ")";
    }

    public static String gte(String column, Object value, Map<String, Object> params) {
        PlaceholderParam placeholderParam = getPlaceholderParamName(column, params);
        params.put(placeholderParam.getPlaceholderParamName(), value);
        // 参数占位符:column
        return column + " &gt;= " + placeholderParam.getPlaceholder();
    }

    public static String gte(String column, PlainSelect select, Map<String, Object> params) {
        params.putAll(select.getParams().getParamMap());
        return column + " &gt;= " + "(" + select.toSql() + ")";
    }

    public static String lt(String column, Object value, Map<String, Object> params) {
        PlaceholderParam placeholderParam = getPlaceholderParamName(column, params);
        params.put(placeholderParam.getPlaceholderParamName(), value);
        // 参数占位符:column
        return column + " &lt; " + placeholderParam.getPlaceholder();
    }

    public static String lt(String column, PlainSelect select, Map<String, Object> params) {
        params.putAll(select.getParams().getParamMap());
        return column + " &lt; " + "(" + select.toSql() + ")";
    }

    public static String lte(String column, Object value, Map<String, Object> params) {
        PlaceholderParam placeholderParam = getPlaceholderParamName(column, params);
        params.put(placeholderParam.getPlaceholderParamName(), value);
        // 参数占位符:column
        return column + " &lt;= " + placeholderParam.getPlaceholder();
    }

    public static String lte(String column, PlainSelect select, Map<String, Object> params) {
        params.putAll(select.getParams().getParamMap());
        return column + " &lt;= " + "(" + select.toSql() + ")";
    }

    public static String in(String column, Collection<?> value, Map<String, Object> params) {
        String paramName = column + "List";
        PlaceholderParam placeholderParam = getPlaceholderParamName(paramName, params);
        params.put(placeholderParam.getPlaceholderParamName(), value);
        // 参数占位符:column
        return String.format("%s in <foreach collection=\"%s\" open=\"(\" close=\")\" item=\"v\" separator=\",\"> #{v} </foreach>", column, BaseMapperParamConstant.QUERY + ".params.paramMap." + placeholderParam.getPlaceholderParamName());
    }

    public static String in(String column, PlainSelect select, Map<String, Object> params) {
        params.putAll(select.getParams().getParamMap());
        return column + " in " + "(" + select.toSql() + ")";
    }

    public static String notIn(String column, Collection<?> value, Map<String, Object> params) {
        PlaceholderParam placeholderParam = getPlaceholderParamName(column + "List", params);
        params.put(placeholderParam.getPlaceholderParamName(), value);
        // 参数占位符:column
        return String.format("%s not in <foreach collection=\"%s\" open=\"(\" close=\")\" item=\"v\" separator=\",\"> #{v} </foreach>", column, BaseMapperParamConstant.QUERY + ".params.paramMap." + placeholderParam.getPlaceholderParamName());
    }

    public static String notIn(String column, PlainSelect select, Map<String, Object> params) {
        params.putAll(select.getParams().getParamMap());
        return column + " not in " + "(" + select.toSql() + ")";
    }

    public static String like(String column, Object value, Map<String, Object> params) {
        PlaceholderParam placeholderParam = getPlaceholderParamName(column, params);
        params.put(placeholderParam.getPlaceholderParamName(), "%" + value + "%");
        // 参数占位符:column
        return column + " like " + placeholderParam.getPlaceholder();
    }

    public static String notLike(String column, Object value, Map<String, Object> params) {
        PlaceholderParam placeholderParam = getPlaceholderParamName(column, params);
        params.put(placeholderParam.getPlaceholderParamName(), "%" + value + "%");
        // 参数占位符:column
        return column + " not like " + placeholderParam.getPlaceholder();
    }

    public static String leftLike(String column, Object value, Map<String, Object> params) {
        PlaceholderParam placeholderParam = getPlaceholderParamName(column, params);
        params.put(placeholderParam.getPlaceholderParamName(), "%" + value);
        // 参数占位符:column
        return column + " like " + placeholderParam.getPlaceholder();
    }

    public static String notLeftLike(String column, Object value, Map<String, Object> params) {
        PlaceholderParam placeholderParam = getPlaceholderParamName(column, params);
        params.put(placeholderParam.getPlaceholderParamName(), "%" + value);
        // 参数占位符:column
        return column + " not like " + placeholderParam.getPlaceholder();
    }

    public static String rightLike(String column, Object value, Map<String, Object> params) {
        PlaceholderParam placeholderParam = getPlaceholderParamName(column, params);
        params.put(placeholderParam.getPlaceholderParamName(), value + "%");
        // 参数占位符:column
        return column + " like " + placeholderParam.getPlaceholder();
    }

    public static String notRightLike(String column, Object value, Map<String, Object> params) {
        PlaceholderParam placeholderParam = getPlaceholderParamName(column, params);
        params.put(placeholderParam.getPlaceholderParamName(), value + "%");
        // 参数占位符:column
        return column + " not like " + placeholderParam.getPlaceholder();
    }

    public static String isNull(String column) {
        return column + " is null ";
    }

    public static String isNotNull(String column) {
        return column + " is not null ";
    }

    public static String between(String column, Object start, Object end, Map<String, Object> params) {
        PlaceholderParam placeholderParamStart = getPlaceholderParamName(column + "Start", params);
        params.put(placeholderParamStart.getPlaceholderParamName(), start);

        PlaceholderParam placeholderParamEnd = getPlaceholderParamName(column + "End", params);
        params.put(placeholderParamEnd.getPlaceholderParamName(), end);

        // 参数
        params.put(placeholderParamStart.getPlaceholderParamName(), start);
        params.put(placeholderParamEnd.getPlaceholderParamName(), end);
        return column + " between " + placeholderParamStart.getPlaceholder() + " and " + placeholderParamEnd.getPlaceholder();
    }

    public static String notBetween(String column, Object start, Object end, Map<String, Object> params) {
        PlaceholderParam placeholderParamStart = getPlaceholderParamName(column + "Start", params);
        params.put(placeholderParamStart.getPlaceholderParamName(), start);

        PlaceholderParam placeholderParamEnd = getPlaceholderParamName(column + "End", params);
        params.put(placeholderParamEnd.getPlaceholderParamName(), end);

        // 参数
        params.put(placeholderParamStart.getPlaceholderParamName(), start);
        params.put(placeholderParamEnd.getPlaceholderParamName(), end);
        return column + " not between " + placeholderParamStart.getPlaceholder() + " and " + placeholderParamEnd.getPlaceholder();
    }

    public static String exists(PlainSelect select, Map<String, Object> params) {
        params.putAll(select.getParams().getParamMap());
        return " exists " + "(" + select.toSql() + ")";
    }

    public static String notExists(PlainSelect select, Map<String, Object> params) {
        params.putAll(select.getParams().getParamMap());
        return " not exists " + "(" + select.toSql() + ")";
    }

    public static String not() {
        return " not";
    }

    public static String joinColumn(String leftColumn, String rightColumn) {
        return leftColumn + " = " + rightColumn;
    }


    /**
     * 获取占位符参数信息
     * 如果存在多个相同名称的列，比如id = 1 or id = 2，此时组装的sql占位符不应该相同，解决方案，使用下标做区分如，column_0，column_1
     * 从Map中获取column0，如果存在则继续获取column1，直到获取不到时，则使用此参数作为占位符
     *
     * @param column 列名
     * @param params 参数
     * @return 占位符参数对象
     */
    /*private static PlaceholderParam getPlaceholderParamName(String column, Map<String, Object> params) {
        String placeholderParamName = column + "_" + IdGen.getId();

        PlaceholderParam placeholderParam = new PlaceholderParam();
        placeholderParam.setPlaceholder(":" + placeholderParamName);
        placeholderParam.setPlaceholderParamName(placeholderParamName);
        return placeholderParam;
    }*/
    private static PlaceholderParam getPlaceholderParamName(String column, Map<String, Object> params) {
        boolean bool = true;
        int index = 0;
        String placeholderParamName = "";
        while (bool) {
            placeholderParamName = index == 0 ? column : column + "_" + index;
            if (params.get(placeholderParamName) != null) {
                index++;
            } else {
                bool = false;
            }
        }
        PlaceholderParam placeholderParam = new PlaceholderParam();
        // AbstractExpression对象里的params参数，params参数里又有paramMap参数，所以用params.paramMap来引用参数（Mybatis特性）
        placeholderParam.setPlaceholder("#{" + BaseMapperParamConstant.QUERY + ".params.paramMap." + placeholderParamName + "}");
        placeholderParam.setPlaceholderParamName(placeholderParamName);
        return placeholderParam;
    }

    /**
     * 占位符参数
     */
    public static class PlaceholderParam {

        /**
         * 占位符，与占位符参数名称对应，如：:username
         */
        private String placeholder;

        /**
         * 占位符参数名称，如：username
         */
        private String placeholderParamName;

        public String getPlaceholder() {
            return placeholder;
        }

        public void setPlaceholder(String placeholder) {
            this.placeholder = placeholder;
        }

        public String getPlaceholderParamName() {
            return placeholderParamName;
        }

        public void setPlaceholderParamName(String placeholderParamName) {
            this.placeholderParamName = placeholderParamName;
        }

    }

}
