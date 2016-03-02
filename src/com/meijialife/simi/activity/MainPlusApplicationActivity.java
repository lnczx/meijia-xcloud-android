package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
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
import com.meijialife.simi.adapter.MainPlusAppAdapter;
import com.meijialife.simi.bean.AppToolsData;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.inter.ListItemClickHelp;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * @description：应用中心
 * @author： kerryg
 * @date:2015年12月3日
 */
public class MainPlusApplicationActivity extends BaseActivity implements ListItemClickHelp {

    //布局控件定义
    private PullToRefreshListView mPullRefreshListView;//上拉刷新的控件 
    private int page = 1;
    
    private ArrayList<AppToolsData> t_menu_List;// t=工具与服务
    private ArrayList<AppToolsData> t_menu_total_List;// t=工具与服务
    private ArrayList<AppToolsData> d_menu_List;// d=成长与赚钱
    private ArrayList<AppToolsData> d_menu_total_List;// d=成长与赚钱
    private ArrayList<AppToolsData> appToolsDatas;// 所有数据
    private MainPlusAppAdapter mainPlusAppAdapter1;
    
    private Button mButtonTool;
    private Button mButtonMoney;
    
    private short is_flag=0;//0=工作工具；1=成长赚钱
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.main_plus_center);
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setTitleName("应用中心");
        requestBackBtn();
        
        appToolsDatas = new ArrayList<AppToolsData>();
        t_menu_List = new ArrayList<AppToolsData>();
        t_menu_total_List = new ArrayList<AppToolsData>();
        d_menu_List = new ArrayList<AppToolsData>();
        d_menu_total_List = new ArrayList<AppToolsData>();
        
        mainPlusAppAdapter1 = new MainPlusAppAdapter(this,this);
        //按钮标题
        mButtonTool = (Button)findViewById(R.id.m_btn_tools);
        mButtonMoney = (Button)findViewById(R.id.m_btn_money);
        mButtonTool.setOnClickListener(new AppCenterClickListener());
        mButtonMoney.setOnClickListener(new AppCenterClickListener());
        mButtonTool.setSelected(true);
        mButtonMoney.setSelected(false);
        
        initAppCenterView();
    }
    /**
     * 标题按钮点击事件
     */
    class AppCenterClickListener implements OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.m_btn_tools:
                is_flag = 0;
                page = 1;
                mButtonTool.setSelected(true);
                mButtonMoney.setSelected(false);
                t_menu_total_List.clear();
                appToolsDatas.clear();
                getAppTools(page);
                break;
           case R.id.m_btn_money:
                is_flag = 1;
                page = 1;
                mButtonMoney.setSelected(true);
                mButtonTool.setSelected(false);
                d_menu_total_List.clear();
                appToolsDatas.clear();
                getAppTools(page);
                break;
            default:
                break;
            }
            
        }
    }
    /**
     *  初始化下拉刷新ListView
     */
    private void initAppCenterView(){
        mPullRefreshListView = (PullToRefreshListView)findViewById(R.id.pull_refresh_discount_list);
        mPullRefreshListView.setAdapter(mainPlusAppAdapter1);
        mPullRefreshListView.setMode(Mode.BOTH);
        initIndicator();
        getAppTools(page);
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                page = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getAppTools(page);
                mainPlusAppAdapter1.notifyDataSetChanged(); 
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if(is_flag==0){
                    if(appToolsDatas!=null && appToolsDatas.size()>=10){
                        page = page+1;
                        getAppTools(page);
                        mainPlusAppAdapter1.notifyDataSetChanged(); 
                    }else {
                        Toast.makeText(MainPlusApplicationActivity.this,"请稍后，没有更多加载数据",Toast.LENGTH_SHORT).show();
                        mPullRefreshListView.onRefreshComplete(); 
                    } 
                }else {
                    if(appToolsDatas!=null && appToolsDatas.size()>=10){
                        page = page+1;
                        getAppTools(page);
                        mainPlusAppAdapter1.notifyDataSetChanged(); 
                    }else {
                        Toast.makeText(MainPlusApplicationActivity.this,"请稍后，没有更多加载数据",Toast.LENGTH_SHORT).show();
                        mPullRefreshListView.onRefreshComplete(); 
                    }
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

    /**
     * 获得应用列表接口
     */
    private void getAppTools(int page) {

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }
        User user = DBHelper.getUser(this);
        Map<String, String> map = new HashMap<String, String>();
        map.put("app_type", "xcloud");
        map.put("user_id", user.getId());
        map.put("page",page+"");
        AjaxParams param = new AjaxParams(map);

        showDialog();
        new FinalHttp().get(Constants.URL_GET_APP_TOOLS, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(MainPlusApplicationActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                appToolsDatas = gson.fromJson(data, new TypeToken<ArrayList<AppToolsData>>() {
                                }.getType());
                                showData(appToolsDatas);
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
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    UIUtils.showToast(MainPlusApplicationActivity.this, errorMsg);
                }
            }
        });
    }

    private void dealData(ArrayList<AppToolsData> appToolsDatas) {
        t_menu_List.clear();
        d_menu_List.clear();
        for (Iterator iterator = appToolsDatas.iterator(); iterator.hasNext();) {
            AppToolsData appToolsData = (AppToolsData) iterator.next();
            String menu_type = appToolsData.getMenu_type().trim();
            if (menu_type.equals(Constants.MENU_TYPE_T)) {
                t_menu_List.add(appToolsData);
            } else if (menu_type.equals(Constants.MENU_TYPE_D)) {
                d_menu_List.add(appToolsData);
            }
        }
    }
    /**
     * 处理数据加载的方法
     * @param list
     */
    private void showData(ArrayList<AppToolsData> appToolsDatas){
        dealData(appToolsDatas);
        if(is_flag==0){
            if(page==1){
                t_menu_total_List.clear();
                appToolsDatas.clear();
                for (AppToolsData appToolsData : t_menu_List) {
                    t_menu_total_List.add(appToolsData);
                }
            }
            if(page>=2){
                for (AppToolsData appToolsData : t_menu_List) {
                    t_menu_total_List.add(appToolsData);
                }
            }
            //给适配器赋值
            mainPlusAppAdapter1.setData(t_menu_total_List); 
        }else {
            if(page==1){
                d_menu_total_List.clear();
                appToolsDatas.clear();
                for (AppToolsData appToolsData : d_menu_List) {
                    d_menu_total_List.add(appToolsData);
                }
            }
            if(page>=2){
                for (AppToolsData appToolsData : d_menu_List) {
                    d_menu_total_List.add(appToolsData);
                }
            }
            //给适配器赋值
            mainPlusAppAdapter1.setData(d_menu_total_List);   
        }
        mPullRefreshListView.onRefreshComplete();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        page = 1;
        t_menu_total_List = new ArrayList<AppToolsData>();
        d_menu_total_List = new ArrayList<AppToolsData>();
        appToolsDatas = new ArrayList<AppToolsData>();
    }

    @Override
    public void onClick() {
        getAppTools(page);
    }

}
