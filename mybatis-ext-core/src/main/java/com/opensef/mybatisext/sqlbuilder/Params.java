package com.opensef.mybatisext.sqlbuilder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 参数对象
 */
public class Params {

    private Params() {
    }

    private final Map<String, Object> paramMap = new LinkedHashMap<>();

    public static Params newInstance() {
        return new Params();
    }

    public Params addParam(String param, Object value) {
        paramMap.put(param, value);
        return this;
    }

    public Params addParams(Map<String, Object> paramMap) {
        this.paramMap.putAll(paramMap);
        return this;
    }

    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    @Override
    public String toString() {
        return "Params{" +
                "paramMap=" + paramMap +
                '}';
    }

}
