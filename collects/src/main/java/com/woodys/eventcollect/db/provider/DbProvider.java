package com.woodys.eventcollect.db.provider;

import android.app.ActivityManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Process;
import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseArray;

import com.woodys.eventcollect.db.DbHelper;
import com.woodys.eventcollect.db.DbTable;
import com.woodys.eventcollect.db.annotation.FieldFilter;
import com.woodys.eventcollect.db.annotation.Table;
import com.woodys.eventcollect.db.annotation.TableField;
import com.woodys.eventcollect.db.db.MyDb;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by momo on 2015/1/1.
 * 统计信息内容提供者
 */
public class DbProvider extends ContentProvider {
    private static final String HOST_CLASS = "/class:";
    private static final String HOST_TABLE = "/table:";
    private static final UriMatcher matcher;
    private static final SparseArray<String> matchIds;
    private static final SparseArray<LinkedHashMap<String, String>> selectionMaps;

    public  static SQLiteDatabase db;// 数据库操作对象

    static {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matchIds = new SparseArray<>();
        selectionMaps = new SparseArray<>();
    }


    private static SQLiteDatabase getDatabase(){
        if(null==db){
            db=new MyDb(DbHelper.getContext()).getWritableDatabase();
        }
        return db;
    }

    public static void execSQL(String sql){
        getDatabase().execSQL(sql);
    }
    public static void execSQL(String sql,String[] bindArgs){
        getDatabase().execSQL(sql,bindArgs);
    }

    public static Cursor rawQuery(String sql,String[] selectionArgs){
        return getDatabase().rawQuery(sql,selectionArgs);
    }

