package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.BaseListActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.SecretaryAdapter;
import com.meijialife.simi.bean.Partner;
import com.meijialife.simi.bean.Secretary;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * 寻找秘书
 * 
 */
public class FindSecretaryActivity extends BaseListActivity implements OnClickListener {

    private ArrayList<Partner> partnerList; // 所有服务商--秘书列表
    private SecretaryAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_contacts_addfirends_list);
        super.onCreate(savedInstanceState);

        init();
        getSecretaryList();
    }
    
    private void init(){
        setTitleName("寻找秘书与助理");
        requestBackBtn();
        
        adapter = new SecretaryAdapter(this);
        setListAdapter(adapter);
    }  

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Partner partner = partnerList.get(position);
        Intent intent = new Intent(FindSecretaryActivity.this,PartnerActivity.class);
        intent.putExtra("Partner",partnerList.get(position));
        startActivity(intent);
        
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.title_btn_left:
            FindSecretaryActivity.this.finish();
            break;

        default:
            break;
        }
    }
    
    /**
     * 获取秘书列表
     */
    public void getSecretaryList() {

        String user_id = DBHelper.getUser(FindSecretaryActivity.this).getId();

        if (!NetworkUtils.isNetworkConnected(FindSecretaryActivity.this)) {
            Toast.makeText(FindSecretaryActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id+"");
        map.put("page", "0");
        map.put("service_type_ids", "75,180");
        AjaxParams param = new AjaxParams(map);

        showDialog();
        new FinalHttp().get(Constants.URL_GET_USER_LIST, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(FindSecretaryActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                partnerList = gson.fromJson(data, new TypeToken<ArrayList<Partner>>() {
                                }.getType());
                                adapter.setData(partnerList);
//                                tv_tips.setVisibility(View.GONE);
                            }else{
                                adapter.setData(new ArrayList<Partner>());
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
                    UIUtils.showToast(FindSecretaryActivity.this, errorMsg);
                }
            }
        });

    }
    
}
