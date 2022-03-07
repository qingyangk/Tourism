package com.webgis.entity.table;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("游记")
public class TravelEntity {
    public String region;
    public String title;
    public String content;
    public String daycount;
    public String figure;
    public String price;
}
