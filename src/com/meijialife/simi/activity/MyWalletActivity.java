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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.MyWalletAdapter;
import com.meijialife.simi.bean.MyWalletData;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * 我的钱包
 * 
 */
public class MyWalletActivity extends BaseActivity implements OnClickListener {

    private Button btn_recharge;// 充值

    private ListView listview;
    private MyWalletAdapter adapter;
    
    private ArrayList<MyWalletData> myWalletDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.my_wallet_activity);
        super.onCreate(savedInstanceState);

        initView();

    }

    private void initView() {
        setTitleName("我的钱包");
        requestBackBtn();

        btn_recharge = (Button) findViewById(R.id.btn_recharge);
        btn_recharge.setOnClickListener(this);

        listview = (ListView) findViewById(R.id.listview);
        TextView  tv_money = (TextView) findViewById(R.id.tv_money);
        adapter = new MyWalletAdapter(this);
        listview.setAdapter(adapter);
        
        UserInfo userInfo = DBHelper.getUserInfo(MyWalletActivity.this);
        if(null!=userInfo){
            tv_money.setText(userInfo.getRest_money());
        }
        getMyWalletList();
        
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_recharge: // 充值
            startActivity(new Intent(MyWalletActivity.this, AccountRechangeActivity.class));
            break;

        default:
            break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        getMyWalletList();
    }
    public void getMyWalletList(){
        //判断是否有网络
        if (!NetworkUtils.isNetworkConnected(MyWalletActivity.this)) {
            Toast.makeText(MyWalletActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }
        User user = DBHelper.getUser(MyWalletActivity.this);
        Map<String,String> map = new HashMap<String,String>();
        map.put("user_id",user.getId());
        map.put("page","1");
        AjaxParams params = new AjaxParams(map);
        showDialog();
        new FinalHttp().get(Constants.URL_GET_WALLET_LIST, params, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(MyWalletActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                //json字符串转为集合对象
                                myWalletDataList = gson.fromJson(data, new TypeToken<ArrayList<MyWalletData>>() {
                                }.getType());
                                //给适配器赋值
                                adapter.setData(myWalletDataList);
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
                    UIUtils.showToast(MyWalletActivity.this, errorMsg);
                }
            }
        });
    }
    
    
    
}
