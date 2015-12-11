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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.SecretaryAdapter;
import com.meijialife.simi.bean.Partner;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * @description：发现详情--秘书助理，综合服务，设计策划
 * @author： kerryg
 * @date:2015年11月13日 
 */
public class Find2DetailActivity extends BaseActivity {

    private ListView listview;
    private SecretaryAdapter adapter;//服务商适配器
    private ArrayList<Partner> partnerList; // 所有服务商--秘书列表
    private String service_type_ids;
    private String title_name;
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.find_2_detail_activity);
        super.onCreate(savedInstanceState);
        init();
    }
    
    /*
     * 初始化适配器
     */
    public void init() {
        
        title_name = getIntent().getStringExtra("title_name");
        service_type_ids = getIntent().getStringExtra("service_type_ids");
        requestBackBtn();
        setTitleName(title_name);
        listview = (ListView) findViewById(R.id.partner_list_view);
        adapter = new SecretaryAdapter(this);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Partner partner = partnerList.get(position);
                Intent intent = new Intent(Find2DetailActivity.this,PartnerActivity.class);
                intent.putExtra("Partner",partnerList.get(position));
                startActivity(intent);
            }
        });
        getPartnerList(service_type_ids);
    }
    
    /**
     * 获取对应的服务商列表接口
     * @param service_type_ids
     */
    public void getPartnerList(String service_type_ids) {
        String user_id = DBHelper.getUser(this).getId();
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id + "");
        map.put("page", "0");
        map.put("service_type_ids", service_type_ids);
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().get(Constants.URL_GET_USER_LIST, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(Find2DetailActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                dismissDialog();
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            if (StringUtils.isNotEmpty(data)) {
                                Gson gson = new Gson();
                                partnerList = gson.fromJson(data, new TypeToken<ArrayList<Partner>>() {
                                }.getType());
                                adapter.setData(partnerList);
                            } else {
                                adapter.setData(new ArrayList<Partner>());
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
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    UIUtils.showToast(Find2DetailActivity.this, errorMsg);
                }
            }
        });
    }
}
