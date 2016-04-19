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
import com.meijialife.simi.database.bean.City;

/**
 * 城市列表适配器
 *
 */
public final class CityListAdapter extends BaseAdapter {

	private Context context;
	private List<City> datas;

	private LayoutInflater layoutInflater;

	/**
	 * @param context上下文
	 * @param 数据列表
	 * @param showDel
	 *            是否显示删除按钮
	 */
	public CityListAdapter(Context context) {
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		this.datas = new ArrayList<City>();
	}
	
	public void setData(List<City> citys) {
		this.datas = citys;
		notifyDataSetChanged();
	}

	public int getCount() {
		return datas.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.city_list_item, null);//

			holder = new ViewHolder();

			holder.tv_addr = (TextView) convertView.findViewById(R.id.city_item_tv_addr);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

	 
		holder.tv_addr.setText(datas.get(position).getName());
		return convertView;
	}

	private static class ViewHolder {
		TextView tv_addr; // 地址
	}

	 

}