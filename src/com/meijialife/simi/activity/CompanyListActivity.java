package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewDebug.FlagToString;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.CompanyListAdapter;
import com.meijialife.simi.bean.CompanyData;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * @description：展示所属企业列表信息
 * @author： kerryg
 * @date:2015年12月5日 
 */
public class CompanyListActivity extends BaseActivity {
    
    
    private ListView listview;
    private CompanyListAdapter companyListAdapter;
    private List<CompanyData> companyDataList;
    public static CompanyListActivity instance =null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_company_list);
        super.onCreate(savedInstanceState);
        instance = this;
        initView();
    }
    
    private void initView(){
        
        requestBackBtn();
        setTitleName("公司列表");
        
        listview = (ListView)findViewById(R.id.listview);
      
        companyListAdapter = new CompanyListAdapter(this);
        listview.setAdapter(companyListAdapter);
        
        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CompanyData companyData = companyDataList.get(position);
                Intent intent = new Intent(CompanyListActivity.this,StaffListActivity.class);
                intent.putExtra("company_id",companyData.getCompany_id());
                intent.putExtra("company_name",companyData.getCompany_name());
                intent.putExtra("flag",getIntent().getIntExtra("flag",0));
                startActivity(intent);
            }
        });
        getCompanyListByUserId();
        
    }
    /**
     * 获取用户所属企业列表
     */
    private void getCompanyListByUserId() {
        String user_id = DBHelper.getUser(this).getId();
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id+"");
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().get(Constants.URL_GET_COMPANY_LIST, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(CompanyListActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                            if(StringUtils.isNotEmpty(data)){
                                Gson gson = new Gson();
                                companyDataList = gson.fromJson(data, new TypeToken<ArrayList<CompanyData>>() {
                                }.getType());
                                companyListAdapter.setData(companyDataList);
                            }else{
                                companyListAdapter.setData(new ArrayList<CompanyData>());
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
                    UIUtils.showToast(CompanyListActivity.this, errorMsg);
                }
            }
        });

    }
}
