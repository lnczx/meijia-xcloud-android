package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.Constants;
import com.meijialife.simi.MainActivity;
import com.meijialife.simi.R;
import com.meijialife.simi.bean.CalendarMark;
import com.meijialife.simi.bean.CityData;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.bean.XcompanySetting;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.ui.RouteUtil;
import com.meijialife.simi.utils.CalendarUtils;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;
import com.simi.easemob.EMDemoHelper;

public class SplashActivity extends Activity {

    private static final int sleepTime = 2000;
    public static String clientid;
    /**
     * 获取当前位置经纬度
     */
    private LocationClient locationClient = null;
    private static final int UPDATE_TIME = 5000;
    private SharedPreferences sp;
    
    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    private ImageView mWelcome;
    private ImageView mWelcome2;
    private Handler handler;
    private TimerTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash);

        final Timer timer = new Timer();

        initLocation();
        AlphaAnimation aa = new AlphaAnimation(0.8f, 1.0f);
        aa.setDuration(2000);
        findViewById(R.id.iv_welcome).startAnimation(aa);
        
        //启动页动态广告
        mWelcome =(ImageView) findViewById(R.id.iv_welcome);
        mWelcome2 =(ImageView) findViewById(R.id.iv_welcome2);
        initSplashAd();
        timer.schedule(task, 2000);
        //2s之后启动页进入首页
        TimerTask MyTask = new TimerTask() {
            @Override
            public void run() {
                List<User> searchAll = DBHelper.getInstance(SplashActivity.this).searchAll(User.class);
                if (searchAll.size() <= 0) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    SplashActivity.this.finish();
                    timer.cancel();
                } else {
                    try {
                        updateUserInfo();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    initEasemob();
                    updateCalendarMark();
                }
            }
        };
        timer.schedule(MyTask, 2000);
        initRoute();
        // initCellsDb();
         getCitys(getCityAddtime());
