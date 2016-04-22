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
    
    	typeList = new ArrayList<XcompanySetting>();
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
