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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.Partner;
import com.meijialife.simi.bean.UserTag;
import com.meijialife.simi.ui.RoundImageView;
import com.meijialife.simi.ui.TagGroup;

/**
 * 秘书适配器
 *
 */
public class SecretaryAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	
	private ArrayList<Partner> partnerList;
	
	private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    private Context contexts;

	public SecretaryAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		partnerList = new ArrayList<Partner>();
		contexts = context;
		finalBitmap = FinalBitmap.create(context);
		//获取默认头像
        defDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_defult_touxiang);
    
	}

	public void setData(ArrayList<Partner> partnerList) {
		this.partnerList = partnerList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return partnerList.size();
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
			convertView = inflater.inflate(R.layout.secretary_list_item, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.item_tv_name);
			holder.tv_text = (TextView) convertView.findViewById(R.id.item_tv_text);
			holder.iv_head = (RoundImageView)convertView.findViewById(R.id.item_iv_icon);
			holder.item_tv_fav = (TextView)convertView.findViewById(R.id.item_tv_fav);
			holder.item_tv_addr_name = (TextView)convertView.findViewById(R.id.item_tv_addr_name);
			holder.item_tv_des_name = (TextView)convertView.findViewById(R.id.item_tv_des_name);
			holder.tg =(TagGroup)convertView.findViewById(R.id.ll_user_tags);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		holder.tv_name.setText(partnerList.get(position).getName());
		holder.tv_text.setText(partnerList.get(position).getIntroduction());
		holder.item_tv_fav.setText(partnerList.get(position).getService_type_name());
		holder.item_tv_addr_name.setText(partnerList.get(position).getCity_and_region());
		holder.item_tv_des_name.setText(partnerList.get(position).getResponse_time_name());
		//获得头像的url
        String url = partnerList.get(position).getHead_img();
        //将默认头像摄者为秘书头像
        finalBitmap.display(holder.iv_head, url, defDrawable.getBitmap(), defDrawable.getBitmap());
        List<UserTag> userTagList = partnerList.get(position).getUser_tags();
        List<String> userTags =new ArrayList<String>();
        for (Iterator iterator = userTagList.iterator(); iterator.hasNext();) {
            UserTag userTag = (UserTag) iterator.next();
            userTags.add(userTag.getTag_name());
        }
        holder.tg.setTags(userTags);
		return convertView;
	}
	
	class Holder {
		TextView tv_name;//昵称
		TextView tv_text;//简介
		RoundImageView iv_head;//头像
		TextView item_tv_fav;//服务范围大类
		TextView item_tv_addr_name;//所在城市
		TextView item_tv_des_name;//服务响应时间
		LinearLayout ll;
		TagGroup tg;//用户标签
		
	}

}
