package com.webgis.controller;

import com.webgis.ResponseInfo;
import com.webgis.service.DataService;
import org.springframework.web.bind.annotation.PostMapping;
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
     * 查询基本信息
     * @return
     */
    @PostMapping("queryScenic")
    public ResponseInfo queryScenic() {
        return dataService.queryScenic();
    }
}
