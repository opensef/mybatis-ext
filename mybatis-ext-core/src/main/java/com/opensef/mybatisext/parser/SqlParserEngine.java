package com.opensef.mybatisext.parser;

import com.opensef.mybatisext.exception.SqlParseExpression;
import com.opensef.mybatisext.expression.Condition;
import com.opensef.mybatisext.util.ExtStringUtil;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * 数据权限sql解析引擎
 */
public class SqlParserEngine {

    public static void main(String[] args) throws Exception {
        // from后面有可能是子查询的虚拟table
        String sql = "select * from (SELECT a.col1,a.col2, b.col2 FROM tableA a,tableB b WHERE a.id = b.id) t where t.a = 11";
        String sql1 = "select * from tableC c left join (SELECT a.col1,a.col2, b.col2 FROM tableA a,tableB b WHERE a.id = b.id) t on t.id = c.tid where t.a = 11";
        String sql2 = "SELECT a.col1,a.col2, b.col3 FROM tableA a, tableB b, table c where a.id = b.id and b.id = c.id";
        String sql3 = "SELECT a.col1,a.col2, b.col3 FROM tableA a left join tableB b on a.id = b.id and a.age > 15";
        String sql4 = "SELECT a.col1,a.col2, b.col3 FROM tableA a left join tableB b on a.id = b.id where a.name like '%dd%'";
        String sql5 = "SELECT a,b,c FROM tableA where id in (select id from tableB where name = '') and sex in (12,13,14) and age > 15 and (name = 'aaa' or name = 'bbb')";
        String sql5a = "SELECT a,b,c FROM tableA where id > (select id from tableB where name = '')";
        String sql5b = "SELECT * FROM gs_sms WHERE id IN (1,2,(select id from gs_area))";
        String sql6 = "SELECT tableA.* FROM tableA inner join tableB ON tableA.id = tableB.aid"; // 没有别名
        String sql7 = "SELECT tableA.* FROM tableA inner join tableB"; // 没有别名，没有On条件
        String sql8 = "SELECT tableA.* FROM tableA inner join tableB join tableC";
        String sql9 = "update tableA set dd = ? where id = 1";
        String sql9a = "update tableA set dd = ? where id = 1 and sex in (select id from tb_age) and age > 15";
        String sql9b = "update tableA set dd = ? where id = 1;update tableA set dd = ? where id = 2;";
        String sql10 = "update tableA set dd = ? where id = 1;update tableA set dd = ? and id = 2"; // 不支持，需在上层自己处理
        String sql11 = "UPDATE gs_acceptance SET is_deleted = 0 WHERE id IN (SELECT id FROM sys_role)";
        String sql12 = "DELETE FROM gs_acceptance";
        String sql13 = "select now from tableA a";
        String sql14 = "SELECT s.* FROM gs_stock s INNER JOIN (SELECT v.stock_id FROM gs_stock_value v WHERE v.is_deleted = 0 AND CONCAT( v.goods_property_name, '=', v.property_value ) IN ( 1, 2 ) GROUP BY v.stock_id HAVING COUNT(*) >= 1 ) t ON s.id = t.stock_id WHERE s.is_deleted = 0 AND s.state = 1 AND s.base_sku_id = '' AND s.batch_no = '' AND s.purchase_price = '' AND s.retail_price = ''";
        String sql15 = "select * from gs_sms where 1=1 and case when 1=1 then 1=1 else 1=1 end";
        String sql16 = "select * from tableA a where a.age > 15 and a.id in (select id from tableC)";

        // System.out.println(parse(getStatement(sql5), Condition.EQUALS, "tenant_id", "?"));
    }

