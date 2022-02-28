package com.webgis.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PageEntity {
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
}
