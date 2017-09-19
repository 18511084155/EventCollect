package com.woodys.eventcollect.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;


/**
 * DeviceUtil
 * Created by Tamic.
 */
public class DeviceUtil {

    private static final String TAG =DeviceUtil.class.getName();

    /**
     * getAppVersionName
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // Get the package info
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (TextUtils.isEmpty(versionName)) {
                return "";
            }
        } catch (Exception e) {
            Log.e(TAG,null!=e?e.getMessage():"");
        }
        return versionName;
    }

    /**
     *  getAppVersionCode
     */
    public static int getAppVersionCode(Context context) {
        int versionCode = 0;
        try {
            // Get the package info
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = pi.versionCode;
        } catch (Exception e) {
            Log.e(TAG,null!=e?e.getMessage():"");
        }
        return versionCode;
    }


    /**
     *getMacAddress
     *
     * @param context
     * @return MAC
     */
    public static String getMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info != null ? info.getMacAddress() : "";
    }

    /**
     * getScreenDisplay
     * @param activity
     * @return
     */
    public static DisplayMetrics getScreenDisplay(Activity activity)
    {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    /**
     * getScreenWidth
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * getScreenHeight
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * etScreenDensity
     * @param context
     * @return
     */
    public static float getScreenDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * 获取当前手机型号
     */
    public static String getPhoneModel() {
        return Build.MODEL;
    }

    /**
     * 获取手机系统类型
     */
    public static String getSystemModel() {
        return Build.BRAND;
    }

    /**获取手机系统版本*/
    public static int getSystemVersion() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获得android设备id
     *
     * @return
     */
    public static String getAndroidId(Context context) {
        String id = null;
        if (TextUtils.isEmpty(id)) {
            try {
                id = Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            } catch (Exception e) {
                Log.e(TAG,null!=e?e.getMessage():"");
            }
        }
        return id;
    }

    /**
     * 获取服务提供商名字
     * @return
     */
    public static String getSimOperatorName(Context context) {
        String providersName = null;
        try {
            // 返回唯一的用户ID;就是这张卡的编号神马的
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);//取得相关系统服务
            //国际移动用户识别码
            String IMSI = tm.getSubscriberId();
            if (!TextUtils.isEmpty(IMSI)) {
                // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
                if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
                    providersName = "中国移动";
                } else if (IMSI.startsWith("46001")) {
                    providersName = "中国联通";
                } else if (IMSI.startsWith("46003")) {
                    providersName = "中国电信";
                }
            }
        }catch (Exception e){
            Log.e(TAG,null!=e?e.getMessage():"");
        }
        return providersName;
    }

    /**
     * 判断某个权限是否授权
     * @param permissionName 权限名称，比如：android.permission.READ_PHONE_STATE
     * *
     * @return
     */

    public static boolean hasPermission(Context context,String permissionName){
        boolean result = false;
        try{
            PackageManager pm = context.getPackageManager();
            result = PackageManager.PERMISSION_GRANTED == pm.checkPermission(permissionName, context.getPackageName());
        }catch (Exception e){
            Log.e(TAG,null!=e?e.getMessage():"");
        }
        return result;
    }

}
