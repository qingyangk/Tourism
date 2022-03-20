package com.webgis.entity.Info;

import com.webgis.entity.table.PointEntity;
import lombok.Data;

import java.util.List;

@Data
public class features {
    public String type = "Feature";
    public PointEntity properties;
    public geometry geometry;
}
