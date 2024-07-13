package com.opensef.mybatisext.page.dialect;

import java.util.Map;

public interface DialectRegister {

    /**
     * 注册方言
     *
     * @param dialectMap 方言集合 key:数据库名称 value:方言
     */
    void register(Map<String, Dialect> dialectMap);

}
