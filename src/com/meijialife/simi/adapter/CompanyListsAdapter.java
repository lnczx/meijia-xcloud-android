package com.meijialife.simi.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.CompanyData;
import com.meijialife.simi.utils.StringUtils;


/**
 * @description：公司列表适配器
 * @author： kerryg
 * @date:2015年12月5日 
 */
public final class CompanyListsAdapter extends BaseAdapter {

	private Context context;
	private List<CompanyData> datas;
	private LayoutInflater layoutInflater;

	/**
	 * @param context上下文
	 * @param 数据列表
	 * @param showDel
	 *            是否显示删除按钮
	 */
	public CompanyListsAdapter(Context context) {
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		this.datas = new ArrayList<CompanyData>();
	}
	
	public void setData(List<CompanyData> companyDataList) {
		this.datas = companyDataList;
		notifyDataSetChanged();
	}

	@Override
    public int getCount() {
		return datas.size();
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
		ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.company_lists_item, null);//

			holder = new ViewHolder();
			
			holder.item_tv_id = (TextView)convertView.findViewById(R.id.item_tv_id);
			holder.item_tv_name = (TextView)convertView.findViewById(R.id.item_tv_name);
			holder.item_selected_icon = (ImageView)convertView.findViewById(R.id.iv_selected_icon);
		
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(StringUtils.isEquals(itemId, datas.get(position).getCompany_id())){
		    holder.item_selected_icon.setVisibility(View.VISIBLE);
		}else {
		    holder.item_selected_icon.setVisibility(View.GONE);
        }
		holder.item_tv_id.setText(datas.get(position).getCompany_id());
		holder.item_tv_name.setText(datas.get(position).getCompany_name());
		return convertView;
	}

	private static class ViewHolder {
		TextView item_tv_id; //公司id
		TextView item_tv_name;//公司名称
		ImageView item_selected_icon;//
	}

	private String itemId;

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
	

}