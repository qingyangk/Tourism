package com.webgis.entity;

import lombok.Data;

@Data
public class Recommend {
    public String city;
    public long time;
    public String type;
    //数据源
    public String datasouse;
}
