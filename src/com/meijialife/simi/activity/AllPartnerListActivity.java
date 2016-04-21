package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.FindAllAdapter;
import com.meijialife.simi.bean.FindBean;
import com.meijialife.simi.utils.AssetsDatabaseManager;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * 
 * 
 */
public class AllPartnerListActivity extends BaseActivity {

  
    private ArrayList<FindBean> myFindBeanList;
    
    private FindAllAdapter appToolsAdapter;
    private LinearLayout find_all_list;
    private GridView gv_application1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_all_partner_list);
        super.onCreate(savedInstanceState);

        initView();

    }

    private void initView() {
        setTitleName("服务大厅");
        requestBackBtn();
        
        
        myFindBeanList = new ArrayList<FindBean>();
        appToolsAdapter = new FindAllAdapter(this);

        find_all_list = (LinearLayout)findViewById(R.id.find_all_list);
        gv_application1 = (GridView)findViewById(R.id.gv_application1);
        find_all_list.setVisibility(View.VISIBLE);
        gv_application1.setAdapter(appToolsAdapter);
        setOnClick();
        getFind2List();
        
    }
    
    private void setOnClick(){
        gv_application1.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FindBean findBean = myFindBeanList.get(position);
              
                String title_name = findBean.getTitle().trim();
                String goto_type = findBean.getGoto_type().trim();
                String goto_url = findBean.getGoto_url().trim();
                String service_type_ids = findBean.getService_type_ids().toString().trim();
                if (goto_type.equals("h5")) {
                    Intent intent = new Intent(AllPartnerListActivity.this, WebViewsFindActivity.class);
                    intent.putExtra("url", goto_url);
                    intent.putExtra("title_name", "");
                    intent.putExtra("service_type_ids", "");
                    startActivity(intent);
                } else if (goto_type.equals("app")) {
                    Intent intent = new Intent(AllPartnerListActivity.this, Find2DetailActivity.class);
                    intent.putExtra("service_type_ids", service_type_ids);
                    intent.putExtra("title_name", title_name);
                    startActivity(intent);
                } else if (goto_type.equals("h5+list")) {
                    Intent intent = new Intent(AllPartnerListActivity.this, WebViewsFindActivity.class);
                    intent.putExtra("url", goto_url);
                    intent.putExtra("title_name", title_name);
                    intent.putExtra("service_type_ids", service_type_ids);
                    startActivity(intent);
                }
            }
        });
    }
    /**
     * 展示服务大厅
     * @param channel_id
     * @param page
     */
    public void getFind2List() {
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("channel_id", "99");
        map.put("page", "1");
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().get(Constants.URL_GET_ADS_LIST, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(AllPartnerListActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                myFindBeanList = gson.fromJson(data, new TypeToken<ArrayList<FindBean>>() {
                                }.getType());
                                   appToolsAdapter.setData(myFindBeanList);
                            } else {
                                    appToolsAdapter.setData(new ArrayList<FindBean>());
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
                    UIUtils.showToast(AllPartnerListActivity.this, errorMsg);
                }
            }
        });
    }

}