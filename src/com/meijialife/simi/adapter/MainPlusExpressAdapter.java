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
import com.meijialife.simi.bean.ExpressData;
import com.meijialife.simi.bean.TeamData;
import com.meijialife.simi.inter.ListItemClickHelp;

/**
 * 加号--团建---适配器
 *
 */
public class MainPlusExpressAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<ExpressData> expressDatas;
	
	private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    private Context contexts;
    private ListItemClickHelp callback;//回调方法
    

	public MainPlusExpressAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		expressDatas = new ArrayList<ExpressData>();
		contexts = context;
		finalBitmap = FinalBitmap.create(context);
		//获取默认头像
        defDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ad_loading);
	}

	public void setData(ArrayList<ExpressData> expressDataList) {
		this.expressDatas = expressDataList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return expressDatas.size();
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
			convertView = inflater.inflate(R.layout.main_plus_express_item, null);
			holder.m_team_icon = (ImageView) convertView.findViewById(R.id.m_team_icon);
			holder.m_team_title = (TextView) convertView.findViewById(R.id.m_team_title);
			holder.m_team_money = (TextView) convertView.findViewById(R.id.m_team_money);
			holder.m_team_status = (TextView) convertView.findViewById(R.id.m_team_statue);
			holder.m_team_time = (TextView) convertView.findViewById(R.id.m_team_time);
			holder.m_team_add = (TextView)convertView.findViewById(R.id.m_team_add);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		final ExpressData expressData = expressDatas.get(position);
        finalBitmap.display(holder.m_team_icon, Constants.TEAM_ICON_URL, defDrawable.getBitmap(), defDrawable.getBitmap());
        holder.m_team_title.setText(expressData.getExpress_name());
        holder.m_team_time.setText("下单时间:"+expressData.getAdd_time_str());
        holder.m_team_status.setText(expressData.getFrom_name());
     
        
        return convertView;
	}
	class Holder {
		ImageView m_team_icon;
		TextView  m_team_title;
		TextView  m_team_money;
		TextView  m_team_status;
		TextView  m_team_time;
		TextView  m_team_add;
	}
}
