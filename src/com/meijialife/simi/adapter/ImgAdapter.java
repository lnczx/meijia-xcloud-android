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
import com.meijialife.simi.bean.FindBean;

public class ImgAdapter extends BaseAdapter {
	
	private Context _context;
	private ArrayList<FindBean> findBeanList; 
//	private List<Integer> imgList; 
	private LayoutInflater inflater;
	private FinalBitmap finalBitmap;
	private BitmapDrawable defDrawable;
	public ImgAdapter(Context context ) {
		_context = context;
		this.findBeanList=new ArrayList<FindBean>();
		inflater = LayoutInflater.from(context);
	    finalBitmap = FinalBitmap.create(context);
	    //获取默认头像
	    defDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ad_loading);
	}
	
	public void setData(ArrayList<FindBean> findBeanList) {
        this.findBeanList = findBeanList;
        notifyDataSetChanged();
    }
	public int getCount() {
		return findBeanList.size();
	}

	public Object getItem(int position) {

		return position;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
	    Holder holder = null;
		if (convertView == null) {
		    holder = new Holder();
			/*ImageView imageView = new ImageView(_context);
			imageView.setAdjustViewBounds(true);
			imageView.setScaleType(ScaleType.FIT_XY);
			imageView.setLayoutParams(new Gallery.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			convertView = imageView;
			viewHolder.imageView = (ImageView) convertView;*/
			convertView = inflater.inflate(R.layout.find_list_item, null);
            holder.tv_ad_des = (TextView) convertView.findViewById(R.id.tv_ad_des);
            holder.tv_ad_goto_type = (TextView) convertView.findViewById(R.id.tv_goto_type);
            holder.tv_ad_goto_url = (TextView) convertView.findViewById(R.id.tv_goto_url);
            holder.iv_ad_icon = (ImageView) convertView.findViewById(R.id.iv_ad_icon);
            holder.tv_ad_share = (TextView)convertView.findViewById(R.id.tv_share);
            holder.tv_service_type_ids = (TextView)convertView.findViewById(R.id.tv_service_type_ids);
			convertView.setTag(holder);
		} else {
		    holder = (Holder) convertView.getTag();
		}
		    holder.tv_ad_des.setText(findBeanList.get(position).getTitle());
	        holder.tv_ad_goto_type.setText(findBeanList.get(position).getGoto_type());
	        holder.tv_ad_goto_url.setText(findBeanList.get(position).getGoto_url());
	        holder.tv_service_type_ids.setText(findBeanList.get(position).getService_type_ids());
	        String url = findBeanList.get(position % findBeanList.size()).getImg_url();
	        finalBitmap.display(holder.iv_ad_icon, url, defDrawable.getBitmap(), defDrawable.getBitmap());
		return convertView;
	}

	private static class Holder {
	        TextView tv_ad_des;//广告说明
	        ImageView iv_ad_icon;//广告图片
	        TextView  tv_ad_goto_type;//跳转方式
	        TextView  tv_ad_goto_url;//跳转url
	        TextView tv_ad_share;//分享
	        TextView tv_service_type_ids;//服务大类集合
	}
}
