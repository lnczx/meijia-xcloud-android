package com.meijialife.simi.adapter;

import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.SecretaryImages;

/**
 * @description：秘书详情展示秘书生活照片
 * @author： kerryg
 * @date:2015年11月12日 
 */
public class HorizontalScrollViewAdapter
{

	private Context mContext;
	private LayoutInflater mInflater;
	private List<SecretaryImages> mDatas;
	
	private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    private int flag =1;//1=秘书自己真实照片 0=默认照片
    private Context context;

	public HorizontalScrollViewAdapter(Context context, List<SecretaryImages> mDatas,int flag)
	{
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
		this.mDatas =mDatas;
		this.flag = flag;
        defDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.mishutupian01);

	}

	public int getCount()
	{
		return mDatas.size();
	}

	public Object getItem(int position)
	{
		return mDatas.get(position);
	}

	public long getItemId(int position)
	{
		return position;
	}

    public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder = null;
		if (convertView == null)
		{
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(
					R.layout.activity_index_gallery_item, parent, false);
			viewHolder.mImg = (ImageView) convertView
					.findViewById(R.id.id_index_gallery_item_image);

			convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
//		viewHolder.mImg.setImageResource(mDatas.get(position));
		//viewHolder.mImg.setImageResourcem(mDatas.get(position).getImg_trumb());
		
		//列表中获取秘书的生活照
		if(flag==1){
	    String url = mDatas.get(position).getImg_trumb();
	    finalBitmap =FinalBitmap.create(context);
        finalBitmap.display(viewHolder.mImg, url, defDrawable.getBitmap(), defDrawable.getBitmap());
		}else {//显示默认的秘书生活照
		 viewHolder.mImg.setImageResource(mDatas.get(position).getDefault_img());
        }
		return convertView;
	}

	private class ViewHolder
	{
		ImageView mImg;
	}

}
