package com.meijialife.simi.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.Friend;
import com.meijialife.simi.ui.RoundImageView;
import com.meijialife.simi.utils.StringUtils;

/**
 * 好友适配器
 *
 */
public class ContactsAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<Friend> list;

    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    private TextView tv_contacts_list;
    private Context context;
    private HashMap<Integer, String> showMap;

    // 用来控制CheckBox选中状态
    private static HashMap<Integer, Boolean> isSelected;

    public ContactsAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        list = new ArrayList<Friend>();
        View view = inflater.inflate(R.layout.contact_choose_list, null);
        tv_contacts_list = (TextView) view.findViewById(R.id.tv_contacts_list);
        finalBitmap = FinalBitmap.create(context);
        defDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_defult_touxiang);
        isSelected = new HashMap<Integer, Boolean>();
        showMap = new HashMap<Integer,String>();
    }

    public void setData(ArrayList<Friend> list, HashMap<Integer, String> showMap) {
        this.list = list;
        this.showMap = showMap;
        for (int i = 0; i < list.size(); i++) {
            getIsSelected().put(i, false);
        }
        notifyDataSetChanged();
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        ContactsAdapter.isSelected = isSelected;
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
            holder.cb.setVisibility(View.VISIBLE);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        if (StringUtils.isEmpty(list.get(position).getName())) {
            holder.tv_name.setText(list.get(position).getMobile());
        } else {
            holder.tv_name.setText(list.get(position).getName());
        }
        Iterator iter = showMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            entry.getKey();
            if ((int) entry.getKey() == position) {
                holder.cb.setChecked(true);
            }
        }
        holder.tv_mobile.setText(list.get(position).getMobile());
        holder.tv_id.setText(list.get(position).getId());
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
    }

}
