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
import com.meijialife.simi.bean.WaterBand;
/**
 * @description：水品牌适配器
 * @author： kerryg
 * @date:2015年11月14日 
 */
public class WaterBandAdapter extends BaseAdapter {
    
    //定义全局变量
	private LayoutInflater inflater;
    private ArrayList<WaterBand> waterBands;
    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    private Context context;

	public WaterBandAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		waterBands = new ArrayList<WaterBand>();
		finalBitmap = FinalBitmap.create(context);
		this.context = context;
		//默认图标赋值
        defDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.ad_loading);
	}

	public void setData(ArrayList<WaterBand> waterBands) {
		this.waterBands = waterBands;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return waterBands.size();
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
			convertView = inflater.inflate(R.layout.water_band_list_item, null);
			holder.tv_band_money = (TextView) convertView.findViewById(R.id.m_tv_band_money);
			holder.tv_band_name = (TextView)convertView.findViewById(R.id.m_tv_band_name);
			holder.iv_band_icon = (ImageView)convertView.findViewById(R.id.m_iv_band_icon);
			
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		WaterBand waterBand = waterBands.get(position);
		holder.tv_band_money.setText("原价"+waterBand.getPrice()+"元/桶"+",折扣价"+waterBand.getDis_price()+"元/桶");
	    holder.tv_band_name.setText(waterBand.getName());
	    finalBitmap.display(holder.iv_band_icon, waterBand.getImg_url(), defDrawable.getBitmap(), defDrawable.getBitmap());
		
        return convertView;
	}
	
	class Holder {
		ImageView iv_band_icon;
		TextView tv_band_money;
		TextView tv_band_name;
	}

}
