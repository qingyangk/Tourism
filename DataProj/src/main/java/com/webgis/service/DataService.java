package com.webgis.service;

import com.webgis.ResponseInfo;
import com.webgis.entity.*;

import java.io.Serializable;

/**
 * 数据查询接口
 */
public interface DataService extends Serializable {

    /**
     * 表格基本信息展示
     */
    ResponseInfo queryScenic(PageEntity model);

    /**
     * 查询景点
     */
    ResponseInfo searchScenic(SearchEntity model);

    /**
     * 空间数据
     */
    ResponseInfo spaceIn(Point model);

    /**
     * dis data
     */
    ResponseInfo disData(SearchEntity model);

    /**
     * 查询游记
     */
    ResponseInfo queryTravel(Travel model);

    /**
     * 景点排行
     */
    ResponseInfo ScenicRank(Request type);

    /**
     * 城市排行
     */
    ResponseInfo CityRank();

    /**
     * 景点推荐
     */
    ResponseInfo Recommend(Recommend model);

    /**
     * id匹配所有景点
     */
    ResponseInfo ScenicID(ID model);

    /**
     * 月度评论
     */
    ResponseInfo ComMonth(Request model);

    /**
     * 城市评论查询
     */
    ResponseInfo CityComment(Request model);

    /**
     * 获取时间段的天数评论
     */
    ResponseInfo CommentDay();

    /**
     * 获取某一天景点的评论
     */
    ResponseInfo ScenicDay(Request model);

    ResponseInfo CityDay(Request model);

    ResponseInfo GetSCT();

    ResponseInfo GetWordCloud(Request model);

    ResponseInfo OutPutPassengerFlow(FlowRequest flow);

    /**
     * 各地（省、市）流入流出量--客流分析
     * 评论分数
     */
    ResponseInfo OutPutScore(FlowRequest flow);

    /**
     * 各地（省、市）流入流出量--客流分析
     * 评论日期变化
     */
    ResponseInfo OutPutDay(FlowRequest flow);

    /**
     * 各地（省、市）流入流出量--客流分析
     * 景点数量
     */
    ResponseInfo OPScenicRank(FlowRequest flow);
}
