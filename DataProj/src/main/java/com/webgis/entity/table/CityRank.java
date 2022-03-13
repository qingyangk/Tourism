package com.webgis.entity.table;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("cityrank")
@Data
public class CityRank {
    @TableId(value = "id", type = IdType.AUTO)
    public int id;
    public String city;
    public int hotrank;
    public int comcount;
    public int sccount;
    public double scscore;
}
