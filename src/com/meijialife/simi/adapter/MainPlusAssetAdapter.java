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
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.AssetData;
import com.meijialife.simi.ui.RoundImageView;

/**
 * 加号--资产列表---适配器
 *
 */
public class MainPlusAssetAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<AssetData> assetDatas;
	
	private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    private Context contexts;
    

	public MainPlusAssetAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		assetDatas = new ArrayList<AssetData>();
		contexts = context;
		finalBitmap = FinalBitmap.create(context);
		//获取默认头像
        defDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ad_loading);
	}

	public void setData(ArrayList<AssetData> assetDataList) {
		this.assetDatas = assetDataList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return assetDatas.size();
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
			convertView = inflater.inflate(R.layout.main_plus_asset_item, null);
			holder.m_center_icon = (RoundImageView) convertView.findViewById(R.id.m_center_icon);
			holder.m_asset_title = (TextView) convertView.findViewById(R.id.m_asset_title);
			holder.m_asset_amount = (TextView) convertView.findViewById(R.id.m_asset_amount);
			holder.m_asset_name = (TextView) convertView.findViewById(R.id.m_asset_name);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		AssetData assetData = assetDatas.get(position);
		//将默认头像摄者为秘书头像/
       /* String url = assetData.getHead_img();     //获得头像的url
        finalBitmap.display(holder.m_center_icon, url, defDrawable.getBitmap(), defDrawable.getBitmap());
		*/
        holder.m_asset_title.setText("资产名称:"+assetData.getName());
        holder.m_asset_amount.setText("总金额:"+assetData.getTotal_price()+"元");
        holder.m_asset_name.setText("编号:"+assetData.getSeq());
       
        return convertView;
	}
	
	class Holder {
	    RoundImageView m_center_icon;
		TextView  m_asset_title;//资产名称
		TextView  m_asset_amount;//数量
		TextView  m_asset_name;//入库人名称
	}
}
