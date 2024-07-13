package com.opensef.mybatisext.sqlbuilder;

import com.opensef.mybatisext.exception.MybatisExtException;
import com.opensef.mybatisext.mapper.FunctionReflectionUtil;
import com.opensef.mybatisext.mapper.SerializableFunction;
import com.opensef.mybatisext.sqlbuilder.operator.Operator;
import com.opensef.mybatisext.util.ExtStringUtil;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 抽象表达式
 *
 * @param <T>          实体类型
 * @param <Children>   子类
 * @param <ColumnType> 属性列的类型
 */
public abstract class AbstractExpression<T, Children, ColumnType> implements OpExpression<Children, ColumnType>, IExpression {

    // 强制转换为子类
    @SuppressWarnings("unchecked")
    private final Children children = (Children) this;

    // sql
    protected final StringBuilder sql = new StringBuilder();

    // 参数
    protected Params params;

    public AbstractExpression() {
        params = Params.newInstance();
    }

    public AbstractExpression(Params params) {
        this.params = params;
    }

    /**
     * 解析表达式
     *
     * @param operators     表达式
     * @param logicOperator 逻辑运算符
     * @return 子类
     */
    protected Children analysisExpression(List<Operator> operators, SqlLogicOperator logicOperator) {
        if (operators.size() == 0) {
            return children;
        }
        // 解析表达式
        for (Operator operator : operators) {
            // 第一个表达式不加逻辑运算符
            if (sql.length() > 0) {
                sql.append(logicOperator.getCode());
            }
            sql.append(operator.toSql(params));
        }
        return children;
    }

    protected Children analysisExpression(Operator operator) {
        if (operator != null) {
            // 第一个表达式不加逻辑运算符
            if (sql.length() > 0) {
                sql.append(SqlLogicOperator.AND.getCode());
            }
            sql.append(operator.toSql(params));
        }
        return children;
    }

    /**
     * 解析列名
     *
     * @param column 泛型列名，支持String或Function
     * @return 实际列名
     */
    @SuppressWarnings("unchecked")
    protected String getTableColumnName(ColumnType column) {
        String tableColumn;
        if (column instanceof SerializableFunction<?, ?>) {
            SerializableFunction<T, ?> serializableFunction = (SerializableFunction<T, ?>) column;
            tableColumn = FunctionReflectionUtil.getTableColumnName(serializableFunction);
        } else if (column instanceof String) {
            tableColumn = (String) column;
        } else {
            throw new MybatisExtException("不支持的类型");
        }
        return tableColumn;
    }

    @SuppressWarnings("unchecked")
    protected String getTableColumnName(String alias, ColumnType column) {
        String tableColumn;
        if (column instanceof SerializableFunction<?, ?>) {
            SerializableFunction<T, ?> serializableFunction = (SerializableFunction<T, ?>) column;
            tableColumn = FunctionReflectionUtil.getTableColumnName(serializableFunction);
        } else if (column instanceof String) {
            tableColumn = (String) column;
        } else {
            throw new MybatisExtException("不支持的类型");
        }
        if (ExtStringUtil.hasText(alias)) {
            tableColumn = alias + "." + tableColumn;
        }
        return tableColumn;
    }

    @Override
    public Children eq(ColumnType column, Object value) {
        return eq(true, column, value);
    }

    @Override
    public Children eq(ColumnType column, PlainSelect select) {
        return eq(true, column, select);
    }

    @Override
    public Children eq(boolean condition, ColumnType column, Object value) {
        return analysisExpression(Op.eq(condition, getTableColumnName(column), value));
    }

    @Override
    public Children eq(boolean condition, ColumnType column, PlainSelect select) {
        return analysisExpression(Op.eq(condition, getTableColumnName(column), select));
    }

    @Override
    public Children notEq(ColumnType column, Object value) {
        return notEq(true, column, value);
    }

    @Override
    public Children notEq(ColumnType column, PlainSelect select) {
        return notEq(true, column, select);
    }

    @Override
    public Children notEq(boolean condition, ColumnType column, Object value) {
        return analysisExpression(Op.notEq(condition, getTableColumnName(column), value));
    }

    @Override
    public Children notEq(boolean condition, ColumnType column, PlainSelect select) {
        return analysisExpression(Op.notEq(condition, getTableColumnName(column), select));
    }

    @Override
    public Children gt(ColumnType column, Object value) {
        return gt(true, column, value);
    }

    @Override
    public Children gt(ColumnType column, PlainSelect select) {
        return gt(true, column, select);
    }

