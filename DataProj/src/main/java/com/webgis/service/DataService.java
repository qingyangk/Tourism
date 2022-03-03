package com.webgis.service;

import com.webgis.ResponseInfo;
import com.webgis.entity.PageEntity;
import com.webgis.entity.Point;
import com.webgis.entity.SearchEntity;

import java.io.Serializable;

public interface DataService extends Serializable {

    /**
     * 表格基本信息展示
     * @return
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
}
