package com.webgis.entity.table;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("compc")
@Data
public class CoPC {
    public String name;
    public String province;
    public String city;
}
