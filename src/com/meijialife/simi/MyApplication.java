package com.meijialife.simi;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.baidu.mapapi.SDKInitializer;
import com.igexin.sdk.PushManager;
import com.simi.easemob.EMDemoHelper;

public class MyApplication extends Application {

    private static final String TAG = "MyApplication";

    public static Context applicationContext;
    private static MyApplication instance;
    // login user name
    public final String PREF_USERNAME = "username";

    /**
     * 当前用户nickname,为了苹果推送不是userid而是昵称
     */
    public static String currentUserNick = "";
    
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        instance = this;

        // init demo helper
        EMDemoHelper.getInstance().init(applicationContext);

        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);

        PushManager.getInstance().initialize(this.getApplicationContext());
    
    
        ///初始化异常报告
//        CrashHandler crashHandler = CrashHandler.getInstance();  
//        crashHandler.init(getApplicationContext());  
    }

    public static MyApplication getInstance() {
        return instance;
    }

}
