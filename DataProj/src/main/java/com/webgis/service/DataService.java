package com.webgis.service;

import com.webgis.ResponseInfo;
import com.webgis.entity.PageEntity;
import com.webgis.entity.Point;
import com.webgis.entity.SearchEntity;
import com.webgis.entity.Travel;

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
    ResponseInfo disData(PageEntity model);

    /**
     * 查询游记
     */
    ResponseInfo queryTravel(Travel model);
}
