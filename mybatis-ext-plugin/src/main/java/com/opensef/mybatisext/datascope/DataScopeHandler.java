package com.opensef.mybatisext.datascope;

/**
 * 数据权限处理器
 */
public interface DataScopeHandler {

    /**
     * 构建数据权限信息
     *
     * @param functionCode 功能编码
     * @return 数据权限集合
     */
    DataScopeInfo create(String functionCode);

}
