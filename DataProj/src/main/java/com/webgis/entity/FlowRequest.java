package com.webgis.entity;

import lombok.Data;

@Data
public class FlowRequest {
    //城市或是省份
    public String model;
    //传入省份还是城市:0为省
    public int type;
    //路径是流入还是流出:0为流出
    public int path;

}
