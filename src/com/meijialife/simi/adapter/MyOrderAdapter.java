package com.meijialife.simi.adapter;

import java.util.ArrayList;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.MyOrder;
import com.meijialife.simi.bean.MyOrderData;

/**
 * 我的订单适配器
 *
 */
public class MyOrderAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	//private ArrayList<MyOrderData> list;
    private ArrayList<MyOrder> orderList;
    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;


	public MyOrderAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		orderList = new ArrayList<MyOrder>();
		finalBitmap = FinalBitmap.create(context);
        defDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.order_icon);
	}

	public void setData(ArrayList<MyOrder> list) {
		this.orderList = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return orderList.size();
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
			convertView = inflater.inflate(R.layout.my_order_list_item, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.item_tv_name);
			holder.tv_date = (TextView)convertView.findViewById(R.id.item_tv_date);
			holder.tv_status = (TextView)convertView.findViewById(R.id.item_tv_status);
			holder.iv_head_img = (ImageView)convertView.findViewById(R.id.item_iv_icon);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		holder.tv_name.setText(orderList.get(position).getPartner_user_name());
	    holder.tv_date.setText(orderList.get(position).getAdd_time_str());
	    holder.tv_status.setText(orderList.get(position).getOrder_status_name());
        finalBitmap.display(holder.iv_head_img, orderList.get(position).getPartner_user_head_img(), defDrawable.getBitmap(), defDrawable.getBitmap());


		return convertView;
	}
	
	class Holder {
		TextView tv_name;
		TextView tv_date;
		TextView tv_status;
		ImageView iv_head_img;
	}

}
