package com.opensef.mybatisext.datascope;

/**
 * 数据权限信息工具类
 */
public class DataScopeUtil {

    private static final ThreadLocal<DataScopeInfo> THREAD_LOCAL = new ThreadLocal<>();

    public static void set(DataScopeInfo dataScopeInfo) {
        THREAD_LOCAL.set(dataScopeInfo);
    }

    public static DataScopeInfo get() {
        return THREAD_LOCAL.get();
    }

    public static void clear() {
        THREAD_LOCAL.remove();
    }

}
