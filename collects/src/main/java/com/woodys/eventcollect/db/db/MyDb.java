package com.woodys.eventcollect.db.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.woodys.eventcollect.db.DbHelper;

/**
 * @author momo
 * @Date 2014/9/20
 */
public class MyDb extends SQLiteOpenHelper {
    private static final int CURRENT_VERSION = 1;

    public MyDb(Context context) {
        super(context, DbHelper.getDefaultDatabaseName(), null, CURRENT_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //统计界面
        onUpgrade(db, db.getVersion(), CURRENT_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DbHelper.get().onUpgrade(db,oldVersion,newVersion);
        db.setVersion(newVersion);
    }

}
