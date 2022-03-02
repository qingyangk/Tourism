package com.webgis.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("point")
public class PointEntity {
    public int id;
    public double x;
    public double y;
}
