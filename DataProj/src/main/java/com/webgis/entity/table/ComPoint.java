package com.webgis.entity.table;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("compoint")
public class ComPoint {
    public String website;
    public String name;
    public String data;
    public String place;
    public double longitude;
    public double latitude;
}
