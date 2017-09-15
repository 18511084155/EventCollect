package com.woodys.eventcollect.collector;

import android.content.Context;

import com.woodys.eventcollect.database.DbHelper;
import com.woodys.eventcollect.database.table.DeviceData;
import com.woodys.eventcollect.database.table.EventData;
import com.woodys.eventcollect.mouble.EventItem;
import com.woodys.eventcollect.util.DeviceUtil;
import com.woodys.eventcollect.util.NetworkUtil;

import java.util.ArrayList;

/**
 * 数据收集接口
 */

public abstract class ICollector{

    /**
     * 获取手机数据源
     */
    protected DeviceData getDeviceData(Context context){
        DeviceData deviceData = new DeviceData();
        deviceData.width = DeviceUtil.getScreenWidth(context);
        deviceData.height = DeviceUtil.getScreenHeight(context);
        deviceData.androidVersion = DeviceUtil.getSystemVersion();
        deviceData.deviceId = DeviceUtil.getAndroidId(context);
        deviceData.mobilemodel = DeviceUtil.getPhoneModel();
        deviceData.mobiletype = DeviceUtil.getSystemModel();
        return deviceData;
    }

    protected EventData initEventData(Context context,EventItem eventItem) {
        EventData eventData = new EventData();
        eventData.phoneNo = eventItem.phoneNo;
        eventData.latitude = eventItem.latitude;
        eventData.longitude = eventItem.longitude;

        eventData.appVersion = DeviceUtil.getAppVersionName(context);
        eventData.operator = DeviceUtil.getSimOperatorName(context);
        eventData.network = NetworkUtil.getNetWorkState(context);

        eventData.type = eventItem.type;
        eventData.clazz = eventItem.clazz;

        eventData.x= eventItem.x;
        eventData.y= eventItem.y;
        eventData.descriptor= eventItem.descriptor;

        eventData.title= eventItem.title;

        eventData.offsetTime = eventItem.offsetTime;
        eventData.ct = eventItem.ct;
        return eventData;
    }

    /**
     * 添加单条数据
     */
    public long insertEvent(EventItem item) throws Exception{
        if (!DeviceUtil.hasPermission(DbHelper.getContext(), "android.permission.WRITE_EXTERNAL_STORAGE")) {
            throw new Exception("not configuration uses-permission android.permission.WRITE_EXTERNAL_STORAGE");
        } else {
            return insertData(item);
        }
    }
    /**
     * 添加单条数据
     */
    public abstract long insertData(EventItem item) throws Exception;

    /**
     * 批量添加数据
     */
    public void insertBatchEvent(ArrayList<EventItem> items) throws Exception{
        if (!DeviceUtil.hasPermission(DbHelper.getContext(), "android.permission.WRITE_EXTERNAL_STORAGE")) {
            throw new Exception("not configuration uses-permission android.permission.WRITE_EXTERNAL_STORAGE");
        } else {
            insertBatchData(items);
        }
    }

    /**s
     * 批量添加数据
     */
    public abstract void insertBatchData(ArrayList<EventItem> items) throws Exception;

    /**
     * 删除数据
     */
    public void deleteEvent(long lastId) throws Exception{
        if (!DeviceUtil.hasPermission(DbHelper.getContext(), "android.permission.WRITE_EXTERNAL_STORAGE")) {
            throw new Exception("not configuration uses-permission android.permission.WRITE_EXTERNAL_STORAGE");
        } else {
            deleteData(lastId);
        }
    }

    /**
     * 删除数据
     */
    public abstract void deleteData(long lastId) throws Exception;

    /**
     * 查询对应的数据集
     * @param clazz
     * @param number
     * @return
     */
    public  ArrayList<?> queryItems(Class<?> clazz, int number) throws Exception{
        if (!DeviceUtil.hasPermission(DbHelper.getContext(), "android.permission.READ_EXTERNAL_STORAGE")) {
            throw new Exception("not configuration uses-permission android.permission.READ_EXTERNAL_STORAGE");
        } else {
            return queryLists(clazz,number);
        }
    }

    /**
     * 查询对应的数据集
     * @param clazz
     * @param number
     * @return
     */
    public abstract ArrayList<?> queryLists(Class<?> clazz, int number) throws Exception;
}
