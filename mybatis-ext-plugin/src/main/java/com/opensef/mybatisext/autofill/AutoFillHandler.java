package com.opensef.mybatisext.autofill;

import java.util.Map;

/**
 * 自动填充处理器
 */
public interface AutoFillHandler {

    /**
     * 获取保存时需要自动填充的属性及属性值
     *
     * @return Map
     */
    Map<String, Object> getInsertAutoFillPropertyValue();

    /**
     * 获取修改时需要自动填充的属性及属性值
     *
     * @return Map
     */
    Map<String, Object> getUpdateAutoFillPropertyValue();

    /**
     * 逻辑删除-正常值
     *
     * @return 逻辑删除-正常值
     */
    default Object logicDeletedNormalValue() {
        return false;
    }

    /**
     * 逻辑删除-删除值
     *
     * @return 逻辑删除-删除值
     */
    default Object logicDeletedValue() {
        return true;
    }

}
