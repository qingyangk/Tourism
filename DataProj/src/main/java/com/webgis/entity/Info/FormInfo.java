package com.webgis.entity.Info;

import com.webgis.entity.ScenicEntity;
import lombok.Data;

import java.util.List;

@Data
public class FormInfo {
    public long pages;
    public long total;
    public List<ScenicEntity> scInfo;
}
