package com.meijialife.simi.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.bean.Contact;
import com.meijialife.simi.bean.Friend;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * 手机通讯录加好友适配器
 *
 */
public class ContactAddFriendsAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<Contact> contactList;
	private ArrayList<Friend> friendList;
	private Context context;

	public ContactAddFriendsAdapter(Context context) {
	    this.context = context;
		inflater = LayoutInflater.from(context);
		contactList = new ArrayList<Contact>();
		friendList = new ArrayList<Friend>();
	}

	/**
	 * 
	 * @param contactList  手机联系人数据
	 * @param friendList   现有好友数据
	 */
	public void setData(ArrayList<Contact> contactList, ArrayList<Friend> friendList) {
		this.contactList = contactList;
		this.friendList = friendList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return contactList.size();
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
			convertView = inflater.inflate(R.layout.contact_addfriends_list_item, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.item_tv_name);
			holder.tv_add = (TextView) convertView.findViewById(R.id.item_tv_add);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		holder.tv_name.setText(contactList.get(position).getName());
		
		boolean isFriend = false;//
		for(int i = 0; i < friendList.size(); i++){
		    if(friendList.get(i).getName().equals(contactList.get(position).getName())){
		        isFriend = true;
		    }
		}
		
		if(isFriend){
		    holder.tv_add.setBackgroundResource(R.drawable.btn_gray_background);
		    holder.tv_add.setEnabled(false);
		}else{
		    holder.tv_add.setBackgroundResource(R.drawable.login_btn_bg_selector);
		    holder.tv_add.setEnabled(true);
		    
		    holder.tv_add.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = contactList.get(position).getName();
                    String mobile = contactList.get(position).getPhoneNum();
                    postFriend(name, mobile);
                }
            });
		}
		
		return convertView;
	}
	
	class Holder {
		TextView tv_name;
		TextView tv_add;
	}
	
	/**
     * 添加好友接口
     */
    private void postFriend(String name, String mobile) {
        String user_id = DBHelper.getUser(context).getId();
        
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id);
        map.put("name", name);
        map.put("mobile", mobile);
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().post(Constants.URL_POST_FRIEND, param, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                LogOut.debug("错误码：" + errorNo);
                dismissDialog();
                Toast.makeText(context, context.getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                dismissDialog();
                LogOut.debug("成功:" + t.toString());

                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            getFriendList();
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            Toast.makeText(context, context.getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(context, context.getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(context, context.getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, context.getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
//                    UIUtils.showToast(context, "网络错误,请稍后重试");
                }

            }
        });
    }
    
    /**
     * 获取好友列表
     */
    public void getFriendList() {

        String user_id = DBHelper.getUser(context).getId();

        if (!NetworkUtils.isNetworkConnected(context)) {
            Toast.makeText(context, context.getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id+"");
        map.put("page", "1");
        AjaxParams param = new AjaxParams(map);

        showDialog();
        new FinalHttp().get(Constants.URL_GET_FRIENDS, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(context, context.getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                dismissDialog();
                LogOut.i("========", "onSuccess：" + t);
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            if(StringUtils.isNotEmpty(data)){
                                Gson gson = new Gson();
                                friendList = gson.fromJson(data, new TypeToken<ArrayList<Friend>>() {
                                }.getType());
                                notifyDataSetChanged();
//                                adapter.setData(friendList);
                            }else{
//                                adapter.setData(new ArrayList<Friend>());
                            }
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            errorMsg = context.getString(R.string.servers_error);
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            errorMsg = context.getString(R.string.param_missing);
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            errorMsg = context.getString(R.string.param_illegal);
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            errorMsg = msg;
                        } else {
                            errorMsg = context.getString(R.string.servers_error);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = context.getString(R.string.servers_error);

                }
                // 操作失败，显示错误信息
                if(!StringUtils.isEmpty(errorMsg.trim())){
                    UIUtils.showToast(context, errorMsg);
                }
            }
        });

    }
    
    private ProgressDialog m_pDialog;
    public void showDialog() {
        if(m_pDialog == null){
            m_pDialog = new ProgressDialog(context);
            m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            m_pDialog.setMessage("请稍等...");
            m_pDialog.setIndeterminate(false);
            m_pDialog.setCancelable(true);
        }
        m_pDialog.show();
    }

    public void dismissDialog() {
        if (m_pDialog != null && m_pDialog.isShowing()) {
            m_pDialog.hide();
        }
    }

}
