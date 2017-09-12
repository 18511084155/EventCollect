package com.woodys.eventcollect.db.table;
import com.cz.dblibrary.annotation.Table;
import com.cz.dblibrary.annotation.TableField;

/**
 * Created by woodys on 2017/9/8.
 */
@Table(value = "device_data")
public class DeviceData {
    @TableField(value = "_id", primaryKey = true, autoIncrement = true)
    public long dId;//设备信息id
    public int width;// 屏幕宽
    public int height;// 屏幕高
    @TableField(value = "android_version")
    public int androidVersion;//android系统版本号
    @TableField(value = "device_id")
    public String deviceId;// 设备唯一id
    @TableField(value = "mobile_model")
    public String mobilemodel;// android6.0 iphone10 ..
    @TableField(value = "mobile_type")
    public String mobiletype;// 小米1 iphone6s ...
}
