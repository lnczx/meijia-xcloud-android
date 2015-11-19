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
import android.widget.LinearLayout;
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
import com.meijialife.simi.bean.AddressData;
import com.meijialife.simi.bean.MyDiscountCard;
import com.meijialife.simi.bean.MyOrder;
import com.meijialife.simi.bean.MyOrderDetail;
import com.meijialife.simi.bean.PartnerDetail;
import com.meijialife.simi.bean.ServiceOrder;
import com.meijialife.simi.bean.ServicePrices;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;
import com.meijialife.simi.wxpay.WxPay;

/**
 * @description：支付： 1、订单列表支付 2、订单详情支付 3、充值支付 4、服务类型详情支付
 * @author： kerryg
 * @date:2015年11月19日
 */
public class PayOrderActivity extends BaseActivity implements OnClickListener {

    /**
     * 开关变量定义
     */
    private int from;
    private int flag;// 1=fromMyOrder,2=fromFind
    public static final int FROM_MEMBER = 1; // 来自会员卡页面
    public static final int FROM_MISHU = 2; // 来自服务页面

    public static final int FROM_MYORDER = 1;// 來自订单中的支付跳转
    public static final int FROM_FIND = 2;// 来自发现中的支付跳转
    public static final int FROM_MYORDER_DETAIL = 3;// 来自订单详情中的支付跳转
    /**
     * 支付类型
     */
    private static final int PAY_TYPE_ALIPAY = 1; // 支付宝支付
    private static final int PAY_TYPE_WXPAY = 2; // 微信支付
    /**
     * 全局对象变量定义
     */
    private PartnerDetail partnerDetail;// 服务详情对象
    private ServicePrices servicePrices;// 服务报价
    private ServiceOrder serviceOrder;// 服务订单
    private MyOrderDetail myOrderDetail;
    private MyOrder myOrder;
    private AddressData addressData;
    private MyDiscountCard myDiscountCard;
    /**
     * 全局基本变量定义
     */
    private String reMoney; // 充值金额
    private int pay_type;// 支付方式
    private String orderId;// 订单Id
    private int is_addr;// 0 = 不需要 1 = 需要
    private String addr_id;// 用户地址Id
    private String user_coupon_id = "0";
    private String orderPay;// 订单实际支付金额
    private Long order_service_type_id;// 订单服务类型
    private String card_id; // 会员卡类型
    /**
     * 布局变量定义
     */
    private TextView tvAddrName;// 显示地址信息
    private TextView tvDiscountCard;// 优惠券信息
    private TextView tvRealPay;// 实际支付金额显示
    private ImageView iv_order_select_alipay;
    private ImageView iv_order_select_weixin;

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
        flag = getIntent().getIntExtra("flag", 2);// 2=发现界面支付
        if (flag == FROM_FIND) {
            if (from == FROM_MEMBER) {
                name = getIntent().getStringExtra("name");
                card_pay = getIntent().getStringExtra("card_pay");
                card_value = getIntent().getStringExtra("card_value");
                card_id = getIntent().getStringExtra("card_id");
            } else if (from == FROM_MISHU) {
                partnerDetail = (PartnerDetail) getIntent().getSerializableExtra("PartnerDetail");
                servicePrices = (ServicePrices) getIntent().getSerializableExtra("servicePrices");
                is_addr = servicePrices.getIs_addr();
                orderPay = String.valueOf(servicePrices.getDis_price());
            }
        } else if (flag == FROM_MYORDER_DETAIL) {
            myOrderDetail = (MyOrderDetail) getIntent().getSerializableExtra("myOrderDetail");
            orderId = myOrderDetail.getOrder_id() + "";
            orderPay = myOrderDetail.getOrder_pay();
        } else if (flag == FROM_MYORDER) {
            myOrder = (MyOrder) getIntent().getSerializableExtra("myOrder");
            orderId = myOrder.getOrder_id() + "";
            orderPay = myOrder.getOrder_pay();
        }

        findViewById(R.id.recharge_ll_wxpay).setOnClickListener(this);
        findViewById(R.id.recharge_ll_alipay).setOnClickListener(this);
        findViewById(R.id.btn_topay).setOnClickListener(this);
        iv_order_select_alipay = (ImageView) findViewById(R.id.iv_order_select_alipay);
        iv_order_select_weixin = (ImageView) findViewById(R.id.iv_order_select_weixin);

        TextView tv_pay_name = (TextView) findViewById(R.id.tv_pay_name);
        TextView tv_pay_money = (TextView) findViewById(R.id.tv_pay_money);

