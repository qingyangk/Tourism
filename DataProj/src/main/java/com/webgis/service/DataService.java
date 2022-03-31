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
     * che
     */
    ResponseInfo ScenicRank(Request type);

    ResponseInfo CityRank();

    ResponseInfo Recommend(Recommend model);

    ResponseInfo ScenicID(ID model);

    ResponseInfo ComMonth(Request model);
}
