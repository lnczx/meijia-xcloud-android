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
import com.meijialife.simi.adapter.WaterBandAdapter;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.bean.WaterBand;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;


/**
 * @description：水品牌列表
 * @author： kerryg
 * @date:2015年11月14日 
 */
public class MainPlusWaterBandActivity extends BaseActivity implements OnItemClickListener{

    //定义全局变量
    private WaterBandAdapter adapter;
    private User user;
    private ArrayList<WaterBand> myWaterBandList;
    private ArrayList<WaterBand> totalWaterBandList;
    //布局控件定义
    private PullToRefreshListView mPullRefreshListView;//上拉刷新的控件 
    private int page = 1;
    private String serviceTypeId ="239";
    private String flag="0";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.my_order_activity);
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
    	setTitleName("选择产品");
    	requestBackBtn();
    	/**
    	 * 列表赋值
    	 */
    	flag = getIntent().getStringExtra("flag");
    	totalWaterBandList = new ArrayList<WaterBand>();
    	myWaterBandList = new ArrayList<WaterBand>();
    	mPullRefreshListView = (PullToRefreshListView)findViewById(R.id.pull_refresh_list);
    	mPullRefreshListView.setOnItemClickListener(this);
        adapter = new WaterBandAdapter(this);
        mPullRefreshListView.setAdapter(adapter);
        mPullRefreshListView.setMode(Mode.BOTH);
        initIndicator();
        user = DBHelper.getUser(this);
        getWaterBandList(page);
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                page = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getWaterBandList(page);
                adapter.notifyDataSetChanged(); 
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if(myWaterBandList!=null && myWaterBandList.size()>=10){
                    page = page+1;
                    getWaterBandList(page);
                    adapter.notifyDataSetChanged(); 
                }else {
                    Toast.makeText(MainPlusWaterBandActivity.this,"请稍后，没有更多加载数据",Toast.LENGTH_SHORT).show();
                    mPullRefreshListView.onRefreshComplete(); 
                }
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        page = 1;
        totalWaterBandList = null;
        myWaterBandList =  null;
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
     * 订单列表接口
     */
     public void getWaterBandList(final int page){
         //判断是否有网络
         if (!NetworkUtils.isNetworkConnected(MainPlusWaterBandActivity.this)) {
             Toast.makeText(MainPlusWaterBandActivity.this, getString(R.string.net_not_open), 0).show();
             return;
         }
         Map<String,String> map = new HashMap<String,String>();
         map.put("user_id",user.getId());
         map.put("page",""+page);
         map.put("service_type_id",serviceTypeId);
         
         AjaxParams params = new AjaxParams(map);
         showDialog();
         new FinalHttp().get(Constants.URL_GET_SERVICE_PRICE_LIST, params, new AjaxCallBack<Object>() {
             @Override
             public void onFailure(Throwable t, int errorNo, String strMsg) {
                 super.onFailure(t, errorNo, strMsg);
                 dismissDialog();
                 Toast.makeText(MainPlusWaterBandActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                 myWaterBandList = gson.fromJson(data, new TypeToken<ArrayList<WaterBand>>() {
                                 }.getType());
                               showData(myWaterBandList);
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
                     UIUtils.showToast(MainPlusWaterBandActivity.this, errorMsg);
                 }
             }
         });
     }
     /**
      * 处理数据加载的方法
      * @param list
      */
     private void showData(List<WaterBand> myWaterBandList){
         if(myWaterBandList!=null && myWaterBandList.size()>0){
             if(page==1){
                 totalWaterBandList.clear();
             }
             for (WaterBand waterBand : myWaterBandList) {
                 totalWaterBandList.add(waterBand);
             }
             //给适配器赋值
             adapter.setData(totalWaterBandList);
         }
         mPullRefreshListView.onRefreshComplete();
     }
     /**
      * 订单列表点击进入详情
      */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(StringUtils.isEquals(flag,"99")){
                WaterBand waterBand = totalWaterBandList.get(position);
                Intent intent = new Intent();
                intent.putExtra("waterBandName",waterBand.getName());
                intent.putExtra("waterMoney","原价"+waterBand.getPrice()+"元/桶"+",折扣价"+waterBand.getDis_price()+"元/桶");
                intent.putExtra("waterBandId",waterBand.getServce_price_id());
                setResult(RESULT_FIRST_USER, intent);
                MainPlusWaterBandActivity.this.finish();
            }
    }
}
