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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.meijialife.simi.R;
import com.meijialife.simi.bean.CustomFields;
import com.meijialife.simi.bean.HomePosts;
import com.meijialife.simi.bean.HomeTag;


/**
 * @description：用户消息列表适配器
 * @author： kerryg
 * @date:2016年1月27日 
 */
public final class HomeListAdapter extends BaseAdapter {

	private Context context;
	private HomeTag homeTag;
	private List<HomePosts> homePosts;
	private LayoutInflater layoutInflater;
	
	  private FinalBitmap finalBitmap;
	    private BitmapDrawable defDrawable;
	/**
	 * @param context上下文
	 * @param 数据列表
	 * @param showDel
	 *            是否显示删除按钮
	 */
	public HomeListAdapter(Context context) {
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		this.homePosts = new ArrayList<HomePosts>();
		this.homeTag = new HomeTag();
		  finalBitmap = FinalBitmap.create(context);
	        defDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.ad_loading);
	}
	
	public void setData(List<HomePosts> homePosts,HomeTag homeTags) {
	    this.homePosts = homePosts;
	    this.homeTag = homeTags;
		notifyDataSetChanged();
	}

	public int getCount() {
		return homePosts.size();
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
			convertView = layoutInflater.inflate(R.layout.home_list_item, null);//
			holder = new ViewHolder();
			holder.tv_title = (TextView) convertView.findViewById(R.id.msg_tv_title);
			holder.tv_summary = (TextView) convertView.findViewById(R.id.msg_tv_summary);
			holder.iv_iconUrl = (ImageView) convertView.findViewById(R.id.msg_iv_icon_url);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
	
		HomePosts  homePost = homePosts.get(position);
		holder.tv_title.setText(homePost.getTitle());
		Gson gson = new Gson();
		CustomFields customFields = homePost.getCustom_fields();
		if(customFields!=null && customFields.getViews()!=null && customFields.getViews().size()>0){
		    holder.tv_summary.setText(customFields.getViews().get(0)+"人已看过");
		}else {
            holder.tv_summary.setText("0人已看过");
        }
        finalBitmap.display(holder.iv_iconUrl,homePost.getThumbnail(),defDrawable.getBitmap(),defDrawable.getBitmap());
		return convertView;
	}

	private static class ViewHolder {
		TextView tv_title; //标题
		TextView tv_summary; //摘要
		ImageView iv_iconUrl; //图片
		
	}

	 

}