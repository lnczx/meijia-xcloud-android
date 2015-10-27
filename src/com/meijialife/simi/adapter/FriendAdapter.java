package com.meijialife.simi.adapter;

import java.util.ArrayList;

import net.tsz.afinal.FinalBitmap;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.Friend;
import com.meijialife.simi.ui.RoundImageView;

/**
 * 好友适配器
 *
 */
public class FriendAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<Friend> list;
	
	private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;

	public FriendAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		list = new ArrayList<Friend>();
		
		finalBitmap = FinalBitmap.create(context);
        defDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_defult_touxiang);
	}

	public void setData(ArrayList<Friend> list) {
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
			convertView = inflater.inflate(R.layout.friend_list_item, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.item_tv_name);
			holder.iv_header = (RoundImageView) convertView.findViewById(R.id.item_iv_icon);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		holder.tv_name.setText(list.get(position).getName());
		
		String url = list.get(position).getHead_img();
//      url = "http://img5.duitang.com/uploads/item/201504/21/20150421H4340_uv24P.thumb.224_0.jpeg";//test
        finalBitmap.display(holder.iv_header, url, defDrawable.getBitmap(), defDrawable.getBitmap());
		
		return convertView;
	}
	
	class Holder {
	    RoundImageView iv_header;
		TextView tv_name;
	}

}