    @Override
    public Children gt(boolean condition, ColumnType column, Object value) {
        return analysisExpression(Op.gt(condition, getTableColumnName(column), value));
    }

    @Override
    public Children gt(boolean condition, ColumnType column, PlainSelect select) {
        return analysisExpression(Op.gt(condition, getTableColumnName(column), select));
    }

    @Override
    public Children gte(ColumnType column, Object value) {
        return gte(true, column, value);
    }

    @Override
    public Children gte(ColumnType column, PlainSelect select) {
        return gte(true, column, select);
    }

    @Override
    public Children gte(boolean condition, ColumnType column, Object value) {
        return analysisExpression(Op.gte(condition, getTableColumnName(column), value));
    }

    @Override
    public Children gte(boolean condition, ColumnType column, PlainSelect select) {
        return analysisExpression(Op.gte(condition, getTableColumnName(column), select));
    }

    @Override
    public Children lt(ColumnType column, Object value) {
        return lt(true, column, value);
    }

    @Override
    public Children lt(ColumnType column, PlainSelect select) {
        return lt(true, column, select);
    }

    @Override
    public Children lt(boolean condition, ColumnType column, Object value) {
        return analysisExpression(Op.lt(condition, getTableColumnName(column), value));
    }

    @Override
    public Children lt(boolean condition, ColumnType column, PlainSelect select) {
        return analysisExpression(Op.lt(condition, getTableColumnName(column), select));
    }

    @Override
    public Children lte(ColumnType column, Object value) {
        return lte(true, column, value);
    }

    @Override
    public Children lte(ColumnType column, PlainSelect select) {
        return lte(true, column, select);
    }

    @Override
    public Children lte(boolean condition, ColumnType column, Object value) {
        return analysisExpression(Op.lte(condition, getTableColumnName(column), value));
    }

    @Override
    public Children lte(boolean condition, ColumnType column, PlainSelect select) {
        return analysisExpression(Op.lte(condition, getTableColumnName(column), select));
    }

    @Override
    public Children in(ColumnType column, Collection<?> value) {
        return in(true, column, value);
    }

    @Override
    public Children in(ColumnType column, PlainSelect select) {
        return in(true, column, select);
    }

    @Override
    public Children in(boolean condition, ColumnType column, Collection<?> value) {
        return analysisExpression(Op.in(condition, getTableColumnName(column), value));
    }

    @Override
    public Children in(boolean condition, ColumnType column, PlainSelect select) {
        return analysisExpression(Op.in(condition, getTableColumnName(column), select));
    }

    @Override
    public Children notIn(ColumnType column, Collection<?> value) {
        return notIn(true, column, value);
    }

    @Override
    public Children notIn(ColumnType column, PlainSelect select) {
        return notIn(true, column, select);
    }

    @Override
    public Children notIn(boolean condition, ColumnType column, Collection<?> value) {
        return analysisExpression(Op.notIn(condition, getTableColumnName(column), value));
    }

    @Override
    public Children notIn(boolean condition, ColumnType column, PlainSelect select) {
        return analysisExpression(Op.notIn(condition, getTableColumnName(column), select));
    }

    @Override
    public Children like(ColumnType column, Object value) {
        return like(true, column, value);
    }

    @Override
    public Children like(ColumnType column, PlainSelect select) {
        return like(true, column, select);
    }

    @Override
    public Children like(boolean condition, ColumnType column, Object value) {
        return analysisExpression(Op.like(condition, getTableColumnName(column), value));
    }

    @Override
    public Children like(boolean condition, ColumnType column, PlainSelect select) {
        return analysisExpression(Op.like(condition, getTableColumnName(column), select));
    }

    @Override
    public Children notLike(ColumnType column, Object value) {
        return notLike(true, column, value);
    }

    @Override
    public Children notLike(ColumnType column, PlainSelect select) {
        return notLike(true, column, select);
    }

    @Override
    public Children notLike(boolean condition, ColumnType column, Object value) {
        return analysisExpression(Op.notLike(condition, getTableColumnName(column), value));
    }

    @Override
    public Children notLike(boolean condition, ColumnType column, PlainSelect select) {
        return analysisExpression(Op.notLike(condition, getTableColumnName(column), select));
    }

    @Override
    public Children leftLike(ColumnType column, Object value) {
        return leftLike(true, column, value);
    }

    @Override
    public Children leftLike(ColumnType column, PlainSelect select) {
        return leftLike(true, column, select);
    }

    @Override
    public Children leftLike(boolean condition, ColumnType column, Object value) {
        return analysisExpression(Op.leftLike(condition, getTableColumnName(column), value));
    }

