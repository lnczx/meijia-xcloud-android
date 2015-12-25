package com.meijialife.simi.adapter;

import java.util.ArrayList;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.DynamicImaData;
import com.meijialife.simi.publish.NineGridAdapter;
import com.squareup.picasso.Picasso;

/**
 * @description：好友动态图片显示适配器
 * @author： kerryg
 * @date:2015年12月24日 
 */
public class DynamicImgAdapter extends NineGridAdapter {
    
    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;

    public DynamicImgAdapter(Context context, ArrayList<DynamicImaData> list) {
        super(context, list);
        finalBitmap = FinalBitmap.create(context);
        defDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ad_loading);
    }

    @Override
    public int getCount() {
        return (list==null)? 0 : list.size();
    }

    @Override
    public String getUrl(int positopn) {
        return getItem(positopn)==null ? 
                null: ((DynamicImaData)getItem(positopn)).getImg_small();
    }

    @Override
    public Object getItem(int position) {
        return (list == null) ? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view) {
        
        ImageView iv = null;
        if (view != null && view instanceof ImageView) {
            iv = (ImageView) view;
        } else {
            iv = new ImageView(context);
        }
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv.setBackgroundColor(Color.parseColor("#f5f5f5"));
        Picasso.with(context).load(getUrl(i)).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(iv);
        return iv;
    }

}
