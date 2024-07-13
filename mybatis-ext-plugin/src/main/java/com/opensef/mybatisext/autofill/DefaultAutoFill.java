package com.opensef.mybatisext.autofill;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认自动填充
 */
public class DefaultAutoFill implements AutoFillHandler {

    @Override
    public Map<String, Object> getInsertAutoFillPropertyValue() {
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> getUpdateAutoFillPropertyValue() {
        return new HashMap<>();
    }

}
