package com.opensef.mybatisext.page;

public class PageUtil {

    /**
     * 获取总页数
     *
     * @param pageSize 每页数量
     * @param total    总数量
     * @return 总页数
     */
    public static Long getTotalPage(Long pageSize, Long total) {
        if (pageSize <= 0) {
            return 0L;
        }
        return (pageSize + total - 1) / pageSize;
    }

    /**
     * 获取开始行
     *
     * @param pageNum  当前页
     * @param pageSize 每页数量
     * @return 开始行
     */
    public static Long getStartRow(long pageNum, long pageSize) {
        return pageNum > 0 ? (pageNum - 1) * pageSize : 0;
    }

}
