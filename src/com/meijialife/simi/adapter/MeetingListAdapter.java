package com.meijialife.simi.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.MeetingData;

/**
 * 会议室列表适配器
 *
 */
public final class MeetingListAdapter extends BaseAdapter {

	private Context context;
	private List<MeetingData> datas;

	private LayoutInflater layoutInflater;

	/**
	 * @param context上下文
	 * @param 数据列表
	 * @param showDel
	 *            是否显示删除按钮
	 */
	public MeetingListAdapter(Context context) {
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		this.datas = new ArrayList<MeetingData>();
	}
	
	public void setData(List<MeetingData> data) {
		this.datas = data;
		notifyDataSetChanged();
	}

	@Override
    public int getCount() {
		return datas.size();
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
		ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.meeting_list_item, null);//
			holder = new ViewHolder();
			holder.tv_name = (TextView) convertView.findViewById(R.id.m_meeting_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

	 
		holder.tv_name.setText(datas.get(position).getName());
		return convertView;
	}

	private static class ViewHolder {
		TextView tv_name; // 地址
	}

	 

}