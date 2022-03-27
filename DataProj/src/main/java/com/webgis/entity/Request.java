package com.webgis.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Request {
    @JsonProperty("model")
    public String model;
}
