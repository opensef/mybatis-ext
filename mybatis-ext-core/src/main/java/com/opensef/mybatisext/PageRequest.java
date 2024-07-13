package com.opensef.mybatisext;

/**
 * 分页请求参数
 */
public class PageRequest {

    /**
     * 页码-属性名称
     */
    public static final String FIELD_NAME_PAGE_NUM = "pageNum";

    /**
     * 每页数量-属性名称
     */
    public static final String FIELD_NAME_PAGE_SIZE = "pageSize";

    /**
     * 页码
     */
    private final long pageNum;

    /**
     * 每页数量
     */
    private final long pageSize;

    public PageRequest(long pageNum, long pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public static PageRequest of(long pageNum, long pageSize) {
        return new PageRequest(pageNum, pageSize);
    }

    public long getPageNum() {
        return pageNum;
    }

    public long getPageSize() {
        return pageSize;
    }

}
