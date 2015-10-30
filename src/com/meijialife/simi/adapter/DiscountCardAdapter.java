package com.meijialife.simi.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.DiscountCardData;

/**
 * 优惠卡券适配器
 * @author baojiarui
 *
 */
public class DiscountCardAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<DiscountCardData> list;
	private Context context;

	public DiscountCardAdapter(Context context) {
	    this.context = context;
		inflater = LayoutInflater.from(context);
		list = new ArrayList<DiscountCardData>();
	}

	public void setData(ArrayList<DiscountCardData> list) {
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
			convertView = inflater.inflate(R.layout.discount_card_list_item, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.item_tv_name);
			holder.rl_bg = (RelativeLayout) convertView.findViewById(R.id.rl_bg);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		holder.tv_name.setText(list.get(position).getType());
		
		if(position%2 == 0){
		    holder.rl_bg.setBackgroundResource(R.drawable.youhuiquan_bg_4);
		}else{
		    holder.rl_bg.setBackgroundResource(R.drawable.youhuiquan_bg_3);
		}
		
		return convertView;
	}
	
	class Holder {
		TextView tv_name;
		RelativeLayout rl_bg;
	}

}
