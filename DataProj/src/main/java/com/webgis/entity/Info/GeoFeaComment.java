package com.webgis.entity.Info;

import com.webgis.entity.table.ComPoint;
import lombok.Data;

@Data
public class GeoFeaComment {
    public String type = "Feature";
    public ComPoint properties;
    public geometry geometry;
}
