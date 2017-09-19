package com.woodys.demo


import android.app.Application
import com.woodys.eventcollect.EventCollectsManager
import com.woodys.eventcollect.database.table.temp.TempEventData
import quant.actionrecord.sample.eventcollect.EvnetsManager
import java.util.ArrayList

/**
 * Created by cz on 11/11/16.
 */

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        EventCollectsManager.get()
                .init(this)
                .setSendActionCallback { items, action -> EvnetsManager.requestUserEvent(items as ArrayList<TempEventData>, 1, action) }
    }

}
