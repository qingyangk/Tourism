package com.webgis.mapper;

import com.webgis.entity.Info.FormInfo;
import com.webgis.entity.ScenicEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TourMapper {

    List<ScenicEntity> get1000();

    List<ScenicEntity> getForm(int page, int count,int jisuan);

    List<ScenicEntity> searchForm(int page, int count,int jisuan, String model);
}
