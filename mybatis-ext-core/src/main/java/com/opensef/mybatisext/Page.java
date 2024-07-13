package com.opensef.mybatisext;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页对象
 *
 * @param <T> 实体类型
 */
public class Page<T> {

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

    /**
     * 数据集合
     */
    private List<T> list = new ArrayList<>();

    public Page() {
    }

    public Page(long pageNum, long pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
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

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "Page{" +
                "pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", total=" + total +
                ", pages=" + pages +
                ", list=" + list +
                '}';
    }

}
