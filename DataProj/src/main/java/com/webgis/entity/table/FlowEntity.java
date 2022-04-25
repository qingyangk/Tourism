package com.webgis.entity.table;

import lombok.Data;

/**
 * 查询客流返回实体
 */
@Data
public class FlowEntity {
    public String destination;
    public int count;
}
