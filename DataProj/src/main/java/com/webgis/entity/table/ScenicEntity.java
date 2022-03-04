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
@TableName("Info")
public class ScenicEntity {

    @TableId(value = "Name", type = IdType.AUTO)
    public String name;

    @TableField("address")
    public String address;

    @TableField("X")
    public double x;

    @TableField("Y")
    public double y;

    @TableField("city")
    public String city;

    @TableField("hot")
    public String hot;

    @TableField("price")
    public String score;

    public int id;

}
