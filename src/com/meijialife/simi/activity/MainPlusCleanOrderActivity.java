package com.meijialife.simi.activity;

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

import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.ui.ToggleButton;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * @description：加号---保洁--下单
 * @author： kerryg
 * @date:2016年3月4日 
 */
public class MainPlusCleanOrderActivity extends BaseActivity implements OnClickListener {
 
  
    private ToggleButton slipBtn_mishuchuli, slipBtn_fatongzhi;
  
    private UserInfo userInfo;
    
    private TextView mCleanAddr;//送水地址
    private EditText mCleanLinkMan;//联系人
    private EditText mCleanLinkTel;//联系电话
    private TextView mCleanRemark;//备注
    private TextView mCleanBandName;
    
    private String mLinkMan;
    private String mLinkTel;//
    private String mRemark;
    private String mServiceAddrId;
    private String mServiceAddrName;
    
    private String cleanTypeName;
    private String cleanTypeId ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_main_plus_clean);
        super.onCreate(savedInstanceState);
        userInfo = DBHelper.getUserInfo(this);
        initView();

    }

    private void initView() {
        
        requestBackBtn();
        setTitleName("下单");
        
        mCleanAddr = (TextView)findViewById(R.id.m_tv_clean_addr);
        mCleanLinkMan = (EditText)findViewById(R.id.m_et_clean_link_man);
        mCleanLinkTel = (EditText)findViewById(R.id.m_et_clean_link_tel);
        mCleanRemark = (TextView)findViewById(R.id.m_tv_clean_remark);
        mCleanBandName=(TextView)findViewById(R.id.m_tv_band_title);
        
        
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
            cleanTypeId= data.getExtras().getString("cleanTypeId");
            cleanTypeName= data.getExtras().getString("cleanTypeName");
            Constants.WATER_TYPE_NAME = cleanTypeName;
            break;
        default:
            break;
        }
    }

    /**
     * 保洁下单接口
     */
    private void postWasterAdd() {
        showDialog();
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", userInfo.getUser_id());
        map.put("company_name",userInfo.getCompany_name());
        map.put("addr_id", mServiceAddrId);
        map.put("clean_type",cleanTypeId);//0=日常办公垃圾 1=废旧电器 2=硒鼓墨盒 3=其他
        map.put("link_man", mLinkMan);
        map.put("link_tel", mLinkTel);
        map.put("remarks", Constants.WATER_ADD_REMARK);

        AjaxParams param = new AjaxParams(map);
        new FinalHttp().post(Constants.POST_CLEAN_ORDER_URL, param, new AjaxCallBack<Object>() {

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
                            Toast.makeText(MainPlusCleanOrderActivity.this, "下单成功了", Toast.LENGTH_SHORT).show();
                            MainPlusCleanOrderActivity.this.finish();
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            Toast.makeText(MainPlusCleanOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(MainPlusCleanOrderActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(MainPlusCleanOrderActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(MainPlusCleanOrderActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainPlusCleanOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainPlusCleanOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constants.WATER_ADD_REMARK="";
        Constants.WATER_ADD_ADDRESS="";
        Constants.WATER_TYPE_NAME="";
      
    }
    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
        case R.id.layout_select_band:
            intent.putExtra("flag","99");
            intent.setClass(MainPlusCleanOrderActivity.this, MainPlusCleanTypeActivity.class);
            startActivityForResult(intent, 1);
            break;
        case R.id.m_rl_remark:
            Intent intent2 = new Intent(MainPlusCleanOrderActivity.this, MainPlusContentActivity.class);
            intent2.putExtra(Constants.MAIN_PLUS_FLAG, Constants.REMARK);
            startActivity(intent2);
            break;
        case R.id.m_rl_addr:
            intent.putExtra("flag",99);// 1=表示从支付页面进入优惠券列表
            intent.setClass(MainPlusCleanOrderActivity.this, AddressActivity.class);
            startActivityForResult(intent, 1);
            break;
        case R.id.bt_create_water:
            mLinkMan = mCleanLinkMan.getText().toString();
            mLinkTel = mCleanLinkTel.getText().toString();
            mRemark = mCleanRemark.getText().toString();
            if (StringUtils.isEmpty(mLinkMan)) {
                UIUtils.showToast(MainPlusCleanOrderActivity.this, "请输入联系人");
                dismissDialog();
                return;
            }
            if (StringUtils.isEmpty(mLinkTel)) {
                UIUtils.showToast(MainPlusCleanOrderActivity.this, "请输入联系电话");
                return;
            }
            if (StringUtils.isEmpty(mRemark)) {
                UIUtils.showToast(MainPlusCleanOrderActivity.this, "请输入备注");
                return;
            }
            postWasterAdd();                
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
                mCleanRemark.setText(Constants.WATER_ADD_REMARK);
                mCleanAddr.setText(Constants.WATER_ADD_ADDRESS);
                mCleanBandName.setText(Constants.WATER_TYPE_NAME);
            }
        });
    }
}
