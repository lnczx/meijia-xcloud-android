package com.meijialife.simi.adapter;

import java.util.ArrayList;

import net.tsz.afinal.FinalBitmap;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.LeaveData;
import com.meijialife.simi.ui.RoundImageView;

/**
 * 加号--请假列表---适配器
 *
 */
public class MainPlusLeaveAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<LeaveData> leaveDatas;
	
	private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    private Context contexts;
    

	public MainPlusLeaveAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		leaveDatas = new ArrayList<LeaveData>();
		contexts = context;
		finalBitmap = FinalBitmap.create(context);
		//获取默认头像
        defDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ad_loading);
	}

	public void setData(ArrayList<LeaveData> leaveDataList) {
		this.leaveDatas = leaveDataList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return leaveDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			convertView = inflater.inflate(R.layout.main_plus_leave_item, null);
			holder.m_center_icon = (RoundImageView) convertView.findViewById(R.id.m_center_icon);
			holder.m_center_title = (TextView) convertView.findViewById(R.id.m_center_title);
			holder.m_center_describe = (TextView) convertView.findViewById(R.id.m_center_describe);
			holder.m_center_provider = (TextView) convertView.findViewById(R.id.m_center_provider);
			holder.m_center_add = (TextView)convertView.findViewById(R.id.m_center_add);
			holder.m_center_default_add = (TextView)convertView.findViewById(R.id.m_center_default_add);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		LeaveData leaveData = leaveDatas.get(position);
		//将默认头像摄者为秘书头像/
        String url = leaveData.getHead_img();     //获得头像的url
        finalBitmap.display(holder.m_center_icon, url, defDrawable.getBitmap(), defDrawable.getBitmap());
		
        holder.m_center_title.setText(leaveData.getName());
        holder.m_center_describe.setText(leaveData.getStatus_name());
       
        return convertView;
	}
	
	class Holder {
	    RoundImageView m_center_icon;
		TextView  m_center_title;
		TextView  m_center_describe;
		TextView  m_center_provider;
		TextView  m_center_add;
		TextView  m_center_default_add;
	}
}
