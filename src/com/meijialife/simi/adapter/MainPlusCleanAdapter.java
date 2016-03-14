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

import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.bean.CleanData;
import com.meijialife.simi.inter.ListItemClickHelp;

/**
 * 加号--保洁---适配器
 *
 */
public class MainPlusCleanAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<CleanData> cleanDatas;
	
	private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    private Context contexts;
    private ListItemClickHelp callback;//回调方法
    

	public MainPlusCleanAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		cleanDatas = new ArrayList<CleanData>();
		contexts = context;
		finalBitmap = FinalBitmap.create(context);
		//获取默认头像
        defDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ad_loading);
	}

	public void setData(ArrayList<CleanData> cleanDataList) {
		this.cleanDatas = cleanDataList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return cleanDatas.size();
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			convertView = inflater.inflate(R.layout.main_plus_waster_item, null);
			holder.m_waster_icon = (ImageView) convertView.findViewById(R.id.m_water_icon);
			holder.m_waster_title = (TextView) convertView.findViewById(R.id.m_water_title);
			holder.m_waster_money = (TextView) convertView.findViewById(R.id.m_water_money);
			holder.m_waster_status = (TextView) convertView.findViewById(R.id.m_water_statue);
			holder.m_waster_time = (TextView) convertView.findViewById(R.id.m_water_time);
			holder.m_waster_add = (TextView)convertView.findViewById(R.id.m_water_add);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		final CleanData cleanData = cleanDatas.get(position);
        finalBitmap.display(holder.m_waster_icon, Constants.CLEAN_ICON_URL, defDrawable.getBitmap(), defDrawable.getBitmap());

        holder.m_waster_title.setText(cleanData.getClean_type_name());
        holder.m_waster_time.setText("下单时间:"+cleanData.getAdd_time_str());
        holder.m_waster_status.setText(cleanData.getOrder_status_name());
     
        
        return convertView;
	}
	class Holder {
		ImageView m_waster_icon;
		TextView  m_waster_title;
		TextView  m_waster_money;
		TextView  m_waster_status;
		TextView  m_waster_time;
		TextView  m_waster_add;
	}
}