    private boolean tableExist(String tableName){
        boolean result = false;
        if(!TextUtils.isEmpty(tableName)) {
            Cursor cursor=null;
            try {
                String sql = "select * from sqlite_master where name="+"'"+tableName+"'";
                cursor = rawQuery(sql, null);
                if (cursor.moveToNext()) {
                    result = cursor.getCount()!=0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(null!=cursor){
                    cursor.close();
                }
            }
        }
        return result;
    }

    private void ensureTable(Class<?> clazz){
        String tableName = DbTable.getTable(clazz);
        if(!tableExist(tableName)){
            createTable(clazz);
        }
        if(0>matchIds.indexOfValue(tableName)){
            addTable(clazz);
        }
    }

    /**
     * 创建表
     * @param clazz
     */
    private void createTable(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        List<Pair<String,Boolean>> primaryKeys=new ArrayList<>();
        HashMap<String,String> fieldItems=new HashMap<>();
        for (int i = 0; i < fields.length; i++) {
            Class<?> type = fields[i].getType();
            String fieldType;
            if (int.class == type || short.class == type || Integer.class == type || Short.class == type) {
                fieldType = " INTEGER";
            } else if (float.class == type || double.class == type || Float.class == type || Double.class == type) {
                fieldType = " FLOAT";
            } else if (boolean.class == type || Boolean.class == type) {
                fieldType = " BOOLEAN";
            } else if (long.class == type || Long.class == type) {
                fieldType = " LONG";
            } else {
                fieldType = " TEXT";
            }
            //过滤字段
            FieldFilter fieldFilter = fields[i].getAnnotation(FieldFilter.class);
            if(Modifier.STATIC!=(fields[i].getModifiers() & Modifier.STATIC)&&(null==fieldFilter||!fieldFilter.value())){
                String fieldName;
                TableField tableField = fields[i].getAnnotation(TableField.class);
                if (null != tableField && !TextUtils.isEmpty(tableField.value())) {
                    fieldName = tableField.value();
                    if (tableField.primaryKey()) {
                        //主键
                        primaryKeys.add(new Pair<>(fieldName,tableField.autoIncrement()));
                    }
                } else {
                    fieldName = fields[i].getName();
                }
                fieldItems.put(fieldName,fieldType);
            }
        }
        String tableName = DbTable.getTable(clazz);
        String sql = "CREATE TABLE " +tableName + "(";
        if(primaryKeys.isEmpty()){
            //当一个字段主键都未设置时,检测Table注释中是否设置默认主键
            Table table = clazz.getAnnotation(Table.class);
            if(null!=table&&!TextUtils.isEmpty(table.primaryKey())){
                sql += (table.primaryKey()+" INTEGER PRIMARY KEY "+(table.autoIncrement()?"AUTOINCREMENT":"")+",");
            }
        }
        //一个主键时,设置单个主键
        int size = primaryKeys.size();
        if(1==size){
            Pair<String, Boolean> primaryPair = primaryKeys.get(0);
            sql += (primaryPair.first+" "+fieldItems.get(primaryPair.first)+" PRIMARY KEY "+(primaryPair.second?"AUTOINCREMENT":"")+",");
        }
        int index=0;
        for(Map.Entry<String,String> entry:fieldItems.entrySet()){
            sql+=(entry.getKey()+" "+entry.getValue()+" "+(index++!=fieldItems.size()-1?",":" "));
        }
        //多个主键时,设置联合主键
        if(1<size){
            sql += (", PRIMARY KEY(");
            for(int i=0;i<size;i++){
                Pair<String, Boolean> pair = primaryKeys.get(i);
                sql+=(pair.first+(i!=size-1?",":"))"));
            }
        } else {
            sql+=")";
        }
        execSQL(sql);
    }

    private static void addTable(Class<?> clazz){
        //创建表
        String tableName = DbTable.getTable(clazz);
        int index = matchIds.size();
        //添加匹配uri
        matcher.addURI(DbTable.AUTHORITY, tableName, index+1);
        //添加匹配表名
        matchIds.append(index+1, tableName);
        //添加selectionMap
        String[] selection = DbTable.getSelection(clazz);
        LinkedHashMap<String, String> selectionMap = new LinkedHashMap<>();
        for (int s = 0; s < selection.length; s++) {
            selectionMap.put(selection[s], selection[s]);
        }
        selectionMaps.append(index+1 , selectionMap);
    }

    private synchronized Uri ensureUri(Uri uri){
        String path = uri.getPath();
        String authority = uri.getAuthority();
        Uri newUri=null;
        if(path.startsWith(HOST_CLASS)){
            try {
                Class<?> clazz = Class.forName(path.substring(HOST_CLASS.length()));
                Table table = clazz.getAnnotation(Table.class);
                if(null!=table&&(table.exported()||isApplicationProcess(getContext()))){
                    //当对象含table注解时暴露数据,或者为主进程时,才允许添加表信息,否则无法访问
                    ensureTable(clazz);
                }
                newUri=Uri.parse("content://" + authority + "/" + DbTable.getTable(clazz));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if(path.startsWith(HOST_TABLE)){
            //此分类下,表无法自动维护,适用于.表已经创建,试图操作
            newUri=Uri.parse("content://" + authority + "/" + path.substring(HOST_TABLE.length()));
        } else {
            newUri=uri;
        }
        return newUri;
    }

    /**
     * 是否为主进程
     *
     * @param context
     * @return
     */
    private boolean isApplicationProcess(Context context) {
        boolean result=false;
        String packageName = context.getPackageName();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if(null!=runningApps){
            int pid = Process.myPid();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningApps) {
                if (processInfo.pid == pid&&packageName.equals(processInfo.processName)) {
                    result=true;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public boolean onCreate() {
        db=new MyDb(DbHelper.getContext()).getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Uri newUri = ensureUri(uri);
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        int matchId = matcher.match(newUri);
        String tableName = matchIds.get(matchId);
        HashMap<String, String> map = selectionMaps.get(matchId);
        Cursor cursor = null;
        if (!TextUtils.isEmpty(tableName) && null != map) {
            builder.setTables(tableName);
            builder.setProjectionMap(map);
            // 判断uid
            try {
                cursor = builder.query(getDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (null != cursor) {
                cursor.setNotificationUri(getContext().getContentResolver(), newUri);
            }
        }
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri newUri = ensureUri(uri);
        int matchId = matcher.match(newUri);
        String tableName = matchIds.get(matchId);
        Uri notifyUri =null;
        if (!TextUtils.isEmpty(tableName)) {
            ContentValues contentValues;
            if (null != values) {
                contentValues = new ContentValues(values);
            } else {
                contentValues = new ContentValues();
            }
            long rowId = getDatabase().insert(tableName, null, contentValues);
            if (rowId > 0) {
                notifyUri = ContentUris.withAppendedId(newUri, rowId);
                getContext().getContentResolver().notifyChange(notifyUri, null);
            }
        }
        return notifyUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Uri newUri = ensureUri(uri);
        int matchId = matcher.match(newUri);
        String tableName = matchIds.get(matchId);
        int count = -1;
        if (!TextUtils.isEmpty(tableName)) {
            if (!TextUtils.isEmpty(tableName)) {
                try {
                    count = getDatabase().delete(tableName, selection, selectionArgs);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (-1 != count) {
                getContext().getContentResolver().notifyChange(newUri, null);
            }
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Uri newUri = ensureUri(uri);
        int matchId = matcher.match(newUri);
        String tableName = matchIds.get(matchId);
        int count = -1;
        if (!TextUtils.isEmpty(tableName)) {
            if (!TextUtils.isEmpty(tableName)) {
                try {
                    count = getDatabase().update(tableName, values, selection, selectionArgs);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (-1 != count) {
                getContext().getContentResolver().notifyChange(newUri, null);
            }
        }
        return count;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        Uri newUri = ensureUri(uri);
        int matchId = matcher.match(newUri);
        String tableName = matchIds.get(matchId);
        long lastId = -1;
        if (!TextUtils.isEmpty(tableName)) {
            SQLiteDatabase db = getDatabase();
            if (!TextUtils.isEmpty(tableName) && null != db) {
                db.beginTransaction();
                for (int i = 0; i < values.length; i++) {
                    long rowId = db.insert(tableName, null, values[i]);
                    if (i == values.length - 1) {
                        lastId = rowId;
                    }
                    if (0 >= rowId) {
                        //异常插入
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            }
            if (lastId > 0) {
                Uri noteUri = ContentUris.withAppendedId(newUri, lastId);
                getContext().getContentResolver().notifyChange(noteUri, null);
            }
        }
        return (int) lastId;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}