    @Override
    public Children leftLike(boolean condition, ColumnType column, PlainSelect select) {
        return analysisExpression(Op.leftLike(condition, getTableColumnName(column), select));
    }

    @Override
    public Children notLeftLike(ColumnType column, Object value) {
        return notLeftLike(true, column, value);
    }

    @Override
    public Children notLeftLike(ColumnType column, PlainSelect select) {
        return notLeftLike(true, column, select);
    }

    @Override
    public Children notLeftLike(boolean condition, ColumnType column, Object value) {
        return analysisExpression(Op.notLeftLike(condition, getTableColumnName(column), value));
    }

    @Override
    public Children notLeftLike(boolean condition, ColumnType column, PlainSelect select) {
        return analysisExpression(Op.notLeftLike(condition, getTableColumnName(column), select));
    }

    @Override
    public Children rightLike(ColumnType column, Object value) {
        return rightLike(true, column, value);
    }

    @Override
    public Children rightLike(ColumnType column, PlainSelect select) {
        return rightLike(true, column, select);
    }

    @Override
    public Children rightLike(boolean condition, ColumnType column, Object value) {
        return analysisExpression(Op.rightLike(condition, getTableColumnName(column), value));
    }

    @Override
    public Children rightLike(boolean condition, ColumnType column, PlainSelect select) {
        return analysisExpression(Op.rightLike(condition, getTableColumnName(column), select));
    }

    @Override
    public Children notRightLike(ColumnType column, Object value) {
        return notRightLike(true, column, value);
    }

    @Override
    public Children notRightLike(ColumnType column, PlainSelect select) {
        return notRightLike(true, column, select);
    }

    @Override
    public Children notRightLike(boolean condition, ColumnType column, Object value) {
        return analysisExpression(Op.notRightLike(condition, getTableColumnName(column), value));
    }

    @Override
    public Children notRightLike(boolean condition, ColumnType column, PlainSelect select) {
        return analysisExpression(Op.notRightLike(condition, getTableColumnName(column), select));
    }

    @Override
    public Children isNull(ColumnType column) {
        return analysisExpression(Op.isNull(getTableColumnName(column)));
    }

    @Override
    public Children isNull(boolean condition, ColumnType column) {
        return analysisExpression(Op.isNull(condition, getTableColumnName(column)));
    }

    @Override
    public Children isNotNull(ColumnType column) {
        return analysisExpression(Op.isNotNull(getTableColumnName(column)));
    }

    @Override
    public Children isNotNull(boolean condition, ColumnType column) {
        return analysisExpression(Op.isNotNull(condition, getTableColumnName(column)));
    }

    @Override
    public Children between(ColumnType column, Object startValue, Object endValue) {
        return analysisExpression(Op.between(getTableColumnName(column), startValue, endValue));
    }

    @Override
    public Children between(boolean condition, ColumnType column, Object startValue, Object endValue) {
        return analysisExpression(Op.between(condition, getTableColumnName(column), startValue, endValue));
    }

    @Override
    public Children notBetween(ColumnType column, Object startValue, Object endValue) {
        return analysisExpression(Op.notBetween(getTableColumnName(column), startValue, endValue));
    }

    @Override
    public Children notBetween(boolean condition, ColumnType column, Object startValue, Object endValue) {
        return analysisExpression(Op.notBetween(condition, getTableColumnName(column), startValue, endValue));
    }

    @Override
    public Children exists(PlainSelect select) {
        return analysisExpression(Op.exists(select));
    }

    @Override
    public Children exists(boolean condition, PlainSelect select) {
        return analysisExpression(Op.exists(condition, select));
    }

    @Override
    public Children notExists(PlainSelect select) {
        return analysisExpression(Op.notExists(select));
    }

    @Override
    public Children notExists(boolean condition, PlainSelect select) {
        return analysisExpression(Op.notExists(condition, select));
    }

    @Override
    public Children not() {
        return analysisExpression(Op.not());
    }

    @Override
    public Children joinColumn(String leftColumn, String rightColumn) {
        return analysisExpression(Op.joinColumn(leftColumn, rightColumn));
    }

    @Override
    public Children sql(String sql) {
        this.sql.append(" ").append(sql);
        return children;
    }

    @Override
    public Children sql(String sql, Map<String, Object> params) {
        this.sql.append(" ").append(sql);
        this.params.addParams(params);
        return children;
    }

    @Override
    public String toSql() {
        return sql.toString();
    }

    @Override
    public Params getParams() {
        return params;
    }

}
