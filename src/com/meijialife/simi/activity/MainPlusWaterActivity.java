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
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.meijialife.simi.adapter.DefaultServiceAdapter;
import com.meijialife.simi.adapter.MainPlusWaterAdapter;
import com.meijialife.simi.bean.DefaultServiceData;
import com.meijialife.simi.bean.WaterData;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.inter.ListItemClickHelp;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * @description：应用中心---送水
 * @author： kerryg
 * @date:2016年3月3日 
 */
public class MainPlusWaterActivity extends Activity implements ListItemClickHelp {
    

    private MainPlusWaterAdapter mainPlusWaterAdapter;
    private DefaultServiceAdapter defaultServiceAdapter;

    
    private ImageView mCardBack;
    private TextView mCardTitle;
    private LinearLayout mLlCard;
    private RelativeLayout mRlCard;
    private LinearLayout mLlBottom;//布局底部控件
    private LinearLayout mAffairCardTitle;

    
    //创建卡片
    private TextView mTv1;
    private TextView mTv2;
    private HashMap<String,String> mCardTitleColor;
    private String mCardType;
    
    //下拉刷新
    private ArrayList<WaterData> myWaterList;
    private ArrayList<WaterData> totalWaterList;
    private PullToRefreshListView mPullRefreshListView;//上拉刷新的控件 
    private LinearLayout m_no_sings;
    private LinearLayout m_ll_no_signs;
    private int page = 1;
    
    private ArrayList<DefaultServiceData> myDefServiceList;
    private ArrayList<DefaultServiceData> totalDefServiceList;
    private int pageDef = 1;

