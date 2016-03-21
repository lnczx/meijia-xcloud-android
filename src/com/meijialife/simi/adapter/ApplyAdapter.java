package com.meijialife.simi.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.bean.FriendApplyData;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.inter.ListItemClickHelp;
import com.meijialife.simi.ui.RoundImageView;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * 好友申请适配器
 *
 */
public class ApplyAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<FriendApplyData> list;
	
	private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    private Context contexts;
    private ListItemClickHelp callback;//回调方法
    
	public ApplyAdapter(Context context,ListItemClickHelp callback) {
	    this.contexts = context;
	    this.callback = callback;
		inflater = LayoutInflater.from(context);
		list = new ArrayList<FriendApplyData>();
		finalBitmap = FinalBitmap.create(context);
        defDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_defult_touxiang);
	}

	public void setData(ArrayList<FriendApplyData> list) {
		this.list = list;
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
			convertView = inflater.inflate(R.layout.apply_list_item, null);
			holder.m_apply_name = (TextView) convertView.findViewById(R.id.m_apply_name);
			holder.m_apply_icon = (RoundImageView) convertView.findViewById(R.id.m_apply_icon);
			holder.m_apply_status = (TextView) convertView.findViewById(R.id.m_apply_status);
			holder.m_apply_status_name = (TextView) convertView.findViewById(R.id.m_apply_status_name);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		final FriendApplyData applyData = list.get(position);
		holder.m_apply_name.setText(applyData.getName());
        finalBitmap.display(holder.m_apply_icon,applyData.getHead_img(), defDrawable.getBitmap(), defDrawable.getBitmap());
		
        if(applyData.getReq_type()==1 && applyData.getStatus()==0){
            holder.m_apply_status.setVisibility(View.VISIBLE);
        }else {
            holder.m_apply_status.setVisibility(View.GONE);
        }
        if(applyData.getStatus()==1){//状态=同意，显示已添加
            holder.m_apply_status_name.setVisibility(View.VISIBLE);
        }else {
            holder.m_apply_status_name.setVisibility(View.GONE);
        }
        holder.m_apply_status.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                postFriendReq(applyData.getFriend_id(),"1");
            }
        });
        
        return convertView;
	}
	
	class Holder {
	    RoundImageView m_apply_icon;
		TextView m_apply_name;
		TextView m_apply_status;
		TextView m_apply_status_name;
	}

	
	 private void postFriendReq(String friendId,String status) {

	        if (!NetworkUtils.isNetworkConnected(contexts)) {
	            Toast.makeText(contexts, contexts.getString(R.string.net_not_open), 0).show();
	            return;
	        }
	        User user = DBHelper.getUser(contexts);
	        Map<String, String> map = new HashMap<String, String>();
	        map.put("friend_id", friendId);
	        map.put("user_id", user.getId());
	        map.put("status", status);
	        AjaxParams param = new AjaxParams(map);

	        new FinalHttp().post(Constants.URL_POST_FRIEND_REQ, param, new AjaxCallBack<Object>() {
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
}
