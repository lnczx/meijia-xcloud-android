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
import com.meijialife.simi.activity.MainPlusWasterActivity;
import com.meijialife.simi.bean.WasterData;
import com.meijialife.simi.inter.ListItemClickHelp;

/**
 * 加号--废品回收---适配器
 *
 */
public class MainPlusWasterAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<WasterData> wasterDatas;
	
	private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    private Context contexts;
    private ListItemClickHelp callback;//回调方法
    

	public MainPlusWasterAdapter(Context context,MainPlusWasterActivity activity) {
		inflater = LayoutInflater.from(context);
		wasterDatas = new ArrayList<WasterData>();
		contexts = context;
		finalBitmap = FinalBitmap.create(context);
		//获取默认头像
        defDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ad_loading);
        this.callback = activity;  
	}

	public void setData(ArrayList<WasterData> waterDataList) {
		this.wasterDatas = waterDataList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return wasterDatas.size();
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
		final WasterData wasterData = wasterDatas.get(position);
        finalBitmap.display(holder.m_waster_icon, Constants.WASTER_ICON_URL, defDrawable.getBitmap(), defDrawable.getBitmap());

        holder.m_waster_title.setText(wasterData.getRecycle_type_name());
        holder.m_waster_time.setText("下单时间:"+wasterData.getAdd_time_str());
        holder.m_waster_status.setText(wasterData.getOrder_status_name());
     
        
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
