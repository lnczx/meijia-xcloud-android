package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
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
import android.widget.Button;
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
import com.meijialife.simi.adapter.MainPlusLeaveAdapter;
import com.meijialife.simi.bean.LeaveData;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * @description：加号---请假列表
 * @author： kerryg
 * @date:2015年12月3日
 */
public class MainPlusLeaveListActivity extends Activity{

    // 布局控件定义
    private PullToRefreshListView mPullRefreshListView;// 上拉刷新的控件
    private int page = 1;

    private ArrayList<LeaveData> myLeaveList;// d=成长与赚钱
    private ArrayList<LeaveData> totalLeaveList;// d=成长与赚钱
    private MainPlusLeaveAdapter mainPlusLeaveAdapter;

    private Button mButtonTool;
    private Button mButtonMoney;

    private ImageView mCardBack;
    private TextView mCardTitle;
    private LinearLayout mLlCard;
    private RelativeLayout mRlCard;
    private LinearLayout mLlBottom;//布局底部控件
    private LinearLayout mAffairCardTitle;
    
    
    private LinearLayout mLlNoSigns1;
    private LinearLayout mLlNoSigns2;

    
    private int leave_from = 0;// 0=我发起的；1=我审批的
    private String titleName="";
    //创建卡片
    private TextView mTv1;
    private TextView mTv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.main_plus_leave_list);
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {

        myLeaveList = new ArrayList<LeaveData>();
        totalLeaveList = new ArrayList<LeaveData>();
        mainPlusLeaveAdapter = new MainPlusLeaveAdapter(this);
        titleName = getIntent().getStringExtra("title");
    
        // 按钮标题
        mButtonTool = (Button) findViewById(R.id.m_btn_tools);
        mButtonMoney = (Button) findViewById(R.id.m_btn_money);
        mButtonTool.setOnClickListener(new AppCenterClickListener());
        mButtonMoney.setOnClickListener(new AppCenterClickListener());
        mButtonTool.setSelected(true);
        mButtonMoney.setSelected(false);
        
        //新建(控件)
        mTv1 = (TextView)findViewById(R.id.m_tv1);
        mTv2 = (TextView)findViewById(R.id.m_tv2);
      
        mTv1.setText("我要请假");
        mTv2.setText("假单统计");
        //标题+返回(控件)
        mCardBack = (ImageView) findViewById(R.id.m_iv_card_back);
        mCardTitle = (TextView) findViewById(R.id.m_tv_card_title);
        mAffairCardTitle = (LinearLayout) findViewById(R.id.m_affair_card_title);
        mLlNoSigns1 = (LinearLayout) findViewById(R.id.m_ll_no_signs);
        mLlNoSigns2 = (LinearLayout) findViewById(R.id.m_no_sings);
        
        //标题背景
        mLlCard = (LinearLayout)findViewById(R.id.m_ll_card);
        mRlCard = (RelativeLayout)findViewById(R.id.view_card_title_bar);
        mLlBottom = (LinearLayout)findViewById(R.id.m_ll_bottom);

        setOnClick();
        setCardTitleColor();
        setTitleBarColor();
        
        initLeaveView();
        
    }
    /*
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
                startActivity(new Intent(MainPlusLeaveListActivity.this,MainPlusLeaveActivity.class));
            }
        });
       /* mTv2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPlusLeaveListActivity.this,WebViewsActivity.class);
                intent.putExtra("url",Constants.H5_TEAM_URL);
                startActivity(intent);
                
            }
        });*/
        mAffairCardTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPlusLeaveListActivity.this, WebViewsActivity.class);
                intent.putExtra("url", Constants.CARD_LAEVE_PASS_HELP_URL);
                startActivity(intent);
            }
        });
    }
    /**
     * 设置标题颜色
     * @param cardType
     */
    private void setCardTitleColor(){
      mCardTitle.setText(titleName);
      mLlBottom.setBackgroundColor(getResources().getColor(R.color.plus_qing_jia));
      mLlCard.setBackgroundColor(getResources().getColor(R.color.plus_qing_jia));
      mRlCard.setBackgroundColor(getResources().getColor(R.color.plus_qing_jia));
        
    }
    /**
     * 标题按钮点击事件
     */
    class AppCenterClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.m_btn_tools:
                leave_from = 0;//我发起的
                page = 1;
                mButtonTool.setSelected(true);
                mButtonMoney.setSelected(false);
                getLeaveList(page, leave_from);
                break;
            case R.id.m_btn_money://我审批的
                leave_from = 1;
                page = 1;
                mButtonMoney.setSelected(true);
                mButtonTool.setSelected(false);
                getLeaveList(page, leave_from);
                break;
            default:
                break;
            }

        }
    }

    /**
     * 初始化下拉刷新ListView
     */
    private void initLeaveView() {
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_discount_list);
        mPullRefreshListView.setAdapter(mainPlusLeaveAdapter);
        mPullRefreshListView.setMode(Mode.BOTH);
        initIndicator();
        getLeaveList(page,leave_from);
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                page = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getLeaveList(page,leave_from);
                mainPlusLeaveAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                    if (myLeaveList != null && myLeaveList.size() >= 10) {
                        page = page + 1;
                        getLeaveList(page,leave_from);
                        mainPlusLeaveAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(MainPlusLeaveListActivity.this, "请稍后，没有更多加载数据", Toast.LENGTH_SHORT).show();
                        mPullRefreshListView.onRefreshComplete();
                    }
            }
        });
        mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LeaveData leaveData = totalLeaveList.get(position);
                Intent intent = new Intent(MainPlusLeaveListActivity.this,MainPlusLeaveDetailActivity.class);
                intent.putExtra("leave_id",leaveData.getLeave_id());
                intent.putExtra("flag",leave_from);
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

    /**
     * 获得用户请假列表
     */
    private void getLeaveList(int page, int leaveFrom) {

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }
        User user = DBHelper.getUser(this);
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user.getId());
        map.put("page", page + "");
        map.put("leave_from", leaveFrom+"");
        map.put("page", page + "");

        AjaxParams param = new AjaxParams(map);

        //showDialog();
        new FinalHttp().get(Constants.GET_LEAVE_ORDER_URL, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
               // dismissDialog();
                Toast.makeText(MainPlusLeaveListActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                //dismissDialog();
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            if (StringUtils.isNotEmpty(data)) {
                                Gson gson = new Gson();
                                myLeaveList = gson.fromJson(data, new TypeToken<ArrayList<LeaveData>>() {
                                }.getType());
                                if(myLeaveList.size()>0){
                                    mLlNoSigns1.setVisibility(View.VISIBLE);
                                    mLlNoSigns2.setVisibility(View.GONE);  
                                    showData(myLeaveList);
                                }else {
                                    mLlNoSigns1.setVisibility(View.GONE);
                                    mLlNoSigns2.setVisibility(View.VISIBLE);  
                                }
                            }else {
                                mLlNoSigns1.setVisibility(View.GONE);
                                mLlNoSigns2.setVisibility(View.VISIBLE);
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
                    UIUtils.showToast(MainPlusLeaveListActivity.this, errorMsg);
                }
            }
        });
    }

    /**
     * 处理数据加载的方法
     * 
     * @param list
     */
    private void showData(ArrayList<LeaveData> myLeaveList) {
        if (page == 1) {
            totalLeaveList.clear();
            for (LeaveData leaveData : myLeaveList) {
                totalLeaveList.add(leaveData);
            }
        }
        if (page >= 2) {
            for (LeaveData leaveData : myLeaveList) {
                totalLeaveList.add(leaveData);
            }
        }
        mainPlusLeaveAdapter.setData(totalLeaveList);
        mPullRefreshListView.onRefreshComplete();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getLeaveList(page,leave_from);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        page = 1;
        myLeaveList = new ArrayList<LeaveData>();
        totalLeaveList = new ArrayList<LeaveData>();
    }
}
