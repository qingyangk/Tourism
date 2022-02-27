package com.webgis.entity;

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
@TableName("scenic")
public class ScenicEntity {

    @TableId(value = "Name", type = IdType.AUTO)
    public String name;

    @TableField("address")
    public String address;

    @TableField("X")
    public String x;

    @TableField("Y")
    public String y;

    @TableField("jianjie")
    public String message;

    public int id;

}
