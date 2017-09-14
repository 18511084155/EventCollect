package com.woodys.eventcollect.mouble;

/**
 * Created by woodys on 2017/9/7.
 * 扩展参数
 */

public class EventItem {
    public long ct;
    public String phoneNo;
    public String latitude;// 纬度
    public String longitude;//经度

    //事件信息
    public String type;// 类型
    public String clazz;//class

    public String title;//title

    //type is leavePage
    public long offsetTime;// 页面停留时间，当前时间减去进入的时间

    //type is click
    public int x;// x坐标
    public int y;// y坐标
    public String descriptor;// 描述信息

    public EventItem(){
        this.ct = System.currentTimeMillis();
    }
}
