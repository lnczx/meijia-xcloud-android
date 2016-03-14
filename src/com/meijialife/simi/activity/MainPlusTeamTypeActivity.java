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
import com.meijialife.simi.adapter.TeamTypeAdapter;
import com.meijialife.simi.bean.TeamTypeData;
import com.meijialife.simi.bean.User;


/**
 * @description：团队建设类型列表
 * @author： kerryg
 * @date:2015年11月14日 
 */
public class MainPlusTeamTypeActivity extends BaseActivity implements OnItemClickListener{

    //定义全局变量
    private TeamTypeAdapter adapter;
    private User user;
    private ArrayList<TeamTypeData> totalTeamTypeList;
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
    	totalTeamTypeList = new ArrayList<TeamTypeData>();
    	setData();
    	listView = (ListView)findViewById(R.id.pull_refresh_list);
    	listView.setOnItemClickListener(this);
        adapter = new TeamTypeAdapter(this);
        listView.setAdapter(adapter);
        adapter.setData(totalTeamTypeList);
    }
    
    private void setData(){
        TeamTypeData teamTypeData1 = new TeamTypeData("0","不限");
        TeamTypeData teamTypeData2 = new TeamTypeData("1","年会");
        TeamTypeData teamTypeData3 = new TeamTypeData("2","拓展培训");
        TeamTypeData teamTypeData4 = new TeamTypeData("3","聚会沙龙");
        TeamTypeData teamTypeData5 = new TeamTypeData("4","休闲度假");
        TeamTypeData teamTypeData6 = new TeamTypeData("5","其他");
        totalTeamTypeList.add(teamTypeData1);
        totalTeamTypeList.add(teamTypeData2);
        totalTeamTypeList.add(teamTypeData3);
        totalTeamTypeList.add(teamTypeData4);
        totalTeamTypeList.add(teamTypeData5);
        totalTeamTypeList.add(teamTypeData6);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        totalTeamTypeList = null;
    }
     /**
      * 订单列表点击进入详情
      */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TeamTypeData teamType = totalTeamTypeList.get(position);
                Intent intent = new Intent();
                intent.putExtra("teamTypeName",teamType.getTeam_type_name());
                intent.putExtra("teamTypeId",teamType.getTeam_type_id());
                setResult(RESULT_FIRST_USER, intent);
                MainPlusTeamTypeActivity.this.finish();
    }
}
