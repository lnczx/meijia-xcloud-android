package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewDebug.FlagToString;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.ContactsAdapter;
import com.meijialife.simi.bean.Friend;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;
/**
 * @description：企业员工通讯录
 * @author： kerryg
 * @date:2015年12月5日 
 */
public class StaffListActivity extends Activity implements OnClickListener{
    
    private ListView listview;
    private ContactsAdapter adapter;
    private ArrayList<Friend> friendList = new ArrayList<Friend>();
    /**
     * 列表Item显示初始化
     */
    private TextView tv_name;//用户名称
    private TextView tv_mobile;//手机号
    private TextView tv_id;//用户id
    private TextView tv_temp;//存放中间变量
    private CheckBox cb;//选择框
    private TextView header_tv_name;
    private ImageView title_btn_left;
    private TextView title_btn_ok;
    
    /**
     * 获得传递的参数
     */
    private String company_id;
    private String company_name;
    private int flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_staff_list);
        super.onCreate(savedInstanceState);
        
        initView();
    }
    @SuppressLint("UseSparseArrays")
	private void initView(){
        /**
         * 初始化变量
         */
        adapter = new ContactsAdapter(this);
        /**
         * 获取传递参数
         */
        company_id =(String) getIntent().getStringExtra("company_id");
        company_name = (String)getIntent().getStringExtra("company_name");
        flag = getIntent().getIntExtra("flag",0);
        
        title_btn_left = (ImageView) findViewById(R.id.title_btn_left);
        header_tv_name = (TextView) findViewById(R.id.header_tv_name);
        header_tv_name.setText(company_name);
        title_btn_ok = (TextView) findViewById(R.id.title_btn_ok);
        
        title_btn_left.setOnClickListener(this);
        title_btn_ok.setOnClickListener(this);
       
        listview = (ListView)findViewById(R.id.listview);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listview.setAdapter(adapter);
        listview.setItemsCanFocus(false);
        
        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cb = (CheckBox) view.findViewById(R.id.cb_check_box);
                tv_name = (TextView) view.findViewById(R.id.item_tv_name);
                tv_mobile = (TextView)view.findViewById(R.id.item_tv_mobile);
                tv_id = (TextView) view.findViewById(R.id.item_tv_id);
                tv_temp = (TextView)view.findViewById(R.id.item_tv_temp);
                if (cb.isChecked() == false) {
                    cb.setChecked(true);
                    CharSequence num = tv_temp.getText();
                    Constants.finalContactList.add(num.toString());
                } else {
                    cb.setChecked(false);
                    CharSequence num = tv_temp.getText();
                    Constants.finalContactList.remove(num.toString());
                }
            }
        });
        getStaffList(Constants.finalContactList);
    }
    
    /**
     * 获得企业员工列表接口
     */
    public void getStaffList(final ArrayList<String> staffList) {

        String user_id = DBHelper.getUser(StaffListActivity.this).getId();

        if (!NetworkUtils.isNetworkConnected(StaffListActivity.this)) {
            Toast.makeText(StaffListActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id + "");
        map.put("company_id",company_id);
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().get(Constants.URL_GET_STAFF_LIST, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(StaffListActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                friendList = gson.fromJson(data, new TypeToken<ArrayList<Friend>>() {
                                }.getType());
                                adapter.setData(friendList,staffList,flag);
                            } else {
                                adapter.setData(new ArrayList<Friend>(),staffList,flag);
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
                    UIUtils.showToast(StaffListActivity.this, errorMsg);
                }
            }
        });

    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
        case R.id.title_btn_left:
                this.finish();
            break;
        case R.id.title_btn_ok:
            /*intent = new Intent(StaffListActivity.this,ContactChooseActivity.class);
            startActivity(intent);*/
            CompanyListActivity.instance.finish();
            StaffListActivity.this.finish();
            break;
        default:
            break;
        }
    }
    /**
     * 进度条使用
     */
    private ProgressDialog m_pDialog;
    public void showDialog() {
        if (m_pDialog == null) {
            m_pDialog = new ProgressDialog(this);
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
