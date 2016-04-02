package com.meijialife.simi.adapter;

import java.util.ArrayList;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.Friend;
import com.meijialife.simi.ui.RoundImageView;
import com.meijialife.simi.utils.SpFileUtil;
import com.meijialife.simi.utils.StringUtils;

/**
 * 好友适配器
 *
 */
public class StaffLeaveAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<Friend> list;
    private ArrayList<Friend> checkedList;

    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    private int flag=1;
    private Context context;
   
    public StaffLeaveAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        list = new ArrayList<Friend>();
        finalBitmap = FinalBitmap.create(context);
        defDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_defult_touxiang);
    }

    public void setData(ArrayList<Friend> list,ArrayList<Friend> checkedList,int flag) {
        this.list = list;
        this.checkedList = checkedList;
        this.flag = flag;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            holder = new Holder();
            convertView = inflater.inflate(R.layout.friend_list_item, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.item_tv_name);
            holder.tv_id = (TextView) convertView.findViewById(R.id.item_tv_id);
            holder.iv_header = (RoundImageView) convertView.findViewById(R.id.item_iv_icon);
            holder.tv_mobile = (TextView)convertView.findViewById(R.id.item_tv_mobile);
            holder.tv_temp = (TextView)convertView.findViewById(R.id.item_tv_temp);
            holder.m_iv_friend = (ImageView)convertView.findViewById(R.id.m_iv_friend);
            holder.m_ll_checked = (LinearLayout)convertView.findViewById(R.id.m_ll_checked);
            holder.cb_check_box = (CheckBox)convertView.findViewById(R.id.cb_check_box);
            if(flag==1){
                holder.cb_check_box.setVisibility(View.VISIBLE);
            }else {
                holder.cb_check_box.setVisibility(View.GONE);
            }
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        
        if (StringUtils.isEmpty(list.get(position).getName())) {
            holder.tv_name.setText(list.get(position).getMobile());
        } else {
            holder.tv_name.setText(list.get(position).getName());
        }
        holder.tv_mobile.setText(list.get(position).getMobile());
        holder.tv_id.setText(list.get(position).getFriend_id());
        
        Friend friend = list.get(position);
        boolean is_checked =false;
        is_checked =SpFileUtil.getBoolean(context, SpFileUtil.KEY_CHECKED_STAFFS,friend.getFriend_id(),false);
        if(is_checked){
            holder.cb_check_box.setChecked(true);
        }else {
            holder.cb_check_box.setChecked(false);
        }
      
        String url = list.get(position).getHead_img();
        finalBitmap.display(holder.iv_header, url, defDrawable.getBitmap(), defDrawable.getBitmap());
        return convertView;
    }

    class Holder {
        RoundImageView iv_header;
        TextView tv_name;
        TextView tv_id;
        TextView tv_mobile;
        TextView tv_temp;
        ImageView m_iv_friend;
        LinearLayout m_ll_checked;
        CheckBox cb_check_box;
    }

}
