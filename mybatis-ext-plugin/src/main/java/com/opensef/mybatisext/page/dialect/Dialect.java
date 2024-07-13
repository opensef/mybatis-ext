package com.opensef.mybatisext.page.dialect;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;

/**
 * 方言接口
 */
public interface Dialect {

    /**
     * 生成分页sql
     *
     * @param originalSql 原始sql
     * @return 分页sql
     */
    String toPageSql(String originalSql);

    void setSqlAndParams(MetaObject metaObject, MappedStatement mappedStatement,
                         BoundSql boundSql, long pageNum, long pageSize);

}
