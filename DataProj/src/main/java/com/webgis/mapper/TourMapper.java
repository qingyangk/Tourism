package com.webgis.mapper;

import com.webgis.entity.CommentDay;
import com.webgis.entity.table.FlowEntity;
import com.webgis.entity.table.SCT;
import com.webgis.entity.ScenicDay;
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

    //南京景点表--景点推荐查询
    List<NanJingEntity> nanjingRe(String preference);

    //获取城市评论--前20条
    List<CommentEntity> cityCom(String city);

    //获取评论表中的最新的时间
    List<CommentEntity> lastCom();

    //根据起止日期获取每天评论数
    List<CommentDay> commentDay(String startDate, String endDate);

    //根据日期获取每月景点热度
    List<ScenicDay> ScenicDay(String time);

    //获取起止日期游记数
    List<CommentDay> travelDay(String startDate, String endDate);

    //根据日期获取每月城市热度
    List<ScenicDay> CityDay(String time);

    //获取景点数、评论数、游记数
    List<SCT> GetSCT();

    //获取城市词云
    List<WordCloud> GetWC(String city);

    //省市流出评论数
    List<FlowEntity> OutFlow(String cORp, String place);

    //省市流入评论数
    List<FlowEntity> PutFlow(String cORp, String place);

    //省市流出平均分数
    List<OPScore> OutScore(String cORp, String place);

    //省市流入平均分数
    List<OPScore> PutScore(String cORp, String place);

    List<OPDay> OutDay(String place);

    List<OPDay> PutDay(String cORp, String place);

    List<FlowEntity> OutScenic(String place);

    List<FlowEntity> PutScenic(String cORp, String place);

    List<ScenicEntity> CitySH(String city);

    List<FlowEntity> CitySource(String city);

    //景点标签
    List<ScenicLabel> ScenicLabel(String label);

    List<Map<String, Object>> SearchLabels(String labels,int type,String region);

    List<Map<String, Object>> CityFeature(String labels);
}
