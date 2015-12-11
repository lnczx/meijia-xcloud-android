package com.meijialife.simi.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.meijialife.simi.R;


/**
 * @description：好友选择通讯录适配器
 * @author： kerryg
 * @date:2015年12月9日 
 */
public class ContactSelectAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<String> list;
    private Context context;
    private ArrayList<String> contactList;
    private int flag=1;
    
    // 用来控制CheckBox选中状态
    private static HashMap<Integer, Boolean> isSelected;

    public ContactSelectAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        list = new ArrayList<String>();
        contactList = new ArrayList<String>();
    }

    public void setData(ArrayList<String> list, ArrayList<String> contactList) {
        this.list = list;
        this.contactList = contactList;
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
        
        String str = list.get(position);
        String name = str.substring(0,str.indexOf("\n"));
        String mobile = str.substring(str.indexOf("\n")+1,str.lastIndexOf("\n"));
        String user_id = str.substring(str.lastIndexOf("\n")+1,str.length());
        
        holder.tv_name.setText(name);
        holder.tv_mobile.setText(mobile);
        holder.tv_id.setText(user_id);
        holder.tv_temp.setText(str);
        if(contactList.size()>0){
            for (int i = 0; i < contactList.size(); i++) {
                CharSequence contatct = contactList.get(i);
                String temp1 = contatct.toString();
                String mobile2 = temp1.substring(temp1.indexOf("\n")+1,temp1.lastIndexOf("\n"));
                CharSequence temp = str;
                if(mobile2.equals(mobile)){
                    holder.cb.setChecked(true);
                }
            } 
        }else {
            holder.cb.setChecked(false);
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
