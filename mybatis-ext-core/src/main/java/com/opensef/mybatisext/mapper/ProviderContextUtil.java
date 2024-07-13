package com.opensef.mybatisext.mapper;

import com.opensef.mybatisext.util.MybatisExtUtil;
import org.apache.ibatis.builder.annotation.ProviderContext;

public class ProviderContextUtil {

    /**
     * 获取接口泛型实体类
     *
     * @param context ProviderContext
     * @return 实体类
     */
    public static Class<?> getEntityClass(ProviderContext context) {
        return MybatisExtUtil.getGenericInterface(context.getMapperType());
    }


}
