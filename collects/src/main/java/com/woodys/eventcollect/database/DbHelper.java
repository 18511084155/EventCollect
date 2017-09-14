package com.woodys.eventcollect.database;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Pair;

import com.woodys.eventcollect.database.annotation.FieldFilter;
import com.woodys.eventcollect.database.annotation.TableField;
import com.woodys.eventcollect.database.provider.DbProvider;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cz on 16/3/15.
 */
public class DbHelper {
    private static String packageName;
    private static final DbHelper helper;
    private static Context appContext;
    private OnDbUpgradeListener upgradeListener;

    static {
        appContext = getContext();
        packageName = getPackageName();
        helper=new DbHelper();
    }

    public static Context getContext(){
        if(null==appContext){
            try{
                final Class<?> activityThreadClass = DbHelper.class.getClassLoader().loadClass("android.app.ActivityThread");
                Application application= (Application) activityThreadClass.getMethod("currentApplication").invoke(null);
                appContext=application.getApplicationContext();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return appContext;
    }
    public static String getPackageName() {
        if (packageName == null) {
            packageName=getContext().getPackageName();
        }
        return packageName;
    }

    public static String getDefaultDatabaseName(){
        return getPackageName()+"_db";
    }

    public static String getAuthority(){
        return getPackageName()+".collects";
    }

    public static DbHelper get(){
        return helper;
    }

    private DbHelper(){
    }


    public void execSQL(String sql,String[] bindArgs){
        DbProvider.execSQL(sql,bindArgs);
    }

    public Cursor rawQuery(String sql,String[] selectionArgs){
        return DbProvider.rawQuery(sql,selectionArgs);
    }

    /**
     * 根据包名,以及类,使用sql进行查询所有数据
     *
     * @param clazz
     * @param <E>
     * @return
     */
    public final <E> ArrayList<E> querySQLItems(Class<E> clazz,String sql,String[] selectionArgs) {
        ArrayList<E> items = new ArrayList<>();
        if (null != clazz && null != sql) {
            Cursor cursor = null;
            try {
                cursor = DbProvider.rawQuery(sql,selectionArgs);
                if (null != cursor) {
                    while (cursor.moveToNext()) {
                        items.add(getItemByCursor(clazz, cursor));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != cursor) {
                    cursor.close();
                }
            }
        }
        return items;
    }


    /**
     * 插入数据
     * @param item
     */
    public final Uri insertItem(Object item){
        Uri rtnUri=null;
        if(null!=item){
            Uri uri = DbTable.getUri(item.getClass());
            Context context = getContext();
            if (null != context && null != uri) {
                ContentResolver resolver = context.getContentResolver();
                rtnUri=resolver.insert(uri, DbTable.getContentValue(item,true));
            }
        }
        return rtnUri;
    }

    public final int bulkInsert(final ArrayList items) {
        int code=-1;
        if (null != items &&!items.isEmpty()){
            Object obj = items.get(0);
            Uri uri = DbTable.getUri(obj.getClass());
            ContentValues[] values = new ContentValues[items.size()];
            for (int i = 0; i < items.size(); i++) {
                values[i] = DbTable.getContentValue(items.get(i),true);
            }
            Context context = getContext();
            if (null != context && null != uri) {
                ContentResolver resolver = context.getContentResolver();
                code=resolver.bulkInsert(uri,values);
            }
        }
        return code;
    }

    public final int updateItem(Object item,String where,String... whereArgs){
        int code=-1;
        if(null!=item){
            Uri uri = DbTable.getUri(item.getClass());
            Context context = getContext();
            if (null != context && null != uri) {
                ContentResolver resolver = context.getContentResolver();
                code = resolver.update(uri, DbTable.getContentValue(item,true), where, whereArgs);
            }
        }
        return code;
    }

    public final int deleteItem(Object item){
        int code=-1;
        if(null!=item){
            Pair<String, String[]> where = getWhere(item);
            code=deleteItem(item.getClass(),where.first,where.second);
        }
        return code;
    }

    public final<E> int deleteItem(Class<E> clazz,String where,String... whereArgs){
        int code=-1;
        if(null!=clazz){
            Uri uri = DbTable.getUri(clazz);
            Context context = getContext();
            if (null != context && null != uri) {
                ContentResolver resolver = context.getContentResolver();
                code = resolver.delete(uri, where, whereArgs);
            }
        }
        return code;
    }

    public final Pair<String,String[]> getWhere(Object item){
        Pair<String,String[]> wherePair=new Pair<>(null,null);
        if(null!=item){
            Field[] fields = item.getClass().getDeclaredFields();
            HashMap<String,String> fieldItems=new HashMap<>();
            for(int i=0;i<fields.length;i++){
                FieldFilter fieldFilter = fields[i].getAnnotation(FieldFilter.class);
                if(Modifier.STATIC!=(fields[i].getModifiers() & Modifier.STATIC)&&(null==fieldFilter||!fieldFilter.value())){
                    fields[i].setAccessible(true);
                    try {
                        Object value = fields[i].get(item);
                        if(null!=value){
                            fieldItems.put(fields[i].getName(),value.toString());
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            int index=0;
            if(!fieldItems.isEmpty()){
                String where=new String();
                String[] whereArgs=new String[fieldItems.size()];
                for(Map.Entry<String,String> entry:fieldItems.entrySet()){
                    where+=(entry.getKey()+"=? "+(index!=fieldItems.size()-1?"and ":""));
                    whereArgs[index++]=entry.getValue();
                }
                wherePair=new Pair<>(where,whereArgs);
            }
        }
        return wherePair;
    }

    /**
     * 根据包名,以及类,查询所有数据
     *
     * @param clazz
     * @param <E>
     * @return
     */
    public final <E> E queryItem(Class<E> clazz,String where,String[] whereArgs,String order) {
        E item = null;
        Uri uri = DbTable.getUri(clazz);
        Context context = getContext();
        if (null != context && null != uri) {
            ContentResolver resolver = context.getContentResolver();
            //这里查询限制,本次/今天/本周
            Cursor cursor = null;
            try {
                String[] selection = DbTable.getSelection(clazz);
                cursor = resolver.query(uri, selection, where, whereArgs, order);
                if (null != cursor) {
                    item = getItemByCursor(clazz, cursor);
                }
            }catch(Exception e){
                e.printStackTrace();
            } finally {
                if(null!=cursor){
                    cursor.close();
                }
            }
        }
        return item;
    }

    public final <E> ArrayList<E> queryItems(Class<E> clazz) {
        return queryItems(clazz, null, null, null);
    }

    /**
     * 根据包名,以及类,查询所有数据
     *
     * @param clazz
     * @param <E>
     * @return
     */
    public final <E> ArrayList<E> queryItems(Class<E> clazz, String where, String[] whereArgs, String order) {
        ArrayList<E> items = new ArrayList<>();
        Uri uri = DbTable.getUri(clazz);
        Context context = getContext();
        if (null != context && null != uri) {
            ContentResolver resolver = context.getContentResolver();
            //这里查询限制,本次/今天/本周
            Cursor cursor = null;
            try {
                String[] selection = DbTable.getSelection(clazz);
                cursor = resolver.query(uri, selection, where, whereArgs, order);
                if (null != cursor) {
                    while (cursor.moveToNext()) {
                        items.add(getItemByCursor(clazz, cursor));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != cursor) {
                    cursor.close();
                }
            }
        }
        return items;
    }

    private <E> E getItemByCursor(Class<E> clazz, Cursor cursor) throws InstantiationException, IllegalAccessException {
        E item = clazz.newInstance();
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            String name;
            TableField tableField = field.getAnnotation(TableField.class);
            if (null != tableField && !TextUtils.isEmpty(tableField.value())) {
                name = tableField.value();
            } else {
                name = field.getName();
            }
            Class<?> type = field.getType();
            FieldFilter fieldFilter = fields[i].getAnnotation(FieldFilter.class);
            if (Modifier.STATIC!=(fields[i].getModifiers() & Modifier.STATIC)&&(null == fieldFilter || !fieldFilter.value())) {
                int columnIndex = cursor.getColumnIndex(name);
                if (0 <= columnIndex) {
                    if (int.class == type || Integer.class == type) {
                        field.set(item, cursor.getInt(columnIndex));
                    } else if (short.class == type || Short.class == type) {
                        field.set(item, cursor.getShort(columnIndex));
                    } else if (float.class == type || Float.class == type) {
                        field.set(item, cursor.getFloat(columnIndex));
                    } else if (double.class == type || Double.class == type) {
                        field.set(item, cursor.getDouble(columnIndex));
                    } else if (boolean.class == type || Boolean.class == type) {
                        field.set(item, 1 == cursor.getInt(columnIndex));
                    } else if (long.class == type || Long.class == type) {
                        field.set(item, cursor.getLong(columnIndex));
                    } else {
                        field.set(item, cursor.getString(columnIndex));
                    }
                }
            }
        }
        return item;
    }

    /**
     * 清空指定表
     *
     * @param clazz
     */
    public void truncateTable(Class<?> clazz) {
        Uri uri = DbTable.getUri(clazz);
        Context context = getContext();
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.delete(uri, null, null);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        if(null!=upgradeListener){
            upgradeListener.onUpgrade(db,oldVersion,newVersion);
        }
    }

    public void setOnDbUpgradeListener(OnDbUpgradeListener listener){
        this.upgradeListener=listener;
    }

    public interface OnDbUpgradeListener{
        void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
    }

}
