package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.AssetTypeAdapter;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.bean.XcompanySetting;
import com.meijialife.simi.utils.AssetsDatabaseManager;


/**
 * @description：资产类型列表
 * @author： kerryg
 * @date:2015年11月14日 
 */
public class MainPlusAssetTypeActivity extends BaseActivity implements OnItemClickListener{

    //定义全局变量
    private AssetTypeAdapter adapter;
    private User user;
    private List<XcompanySetting> typeList;
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
    	typeList = new ArrayList<XcompanySetting>();
//    	setData();
    	listView = (ListView)findViewById(R.id.pull_refresh_list);
    	listView.setOnItemClickListener(this);
        adapter = new AssetTypeAdapter(this);
        listView.setAdapter(adapter);
        
        // 初始化，只需要调用一次  
        AssetsDatabaseManager.initManager(getApplication());  
        // 获取管理对象，因为数据库需要通过管理对象才能够获取  
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();  
        // 通过管理对象获取数据库  
        SQLiteDatabase db = mg.getDatabase("simi01.db"); 
        typeList = AssetsDatabaseManager.searchAllXcompany(db);
        adapter.setData(typeList);
       
        
    }
    
    private void setData(){
        //请假类型 0 = 病假 1 = 事假 2 = 婚假 3 = 丧假 4 = 产假 5 = 年休假 6 = 其他
        XcompanySetting typeData1 = new XcompanySetting("7","文具用品");
        XcompanySetting typeData2 = new XcompanySetting("8","办公耗材");
        XcompanySetting typeData3 = new XcompanySetting("9","日杂百货");
        XcompanySetting typeData4 = new XcompanySetting("10","办公设备");
        XcompanySetting typeData5 = new XcompanySetting("11","办公家具");
        XcompanySetting typeData6 = new XcompanySetting("12","财务用品");
        XcompanySetting typeData7 = new XcompanySetting("13","其他");
        typeList.add(typeData1);
        typeList.add(typeData2);
        typeList.add(typeData3);
        typeList.add(typeData4);
        typeList.add(typeData5);
        typeList.add(typeData6);
        typeList.add(typeData7);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        typeList.clear();
        AssetsDatabaseManager.closeDatabase("simi01.db");
    }
     /**
      * 订单列表点击进入详情
      */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                XcompanySetting type = typeList.get(position);
                Intent intent = new Intent();
                intent.putExtra("typeName",type.getName());
                intent.putExtra("typeId",type.getId());
                setResult(RESULT_FIRST_USER, intent);
                MainPlusAssetTypeActivity.this.finish();
    }
}
