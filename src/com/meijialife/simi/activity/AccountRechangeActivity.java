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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.AccountRechangeAdapter;
import com.meijialife.simi.bean.RechangeList;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * 账户充值
 * 
 */
public class AccountRechangeActivity extends BaseActivity {

    private AccountRechangeAdapter adapter;
    private ListView listview;
    private TextView tv_money;
    private TextView tv_charge;//充值按钮
    private EditText et_value;//输入金额
    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_account_rechange_activity);
        super.onCreate(savedInstanceState);

        initView();

    }

    private void initView() {
        setTitleName("充值");
        requestBackBtn();
        Constants.USER_CHARGE_TYPE=99;
        tv_money = (TextView) findViewById(R.id.tv_money);
        listview = (ListView) findViewById(R.id.listview);
        tv_charge = (TextView)findViewById(R.id.tv_charge);
        et_value = (EditText)findViewById(R.id.et_value);
        adapter = new AccountRechangeAdapter(this);

        getRechangeList();

         userInfo = DBHelper.getUserInfo(this);
        if (null != userInfo) {
            tv_money.setText(userInfo.getRest_money());
        }
        
        tv_charge.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String money = et_value.getText().toString().trim();
                if(StringUtils.isEmpty(money)){
                    Toast.makeText(AccountRechangeActivity.this, "充值金额不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                double charge_money = new Double(money).doubleValue();
                if(charge_money<0.01){
                    Toast.makeText(AccountRechangeActivity.this, "充值金额不能小于0.01元",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(AccountRechangeActivity.this, PayOrderActivity.class);
                intent.putExtra("from",PayOrderActivity.FROM_MEMBER);
                intent.putExtra("name", "充值");
                intent.putExtra("card_pay", money);
                intent.putExtra("card_value", money);
                intent.putExtra("card_id", "0");
                startActivity(intent);
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        if(null!=userInfo){
            tv_money.setText(userInfo.getRest_money());
        }
    }
    /**
     * 获取充值卡列表
     */
    public void getRechangeList() {

        if (!NetworkUtils.isNetworkConnected(AccountRechangeActivity.this)) {
            Toast.makeText(AccountRechangeActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        AjaxParams param = new AjaxParams(map);

        showDialog();
        new FinalHttp().get(Constants.URL_GET_CARDS, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(AccountRechangeActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                            if (StringUtils.isNotEmpty(data)) {
                                Gson gson = new Gson();
                                ArrayList<RechangeList> secData = gson.fromJson(data, new TypeToken<ArrayList<RechangeList>>() {
                                }.getType());
                                adapter.setData(secData);
                                listview.setAdapter(adapter);
                            } else {
                                adapter.setData(new ArrayList<RechangeList>());
                                listview.setAdapter(adapter);
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
                    UIUtils.showToast(AccountRechangeActivity.this, errorMsg);
                }
            }
        });

    }

}
