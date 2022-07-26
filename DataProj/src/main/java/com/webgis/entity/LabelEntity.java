package com.webgis.entity;

import lombok.Data;

import java.util.List;

@Data
public class LabelEntity {
    public int type;
    public List<String> labels;
    public String region;
}
