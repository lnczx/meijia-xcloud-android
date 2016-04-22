package com.meijialife.simi.adapter;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.Contact;
import com.meijialife.simi.bean.Friend;
import com.meijialife.simi.utils.SpFileUtil;


/**
 * @description：好友选择通讯录适配器
 * @author： kerryg
 * @date:2015年12月9日 
 */
public class ContactSelectAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<Contact> list;
    private ArrayList<Friend> checkedList;
    private int flag=1;
    private Context context;
    
    public ContactSelectAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        list = new ArrayList<Contact>();
        checkedList = new ArrayList<Friend>();
    }

    public void setData(ArrayList<Contact> list, ArrayList<Friend> checkedList) {
        this.list = list;
        this.checkedList = checkedList;
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
            convertView = inflater.inflate(R.layout.contact_selected_item, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.item_tv_name);
            holder.tv_id = (TextView) convertView.findViewById(R.id.item_tv_id);
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
        Contact contact = list.get(position);
        holder.tv_name.setText(contact.getName());
        holder.tv_mobile.setText(contact.getPhoneNum());
        boolean is_checked =SpFileUtil.getBoolean(context, SpFileUtil.KEY_CHECKED_FRIENDS,contact.getPhoneNum(),false);
        if(is_checked){
            holder.cb.setChecked(true);;
        }else {
            holder.cb.setChecked(false);;
        }
        return convertView;
    }

    class Holder {
        TextView tv_name;
        TextView tv_id;
        CheckBox cb;
        TextView tv_mobile;
        TextView tv_temp;
    }

}
