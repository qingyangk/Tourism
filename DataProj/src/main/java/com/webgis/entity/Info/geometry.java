package com.webgis.entity.Info;

import lombok.Data;

@Data
public class geometry {
    public String type = "Point";
    public double coordinates[];
}
