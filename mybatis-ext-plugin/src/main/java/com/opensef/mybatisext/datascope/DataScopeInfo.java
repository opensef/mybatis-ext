package com.opensef.mybatisext.datascope;

import com.opensef.mybatisext.expression.DataExpression;

import java.util.List;
import java.util.Map;

/**
 * 数据权限信息
 */
public class DataScopeInfo {

    /**
     * 数据权限表达式
     */
    private List<DataExpression> dataExpressions;

    /**
     * 数据权限参数-值Map
     */
    private Map<String, List<?>> paramValueMap;

    public List<DataExpression> getDataScopeExpressions() {
        return dataExpressions;
    }

    public void setDataScopeExpressions(List<DataExpression> dataExpressions) {
        this.dataExpressions = dataExpressions;
    }

    public Map<String, List<?>> getParamValueMap() {
        return paramValueMap;
    }

    public void setParamValueMap(Map<String, List<?>> paramValueMap) {
        this.paramValueMap = paramValueMap;
    }

    @Override
    public String toString() {
        return "DataScopeInfo{" +
                "dataScopeExpressions=" + dataExpressions +
                ", paramValueMap=" + paramValueMap +
                '}';
    }

}