//         getBaseDatas(getXcompanySettingAddtime());
         
    }
    /**
     * 增加启动页动态广告
     */
    private void initSplashAd(){
        finalBitmap = FinalBitmap.create(SplashActivity.this);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch(msg.what){
                case 1:
                    finalBitmap.display(mWelcome2,Constants.SPLASH_ICON_URL);
                    break;
                }
                super.handleMessage(msg);
            }
        };
        task =new TimerTask() {
            @Override
            public void run() {
                //由于主线程安全，页面的更新需放到主线程中
                Message msg =new Message();
                msg.what=1;
                handler.sendMessage(msg);
            }
        };
    }
    /**
     * 初始化跳转路由
     */
    private void initRoute(){
        Uri uri = getIntent().getData();
        if(null !=uri){
            String category= uri.getQueryParameter("category"); 
            String action= uri.getQueryParameter("action");
            String goto_url= uri.getQueryParameter("goto_url"); 
            String params= uri.getQueryParameter("params");
            RouteUtil routeUtil  = new RouteUtil(SplashActivity.this);
            routeUtil.Routing(category, action, goto_url, params);
        }
    }
    /**
     * 获取用户当前位置的经纬度
     */
    private void initLocation() {
        sp = getApplicationContext().getSharedPreferences("Secretary",MODE_PRIVATE);
        locationClient = new LocationClient(this);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 是否打开GPS
        option.setCoorType("bd09ll"); // 设置返回值的坐标类型。
        option.setPriority(LocationClientOption.NetWorkFirst); // 设置定位优先级
        option.setProdName("Secretary"); // 设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
        option.setScanSpan(UPDATE_TIME); // 设置定时定位的时间间隔。单位毫秒
        option.setIsNeedAddress(true);// 设置返回城市
        option.setIsNeedLocationDescribe(true);
        locationClient.setLocOption(option);
        locationClient.start();
        locationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                if (location == null) {
                    return;
                }
                if(DBHelper.getUser(SplashActivity.this)!=null){
                    post_trail(location);
                }
            }
        });
    }
    
    /**
     * 初始化环信
     */
    private void initEasemob(){
        new Thread(new Runnable() {
            public void run() {
             // 如果登录成功过，直接进入主页面
                if (EMDemoHelper.getInstance().isLoggedIn()) {
                    // ** 免登陆情况 加载所有本地群和会话
                    //不是必须的，不加sdk也会自动异步去加载(不会重复加载)；
                    //加上的话保证进了主页面会话和群组都已经load完毕
                    long start = System.currentTimeMillis();
                    EMGroupManager.getInstance().loadAllGroups();
                    EMChatManager.getInstance().loadAllConversations();
                    long costTime = System.currentTimeMillis() - start;
                    //等待sleeptime时长
                    if (sleepTime - costTime > 0) {
                        try {
                            Thread.sleep(sleepTime - costTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //进入主页面
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }else {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                    }
                    //去登陆
                    startActivity(new Intent(SplashActivity.this, SplashActivity.class));
                    finish();
                }
            }
        }).start();
    }
    /**
     * 获取当前地理位置
     * @param useid
     * @param clientid
     */
    private void post_trail(BDLocation location) {
        String user_id = DBHelper.getUser(SplashActivity.this).getId();
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id);
        map.put("lat", location.getLatitude()+"");
        map.put("lng", location.getLongitude()+"");
        map.put("poi_name", location.getProvince());
        map.put("city", location.getCity());
        AjaxParams param = new AjaxParams(map);

        new FinalHttp().post(Constants.URL_POST_TRAIL, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(SplashActivity.this,  getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            if (locationClient != null && locationClient.isStarted()) {
                                locationClient.stop();
                                locationClient = null;
                            }
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            errorMsg =  getString(R.string.servers_error);
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            errorMsg =  getString(R.string.param_missing);
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            errorMsg =  getString(R.string.param_illegal);
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            errorMsg = msg;
                        } else {
                            errorMsg =  getString(R.string.servers_error);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg =  getString(R.string.servers_error);

                }
                // 操作失败，显示错误信息|
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    UIUtils.showToast(SplashActivity.this, errorMsg);
                }
            }
        });
    }
    /**
     * 获取用户详情接口
     */
    private void updateUserInfo() {

        if (!NetworkUtils.isNetworkConnected(SplashActivity.this)) {
            Toast.makeText(SplashActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }
        
        User user = DBHelper.getUser(this);
        if(null==user){
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user.getId());
        AjaxParams param = new AjaxParams(map);

        new FinalHttp().get(Constants.URL_GET_USER_INFO, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(SplashActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            if (StringUtils.isNotEmpty(data)) {
                                Gson gson = new Gson();
                                UserInfo  userInfo = gson.fromJson(data, UserInfo.class);
                                DBHelper.updateUserInfo(SplashActivity.this, userInfo);
                                
                                String clientidFromWeb = userInfo.getClient_id();
                                if (StringUtils.isEmpty(clientidFromWeb)||!StringUtils.isEquals(clientidFromWeb, clientid)) {
                                    bind_user(userInfo.getId(), clientid);
                                }
                            } else {
                                // UIUtils.showToast(SplashActivity.this, "数据错误");
                            }
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            errorMsg = getString(R.string.servers_error);
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            errorMsg = getString(R.string.param_missing);
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            errorMsg = getString(R.string.param_illegal);
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            errorMsg = msg;
                        } else {
                            errorMsg = getString(R.string.servers_error);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = getString(R.string.servers_error);

                }
                // 操作失败，显示错误信息|
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    UIUtils.showToast(SplashActivity.this, errorMsg);
                }
            }
        });

    }
    
    /**
     * 绑定接口
     * @param clientid2 
     * 
     * @param date
     */
    private void bind_user(String useid, String clientid) {
       
        if(StringUtils.isEmpty(clientid)||StringUtils.isEmpty(useid)){
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", useid + "");
        map.put("device_type", "android");
        map.put("client_id", clientid);
        AjaxParams param = new AjaxParams(map);

        new FinalHttp().post(Constants.URL_POST_PUSH_BIND, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(SplashActivity.this,  getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
//                            UIUtils.showToast(mContext, "推送绑定成功");
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            errorMsg =  getString(R.string.servers_error);
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            errorMsg =  getString(R.string.param_missing);
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            errorMsg =  getString(R.string.param_illegal);
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            errorMsg = msg;
                        } else {
                            errorMsg =  getString(R.string.servers_error);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg =  getString(R.string.servers_error);

                }
                // 操作失败，显示错误信息|
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    UIUtils.showToast(SplashActivity.this, errorMsg);
                }
            }
        });
    }
    /**
     * 获取请求城市列表的时间戳
     */
    private long getCityAddtime(){
    	long addtime = 0;
    	List<CityData> list = DBHelper.getCitys(this);
    	for(int i = 0; i < list.size(); i++){
    		if(list.get(i).getAdd_time() > addtime){
    			addtime = list.get(i).getAdd_time();
    		}
    	}
    	
    	return addtime;
    }
    /**
     * 获取请求资产列表的时间戳
     **/
    private long getXcompanySettingAddtime(){
        long updateTime = 0;
        List<XcompanySetting> list = DBHelper.getXcompanySettings(this);
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).getUpdate_time() > updateTime){
                updateTime = list.get(i).getUpdate_time();
            }
        }
        
        return updateTime;
    }
    
    /**
     * 网络获取城市数据
     */
	private void getCitys(long addtime) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("t", String.valueOf(addtime));
		AjaxParams param = new AjaxParams(map);

		new FinalHttp().get(Constants.URL_GET_CITY_LIST, param,
				new AjaxCallBack<Object>() {
					@Override
					public void onLoading(long count, long current) {
						// TODO Auto-generated method stub
						super.onLoading(count, current);
						Log.i("===getCity", "onLoading...");
					}

					@Override
					public void onFailure(Throwable t, int errorNo, String strMsg) {
						// TODO Auto-generated method stub
						super.onFailure(t, errorNo, strMsg);
						Log.i("===getCity", getString(R.string.network_failure));
					}

					@Override
					public void onSuccess(Object t) {
						// TODO Auto-generated method stub
						super.onSuccess(t);
						Log.i("===getCity", "onSuccess：" + t);
						final JSONObject json;
						try {
							json = new JSONObject(t.toString());
							int status = Integer.parseInt(json.getString("status"));
							String msg = json.getString("msg");

							if (status == Constants.STATUS_SUCCESS) { // 正确
								new Thread(new Runnable() {
									@Override
									public void run() {
										// TODO Auto-generated method stub
										updateCity(json);
									}
								}).start();
							} else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
								Log.i("===getCells", getString(R.string.servers_error));
							} else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
								Log.i("===getCells", getString(R.string.param_missing));
							} else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
								Log.i("===getCells", getString(R.string.param_illegal));
							} else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
								Log.i("===getCells", "999错误");
							} else {
								Log.i("===getCells", getString(R.string.servers_error));
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast.makeText(getApplicationContext(), getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
						}
					}

				});
	}
	
	/**
	 * 基础数据接口
	 * @param addtime
	 */
	private void getBaseDatas(long addtime) {
	    Map<String, String> map = new HashMap<String, String>();
	    map.put("t_assets", String.valueOf(addtime));
	    AjaxParams param = new AjaxParams(map);
	    
	    new FinalHttp().get(Constants.GET_BASE_DATAS, param,
	            new AjaxCallBack<Object>() {
	        @Override
	        public void onLoading(long count, long current) {
	            super.onLoading(count, current);
	        }
	        @Override
	        public void onFailure(Throwable t, int errorNo, String strMsg) {
	            super.onFailure(t, errorNo, strMsg);
	        }
	        
	        @Override
	        public void onSuccess(Object t) {
	            super.onSuccess(t);
	            final JSONObject json;
	            try {
	                json = new JSONObject(t.toString());
	                int status = Integer.parseInt(json.getString("status"));
	                String msg = json.getString("msg");
	                if (status == Constants.STATUS_SUCCESS) { // 正确
	                    new Thread(new Runnable() {
	                        @Override
	                        public void run() {
	                            updateXcompany(json);
	                        }
	                    }).start();
	                } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
	                    Log.i("===getCells", getString(R.string.servers_error));
	                } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
	                    Log.i("===getCells", getString(R.string.param_missing));
	                } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
	                    Log.i("===getCells", getString(R.string.param_illegal));
	                } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
	                    Log.i("===getCells", "999错误");
	                } else {
	                    Log.i("===getCells", getString(R.string.servers_error));
	                }
	            } catch (JSONException e) {
	                e.printStackTrace();
	                Toast.makeText(getApplicationContext(), getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
	            }
	        }
	        
	    });
	}

    /**
     * 获取城市列表成功，更新数据库
     */
    private void updateCity(JSONObject json) {
        ArrayList<CityData> cityDatas = new ArrayList<CityData>();
        try {
            JSONArray jsons = json.getJSONArray("data");
            for (int i = 0; i < jsons.length(); i++) {
                JSONObject obj = jsons.getJSONObject(i);
                String city_id = obj.getString("city_id"); // 城市ID
                String zip_code = obj.getString("zip_code"); // 
                String name = obj.getString("name"); // 名称
                String province_id = obj.getString("province_id"); //
                String is_enable = obj.getString("is_enable"); //
                String add_time = obj.getString("add_time"); // 最后更新时间

                CityData cityData = new CityData(city_id, zip_code, name, province_id, Integer.parseInt(is_enable), Long.parseLong(add_time));
                cityDatas.add(cityData);
            }

            DBHelper db = DBHelper.getInstance(this);
            for (int i = 0; i < cityDatas.size(); i++) {
                db.add(cityDatas.get(i), cityDatas.get(i).getCity_id());
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.i("===getCells", getString(R.string.servers_error));
        }

    }
    /**
     * 
     * @param json
     */
    private void updateXcompany(JSONObject json) {
        ArrayList<XcompanySetting> assetDatas = new ArrayList<XcompanySetting>();
        try {
            JSONArray jsons = json.getJSONArray("data");
            for (int i = 0; i < jsons.length(); i++) {
                JSONObject obj = jsons.getJSONObject(i);
                String id = obj.getString("id"); // 城市ID
                String xcompany_id = obj.getString("xcompany_id"); // 
                String name = obj.getString("name"); // 名称
                String setting_type = obj.getString("setting_type"); //
                String seeting_json = obj.getString("seeting_json"); //
                String is_enable = obj.getString("is_enable"); //
                String add_time = obj.getString("add_time"); // 最后更新时间
                String update_time = obj.getString("update_time"); // 最后更新时间
                XcompanySetting cityData = new XcompanySetting(id, xcompany_id, name, setting_type, seeting_json,Short.parseShort(is_enable), Long.parseLong(add_time),Long.parseLong(update_time));
                assetDatas.add(cityData);
            }
            DBHelper db = DBHelper.getInstance(this);
            for (int i = 0; i < assetDatas.size(); i++) {
                db.add(assetDatas.get(i), assetDatas.get(i).getId());
            }
        } catch (JSONException e) {
        }
        
    }
    
    /**
     * 一次获取多个月份的首页日历数据，并更新本地数据库存储，用来显示标记圆点
     */
    private void updateCalendarMark(){
        int year = CalendarUtils.getCurrentYear();
        int month = CalendarUtils.getCurrentMonth();
        getTotalByMonth(year+"", month+"");
        
       /* for(int i = 0; i < 8; i++){
            if(month == 12){
                month = 1;
                year += 1;
            }else{
                month += 1;
            }
            getTotalByMonth(year+"", month+"");
        }*/
    }
    
    /**
     * 按月份获取卡片日期分布接口
     * 
     * @param year  年份，格式为 YYYY
     * @param month 月份，格式为 MM
     */
    public void getTotalByMonth(String year, String month) {

        String user_id = DBHelper.getUser(SplashActivity.this).getId();

        if (!NetworkUtils.isNetworkConnected(SplashActivity.this)) {
//            Toast.makeText(SplashActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id+"");
        map.put("year", year);
        map.put("month", month);
        AjaxParams param = new AjaxParams(map);

        new FinalHttp().get(Constants.URL_GET_TOTAL_BY_MONTH, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
//                Toast.makeText(SplashActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                LogOut.i("========", "onSuccess：" + t);
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            if(StringUtils.isNotEmpty(data)){
                                Gson gson = new Gson();
                                ArrayList<CalendarMark> calendarMarks = gson.fromJson(data, new TypeToken<ArrayList<CalendarMark>>() {
                                }.getType());
                                
                                DBHelper db = DBHelper.getInstance(SplashActivity.this);
                                for (int i = 0; i < calendarMarks.size(); i++) {
                                    db.add(calendarMarks.get(i), calendarMarks.get(i).getService_date());
                                }
                            }else{
                                
                            }
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            errorMsg = getString(R.string.servers_error);
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            errorMsg = getString(R.string.param_missing);
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            errorMsg = getString(R.string.param_illegal);
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            errorMsg = msg;
                        } else {
                            errorMsg = getString(R.string.servers_error);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = getString(R.string.servers_error);

                }
                // 操作失败，显示错误信息
//                if(!StringUtils.isEmpty(errorMsg.trim())){
//                    UIUtils.showToast(SplashActivity.this, errorMsg);
//                }
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 关闭定位
        if (locationClient != null && locationClient.isStarted()) {
            locationClient.stop();
            locationClient = null;
        }
    }
}
