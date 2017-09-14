package com.woodys.eventcollect.collector;

import android.content.Context;

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
        return eventData;
    }

    /**
     * 添加单条数据
     */
    public abstract long insertEvent(EventItem item);

    /**
     * 批量添加数据
     */
    public abstract void insertBatchEvent(ArrayList<EventItem> item);

    /**
     * 删除数据
     */
    public abstract void deleteEvent(long lastId);

    /**
     * 查询对应的数据集
     * @param clazz
     * @param number
     * @return
     */
    public abstract ArrayList<?> queryItems(Class<?> clazz, int number);
}
