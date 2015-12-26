package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.MyOrderAdapter;
import com.meijialife.simi.bean.MyOrder;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;


/**
 * @description：侧边栏--我的订单--列表
 * @author： kerryg
 * @date:2015年11月14日 
 */
public class MyOrderActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

    //定义全局变量
    private MyOrderAdapter adapter;
    private User user;
    private ArrayList<MyOrder> myOrderList;
    
    //布局控件定义
    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.my_order_activity);
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
    	setTitleName("订 单");
    	requestBackBtn();
    	
    	//布局控件赋值+点击事件
    	listview = (ListView)findViewById(R.id.order_list_view);
    	listview.setOnItemClickListener(this);
    	adapter = new MyOrderAdapter(this);
    	listview.setAdapter(adapter);
    	 
    	user = DBHelper.getUser(this);
    	//访问订单列表接口
    	getOrderList();
    }
    /**
     * 订单列表接口
     */
     public void getOrderList(){
         //判断是否有网络
         if (!NetworkUtils.isNetworkConnected(MyOrderActivity.this)) {
             Toast.makeText(MyOrderActivity.this, getString(R.string.net_not_open), 0).show();
             return;
         }
         Map<String,String> map = new HashMap<String,String>();
         map.put("user_id",user.getId());
         map.put("page","0");
         AjaxParams params = new AjaxParams(map);
         showDialog();
         new FinalHttp().get(Constants.URL_GET_ORDER_GET_LIST, params, new AjaxCallBack<Object>() {
             @Override
             public void onFailure(Throwable t, int errorNo, String strMsg) {
                 super.onFailure(t, errorNo, strMsg);
                 dismissDialog();
                 Toast.makeText(MyOrderActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                 myOrderList = gson.fromJson(data, new TypeToken<ArrayList<MyOrder>>() {
                                 }.getType());
                                 //给适配器赋值
                                 adapter.setData(myOrderList);
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
                     UIUtils.showToast(MyOrderActivity.this, errorMsg);
                 }
             }
         });
     }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//        case R.id.:	//
//        	Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
//            break;
         
        default:
            break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        getOrderList();
    }
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
	    MyOrder myOrder = myOrderList.get(position);
	    Intent intent = new Intent(this,OrderDetailsActivity.class);
	    intent.putExtra("myOrder", myOrder);
	    intent.putExtra("orderId",myOrder.getOrder_id()+"");
	    intent.putExtra("orderStatusId",myOrder.getOrder_status());
		startActivity(intent);
	}
}
