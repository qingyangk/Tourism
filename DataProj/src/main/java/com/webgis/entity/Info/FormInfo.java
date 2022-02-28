package com.webgis.entity.Info;

import lombok.Data;

import java.util.List;

@Data
public class FormInfo {
    public int pages;
    public int total;
    public List<ScenicInfo> scInfo;
}
