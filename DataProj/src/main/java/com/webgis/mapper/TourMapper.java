package com.webgis.mapper;

import com.webgis.entity.table.*;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TourMapper {

    //几何查询--获取区域内点
    List<ScenicEntity> queryPoint(double xmax, double xmin, double ymax, double ymin);

    List<ScenicEntity> queryAll();

    List<CoScore> coName(String name);

    void deSC(int id);

    void upSH(int id, int hot, double score);

    List<CoPC> coPC(String name);

    void upPC(String name, String sheng, String shi);


}
