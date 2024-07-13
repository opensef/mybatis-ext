package com.opensef.mybatisext.tenant;

import com.opensef.mybatisext.expression.Condition;
import com.opensef.mybatisext.expression.ConditionDataExpression;

import java.util.List;

/**
 * 多租户处理器
 *
 * @param <T> 租户属性类型
 */
public abstract class TenantHandler<T> {

    /**
     * 不需要添加租户条件的白名单
     */
    private final List<String> whiteList;

    public TenantHandler(List<String> whiteList) {
        this.whiteList = whiteList;
    }

    /**
     * 获取列名
     *
     * @return 租户列名
     */
    public abstract String getTenantColumnName();

    /**
     * 获取列的值（租户id）
     *
     * @return 租户的值
     */
    public abstract T getTenantColumnValue();

    /**
     * 生成表达式
     *
     * @return ConditionDataExpression
     */
    public ConditionDataExpression getExpression() {
        ConditionDataExpression conditionDataExpression = new ConditionDataExpression();
        conditionDataExpression.setCondition(Condition.EQUALS);
        conditionDataExpression.setTableColumn(this.getTenantColumnName());
        // 使用表达式占位符
        conditionDataExpression.setValue("${" + this.getTenantColumnName() + "}");
        return conditionDataExpression;
    }

    /**
     * 是否需要过滤
     *
     * @param id MappedStatement的id
     * @return Boolean
     */
    public Boolean isNeedFilter(String id) {
        boolean bool = false;
        for (String s : whiteList) {
            if (s.contains(".*") && id.startsWith(s.substring(0, s.indexOf(".*")))) {
                bool = true;
                break;
            }
            if (s.equals(id)) {
                bool = true;
            }
        }
        return bool;
    }

}
