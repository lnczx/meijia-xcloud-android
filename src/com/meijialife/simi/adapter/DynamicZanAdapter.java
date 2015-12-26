package com.meijialife.simi.adapter;

import java.util.ArrayList;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.CardZan;
import com.meijialife.simi.bean.DynamicZan;
import com.meijialife.simi.ui.RoundImageView;

/**
 * 卡片被赞列表适配器
 *
 */
public class DynamicZanAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<DynamicZan> list;
	
	private FinalBitmap finalBitmap;
	private BitmapDrawable defDrawable;

	public DynamicZanAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		list = new ArrayList<DynamicZan>();
		
		finalBitmap = FinalBitmap.create(context);
		defDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_defult_touxiang);
	}

	public void setData(ArrayList<DynamicZan> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
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
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			convertView = inflater.inflate(R.layout.card_zan_grid_item, null);
			holder.image = (RoundImageView) convertView.findViewById(R.id.image);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		String url = list.get(position).getHead_img();
//		url = "http://img5.duitang.com/uploads/item/201504/21/20150421H4340_uv24P.thumb.224_0.jpeg";
        finalBitmap.display(holder.image, url, defDrawable.getBitmap(), defDrawable.getBitmap());
		
		return convertView;
	}
	
	class Holder {
		RoundImageView image;
	}

}
