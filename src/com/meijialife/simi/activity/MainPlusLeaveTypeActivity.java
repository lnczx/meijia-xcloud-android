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
import com.meijialife.simi.adapter.LeaveTypeAdapter;
import com.meijialife.simi.bean.LeaveTypeData;
import com.meijialife.simi.bean.User;


/**
 * @description：假期类型列表
 * @author： kerryg
 * @date:2015年11月14日 
 */
public class MainPlusLeaveTypeActivity extends BaseActivity implements OnItemClickListener{

    //定义全局变量
    private LeaveTypeAdapter adapter;
    private User user;
    private ArrayList<LeaveTypeData> totalLeaveTypeList;
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
    	totalLeaveTypeList = new ArrayList<LeaveTypeData>();
    	setData();
    	listView = (ListView)findViewById(R.id.pull_refresh_list);
    	listView.setOnItemClickListener(this);
        adapter = new LeaveTypeAdapter(this);
        listView.setAdapter(adapter);
        adapter.setData(totalLeaveTypeList);
    }
    
    private void setData(){
        //请假类型 0 = 病假 1 = 事假 2 = 婚假 3 = 丧假 4 = 产假 5 = 年休假 6 = 其他
        LeaveTypeData leaveTypeData1 = new LeaveTypeData("0","病假");
        LeaveTypeData leaveTypeData2 = new LeaveTypeData("1","事假");
        LeaveTypeData leaveTypeData3 = new LeaveTypeData("2","婚假");
        LeaveTypeData leaveTypeData4 = new LeaveTypeData("3","丧假");
        LeaveTypeData leaveTypeData5 = new LeaveTypeData("4","产假");
        LeaveTypeData leaveTypeData6 = new LeaveTypeData("5","年休假");
        LeaveTypeData leaveTypeData7 = new LeaveTypeData("6","其他");
        totalLeaveTypeList.add(leaveTypeData1);
        totalLeaveTypeList.add(leaveTypeData2);
        totalLeaveTypeList.add(leaveTypeData3);
        totalLeaveTypeList.add(leaveTypeData4);
        totalLeaveTypeList.add(leaveTypeData5);
        totalLeaveTypeList.add(leaveTypeData6);
        totalLeaveTypeList.add(leaveTypeData7);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        totalLeaveTypeList = null;
    }
     /**
      * 订单列表点击进入详情
      */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LeaveTypeData leaveType = totalLeaveTypeList.get(position);
                Intent intent = new Intent();
                intent.putExtra("leaveTypeName",leaveType.getLeave_type_name());
                intent.putExtra("leaveTypeId",leaveType.getLeave_type_id());
                setResult(RESULT_OK, intent);
                MainPlusLeaveTypeActivity.this.finish();
    }
}
