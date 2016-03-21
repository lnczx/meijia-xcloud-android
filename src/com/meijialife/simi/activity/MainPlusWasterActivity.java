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
import com.meijialife.simi.adapter.MainPlusWasterAdapter;
import com.meijialife.simi.bean.WasterData;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.inter.ListItemClickHelp;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * @description：应用中心---废品回收
 * @author： kerryg
 * @date:2016年3月3日 
 */
public class MainPlusWasterActivity extends Activity implements ListItemClickHelp {
    

    private MainPlusWasterAdapter mainPlusWasterAdapter;
    
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
    private ArrayList<WasterData> myWasterList;
    private ArrayList<WasterData> totalWasterList;
    private PullToRefreshListView mPullRefreshListView;//上拉刷新的控件 
    private int page = 1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.water_list_activity);
        super.onCreate(savedInstanceState);
        
        initView();
        
    }
    
    private void initView(){
        //标题+返回(控件)
        mCardBack = (ImageView) findViewById(R.id.m_iv_card_back);
        mCardTitle = (TextView) findViewById(R.id.m_tv_card_title);
        mAffairCardTitle = (LinearLayout)findViewById(R.id.m_affair_card_title);

        //标题背景
        mLlCard = (LinearLayout)findViewById(R.id.m_ll_card);
        mRlCard = (RelativeLayout)findViewById(R.id.view_card_title_bar);
        mLlBottom = (LinearLayout)findViewById(R.id.m_ll_bottom);
        //新建(控件)
        mTv1 = (TextView)findViewById(R.id.m_tv1);
        mTv2 = (TextView)findViewById(R.id.m_tv2);
      
        mTv1.setText("上门回收");
        mTv2.setText("参考价格");
       
        setOnClick();//设置点击事件
        setCardTitleColor();//设置标题颜色
        setTitleBarColor();//设置沉浸栏样式
        
        initWaterView();
       
    }
    /**
     * 初始化布局
     */
    private void initWaterView(){
        totalWasterList = new ArrayList<WasterData>();
        mPullRefreshListView = (PullToRefreshListView)findViewById(R.id.m_water_list);
        mainPlusWasterAdapter = new MainPlusWasterAdapter(MainPlusWasterActivity.this,this);
        mPullRefreshListView.setAdapter(mainPlusWasterAdapter);
        mPullRefreshListView.setMode(Mode.BOTH);
        initIndicator();
        getWasterListData(page);
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                page = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getWasterListData(page);
                mainPlusWasterAdapter.notifyDataSetChanged(); 
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if(myWasterList!=null && myWasterList.size()>=10){
                    page = page+1;
                    getWasterListData(page);
                    mainPlusWasterAdapter.notifyDataSetChanged(); 
                }else {
                    Toast.makeText(MainPlusWasterActivity.this,"请稍后，没有更多加载数据",Toast.LENGTH_SHORT).show();
                    mPullRefreshListView.onRefreshComplete(); 
                }
            }
        });
        
        mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WasterData wasterData = totalWasterList.get(position);
                Intent intent = new Intent(MainPlusWasterActivity.this,OrderDetailsActivity.class);
                intent.putExtra("orderId", wasterData.getOrder_id());
                intent.putExtra("orderType", 1);
                startActivity(intent);
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
                finish();
            }
        });
        mTv1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainPlusWasterActivity.this,MainPlusWasterOrderActivity.class));
            }
        });
        mTv2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPlusWasterActivity.this,WebViewsActivity.class);
                intent.putExtra("url",Constants.H5_WASTER_URL);
                startActivity(intent);
                
            }
        });
        
        mAffairCardTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPlusWasterActivity.this, WebViewsActivity.class);
                intent.putExtra("url", Constants.CARD_RECYCLE_HELP_URL);
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
    private void setCardTitleColor(){
      mCardTitle.setText("废品回收");
      mLlBottom.setBackgroundColor(getResources().getColor(R.color.plus_song_feipin));
      mLlCard.setBackgroundColor(getResources().getColor(R.color.plus_song_feipin));
      mRlCard.setBackgroundColor(getResources().getColor(R.color.plus_song_feipin));
        
    }
    /**
     * 废品回收列表接口
     * @param page
     */
    public void getWasterListData(int page) {
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
        new FinalHttp().get(Constants.URL_GET_LIST_WASTER, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
//                dismissDialog();
                Toast.makeText(MainPlusWasterActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                myWasterList = new ArrayList<WasterData>();
                                myWasterList = gson.fromJson(data, new TypeToken<ArrayList<WasterData>>() {
                                }.getType());
                                showData(myWasterList);
                            } else {
                                mainPlusWasterAdapter.setData(new ArrayList<WasterData>());
                                mPullRefreshListView.onRefreshComplete();
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
                    UIUtils.showToast(MainPlusWasterActivity.this, errorMsg);
                }
            }
        });
    }
    
    /**
     * 处理数据加载的方法
     * @param list
     */
    private void showData(List<WasterData> myWasterList){
        if(page==1){
            totalWasterList.clear();
            for (WasterData water : myWasterList) {
                totalWasterList.add(water);
            }
        }
        if(page>=2){
            for (WasterData water : myWasterList) {
                totalWasterList.add(water);
            }
        }
        //给适配器赋值
        mainPlusWasterAdapter.setData(totalWasterList);
        mPullRefreshListView.onRefreshComplete();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        page =1;
        totalWasterList = new ArrayList<WasterData>();
        myWasterList = new ArrayList<WasterData>();
        
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        getWasterListData(page);
    }

    @Override
    public void onClick() {
        getWasterListData(page);
    }

}
