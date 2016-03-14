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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.PassUsersData;
import com.meijialife.simi.utils.StringUtils;
/**
 * @description：请假类型适配器
 * @author： kerryg
 * @date:2015年11月14日 
 */
/**
 * @description：
 * @author： kerryg
 * @date:2016年3月10日 
 */
public class PassUsersAdapter extends BaseAdapter {
    
    //定义全局变量
	private LayoutInflater inflater;
    private ArrayList<PassUsersData> passUsersDatas;
    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    private Context context;
    

	public PassUsersAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		passUsersDatas = new ArrayList<PassUsersData>();
		finalBitmap = FinalBitmap.create(context);
		this.context = context;
		//默认图标赋值
        defDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.ad_loading);
	
	}

	public void setData(ArrayList<PassUsersData> passUsersList) {
		this.passUsersDatas = passUsersList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return passUsersDatas.size();
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
			convertView = inflater.inflate(R.layout.leave_pass_user_item, null);
			holder.tv_pass_status = (TextView) convertView.findViewById(R.id.m_pass_status);
			holder.tv_pass_name = (TextView)convertView.findViewById(R.id.m_pass_name);
			holder.tv_pass_time = (TextView)convertView.findViewById(R.id.m_pass_time);
			holder.iv_pass_icon = (ImageView)convertView.findViewById(R.id.m_pass_icon);
			holder.ll_view = (LinearLayout)convertView.findViewById(R.id.ll_view);
			holder.m_leave_view = (View)convertView.findViewById(R.id.m_leave_view);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		int[] imgs = {R.drawable.leave_dengdai,R.drawable.leave_tongyi,R.drawable.leave_jujue,R.drawable.leave_chexiao,R.drawable.leave_faqi,R.drawable.leave_bianji
	                };
		final PassUsersData passUsersData = passUsersDatas.get(position);
	    holder.tv_pass_status.setText(passUsersData.getStatus_name());
	    holder.tv_pass_name.setText(passUsersData.getName());
	    holder.tv_pass_time.setText(passUsersData.getAdd_time_str());
	    
	    if(position==(passUsersDatas.size()-1)){
	        holder.ll_view .setVisibility(View.GONE);
	    }
	    if(position==0){
	        holder.m_leave_view.setVisibility(View.VISIBLE);
	    }
	    //审批状态  0 = 审批中  1= 审批通过  2= 审批不通过 9 = 发起审批
	    if(!StringUtils.isEmpty(passUsersData.getStatus())){
	        switch (passUsersData.getStatus()) {
	        case "0":
	            holder.iv_pass_icon.setImageResource(imgs[0]);
	            break;
	        case "1":
	            holder.iv_pass_icon.setImageResource(imgs[1]);
	            break;
	        case "2":
	            holder.iv_pass_icon.setImageResource(imgs[2]);
	            break;
	        case "3":
	            holder.iv_pass_icon.setImageResource(imgs[3]);
	            break;
	        case "9":
	            holder.iv_pass_icon.setImageResource(imgs[4]);
	            break;
	        default:
	            break;
	        }
	    }
	    return convertView;
	}
	
	class Holder {
		ImageView iv_pass_icon;
		TextView tv_pass_name;
		TextView tv_pass_status;
		TextView tv_pass_time;
		LinearLayout ll_view;
		View m_leave_view;
	}
}