    /**
     * 给select语句表达式添加字段
     *
     * @param fromItem    FromItem
     * @param plainSelect PlainSelect
     * @param columnName  列名
     * @param value       值
     */
    private static void addSelectExpressionSql(FromItem fromItem, PlainSelect plainSelect, Condition condition, String columnName, String value) {
        // 是否有join
        List<Join> joins = plainSelect.getJoins();
        if (null != joins && joins.size() > 0) {
            // 倒叙排列join，使输出的拼接条件按join表的先后顺序输出
            List<Join> tempJoins = new ArrayList<>(joins);
            Collections.reverse(tempJoins);
            for (Join join : tempJoins) {
                parseJoin(plainSelect, join, condition, columnName, value);
            }
        }

        // where表达式是否存在子查询或join，如果存在则解析
        parseExpression(plainSelect.getWhere(), condition, columnName, value);

        // fromItem是否是子查询，如果是子查询，则在子查询中添加条件表达式（租户或数据权限）即可，子查询外面无需再次添加
        // 最后处理fromItem，保证拼接条件按照原来sql中表的顺序进行排列
        if (fromItem instanceof SubSelect) {
            SubSelect subSelect = (SubSelect) fromItem;
            SelectBody selectBody = subSelect.getSelectBody();
            parseSelectBody(selectBody, condition, columnName, value);
        } else if (fromItem instanceof SubJoin) {
            SubJoin subJoin = (SubJoin) fromItem;
            if (null != subJoin.getJoinList() && subJoin.getJoinList().size() > 0) {
                for (Join join : subJoin.getJoinList()) {
                    parseJoin(plainSelect, join, condition, columnName, value);
                }
            }
        } else if (fromItem instanceof SpecialSubSelect) {
            SpecialSubSelect specialSubSelect = (SpecialSubSelect) fromItem;
            if (null != specialSubSelect.getSubSelect()) {
                parseSelectBody(specialSubSelect.getSubSelect().getSelectBody(), condition, columnName, value);
            }
        } else {
            setWhere(fromItem, plainSelect, condition, columnName, value);
        }
    }

    /**
     * 给select语句表达式添加字段
     *
     * @param fromItem    FromItem
     * @param plainSelect PlainSelect
     * @param sql         新增的sql
     */
    private static void addSelectExpressionSql(FromItem fromItem, PlainSelect plainSelect, String sql) {
        // 是否有join
        List<Join> joins = plainSelect.getJoins();
        if (null != joins && joins.size() > 0) {
            // 倒叙排列join，使输出的拼接条件按join表的先后顺序输出
            List<Join> tempJoins = new ArrayList<>(joins);
            Collections.reverse(tempJoins);
            for (Join join : tempJoins) {
                parseJoin(plainSelect, join, sql);
            }
        }

        // where表达式是否存在子查询或join，如果存在则解析
        parseExpression(plainSelect.getWhere(), sql);

        // fromItem是否是子查询，如果是子查询，则在子查询中添加条件表达式（租户或数据权限）即可，子查询外面无需再次添加
        // 最后处理fromItem，保证拼接条件按照原来sql中表的顺序进行排列
        if (fromItem instanceof SubSelect) {
            SubSelect subSelect = (SubSelect) fromItem;
            SelectBody selectBody = subSelect.getSelectBody();
            parseSelectBody(selectBody, sql);
        } else if (fromItem instanceof SubJoin) {
            SubJoin subJoin = (SubJoin) fromItem;
            if (null != subJoin.getJoinList() && subJoin.getJoinList().size() > 0) {
                for (Join join : subJoin.getJoinList()) {
                    parseJoin(plainSelect, join, sql);
                }
            }
        } else if (fromItem instanceof SpecialSubSelect) {
            SpecialSubSelect specialSubSelect = (SpecialSubSelect) fromItem;
            if (null != specialSubSelect.getSubSelect()) {
                parseSelectBody(specialSubSelect.getSubSelect().getSelectBody(), sql);
            }
        } else {
            setWhere(fromItem, plainSelect, sql);
        }
    }

