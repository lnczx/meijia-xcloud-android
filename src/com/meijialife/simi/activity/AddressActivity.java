package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.AddressListAdapter;
import com.meijialife.simi.bean.AddressData;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * 常用地址
 *
 */
public class AddressActivity extends BaseActivity implements OnClickListener, OnItemClickListener {
	
	private ListView listview;
    private AddressListAdapter adapter;
    private ArrayList<AddressData> addressList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.address_manage_activity);
        super.onCreate(savedInstanceState);
        
        initView();
    }

    private void initView() {
    	setTitleName("地址管理");
    	requestBackBtn();
    	
    	listview = (ListView)findViewById(R.id.listview);
    	listview.setOnItemClickListener(this);
    	adapter = new AddressListAdapter(this, true);
    	listview.setAdapter(adapter);
    	
    	findViewById(R.id.address_manage_btn_new).setOnClickListener(this);
    }
    
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        
        getAddList();
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.address_manage_btn_new:	//   添加一个新地址
            startActivity(new Intent(AddressActivity.this, AddAddress.class));
            break;
         
        default:
            break;
        }
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		AddressData addressData = (AddressData)addressList.get(position);
		Intent intent = new Intent();
		intent.putExtra("addr_id",addressData.getId());
		intent.putExtra("addressData",addressData);
		setResult(RESULT_OK, intent);
		finish();
	}
	/**
     * 获取地址列表
     */
    private void getAddList() {
        String user_id = DBHelper.getUser(this).getId();

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id+"");
        AjaxParams param = new AjaxParams(map);

        showDialog();
        new FinalHttp().get(Constants.URL_GET_ADDRS, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(AddressActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                addressList = gson.fromJson(data, new TypeToken<ArrayList<AddressData>>() {
                                }.getType());
                                adapter.setData(addressList);
//                                tv_tips.setVisibility(View.GONE);
                            }else{
//                                tv_tips.setVisibility(View.VISIBLE);
                            }
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            errorMsg = getString(R.string.servers_error);
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            errorMsg = getString(R.string.param_missing);
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            errorMsg = getString(R.string.param_illegal);
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            errorMsg = msg;
                        } else {
                            errorMsg = getString(R.string.servers_error);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = getString(R.string.servers_error);

                }
                // 操作失败，显示错误信息
                if(!StringUtils.isEmpty(errorMsg.trim())){
                    UIUtils.showToast(AddressActivity.this, errorMsg);
                }
            }
        });

    }
    
}
