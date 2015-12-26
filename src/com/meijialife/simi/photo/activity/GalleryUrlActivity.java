package com.meijialife.simi.photo.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.SecretaryImages;
import com.meijialife.simi.photo.adapter.BasePagerAdapter.OnItemChangeListener;
import com.meijialife.simi.photo.adapter.GalleryViewPager;
import com.meijialife.simi.photo.adapter.UrlPagerAdapter;


/**
 * @description：显示大图支持滑动和缩放
 * @author： kerryg
 * @date:2015年11月12日 
 */
public class GalleryUrlActivity extends Activity {

    private GalleryViewPager mViewPager;
    private TextView titlebar_title;
    private  List<String> list_imgs  = new ArrayList<String>();
    private  List<SecretaryImages> secImageData;
    private int my_position=0;
	private int lastIndex;
    @SuppressWarnings("unchecked")
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sec_img_big);
        Intent intent=getIntent();
        titlebar_title=(TextView) findViewById(R.id.titlebar_title);
        secImageData = (List<SecretaryImages>) intent.getSerializableExtra("list_img");
        for (Iterator iterator = secImageData.iterator(); iterator.hasNext();) {
            SecretaryImages secretaryImages = (SecretaryImages) iterator.next();
            list_imgs.add(secretaryImages.getImg_url());
        }
        my_position=intent.getIntExtra("tag", my_position);
        UrlPagerAdapter pagerAdapter = new UrlPagerAdapter(GalleryUrlActivity.this, list_imgs);
        pagerAdapter.setOnItemChangeListener(new OnItemChangeListener()
		{
			@Override
			public void onItemChange(int currentPosition)
			{
				titlebar_title.setText((currentPosition + 1) + " / " + list_imgs.size());
			}
		});
        titlebar_title.setText((my_position + 1) + " / " + list_imgs.size());
        mViewPager = (GalleryViewPager)findViewById(R.id.viewer);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(my_position, true);
    }
	
}