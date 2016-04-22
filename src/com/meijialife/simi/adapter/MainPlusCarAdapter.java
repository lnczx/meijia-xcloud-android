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
import com.meijialife.simi.bean.MyOrder;
/**
 * @description：我的订单--列表--适配器
 * @author： kerryg
 * @date:2015年11月14日 
 */
public class MainPlusCarAdapter extends BaseAdapter {
    
    //定义全局变量
	private LayoutInflater inflater;
    private ArrayList<MyOrder> orderList;
    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    private Context context;

	public MainPlusCarAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		orderList = new ArrayList<MyOrder>();
		finalBitmap = FinalBitmap.create(context);
		this.context = context;
		//默认图标赋值
        defDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.order_icon);
	}

	public void setData(ArrayList<MyOrder> list) {
		this.orderList = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return orderList.size();
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
			convertView = inflater.inflate(R.layout.main_plus_car_item, null);
			holder.m_car_title = (TextView) convertView.findViewById(R.id.m_car_title);
			holder.m_car_money = (TextView)convertView.findViewById(R.id.m_car_money);
			holder.m_car_time = (TextView)convertView.findViewById(R.id.m_car_time);
			holder.m_car_icon = (ImageView)convertView.findViewById(R.id.m_car_icon);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		holder.m_car_title.setText(orderList.get(position).getService_type_name()+":");
	    holder.m_car_time.setText("下单时间:"+orderList.get(position).getAdd_time_str());
	    holder.m_car_money.setText("订单金额:"+orderList.get(position).getOrder_pay()+"元");
	    finalBitmap.display(holder.m_car_icon, orderList.get(position).getPartner_user_head_img(), defDrawable.getBitmap(), defDrawable.getBitmap());
		
        return convertView;
	}
	
	class Holder {
		ImageView m_car_icon;
		TextView m_car_title;
		TextView m_car_money;
		TextView m_car_time;
	}

}
