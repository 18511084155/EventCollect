package com.woodys.eventcollect;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.woodys.eventcollect.callback.Action;
import com.woodys.eventcollect.callback.SendActionCallback;
import com.woodys.eventcollect.database.DbHelper;
import com.woodys.eventcollect.collector.DataBaseCollector;
import com.woodys.eventcollect.collector.ICollector;
import com.woodys.eventcollect.database.helper.OnCollectDbUpgradeListener;
import com.woodys.eventcollect.database.table.temp.TempEventData;
import com.woodys.eventcollect.mouble.EventItem;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 *
 * Created by woodys on 2016-04-06.
 */
public final class EventCollectsManager {
    private static final String TAG = EventCollectsManager.class.getName();
    public static boolean enable = true;
    private static final ExecutorService FIXED_THREAD_POOL;
    private static final EventCollectsManager instance;


    private Context context;
    //事件收集器
    private static ICollector eventCollector = new DataBaseCollector();
    //发送事件回调
    private static SendActionCallback sendActionCallback;
    /**
     * 上报策略模式
     */
    public enum UploadPolicy {
        /**实时发送*/
        UPLOAD_POLICY_REALTIME,
        /**只在wifi下*/
        UPLOAD_POLICY_WIFI_ONLY,
        /**批量上报 达到一定次数*/
        UPLOAD_POLICY_BATCH,
        /**每次启动,发送上次产生的数据 */
        UPLOAD_POLICY_WHILE_INITIALIZE
    }

    /**
     * 上报策略
     */
    protected static UploadPolicy uploadPolicy = UploadPolicy.UPLOAD_POLICY_WHILE_INITIALIZE;

    /**
     * private constructor
     */
    private EventCollectsManager() {}

    static {
        instance = new EventCollectsManager();
        FIXED_THREAD_POOL = Executors.newFixedThreadPool(8);
    }

    public static EventCollectsManager get() {
        return instance;
    }

    public EventCollectsManager init(Application application){
        context = application.getApplicationContext();
        DbHelper.get().setOnDbUpgradeListener(new OnCollectDbUpgradeListener());
        uploadPolicy = UploadPolicy.UPLOAD_POLICY_WHILE_INITIALIZE;
        return this;
    }

    /**
     * 开启是否启用收集和发送功能
     * @param enable
     * @return
     */
    public EventCollectsManager setEnable(boolean enable){
        this.enable = enable;
        return this;
    }

    public EventCollectsManager setSendActionCallback(SendActionCallback sendActionCallback){
        this.sendActionCallback=sendActionCallback;
        return this;
    }

    /**
     * 设置策略模式
     * @param policy
     *     策略模式
     *     目前默认为UPLOAD_POLICY_WHILE_INITIALIZE模式
     */
    public EventCollectsManager setUploadPolicy(UploadPolicy policy) {
        if (policy != null) {
            uploadPolicy = policy;
        }
        return this;
    }

    /**
     * 设置数据收集器
     * @param collector
     *     收集器
     *     目前默认为DataBaseCollector模式
     */
    public EventCollectsManager setEventCollector(ICollector collector) {
        if (collector != null) {
            eventCollector = collector;
        }
        return this;
    }

    /**
     * 添加对应的事件
     * @param eventItem
     */
    public void addAction(final EventItem eventItem){
        if (null != eventCollector && enable) {
            try {
                eventCollector.insertEvent(eventItem);
            } catch (Exception e) {
                Log.e(TAG,null!=e?e.getMessage():"");
            }
        }
    }

    /**
     * 添加对应的事件
     * @param eventItem
     */
    public void addAsyncAction(final EventItem eventItem) {
        if (null != eventCollector && enable) {
            FIXED_THREAD_POOL.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        eventCollector.insertEvent(eventItem);
                    } catch (Exception e) {
                        Log.e(TAG,null!=e?e.getMessage():"");
                    }
                }
            });
        }
    }


    /**
     * 发送数据
     */
    public void sendAction() {
        sendAction(null);
    }
    /**
     * 发送数据
     */
    public void sendAction(final Action<Object,Boolean> action) {
        if (null != sendActionCallback && enable) {
            FIXED_THREAD_POOL.execute(new Runnable() {
                @Override
                public void run() {
                try {
                    final ArrayList<TempEventData> items = (ArrayList<TempEventData>) eventCollector.queryItems(TempEventData.class, 100);
                    sendActionCallback.sendAction(items, new Action<Boolean, Boolean>() {
                        @Override
                        public Boolean call(Boolean isSuccess) {
                            if (isSuccess) {
                                try {
                                    eventCollector.deleteEvent(items.get(items.size() - 1).eId);
                                } catch (Exception e) {
                                    Log.e(TAG,null!=e?e.getMessage():"");
                                }
                            }
                            if (null != action) action.call(isSuccess);
                            return true;
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG,null!=e?e.getMessage():"");
                }
                }
            });
        }
    }

}
