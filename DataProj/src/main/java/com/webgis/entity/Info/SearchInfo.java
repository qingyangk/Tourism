package com.webgis.entity.Info;

import com.webgis.entity.ScenicEntity;
import lombok.Data;

import java.util.List;

@Data
public class SearchInfo {
    public int pages;
    public int total;
    public List<ScenicInfo> ScInfo;
}
