package com.nowcoder.community.entity;

/**
 * 封装分页相关信息
 */
public class Page {

    // 服务端接受浏览器发送的页面信息

    // 当前页码
    private int current = 1;
    // 每页显示的帖子最大限制
    private int limit = 10;

    // 查询信息（浏览器接受服务端的页面信息）

    // 数据总数（用于计算总页数）
    private int rows;
    // 查询路径（用于复用分页链接）
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1) {                 // 排除 0 和负数的情况
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100) {   // 每页显示帖子上限最小取1最大取100
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0) {                    //
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    // 额外条件

    /**
     * 获取当前页的起始行
     * @return
     */
    public int getOffset() {
        return (current - 1) * limit;
    }

    /**
     * 获取总页数
     * @return
     */
    public int getTotal() {
        return rows % limit == 0 ? rows / limit : rows / limit + 1;
    }

    /**
     * 获取起始页码
     * @return
     */
    public int getFrom() {
        int from = current - 2;     // 起始页码为相对当前页向左偏移 2 页的页码
        return Math.max(from, 1);
    }

    /**
     * 获取结束页码
     * @return
     */
    public int getTo() {
        int to = current + 2;       // 结束页码为相对当前页向右偏移 2 页的页码
        return Math.min(to, getTotal());
    }
}
