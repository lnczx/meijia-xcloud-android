package com.meijialife.simi.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.WasterTypeAdapter;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.bean.WasterTypeData;


/**
 * @description：废品类型列表
 * @author： kerryg
 * @date:2015年11月14日 
 */
public class MainPlusWasterTypeActivity extends BaseActivity implements OnItemClickListener{

    //定义全局变量
    private WasterTypeAdapter adapter;
    private User user;
    private ArrayList<WasterTypeData> totalWasterTypeList;
    //布局控件定义
    private ListView listView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.order_listview_activity);
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
    	setTitleName("选择类型");
    	requestBackBtn();
    	/**
    	 * 列表赋值
    	 */
    	totalWasterTypeList = new ArrayList<WasterTypeData>();
    	setData();
    	listView = (ListView)findViewById(R.id.pull_refresh_list);
    	listView.setOnItemClickListener(this);
        adapter = new WasterTypeAdapter(this);
        listView.setAdapter(adapter);
        adapter.setData(totalWasterTypeList);
    }
    
    private void setData(){
        WasterTypeData wasterTypeData1 = new WasterTypeData("0","日常办公垃圾");
        WasterTypeData wasterTypeData2 = new WasterTypeData("1","废旧电器");
        WasterTypeData wasterTypeData3 = new WasterTypeData("2","硒鼓墨盒");
        WasterTypeData wasterTypeData4 = new WasterTypeData("3","其他");
        totalWasterTypeList.add(wasterTypeData1);
        totalWasterTypeList.add(wasterTypeData2);
        totalWasterTypeList.add(wasterTypeData3);
        totalWasterTypeList.add(wasterTypeData4);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        totalWasterTypeList = null;
    }
     /**
      * 订单列表点击进入详情
      */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WasterTypeData wasterType = totalWasterTypeList.get(position);
                Intent intent = new Intent();
                intent.putExtra("wasterTypeName",wasterType.getRecycle_type_name());
                intent.putExtra("wasterTypeId",wasterType.getRecycle_type_id());
                setResult(RESULT_FIRST_USER, intent);
                MainPlusWasterTypeActivity.this.finish();
    }
}
