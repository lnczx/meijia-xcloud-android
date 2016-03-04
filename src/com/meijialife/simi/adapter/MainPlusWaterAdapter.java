package com.meijialife.simi.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.MainPlusWaterActivity;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.bean.WaterData;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.inter.ListItemClickHelp;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * 加号--送水---适配器
 *
 */
public class MainPlusWaterAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<WaterData> waterDatas;
	
	private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    private Context contexts;
    private ListItemClickHelp callback;//回调方法
    

	public MainPlusWaterAdapter(Context context,MainPlusWaterActivity activity) {
		inflater = LayoutInflater.from(context);
		waterDatas = new ArrayList<WaterData>();
		contexts = context;
		finalBitmap = FinalBitmap.create(context);
		//获取默认头像
        defDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ad_loading);
        this.callback = activity;  
	}

	public void setData(ArrayList<WaterData> waterDatas) {
		this.waterDatas = waterDatas;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return waterDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			convertView = inflater.inflate(R.layout.main_plus_water_item, null);
			holder.m_water_icon = (ImageView) convertView.findViewById(R.id.m_water_icon);
			holder.m_water_title = (TextView) convertView.findViewById(R.id.m_water_title);
			holder.m_water_money = (TextView) convertView.findViewById(R.id.m_water_money);
			holder.m_water_status = (TextView) convertView.findViewById(R.id.m_water_statue);
			holder.m_water_time = (TextView) convertView.findViewById(R.id.m_water_time);
			holder.m_water_add = (TextView)convertView.findViewById(R.id.m_water_add);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		final WaterData waterData = waterDatas.get(position);
        String url = waterData.getImg_url();//送水商品图片
        finalBitmap.display(holder.m_water_icon, url, defDrawable.getBitmap(), defDrawable.getBitmap());
		
        holder.m_water_title.setText(waterData.getService_type_name());
        holder.m_water_money.setText("订单金额:"+waterData.getOrder_money()+"元");
        holder.m_water_time.setText("下单时间:"+waterData.getAdd_time_str());
        holder.m_water_status.setText(waterData.getOrder_status_name());
        int orderExtStatus = waterData.getOrder_ext_status();
        if(orderExtStatus==2){
            holder.m_water_add.setText("已签收");
            holder.m_water_add.setClickable(false);
            holder.m_water_add.setSelected(true);
        }else {
            holder.m_water_add.setText("签收");
            holder.m_water_add.setClickable(true);
            holder.m_water_add.setSelected(false);
        }
        holder.m_water_add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                postDone(waterData.getOrder_id());
            }
        });
        
        return convertView;
	}
	/**
     * 送水---签收接口
     */
    private void postDone(String orderId) {

        if (!NetworkUtils.isNetworkConnected(contexts)) {
            Toast.makeText(contexts, contexts.getString(R.string.net_not_open), 0).show();
            return;
        }
        User user = DBHelper.getUser(contexts);
        Map<String, String> map = new HashMap<String, String>();
        map.put("order_id", orderId);
        map.put("user_id", user.getId());
        AjaxParams param = new AjaxParams(map);

        new FinalHttp().post(Constants.URL_POST_WATER_DONE, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(contexts, contexts.getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            callback.onClick();
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            errorMsg = contexts.getString(R.string.servers_error);
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            errorMsg = contexts.getString(R.string.param_missing);
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            errorMsg = contexts.getString(R.string.param_illegal);
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            errorMsg = msg;
                        } else {
                            errorMsg = contexts.getString(R.string.servers_error);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = contexts.getString(R.string.servers_error);
                }
                // 操作失败，显示错误信息
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    UIUtils.showToast(contexts, errorMsg);
                }
            }
        });
    }
	class Holder {
		ImageView m_water_icon;
		TextView  m_water_title;
		TextView  m_water_money;
		TextView  m_water_status;
		TextView  m_water_time;
		TextView  m_water_add;
	}
}
