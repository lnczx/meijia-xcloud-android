package com.meijialife.simi.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.bean.CardExtra;
import com.meijialife.simi.bean.Cards;
import com.meijialife.simi.bean.ReceiverBean;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

@SuppressLint("SimpleDateFormat")
public class CarAlertActivity extends Activity {
    
    private TextView mCarBand;//识别车牌
    private TextView mCarColor;//车颜色
    private TextView mBindUser;//绑定用户
    private TextView mAddTime;//识别时间
    private TextView mCarMoney;//本次缴费
    private TextView mRestMoney;//账户余额
    private TextView mUserType;//账户类型
    private ImageView mCarIcon;//
    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    
    
    private Button bt_car_confirm;
    
    private String mCardId;//卡片Id
    private String mAlertTitle="";
    private String mAlertText ="";
    private String mAlert_date= "";
    private String mAlert_time= "";
    private Date mAlertDate = new Date();
    
    private ReceiverBean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.car_alert_activity);
        super.onCreate(savedInstanceState);
        
        findView();
    }
    
	private void findView(){
        
      /*  mAlertTitle = getIntent().getStringExtra("title");
        mAlertText = getIntent().getStringExtra("text");
        mCardId = getIntent().getStringExtra("card_id");
        mAlertDate = (Date) getIntent().getSerializableExtra("date");*/
        
        bean = (ReceiverBean)getIntent().getSerializableExtra("bean");
        
     /*   mAlert_time = new SimpleDateFormat("HH:mm").format(mAlertDate);
        mAlert_date = new SimpleDateFormat("yyyy-MM-dd").format(mAlertDate);*/

        finalBitmap = FinalBitmap.create(this);
        defDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.ad_loading);
        mCarBand = (TextView)findViewById(R.id.card_band);
        mCarColor = (TextView)findViewById(R.id.car_color);
        mBindUser = (TextView)findViewById(R.id.car_bind_user);
        mAddTime = (TextView)findViewById(R.id.car_add_time);
        mCarMoney = (TextView)findViewById(R.id.car_money);
        mRestMoney = (TextView)findViewById(R.id.car_rest_money);
        mUserType = (TextView)findViewById(R.id.car_user_type);
        mCarIcon = (ImageView)findViewById(R.id.car_icon);
        bt_car_confirm = (Button) findViewById(R.id.bt_car_confirm);
        
        initView();
    }
    
    private void initView(){
        mCarBand.setText(bean.getCar_no());
        mCarColor.setText(bean.getCar_color());
        mBindUser.setText(bean.getMobile());
        mAddTime.setText(bean.getOcx_time());
        mCarMoney.setText(bean.getOrder_money());
        mRestMoney.setText(bean.getRest_money());
        mUserType.setText(bean.getOrder_type());
        finalBitmap.display(mCarIcon,bean.getCap_img(),defDrawable.getBitmap(), defDrawable.getBitmap());

      /*  bt_alert_done.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/
        bt_car_confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               
            }
        });
    }
   
    private ProgressDialog m_pDialog;
    public void showDialog() {
        if(m_pDialog == null){
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
            m_pDialog.dismiss();
            m_pDialog = null;
        }
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        dismissDialog();
    
    }
}
