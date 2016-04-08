package com.meijialife.simi.adapter;

import java.util.List;

import com.meijialife.simi.bean.AdData;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class BannerAdapter extends PagerAdapter {

    private List<AdData> adList;
    private List<ImageView> imageViews;

    public BannerAdapter() {
        super();
    }

    public void setData(List<AdData> adList,List<ImageView> imageView){
        this.adList = adList;
        this.imageViews = imageView;
    }
    @Override
    public int getCount() {
        return adList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView iv = imageViews.get(position);
        ((ViewPager) container).addView(iv);
        final AdData adData = adList.get(position);

        return iv;

    }

    @Override
    public void destroyItem(View container, int position, Object arg2) {
        ((ViewPager) container).removeView((View) arg2);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

}