    private static void parseJoin(PlainSelect plainSelect, Join join, Condition condition, String columnName, String value) {
        if (join.getRightItem() instanceof SubSelect) { // 或join.getRightItem() instanceof Table //查询的表是否是一个table
            SubSelect subSelect = (SubSelect) join.getRightItem();
            PlainSelect subPlainSelect = (PlainSelect) subSelect.getSelectBody();
            addSelectExpressionSql(subPlainSelect.getFromItem(), subPlainSelect, condition, columnName, value);
        } else {
            if (join.isSimple()) {
                // 如果是简写（例如没有ON条件），则将条件添加到where中
                setWhere(join.getRightItem(), plainSelect, condition, columnName, value);
            } else {
                // ON表达式是否存在子查询或join，如果存在则解析
                List<Expression> onExpressions = (List<Expression>) join.getOnExpressions();
                if (null != onExpressions && onExpressions.size() > 0 && null != onExpressions.get(0)) {
                    parseExpression(onExpressions.get(0), condition, columnName, value);
                }

                // 将条件添加到ON语句中
                setOn(join, condition, columnName, value);
            }
        }
    }

    private static void parseJoin(PlainSelect plainSelect, Join join, String sql) {
        if (join.getRightItem() instanceof SubSelect) { // 或join.getRightItem() instanceof Table //查询的表是否是一个table
            SubSelect subSelect = (SubSelect) join.getRightItem();
            PlainSelect subPlainSelect = (PlainSelect) subSelect.getSelectBody();
            addSelectExpressionSql(subPlainSelect.getFromItem(), subPlainSelect, sql);
        } else {
            if (join.isSimple()) {
                // 如果是简写（例如没有ON条件），则将条件添加到where中
                setWhere(join.getRightItem(), plainSelect, sql);
            } else {
                // ON表达式是否存在子查询或join，如果存在则解析
                List<Expression> onExpressions = (List<Expression>) join.getOnExpressions();
                if (null != onExpressions && onExpressions.size() > 0 && null != onExpressions.get(0)) {
                    parseExpression(onExpressions.get(0), sql);
                }

                // 将条件添加到ON语句中
                setOn(join, sql);
            }
        }
    }

    /**
     * 设置select的where条件
     *
     * @param fromItem    FromItem
     * @param plainSelect PlainSelect
     * @param columnName  列名
     * @param value       值
     */
    public static void setWhere(FromItem fromItem, PlainSelect plainSelect, Condition condition, String columnName, String value) {
        StringBuilder appendLeftSql = new StringBuilder();
        if (fromItem.getAlias() != null) {
            appendLeftSql.append(fromItem.getAlias().getName());
            appendLeftSql.append(".");
            appendLeftSql.append(columnName);
        } else {
            // 没有别名则使用表名
            appendLeftSql.append(fromItem);
            appendLeftSql.append(".");
            appendLeftSql.append(columnName);
        }

        Expression expression = makeExpression(condition, appendLeftSql.toString(), value);

        if (null != plainSelect.getWhere()) {
            AndExpression andExpression = new AndExpression(expression, plainSelect.getWhere());
            plainSelect.setWhere(andExpression);
        } else {
            plainSelect.setWhere(expression);
        }
    }

    /**
     * 设置select的where条件
     *
     * @param fromItem    FromItem
     * @param plainSelect PlainSelect
     * @param sql         新增的sql
     */
    public static void setWhere(FromItem fromItem, PlainSelect plainSelect, String sql) {
        if (null != plainSelect.getWhere()) {
            AndExpression andExpression = new AndExpression(new Column(sql), plainSelect.getWhere());
            plainSelect.setWhere(andExpression);
        } else {
            plainSelect.setWhere(new Column(sql));
        }
    }

    /**
     * 设置ON条件
     *
     * @param join       Join
     * @param columnName 列名
     * @param value      值
     */
    private static void setOn(Join join, Condition condition, String columnName, String value) {
        StringBuilder appendLeftSql = new StringBuilder();
        if (join.getRightItem().getAlias() != null) {
            appendLeftSql.append(join.getRightItem().getAlias().getName());
            appendLeftSql.append(".");
            appendLeftSql.append(columnName);
        } else {
            // 没有别名则使用表名
            appendLeftSql.append(join.getRightItem().toString());
            appendLeftSql.append(".");
            appendLeftSql.append(columnName);
        }

        Expression expression = makeExpression(condition, appendLeftSql.toString(), value);

        List<Expression> onExpressions = (List<Expression>) join.getOnExpressions();
        if (null != onExpressions && onExpressions.size() > 0 && null != onExpressions.get(0)) {
            AndExpression andExpression = new AndExpression(expression, onExpressions.get(0));
            join.addOnExpression(andExpression);
        } else {
            join.addOnExpression(expression);
        }
    }

