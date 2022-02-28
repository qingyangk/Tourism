package com.webgis.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SearchEntity {
    /**
     * 当前页数
     */
    @JsonProperty("pageNum")
    public int page;

    /**
     * 页包含信息个数
     */
    @JsonProperty("count")
    public int count;

    /**
     * 关键词
     */
    @JsonProperty("search")
    public String search;
}
