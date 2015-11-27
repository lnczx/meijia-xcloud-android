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
import android.widget.CompoundButton;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.Friend;
import com.meijialife.simi.ui.RoundImageView;

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
    
    //用来控制CheckBox选中状态
    private static HashMap<Integer,Boolean> isSelected;

	public ContactsAdapter(Context context) {
	    this.context = context;
		inflater = LayoutInflater.from(context);
		list = new ArrayList<Friend>();
		View view =inflater.inflate(R.layout.contact_choose_list, null);
		tv_contacts_list = (TextView)view.findViewById(R.id.tv_contacts_list);
		finalBitmap = FinalBitmap.create(context);
        defDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_defult_touxiang);
        isSelected = new HashMap<Integer,Boolean>();
	}

	public void setData(ArrayList<Friend> list) {
		this.list = list;
		for (int i = 0; i < list.size(); i++) {
            getIsSelected().put(i,false);
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
			holder.iv_header = (RoundImageView) convertView.findViewById(R.id.item_iv_icon);
			holder.cb = (CheckBox)convertView.findViewById(R.id.cb_check_box);
			holder.cb.setVisibility(View.VISIBLE);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		holder.tv_name.setText(list.get(position).getName());
		String url = list.get(position).getHead_img();
        finalBitmap.display(holder.iv_header, url, defDrawable.getBitmap(), defDrawable.getBitmap());
		//监听checkbox，并根据原来的状态设置新的状态
       /* holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    isSelected.put(position, true); 
                    setIsSelected(isSelected); 
                }
                else
                {
                    isSelected.put(position, false);
                    setIsSelected(isSelected); 
                }
            }
        } );
        
        
        holder.cb.setChecked(getIsSelected().get(position));*/
		return convertView;
	}
	
	class Holder {
	    RoundImageView iv_header;
		TextView tv_name;
		CheckBox cb;
	}

}
