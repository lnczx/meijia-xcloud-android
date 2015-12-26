package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.Calendar;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.easeui.utils.EaseCommonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.MainActivity;
import com.meijialife.simi.R;
import com.meijialife.simi.bean.CalendarMark;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.BasicToolUtil;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;
import com.simi.easemob.EMDemoHelper;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

public class LoginActivity extends BaseActivity implements OnClickListener {

    // 整个平台的Controller, 负责管理整个SDK的配置、操作等处理
    private UMSocialService mController = UMServiceFactory.getUMSocialService(Constants.DESCRIPTOR);

    private EditText et_user, et_pwd;
    private TextView login_btn;
    private ImageView qq_login_btn;
    private ImageView sina_login_btn;
    private ImageView wx_login_btn;
    private String qq_uid;

    private TextView login_getcode;

    private TextView tv_user_tip;

    private TextView login_not_get_captcha;

    private LinearLayout login_nocode_tip;

    private Handler mHandler;

    private User user;// 登陆成功后的用户数据
    private UserInfo userInfo;// 用户详情数据

    private TextView tv_nocode_tip;

    private TextView tv_number;
    public static String clientid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.login_layout);
        super.onCreate(savedInstanceState);
        initView();

        setConfig();// 配置第三方登录

        mHandler = new Handler() {
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

    private void setConfig() {
        // 设置qq的
        String appId = "1104934408";
        String appKey = "bRW2glhUCR6aJYIZ";
        // 添加QQ支持, 并且设置QQ分享内容的target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(LoginActivity.this, appId, appKey);
        qqSsoHandler.addToSocialSDK();

        // 设置新浪SSO handler
        mController.getConfig().setSsoHandler(new SinaSsoHandler());

        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(LoginActivity.this, "wx93aa45d30bf6cba3", "7a4ec42a0c548c6e39ce9ed25cbc6bd7");
        wxHandler.addToSocialSDK();

    }

    private void initView() {

        setTitleName("快速注册与登录");

        et_user = (EditText) findViewById(R.id.login_user_name);
        et_pwd = (EditText) findViewById(R.id.login_password);

        login_getcode = (TextView) findViewById(R.id.login_getcode);
        login_not_get_captcha = (TextView) findViewById(R.id.login_not_get_captcha);
        login_btn = (TextView) findViewById(R.id.login_btn);
        tv_user_tip = (TextView) findViewById(R.id.tv_user_tip);
        tv_nocode_tip = (TextView) findViewById(R.id.tv_nocode_tip);
        tv_number = (TextView) findViewById(R.id.tv_number);
        login_nocode_tip = (LinearLayout) findViewById(R.id.login_nocode_tip);

        qq_login_btn = (ImageView) findViewById(R.id.qq_login_btn);
        sina_login_btn = (ImageView) findViewById(R.id.sina_login_btn);
        wx_login_btn = (ImageView) findViewById(R.id.wx_login_btn);

        login_btn.setOnClickListener(this);
        login_getcode.setOnClickListener(this);
        login_not_get_captcha.setOnClickListener(this);
        tv_user_tip.setOnClickListener(this);

        qq_login_btn.setOnClickListener(this);
        sina_login_btn.setOnClickListener(this);
        wx_login_btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.qq_login_btn:

            login_with_social(SHARE_MEDIA.QQ);
            break;
        case R.id.sina_login_btn:
            login_with_social(SHARE_MEDIA.SINA);
            break;
        case R.id.wx_login_btn:
            login_with_social(SHARE_MEDIA.WEIXIN);
            break;
        case R.id.login_getcode:// 获取验证码
            String reg_phone = et_user.getText().toString().trim();
            if (BasicToolUtil.checkMobileNum(this, reg_phone)) {
                getSmsToken(reg_phone);
            }

            break;
        case R.id.login_btn: // 登录
            login_nomal();
            break;
        case R.id.login_not_get_captcha:// 没有收到
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
        case R.id.tv_user_tip:// 用户协议
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("url", Constants.URL_WEB_AGREE);
            intent.putExtra("title", "用户使用协议");
            startActivity(intent);

            break;
        default:
            break;
        }
    }

    /**
     * 普通登陆
     * 
     * @param name
     * @param pwd
     */
    private void login_nomal() {
        final String mobile = et_user.getText().toString().trim();
        String sms_token = et_pwd.getText().toString().trim();

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
        map.put("sms_token", sms_token);
        map.put("device_type", "android");
        map.put("login_from", "0");
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().post(Constants.URL_LOGIN, param, new AjaxCallBack<Object>() {

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
                LogOut.debug("成功:" + t.toString());

                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            loginSuccess(data);
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            Toast.makeText(LoginActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(LoginActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(LoginActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    UIUtils.showToast(LoginActivity.this, "登录失败,请稍后重试");
                }

            }
        });

    }

    /**
     * 第三方登录
     * 
     * @param uid
     * @param platform
     *            来源
     * @param info
     * 
     *            qq用户信息:{is_yellow_year_vip=0, vip=0, level=0, province=北京, yellow_vip_level=0, is_yellow_vip=0, gender=男, screen_name=清风飘叶, msg=,
     *            profile_image_url=http://q.qlogo.cn/qqapp/1104763123/06183F86F7AA0ABE4314055856835450/100, city=海淀} time: 1441082419979
     */
    private void login_3rd(final String uid, SHARE_MEDIA platform, final Map<String, Object> info) {
        showDialog();

        Map<String, String> map = new HashMap<String, String>();
        if (platform == SHARE_MEDIA.QQ) {
            map.put("openid", uid);
            map.put("3rd_type", "QQ");
            map.put("name", info.get("screen_name").toString());
            map.put("head_img", info.get("profile_image_url").toString());
        } else if (platform == SHARE_MEDIA.SINA) {
            map.put("openid", uid);
            map.put("3rd_type", "SINA");
            map.put("name", info.get("screen_name").toString());
            map.put("head_img", info.get("profile_image_url").toString());
        } else if (platform == SHARE_MEDIA.WEIXIN) {
            map.put("openid", uid);
            map.put("3rd_type", "WEIXIN");
            map.put("name", info.get("nickname").toString());
            map.put("head_img", info.get("headimgurl").toString());
        }

        map.put("device_type", "android");
        map.put("login_from", "0");
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().post(Constants.URL_THIRD_PARTY_LOGIN, param, new AjaxCallBack<Object>() {

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
                LogOut.debug("成功:" + t.toString());

                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            loginSuccess(data);
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            Toast.makeText(LoginActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(LoginActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(LoginActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    UIUtils.showToast(LoginActivity.this, "登录失败,请稍后重试");
                }

            }
        });

    }

    /**
     * 授权。如果授权成功，则获取用户信息
     */
    private void login_with_social(final SHARE_MEDIA platform) {
        mController.doOauthVerify(LoginActivity.this, platform, new UMAuthListener() {

            @Override
            public void onStart(SHARE_MEDIA platform) {
                // Toast.makeText(LoginActivity.this, "start", 0).show();
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
            }

            @Override
            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                // Toast.makeText(LoginActivity.this, "onComplete", 0).show();
                qq_uid = value.getString("uid");

                if (!TextUtils.isEmpty(qq_uid)) {
                    getUserInfo(qq_uid, platform);
                } else {
                    Toast.makeText(LoginActivity.this, "授权失败...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
            }
        });
    }

    /**
     * 获取授权平台的用户信息，并注册新帐号
     */
    private void getUserInfo(final String uid, final SHARE_MEDIA platform) {
        mController.getPlatformInfo(LoginActivity.this, platform, new UMDataListener() {

            @Override
            public void onStart() {

            }

            // 获取到的信息
            // {is_yellow_year_vip=0, vip=0, level=0, province=北京,
            // yellow_vip_level=0, is_yellow_vip=0, gender=男,
            // screen_name=清风飘叶, msg=,
            // profile_image_url=http://q.qlogo.cn/qqapp/1104722481/E14A56D79EFE74A653DB958C5DDA84B0/100,
            // city=海淀}
            @Override
            public void onComplete(int status, Map<String, Object> info) {
                if (status == StatusCode.ST_CODE_SUCCESSED) {
                    LogOut.debug("用户信息:" + info.toString());
                    // Toast.makeText(LoginActivity.this, info.toString(), Toast.LENGTH_LONG).show();

                    login_3rd(uid, platform, info);

                }
            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
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
                LogOut.debug("成功:" + t.toString());

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
                            Toast.makeText(LoginActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(LoginActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(LoginActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                }

            }
        });

    };

    /**
     * 后台接口登陆成功，开始登陆环信
     */
    private void loginSuccess(String data) {
        Gson gson = new Gson();
        user = gson.fromJson(data, User.class);
        DBHelper.updateUser(LoginActivity.this, user);

        // 登录成功
        Toast.makeText(getApplicationContext(), "登录成功！", Toast.LENGTH_SHORT).show();

        // 获取用户详情，之后去登陆环信
        getUserInfo();

        String clientidFromWeb = user.getClient_id();
        if (StringUtils.isEmpty(clientidFromWeb)||!StringUtils.isEquals(clientidFromWeb, clientid)) {
            bind_user(user.getId(), clientid);
        }
    }

    /**
     * 获取用户详情接口
     */
    private void getUserInfo() {
        if (user == null) {
            Toast.makeText(LoginActivity.this, "用户信息错误", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!NetworkUtils.isNetworkConnected(LoginActivity.this)) {
            Toast.makeText(LoginActivity.this, getString(R.string.net_not_open), 0).show();
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
                Toast.makeText(LoginActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                dismissDialog();
                LogOut.i("========", "用户详情 onSuccess：" + t);
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
                                DBHelper.updateUserInfo(LoginActivity.this, userInfo);
                                // 去登陆环信
                                loginIm();
                                updateCalendarMark();// 请求日历数据

                            } else {
                                // UIUtils.showToast(LoginActivity.this, "数据错误");
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
                    UIUtils.showToast(LoginActivity.this, errorMsg);
                }
            }
        });

    }

    /**
     * 登录环信
     * 
     * @param view
     */
    public void loginIm() {
        final String currentUsername = userInfo.getIm_username();
        final String currentPassword = userInfo.getIm_password();

        if (!EaseCommonUtils.isNetWorkConnected(this)) {
            Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(currentUsername)) {
            Toast.makeText(this, R.string.User_name_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(currentPassword)) {
            Toast.makeText(this, R.string.Password_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        showDialog();
        /*
         * progressShow = true; final ProgressDialog pd = new ProgressDialog(EMLoginActivity.this); pd.setCanceledOnTouchOutside(false);
         * pd.setOnCancelListener(new OnCancelListener() {
         * 
         * @Override public void onCancel(DialogInterface dialog) { progressShow = false; } }); pd.setMessage(getString(R.string.Is_landing));
         * pd.show();
         */

        final long start = System.currentTimeMillis();
        // 调用sdk登陆方法登陆聊天服务器
        EMChatManager.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

            @Override
            public void onSuccess() {
                /*
                 * if (!progressShow) { return; }
                 */
                // 登陆成功，保存用户名
                EMDemoHelper.getInstance().setCurrentUserName(currentUsername);
                // 注册群组和联系人监听
                EMDemoHelper.getInstance().registerGroupAndContactListener();

                // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                // ** manually load all local groups and
                EMGroupManager.getInstance().loadAllGroups();
                EMChatManager.getInstance().loadAllConversations();

                // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                // boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(
                // MyApplication.currentUserNick.trim());
                boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(userInfo.getName());
                if (!updatenick) {
                    Log.e("LoginActivity", "update current user nick fail");
                }
                // 异步获取当前用户的昵称和头像(从自己服务器获取，demo使用的一个第三方服务)
                // EMDemoHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();
                String nickName = userInfo.getName();
                String avatar = userInfo.getHead_img();
                if (StringUtils.isEmpty(nickName)) {
                    nickName = userInfo.getMobile();
                }
                if (!StringUtils.isEmpty(nickName)) {
                    EMDemoHelper.getInstance().getUserProfileManager().setCurrentUserNick(nickName);
                }
                if (!StringUtils.isEmpty(avatar)) {
                    EMDemoHelper.getInstance().getUserProfileManager().setCurrentUserAvatar(avatar);
                }

                // 进入主页面
                runOnUiThread(new Runnable() {
                    public void run() {
                        dismissDialog();
//                        Toast.makeText(getApplicationContext(), "环信登录成功！", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(final int code, final String message) {
                /*
                 * if (!progressShow) { return; }
                 */
                runOnUiThread(new Runnable() {
                    public void run() {
                        dismissDialog();
                        Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 一次获取多个月份的首页日历数据，并更新本地数据库存储，用来显示标记圆点
     */
    private void updateCalendarMark() {
        // int year = CalendarUtils.getCurrentYear();
        // int month = CalendarUtils.getCurrentMonth();
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;// 月
        int year = cal.get(Calendar.YEAR); // 年

        getTotalByMonth(year + "", month + "");

      /*  for (int i = 0; i < 8; i++) {
            if (month == 12) {
                month = 1;
                year += 1;
            } else {
                month += 1;
            }
            getTotalByMonth(year + "", month + "");
        }*/
    }

    /**
     * 按月份获取卡片日期分布接口
     * 
     * @param year
     *            年份，格式为 YYYY
     * @param month
     *            月份，格式为 MM
     */
    public void getTotalByMonth(String year, String month) {

        String user_id = DBHelper.getUser(LoginActivity.this).getId();

        if (!NetworkUtils.isNetworkConnected(LoginActivity.this)) {
            Toast.makeText(LoginActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id + "");
        map.put("year", year);
        map.put("month", month);
        AjaxParams param = new AjaxParams(map);

        new FinalHttp().get(Constants.URL_GET_TOTAL_BY_MONTH, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                // Toast.makeText(SplashActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
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
                                ArrayList<CalendarMark> calendarMarks = gson.fromJson(data, new TypeToken<ArrayList<CalendarMark>>() {
                                }.getType());

                                DBHelper db = DBHelper.getInstance(LoginActivity.this);
                                for (int i = 0; i < calendarMarks.size(); i++) {
                                    db.add(calendarMarks.get(i), calendarMarks.get(i).getService_date());
                                }
                            } else {

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
                // if(!StringUtils.isEmpty(errorMsg.trim())){
                // UIUtils.showToast(SplashActivity.this, errorMsg);
                // }
            }
        });

    }

    /**
     * 绑定接口
     * 
     * @param date
     */
    private void bind_user(String user_id, String client_id) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id);
        map.put("device_type", "android");
        map.put("client_id", client_id);
        AjaxParams param = new AjaxParams(map);

        new FinalHttp().post(Constants.URL_POST_PUSH_BIND, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(LoginActivity.this, LoginActivity.this.getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                LogOut.i("========", "onSuccess：" + t);
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
//                            UIUtils.showToast(LoginActivity.this, "推送绑定成功");
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            errorMsg = LoginActivity.this.getString(R.string.servers_error);
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            errorMsg = LoginActivity.this.getString(R.string.param_missing);
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            errorMsg = LoginActivity.this.getString(R.string.param_illegal);
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            errorMsg = msg;
                        } else {
                            errorMsg = LoginActivity.this.getString(R.string.servers_error);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = LoginActivity.this.getString(R.string.servers_error);

                }
                // 操作失败，显示错误信息|
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    UIUtils.showToast(LoginActivity.this, errorMsg);
                }
            }
        });

    }

}
