package com.meijialife.simi.activity;

import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.BasicToolUtil;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * @description：发现--服务详情购买--跳转绑定手机号
 * @author： kerryg
 * @date:2015年11月17日 
 */
public class BindMobileActivity extends BaseActivity implements OnClickListener{

    private EditText et_user, et_pwd,et_name;//手机号+验证码+名字编辑框
   
    private TextView tv_user_tip;
    private LinearLayout login_nocode_tip;
    private TextView tv_nocode_tip;
    private TextView tv_number;
    
    private TextView login_btn;//绑定按钮
    private TextView login_getcode;//获取验证码按钮
    private TextView login_not_get_captcha;//获取失败按钮

    
    private Handler mHandler;
    //实体声明
    private User user;// 登陆成功后的用户数据
    private UserInfo userInfo;// 用户详情数据
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.bind_mobile_layout);
        super.onCreate(savedInstanceState);
        
        initView();
        
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                case 0:
                    if (msg.obj.toString().equalsIgnoreCase("0")) {
                        login_getcode.setText("获取验证码");
                        login_getcode.setClickable(true);
                        login_getcode.setFocusable(true);
                        login_getcode.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_login_getcode));
                    } else {
                        login_getcode.setText("重发验证码" + msg.obj + "s");
                    }
                    break;
                }
            }
        };
    }
    //初始化界面
    public void initView(){
        setTitleName("绑定手机号");
        requestBackBtn();
        
        et_user = (EditText) findViewById(R.id.bind_user_name);
        et_pwd = (EditText) findViewById(R.id.bind_password);
        et_name = (EditText) findViewById(R.id.bind_name);
        
        login_getcode = (TextView) findViewById(R.id.bind_getcode);
        login_not_get_captcha = (TextView) findViewById(R.id.bind_not_get_captcha);
        login_btn = (TextView) findViewById(R.id.bind_btn);
        login_nocode_tip = (LinearLayout) findViewById(R.id.bind_nocode_tip);
        
        tv_nocode_tip = (TextView) findViewById(R.id.tv_nocode_tip);
        tv_number = (TextView) findViewById(R.id.tv_number);

        login_btn.setOnClickListener(this);
        login_getcode.setOnClickListener(this);
        login_not_get_captcha.setOnClickListener(this);    
        
        user = DBHelper.getUser(this);
        et_name.setText(user.getName());
        et_user.setText(user.getMobile());
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.bind_getcode:// 获取验证码
            String reg_phone = et_user.getText().toString().trim();
            if (BasicToolUtil.checkMobileNum(this, reg_phone)) {
                getSmsToken(reg_phone);
            }
            break;
        case R.id.bind_btn: // 登录
            bindMobile();
            break;
        case R.id.bind_not_get_captcha:// 没有收到
            String styledText = "<font color='blue'>" + Constants.SERVICE_NUMBER + "</font>";
            Spanned number = Html.fromHtml("<u>" + styledText + "</u>");
            tv_number.setText(number, TextView.BufferType.SPANNABLE);
            String text = getResources().getString(R.string.login_no_code_tip);
            tv_nocode_tip.setText(text, TextView.BufferType.SPANNABLE);
            if (login_nocode_tip.getVisibility() == View.INVISIBLE) {
                login_nocode_tip.setVisibility(View.VISIBLE);
            }
            tv_number.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "4001691615"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
            break;
        default:
            break;
        }
    }
    public void bindMobile(){
        final String mobile = et_user.getText().toString().trim();
        String sms_token = et_pwd.getText().toString().trim();
        String name = et_name.getText().toString().trim();
        if (StringUtils.isEmpty(name)) {
            Toast.makeText(this, "名字不能为空", 0).show();
            return;
        }
        if (StringUtils.isEmpty(mobile)) {
            Toast.makeText(this, "手机号不能为空", 0).show();
            return;
        }
        if (StringUtils.isEmpty(sms_token)) {
            Toast.makeText(this, "验证码不能为空", 0).show();
            return;
        }
        showDialog();
        Map<String, String> map = new HashMap<String, String>();
        map.put("mobile", mobile);
        map.put("name", name);
        map.put("sms_token", sms_token);
        map.put("user_id", user.getId());
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().post(Constants.URL_POST_BIND_MOBILE, param, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                LogOut.debug("错误码：" + errorNo);
                dismissDialog();
                Toast.makeText(getApplicationContext(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                dismissDialog();
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            bindSuccess();
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            Toast.makeText(BindMobileActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(BindMobileActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(BindMobileActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(BindMobileActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BindMobileActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    UIUtils.showToast(BindMobileActivity.this, "登录失败,请稍后重试");
                }
            }
        });
    }
    
    private void bindSuccess() {
        Toast.makeText(getApplicationContext(), "绑定成功！", Toast.LENGTH_SHORT).show();
        //绑定成功之后，获得userInfo，更新本地User表和UserInfo表
        getUserInfo();
    }
    private void getUserInfo() {
        if (user == null) {
            Toast.makeText(BindMobileActivity.this, "用户信息错误", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!NetworkUtils.isNetworkConnected(BindMobileActivity.this)) {
            Toast.makeText(BindMobileActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user.getId());
        AjaxParams param = new AjaxParams(map);

        showDialog();
        new FinalHttp().get(Constants.URL_GET_USER_INFO, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(BindMobileActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                userInfo = gson.fromJson(data, UserInfo.class);
                                DBHelper.updateUserInfo(BindMobileActivity.this, userInfo);
                                user.setName(userInfo.getName());
                                user.setMobile(userInfo.getMobile());
                                DBHelper.updateUser(BindMobileActivity.this, user);
                                et_name.setText(user.getName());
                                et_user.setText(user.getMobile());
                                BindMobileActivity.this.finish();
                            } else {
                                // UIUtils.showToast(BindMobileActivity.this, "数据错误");
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
                // 操作失败，显示错误信息|
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    UIUtils.showToast(BindMobileActivity.this, errorMsg);
                }
            }
        });
    }
  
    /**
     * 获取手机验证码
     */
    private void getSmsToken(String mobile) {
        showDialog();

        Map<String, String> map = new HashMap<String, String>();
        map.put("mobile", mobile);
        map.put("sms_type", "0");
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().get(Constants.URL_GET_SMS_TOKEN, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                LogOut.debug("错误码：" + errorNo);
                dismissDialog();
                Toast.makeText(getApplicationContext(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                dismissDialog();
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            login_not_get_captcha.setVisibility(View.VISIBLE);
                            login_getcode.setClickable(false);
                            login_getcode.setFocusable(false);
                            login_getcode.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_login_getcode_over));
                            Toast.makeText(getApplicationContext(), "验证码获取成功", 0).show();
                            new Thread() {
                                int a = 60;
                                @Override
                                public void run() {
                                    while (a > 0) {
                                        try {
                                            sleep(1000);
                                            a--;
                                            Message msg = mHandler.obtainMessage(0, a);
                                            mHandler.sendMessage(msg);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                            }.start();

                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            Toast.makeText(BindMobileActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(BindMobileActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(BindMobileActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(BindMobileActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BindMobileActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(BindMobileActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    };
}
