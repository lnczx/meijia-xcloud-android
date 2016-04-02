package com.meijialife.simi.adapter;

import java.util.ArrayList;
import java.util.List;

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
import com.meijialife.simi.bean.ExpressTypeData;
/**
 * @description：请假类型适配器
 * @author： kerryg
 * @date:2015年11月14日 
 */
public class ExpressTypeAdapter extends BaseAdapter {
    
    //定义全局变量
	private LayoutInflater inflater;
    private List<ExpressTypeData> typeDatas;
    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    private Context context;

	public ExpressTypeAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		typeDatas = new ArrayList<ExpressTypeData>();
		finalBitmap = FinalBitmap.create(context);
		this.context = context;
		//默认图标赋值
        defDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.ad_loading);
	}

	public void setData(List<ExpressTypeData> typeList) {
		this.typeDatas = typeList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return typeDatas.size();
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
			convertView = inflater.inflate(R.layout.leave_type_list_item, null);
			holder.tv_type_name = (TextView)convertView.findViewById(R.id.m_tv_type_name);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		ExpressTypeData type = typeDatas.get(position);
	    holder.tv_type_name.setText(type.getName());
//	    finalBitmap.display(holder.iv_band_icon, waterBand.getImg_url(), defDrawable.getBitmap(), defDrawable.getBitmap());
        return convertView;
	}
	
	class Holder {
		ImageView iv_type_icon;
		TextView tv_type_money;
		TextView tv_type_name;
	}

}
