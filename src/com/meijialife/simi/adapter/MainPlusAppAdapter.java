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
import android.graphics.Color;
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
import com.meijialife.simi.bean.AppToolsData;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.inter.ListItemClickHelp;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * 加号--应用中心---适配器
 *
 */
public class MainPlusAppAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<AppToolsData> appToolsDatas;
	
	private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    private Context contexts;
    private ListItemClickHelp callback;//回调方法
    

	public MainPlusAppAdapter(Context context,ListItemClickHelp callback) {
		inflater = LayoutInflater.from(context);
		appToolsDatas = new ArrayList<AppToolsData>();
		contexts = context;
		finalBitmap = FinalBitmap.create(context);
		//获取默认头像
        defDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ad_loading);
        this.callback = callback;
	}

	public void setData(ArrayList<AppToolsData> appToolsDatas) {
		this.appToolsDatas = appToolsDatas;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return appToolsDatas.size();
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
			convertView = inflater.inflate(R.layout.main_plus_app_item, null);
			holder.m_center_icon = (ImageView) convertView.findViewById(R.id.m_center_icon);
			holder.m_center_title = (TextView) convertView.findViewById(R.id.m_center_title);
			holder.m_center_describe = (TextView) convertView.findViewById(R.id.m_center_describe);
			holder.m_center_provider = (TextView) convertView.findViewById(R.id.m_center_provider);
			holder.m_center_add = (TextView)convertView.findViewById(R.id.m_center_add);
			holder.m_center_default_add = (TextView)convertView.findViewById(R.id.m_center_default_add);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		AppToolsData appToolsData = appToolsDatas.get(position);
		//将默认头像摄者为秘书头像/
        String url = appToolsData.getLogo();     //获得头像的url
        finalBitmap.display(holder.m_center_icon, url, defDrawable.getBitmap(), defDrawable.getBitmap());
		
        holder.m_center_title.setText(appToolsData.getName());
        holder.m_center_provider.setText("提供者:"+appToolsData.getApp_provider());
        holder.m_center_describe.setText(appToolsData.getApp_describe());
        Short status = appToolsData.getStatus();
        Short is_default = appToolsData.getIs_default();
        Short is_del = appToolsData.getIs_del();
        /**
         * 判断逻辑：
         * 1.status = null  is_default =0不是默认 添加
           2.status = null  is_default =1  是默认 is_del = 0 取消 is_del = 1 已添加
           3.status != null status = 0 添加  status =1 取消
         */
        if(status==null){
            if(is_default==1){
                holder.m_center_default_add.setVisibility(View.GONE);
                holder.m_center_add.setVisibility(View.VISIBLE);
                holder.m_center_add.setText("添加");
                holder.m_center_add.setTextColor(Color.parseColor("#E8374A"));
                holder.m_center_add.setSelected(true);
            }else {
                if(is_default==0){
                    holder.m_center_default_add.setVisibility(View.GONE);
                    holder.m_center_add.setVisibility(View.VISIBLE);
                    holder.m_center_add.setText("取消");
                    holder.m_center_add.setTextColor(Color.parseColor("#929292"));
                    holder.m_center_add.setSelected(false);
                }else if(is_del==1){
                    holder.m_center_default_add.setVisibility(View.VISIBLE);
                    holder.m_center_add.setVisibility(View.GONE);
                }
            }
        }else {
           if(status==1){
               if(is_del==0){
                   holder.m_center_default_add.setVisibility(View.GONE);
                   holder.m_center_add.setVisibility(View.VISIBLE);
                   holder.m_center_add.setText("取消");
                   holder.m_center_add.setTextColor(Color.parseColor("#929292"));
                   holder.m_center_add.setSelected(false);
               }else {
                   holder.m_center_default_add.setVisibility(View.VISIBLE);
                   holder.m_center_add.setVisibility(View.GONE);
               }
           }else if(status==0) {
               holder.m_center_default_add.setVisibility(View.GONE);
               holder.m_center_add.setVisibility(View.VISIBLE);
               holder.m_center_add.setText("添加");
               holder.m_center_add.setTextColor(Color.parseColor("#E8374A"));
               holder.m_center_add.setSelected(true);
           }
        }
        holder.m_center_add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppToolsData appToolsData = appToolsDatas.get(position);
                String t_id = appToolsData.getT_id();
                short status = appToolsData.getStatus();
                short temp_status = 0;
                if(status==1){
                    temp_status = 0;
                }else if (status==0) {
                    temp_status = 1;
                }
                addApp(t_id, temp_status);
            }
        });
        return convertView;
	}
	
	
	/**
     * 新增应用显示配置接口
     */
    private void addApp(String t_id,short status) {

        if (!NetworkUtils.isNetworkConnected(contexts)) {
            Toast.makeText(contexts, contexts.getString(R.string.net_not_open), 0).show();
            return;
        }
        User user = DBHelper.getUser(contexts);
        Map<String, String> map = new HashMap<String, String>();
        map.put("t_id", t_id);
        map.put("user_id", user.getId());
        map.put("status", status+"");
        AjaxParams param = new AjaxParams(map);

        new FinalHttp().post(Constants.URL_GET_USER_APP_TOOLS, param, new AjaxCallBack<Object>() {
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
		ImageView m_center_icon;
		TextView  m_center_title;
		TextView  m_center_describe;
		TextView  m_center_provider;
		TextView  m_center_add;
		TextView  m_center_default_add;
	}
}
