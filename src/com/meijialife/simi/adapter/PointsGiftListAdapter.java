package com.meijialife.simi.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.PointsGiftData;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;

/**
 * 积分兑换列表适配器
 * @author RUI
 *
 */
public final class PointsGiftListAdapter extends BaseAdapter {

	private Context context;
	private Account account;
	private ArrayList<PointsGiftData> datas;

	private LayoutInflater layoutInflater;

	/**
	 * @param context上下文
	 * @param 数据列表
	 */
	public PointsGiftListAdapter(Context context, Account account,ArrayList<PointsGiftData> datas) {
		this.context = context;
		this.account = account;
		layoutInflater = LayoutInflater.from(context);
		this.datas = datas;
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
			convertView = layoutInflater.inflate(R.layout.points_tab1_list_item, null);//

			holder = new ViewHolder();

			holder.tv_name = (TextView) convertView.findViewById(R.id.item_tv_name);
			holder.tv_score = (TextView) convertView.findViewById(R.id.item_tv_score);
			holder.btn_exchange = (Button) convertView.findViewById(R.id.item_btn_exchange);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_name.setText(datas.get(position).getName());
		holder.tv_score.setText(datas.get(position).getScore());
		holder.btn_exchange.setOnClickListener(new OnClickListener() {
			@Override
            public void onClick(View v) {
				postExchange();
			}
		});

		return convertView;
	}

	private static class ViewHolder {

		TextView tv_name; 	//名称
		TextView tv_score; 	//所需积分
		Button btn_exchange;//兑换按钮
	}


	/**
     * 积分兑换接口
     *
     */
    private void postExchange() {

        if (!NetworkUtils.isNetworkConnected(context)) {
            Toast.makeText(context, context.getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", DBHelper.getUser(context).getId());	//
        map.put("exchange_id", "0"); 		// 兑换物品ID , 默认为0
        AjaxParams param = new AjaxParams(map);

        showDialog();
        new FinalHttp().post("wwwwwwwwwwwwwwwwww", param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(context, context.getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                dismissDialog();
                LogOut.i("========", "onSuccess：" + t);
                JSONObject json;
                try {
                    json = new JSONObject(t.toString());
                    int status = Integer.parseInt(json.getString("status"));
                    String msg = json.getString("msg");

//                    if (status == Constant.STATUS_SUCCESS) { // 正确
//                    	parseJson(json);
//                    } else if (status == Constant.STATUS_SERVER_ERROR) { // 服务器错误
//                        Toast.makeText(context, context.getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
//                    } else if (status == Constant.STATUS_PARAM_MISS) { // 缺失必选参数
//                        Toast.makeText(context, context.getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
//                    } else if (status == Constant.STATUS_PARAM_ILLEGA) { // 参数值非法
//                        Toast.makeText(context, context.getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
//                    } else if (status == Constant.STATUS_OTHER_ERROR) { // 999其他错误
//                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(context, context.getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
//                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(context, context.getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 解析接口的json数据
     *
     * @param json
     */
    private void parseJson(JSONObject json) {
        Toast.makeText(context, "兑换成功！", 1).show();
    }
    
    private ProgressDialog m_pDialog;
	public void showDialog() {
		if(m_pDialog == null){
			m_pDialog = new ProgressDialog(context);
			m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			m_pDialog.setMessage("请稍等...");
			m_pDialog.setIndeterminate(false);
			m_pDialog.setCancelable(false);
		}
		m_pDialog.show();
	}

	public void dismissDialog() {
		if (m_pDialog != null && m_pDialog.isShowing()) {
			m_pDialog.hide();
		}
	}

}