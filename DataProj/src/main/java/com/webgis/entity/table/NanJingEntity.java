package com.webgis.entity.table;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("南京景点")
public class NanJingEntity {
    public Integer id;
    public String name;
    public String city;
    public int hot;
    public double score;
    public int comrank;
    public int scorerank;
    public int hotrank;
    public String label1;
    public String label2;
    public String label3;

}
