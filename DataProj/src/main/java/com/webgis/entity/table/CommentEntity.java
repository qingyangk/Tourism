package com.webgis.entity.table;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 评论表实体
 */
@Data
@TableName("comment")
public class CommentEntity {
    public String website;
    public String name;
    public String author;
    public Date date;
    public String place;
    public double score;
    public String pinglun;
    public String biaoshi;
    public String province;
    public String city;
    public double longitude;
    public double latitude;

}
