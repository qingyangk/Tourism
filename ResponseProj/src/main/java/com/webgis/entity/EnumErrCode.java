package com.webgis.entity;

public enum EnumErrCode {

    /**
     * 常见错误
     */
    CommonError(999, "常见错误"),

    /**
     * 非法参数
     */
    InValidParam(1, "非法参数"),


    /**
     *成功
     */
    OK(0, "成功"),

    /**
     * 任务错误
     */
    TaskError(300, "任务错误"),

    /**
     * 业务报错
     */
    BusinessError(400, "业务报错"),
    /**
     *未知错误
     */
    Unknown(-1, "未知错误");


    /**
     * 获取值
     * @return
     */
    public int getValue() {
        return value;
    }

    /**
     * 设置值
     * @param value
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * 枚举值
     */
    private int value;

    /**
     * 获取描述
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置描述
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 枚举描述
     */
    private String message;

    EnumErrCode(int value, String message) {
        this.value = value;
        this.message = message;
    }

    /**
     * 构造函数
     * @param i
     */
    EnumErrCode(int i) {
        this.value = i;
    }
}
