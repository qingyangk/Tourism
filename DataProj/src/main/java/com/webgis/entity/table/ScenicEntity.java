package com.webgis.entity.table;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 景点信息对应表
 *
 * @author QingYang
 */

@Data
@TableName("info")
public class ScenicEntity {

    @TableField("name")
    public String name;

    @TableField("address")
    public String address;

    @TableField("x")
    public double x;

    @TableField("y")
    public double y;

    @TableField("province")
    public String province;

    @TableField("city")
    public String city;

    @TableField("hot")
    public int hot;

    @TableField("score")
    public double score;

    @TableId(value = "id", type = IdType.AUTO)
    public Integer id;

    public int comrank;

    public String level;

}