    /**
     * 设置ON条件
     *
     * @param join Join
     * @param sql  新增的sql
     */
    private static void setOn(Join join, String sql) {
        if (null != join.getOnExpressions() && join.getOnExpressions().size() > 0) {
            List<Expression> onExpressions = (List<Expression>) join.getOnExpressions();
            if (null != onExpressions.get(0)) {
                AndExpression andExpression = new AndExpression(new Column(sql), onExpressions.get(0));
                join.addOnExpression(andExpression);
            }
        } else {
            join.addOnExpression(new Column(sql));
        }
    }

    /**
     * 解析where或on后面的表达式
     *
     * @param expression Expression
     * @param columnName 列名
     * @param value      值
     */
    private static void parseExpression(Expression expression, Condition condition, String columnName, String value) {
        if (expression instanceof BinaryExpression) {
            parseExpression(((BinaryExpression) expression).getLeftExpression(), condition, columnName, value);
            parseExpression(((BinaryExpression) expression).getRightExpression(), condition, columnName, value);
        } else if (expression instanceof InExpression) {
            parseExpression(((InExpression) expression).getLeftExpression(), condition, columnName, value);
            ItemsList rightItemsList = ((InExpression) expression).getRightItemsList();
            if (null != rightItemsList) {
                if (rightItemsList instanceof ExpressionList) {
                    ExpressionList expressionList = (ExpressionList) rightItemsList;
                    if (null != expressionList.getExpressions() && expressionList.getExpressions().size() > 0) {
                        for (Expression expressionListExpression : expressionList.getExpressions()) {
                            parseExpression(expressionListExpression, condition, columnName, value);
                        }
                    }
                } else if (rightItemsList instanceof SubSelect) {
                    SubSelect subSelect = (SubSelect) rightItemsList;
                    PlainSelect subPlainSelect = (PlainSelect) subSelect.getSelectBody();
                    addSelectExpressionSql(subPlainSelect.getFromItem(), subPlainSelect, condition, columnName, value);
                } else if (rightItemsList instanceof MultiExpressionList) {
                    MultiExpressionList multiExpressionList = (MultiExpressionList) rightItemsList;
                    if (null != multiExpressionList.getExpressionLists() && multiExpressionList.getExpressionLists().size() > 0) {
                        for (ExpressionList expressionList : multiExpressionList.getExpressionLists()) {
                            if (null != expressionList.getExpressions() && expressionList.getExpressions().size() > 0) {
                                for (Expression expressionListExpression : expressionList.getExpressions()) {
                                    parseExpression(expressionListExpression, condition, columnName, value);
                                }
                            }
                        }
                    }
                } else if (rightItemsList instanceof NamedExpressionList) {
                    NamedExpressionList namedExpressionList = (NamedExpressionList) rightItemsList;
                    if (null != namedExpressionList.getExpressions() && namedExpressionList.getExpressions().size() > 0) {
                        for (Expression namedExpressionListExpression : namedExpressionList.getExpressions()) {
                            parseExpression(namedExpressionListExpression, condition, columnName, value);
                        }
                    }
                }
            }
        } else if (expression instanceof ExistsExpression) {
            parseExpression(((ExistsExpression) expression).getRightExpression(), condition, columnName, value);
        } else if (expression instanceof IsBooleanExpression) {
            parseExpression(((IsBooleanExpression) expression).getLeftExpression(), condition, columnName, value);
        } else if (expression instanceof IsNullExpression) {
            parseExpression(((IsNullExpression) expression).getLeftExpression(), condition, columnName, value);
        } else {
            if (expression instanceof SubSelect) {
                SubSelect subSelect = (SubSelect) expression;
                PlainSelect subPlainSelect = (PlainSelect) subSelect.getSelectBody();
                addSelectExpressionSql(subPlainSelect.getFromItem(), subPlainSelect, condition, columnName, value);
            }
        }
    }

