package com.opensef.mybatisext.sqlbuilder;

/**
 * 查询接口
 */
public interface PlainSelect {

    /**
     * 生成sql
     *
     * @return sql
     */
    String toSql();

    /**
     * 获取参数
     *
     * @return 参数
     */
    Params getParams();

}
