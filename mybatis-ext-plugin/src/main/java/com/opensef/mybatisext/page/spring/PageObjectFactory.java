package com.opensef.mybatisext.page.spring;

import com.opensef.mybatisext.Page;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;

/**
 * 支持mybatis直接返回Page对象
 */
public class PageObjectFactory extends DefaultObjectFactory {

    private static final long serialVersionUID = 3263031299998136554L;

    /**
     * 如果类型为Page让其也判断为集合
     */
    public <T> boolean isCollection(Class<T> type) {
        if (type == Page.class) {
            return true;
        }
        return super.isCollection(type);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> T create(Class<T> type) {
        if (type == Page.class) {
            return (T) new Page();
        }
        return create(type, null, null);
    }

}