    /**
     * 解析where或on后面的表达式
     *
     * @param expression Expression
     * @param sql        新增的sql
     */
    private static void parseExpression(Expression expression, String sql) {
        if (expression instanceof BinaryExpression) {
            parseExpression(((BinaryExpression) expression).getLeftExpression(), sql);
            parseExpression(((BinaryExpression) expression).getRightExpression(), sql);
        } else if (expression instanceof InExpression) {
            parseExpression(((InExpression) expression).getLeftExpression(), sql);
            ItemsList rightItemsList = ((InExpression) expression).getRightItemsList();
            if (null != rightItemsList) {
                if (rightItemsList instanceof ExpressionList) {
                    ExpressionList expressionList = (ExpressionList) rightItemsList;
                    if (null != expressionList.getExpressions() && expressionList.getExpressions().size() > 0) {
                        for (Expression expressionListExpression : expressionList.getExpressions()) {
                            parseExpression(expressionListExpression, sql);
                        }
                    }
                } else if (rightItemsList instanceof SubSelect) {
                    SubSelect subSelect = (SubSelect) rightItemsList;
                    PlainSelect subPlainSelect = (PlainSelect) subSelect.getSelectBody();
                    addSelectExpressionSql(subPlainSelect.getFromItem(), subPlainSelect, sql);
                } else if (rightItemsList instanceof MultiExpressionList) {
                    MultiExpressionList multiExpressionList = (MultiExpressionList) rightItemsList;
                    if (null != multiExpressionList.getExpressionLists() && multiExpressionList.getExpressionLists().size() > 0) {
                        for (ExpressionList expressionList : multiExpressionList.getExpressionLists()) {
                            if (null != expressionList.getExpressions() && expressionList.getExpressions().size() > 0) {
                                for (Expression expressionListExpression : expressionList.getExpressions()) {
                                    parseExpression(expressionListExpression, sql);
                                }
                            }
                        }
                    }
                } else if (rightItemsList instanceof NamedExpressionList) {
                    NamedExpressionList namedExpressionList = (NamedExpressionList) rightItemsList;
                    if (null != namedExpressionList.getExpressions() && namedExpressionList.getExpressions().size() > 0) {
                        for (Expression namedExpressionListExpression : namedExpressionList.getExpressions()) {
                            parseExpression(namedExpressionListExpression, sql);
                        }
                    }
                }
            }
        } else if (expression instanceof ExistsExpression) {
            parseExpression(((ExistsExpression) expression).getRightExpression(), sql);
        } else if (expression instanceof IsBooleanExpression) {
            parseExpression(((IsBooleanExpression) expression).getLeftExpression(), sql);
        } else if (expression instanceof IsNullExpression) {
            parseExpression(((IsNullExpression) expression).getLeftExpression(), sql);
        } else {
            if (expression instanceof SubSelect) {
                SubSelect subSelect = (SubSelect) expression;
                PlainSelect subPlainSelect = (PlainSelect) subSelect.getSelectBody();
                addSelectExpressionSql(subPlainSelect.getFromItem(), subPlainSelect, sql);
            }
        }
    }

    /**
     * 解析selectBody
     *
     * @param selectBody SelectBody
     * @param columnName 列名
     * @param value      值
     */
    private static void parseSelectBody(SelectBody selectBody, Condition condition, String columnName, String value) {
        if (selectBody instanceof PlainSelect) {
            PlainSelect plainSelect = (PlainSelect) selectBody;
            FromItem fromItem = plainSelect.getFromItem();
            if (null != fromItem) {
                addSelectExpressionSql(fromItem, plainSelect, condition, columnName, value);
            }
        } else if (selectBody instanceof WithItem) {
            WithItem withItem = (WithItem) selectBody;
            parseSelectBody(withItem, condition, columnName, value);
        } else if (selectBody instanceof SetOperationList) {
            SetOperationList setOperationList = (SetOperationList) selectBody;
            if (null != setOperationList.getSelects() && setOperationList.getSelects().size() > 0) {
                for (SelectBody body : setOperationList.getSelects()) {
                    parseSelectBody(body, condition, columnName, value);
                }
            }
        }
    }

