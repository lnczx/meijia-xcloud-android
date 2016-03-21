package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.MainPlusCheckAdapter;
import com.meijialife.simi.bean.CheckData;
import com.meijialife.simi.bean.CheckListData;
import com.meijialife.simi.bean.CompanySetting;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.AlertWindow;
import com.meijialife.simi.utils.CalendarUtils;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * @description：加号---签到
 * @author： kerryg
 * @date:2016年3月1日
 */
public class MainPlusSignInActivity extends Activity {

    private MainPlusCheckAdapter adapter;
    private CheckData checkData;
    private ArrayList<CheckListData> checkListDatas;
    private ArrayList<CheckListData> totalCheckListDatas;

    private ArrayList<CompanySetting> companySettings;

    private TextView mCompanyName;
    private TextView mWeek;
    private TextView mDay;
    private TextView mSignlog;
    private ImageView mSignIn;
    private User user;

    private ImageView mCardBack;
    private TextView mCardTitle;
    private LinearLayout mAffairCardTitle;
    private LinearLayout mLlCard;
    private RelativeLayout mRlCard;
    private LinearLayout mLlBottom;// 布局底部控件

    private String checkinet;// 网络名称
    private int flag=0;// 0=wifi,1=手机网络

    // 布局控件定义
    private PullToRefreshListView mPullRefreshListView;// 上拉刷新的控件
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_main_plus_sign);
        super.onCreate(savedInstanceState);
        initView();

    }

    private void initView() {

        // 标题+返回(控件)
        mCardBack = (ImageView) findViewById(R.id.m_iv_card_back);
        mCardTitle = (TextView) findViewById(R.id.m_tv_card_title);
        mAffairCardTitle = (LinearLayout) findViewById(R.id.m_affair_card_title);

        // 标题背景
        mLlCard = (LinearLayout) findViewById(R.id.m_ll_card);
        mRlCard = (RelativeLayout) findViewById(R.id.view_card_title_bar);
        // mLlBottom = (LinearLayout)findViewById(R.id.m_ll_bottom);

        user = DBHelper.getUser(this);
        mCompanyName = (TextView) findViewById(R.id.m_tv_company);
        mWeek = (TextView) findViewById(R.id.m_tv_week);
        mDay = (TextView) findViewById(R.id.m_tv_day);
        mSignlog = (TextView) findViewById(R.id.m_tv_sign_log);
        mSignIn = (ImageView) findViewById(R.id.m_iv_sign);

        Date date = new Date();
        mWeek.setText(CalendarUtils.getWeek());
        mDay.setText(DateUtils.getStringByPattern(date.getTime(), "yyyy-MM-dd"));

        setClick();
        setCardTitleColor();
        setTitleBarColor();

        initSignView();

    }

    /**
     * 初始化数据
     */
    private void initSignView() {
        totalCheckListDatas = new ArrayList<CheckListData>();
        checkListDatas = new ArrayList<CheckListData>();
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        adapter = new MainPlusCheckAdapter(this);
        mPullRefreshListView.setAdapter(adapter);
        mPullRefreshListView.setMode(Mode.BOTH);
        initIndicator();

        getNetWorkType();
        getCheckInList(page);

        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                page = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getCheckInList(page);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if (checkListDatas != null && checkListDatas.size() >= 10) {
                    page = page + 1;
                    getCheckInList(page);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainPlusSignInActivity.this, "请稍后，没有更多加载数据", Toast.LENGTH_SHORT).show();
                    mPullRefreshListView.onRefreshComplete();
                }
            }
        });
    }

    private void getNetWorkType() {
        // 1.判断是否有网络
        if (NetworkUtils.isNetworkConnected(this)) {
            // 2.如果wifi可用
            if (NetworkUtils.isWifiConnected(this)) {
                WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                checkinet = wifiInfo.getSSID();
                flag = 0;
                return;
            }
            // 3.如果手机网络可用
            if (NetworkUtils.isMobileConnected(this)) {
                checkinet = NetworkUtils.GetNetworkType(this);
                flag = 1;
                return;
            }
        }
    }

    /**
     * 设置标题颜色
     * 
     * @param cardType
     */
    private void setCardTitleColor() {
        mCardTitle.setText("签到");
        // mLlBottom.setBackgroundColor(getResources().getColor(R.color.plus_qing_jia));
        mLlCard.setBackgroundColor(getResources().getColor(R.color.plus_kao_qin));
        mRlCard.setBackgroundColor(getResources().getColor(R.color.plus_kao_qin));

    }

    /*
     * 设置沉浸栏样式
     */
    private void setTitleBarColor() {
        /**
         * 沉浸栏方式实现(android4.4以上)
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {// 4.4以上
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    private void setClick() {
        mSignlog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPlusSignInActivity.this, WebViewsActivity.class);
                intent.putExtra("url", Constants.PLUS_SIGN_URL + user.getId());
                startActivity(intent);
            }
        });

        mSignIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkData != null && checkData.getCompanyId() > 0) {
                    // 1.公司指定签到网络不为空
                    if (companySettings != null && companySettings.size() > 0) {
                        if (flag == 0) {// wifi状态下
                            boolean is_sign_net = isCompanyNet();
                            if (is_sign_net) {
                                Long companyId = checkData.getCompanyId();
                                Intent intent = new Intent(MainPlusSignInActivity.this, MainPlusSignActivity.class);
                                intent.putExtra("companyId", companyId + "");
                                intent.putExtra("checkinet", checkinet);
                                startActivity(intent);
                            } else {
                                showAlert("友情提示", "您当前连接的WiFi网络不是公司考勤要求的WiFi网络，是否继续打卡签到？");
                            }
                        } else if (flag == 1) {// 手机网络状态下
                            showAlert("友情提示", "您当前还未连接公司的WiFi网络，是否继续打卡签到？");
                        }
                    } else {
                        // 公司指定签到网络为空，继续签到
                        Long companyId = checkData.getCompanyId();
                        Intent intent = new Intent(MainPlusSignInActivity.this, MainPlusSignActivity.class);
                        intent.putExtra("companyId", companyId + "");
                        intent.putExtra("checkinet", checkinet);
                        startActivity(intent);
                    }

                } else {
                    Toast.makeText(MainPlusSignInActivity.this, "请选择签到公司", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
          
        });

        mCardBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAffairCardTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPlusSignInActivity.this, WebViewsActivity.class);
                intent.putExtra("url", Constants.CARD_PUNCH_SIGN_HELP_URL);
                startActivity(intent);
            }
        });
    }

    /**
     * 设置下拉刷新提示
     */
    private void initIndicator() {
        ILoadingLayout startLabels = mPullRefreshListView.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在刷新...");// 刷新时
        startLabels.setReleaseLabel("释放更新");// 下来达到一定距离时，显示的提示

        ILoadingLayout endLabels = mPullRefreshListView.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("上拉加载");
        endLabels.setRefreshingLabel("正在刷新...");// 刷新时
        endLabels.setReleaseLabel("释放加载");// 下来达到一定距离时，显示的提示
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        page = 1;
        totalCheckListDatas = null;
        checkListDatas = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCheckInList(page);
    }

    /**
     * 员工考勤记录接口
     */
    public void getCheckInList(final int page) {
        // 判断是否有网络
        if (!NetworkUtils.isNetworkConnected(MainPlusSignInActivity.this)) {
            Toast.makeText(MainPlusSignInActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user.getId());
        // map.put("page",""+page);
        AjaxParams params = new AjaxParams(map);
        new FinalHttp().post(Constants.URL_GET_CHECKIN_LISTS, params, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(MainPlusSignInActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                // json字符串转为集合对象
                                checkData = gson.fromJson(data, CheckData.class);
                                mCompanyName.setText(checkData.getCompanyName());
                                showData(checkData.getList());
                                getCompanySetting();
                            }
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            errorMsg = getString(R.string.servers_error);
                            mPullRefreshListView.onRefreshComplete();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            errorMsg = getString(R.string.param_missing);
                            mPullRefreshListView.onRefreshComplete();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            errorMsg = getString(R.string.param_illegal);
                            mPullRefreshListView.onRefreshComplete();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            errorMsg = msg;
                            mPullRefreshListView.onRefreshComplete();
                        } else {
                            errorMsg = getString(R.string.servers_error);
                            mPullRefreshListView.onRefreshComplete();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = getString(R.string.servers_error);
                    mPullRefreshListView.onRefreshComplete();
                }
                // 操作失败，显示错误信息
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    UIUtils.showToast(MainPlusSignInActivity.this, errorMsg);
                }
            }
        });
    }

    /**
     * 用户-公司配置信息接口
     */
    public void getCompanySetting() {
        // 判断是否有网络
        if (!NetworkUtils.isNetworkConnected(MainPlusSignInActivity.this)) {
            Toast.makeText(MainPlusSignInActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        /**
         * 设置类型 meeting-room = 会议室 meeting-type = 会议类型 assets-type = 资产类型 work-time = 上下班时间 checkin-net = 指定网络考勤wifi名称
         */
        map.put("setting_type", "checkin-net");
        map.put("user_id", user.getId());
        map.put("company_id", checkData.getCompanyId() + "");
        AjaxParams params = new AjaxParams(map);
        new FinalHttp().get(Constants.URL_GET_COMPANY_SETTING, params, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(MainPlusSignInActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                // json字符串转为集合对象
                                companySettings = gson.fromJson(data, new TypeToken<ArrayList<CompanySetting>>() {
                                }.getType());
                            }
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            errorMsg = getString(R.string.servers_error);
                            mPullRefreshListView.onRefreshComplete();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            errorMsg = getString(R.string.param_missing);
                            mPullRefreshListView.onRefreshComplete();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            errorMsg = getString(R.string.param_illegal);
                            mPullRefreshListView.onRefreshComplete();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            errorMsg = msg;
                            mPullRefreshListView.onRefreshComplete();
                        } else {
                            errorMsg = getString(R.string.servers_error);
                            mPullRefreshListView.onRefreshComplete();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = getString(R.string.servers_error);
                    mPullRefreshListView.onRefreshComplete();
                }
                // 操作失败，显示错误信息
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    UIUtils.showToast(MainPlusSignInActivity.this, errorMsg);
                }
            }
        });
    }

    /**
     * 处理数据加载的方法
     * 
     * @param list
     */
    private void showData(List<CheckListData> checkListDatas) {
        if (checkListDatas != null && checkListDatas.size() > 0) {
            if (page == 1) {
                totalCheckListDatas.clear();
            }
            for (CheckListData checkListData : checkListDatas) {
                totalCheckListDatas.add(checkListData);
            }
            // 给适配器赋值
            adapter.setData(totalCheckListDatas);
        }
        mPullRefreshListView.onRefreshComplete();
    }

    /**
     * 弹出确认|取消对话框
     * @param title
     * @param message
     */
    private void showAlert(String title, String message) {
        AlertWindow.dialog(MainPlusSignInActivity.this, title, message, new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Long companyId = checkData.getCompanyId();
                Intent intent = new Intent(MainPlusSignInActivity.this, MainPlusSignActivity.class);
                intent.putExtra("companyId", companyId + "");
                intent.putExtra("checkinet", checkinet);
                startActivity(intent);
            }
        }, new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                return;
            }
        });
    }
    /**
     * 判断是否公司指定签到网络
     * @return
     */
    private boolean isCompanyNet() {
        boolean is_going = false;// true=连接网络和签到网络一直，false不一致
        for (Iterator iterator = companySettings.iterator(); iterator.hasNext();) {
            CompanySetting companySetting = (CompanySetting) iterator.next();
            String cheinLower = checkinet.toLowerCase();
            cheinLower = cheinLower.substring(1,cheinLower.lastIndexOf("\""));
            String compamySettingLower = companySetting.getName().toLowerCase();
            if (StringUtils.isEquals(compamySettingLower, cheinLower)) {
                is_going = true;
            }
        }
        return is_going;
    }
}
