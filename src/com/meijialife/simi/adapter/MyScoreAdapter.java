package com.meijialife.simi.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.MyScore;
import com.meijialife.simi.utils.DateUtils;

/**
 * 我的积分适配器
 *
 */
public class MyScoreAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<MyScore> dataList;
	
	private Context context;

	public MyScoreAdapter(Context context) {
	    this.context = context;
		inflater = LayoutInflater.from(context);
		dataList = new ArrayList<MyScore>();
	}

	public void setData(ArrayList<MyScore> list) {
		this.dataList = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return dataList.size();
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
			convertView = inflater.inflate(R.layout.my_score_list_item, null);
			holder.item_tv_order_type = (TextView) convertView.findViewById(R.id.item_tv_order_type);
			holder.item_tv_order_pay = (TextView) convertView.findViewById(R.id.item_tv_order_pay);
			holder.item_tv_date = (TextView) convertView.findViewById(R.id.item_tv_date);
			holder.item_tv_mobile = (TextView) convertView.findViewById(R.id.item_tv_mobile);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		holder.item_tv_order_type.setText(dataList.get(position).getAction());
		String score = dataList.get(position).getScore();
		if(0==dataList.get(position).getIs_consume()){
		    holder.item_tv_order_pay.setText("+"+score);
		    holder.item_tv_order_pay.setTextColor(context.getResources().getColor(R.color.score_text_use));
		}else if(1==dataList.get(position).getIs_consume()){
		    holder.item_tv_order_pay.setText("-"+score);
	        holder.item_tv_order_pay.setTextColor(context.getResources().getColor(R.color.score_button));
		}
        String mDate = DateUtils.getStringByPattern(dataList.get(position).getAdd_time()*1000, "yyyy-MM-dd");
		holder.item_tv_date.setText(mDate);
		return convertView;
	}
	
	class Holder {
		TextView item_tv_order_type;
		TextView item_tv_order_pay;
		TextView item_tv_date;
		TextView item_tv_mobile;
	}

}
