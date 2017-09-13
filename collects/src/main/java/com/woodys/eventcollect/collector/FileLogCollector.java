package com.woodys.eventcollect.collector;


import com.woodys.eventcollect.mouble.EventItem;

import java.util.ArrayList;

/**
 * 收集的数据，以文件的形式进行存储
 */
public class FileLogCollector extends ICollector {


    @Override
    public long insertEvent(EventItem item) {
        return 0;
    }

    @Override
    public void insertBatchEvent(ArrayList<EventItem> items) {

    }

    @Override
    public void deleteEvent(long lastId) {

    }

    @Override
    public ArrayList<?> queryItems(Class<?> clazz, int number) {
        return null;
    }
}
