package com.webgis.controller;

import com.webgis.ResponseInfo;
import com.webgis.entity.PageEntity;
import com.webgis.entity.Point;
import com.webgis.entity.SearchEntity;
import com.webgis.entity.Travel;
import com.webgis.service.DataService;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseInfo disData(@RequestBody PageEntity model) {
        return dataService.disData(model);
    }

    /**
     * 获取城市游记
     * @param model
     * @return
     */
    @PostMapping("cityTravel")
    public ResponseInfo queryTravel(@RequestBody Travel model) {
        return dataService.queryTravel(model);
    }


}
