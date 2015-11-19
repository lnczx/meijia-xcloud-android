package com.meijialife.simi.activity;

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
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
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

/**
 * @description：免费资源--支付页面
 * @author： kerryg
 * @date:2015年11月17日 
 */
public class PayZeroOrderActivity extends BaseActivity implements OnClickListener {
    private String card_id; // 会员卡类型
    private int from;
    private int flag;//1=fromMyOrder,2=fromFind
    public static final int FROM_MEMBER = 1; // 来自会员卡页面
    public static final int FROM_MISHU = 2; // 来自秘书页面
    
    public static final int FROM_MYORDER =1;//來自订单中的支付跳转
    public static final int FROM_FIND = 2;//来自发现中的支付跳转
    public static final int FROM_MYORDER_DETAIL = 3;//来自订单的支付跳转


    private ImageView iv_order_select_alipay;
    private ImageView iv_order_select_weixin;
    /** 支付类型 **/
    private static final int PAY_TYPE_ALIPAY = 1; // 支付宝支付
    private static final int PAY_TYPE_WXPAY = 2; // 微信支付
    
    private PartnerDetail partnerDetail;//服务详情对象
    private ServicePrices servicePrices;//服务报价
    
    private MyOrderDetail myOrderDetail;
    private MyOrder myOrder;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_pay_zero_activity);
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        requestBackBtn();
        setTitleName("订单支付");

        from = getIntent().getIntExtra("from", 0);
        flag = getIntent().getIntExtra("flag", 2);//2=发现界面支付
        if(flag == FROM_FIND){
            partnerDetail = (PartnerDetail) getIntent().getSerializableExtra("PartnerDetail");
            servicePrices = (ServicePrices)getIntent().getSerializableExtra("servicePrices");
        }else if(flag == FROM_MYORDER_DETAIL){
            myOrderDetail = (MyOrderDetail) getIntent().getSerializableExtra("myOrderDetail");
            orderId = myOrderDetail.getOrder_id()+"";
        }else if(flag == FROM_MYORDER){
            myOrder = (MyOrder) getIntent().getSerializableExtra("myOrder");
            orderId = myOrder.getOrder_id()+"";
        }
        findViewById(R.id.btn_topay).setOnClickListener(this);
        TextView tv_pay_name = (TextView) findViewById(R.id.tv_pay_name);
        TextView tv_pay_money = (TextView) findViewById(R.id.tv_pay_money);
        if(flag == FROM_FIND){
            tv_pay_name.setText(servicePrices.getName());
            tv_pay_money.setText(servicePrices.getDis_price() + "元");
        }else if (flag==FROM_MYORDER_DETAIL) {
            tv_pay_name.setText(myOrderDetail.getService_type_name());
            tv_pay_money.setText(0.0+ "元");
        }else if (flag==FROM_MYORDER) {
            tv_pay_name.setText(myOrder.getService_type_name());
            tv_pay_money.setText(0.0+ "元");
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_topay:
            //来自发现页面的支付跳转
            postSeniorBuy(PAY_TYPE_ALIPAY);
            break;
        default:
            break;
        }
    }
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
        UserInfo userInfo = DBHelper.getUserInfo(this);
        Map<String, String> map = new HashMap<String, String>();
        map.put("partner_user_id",partnerDetail.getUser_id()+"");
        map.put("service_type_id",partnerDetail.getService_type_id()+"");//服务大类Id
        map.put("service_price_id", servicePrices.getService_price_id()+"");
        map.put("user_id", userInfo.getUser_id());
        map.put("mobile",userInfo.getMobile());
        map.put("pay_type", "" + payType); 
        
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().post(Constants.URL_POST_PARTNER_SERVICE_BUY, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                disDialog();
                Toast.makeText(PayZeroOrderActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(PayZeroOrderActivity.this,"购买成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                        Toast.makeText(PayZeroOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                    } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                        Toast.makeText(PayZeroOrderActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                    } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                        Toast.makeText(PayZeroOrderActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                    } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                        Toast.makeText(PayZeroOrderActivity.this, msg, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(PayZeroOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(PayZeroOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
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
