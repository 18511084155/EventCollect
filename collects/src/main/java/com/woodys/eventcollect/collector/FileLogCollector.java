package com.woodys.eventcollect.collector;


import com.woodys.eventcollect.db.table.temp.TempEventData;
import com.woodys.eventcollect.mouble.ActionItem;

import java.util.ArrayList;

/**
 * 收集的数据，以文件的形式进行存储
 */
public class FileLogCollector extends ICollector {


    @Override
    public long insertEvent(ActionItem item) {
        return 0;
    }

    @Override
    public void insertBatchEvent(ArrayList<ActionItem> items) {

    }

    @Override
    public void deleteEvent(long lastId) {

    }

    @Override
    public ArrayList<TempEventData> queryItems(Class<TempEventData> clazz, int number) {
        return null;
    }
}
