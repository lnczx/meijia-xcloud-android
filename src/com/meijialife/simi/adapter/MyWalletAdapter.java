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
	private ArrayList<MyWalletData> myWalletDataList;

	public MyWalletAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		myWalletDataList = new ArrayList<MyWalletData>();
	}

	public void setData(ArrayList<MyWalletData> list) {
		this.myWalletDataList = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return myWalletDataList.size();
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
			convertView = inflater.inflate(R.layout.my_wallet_list_item, null);
			holder.item_tv_order_type = (TextView) convertView.findViewById(R.id.item_tv_order_type);
			holder.item_tv_order_pay = (TextView) convertView.findViewById(R.id.item_tv_order_pay);
			holder.item_tv_date = (TextView) convertView.findViewById(R.id.item_tv_date);
			holder.item_tv_mobile = (TextView) convertView.findViewById(R.id.item_tv_mobile);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		holder.item_tv_order_type.setText(myWalletDataList.get(position).getOrder_type_name()+"");
		holder.item_tv_order_pay.setText(myWalletDataList.get(position).getOrder_pay()+"元");
		holder.item_tv_mobile.setText(myWalletDataList.get(position).getMobile());
		holder.item_tv_date.setText(myWalletDataList.get(position).getAdd_time_str());
		return convertView;
	}
	
	class Holder {
		TextView item_tv_order_type;
		TextView item_tv_order_pay;
		TextView item_tv_date;
		TextView item_tv_mobile;
	}

}
