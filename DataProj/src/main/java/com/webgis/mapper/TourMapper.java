package com.webgis.mapper;

import com.webgis.entity.table.*;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface TourMapper {

    //几何查询--获取区域内点
    List<ScenicEntity> queryPoint(double xmax, double xmin, double ymax, double ymin);

    //查询景点的所有信息
    List<ScenicEntity> queryAll();

    //查询景点分数及评论个数
    List<CoScore> coName(String name);

    //删除信息不对应的景点
    void deSC(int id);

    //更新景点热度和分数
    void upSH(int id, int hot, double score);

    //查询景点个数
    List<CoPC> coPC(String name);

    //更新评论省、市
    void upPC(String name, String sheng, String shi);

    //获取评论城市列表
    List<Map<String, Object>> cRank();

    //查询某城市有多少条评论
    int cCount(String city);

    //新表插入城市和评论数
    void inCC(int id, String city, int comcount);

    //查询该城市下的景点名及分数
    List<ScenicEntity> cScenicCount(String city);

    //获取城市表城市名
    List<CityRank> getcity();

    //更新城市表的评论数和分数
    void upCityScore(String city, int sccount, double scscore);

    //查询排名前十的城市
    List<CityRank> cityRank();

    //获取点数据
    List<PointEntity> getPoint();

    //评论表中添加经纬度
    void upLaLg(double longitude, double latitude, String name);

    //查询景点经纬度
    List<ScenicEntity> queryLL();

    //获取评论点数据
    List<ComPoint> comPoint();

    //依据景点名称获取评论前20条
    List<CommentEntity> getComment(String name);

    //依据景点名称获取评论
    List<CommentEntity> getCom(String name);
}
