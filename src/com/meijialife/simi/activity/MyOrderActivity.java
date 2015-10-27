package com.meijialife.simi.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.MyOrderAdapter;
import com.meijialife.simi.bean.MyOrderData;

/**
 * 我的订单
 *
 */
public class MyOrderActivity extends BaseActivity implements OnClickListener, OnItemClickListener {
	
	private ListView listview;
    private MyOrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.my_order_activity);
        super.onCreate(savedInstanceState);
        
        initView();
        test();

    }

    private void initView() {
    	setTitleName("订 单");
    	requestBackBtn();
    	
    	listview = (ListView)findViewById(R.id.listview);
    	listview.setOnItemClickListener(this);
    	adapter = new MyOrderAdapter(this);
    	listview.setAdapter(adapter);
    }
    
    /**
     * 测试数据
     */
    private void test(){
    	ArrayList<MyOrderData> list = new ArrayList<MyOrderData>();
    	MyOrderData data1 = new MyOrderData("机票");
    	MyOrderData data2 = new MyOrderData("通用");
    	MyOrderData data3 = new MyOrderData("酒店");
    	MyOrderData data4 = new MyOrderData("机票");
    	MyOrderData data5 = new MyOrderData("机票");
    	list.add(data1);
    	list.add(data2);
    	list.add(data3);
    	list.add(data4);
    	list.add(data5);
    	
    	adapter.setData(list);
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		startActivity(new Intent(this, OrderDetailsActivity.class));
	}
}
