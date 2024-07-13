package com.opensef.mybatisext.sqlbuilder;


import com.opensef.mybatisext.sqlbuilder.operator.*;

import java.util.Collection;

/**
 * sql运算符构建器
 */
public class Op {

    public static Operator eq(String column, Object value) {
        return eq(true, column, value);
    }

    public static Operator eq(String column, PlainSelect select) {
        return eq(true, column, select);
    }

    public static Operator eq(boolean condition, String column, Object value) {
        if (condition) {
            return new Eq(column, value);
        }
        return null;
    }

    public static Operator eq(boolean condition, String column, PlainSelect select) {
        if (condition) {
            return new Eq(column, select);
        }
        return null;
    }

    public static Operator notEq(String column, Object value) {
        return notEq(true, column, value);
    }

    public static Operator notEq(String column, PlainSelect select) {
        return notEq(true, column, select);
    }

    public static Operator notEq(boolean condition, String column, Object value) {
        if (condition) {
            return new NotEq(column, value);
        }
        return null;
    }

    public static Operator notEq(boolean condition, String column, PlainSelect select) {
        if (condition) {
            return new NotEq(column, select);
        }
        return null;
    }

    public static Operator gt(String column, Object value) {
        return gt(true, column, value);
    }

    public static Operator gt(String column, PlainSelect select) {
        return gt(true, column, select);
    }

    public static Operator gt(boolean condition, String column, Object value) {
        if (condition) {
            return new Gt(column, value);
        }
        return null;
    }

    public static Operator gt(boolean condition, String column, PlainSelect select) {
        if (condition) {
            return new Gt(column, select);
        }
        return null;
    }

    public static Operator gte(String column, Object value) {
        return gte(true, column, value);
    }

    public static Operator gte(String column, PlainSelect select) {
        return gte(true, column, select);
    }

    public static Operator gte(boolean condition, String column, Object value) {
        if (condition) {
            return new Gte(column, value);
        }
        return null;
    }

    public static Operator gte(boolean condition, String column, PlainSelect select) {
        if (condition) {
            return new Gte(column, select);
        }
        return null;
    }

    public static Operator lt(String column, Object value) {
        return lt(true, column, value);
    }

    public static Operator lt(String column, PlainSelect select) {
        return notEq(true, column, select);
    }

    public static Operator lt(boolean condition, String column, Object value) {
        if (condition) {
            return new Lt(column, value);
        }
        return null;
    }

    public static Operator lt(boolean condition, String column, PlainSelect select) {
        if (condition) {
            return new Lt(column, select);
        }
        return null;
    }

    public static Operator lte(String column, Object value) {
        return lte(true, column, value);
    }

    public static Operator lte(String column, PlainSelect select) {
        return lte(true, column, select);
    }

    public static Operator lte(boolean condition, String column, Object value) {
        if (condition) {
            return new Lte(column, value);
        }
        return null;
    }

    public static Operator lte(boolean condition, String column, PlainSelect select) {
        if (condition) {
            return new Lte(column, select);
        }
        return null;
    }

    public static Operator in(String column, Collection<?> value) {
        return in(true, column, value);
    }

    public static Operator in(String column, PlainSelect select) {
        return in(true, column, select);
    }

    public static Operator in(boolean condition, String column, Collection<?> value) {
        if (condition) {
            return new In(column, value);
        }
        return null;
    }

    public static Operator in(boolean condition, String column, PlainSelect select) {
        if (condition) {
            return new In(column, select);
        }
        return null;
    }

    public static Operator not() {
        return new Not();
    }

    public static Operator notIn(String column, Collection<?> value) {
        return notIn(true, column, value);
    }

    public static Operator notIn(String column, PlainSelect select) {
        return notIn(true, column, select);
    }

    public static Operator notIn(boolean condition, String column, Collection<?> value) {
        if (condition) {
            return new NotIn(column, value);
        }
        return null;
    }

    public static Operator notIn(boolean condition, String column, PlainSelect select) {
        if (condition) {
            return new NotIn(column, select);
        }
        return null;
    }

    public static Operator like(String column, Object value) {
        return like(true, column, value);
    }

    public static Operator like(String column, PlainSelect select) {
        return like(true, column, select);
    }

    public static Operator like(boolean condition, String column, Object value) {
        if (condition) {
            return new Like(column, value);
        }
        return null;
    }

