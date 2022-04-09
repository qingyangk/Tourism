package com.webgis.entity;

import lombok.Data;

@Data
public class Recommend {
    //年龄、性别、职业、收入、季节、距离--省市、数据偏好--hot and score、时间、景点偏好--标签、数据源
    //数组形式：数据源、标签
    public int age;
    public String sex;
    public String occupation;
    public String income;
    public String season;
    public String province;
    public String preference;
    public String city;
    public long time;
    public String[] label;
    //数据源
    public String[] datasource;
}
