package com.webgis.entity.Info;

import com.webgis.entity.table.ScenicEntity;
import lombok.Data;

import java.util.List;

@Data
public class PointInfo {
    public List<ScenicEntity> scen;
    public int id;
    public int x;
    public int y;
}