        tvAddrName = (TextView) findViewById(R.id.tv_addr_name);
        tvDiscountCard = (TextView) findViewById(R.id.tv_discount_card);
        tvRealPay = (TextView) findViewById(R.id.tv_real_pay);

        if (flag == FROM_FIND) {
            if (from == FROM_MEMBER) {
                tv_pay_name.setText(name);
                tv_pay_money.setText(card_pay + "元");
                Constants.REAL_PAY_CONTENT = card_pay + "元";
                Constants.DISCOUNT_CARD_CONTENT = "为您节省0元";
                order_service_type_id = 0L;

                tvRealPay.setText(Constants.REAL_PAY_CONTENT);
                tvDiscountCard.setText(Constants.DISCOUNT_CARD_CONTENT);
            } else if (from == FROM_MISHU) {
                tv_pay_name.setText(servicePrices.getName());
                tv_pay_money.setText(servicePrices.getDis_price() + "元");
                Constants.REAL_PAY_CONTENT = servicePrices.getDis_price() + "元";
                Constants.DISCOUNT_CARD_CONTENT = "为您节省0元";
                order_service_type_id = (long) partnerDetail.getService_type_id();// 获得服务类型

                tvRealPay.setText(Constants.REAL_PAY_CONTENT);
                tvDiscountCard.setText(Constants.DISCOUNT_CARD_CONTENT);
            }
        } else if (flag == FROM_MYORDER_DETAIL) {
            tv_pay_name.setText(myOrderDetail.getService_type_name());
            tv_pay_money.setText(myOrderDetail.getOrder_pay() + "元");
            Constants.REAL_PAY_CONTENT = myOrderDetail.getOrder_pay() + "元";
            Constants.DISCOUNT_CARD_CONTENT = "为您节省0元";
            order_service_type_id = myOrderDetail.getService_type_id();

            tvRealPay.setText(Constants.REAL_PAY_CONTENT);
            tvDiscountCard.setText(Constants.DISCOUNT_CARD_CONTENT);
        } else if (flag == FROM_MYORDER) {
            tv_pay_name.setText(myOrder.getService_type_name());
            tv_pay_money.setText(myOrder.getOrder_pay() + "元");
            Constants.REAL_PAY_CONTENT = myOrder.getOrder_pay() + "元";
            Constants.DISCOUNT_CARD_CONTENT = "为您节省0元";
            order_service_type_id = myOrder.getService_type_id();

            tvRealPay.setText(Constants.REAL_PAY_CONTENT);
            tvDiscountCard.setText(Constants.DISCOUNT_CARD_CONTENT);

        }
        // 设置默认选中的支付方式
        iv_order_select_alipay.setSelected(true);
        iv_order_select_weixin.setSelected(false);
        RelativeLayout layout_quan = (RelativeLayout) findViewById(R.id.layout_quan);
        RelativeLayout layout_addr = (RelativeLayout) findViewById(R.id.layout_addr);
        LinearLayout ll_service_type = (LinearLayout) findViewById(R.id.ll_service_type);

