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
     *
     * @return
     */
    @PostMapping("queryScenic")
    public ResponseInfo queryScenic(@RequestBody PageEntity model) {
        return dataService.queryScenic(model);
    }

    /**
     * 表格信息搜索
     *
     * @param model
     * @return
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
     *
     * @param model
     * @return
     */
    @PostMapping("cityTravel")
    public ResponseInfo queryTravel(@RequestBody Travel model) {
        return dataService.queryTravel(model);
    }

    /**
     * 查询景点排行
     *
     * @return
     */
    @GetMapping("scenicRank")
    public ResponseInfo ScenicRank() {
        return dataService.ScenicRank();
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
}
