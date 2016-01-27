package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.R.integer;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.meijialife.simi.adapter.SecretaryAdapter;
import com.meijialife.simi.bean.MyOrder;
import com.meijialife.simi.bean.Partner;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * @description：发现详情--秘书助理，综合服务，设计策划
 * @author： kerryg
 * @date:2015年11月13日 
 */
public class Find2DetailActivity extends BaseActivity {

    private ListView listview;
    private SecretaryAdapter adapter;//服务商适配器
    private ArrayList<Partner> partnerList; // 所有服务商--秘书列表
    private String service_type_ids;
    private String title_name;
    
    private ArrayList<Partner> myPartnerList;
    private ArrayList<Partner> totalPartnerList;
    //布局控件定义
    private PullToRefreshListView mPullRefreshListView;//上拉刷新的控件 
    private int page = 1;
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.find_2_detail_activity);
        super.onCreate(savedInstanceState);
        init();
    }
    
    /*
     * 初始化适配器
     */
    public void init() {
        
        title_name = getIntent().getStringExtra("title_name");
        service_type_ids = getIntent().getStringExtra("service_type_ids");
        requestBackBtn();
        setTitleName(title_name);
        initPartnerView();
    }
    private void initPartnerView(){
        totalPartnerList = new ArrayList<Partner>();
        myPartnerList = new ArrayList<Partner>();
        mPullRefreshListView = (PullToRefreshListView)findViewById(R.id.pull_refresh_detail_list);
        adapter = new SecretaryAdapter(this);   
        mPullRefreshListView.setAdapter(adapter);
        mPullRefreshListView.setMode(Mode.BOTH);
        initIndicator();
        getPartnerList(service_type_ids,page);
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                page = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getPartnerList(service_type_ids,page);
                adapter.notifyDataSetChanged(); 
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if(myPartnerList!=null && myPartnerList.size()>=10){
                    page = page+1;
                    getPartnerList(service_type_ids,page);
                    adapter.notifyDataSetChanged(); 
                }else {
                    Toast.makeText(Find2DetailActivity.this,"请稍后，没有更多加载数据",Toast.LENGTH_SHORT).show();
                    mPullRefreshListView.onRefreshComplete(); 
                }
            }
        });
        mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Partner partner = partnerList.get(position);
                Intent intent = new Intent(Find2DetailActivity.this,PartnerActivity.class);
                intent.putExtra("Partner",partnerList.get(position));
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
    /**
     * 获取对应的服务商列表接口
     * @param service_type_ids
     */
    public void getPartnerList(String service_type_ids,int page) {
        String user_id = DBHelper.getUser(this).getId();
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id + "");
        map.put("page", ""+page);
        map.put("service_type_ids", service_type_ids);
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().get(Constants.URL_GET_USER_LIST, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(Find2DetailActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                myPartnerList = gson.fromJson(data, new TypeToken<ArrayList<Partner>>() {
                                }.getType());
//                                adapter.setData(partnerList);
                                showData(myPartnerList);
                            } /*else {
                                adapter.setData(new ArrayList<Partner>());
                            }*/
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
                    mPullRefreshListView.onRefreshComplete();
                    errorMsg = getString(R.string.servers_error);
                }
                // 操作失败，显示错误信息
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    mPullRefreshListView.onRefreshComplete();
                    UIUtils.showToast(Find2DetailActivity.this, errorMsg);
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        page = 1;
        myPartnerList = null;
        totalPartnerList = null;
    }
    /**
     * 处理数据加载的方法
     * @param list
     */
    private void showData(List<Partner> myParterList){
        if(myParterList!=null && myParterList.size()>0){
            if(page==1){
                totalPartnerList.clear();
            }
            for (Partner partner : myParterList) {
                totalPartnerList.add(partner);
            }
            //给适配器赋值
            adapter.setData(totalPartnerList);
        }
        mPullRefreshListView.onRefreshComplete();
    }
}
