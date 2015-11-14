package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.bean.MyOrder;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * 订单详情
 *
 */
public class OrderDetailsActivity extends BaseActivity implements OnClickListener {
    
    private String order_id;
    private User user;
	private MyOrderDetail myOrderDetail;
	private FinalBitmap finalBitmap;
	
	
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
	
	private MyOrder myOrder;
	
	
	
	
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.order_details_activity);
        super.onCreate(savedInstanceState);
        
        initView();

    }

    private void initView() {
    	setTitleName("订单详情");
    	requestBackBtn();
    	
    	finalBitmap = FinalBitmap.create(this);
    	defaultBitmap = (BitmapDrawable)getResources().getDrawable(R.drawable.order_icon);
    	mOrderNo = (TextView)findViewById(R.id.item_tv_order_no);
    	mOrderName =(TextView)findViewById(R.id.item_tv_order_name);
    	mOrderDate =(TextView)findViewById(R.id.item_tv_date);
    	mOrderStatus =(TextView)findViewById(R.id.item_tv_status);
        mOrderMoney =(TextView)findViewById(R.id.item_tv_order_money);
        mOrderPayType =(TextView)findViewById(R.id.item_tv_pay_type);
        mRemarks =(TextView)findViewById(R.id.itemt_tv_remarks);
        mContent =(TextView)findViewById(R.id.item_tv_order_content);
        mHeadImage = (ImageView)findViewById(R.id.item_tv_icon);
    	
    	user = DBHelper.getUser(this);
    	myOrder = (MyOrder)getIntent().getSerializableExtra("myOrder");
    	order_id =String.valueOf(myOrder.getOrder_id());
    	
    	
    	getOrderDetail();
    }
    
    public void getOrderDetail(){
        
        //判断是否有网络
        if (!NetworkUtils.isNetworkConnected(OrderDetailsActivity.this)) {
            Toast.makeText(OrderDetailsActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String,String> map = new HashMap<String,String>();
        map.put("user_id",user.getId());
        map.put("order_id",""+order_id);
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
                                myOrderDetail = gson.fromJson(data, MyOrderDetail.class);
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
    public void showMyOrderDetail(MyOrderDetail myOrderDetail){
        mOrderNo.setText(myOrderDetail.getOrder_no().trim());
        mContent.setText(myOrderDetail.getService_content());
        mOrderName.setText(myOrderDetail.getPartner_user_name());
        mOrderDate.setText(myOrderDetail.getAdd_time_str().trim());
        mOrderStatus.setText(myOrderDetail.getOrder_status_name().trim());
        mOrderMoney.setText(myOrderDetail.getOrder_money().trim()+"元");
        mRemarks.setText(myOrderDetail.getRemarks().trim());
        mOrderPayType.setText(myOrderDetail.getPay_type_name().trim());
        finalBitmap.display(mHeadImage,myOrderDetail.getPartner_user_head_img(),defaultBitmap.getBitmap(),defaultBitmap.getBitmap());
        
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//        case R.id.:	//
//        	Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
//            break;
         
        default:
            break;
        }
    }
}
