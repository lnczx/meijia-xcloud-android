package com.meijialife.simi.adapter;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.AssetConsumeActivity;
import com.meijialife.simi.bean.AssetData;
import com.meijialife.simi.bean.AssetJsons;
import com.meijialife.simi.utils.StringUtils;

/**
 * Created by bobge on 15/7/31.
 */
public class GoodsAdapter extends BaseAdapter {

    private List<AssetData> list;
    private Context context;
    private CatograyAdapter catograyAdapter;

    public GoodsAdapter(Context context, List<AssetData> list1) {
        this.context = context;
        this.list = list1;
    }

    public GoodsAdapter(Context context, List<AssetData> list2, CatograyAdapter catograyAdapter) {
        this.context = context;
        this.list = list2;
        this.catograyAdapter = catograyAdapter;
    }

    public void setData(List<AssetData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final Viewholder viewholder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.right_listview, null);
            viewholder = new Viewholder();
            viewholder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            viewholder.tv_desc = (TextView) view.findViewById(R.id.tv_desc);
            viewholder.tv_price = (TextView) view.findViewById(R.id.tv_price);
            viewholder.iv_add = (ImageView) view.findViewById(R.id.iv_add);
            viewholder.iv_remove = (ImageView) view.findViewById(R.id.iv_remove);
            viewholder.et_acount = (EditText) view.findViewById(R.id.et_count);
            view.setTag(viewholder);
        } else if (list != null && list.size() > 0) {

            final AssetData assetData = list.get(position);
            viewholder = (Viewholder) view.getTag();
            viewholder.tv_name.setText(list.get(position).getName());
            viewholder.tv_desc.setText(list.get(position).getSeq());
            viewholder.tv_price.setText(list.get(position).getPrice());
            viewholder.iv_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    int count = list.get(position).getCount();
                    int count = 0;
                    if(Constants.ASSET_JSON !=null && Constants.ASSET_JSON.size()>0){
                        for (int i = 0; i < Constants.ASSET_JSON.size(); i++) {
                            AssetJsons assetJson = (AssetJsons)Constants.ASSET_JSON.get(i);
                            if(list.get(position).getAsset_id()==assetJson.getAsset_id()){
                                count = assetJson.getTotal();
                                count++;
                                Constants.ASSET_JSON.get(position).setTotal(count);
                            }else {
                                AssetJsons assetJsons = new AssetJsons(assetData.getAsset_id(),count++);
                                Constants.ASSET_JSON.add(assetJsons);
                                break;
                            }
                        }
                    }else {
                        count++;
                        AssetJsons assetJsons = new AssetJsons(assetData.getAsset_id(),count);
                        Constants.ASSET_JSON.add(assetJsons);
                    }
                    viewholder.et_acount.setVisibility(View.VISIBLE);
                    viewholder.iv_remove.setVisibility(View.VISIBLE);
                    viewholder.et_acount.setText(count + "");
                    catograyAdapter.notifyDataSetChanged();

                    int[] startLocation = new int[2];// 一个整型数组，用来存储按钮的在屏幕的X、Y坐标
                    v.getLocationInWindow(startLocation);// 这是获取购买按钮的在屏幕的X、Y坐标（这也是动画开始的坐标）
                    ImageView ball = new ImageView(context);// buyImg是动画的图片，我的是一个小球（R.drawable.sign）
                    ball.setImageResource(R.drawable.ic_launcher);// 设置buyImg的图片
                    ((AssetConsumeActivity) context).setAnim(ball, startLocation);// 开始执行动画
                }
            });
            viewholder.iv_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count = 0;
                    if(Constants.ASSET_JSON !=null && Constants.ASSET_JSON.size()>0){
                        for (int i = 0; i < Constants.ASSET_JSON.size(); i++) {
                            AssetJsons assetJson = (AssetJsons)Constants.ASSET_JSON.get(i);
                            if(list.get(position).getAsset_id()==assetJson.getAsset_id()){
                                count = assetJson.getTotal();
                                count--;
                                if(count<0){
                                    count=0;
                                }
                                Constants.ASSET_JSON.get(position).setTotal(count);
                            }
                        }
                    }
                    if(count==0){
                        viewholder.et_acount.setVisibility(View.GONE);
                        viewholder.iv_remove.setVisibility(View.GONE);
                    }
                    viewholder.et_acount.setText(count + "");
                    catograyAdapter.notifyDataSetChanged();
                }
            });
            
            if (list.get(position).getCount() <= 0) {
                viewholder.et_acount.setVisibility(View.INVISIBLE);
                viewholder.iv_remove.setVisibility(View.INVISIBLE);
            } else {
                viewholder.et_acount.setVisibility(View.VISIBLE);
                viewholder.iv_remove.setVisibility(View.VISIBLE);
            }
        }
        return view;
    }

    class Viewholder {
        TextView tv_name;
        TextView tv_desc;
        TextView tv_price;
        ImageView iv_add, iv_remove;
        EditText et_acount;
    }

}
