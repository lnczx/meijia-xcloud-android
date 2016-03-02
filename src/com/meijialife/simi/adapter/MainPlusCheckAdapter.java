package com.meijialife.simi.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.CheckListData;
/**
 * @description：员工签到--列表--适配器
 * @author： kerryg
 * @date:2015年11月14日 
 */
public class MainPlusCheckAdapter extends BaseAdapter {
    
    //定义全局变量
	private LayoutInflater inflater;
    private ArrayList<CheckListData> checkDataList;
    private Context context;

	public MainPlusCheckAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		checkDataList = new ArrayList<CheckListData>();
		this.context = context;
		//默认图标赋值
	}

	public void setData(ArrayList<CheckListData> list) {
		this.checkDataList = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return checkDataList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			convertView = inflater.inflate(R.layout.main_plus_checkin_list_item, null);
			holder.m_checkin_time = (TextView) convertView.findViewById(R.id.m_tv_checkin_time);
			holder.m_poi_name = (TextView)convertView.findViewById(R.id.m_tv_poi_name);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		holder.m_checkin_time.setText(checkDataList.get(position).getCheckinTime());
	    holder.m_poi_name.setText(checkDataList.get(position).getPoiName());
	
        return convertView;
	}
	
	class Holder {
		TextView m_checkin_time;
		TextView m_poi_name;
	}

}
