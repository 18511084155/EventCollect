package com.woodys.eventcollect.collector;

import android.content.ContentUris;

import com.woodys.eventcollect.database.DbHelper;
import com.woodys.eventcollect.database.table.DeviceData;
import com.woodys.eventcollect.database.table.EventData;
import com.woodys.eventcollect.mouble.EventItem;
import com.woodys.eventcollect.util.DeviceUtil;

import java.util.ArrayList;

/**
 * 收集的数据，以数据库的的形式进行存储
 */
public class DataBaseCollector extends ICollector{

    @Override
    public long insertEvent(EventItem eventItem) {
        EventData item = initEventData(DbHelper.getContext(), eventItem);
        DeviceData queryItem = null;
        try {
            queryItem = DbHelper.get().queryItem(DeviceData.class, "device_id=?", new String[]{DeviceUtil.getAndroidId(DbHelper.getContext())}, null);
            if (null == queryItem) {
                // insert
                item.dId = ContentUris.parseId(DbHelper.get().insertItem(getDeviceData(DbHelper.getContext())));
            }else {
                item.dId = queryItem.dId;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ContentUris.parseId(DbHelper.get().insertItem(item));
    }

    @Override
    public void insertBatchEvent(ArrayList<EventItem> eventItems) {
        long dId = 0l;
        try {
            DeviceData queryItem = DbHelper.get().queryItem(DeviceData.class, "device_id=?", new String[]{DeviceUtil.getAndroidId(DbHelper.getContext())}, null);
            if (null == queryItem) {
                // insert
                dId = ContentUris.parseId(DbHelper.get().insertItem(getDeviceData(DbHelper.getContext())));
            }else {
                dId = queryItem.dId;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        //设置关联关系
        ArrayList<EventData> items = new ArrayList<>();
        EventData item = null;
        for (EventItem eventItem : eventItems){
            item = initEventData(DbHelper.getContext(), eventItem);
            item.dId = dId;
            items.add(item);
        }
        //批量添加数据
        DbHelper.get().bulkInsert(items);
    }

    @Override
    public void deleteEvent(long lastId){
        DbHelper.get().deleteItem(EventData.class,"e_id<=?",new String[]{String.valueOf(lastId)});
    }

    @Override
    public ArrayList<?> queryItems(Class<?> clazz, int number) {
        return DbHelper.get().querySQLItems(clazz,"select * from event_data a,device_data b where a.d_id=b.d_id order by a.e_id asc limit 0,?",new String[]{String.valueOf(number)});
    }

}
