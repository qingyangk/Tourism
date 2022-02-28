package com.webgis.entity.Info;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ScenicInfo {
    public String name;
    public String address;
    public String message;

    @JsonProperty("x")
    public double X;
    @JsonProperty("y")
    public double Y;

    public int id;

}
