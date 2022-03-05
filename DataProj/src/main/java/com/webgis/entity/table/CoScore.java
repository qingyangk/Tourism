package com.webgis.entity.table;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("coscore")
public class CoScore {
    public String name;
    public int score;
}
