package com.woodys.eventcollect;

import android.app.Application;
import android.content.Context;

import com.woodys.eventcollect.db.DbHelper;
import com.woodys.eventcollect.collector.DataBaseCollector;
import com.woodys.eventcollect.collector.ICollector;
import com.woodys.eventcollect.db.helper.OnCollectDbUpgradeListener;
import com.woodys.eventcollect.mouble.ActionItem;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 *
 * Created by woodys on 2016-04-06.
 */
public final class EventCollectsManager {
    private static final ExecutorService FIXED_THREAD_POOL;
    private static final EventCollectsManager instance;

    private Context context;
    //事件收集器
    private static ICollector eventCollector = new DataBaseCollector();
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
        DbHelper.getPackageName();
        DbHelper.get().setOnDbUpgradeListener(new OnCollectDbUpgradeListener());
        uploadPolicy = UploadPolicy.UPLOAD_POLICY_WHILE_INITIALIZE;
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
     * @param actionItem
     * @return
     */
    public void addAction(final ActionItem actionItem) {
        FIXED_THREAD_POOL.execute(new Runnable() {
            @Override
            public void run() {
                eventCollector.insertEvent(actionItem);
            }
        });
    }



    /**
     * 上报数据
     *
     */
    public static void report() {

    }

}
