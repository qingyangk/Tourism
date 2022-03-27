package com.webgis.entity.Info;

import com.webgis.entity.table.CommentEntity;
import com.webgis.entity.table.ScenicEntity;
import lombok.Data;

import java.util.List;

@Data
public class AllInfo {
    public ScenicEntity scenicEntity;
    public List<CommentEntity> commentEntity;
}
