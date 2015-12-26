package com.meijialife.simi.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.AppToolsData;
import com.meijialife.simi.bean.Partner;
import com.meijialife.simi.bean.UserTag;
import com.meijialife.simi.ui.RoundImageView;
import com.meijialife.simi.ui.TagGroup;

/**
 * 秘书适配器
 *
 */
public class AppToolsAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	
	private ArrayList<AppToolsData> appToolsDatas;
	
	private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    private Context contexts;

	public AppToolsAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		appToolsDatas = new ArrayList<AppToolsData>();
		contexts = context;
		finalBitmap = FinalBitmap.create(context);
		//获取默认头像
        defDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ad_loading);
    
	}

	public void setData(ArrayList<AppToolsData> appToolsDatas) {
		this.appToolsDatas = appToolsDatas;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return appToolsDatas.size();
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
			convertView = inflater.inflate(R.layout.application_center_item, null);
			holder.iv_application_icon = (ImageView) convertView.findViewById(R.id.iv_application_icon);
			holder.tv_application_name = (TextView) convertView.findViewById(R.id.tv_application_name);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.tv_application_name.setText(appToolsDatas.get(position).getName());
		//获得头像的url
        String url = appToolsDatas.get(position).getLogo();
        //将默认头像摄者为秘书头像
        finalBitmap.display(holder.iv_application_icon, url, defDrawable.getBitmap(), defDrawable.getBitmap());
		return convertView;
	}
	class Holder {
		ImageView iv_application_icon;
		TextView  tv_application_name;
	}
}
