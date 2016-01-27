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
import com.meijialife.simi.bean.CityData;
import com.meijialife.simi.bean.UserMsg;


/**
 * @description：用户消息列表适配器
 * @author： kerryg
 * @date:2016年1月27日 
 */
public final class UserMsgListAdapter extends BaseAdapter {

	private Context context;
	private List<UserMsg> datas;
	private LayoutInflater layoutInflater;
	
	  private FinalBitmap finalBitmap;
	    private BitmapDrawable defDrawable;
	/**
	 * @param context上下文
	 * @param 数据列表
	 * @param showDel
	 *            是否显示删除按钮
	 */
	public UserMsgListAdapter(Context context) {
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		this.datas = new ArrayList<UserMsg>();
		  finalBitmap = FinalBitmap.create(context);
	        defDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.ad_loading);
	}
	
	public void setData(List<UserMsg> userMsgs) {
		this.datas = userMsgs;
		notifyDataSetChanged();
	}

	public int getCount() {
		return datas.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.user_msg_list_item, null);//

			holder = new ViewHolder();
			holder.tv_title = (TextView) convertView.findViewById(R.id.msg_tv_title);
			holder.tv_summary = (TextView) convertView.findViewById(R.id.msg_tv_summary);
			holder.tv_msgTime = (TextView) convertView.findViewById(R.id.msg_tv_msg_time);
			holder.iv_iconUrl = (ImageView) convertView.findViewById(R.id.msg_iv_icon_url);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		UserMsg  userMsg = datas.get(position);
		holder.tv_title.setText(userMsg.getTitle());
		holder.tv_msgTime.setText(userMsg.getMsg_time());
		holder.tv_summary.setText(userMsg.getSummary());
        finalBitmap.display(holder.iv_iconUrl,userMsg.getIcon_url(),defDrawable.getBitmap(),defDrawable.getBitmap());
		return convertView;
	}

	private static class ViewHolder {
		TextView tv_title; //标题
		TextView tv_summary; //摘要
		ImageView iv_iconUrl; //图片
		TextView tv_msgTime; //时间
		
	}

	 

}