    /**
     * 解析selectBody
     *
     * @param selectBody SelectBody
     * @param sql        新增的sql
     */
    private static void parseSelectBody(SelectBody selectBody, String sql) {
        if (selectBody instanceof PlainSelect) {
            PlainSelect plainSelect = (PlainSelect) selectBody;
            FromItem fromItem = plainSelect.getFromItem();
            if (null != fromItem) {
                addSelectExpressionSql(fromItem, plainSelect, sql);
            }
        } else if (selectBody instanceof WithItem) {
            WithItem withItem = (WithItem) selectBody;
            parseSelectBody(withItem, sql);
        } else if (selectBody instanceof SetOperationList) {
            SetOperationList setOperationList = (SetOperationList) selectBody;
            if (null != setOperationList.getSelects() && setOperationList.getSelects().size() > 0) {
                for (SelectBody body : setOperationList.getSelects()) {
                    parseSelectBody(body, sql);
                }
            }
        }
    }

    /**
     * 解析order by sql片段
     *
     * @param selectBody SelectBody
     * @return order by 片段
     */
    public static List<OrderByElement> getOrderBy(SelectBody selectBody) {
        if (selectBody instanceof PlainSelect) {
            PlainSelect plainSelect = (PlainSelect) selectBody;
            return plainSelect.getOrderByElements();
        } else if (selectBody instanceof WithItem) {
            WithItem withItem = (WithItem) selectBody;
            getOrderBy(withItem);
        } else if (selectBody instanceof SetOperationList) {
            SetOperationList setOperationList = (SetOperationList) selectBody;
            if (null != setOperationList.getSelects() && setOperationList.getSelects().size() > 0) {
                for (SelectBody body : setOperationList.getSelects()) {
                    getOrderBy(body);
                }
            }
        }
        return new ArrayList<>();
    }

    /**
     * 生成Statement
     *
     * @param sourceSql sql语句
     * @return Statement
     * @throws JSQLParserException sql解析异常
     */
    public static Statement getStatement(String sourceSql) throws JSQLParserException {
        return CCJSqlParserUtil.parse(sourceSql);
    }

    /**
     * 组装表达式
     * <p>等于、大于等二元表达式及in表达式，value传入的是什么，表达式生成后的依然保持不变，例如：value传入的是${orgIds}，这里不会对“${orgIds}”做任何改变</p>
     * <p>between表达式，传入的value必须是“begin,end”格式，程序会根据英文逗号将其拆分为对应的begin和end表达式</p>
     *
     * @param condition 条件
     * @param column    列名
     * @param value     值
     * @return 表达式
     */
    private static Expression makeExpression(Condition condition, String column, String value) {
        switch (condition) {
            case EQUALS:
                return new EqualsTo(new Column(column), new Column(value));
            case NOT_EQUALS:
                return new NotEqualsTo(new Column(column), new StringValue(value));
            case IN:
                ExpressionList expressionList = new ExpressionList();
                expressionList.withExpressions(List.of(new Column(value)));
                return new InExpression(new Column(column), expressionList);
            case NOT_IN:

            case GREATER_THAN:
                return new GreaterThan().withLeftExpression(new Column(column)).withRightExpression(new StringValue(value));
            case GREATER_THAN_EQUALS:
                return new GreaterThanEquals().withLeftExpression(new Column(column)).withRightExpression(new StringValue(value));
            case LESS_THAN:
                return new MinorThan().withLeftExpression(new Column(column)).withRightExpression(new StringValue(value));
            case LESS_THAN_EQUALS:
                return new MinorThanEquals().withLeftExpression(new Column(column)).withRightExpression(new StringValue(value));
            case LIKE:
            case LEFT_LIKE:
            case RIGHT_LIKE:
                return new LikeExpression().withLeftExpression(new Column(column)).withRightExpression(new StringValue(value));
            case NOT_LIKE:
            case NOT_LEFT_LIKE:
            case NOT_RIGHT_LIKE:
                return new LikeExpression().withLeftExpression(new Column(column)).withRightExpression(new StringValue(value)).withNot(true);
            case BETWEEN:
                String[] betweenSplit = value.split(",");
                if (betweenSplit.length != 2) {
                    throw new SqlParseExpression("between表达式不是以a,b的格式组成的");
                }
                return new Between().withLeftExpression(new Column(column)).withBetweenExpressionStart(new StringValue(betweenSplit[0])).withBetweenExpressionEnd(new StringValue(betweenSplit[1]));
            case NOT_BETWEEN:
                // 去除所有空格
                value = ExtStringUtil.deleteWhitespace(value);
                String[] notBetweenSplit = value.split(",");
                if (notBetweenSplit.length != 2) {
                    throw new SqlParseExpression("between表达式不是以a,b的格式组成的");
                }
                return new Between().withLeftExpression(new Column(column)).withBetweenExpressionStart(new StringValue(notBetweenSplit[0])).withBetweenExpressionEnd(new StringValue(notBetweenSplit[1])).withNot(true);

            default:
                throw new SqlParseExpression("不支持此表达式");
        }
    }

