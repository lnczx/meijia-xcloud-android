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
import com.meijialife.simi.bean.DefaultServiceData;

/**
 * @description:默认商品适配器
 * @author： kerryg
 * @date:2015年12月9日 
 */
public class DefaultServiceAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	
	private ArrayList<DefaultServiceData> lists;
	
	private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    private Context contexts;

	public DefaultServiceAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		lists = new ArrayList<DefaultServiceData>();
		contexts = context;
		finalBitmap = FinalBitmap.create(context);
		//获取默认头像
        defDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ad_loading);
	}

	public void setData(ArrayList<DefaultServiceData> list) {
		this.lists = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return lists.size();
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
			convertView = inflater.inflate(R.layout.def_service_item, null);
			holder.tv_ad_des = (TextView) convertView.findViewById(R.id.tv_ad_des);
			holder.iv_ad_icon = (ImageView) convertView.findViewById(R.id.iv_ad_icon);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.tv_ad_des.setText(lists.get(position).getName());
		String url = lists.get(position).getImg_url();
        finalBitmap.display(holder.iv_ad_icon, url, defDrawable.getBitmap(), defDrawable.getBitmap());

        return convertView;
	}
	class Holder {
		TextView tv_ad_des;//广告说明
		ImageView iv_ad_icon;//广告图片
	}

}
