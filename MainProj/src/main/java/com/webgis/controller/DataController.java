package com.webgis.controller;

import com.webgis.ResponseInfo;
import com.webgis.entity.*;
import com.webgis.service.DataService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 景点信息接口
 */

@RestController
@RequestMapping("/data")
public class DataController {

    @Resource
    DataService dataService;

    /**
     * 表格基本信息展示
     */
    @PostMapping("queryScenic")
    public ResponseInfo queryScenic(@RequestBody PageEntity model) {
        return dataService.queryScenic(model);
    }

    /**
     * 表格信息搜索
     */
    @PostMapping("searchScenic")
    public ResponseInfo searchScenic(@RequestBody SearchEntity model) {
        return dataService.searchScenic(model);
    }

    /**
     * 空间数据
     */
    @PostMapping("space")
    public ResponseInfo space(@RequestBody Point model) {
        return dataService.spaceIn(model);
    }

    /**
     * 处理数据--景点与评论对应
     */
    @PostMapping("disData")
    public ResponseInfo disData(@RequestBody SearchEntity model) {
        return dataService.disData(model);
    }

    /**
     * 获取城市游记
     */
    @PostMapping("cityTravel")
    public ResponseInfo queryTravel(@RequestBody Travel model) {
        return dataService.queryTravel(model);
    }

    /**
     * 查询景点排行
     */
    @PostMapping("scenicRank")
    public ResponseInfo ScenicRank(@RequestBody Request type) {
        return dataService.ScenicRank(type);
    }

    /**
     * 获取城市排行
     */
    @GetMapping("cityRank")
    public ResponseInfo CityRank() {
        return dataService.CityRank();
    }

    /**
     * 景点推荐接口
     */
    @PostMapping("recommend")
    public ResponseInfo Recommend(@RequestBody Recommend model) {
        return dataService.Recommend(model);
    }

    /**
     * 依据id查询景点所有信息
     */
    @PostMapping("scenicID")
    public ResponseInfo ScenicID(@RequestBody ID model) {
        return dataService.ScenicID(model);
    }

    /**
     * 查询该景点的月度评论数据
     */
    @PostMapping("comMonth")
    public ResponseInfo ComMonth(@RequestBody Request model) {
        return dataService.ComMonth(model);
    }

    /**
     * 城市评论
     */
    @PostMapping("cityCom")
    public ResponseInfo CityComment(@RequestBody Request model) {
        return dataService.CityComment(model);
    }

    /**
     * 获取时间段的天数评论
     */
    @PostMapping("commentDay")
    public ResponseInfo CommentDay() {
        return dataService.CommentDay();
    }

    /**
     * 日期景点评论数--传入年月
     */
    @PostMapping("scenicDay")
    public ResponseInfo ScenicDay(@RequestBody Request model) {
        return dataService.ScenicDay(model);
    }

    /**
     * 城市每月评论热度
     */
    @PostMapping("cityDay")
    public ResponseInfo CityDay(@RequestBody Request model) {
        return dataService.CityDay(model);
    }

    /**
     * 景点数、评论数、游记数获取
     */
    @GetMapping("getSCT")
    public ResponseInfo GetSCT() {
        return dataService.GetSCT();
    }

    /**
     * 获取词云
     */
    @PostMapping("wordCloud")
    public ResponseInfo GetWordCloud(@RequestBody Request model) {
        return dataService.GetWordCloud(model);
    }

    /**
     * 各地（省、市）流入流出量--客流分析
     * 评论数量
     */
    @PostMapping("outPut")
    public ResponseInfo OutPutPassengerFlow(@RequestBody FlowRequest flow) {
        return dataService.OutPutPassengerFlow(flow);
    }

    /**
     * 各地（省、市）流入流出量--客流分析
     * 评论分数
     */
    @PostMapping("opScore")
    public ResponseInfo OutPutScore(@RequestBody FlowRequest flow) {
        return dataService.OutPutScore(flow);
    }

    /**
     * 各地（省、市）流入流出量--客流分析
     * 评论日期变化
     */
    @PostMapping("opDay")
    public ResponseInfo OutPutDay(@RequestBody FlowRequest flow) {
        return dataService.OutPutDay(flow);
    }

    /**
     * 各地（省、市）流入流出量--客流分析
     * 景点数量
     */
    @PostMapping("opScenic")
    public ResponseInfo OPScenicRank(@RequestBody FlowRequest flow) {
        return dataService.OPScenicRank(flow);
    }
}
