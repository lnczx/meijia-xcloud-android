package com.meijialife.simi.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.MyWalletData;

/**
 * 我的钱包适配器
 *
 */
public class MyWalletAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<MyWalletData> list;

	public MyWalletAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		list = new ArrayList<MyWalletData>();
	}

	public void setData(ArrayList<MyWalletData> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
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
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			convertView = inflater.inflate(R.layout.my_wallet_list_item, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.item_tv_name);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		holder.tv_name.setText(list.get(position).getType());
		
		return convertView;
	}
	
	class Holder {
		TextView tv_name;
	}

}
