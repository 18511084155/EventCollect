package com.woodys.eventcollect.database.table;

import com.woodys.eventcollect.database.annotation.Table;
import com.woodys.eventcollect.database.annotation.TableField;

/**
 * Created by woodys on 2017/9/7.
 */
@Table(value = "event_data")
public class EventData {
    @TableField(value = "e_id", primaryKey = true, autoIncrement = true)
    public int eId;//事件信息id
    @TableField(value = "phone_no")
    public String phoneNo;
    @TableField(value = "d_id")
    public int dId;// 设备信息id
    @TableField(value = "app_version")
    public String appVersion;// 版本
    public String operator;// 运营商
    public String latitude;// 纬度
    public String longitude;//经度
    public String network;// 3G WIFI ...

    //事件信息
    public String type;// 类型
    public String clazz;// class
    //type is leavePage
    @TableField(value = "offset_time")
    public long offsetTime;// 页面停留时间，当前时间减去进入的时间

    //type is enterPage
    @TableField(value = "title")
    public String title;// 页面title

    //type is click
    public int x;// x坐标
    public int y;// y坐标
    public String descriptor;// 描述信息

    public long ct;//创建时间

}
