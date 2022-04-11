package com.webgis.entity.table;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 评论表实体
 */
@Data
@TableName("comment")
public class CommentEntity {
    public Integer id;
    public String website;
    public String scenicName;
    public String author;

    @TableField("release_time")
    public String releaseTime;

    public String authorPlace;
    public double score;
    public String content;
    public String province;
    public String city;
    public double longitude;
    public double latitude;

}
