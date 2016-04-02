package com.meijialife.simi.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.Friend;
import com.meijialife.simi.ui.RoundImageView;
import com.meijialife.simi.utils.StringUtils;

/**
 * 好友适配器
 *
 */
public class ContactsAdapter1 extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<Friend> list;

    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    private Context context;
    private ArrayList<String> contactList;
    private int flag=1;
    
    // 用来控制CheckBox选中状态
    private static HashMap<Integer, Boolean> isSelected;

    public ContactsAdapter1(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        list = new ArrayList<Friend>();
        finalBitmap = FinalBitmap.create(context);
        defDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_defult_touxiang);
        isSelected = new HashMap<Integer, Boolean>();
        contactList = new ArrayList<String>();
    }

    public void setData(ArrayList<Friend> list, ArrayList<String> contactList,int flag) {
        this.list = list;
        this.contactList = contactList;
        for (int i = 0; i < list.size(); i++) {
            getIsSelected().put(i, false);
        }
        this.flag = flag;
        notifyDataSetChanged();
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        ContactsAdapter1.isSelected = isSelected;
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
            holder.cb = (CheckBox) convertView.findViewById(R.id.cb_check_box);
            holder.tv_mobile = (TextView)convertView.findViewById(R.id.item_tv_mobile);
            holder.tv_temp = (TextView)convertView.findViewById(R.id.item_tv_temp);
            if(flag==1){
                holder.cb.setVisibility(View.VISIBLE);
            }else {
                holder.cb.setVisibility(View.GONE);
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
        String contactStr =list.get(position).getName()+"\n"+
                    list.get(position).getMobile()+"\n"+list.get(position).getFriend_id();
        holder.tv_temp.setText(contactStr);
        if(contactList.size()>0){
            for (int i = 0; i < contactList.size(); i++) {
                CharSequence contatct = contactList.get(i);
                CharSequence temp = contactStr;
                if(contatct.equals(temp)){
                    holder.cb.setChecked(true);
                }
            } 
        }else {
            holder.cb.setChecked(false);
        }
        String url = list.get(position).getHead_img();
        finalBitmap.display(holder.iv_header, url, defDrawable.getBitmap(), defDrawable.getBitmap());
        return convertView;
    }

    class Holder {
        RoundImageView iv_header;
        TextView tv_name;
        TextView tv_id;
        CheckBox cb;
        TextView tv_mobile;
        TextView tv_temp;
    }

}
