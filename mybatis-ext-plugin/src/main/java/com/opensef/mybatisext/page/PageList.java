package com.opensef.mybatisext.page;

import java.util.ArrayList;

public class PageList extends ArrayList<Object> {

    /**
     * 页码
     */
    private long pageNum;

    /**
     * 每页数量
     */
    private long pageSize;

    /**
     * 总数量
     */
    private long total;

    /**
     * 总页数
     */
    private long pages;

    public PageList(long pageNum, long pageSize) {
        super();
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public PageList() {
        super();
    }

    public long getPageNum() {
        return pageNum;
    }

    public void setPageNum(long pageNum) {
        this.pageNum = pageNum;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getPages() {
        return pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }

}
