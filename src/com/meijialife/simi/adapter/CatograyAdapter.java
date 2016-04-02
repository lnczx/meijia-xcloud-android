package com.meijialife.simi.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.XcompanySetting;

/**
 * Created by bobge on 15/7/31.
 */
public class CatograyAdapter extends BaseAdapter {

    private Context context;
    private List<XcompanySetting> list;
    public CatograyAdapter(Context context, List<XcompanySetting> list) {
        this.context=context;
        this.list=list;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=convertView;
        Viewholder viewholder;
        if (view==null){
            view= LayoutInflater.from(context).inflate(R.layout.left_listview,null);
            viewholder=new Viewholder();
            viewholder.tv_catogray= (TextView) view.findViewById(R.id.tv_catogray);
            viewholder.tv_count= (TextView) view.findViewById(R.id.tv_count);
            view.setTag(viewholder);
        }else
            viewholder= (Viewholder) view.getTag();

        XcompanySetting setting = list.get(position);
        viewholder.tv_catogray.setText(setting.getName());
        int count=0;
      /*  for (int i = 0; i < list.get(position).getList().size(); i++) {
            count+=list.get(position).getList().get(i).getCount();
        }*/
        list.get(position).setCount(count);
        if (count<=0){
            viewholder.tv_count.setVisibility(View.INVISIBLE);
        }else{
            viewholder.tv_count.setVisibility(View.VISIBLE);
            viewholder.tv_count.getPaint().setAntiAlias(true);
            viewholder.tv_count.setText(count+"");
        }
        return view;
    }
    class Viewholder{
         TextView tv_catogray;
        TextView tv_count;
    }
}
