package com.woodys.eventcollect;

import android.app.Application;

import com.woodys.eventcollect.callback.Action;
import com.woodys.eventcollect.callback.SendActionCallback;
import com.woodys.eventcollect.database.table.temp.TempEventData;

import java.util.ArrayList;

/**
 * Created by woodys on 2017/9/13.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        EventCollectsManager.get()
                .init(this)
                .setSendActionCallback(new SendActionCallback<ArrayList<TempEventData>,Boolean,Boolean>() {
                    @Override
                    public void sendAction(ArrayList<TempEventData> items, Action<Boolean,Boolean> action) {

                    }
                });
    }
}
