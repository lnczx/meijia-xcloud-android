package com.meijialife.simi.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

public class AndroidUtil {

    public static class ConnectivityManager {

        /**
         * The Bluetooth data connection. When active, all data traffic will use this network type's interface by default (it has a default route).
         * 
         * @API 13
         */
        public static final int TYPE_BLUETOOTH = 7;

        /**
         * Dummy data connection. This should not be used on shipping devices.
         * 
         * @API 14
         */
        public static final int TYPE_DUMMY = 8;

        /**
         * The Ethernet data connection. When active, all data traffic will use this network type's interface by default (it has a default route).
         * 
         * @API 13
         */
        public static final int TYPE_ETHERNET = 9;
    }

    public static class VERSION_CODES {
        /**
         * June 2010: Android 2.2
         */
        public static final int FROYO = 8;

        /**
         * November 2010: Android 2.3
         */
        public static final int GINGERBREAD = 9;

        /**
         * February 2011: Android 2.3.3.
         */
        public static final int GINGERBREAD_MR1 = 10;

        /**
         * February 2011: Android 3.0.
         */
        public static final int HONEYCOMB = 11;

        /**
         * May 2011: Android 3.1.
         */
        public static final int HONEYCOMB_MR1 = 12;

        /**
         * June 2011: Android 3.2.
         */
        public static final int HONEYCOMB_MR2 = 13;

        /**
         * October 2011: Android 4.0.
         */
        public static final int ICE_CREAM_SANDWICH = 14;

        /**
         * December 2011: Android 4.0.3.
         */
        public static final int ICE_CREAM_SANDWICH_MR1 = 15;

        /**
         * June 2012: Android 4.1.
         */
        public static final int JELLY_BEAN = 16;

        /**
         * Android 4.2: Moar jelly beans!
         */
        public static final int JELLY_BEAN_MR1 = 17;

        /**
         * Android 4.3: Moar jelly beans!
         */
        public static final int JELLY_BEAN_MR2 = 18;

        /**
         * Android 4.4: Kitkat!
         */
        public static final int KITKAT_MR1 = 19;
        /**
         * Android 4.4w: Kitkat_watch!
         */
        public static final int KITKAT_W = 20;
        /**
         * Android 5.0: Lollipop!
         */
        public static final int LOLLIPOP = 21;
    }

    public static boolean isMeizu() {
        String product = Build.PRODUCT;
        if (null != product) {
            product = product.toLowerCase();
            return product.contains("meizu");
        }

        return false;
    }
    
    /**
     * 判断app处于后台状态
     * @param context
     * @return
     */
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : appProcesses) {
             if (appProcess.processName.equals(context.getPackageName())) {
                    if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                              return true;
                    }else{
                              return false;
                    }
               }
        }
        return false;
    }
    public static boolean isAppProcess(Context context) {
        int pid = android.os.Process.myPid();
        String packageName = context.getPackageName();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid && info.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
    

    @SuppressWarnings({ "unused", "deprecation" })
    public static  boolean isRunningForeground (Context context)  
    {   //获得task的topActivity
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);  
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;  
        String currentPackageName = cn.getPackageName();  
        if(!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(context.getPackageName()))  
        {  
            return true ;  
        }  
        return false ;  
    }  

}
