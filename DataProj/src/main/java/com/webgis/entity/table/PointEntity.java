package com.webgis.entity.table;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("point")
public class PointEntity {
    public int id;
    public String name;
    public double x;
    public double y;
}
