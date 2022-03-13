package com.webgis.entity.Info;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class RankInfo {
    public String name;
    public String city;
    public int hot;
    public double score;
    public int comrank;
}
