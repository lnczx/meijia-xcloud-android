package com.meijialife.simi.adapter;

import java.util.ArrayList;

import net.tsz.afinal.FinalBitmap;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.FindPlusData;

/**
 * 秘书适配器
 *
 */
public class FindPlusAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	
	private ArrayList<FindPlusData> findPlusDatas;
	
	private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    private Context contexts;

	public FindPlusAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		findPlusDatas = new ArrayList<FindPlusData>();
		contexts = context;
		finalBitmap = FinalBitmap.create(context);
		//获取默认头像
        defDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ad_loading);
    
	}

	public void setData(ArrayList<FindPlusData> findPlusDatas) {
		this.findPlusDatas = findPlusDatas;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return findPlusDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			convertView = inflater.inflate(R.layout.find_plus_item, null);
			holder.iv_plus_icon = (ImageView) convertView.findViewById(R.id.iv_plus_icon);
			holder.iv_plus_name = (TextView) convertView.findViewById(R.id.tv_plus_name);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.iv_plus_name.setText(findPlusDatas.get(position).getTitle());
		//获得头像的url
        String url = findPlusDatas.get(position).getIcon_url();
        //将默认头像摄者为秘书头像
        finalBitmap.display(holder.iv_plus_icon, url, defDrawable.getBitmap(), defDrawable.getBitmap());
		return convertView;
	}
	class Holder {
		ImageView iv_plus_icon;
		TextView  iv_plus_name;
	}
}