        /** 优惠券增加点击时间 **/
        layout_quan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(PayOrderActivity.this, DiscountCardActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        /** 服务地址增加点击事件 **/
        if (is_addr == 1) {
            layout_addr.setVisibility(View.VISIBLE);
            ll_service_type.setVisibility(View.VISIBLE);
        } else {
            layout_addr.setVisibility(View.GONE);
            ll_service_type.setVisibility(View.GONE);
        }
        layout_addr.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(PayOrderActivity.this, AddressActivity.class);
                startActivityForResult(intent, 0);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
        case RESULT_OK:
            addr_id = data.getStringExtra("addr_id"); // data为B中回传的Intent
            addressData = (AddressData) data.getSerializableExtra("addressData");
            Constants.ADDRESS_NAME_CONTENT = addressData.getName() + addressData.getAddr();
            break;
        case RESULT_FIRST_USER:
            myDiscountCard = (MyDiscountCard) data.getSerializableExtra("myDiscountCard");
            user_coupon_id = String.valueOf(myDiscountCard.getId());

            Double orderPays = Double.valueOf(orderPay);
            Double value = Double.valueOf(myDiscountCard.getValue());
            // 1.如果优惠券和订单服务类型一致，则判断金额
            if (order_service_type_id.longValue() == myDiscountCard.getService_type_id().longValue()) {
                // 2.判断优惠券是否满足最大金额消费使用
                if (orderPays > value) {// 如果订单金额>maxValue则可以使用优惠券
                    Constants.REAL_PAY_CONTENT = (orderPays - value) + "元";
                    Constants.DISCOUNT_CARD_CONTENT = "为您节省" + myDiscountCard.getValue() + "元";
                } else {
                    Toast.makeText(PayOrderActivity.this, "订单满" + myDiscountCard.getMax_value() + "元才能使用优惠券", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                Toast.makeText(PayOrderActivity.this, "服务类型不一致，不能使用优惠券", Toast.LENGTH_SHORT).show();
                return;
            }
            break;
        default:
            break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvAddrName.setText(Constants.ADDRESS_NAME_CONTENT);
        tvDiscountCard.setText(Constants.DISCOUNT_CARD_CONTENT);
        tvRealPay.setText(Constants.REAL_PAY_CONTENT);
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
            // 来自发现页面的支付跳转
            if (flag == FROM_FIND) {
                if (iv_order_select_alipay.isSelected() && from == FROM_MEMBER) {
                    postCardBuy(PAY_TYPE_ALIPAY);
                } else if (iv_order_select_alipay.isSelected() && from == FROM_MISHU) {// 支付宝支付秘书服务
                    postSeniorBuy(PAY_TYPE_ALIPAY);
                } else if (iv_order_select_weixin.isSelected() && from == FROM_MEMBER) {
                    postCardBuy(PAY_TYPE_WXPAY);
                } else if (iv_order_select_weixin.isSelected() && from == FROM_MISHU) {// 微信支付秘书服务
                    postSeniorBuy(PAY_TYPE_WXPAY);
                } else {
                    Toast.makeText(this, "请选择支付方式", 0).show();
                }
            } else if (flag == FROM_MYORDER) {// 来自于订单页面支付(待支付支付)
                if (iv_order_select_alipay.isSelected()) {// 支付宝支付秘书服务
                    /*
                     * new PayWithAlipay(PayOrderActivity.this, PayOrderActivity.this, guanjiaCallback, mobile, ConsAli.PAY_TO_MS_CARD, "0.01"
                     * order_pay , myOrder.getOrder_no()).pay();
                     */
                    postExistedOrderBuyFromMyOrderList(PAY_TYPE_ALIPAY, myOrder);
                } else if (iv_order_select_weixin.isSelected()) {// 微信支付秘书服务
                // new WxPay(PayOrderActivity.this, PayOrderActivity.this,/* ConsAli.PAY_TO_MS_CARD */0, myOrder.getOrder_no(), "秘书服务购买", "0.01" /*
                // order_pay */);
                    postExistedOrderBuyFromMyOrderList(PAY_TYPE_WXPAY, myOrder);
                } else {
                    Toast.makeText(this, "请选择支付方式", 0).show();
                }
            } else if (flag == FROM_MYORDER_DETAIL) {// 来自于订单页面支付(待支付支付)
                if (iv_order_select_alipay.isSelected()) {// 支付宝支付秘书服务
                    /*
                     * new PayWithAlipay(PayOrderActivity.this, PayOrderActivity.this, guanjiaCallback, mobile, ConsAli.PAY_TO_MS_CARD, "0.01"
                     * order_pay , myOrderDetail.getOrder_no()).pay();
                     */
                    postExistedOrderBuyFromMyOrderDetail(PAY_TYPE_ALIPAY, myOrderDetail);
                } else if (iv_order_select_weixin.isSelected()) {// 微信支付秘书服务
                    /*
                     * new WxPay(PayOrderActivity.this, PayOrderActivity.this, ConsAli.PAY_TO_MS_CARD 0, myOrderDetail.getOrder_no(), "秘书服务购买", "0.01"
                     * order_pay );
                     */
                    postExistedOrderBuyFromMyOrderDetail(PAY_TYPE_WXPAY, myOrderDetail);
                } else {
                    Toast.makeText(this, "请选择支付方式", 0).show();
                }
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
                String tradeNo = msg;
                Toast.makeText(getApplication(), "支付成功！", 1).show();
                // 支付成功跳转到订单详情页面
               // Intent intent = new Intent(PayOrderActivity.this,MyOrderDetail.class); intent.putExtra("orderId",orderId); startActivity(intent);
                finish();
                // 管家卡在线支付同步接口
                // postSeniorOnlinePay(activity, context, tradeNo);
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
     * 服务订单下单接口
     * 
     * @param payType
     *            //支付类型 0 = 微信支付 1 = 支付宝
     */
    private void postSeniorBuy(final int payType) {

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }
        if (is_addr == 1 && Long.valueOf(addr_id) <= 0) {
            Toast.makeText(this, "请选择服务方式", 0).show();
            return;
        }
        UserInfo userInfo = DBHelper.getUserInfo(this);
        Map<String, String> map = new HashMap<String, String>();
        map.put("partner_user_id", partnerDetail.getUser_id() + "");
        map.put("service_type_id", partnerDetail.getService_type_id() + "");// 服务大类Id
        map.put("service_price_id", servicePrices.getService_price_id() + "");
        map.put("user_id", userInfo.getUser_id());
        map.put("mobile", userInfo.getMobile());
        map.put("pay_type", "" + payType);
        if (Long.valueOf(user_coupon_id) > 0) {
            map.put("user_coupon_id", user_coupon_id);
        }
        if (is_addr == 1) {
            map.put("addr_id", addr_id);
        }
        AjaxParams param = new AjaxParams(map);

        new FinalHttp().post(Constants.URL_POST_PARTNER_SERVICE_BUY, param, new AjaxCallBack<Object>() {
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
     * 服务 购买成功
     * 
     * @param json
     * @param payType
     *            //支付类型 0 = 余额支付 1 = 支付宝
     */
    private void parseSeniorBuyJson(JSONObject json, int payType) {
        String order_no = "";// 管家卡订单号
        try {
            JSONObject obj = json.getJSONObject("data");
            String data = json.getString("data");
            mobile = obj.getString("mobile");
            order_pay = obj.getString("order_pay");
            order_no = obj.getString("order_no");
            orderId = obj.getString("order_id");
            Gson gson = new Gson();
            serviceOrder = gson.fromJson(data, ServiceOrder.class);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
        }

        if (payType == PAY_TYPE_ALIPAY) {
            new PayWithAlipay(PayOrderActivity.this, PayOrderActivity.this, guanjiaCallback, mobile, ConsAli.PAY_TO_MS_CARD, "0.01"/* order_pay */,
                    order_no).pay();
        } else if (payType == PAY_TYPE_WXPAY) {
            new WxPay(PayOrderActivity.this, PayOrderActivity.this,/* ConsAli.PAY_TO_MS_CARD */0, order_no, "秘书服务购买", "0.01" /* order_pay */);
        }
    }

    /**
     * 对于已经存在的待支付订单支付接口(来自订单详情)
     * 
     * @param payType
     */
    private void postExistedOrderBuyFromMyOrderDetail(final int payType, MyOrderDetail myOrderDetail) {

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }
        if (is_addr == 1 && Long.valueOf(addr_id) <= 0) {
            Toast.makeText(this, "请选择服务方式", 0).show();
            return;
        }
        UserInfo userInfo = DBHelper.getUserInfo(this);
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", userInfo.getUser_id());
        map.put("order_id", myOrderDetail.getOrder_id() + "");
        map.put("order_no", myOrderDetail.getOrder_no());
        map.put("pay_type", "" + payType);
        if (Long.valueOf(user_coupon_id) > 0) {
            map.put("user_coupon_id", user_coupon_id);
        }
        if (is_addr == 1) {
            map.put("addr_id", addr_id);
        }

        AjaxParams param = new AjaxParams(map);

        new FinalHttp().post(Constants.URL_POST_EXISTED_PARTNER_SERVICE_BUY, param, new AjaxCallBack<Object>() {
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
     * 对于已经存在的待支付订单支付接口（来自订单列表）
     * 
     * @param payType
     */
    private void postExistedOrderBuyFromMyOrderList(final int payType, MyOrder myOrder) {

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }
        if (is_addr == 1 && Long.valueOf(addr_id) <= 0) {
            Toast.makeText(this, "请选择服务方式", 0).show();
            return;
        }
        UserInfo userInfo = DBHelper.getUserInfo(this);
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", userInfo.getUser_id());
        map.put("order_id", myOrder.getOrder_id() + "");
        map.put("order_no", myOrder.getOrder_no());
        map.put("pay_type", "" + payType);
        if (Long.valueOf(user_coupon_id) > 0) {
            map.put("user_coupon_id", user_coupon_id);
        }
        if (is_addr == 1) {
            map.put("addr_id", addr_id);
        }
        AjaxParams param = new AjaxParams(map);

        new FinalHttp().post(Constants.URL_POST_EXISTED_PARTNER_SERVICE_BUY, param, new AjaxCallBack<Object>() {
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
            new PayWithAlipay(PayOrderActivity.this, PayOrderActivity.this, memberCallback, mobile2, ConsAli.PAY_TO_MEMBER, card_pay, card_order_no)
                    .pay();
        } else if (payType == PAY_TYPE_WXPAY) {
            new WxPay(PayOrderActivity.this, PayOrderActivity.this, ConsAli.PAY_TO_MEMBER, card_order_no, "云行政会员卡充值", card_pay);
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
        if (null == user) {
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
                                UserInfo userInfo = gson.fromJson(data, UserInfo.class);
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
