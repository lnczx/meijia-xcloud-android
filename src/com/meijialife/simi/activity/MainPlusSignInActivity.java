package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.MainPlusCheckAdapter;
import com.meijialife.simi.bean.CheckData;
import com.meijialife.simi.bean.CheckListData;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
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
public class MainPlusSignInActivity extends BaseActivity {
    
  
   private MainPlusCheckAdapter adapter;
   private CheckData checkData;
   private ArrayList<CheckListData> checkListDatas;
   private ArrayList<CheckListData> totalCheckListDatas;
   
  
   private TextView mCompanyName;
   private TextView mWeek;
   private TextView mDay;
   private TextView mSignlog;
   private ImageView mSignIn;
   private User user ;
   
   
   //布局控件定义
   private PullToRefreshListView mPullRefreshListView;//上拉刷新的控件 
   private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_main_plus_sign);
        super.onCreate(savedInstanceState);
        initView();

    }

    private void initView() {
        requestBackBtn();
        requestRightBtn();
        setTitleName("签到");
        user = DBHelper.getUser(this);
        mCompanyName = (TextView)findViewById(R.id.m_tv_company);
        mWeek = (TextView)findViewById(R.id.m_tv_week);
        mDay = (TextView)findViewById(R.id.m_tv_day);
        mSignlog = (TextView)findViewById(R.id.m_tv_sign_log);
        mSignIn = (ImageView)findViewById(R.id.m_iv_sign);
        
        
        Date date = new Date();
        mWeek.setText(CalendarUtils.getWeek());
        mDay.setText(DateUtils.getStringByPattern(date.getTime(), "yyyy-MM-dd"));
        
        setClick();
        
        totalCheckListDatas = new ArrayList<CheckListData>();
        checkListDatas = new ArrayList<CheckListData>();
        mPullRefreshListView = (PullToRefreshListView)findViewById(R.id.pull_refresh_list);
        adapter = new MainPlusCheckAdapter(this);
        mPullRefreshListView.setAdapter(adapter);
        mPullRefreshListView.setMode(Mode.BOTH);
        initIndicator();
        
        getCheckInList(page);
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                page = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getCheckInList(page);
                adapter.notifyDataSetChanged(); 
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if(checkListDatas!=null && checkListDatas.size()>=10){
                    page = page+1;
                    getCheckInList(page);
                    adapter.notifyDataSetChanged(); 
                }else {
                    Toast.makeText(MainPlusSignInActivity.this,"请稍后，没有更多加载数据",Toast.LENGTH_SHORT).show();
                    mPullRefreshListView.onRefreshComplete(); 
                }
            }
        });
    }
    
    private void setClick(){
        mSignlog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPlusSignInActivity.this,WebViewsActivity.class);
                intent.putExtra("url",Constants.PLUS_SIGN_URL+user.getId());
                startActivity(intent);                
            }
        });
        
        mSignIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Long companyId = checkData.getCompanyId();
                if(companyId <=0){
                    Toast.makeText(MainPlusSignInActivity.this,"请选择签到公司",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(MainPlusSignInActivity.this,MainPlusSignActivity.class);
                intent.putExtra("companyId",companyId+"");
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
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        page = 1;
        totalCheckListDatas = null;
        checkListDatas =  null;
    }
    
    @Override
        protected void onResume() {
            super.onResume();
            getCheckInList(page);
        }
    /**
     * 员工考勤记录接口
     */
     public void getCheckInList(final int page){
         //判断是否有网络
         if (!NetworkUtils.isNetworkConnected(MainPlusSignInActivity.this)) {
             Toast.makeText(MainPlusSignInActivity.this, getString(R.string.net_not_open), 0).show();
             return;
         }
         Map<String,String> map = new HashMap<String,String>();
         map.put("user_id",user.getId());
//         map.put("page",""+page);
         AjaxParams params = new AjaxParams(map);
         showDialog();
         new FinalHttp().post(Constants.URL_GET_CHECKIN_LISTS, params, new AjaxCallBack<Object>() {
             @Override
             public void onFailure(Throwable t, int errorNo, String strMsg) {
                 super.onFailure(t, errorNo, strMsg);
                 dismissDialog();
                 Toast.makeText(MainPlusSignInActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                checkData = gson.fromJson(data,CheckData.class);
                                mCompanyName.setText(checkData.getCompanyName());
                                showData(checkData.getList());
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
      * @param list
      */
     private void showData(List<CheckListData> checkListDatas){
         if(checkListDatas!=null && checkListDatas.size()>0){
             if(page==1){
                 totalCheckListDatas.clear();
             }
             for (CheckListData checkListData : checkListDatas) {
                 totalCheckListDatas.add(checkListData);
             }
             //给适配器赋值
             adapter.setData(totalCheckListDatas);
         }
         mPullRefreshListView.onRefreshComplete();
     }

}
