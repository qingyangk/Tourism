package com.webgis.entity.Info;

import com.webgis.entity.table.PointEntity;
import lombok.Data;


@Data
public class GeoFeaScenic {
    public String type = "Feature";
    public PointEntity properties;
    public geometry geometry;
}
