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
import com.meijialife.simi.adapter.CleanTypeAdapter;
import com.meijialife.simi.bean.CleanTypeData;
import com.meijialife.simi.bean.User;


/**
 * @description：保洁类型列表
 * @author： kerryg
 * @date:2015年11月14日 
 */
public class MainPlusCleanTypeActivity extends BaseActivity implements OnItemClickListener{

    //定义全局变量
    private CleanTypeAdapter adapter;
    private User user;
    private ArrayList<CleanTypeData> totalCleanTypeList;
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
    	totalCleanTypeList = new ArrayList<CleanTypeData>();
    	setData();
    	listView = (ListView)findViewById(R.id.pull_refresh_list);
    	listView.setOnItemClickListener(this);
        adapter = new CleanTypeAdapter(this);
        listView.setAdapter(adapter);
        adapter.setData(totalCleanTypeList);
    }
    
    private void setData(){
        //0 = 定期保洁 1= 深度养护 2 = 维修清洗 3 = 其他
        CleanTypeData cleanTypeData1 = new CleanTypeData("0","定期保洁");
        CleanTypeData cleanTypeData2 = new CleanTypeData("1","深度养护");
        CleanTypeData cleanTypeData3 = new CleanTypeData("2","维修清洗");
        CleanTypeData cleanTypeData4 = new CleanTypeData("3","其他");
        totalCleanTypeList.add(cleanTypeData1);
        totalCleanTypeList.add(cleanTypeData2);
        totalCleanTypeList.add(cleanTypeData3);
        totalCleanTypeList.add(cleanTypeData4);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        totalCleanTypeList = null;
    }
     /**
      * 订单列表点击进入详情
      */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CleanTypeData cleanType = totalCleanTypeList.get(position);
                Intent intent = new Intent();
                intent.putExtra("cleanTypeName",cleanType.getClean_type_name());
                intent.putExtra("cleanTypeId",cleanType.getClean_type_id());
                setResult(RESULT_FIRST_USER, intent);
                MainPlusCleanTypeActivity.this.finish();
    }
}
