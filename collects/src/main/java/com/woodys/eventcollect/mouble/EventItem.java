package com.woodys.eventcollect.mouble;

/**
 * Created by woodys on 2017/9/7.
 * 扩展参数
 */

public class EventItem extends UserEvent{
    public String phoneNo;
    public String latitude;// 纬度
    public String longitude;//经度

    /*public int tokenId;
    public EventType type;
    public String clazzName;
    public String token;
    public String value;
    public Object args;
    public long ct;

    public EventItem(){

    }

    public EventItem(String phoneNo, String latitude, String longitude) {
        this.phoneNo = phoneNo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.ct = System.currentTimeMillis();
    }*/
}