    /**
     * 根据表达式参数增加sql片段
     *
     * @param statement Statement
     * @param condition 条件
     * @param column    列名
     * @param value     值，左右表达式为具体的值，如?、100等，in表达式为?,?,?，between表达式为a,b
     * @return 新sql
     */
    public static String parse(Statement statement, Condition condition, String column, String value) {
        if (statement instanceof Select) {
            Select select = (Select) statement;
            SelectBody selectBody = select.getSelectBody();
            parseSelectBody(selectBody, condition, column, value);

            return statement.toString();
        } else if (statement instanceof Update) { // 暂不支持update join的语法
            Update update = (Update) statement;

            Expression expression = makeExpression(condition, column, value);

            if (null != update.getWhere()) {
                // 将条件添加到where后第一个位置
                AndExpression andExpression = new AndExpression(expression, update.getWhere());
                update.setWhere(andExpression);
            } else {
                update.setWhere(expression);
            }
            // 解析where后面的表达式
            parseExpression(update.getWhere(), condition, column, value);
            return update.toString();
        } else if (statement instanceof Delete) { // 暂不支持delete join的语法
            Delete delete = (Delete) statement;

            Expression expression = makeExpression(condition, column, value);

            if (null != delete.getWhere()) {
                // 将条件添加到where后第一个位置
                AndExpression andExpression = new AndExpression(expression, delete.getWhere());
                delete.setWhere(andExpression);
            } else {
                delete.setWhere(expression);
            }
            // 解析where后面的表达式
            parseExpression(delete.getWhere(), condition, column, value);
            return delete.toString();
        } else {
            return statement.toString();
        }
    }

    /**
     * 根据传入sql增加sql片段
     *
     * @param statement Statement
     * @param sql       sql片段
     * @return 新sql
     */
    public static String parse(Statement statement, String sql) {
        if (statement instanceof Select) {
            Select select = (Select) statement;
            SelectBody selectBody = select.getSelectBody();
            parseSelectBody(selectBody, sql);

            return statement.toString();
        } else if (statement instanceof Update) { // 暂不支持update join的语法
            Update update = (Update) statement;

            if (null != update.getWhere()) {
                // 将条件添加到where后第一个位置
                AndExpression andExpression = new AndExpression(new Column(sql), update.getWhere());
                update.setWhere(andExpression);
            } else {
                update.setWhere(new Column(sql));
            }
            // 解析where后面的表达式
            parseExpression(update.getWhere(), sql);
            return update.toString();
        } else if (statement instanceof Delete) { // 暂不支持delete join的语法
            Delete delete = (Delete) statement;

            if (null != delete.getWhere()) {
                // 将条件添加到where后第一个位置
                AndExpression andExpression = new AndExpression(new Column(sql), delete.getWhere());
                delete.setWhere(andExpression);
            } else {
                delete.setWhere(new Column(sql));
            }
            // 解析where后面的表达式
            parseExpression(delete.getWhere(), sql);
            return delete.toString();
        } else {
            return statement.toString();
        }
    }

}
