package com.woodys.eventcollect.database.helper;

import android.database.sqlite.SQLiteDatabase;

import com.woodys.eventcollect.database.DbHelper;


/**
 * Created by woodys on 2017/9/11.
 *
 * 数据升级监听
 */

public class OnCollectDbUpgradeListener implements DbHelper.OnDbUpgradeListener {
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
