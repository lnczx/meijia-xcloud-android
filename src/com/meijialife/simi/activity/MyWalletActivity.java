package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.MyWalletAdapter;
import com.meijialife.simi.bean.MyWalletData;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * 我的钱包
 * 
 */
public class MyWalletActivity extends BaseActivity implements OnClickListener {

    private Button btn_recharge;// 充值

    private MyWalletAdapter adapter;
    TextView  tv_money;
    
    private ArrayList<MyWalletData> myWalletDataList;
    private ArrayList<MyWalletData> totalWalletDataList;
    //布局控件定义
    private PullToRefreshListView mPullRefreshListView;//上拉刷新的控件 
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.my_wallet_activity);
        super.onCreate(savedInstanceState);

        initView();

    }

    private void initView() {
        setTitleName("我的钱包");
        requestBackBtn();

        btn_recharge = (Button) findViewById(R.id.btn_recharge);
        btn_recharge.setOnClickListener(this);

        tv_money = (TextView) findViewById(R.id.tv_money);
        initWalletView();
        UserInfo userInfo = DBHelper.getUserInfo(MyWalletActivity.this);
        if(null!=userInfo){
            tv_money.setText(userInfo.getRest_money());
        }
        
    }
    /**
     * 初始化钱包列表
     */
    private void initWalletView(){
        totalWalletDataList = new ArrayList<MyWalletData>();
        mPullRefreshListView = (PullToRefreshListView)findViewById(R.id.pull_refresh_wallet_list);
        adapter = new MyWalletAdapter(this);
        mPullRefreshListView.setAdapter(adapter);
        mPullRefreshListView.setMode(Mode.BOTH);
        initIndicator();
        getMyWalletList(page);
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                page = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getMyWalletList(page);
                adapter.notifyDataSetChanged(); 
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if(myWalletDataList!=null && myWalletDataList.size()>=10){
                    page = page+1;
                    getMyWalletList(page);
                    adapter.notifyDataSetChanged(); 
                }else {
                    Toast.makeText(MyWalletActivity.this,"请稍后，没有更多加载数据",Toast.LENGTH_SHORT).show();
                    mPullRefreshListView.onRefreshComplete(); 
                }
            }
        });
    }
    /**
     * 设置下拉刷新提示
     */
    private void initIndicator()  
    {  
        ILoadingLayout startLabels = mPullRefreshListView  
                .getLoadingLayoutProxy(true, false);  
        startLabels.setPullLabel("下拉刷新");// 刚下拉时，显示的提示  
        startLabels.setRefreshingLabel("正在刷新...");// 刷新时  
        startLabels.setReleaseLabel("释放更新");// 下来达到一定距离时，显示的提示  
  
        ILoadingLayout endLabels = mPullRefreshListView.getLoadingLayoutProxy(  
                false, true);  
        endLabels.setPullLabel("上拉加载");
        endLabels.setRefreshingLabel("正在刷新...");// 刷新时  
        endLabels.setReleaseLabel("释放加载");// 下来达到一定距离时，显示的提示  
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_recharge: // 充值
            startActivity(new Intent(MyWalletActivity.this, AccountRechangeActivity.class));
            
            break;

        default:
            break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        UserInfo userInfo = DBHelper.getUserInfo(MyWalletActivity.this);
        if(null!=userInfo){
            tv_money.setText(userInfo.getRest_money());
        }
        getMyWalletList(page);
//        updateUserInfo(MyWalletActivity.this);
    }
    
   /* private static void updateUserInfo(final Activity activity) {

        if (!NetworkUtils.isNetworkConnected(activity)) {
            Toast.makeText(activity, activity.getString(R.string.net_not_open), 0).show();
            return;
        }

        User user = DBHelper.getUser(activity);
        if (null == user) {
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user.getId());
        AjaxParams param = new AjaxParams(map);

        new FinalHttp().get(Constants.URL_GET_USER_INFO, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(activity, activity.getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                UserInfo userInfo = gson.fromJson(data, UserInfo.class);
                                DBHelper.updateUserInfo(activity, userInfo);
                            } else {
                                // UIUtils.showToast(PayOrderActivity.this, "数据错误");
                            }
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            errorMsg = activity.getString(R.string.servers_error);
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            errorMsg = activity.getString(R.string.param_missing);
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            errorMsg = activity.getString(R.string.param_illegal);
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            errorMsg = msg;
                        } else {
                            errorMsg = activity.getString(R.string.servers_error);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = activity.getString(R.string.servers_error);

                }
                // 操作失败，显示错误信息|
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    UIUtils.showToast(activity, errorMsg);
                }
            }
        });

    }*/
    public void getMyWalletList(int page){
        //判断是否有网络
        if (!NetworkUtils.isNetworkConnected(MyWalletActivity.this)) {
            Toast.makeText(MyWalletActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }
        User user = DBHelper.getUser(MyWalletActivity.this);
        Map<String,String> map = new HashMap<String,String>();
        map.put("user_id",user.getId());
        map.put("page",""+page);
        AjaxParams params = new AjaxParams(map);
        showDialog();
        new FinalHttp().get(Constants.URL_GET_WALLET_LIST, params, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(MyWalletActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                dismissDialog();
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            if (StringUtils.isNotEmpty(data)) {
                                Gson gson = new Gson();
                                //json字符串转为集合对象
                                myWalletDataList = gson.fromJson(data, new TypeToken<ArrayList<MyWalletData>>() {
                                }.getType());
                                //给适配器赋值
                                showData(myWalletDataList);
//                                adapter.setData(myWalletDataList);
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
                            mPullRefreshListView.onRefreshComplete();
                            errorMsg = getString(R.string.servers_error);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mPullRefreshListView.onRefreshComplete();
                    errorMsg = getString(R.string.servers_error);

                }
                // 操作失败，显示错误信息
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    mPullRefreshListView.onRefreshComplete();
                    UIUtils.showToast(MyWalletActivity.this, errorMsg);
                }
            }
        });
    }
    /**
     * 处理数据加载的方法
     * @param list
     */
    private void showData(List<MyWalletData> myWalletDataList){
        if(myWalletDataList!=null && myWalletDataList.size()>0){
            if(page==1){
                totalWalletDataList.clear();
                for (MyWalletData myWalletData : myWalletDataList) {
                    totalWalletDataList.add(myWalletData);
                }
            }
            if(page>=2){
                for (MyWalletData myWalletData : myWalletDataList) {
                    totalWalletDataList.add(myWalletData);
                }
            }
            //给适配器赋值
            adapter.setData(totalWalletDataList);
        }else {
            adapter.setData(new ArrayList<MyWalletData>() );
        }
        mPullRefreshListView.onRefreshComplete();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        page = 1;
        totalWalletDataList = new ArrayList<MyWalletData>();
        myWalletDataList = new ArrayList<MyWalletData>();
    }
    
}
