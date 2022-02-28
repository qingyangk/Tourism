package com.webgis.mapper;

import com.webgis.entity.Info.FormInfo;
import com.webgis.entity.ScenicEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TourMapper {
    /**
     * 查询前1000条数据
     */
    List<ScenicEntity> get1000();
}
