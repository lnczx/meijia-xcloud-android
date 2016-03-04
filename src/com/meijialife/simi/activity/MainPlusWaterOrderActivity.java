package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.bean.WaterBand;
import com.meijialife.simi.bean.WaterData;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.ui.ToggleButton;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * @description：加号---送水--下单
 * @author： kerryg
 * @date:2016年3月4日 
 */
public class MainPlusWaterOrderActivity extends BaseActivity implements OnClickListener {
 
  
    private ToggleButton slipBtn_mishuchuli, slipBtn_fatongzhi;
  
    private UserInfo userInfo;
    
    private TextView mWaterAddr;//送水地址
    private EditText mWaterLinkMan;//联系人
    private EditText mWaterLinkTel;//联系电话
    private TextView mWaterRemark;//备注
    private TextView mWaterBandName;
    private TextView mWaterBandMoney;
    
    private TextView mNum;//数量
    
    private String mLinkMan;
    private String mLinkTel;//
    private String mRemark;
    private String mServiceAddrId;
    private String mServiceAddrName;
    
    private int mWaterNum;
    private String waterBandName;
    private String waterBandMoney;
    private String waterBandId ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_main_plus_water);
        super.onCreate(savedInstanceState);
        userInfo = DBHelper.getUserInfo(this);
        initView();

    }

    private void initView() {
        
        requestBackBtn();
        setTitleName("下单");
        
        mWaterAddr = (TextView)findViewById(R.id.m_tv_water_addr);
        mWaterLinkMan = (EditText)findViewById(R.id.m_et_water_link_man);
        mWaterLinkTel = (EditText)findViewById(R.id.m_et_water_link_tel);
        mWaterRemark = (TextView)findViewById(R.id.m_tv_water_remark);
        mNum = (TextView)findViewById(R.id.m_tv_num);
        mWaterBandMoney=(TextView)findViewById(R.id.m_tv_band_dis);
        mWaterBandName=(TextView)findViewById(R.id.m_tv_band_title);
        
        
        findViewById(R.id.m_rl_remark).setOnClickListener(this);
        findViewById(R.id.m_iv_plus).setOnClickListener(this);
        findViewById(R.id.m_iv_min).setOnClickListener(this);
        findViewById(R.id.m_rl_addr).setOnClickListener(this);
        findViewById(R.id.bt_create_water).setOnClickListener(this);
        findViewById(R.id.layout_select_band).setOnClickListener(this);
        
    }

   
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
        case RESULT_OK:
            mServiceAddrId= data.getExtras().getString("addr_id");
            mServiceAddrName = data.getExtras().getString("addr_name");
            Constants.WATER_ADD_ADDRESS = mServiceAddrName;
            break;
        case RESULT_FIRST_USER:
            waterBandId= data.getExtras().getString("waterBandId");
            waterBandName= data.getExtras().getString("waterBandName");
            waterBandMoney= data.getExtras().getString("waterMoney");

            Constants.WATER_BAND_NAME = waterBandName;
            Constants.WATER_BAND_MONEY = waterBandMoney;
            break;
        default:
            break;
        }
    }

    /**
     * 送水下单接口
     */
    private void postWaterAdd() {
        
        showDialog();

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", userInfo.getUser_id());
        map.put("addr_id", mServiceAddrId);
        map.put("service_price_id",waterBandId);
        map.put("service_num",mWaterNum+"");
        map.put("link_man", mLinkMan);
        map.put("link_tel", mLinkTel);
        map.put("remarks", Constants.WATER_ADD_REMARK);

        AjaxParams param = new AjaxParams(map);
        new FinalHttp().post(Constants.URL_POST_ADD_WATER, param, new AjaxCallBack<Object>() {

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
                        if (status == Constants.STATUS_SUCCESS) {
                            Toast.makeText(MainPlusWaterOrderActivity.this, "下单成功了", Toast.LENGTH_SHORT).show();
                            MainPlusWaterOrderActivity.this.finish();
                           /* Gson gson = new Gson();
                            WaterData waterData = new WaterData();
                            waterData = gson.fromJson(data, WaterData.class);
                            Intent intent = new Intent(MainPlusWaterOrderActivity.this,PayOrderActivity.class);
                            intent.putExtra("flag","99");
                            intent.putExtra("waterData",waterData);
                            startActivity(intent);*/
                        
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            Toast.makeText(MainPlusWaterOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(MainPlusWaterOrderActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(MainPlusWaterOrderActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(MainPlusWaterOrderActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainPlusWaterOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainPlusWaterOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constants.WATER_ADD_REMARK="";
        Constants.WATER_ADD_ADDRESS="";
        Constants.WATER_BAND_MONEY="";
        Constants.WATER_BAND_NAME="";
      
    }
    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
        case R.id.layout_select_band:
            intent.putExtra("flag","99");
            intent.setClass(MainPlusWaterOrderActivity.this, MainPlusWaterBandActivity.class);
            startActivityForResult(intent, 1);
            break;
        case R.id.m_rl_remark:
            Intent intent2 = new Intent(MainPlusWaterOrderActivity.this, MainPlusContentActivity.class);
            intent2.putExtra(Constants.MAIN_PLUS_FLAG, Constants.REMARK);
            startActivity(intent2);
            break;
        case R.id.m_iv_plus:
              String num =  mNum.getText().toString().trim();
              mWaterNum = new Integer(num).intValue();
              mWaterNum = mWaterNum+1;
              mNum.setText(mWaterNum+"");
            break;
        case R.id.m_iv_min:
            String num1 =  mNum.getText().toString().trim();
            mWaterNum = new Integer(num1).intValue();
            mWaterNum = mWaterNum-1;
            if(mWaterNum<=0){
                mWaterNum =1;
            }
            mNum.setText(mWaterNum+"");
            break;
        case R.id.m_rl_addr:
            intent.putExtra("flag",99);// 1=表示从支付页面进入优惠券列表
            intent.setClass(MainPlusWaterOrderActivity.this, AddressActivity.class);
            startActivityForResult(intent, 1);
            break;
        case R.id.bt_create_water:
            mLinkMan = mWaterLinkMan.getText().toString();
            mLinkTel = mWaterLinkTel.getText().toString();
            mRemark = mWaterRemark.getText().toString();
            if (StringUtils.isEmpty(mLinkMan)) {
                UIUtils.showToast(MainPlusWaterOrderActivity.this, "请输入联系人");
                dismissDialog();
                return;
            }
            if (StringUtils.isEmpty(mLinkTel)) {
                UIUtils.showToast(MainPlusWaterOrderActivity.this, "请输入联系电话");
                return;
            }
            if (StringUtils.isEmpty(mRemark)) {
                UIUtils.showToast(MainPlusWaterOrderActivity.this, "请输入备注");
                return;
            }
         
            postWaterAdd();                
            break;
        default:
            break;
        }
    }

    @Override
    protected void onResume() {
        super.onRestart();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWaterRemark.setText(Constants.WATER_ADD_REMARK);
                mWaterAddr.setText(Constants.WATER_ADD_ADDRESS);
                mWaterBandMoney.setText(Constants.WATER_BAND_MONEY);
                mWaterBandName.setText(Constants.WATER_BAND_NAME);
            }
        });
    }
  
    
    
}
