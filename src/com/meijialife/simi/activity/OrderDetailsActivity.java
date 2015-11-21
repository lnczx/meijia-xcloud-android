package com.meijialife.simi.activity;

import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
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
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * @description：侧边栏--我的订单--订单详情
 * @author： kerryg
 * @date:2015年11月14日
 */
public class OrderDetailsActivity extends BaseActivity implements OnClickListener {

    // 定义全局变量
    private String order_id;
    private User user;
    private MyOrderDetail myOrderDetail;
    private FinalBitmap finalBitmap;
    private MyOrder myOrder;

    // 布局控件定义
    private TextView mOrderNo;
    private TextView mOrderName;
    private TextView mOrderDate;
    private TextView mOrderStatus;
    private TextView mContent;
    private TextView mOrderMoney;
    private TextView mOrderPayType;
    private TextView mRemarks;
    private BitmapDrawable defaultBitmap;
    private ImageView mHeadImage;
    private TextView tv_city_name;

    
    private String orderStatus;
    private int orderStatusId;//订单状态Id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.order_details_activity);
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setTitleName("订单详情");
        requestBackBtn();
        //初始化布局控件
        finalBitmap = FinalBitmap.create(this);
        defaultBitmap = (BitmapDrawable) getResources().getDrawable(R.drawable.order_icon);
        mOrderNo = (TextView) findViewById(R.id.item_tv_order_no);
        mOrderName = (TextView) findViewById(R.id.item_tv_order_name);
        mOrderDate = (TextView) findViewById(R.id.item_tv_date);
        mOrderStatus = (TextView) findViewById(R.id.item_tv_status);
        mOrderMoney = (TextView) findViewById(R.id.item_tv_order_money);
        mOrderPayType = (TextView) findViewById(R.id.item_tv_pay_type);
        mRemarks = (TextView) findViewById(R.id.itemt_tv_remarks);
        mContent = (TextView) findViewById(R.id.item_tv_order_content);
        mHeadImage = (ImageView) findViewById(R.id.item_tv_icon);
        tv_city_name =(TextView)findViewById(R.id.item_tv_city_name);

       
        //获取Intent中值
        user = DBHelper.getUser(this);
        /*myOrder = (MyOrder) getIntent().getSerializableExtra("myOrder");
        order_id = String.valueOf(myOrder.getOrder_id());*/
        order_id = getIntent().getStringExtra("orderId");
        orderStatusId = getIntent().getIntExtra("orderStatusId",1);
        
        //访问订单详情接口
        getOrderDetail();
    }

    public void getOrderDetail() {
        // 判断是否有网络
        if (!NetworkUtils.isNetworkConnected(OrderDetailsActivity.this)) {
            Toast.makeText(OrderDetailsActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user.getId());
        map.put("order_id", "" + order_id);
        AjaxParams params = new AjaxParams(map);
        showDialog();
        new FinalHttp().get(Constants.URL_GET_ORDER_DETAIL, params, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(OrderDetailsActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                //将json字符串转为订单详情对象
                                myOrderDetail = gson.fromJson(data, MyOrderDetail.class);
                                //展示详情
                                showMyOrderDetail(myOrderDetail);
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
                    UIUtils.showToast(OrderDetailsActivity.this, errorMsg);
                }
            }
        });
    }
    /**
     * 根据接口返回的值，赋值展示订单详情
     * @param myOrderDetail
     */
    public void showMyOrderDetail(final MyOrderDetail myOrderDetail) {
        orderStatus = myOrderDetail.getOrder_status_name();
        mOrderNo.setText(myOrderDetail.getOrder_no().trim());
        mContent.setText(myOrderDetail.getService_content());
        mOrderName.setText(myOrderDetail.getService_type_name().trim());
        mOrderDate.setText(myOrderDetail.getAdd_time_str().trim());
        mOrderStatus.setText(myOrderDetail.getOrder_status_name().trim());
        mOrderMoney.setText(myOrderDetail.getOrder_money().trim() + "元");
        mRemarks.setText(myOrderDetail.getRemarks().trim());
        mOrderPayType.setText(myOrderDetail.getPay_type_name().trim());
        tv_city_name.setText(myOrderDetail.getCity_name());
        finalBitmap.display(mHeadImage, myOrderDetail.getPartner_user_head_img(), defaultBitmap.getBitmap(), defaultBitmap.getBitmap());
        mOrderStatus.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(orderStatusId ==Constants.ORDER_NOT_PAY){
                        mOrderStatus.setClickable(true);
                        Intent intent = new Intent(OrderDetailsActivity.this,PayOrderActivity.class);
                        intent.putExtra("flag",PayOrderActivity.FROM_MYORDER_DETAIL);
                        intent.putExtra("myOrderDetail",myOrderDetail);
                        startActivity(intent);
                    }else {
                        mOrderStatus.setClickable(false);
                    }
                }
            });
        
    }
    @Override
    protected void onResume() {
        super.onResume();
        getOrderDetail();
        if(orderStatusId ==Constants.ORDER_NOT_PAY){
            mOrderStatus.setClickable(true);
        }else {
            mOrderStatus.setClickable(false);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        // case R.id.: //
        // Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        // break;
        default:
            break;
        }
    }
}
