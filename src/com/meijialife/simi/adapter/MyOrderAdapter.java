package com.meijialife.simi.adapter;

import java.util.ArrayList;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.OrderDetailsActivity;
import com.meijialife.simi.activity.PayOrderActivity;
import com.meijialife.simi.bean.MyOrder;
/**
 * @description：我的订单--列表--适配器
 * @author： kerryg
 * @date:2015年11月14日 
 */
public class MyOrderAdapter extends BaseAdapter {
    
    //定义全局变量
	private LayoutInflater inflater;
    private ArrayList<MyOrder> orderList;
    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    private Context context;

	public MyOrderAdapter(Context context) {
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
			convertView = inflater.inflate(R.layout.my_order_list_item, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.item_tv_name);
			holder.tv_date = (TextView)convertView.findViewById(R.id.item_tv_date);
			holder.tv_status = (TextView)convertView.findViewById(R.id.item_tv_status);
			holder.iv_head_img = (ImageView)convertView.findViewById(R.id.item_iv_icon);
			holder.tv_addr = (TextView)convertView.findViewById(R.id.item_tv_order_addr);
			holder.ll_order_status = (RelativeLayout)convertView.findViewById(R.id.ll_order_status);
			holder.tv_order_money = (TextView)convertView.findViewById(R.id.item_tv_order_money);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		holder.tv_name.setText(orderList.get(position).getService_type_name()+":");
	    holder.tv_date.setText(orderList.get(position).getAdd_time_str());
	    holder.tv_status.setText(orderList.get(position).getOrder_status_name());
	    holder.tv_order_money.setText(orderList.get(position).getOrder_money()+"元");
	    finalBitmap.display(holder.iv_head_img, orderList.get(position).getPartner_user_head_img(), defDrawable.getBitmap(), defDrawable.getBitmap());
		holder.tv_addr.setText(orderList.get(position).getAddr_name());
		      holder.ll_order_status.setOnClickListener(new OnClickListener() {
	              @Override
	              public void onClick(View v) {
	                  if(Constants.ORDER_NOT_PAY==orderList.get(position).getOrder_status()){
	                      Intent intent = new Intent(context,PayOrderActivity.class);
	                      intent.putExtra("flag",PayOrderActivity.FROM_MYORDER);
	                      intent.putExtra("myOrder", orderList.get(position));
	                      context.startActivity(intent);
	                  }
	                 
	              }
	          });
        return convertView;
	}
	
	class Holder {
		TextView tv_name;
		TextView tv_date;
		TextView tv_status;
		ImageView iv_head_img;
		TextView tv_addr;
		TextView tv_order_money;
		RelativeLayout ll_order_status;
	}

}
