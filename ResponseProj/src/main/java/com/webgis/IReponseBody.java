package com.webgis;

import com.webgis.entity.EnumErrCode;

public interface IReponseBody {
    /**
     * 错误代码
     */
    EnumErrCode getErrCode();

    /**
     * 设置错误代码
     * @param errCode
     */
    void setErrCode(EnumErrCode errCode);

    /**
     * 获取描述
     * @return
     */
    String getDescirption();

    /**
     * 设置描述
     * @param descirption
     */
    void setDescirption(String descirption);

    /**
     * 获取返回数据
     * @return
     */
    Object getData();

    /**
     * 设置返回数据
     * @param data
     */
    void setData(Object data);
}
