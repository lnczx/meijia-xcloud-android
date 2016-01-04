package com.meijialife.simi.adapter;

import java.util.ArrayList;

import net.tsz.afinal.FinalBitmap;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.FindBean;

/**
 * @description：新版发现-->适配器
 * @author： kerryg
 * @date:2015年12月9日 
 */
public class Find2Adapter extends BaseAdapter {
	private LayoutInflater inflater;
	
	private ArrayList<FindBean> findBeanList;
	
	private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    private Context contexts;

	public Find2Adapter(Context context) {
		inflater = LayoutInflater.from(context);
		findBeanList = new ArrayList<FindBean>();
		contexts = context;
		finalBitmap = FinalBitmap.create(context);
		//获取默认头像
        defDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ad_loading);
	}

	public void setData(ArrayList<FindBean> findBeanList) {
		this.findBeanList = findBeanList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return findBeanList.size();
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
			convertView = inflater.inflate(R.layout.find_list_item, null);
			holder.tv_ad_des = (TextView) convertView.findViewById(R.id.tv_ad_des);
			holder.tv_ad_goto_type = (TextView) convertView.findViewById(R.id.tv_goto_type);
			holder.tv_ad_goto_url = (TextView) convertView.findViewById(R.id.tv_goto_url);
			holder.iv_ad_icon = (ImageView) convertView.findViewById(R.id.iv_ad_icon);
/*			holder.tv_ad_share = (TextView)convertView.findViewById(R.id.tv_share);
*/			holder.tv_service_type_ids = (TextView)convertView.findViewById(R.id.tv_service_type_ids);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.tv_ad_des.setText(findBeanList.get(position).getTitle());
		holder.tv_ad_goto_type.setText(findBeanList.get(position).getGoto_type());
		holder.tv_ad_goto_url.setText(findBeanList.get(position).getGoto_url());
		holder.tv_service_type_ids.setText(findBeanList.get(position).getService_type_ids());
		String url = findBeanList.get(position).getImg_url();
        finalBitmap.display(holder.iv_ad_icon, url, defDrawable.getBitmap(), defDrawable.getBitmap());
  /*      holder.tv_ad_share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(contexts,"share",Toast.LENGTH_SHORT).show();
            }
        });
        */
        
        return convertView;
	}
	class Holder {
		TextView tv_ad_des;//广告说明
		ImageView iv_ad_icon;//广告图片
		TextView  tv_ad_goto_type;//跳转方式
		TextView  tv_ad_goto_url;//跳转url
		TextView tv_ad_share;//分享
		TextView tv_service_type_ids;//服务大类集合
		
	}

}
