package com.meijialife.simi.photo.adapter;

import java.lang.ref.WeakReference;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.meijialife.simi.photo.touchview.UrlTouchImageView;

/**
 * @description：支持大图滑动的的适配器
 * @author： kerryg
 * @date:2015年11月12日 
 */
public class UrlPagerAdapter extends BasePagerAdapter {
	WeakReference<Activity> weak; // 定义弱引用变量
//	private GalleryUrlActivity activity=new GalleryUrlActivity();
	public UrlPagerAdapter(Context context, List<String> resources)
	{
		
		super(context, resources);
		this.weak = new WeakReference<Activity>((Activity)context);
	}

	@Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        ((GalleryViewPager)container).mCurrentView = ((UrlTouchImageView)object).getImageView();
        ((GalleryViewPager)container).mCurrentView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				GalleryUrlActivity.finish();
				final Activity activity = weak.get();
				activity.finish();
			}
		});
    }

    @Override
    public Object instantiateItem(ViewGroup collection, final int position){
        final UrlTouchImageView iv = new UrlTouchImageView(mContext);
        iv.setUrl(mResources.get(position));
        iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        collection.addView(iv, 0);
        return iv;
    }
}
