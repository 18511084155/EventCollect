package com.woodys.eventcollect.database;

import android.content.ContentValues;
import android.net.Uri;
import android.text.TextUtils;

import com.woodys.eventcollect.database.annotation.FieldFilter;
import com.woodys.eventcollect.database.annotation.Table;
import com.woodys.eventcollect.database.annotation.TableField;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * Created by cz on 2015/1/1.
 */
public class DbTable {
    public static final String AUTHORITY;

    static {
        AUTHORITY = DbHelper.getPackageName();
    }
    /**
     * 获得对象字段列
     *
     * @param clazz
     * @return
     */
    public static String[] getSelection(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        ArrayList<String> selectionLists = new ArrayList<>();
        Table table = clazz.getAnnotation(Table.class);
        if(null!=table&&!TextUtils.isEmpty(table.primaryKey())){
            selectionLists.add(table.primaryKey());
        }
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            String fieldName;
            TableField tableField = fields[i].getAnnotation(TableField.class);
            if (null != tableField && !TextUtils.isEmpty(tableField.value())) {
                fieldName = tableField.value();
            } else {
                fieldName = fields[i].getName();
            }
            FieldFilter fieldFilter = fields[i].getAnnotation(FieldFilter.class);
            if (Modifier.STATIC!=(fields[i].getModifiers() & Modifier.STATIC)&&(null == fieldFilter || !fieldFilter.value())) {
                selectionLists.add(fieldName);
            }
        }
        return selectionLists.toArray(new String[selectionLists.size()]);
    }

    public static String getTable(Class<?> clazz) {
        String tableName;
        Table table = clazz.getAnnotation(Table.class);
        if (null != table&&!TextUtils.isEmpty(table.value())) {
            tableName = table.value();
        } else {
            tableName = clazz.getSimpleName();
        }
        return tableName;
    }

    /**
     * 根据对象获得指定数据库ContentValues对象
     *
     * @param item
     * @return
     */
    public static ContentValues getContentValue(Object item) {
        Class<?> clazz = item.getClass();
        ContentValues values = new ContentValues();
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
                try {
                    if (int.class == type || Integer.class == type) {
                        values.put(name, field.getInt(item));
                    } else if (short.class == type || Short.class == type) {
                        values.put(name, field.getShort(item));
                    } else if (float.class == type || Float.class == type) {
                        values.put(name, field.getFloat(item));
                    } else if (double.class == type || Double.class == type) {
                        values.put(name, field.getDouble(item));
                    } else if (boolean.class == type || Boolean.class == type) {
                        values.put(name, field.getBoolean(item));
                    } else if (long.class == type || Long.class == type) {
                        values.put(name, field.getLong(item));
                    } else if (null != field.get(item)) {
                        values.put(name, field.get(item).toString());
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return values;
    }

    /**
     * 根据class获得访问uri地址
     *
     * @param clazz
     * @return
     */
    public static Uri getUri(Class<?> clazz) {
        return Uri.parse("content://" + AUTHORITY + "/class:" + clazz.getName());
    }

    /**
     * 根据表名获得访问uri地址
     *
     * @param tableName
     * @return
     */
    public static Uri getUri(String tableName) {
        return Uri.parse("content://" + AUTHORITY + "/table:" + tableName);
    }

}
