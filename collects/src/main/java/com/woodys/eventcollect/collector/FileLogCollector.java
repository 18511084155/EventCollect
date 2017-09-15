package com.woodys.eventcollect.collector;


import com.woodys.eventcollect.mouble.EventItem;

import java.util.ArrayList;

/**
 * 收集的数据，以文件的形式进行存储
 */
public class FileLogCollector extends ICollector {

    @Override
    public long insertData(EventItem item) throws Exception {
        return 0;
    }

    @Override
    public void insertBatchData(ArrayList<EventItem> items) throws Exception {

    }

    @Override
    public void deleteData(long lastId) throws Exception {

    }

    @Override
    public ArrayList<?> queryLists(Class<?> clazz, int number) throws Exception {
        return null;
    }
}
