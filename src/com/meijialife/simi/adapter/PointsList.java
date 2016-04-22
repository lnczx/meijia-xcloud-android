//package com.meijialife.simi.adapter;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//
//import android.app.Activity;
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.meijialife.simi.R;
//import com.meijialife.simi.bean.PointsData;
//
///**
// * 积分明细列表适配器
// *
// * @author RUI
// *
// */
//public final class PointsList {
//
//	private Context context;
//	private ArrayList<PointsData> datas;
//
//	private LayoutInflater layoutInflater;
//
//	/**
//	 * @param context上下文
//	 * @param 数据列表
//	 */
//	public PointsList(Context context, Activity activity , ArrayList<PointsData> datas) {
//		this.context = context;
//		layoutInflater = LayoutInflater.from(context);
//		this.datas = datas;
////		LinearLayout moreDataLinearLayout = (LinearLayout) activity.findViewById(R.id.more_data_item_root);
////		for(PointsData pointsData : datas){
////			LinearLayout linearLayout = getItemView(pointsData);
////			moreDataLinearLayout.addView(linearLayout);
////		}
//	}
//
//	public LinearLayout getItemView(PointsData pointsData) {
//		ViewHolder holder;
//		LinearLayout convertView = (LinearLayout) layoutInflater.inflate(
//				R.layout.points_tab2_list_item, null);//
//
//		holder = new ViewHolder();
//
//		holder.tv_details = (TextView) convertView
//				.findViewById(R.id.item_tv_details);
//		holder.tv_points = (TextView) convertView
//				.findViewById(R.id.item_tv_points);
//		holder.tv_date = (TextView) convertView.findViewById(R.id.item_tv_date);
//
//		convertView.setTag(holder);
//
//		// 1 = 订单获得积分 2 = 订单使用积分 3 = 分享获得积分
//		int flag = pointsData.getAction_id();
//		switch (flag) {
//		case 1:
//			holder.tv_details.setText("订单获得");
//			break;
//		case 2:
//			holder.tv_details.setText("订单使用");
//			break;
//		case 3:
//			holder.tv_details.setText("分享获得");
//			break;
//		default:
//			break;
//		}
//
//		// 0 = 获得 + 1 = 使用 -
//		int consume = pointsData.getIs_consume();
//		String score = pointsData.getScore();
//		if (consume == 0) {
//			holder.tv_points.setText("+" + score);
//		} else if (consume == 1) {
//			holder.tv_points.setText("-" + score);
//		}
//
//		long dateL = Long.parseLong(pointsData.getAdd_time());
//		String date = new SimpleDateFormat("yyyy-MM-dd").format(dateL * 1000);
//		holder.tv_date.setText(date);
//
//		return convertView;
//	}
//
//	private static class ViewHolder {
//
//		TextView tv_details; // 详情
//		TextView tv_points; // 积分
//		TextView tv_date; // 时间
//	}
//
//}