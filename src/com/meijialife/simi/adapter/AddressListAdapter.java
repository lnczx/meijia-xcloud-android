package com.meijialife.simi.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.meijialife.simi.bean.AddressData;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;

/**
 * 我的地址列表适配器
 *
 * @author RUI
 *
 */
public final class AddressListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<AddressData> datas;

	private LayoutInflater layoutInflater;
	private boolean showDel; // 是否显示删除按钮

	/**
	 * @param context上下文
	 * @param 数据列表
	 * @param showDel
	 *            是否显示删除按钮
	 */
	public AddressListAdapter(Context context, boolean showDel) {
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		this.datas = new ArrayList<AddressData>();
		this.showDel = showDel;
	}
	
	public void setData(ArrayList<AddressData> datas) {
		this.datas = datas;
		notifyDataSetChanged();
	}

	public int getCount() {
		return datas.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.address_list_item, null);//

			holder = new ViewHolder();

			holder.iv_del = (ImageView) convertView.findViewById(R.id.address_item_iv_del);
			holder.tv_addr = (TextView) convertView.findViewById(R.id.address_item_tv_addr);
			holder.tv_def = (TextView) convertView.findViewById(R.id.address_item_tv_def);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String name = datas.get(position).getName();
		String addr = datas.get(position).getAddr();
		holder.tv_addr.setText(name + " " + addr);

		if (showDel) {
			holder.iv_del.setVisibility(View.VISIBLE);
		} else {
			holder.iv_del.setVisibility(View.GONE);
		}
		holder.iv_del.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showTipsDlg(position);
			}
		});

		int is_def = datas.get(position).getIs_default();
		if (is_def == 1) {
			holder.tv_def.setVisibility(View.VISIBLE);
		} else {
			holder.tv_def.setVisibility(View.INVISIBLE);
		}

		return convertView;
	}

	private static class ViewHolder {

		ImageView iv_del; // 删除
		TextView tv_addr; // 地址
		TextView tv_def; // 是否默认
	}

	/**
	 * 删除提示
	 */
	private void showTipsDlg(final int position) {
		String msg = "是否删除该地址？";
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("是否删除？");
		builder.setMessage(msg);
		builder.setCancelable(false);
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			    postDelAddress(position);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		builder.create().show();
	}

	/**
     * 删除地址接口
     */
    private void postDelAddress(final int position) {
        AddressData data = datas.get(position);
        
        String user_id = DBHelper.getUser(context).getId();

        if (!NetworkUtils.isNetworkConnected(context)) {
            Toast.makeText(context, context.getString(R.string.net_not_open), 0).show();
            return;
        }
        
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id);
        map.put("addr_id", data.getId());//地址ID
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().post(Constants.URL_POST_DEL_ADDRS, param, new AjaxCallBack<Object>() {

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
                            Toast.makeText(context, "删除成功!", Toast.LENGTH_SHORT).show();
//                            getAddList();
                            datas.remove(position);
                            notifyDataSetChanged();
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