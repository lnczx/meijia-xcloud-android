package com.meijialife.simi.activity;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.alipay.ConsAli;
import com.meijialife.simi.alipay.OnAlipayCallback;
import com.meijialife.simi.alipay.PayWithAlipay;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;
import com.meijialife.simi.wxpay.WxPay;

/**
 * 支付
 */
public class PayOrderActivity extends BaseActivity implements OnClickListener {
    private String card_id; // 会员卡类型
    private int from;
    public static final int FROM_MEMBER = 1; // 来自会员卡页面
    public static final int FROM_MISHU = 2; // 来自秘书页面

    private ImageView iv_order_select_alipay;
    private ImageView iv_order_select_weixin;
    /** 支付类型 **/
    private static final int PAY_TYPE_ALIPAY = 1; // 支付宝支付
    private static final int PAY_TYPE_WXPAY = 2; // 微信支付

    private String reMoney; // 充值金额

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_pay_activity);
        super.onCreate(savedInstanceState);

        init();

    }

    private void init() {
        requestBackBtn();
        setTitleName("订单支付");

        from = getIntent().getIntExtra("from", 0);

        if (from == FROM_MEMBER) {
            name = getIntent().getStringExtra("name");
            card_pay = getIntent().getStringExtra("card_pay");
            card_value = getIntent().getStringExtra("card_value");
            card_id = getIntent().getStringExtra("card_id");
        } else if (from == FROM_MISHU) {
            name = getIntent().getStringExtra("name");
            senior_pay = getIntent().getStringExtra("senior_pay");
          senior_type_id = getIntent().getStringExtra("senior_type_id");
          sec_id = getIntent().getStringExtra("sec_id");
        }

        findViewById(R.id.recharge_ll_wxpay).setOnClickListener(this);
        findViewById(R.id.recharge_ll_alipay).setOnClickListener(this);
        findViewById(R.id.btn_topay).setOnClickListener(this);
        iv_order_select_alipay = (ImageView) findViewById(R.id.iv_order_select_alipay);
        iv_order_select_weixin = (ImageView) findViewById(R.id.iv_order_select_weixin);

        TextView tv_pay_name = (TextView) findViewById(R.id.tv_pay_name);
        TextView tv_pay_money = (TextView) findViewById(R.id.tv_pay_money);

        if (from == FROM_MEMBER) {
            tv_pay_name.setText(name);
            tv_pay_money.setText(card_pay + "元");
        } else if (from == FROM_MISHU) {
            tv_pay_name.setText(name);
            tv_pay_money.setText(senior_pay + "元");
        }

        RelativeLayout layout_quan = (RelativeLayout) findViewById(R.id.layout_quan);
        layout_quan.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(PayOrderActivity.this, DiscountCardActivity.class));

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.recharge_ll_alipay:
            iv_order_select_alipay.setSelected(true);
            iv_order_select_weixin.setSelected(false);
            break;
        case R.id.recharge_ll_wxpay:
            iv_order_select_alipay.setSelected(false);
            iv_order_select_weixin.setSelected(true);
            break;
        case R.id.btn_topay:

            if (iv_order_select_alipay.isSelected() && from == FROM_MEMBER) {
                postCardBuy(PAY_TYPE_ALIPAY);
            } else if (iv_order_select_alipay.isSelected() && from == FROM_MISHU) {//支付宝支付秘书服务
                postSeniorBuy(PAY_TYPE_ALIPAY);
            } else if (iv_order_select_weixin.isSelected() && from == FROM_MEMBER) {
                postCardBuy(PAY_TYPE_WXPAY);
            } else if (iv_order_select_weixin.isSelected() && from == FROM_MISHU) {//微信支付秘书服务
                postSeniorBuy(PAY_TYPE_WXPAY);
            } else {
                Toast.makeText(this, "请选择支付方式", 0).show();
            }

            break;

        default:
            break;
        }

    }

    /**
     * 秘书服务购买，支付宝回调
     */
    OnAlipayCallback guanjiaCallback = new OnAlipayCallback() {

        public void onAlipayCallback(Activity activity, Context context, boolean isSucceed, String msg) {
            /** 支付宝回调位置 **/
            if (isSucceed) {
                // 支付成功
                // LogOut.i("======", "onAlipayCallback \n msg：" + msg);
                String tradeNo = msg;
                // Toast.makeText(getApplication(), "支付成功！", 1).show();
                postSeniorOnlinePay(activity, context, tradeNo);
            } else {
                Toast.makeText(getApplication(), msg, 1).show();
            }
        }
    };

    /**
     * 会员充值，支付宝回调
     */
    OnAlipayCallback memberCallback = new OnAlipayCallback() {

        public void onAlipayCallback(Activity activity, Context context, boolean isSucceed, String msg) {
            if (isSucceed) {
                String tradeNo = msg;
                postCardOnlinePay(activity, context, tradeNo, "1", "TRADE_SUCCESS ");
            } else {
                Toast.makeText(context, msg, 1).show();
            }
        }
    };
    private String name;
    private String senior_pay;
    private String card_pay;
    private String card_value;

    /**
     * 私密卡购买接口
     * 
     * @param payType
     *            //支付类型 0 = 微信支付 1 = 支付宝
     */
    private void postSeniorBuy(final int payType) {

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", DBHelper.getUserInfo(this).getUser_id());  
        map.put("sec_id", sec_id); // 秘书id
        map.put("senior_type_id", senior_type_id); // 秘书卡服务id
        map.put("pay_type", "" + payType); 
        AjaxParams param = new AjaxParams(map);

        new FinalHttp().post(Constants.URL_POST_SENIOR_BUY, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                disDialog();
                Toast.makeText(PayOrderActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                disDialog();
                LogOut.i("========", "onSuccess：" + t);
                JSONObject json;
                try {
                    json = new JSONObject(t.toString());
                    int status = Integer.parseInt(json.getString("status"));
                    String msg = json.getString("msg");

                    if (status == Constants.STATUS_SUCCESS) { // 正确
                        parseSeniorBuyJson(json, payType);
                    } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                        Toast.makeText(PayOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                    } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                        Toast.makeText(PayOrderActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                    } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                        Toast.makeText(PayOrderActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                    } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                        Toast.makeText(PayOrderActivity.this, msg, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(PayOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(PayOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 私密卡购买成功
     * 
     * @param json
     * @param payType
     *            //支付类型 0 = 余额支付 1 = 支付宝
     */
    private void parseSeniorBuyJson(JSONObject json, int payType) {
        String senior_order_no = "";// 管家卡订单号
        try {
            JSONObject obj = json.getJSONObject("data");
            mobile = obj.getString("mobile");
            order_pay = obj.getString("card_pay");
            senior_order_no = obj.getString("senior_order_no");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
        }

        if (payType == PAY_TYPE_ALIPAY) {
            new PayWithAlipay(PayOrderActivity.this, PayOrderActivity.this, guanjiaCallback, mobile,
                    ConsAli.PAY_TO_MS_CARD, order_pay, senior_order_no).pay();
        } else if (payType == PAY_TYPE_WXPAY) {
            new WxPay(PayOrderActivity.this, PayOrderActivity.this,ConsAli.PAY_TO_MS_CARD, senior_order_no, "秘书服务购买", order_pay);
        }
        
    }

    /**
     * 管家卡在线支付成功同步接口
     * 
     */
    public void postSeniorOnlinePay(final Activity activty, final Context context, String tradeNo) {

        if (!NetworkUtils.isNetworkConnected(context)) {
            Toast.makeText(context, context.getString(R.string.net_not_open), 0).show();
            return;
        }

        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", DBHelper.getUserInfo(this).getMobile()); // 手机号
        map.put("senior_order_no", tradeNo); // 订单号
        map.put("pay_type", "1"); // 0 = 余额支付 1 = 支付宝 2 = 微信支付 3 = 智慧支付 4 =
                                  // 上门刷卡（保留，站位）
        map.put("notify_id", "0"); // 通知ID
        map.put("notify_time", date); // 通知时间
        map.put("trade_no", "0"); // 流水号
        map.put("trade_status", "success"); // 支付状态
        AjaxParams param = new AjaxParams(map);

        new FinalHttp().post(Constants.URL_POST_SENIOR_ONLINE, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                disDialog();
                Toast.makeText(context, context.getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                disDialog();
                LogOut.i("========", "onSuccess：" + t);
                JSONObject json;
                try {
                    json = new JSONObject(t.toString());
                    int status = Integer.parseInt(json.getString("status"));
                    String msg = json.getString("msg");

                    if (status == Constants.STATUS_SUCCESS) { // 正确
                        parseSeniorOnlineJson(activty, context, json);
                    } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                        Toast.makeText(context, context.getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                    } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                        Toast.makeText(context, context.getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                    } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                        Toast.makeText(context, context.getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                    } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, context.getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(context, context.getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 解析管家卡在线支付成功同步接口
     * 
     * @param json
     */
    public static void parseSeniorOnlineJson(Activity activity, Context context, JSONObject json) {
        Toast.makeText(context, "购买成功！", 1).show();
        updateUserInfo(activity);
    }

    /**
     * 充值卡购买接口
     * 
     */
    private void postCardBuy(final int payType) {
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", DBHelper.getUserInfo(this).getUser_id());
        map.put("card_type", card_id); // 充值卡类型
        map.put("pay_type", payType + ""); // 支付类型 1 = 支付宝
        AjaxParams param = new AjaxParams(map);

        new FinalHttp().post(Constants.URL_POST_CARD_BUY, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                disDialog();
                Toast.makeText(PayOrderActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                disDialog();
                LogOut.i("========", "onSuccess：" + t);
                JSONObject json;
                try {
                    json = new JSONObject(t.toString());
                    int status = Integer.parseInt(json.getString("status"));
                    String msg = json.getString("msg");

                    if (status == Constants.STATUS_SUCCESS) { // 正确
                        parseCardBuyJson(payType, json);
                    } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                        Toast.makeText(PayOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                    } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                        Toast.makeText(PayOrderActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                    } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                        Toast.makeText(PayOrderActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                    } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                        Toast.makeText(PayOrderActivity.this, msg, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(PayOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(PayOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 充值数据拼接
     * 
     * @param json
     */
    private void parseCardBuyJson(int payType, JSONObject json) {
        String card_order_no = "";// 充值卡订单号
        try {
            JSONObject obj = json.getJSONObject("data");
            mobile2 = obj.getString("mobile");
            card_order_no = obj.getString("card_order_no");
            card_pay = obj.getString("card_pay");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
        }
        if (payType == PAY_TYPE_ALIPAY) {
            new PayWithAlipay(PayOrderActivity.this, PayOrderActivity.this, memberCallback, mobile2,
                    ConsAli.PAY_TO_MEMBER, "0.01", card_order_no).pay();
        } else if (payType == PAY_TYPE_WXPAY) {
            new WxPay(PayOrderActivity.this, PayOrderActivity.this,ConsAli.PAY_TO_MEMBER, card_order_no, "云行政会员卡充值",card_pay);
        }
    }

    /**
     * 会员充值在线支付成功同步接口
     * 
     */
    public static void postCardOnlinePay(final Activity activity, final Context context, String tradeNo, String payType, String tradeStatus) {

        if (!NetworkUtils.isNetworkConnected(context)) {
            Toast.makeText(context, context.getString(R.string.net_not_open), 0).show();
            return;
        }

        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", DBHelper.getUserInfo(activity).getUser_id()); // 手机号
        map.put("card_order_no", tradeNo); // 订单号
        map.put("pay_type", payType); // 0 = 余额支付 1 = 支付宝 2 = 微信支付 3 = 智慧支付 4 =
                                      // 上门刷卡（保留，站位）
        map.put("notify_id", "0"); // 通知ID
        map.put("notify_time", date); // 通知时间
        map.put("trade_no", "0"); // 流水号
        map.put("trade_status", tradeStatus); // 支付状态
        AjaxParams param = new AjaxParams(map);

        new FinalHttp().post(Constants.URL_POST_CARD_ONLINE, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                disDialog();
                Toast.makeText(context, context.getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                disDialog();
                LogOut.i("========", "onSuccess：" + t);
                JSONObject json;
                try {
                    json = new JSONObject(t.toString());
                    int status = Integer.parseInt(json.getString("status"));
                    String msg = json.getString("msg");

                    if (status == Constants.STATUS_SUCCESS) { // 正确
                        parseCardOnlineJson(activity, context, json);
                    } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                        Toast.makeText(context, context.getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                    } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                        Toast.makeText(context, context.getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                    } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                        Toast.makeText(context, context.getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                    } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, context.getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(context, context.getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 解析会员充值在线支付成功同步接口
     * 
     * @param json
     */
    private static void parseCardOnlineJson(Activity activity, Context context, JSONObject json) {
        Toast.makeText(context, "购买成功！", 1).show();
        updateUserInfo(activity);
        
    }
    
    /**
     * 获取用户详情接口
     */
    private static void updateUserInfo(final Activity activity) {
        

        if (!NetworkUtils.isNetworkConnected(activity)) {
            Toast.makeText(activity, activity.getString(R.string.net_not_open), 0).show();
            return;
        }

        User user = DBHelper.getUser(activity);
        if(null==user){
            return;
        }
        
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user.getId());
        AjaxParams param = new AjaxParams(map);

        showDialog(activity);
        new FinalHttp().get(Constants.URL_GET_USER_INFO, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                disDialog();
                Toast.makeText(activity, activity.getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                disDialog();
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
                                UserInfo  userInfo = gson.fromJson(data, UserInfo.class);
                                DBHelper.updateUserInfo(activity, userInfo);
                                activity.finish();
                            } else {
                                // UIUtils.showToast(PayOrderActivity.this, "数据错误");
                            }
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            errorMsg = activity.getString(R.string.servers_error);
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            errorMsg = activity.getString(R.string.param_missing);
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            errorMsg = activity.getString(R.string.param_illegal);
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            errorMsg = msg;
                        } else {
                            errorMsg = activity.getString(R.string.servers_error);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = activity.getString(R.string.servers_error);

                }
                // 操作失败，显示错误信息|
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    UIUtils.showToast(activity, errorMsg);
                }
            }
        });

    }

    private static ProgressDialog m_pDialog;
    private String senior_type_id;
    private String sec_id;
    private String order_pay;
    private String mobile;
    private String mobile2;

    public static void showDialog(Context context) {
        if (m_pDialog == null) {
            m_pDialog = new ProgressDialog(context);
            m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            m_pDialog.setMessage("请稍等...");
            m_pDialog.setIndeterminate(false);
            m_pDialog.setCancelable(true);
        }
        m_pDialog.show();
    }

    public static void disDialog() {
        if (m_pDialog != null && m_pDialog.isShowing()) {
            m_pDialog.hide();
        }
    }

}
