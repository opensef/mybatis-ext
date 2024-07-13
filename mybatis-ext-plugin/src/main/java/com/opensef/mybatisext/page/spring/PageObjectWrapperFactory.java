package com.opensef.mybatisext.page.spring;

import com.opensef.mybatisext.Page;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectionException;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;

/**
 * 支持mybatis直接返回Page对象
 */
public class PageObjectWrapperFactory extends DefaultObjectWrapperFactory {

    @Override
    public boolean hasWrapperFor(Object object) {
        if (object instanceof Page) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
        if (object instanceof Page) {
            return new PageObjectWrapper((Page) object);
        }
        throw new ReflectionException("The DefaultObjectWrapperFactory should never be called to provide an ObjectWrapper.");
    }

}
