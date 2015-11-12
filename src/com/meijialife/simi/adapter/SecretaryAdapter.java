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
import com.meijialife.simi.bean.Secretary;
import com.meijialife.simi.ui.RoundImageView;

/**
 * 秘书适配器
 *
 */
public class SecretaryAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<Secretary> list;
	
	private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;

	public SecretaryAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		list = new ArrayList<Secretary>();
		
		finalBitmap = FinalBitmap.create(context);
		//获取默认头像
        defDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_defult_touxiang);
    
	}

	public void setData(ArrayList<Secretary> list) {
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
			convertView = inflater.inflate(R.layout.secretary_list_item, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.item_tv_name);
			holder.tv_text = (TextView) convertView.findViewById(R.id.item_tv_text);
			holder.iv_head = (RoundImageView)convertView.findViewById(R.id.item_iv_icon);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		holder.tv_name.setText(list.get(position).getName());
		holder.tv_text.setText(list.get(position).getDescription());
		//获得头像的url
        String url = list.get(position).getHead_img();
        //将默认头像摄者为秘书头像
        finalBitmap.display(holder.iv_head, url, defDrawable.getBitmap(), defDrawable.getBitmap());
        
		return convertView;
	}
	
	class Holder {
		TextView tv_name;
		TextView tv_text;
		RoundImageView iv_head;
	}

}
