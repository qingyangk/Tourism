package com.webgis.service;

import com.webgis.ResponseInfo;

import java.io.Serializable;

public interface DataService extends Serializable {

    /**
     * 查询基本信息
     * @return
     */
    ResponseInfo queryScenic();
}