    public static Operator like(boolean condition, String column, PlainSelect select) {
        if (condition) {
            return new Like(column, select);
        }
        return null;
    }

    public static Operator notLike(String column, Object value) {
        return notLike(true, column, value);
    }

    public static Operator notLike(String column, PlainSelect select) {
        return notLike(true, column, select);
    }

    public static Operator notLike(boolean condition, String column, Object value) {
        if (condition) {
            return new NotLike(column, value);
        }
        return null;
    }

    public static Operator notLike(boolean condition, String column, PlainSelect select) {
        if (condition) {
            return new NotLike(column, select);
        }
        return null;
    }

    public static Operator leftLike(String column, Object value) {
        return leftLike(true, column, value);
    }

    public static Operator leftLike(String column, PlainSelect select) {
        return leftLike(true, column, select);
    }

    public static Operator leftLike(boolean condition, String column, Object value) {
        if (condition) {
            return new LeftLike(column, value);
        }
        return null;
    }

    public static Operator leftLike(boolean condition, String column, PlainSelect select) {
        if (condition) {
            return new LeftLike(column, select);
        }
        return null;
    }

    public static Operator notLeftLike(String column, Object value) {
        return notLeftLike(true, column, value);
    }

    public static Operator notLeftLike(String column, PlainSelect select) {
        return notLeftLike(true, column, select);
    }

    public static Operator notLeftLike(boolean condition, String column, Object value) {
        if (condition) {
            return new NotLeftLike(column, value);
        }
        return null;
    }

    public static Operator notLeftLike(boolean condition, String column, PlainSelect select) {
        if (condition) {
            return new RightLike(column, select);
        }
        return null;
    }

    public static Operator rightLike(String column, Object value) {
        return rightLike(true, column, value);
    }

    public static Operator rightLike(String column, PlainSelect select) {
        return rightLike(true, column, select);
    }

    public static Operator rightLike(boolean condition, String column, Object value) {
        if (condition) {
            return new RightLike(column, value);
        }
        return null;
    }

    public static Operator rightLike(boolean condition, String column, PlainSelect select) {
        if (condition) {
            return new RightLike(column, select);
        }
        return null;
    }

    public static Operator notRightLike(String column, Object value) {
        return notRightLike(true, column, value);
    }

    public static Operator notRightLike(String column, PlainSelect select) {
        return notRightLike(true, column, select);
    }

    public static Operator notRightLike(boolean condition, String column, Object value) {
        if (condition) {
            return new NotRightLike(column, value);
        }
        return null;
    }

    public static Operator notRightLike(boolean condition, String column, PlainSelect select) {
        if (condition) {
            return new NotRightLike(column, select);
        }
        return null;
    }

    public static Operator isNull(String column) {
        return isNull(true, column);
    }

    public static Operator isNull(boolean condition, String column) {
        if (condition) {
            return new IsNull(column);
        }
        return null;
    }

    public static Operator isNotNull(String column) {
        return isNotNull(true, column);
    }

    public static Operator isNotNull(boolean condition, String column) {
        if (condition) {
            return new IsNotNull(column);
        }
        return null;
    }

    public static Operator between(String column, Object startValue, Object endValue) {
        return between(true, column, startValue, endValue);
    }

    public static Operator between(boolean condition, String column, Object startValue, Object endValue) {
        if (condition) {
            return new Between(column, startValue, endValue);
        }
        return null;
    }

    public static Operator notBetween(String column, Object startValue, Object endValue) {
        return notBetween(true, column, startValue, endValue);
    }

    public static Operator notBetween(boolean condition, String column, Object startValue, Object endValue) {
        if (condition) {
            return new NotBetween(column, startValue, endValue);
        }
        return null;
    }

    public static Operator exists(PlainSelect select) {
        return exists(true, select);
    }

    public static Operator exists(boolean condition, PlainSelect select) {
        if (condition) {
            return new Exists(select);
        }
        return null;
    }

    public static Operator notExists(PlainSelect select) {
        return notExists(true, select);
    }

    public static Operator notExists(boolean condition, PlainSelect select) {
        if (condition) {
            return new NotExists(select);
        }
        return null;
    }

    public static Operator joinColumn(String leftColumn, String rightColumn) {
        return new JoinColumn(leftColumn, rightColumn);
    }

}
