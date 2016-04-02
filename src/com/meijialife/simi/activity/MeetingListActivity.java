package com.meijialife.simi.activity;

import java.util.ArrayList;
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
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.MainPlusCleanAdapter;
import com.meijialife.simi.adapter.MeetingListAdapter;
import com.meijialife.simi.bean.CleanData;
import com.meijialife.simi.bean.MeetingData;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * @description：会议室列表
 * @author： kerryg
 * @date:2016年3月3日 
 */
public class MeetingListActivity extends BaseActivity  {
    

    private MeetingListAdapter adapter;
    
    private ImageView mCardBack;
    private TextView mCardTitle;
    private LinearLayout mLlCard;
    private RelativeLayout mRlCard;
    private LinearLayout mLlBottom;//布局底部控件
    
    //创建卡片
    private TextView mTv1;
    private TextView mTv2;
    private HashMap<String,String> mCardTitleColor;
    private String mCardType;
    
    //下拉刷新
    private ArrayList<MeetingData> myMeetingList;
    private ArrayList<MeetingData> totalMeetingList;
    private PullToRefreshListView mPullRefreshListView;//上拉刷新的控件 
    private int page = 1;
    private RelativeLayout m_rl_meeting;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.meeting_list_activity);
        super.onCreate(savedInstanceState);
        initView();
        
    }
    
    private void initView(){
        requestBackBtn();
        setTitleName("会议室列表");
        
        initMeetingView();
       
    }
    /**
     * 初始化布局
     */
    private void initMeetingView(){
        totalMeetingList = new ArrayList<MeetingData>();
        
        m_rl_meeting = (RelativeLayout)findViewById(R.id.m_rl_meeting);
        m_rl_meeting.setVisibility(View.VISIBLE);
        mPullRefreshListView = (PullToRefreshListView)findViewById(R.id.m_meeting_list);
        adapter = new MeetingListAdapter(MeetingListActivity.this);
        mPullRefreshListView.setAdapter(adapter);
        mPullRefreshListView.setMode(Mode.BOTH);
        initIndicator();
        getMeetingListData(page);
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                page = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getMeetingListData(page);
                adapter.notifyDataSetChanged(); 
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if(myMeetingList!=null && myMeetingList.size()>=10){
                    page = page+1;
                    getMeetingListData(page);
                    adapter.notifyDataSetChanged(); 
                }else {
                    Toast.makeText(MeetingListActivity.this,"请稍后，没有更多加载数据",Toast.LENGTH_SHORT).show();
                    mPullRefreshListView.onRefreshComplete(); 
                }
            }
        });
        
        mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MeetingData meetingData = totalMeetingList.get(position);
                Intent intent = new Intent();
                intent.putExtra("meetingData",meetingData);
                setResult(RESULT_OK,intent);
                MeetingListActivity.this.finish();
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
     * 会议室列表接口
     * @param page
     */
    public void getMeetingListData(int page) {
        UserInfo  userInfo = DBHelper.getUserInfo(this);
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", userInfo.getUser_id()+"");
        map.put("company_id", userInfo.getCompany_id());
        map.put("setting_type","meeting-room");
        map.put("page",page+"");
        AjaxParams param = new AjaxParams(map);
//        showDialog();
        new FinalHttp().get(Constants.URL_GET_COMPANY_SETTING, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
//                dismissDialog();
                Toast.makeText(MeetingListActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                myMeetingList = new ArrayList<MeetingData>();
                                myMeetingList = gson.fromJson(data, new TypeToken<ArrayList<MeetingData>>() {
                                }.getType());
                                showData(myMeetingList);
                            } else {
                                adapter.setData(new ArrayList<MeetingData>());
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
                    UIUtils.showToast(MeetingListActivity.this, errorMsg);
                }
            }
        });
    }
    
    /**
     * 处理数据加载的方法
     * @param list
     */
    private void showData(List<MeetingData> myMeetingList){
        if(page==1){
            totalMeetingList.clear();
            for (MeetingData meeting : myMeetingList) {
                totalMeetingList.add(meeting);
            }
        }
        if(page>=2){
            for (MeetingData meeting : myMeetingList) {
                totalMeetingList.add(meeting);
            }
        }
        //给适配器赋值
        adapter.setData(totalMeetingList);
        mPullRefreshListView.onRefreshComplete();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        page =1;
        totalMeetingList = new ArrayList<MeetingData>();
        myMeetingList = new ArrayList<MeetingData>();
        
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        getMeetingListData(page);
    }
}