    private boolean flag =true;//true=显示默认商品，fales表示显示送水列表
    private String titleName="";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.water_list_activity);
        super.onCreate(savedInstanceState);
        
        initView();
        
    }
    
    private void initView(){
        
        //接收参数
        mCardType = getIntent().getStringExtra("cardType");
        titleName = getIntent().getStringExtra("title");
        //标题+返回(控件)
        mCardBack = (ImageView) findViewById(R.id.m_iv_card_back);
        mCardTitle = (TextView) findViewById(R.id.m_tv_card_title);
        mAffairCardTitle = (LinearLayout)findViewById(R.id.m_affair_card_title);

        //标题背景
        mLlCard = (LinearLayout)findViewById(R.id.m_ll_card);
        mRlCard = (RelativeLayout)findViewById(R.id.view_card_title_bar);
        mLlBottom = (LinearLayout)findViewById(R.id.m_ll_bottom);
        m_no_sings = (LinearLayout)findViewById(R.id.m_no_sings);
        m_ll_no_signs = (LinearLayout)findViewById(R.id.m_ll_no_signs);
        
        //新建(控件)
        mTv1 = (TextView)findViewById(R.id.m_tv1);
        mTv2 = (TextView)findViewById(R.id.m_tv2);
      
        mTv1.setText("一键送水");
        mTv2.setText("服务记录");
       
        setOnClick();//设置点击事件
        setCardTitleColor(mCardType);//设置标题颜色
        setTitleBarColor();//设置沉浸栏样式
        
        initWaterView();
       
    }
    /**
     * 初始化布局
     */
    private void initWaterView(){
        myDefServiceList = new ArrayList<DefaultServiceData>();
        totalDefServiceList = new ArrayList<DefaultServiceData>();
        defaultServiceAdapter =new DefaultServiceAdapter(MainPlusWaterActivity.this);
      
        totalWaterList = new ArrayList<WaterData>();
        mPullRefreshListView = (PullToRefreshListView)findViewById(R.id.m_water_list);
        mainPlusWaterAdapter = new MainPlusWaterAdapter(MainPlusWaterActivity.this,this);
        if(flag){
            mPullRefreshListView.setAdapter(defaultServiceAdapter);
        }else {
            mPullRefreshListView.setAdapter(mainPlusWaterAdapter);
        }
        mPullRefreshListView.setMode(Mode.BOTH);
        initIndicator();
        getDefaultServiceListData(pageDef);
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if(flag){
                    pageDef = 1;
                    getDefaultServiceListData(pageDef);
                    defaultServiceAdapter.notifyDataSetChanged(); 
                }else {
                    page = 1;
                    getWaterListData(page);
                    mainPlusWaterAdapter.notifyDataSetChanged(); 
                }
               
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if(flag){
                    if(myDefServiceList!=null && myDefServiceList.size()>=10){
                        pageDef = pageDef+1;
                        getDefaultServiceListData(pageDef);
                        defaultServiceAdapter.notifyDataSetChanged(); 
                    }else {
                        Toast.makeText(MainPlusWaterActivity.this,"请稍后，没有更多加载数据",Toast.LENGTH_SHORT).show();
                        mPullRefreshListView.onRefreshComplete(); 
                    }
                }else {
                    if(myWaterList!=null && myWaterList.size()>=10){
                        page = page+1;
                        getWaterListData(page);
                        mainPlusWaterAdapter.notifyDataSetChanged(); 
                    }else {
                        Toast.makeText(MainPlusWaterActivity.this,"请稍后，没有更多加载数据",Toast.LENGTH_SHORT).show();
                        mPullRefreshListView.onRefreshComplete(); 
                    } 
                }
            }
        });
        
        mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(flag){
                    DefaultServiceData def = totalDefServiceList.get(position);
                    if(StringUtils.isNotEmpty(def.getDetail_url())){
                    Intent intent = new Intent(MainPlusWaterActivity.this,WebViewPartnerActivity.class);
                    intent.putExtra("url", def.getDetail_url());
                    intent.putExtra("title","服务详情");
                    intent.putExtra("dis_price",def.getDis_price());
                    intent.putExtra("flag",1);//0=发现服务详情，1=默认服务详情
                    intent.putExtra("defService", def);
                    startActivity(intent);
                    }
                }else{
                    WaterData waterData = totalWaterList.get(position);
                    Intent intent = new Intent(MainPlusWaterActivity.this,OrderDetailsActivity.class);
                    intent.putExtra("orderId", waterData.getOrder_id());
                    intent.putExtra("orderType", 99);
                    startActivity(intent);  
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
    /*
     * 设置点击事件
     */
    private void setOnClick(){
        
        mCardBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!flag){
                    flag = true;
                    mPullRefreshListView.setAdapter(defaultServiceAdapter);
                    getDefaultServiceListData(pageDef);
                }else {
                    finish();
                }
            }
        });
        mTv1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainPlusWaterActivity.this,MainPlusWaterOrderActivity.class));
            }
        });
        mTv2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = false;
                mPullRefreshListView.setAdapter(mainPlusWaterAdapter);
                getWaterListData(page);
            }
        });
        
        mAffairCardTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPlusWaterActivity.this, WebViewsActivity.class);
                intent.putExtra("url", Constants.CARD_WATER_HELP_URL);
                startActivity(intent);
            }
        });
    }
    /**
     * 设置沉浸栏样式
     */
    private void setTitleBarColor(){
        /**
         * 沉浸栏方式实现(android4.4以上)
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {// 4.4以上
            // 透明状态栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }
    
    /**
     * 设置标题颜色
     * @param cardType
     */
    private void setCardTitleColor(String cardType){
      mCardTitle.setText(titleName);
      mLlBottom.setBackgroundColor(getResources().getColor(R.color.plus_song_shui));
      mLlCard.setBackgroundColor(getResources().getColor(R.color.plus_song_shui));
      mRlCard.setBackgroundColor(getResources().getColor(R.color.plus_song_shui));
        
    }
  
    public void getWaterListData(int page) {
        String user_id = DBHelper.getUser(this).getId();
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id+"");
        map.put("page",page+"");
        AjaxParams param = new AjaxParams(map);

//        showDialog();
        new FinalHttp().get(Constants.URL_GET_WATER_LIST, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
//                dismissDialog();
                Toast.makeText(MainPlusWaterActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
//                dismissDialog();
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data").trim();
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            if (StringUtils.isNotEmpty(data.trim())) {
                                Gson gson = new Gson();
                                myWaterList = new ArrayList<WaterData>();
                                myWaterList = gson.fromJson(data, new TypeToken<ArrayList<WaterData>>() {
                                }.getType());
                                showData(myWaterList);
                                if(myWaterList.size()>0){
                                    m_ll_no_signs.setVisibility(View.VISIBLE);
                                    m_no_sings.setVisibility(View.GONE);
                                }else {
                                    m_ll_no_signs.setVisibility(View.GONE);
                                    m_no_sings.setVisibility(View.VISIBLE);
                                }
                            } else {
                               mPullRefreshListView.setVisibility(View.GONE);
                               m_no_sings.setVisibility(View.VISIBLE);
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
                    mPullRefreshListView.onRefreshComplete();
                    UIUtils.showToast(MainPlusWaterActivity.this, errorMsg);
                }
            }
        });
    }
    
    /**
     * 处理数据加载的方法
     * @param list
     */
    private void showData(List<WaterData> myWaterList){
        if(page==1){
            totalWaterList.clear();
            for (WaterData water : myWaterList) {
                totalWaterList.add(water);
            }
        }
        if(page>=2){
            for (WaterData water : myWaterList) {
                totalWaterList.add(water);
            }
        }
        //给适配器赋值
        mainPlusWaterAdapter.setData(totalWaterList);
        mPullRefreshListView.onRefreshComplete();
    }
    
    /**
     * 获取默认商品列表
     * @param page
     */
    public void getDefaultServiceListData(int page) {
        String user_id = DBHelper.getUser(this).getId();
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id+"");
        map.put("service_type_id", "239");
        map.put("page",pageDef+"");
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().get(Constants.GET_DEF_SERVICE_URL, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(MainPlusWaterActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                        String data = obj.getString("data").trim();
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            if (StringUtils.isNotEmpty(data.trim())) {
                                Gson gson = new Gson();
                                myDefServiceList = new ArrayList<DefaultServiceData>();
                                myDefServiceList = gson.fromJson(data, new TypeToken<ArrayList<DefaultServiceData>>() {
                                }.getType());
                                showDefServiceData(myDefServiceList);
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
                    mPullRefreshListView.onRefreshComplete();
                    UIUtils.showToast(MainPlusWaterActivity.this, errorMsg);
                }
            }
        });
    }
    private void showDefServiceData(List<DefaultServiceData> myDefServiceList){
        if(pageDef==1){
            totalDefServiceList.clear();
            for (DefaultServiceData def : myDefServiceList) {
                totalDefServiceList.add(def);
            }
        }
        if(pageDef>=2){
            for (DefaultServiceData def : myDefServiceList) {
                totalDefServiceList.add(def);
            }
        }
        //给适配器赋值
        defaultServiceAdapter.setData(totalDefServiceList);
        mPullRefreshListView.onRefreshComplete();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        page =1;
        totalWaterList = new ArrayList<WaterData>();
        myWaterList = new ArrayList<WaterData>();
        pageDef=1;
        flag =true;
        totalDefServiceList = new ArrayList<DefaultServiceData>();
        myDefServiceList = new ArrayList<DefaultServiceData>();
        
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        flag = true;
        mPullRefreshListView.setAdapter(defaultServiceAdapter);
        getDefaultServiceListData(pageDef);
    }

    @Override
    public void onClick() {
        mPullRefreshListView.setAdapter(mainPlusWaterAdapter);
        getWaterListData(page);
    }

}
