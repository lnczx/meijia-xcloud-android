package com.meijialife.simi.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.CardComment;

/**
 * 卡片评论适配器
 *
 */
public class CardCommentAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<CardComment> list;

	public CardCommentAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		list = new ArrayList<CardComment>();
	}

	public void setData(ArrayList<CardComment> list) {
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
			convertView = inflater.inflate(R.layout.card_comment_list_item, null);
			holder.tv_date = (TextView) convertView.findViewById(R.id.item_tv_date);
			holder.tv_comment = (TextView) convertView.findViewById(R.id.item_tv_text);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		long timeL = list.get(position).getAdd_time();
		holder.tv_date.setText(new SimpleDateFormat("HH:mm").format(timeL*1000));
		holder.tv_comment.setText(list.get(position).getComment());
		
		return convertView;
	}
	
	class Holder {
		TextView tv_date;
		TextView tv_comment;
	}

}
