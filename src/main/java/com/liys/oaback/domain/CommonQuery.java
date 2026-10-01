package com.liys.oaback.domain;

public class CommonQuery {


    private String name;
    private Integer page = 1;
    private Integer pageSize = 10;
    private String sortBy;
    private String sortOrder;

    /**
     * Calculates the offset value based on the current page number and page size.
     * The offset is used to determine the starting point for data retrieval in pagination.
     *
     * @return The calculated offset value as an Integer
     */
    public Long getOffset() {
        return ((long) getPage() - 1) * getPageSize();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPage() {
        return page == null || page < 1 ? 1 : page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 100);
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    public String getSortOrder() { return sortOrder; }
    public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }

}
