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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.bean.Barcode;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;
import com.zbar.lib.CaptureActivity;

/**
 * @description：加号--资产管理---下单
 * @author： kerryg
 * @date:2016年3月4日
 */
public class MainPlusAssetOrderActivity extends BaseActivity implements OnClickListener {

    private final static int SCANNIN_GREQUEST_CODES = 5;
    private final static int REQUEST_CODES = 1;
    private final static int CATEGORY_REQUEST_CODES = 2;//跳转类型
    private final static int CATEGORY_RESULT_CODES = 3;//跳转类型

    // 控件声明---领用登记
    private EditText mAssetUserName;
    private EditText mAssetUserMobile;
    private TextView mAssetRemarks;
    private LinearLayout mAssetCategory;
    
    //变量声明---领用登记
    private String assetUserNamelStr;// 用户名称
    private String assetUserMobileStr;// 用户手机号
    private String assetRemarksStr;// 领用用途

    // 控件声明---入库登记
    private TextView mAssetLevel;// 资产类别
    private EditText mAssetName;// 资产名称
    private EditText mAssetRegisterAmount;// 数量
    private EditText mAssetNum;// 编号
    private EditText mAssetNorm;// 规格
    private EditText mAssetLocation;// 存放位置
    private EditText mAssetUnit;//单价

    // 变量声明----入库登记
    private String assetLevelStr;// 资产级别
    private String assetLevelId;// 资产级别
    private String assetNameStr;// 资产名称
    private String assetAmountStr;// 数量
    private String assetNormStr;// 规格
    private String assetUnitStr;// 规格
    private String assetNumStr;// 编号
    private String assetLocationStr;// 位置
    

    private int flag;// 0==领用登记；1==入库登记
    private LinearLayout asset_consue;// 领用
    private ScrollView asset_register;// 登记
    private Barcode barcodeData;//条形码实体

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_main_plus_asset);
        super.onCreate(savedInstanceState);
        initView();

    }

    private void initView() {

        requestBackBtn();
        flag = getIntent().getIntExtra("flag", 0);

        findViewById();
        setOnClick();

    }

    private void setOnClick() {
        findViewById(R.id.m_ll_asset).setOnClickListener(this);
        findViewById(R.id.bt_create_asset).setOnClickListener(this);
        findViewById(R.id.m_scan_zbar).setOnClickListener(this);
        findViewById(R.id.m_ll_asset_level).setOnClickListener(this);
        findViewById(R.id.m_ll_asset_category).setOnClickListener(this);
        
    }

    private void findViewById() {

        asset_consue = (LinearLayout) findViewById(R.id.asset_consue);
        asset_register = (ScrollView) findViewById(R.id.asset_register);
        if (flag == 0) {
            asset_consue.setVisibility(View.VISIBLE);
            asset_register.setVisibility(View.GONE);
            setTitleName("领用登记");
        } else if (flag == 1) {
            asset_register.setVisibility(View.VISIBLE);
            asset_consue.setVisibility(View.GONE);
            setTitleName("入库登记");
        }
        // 资产领用登记
        mAssetUserName = (EditText) findViewById(R.id.m_asset_user_name);
        mAssetUserMobile = (EditText) findViewById(R.id.m_asset_user_mobile);
        mAssetRemarks = (TextView) findViewById(R.id.tv_beizu_content);

        
        // 资产入库登记
        mAssetLevel = (TextView) findViewById(R.id.m_asset_level);
        mAssetName = (EditText) findViewById(R.id.m_asset_name);
        mAssetRegisterAmount = (EditText) findViewById(R.id.m_asset_register_amount);
        mAssetNum = (EditText) findViewById(R.id.m_asset_num);
        mAssetNorm = (EditText) findViewById(R.id.m_asset_norm);
        mAssetLocation = (EditText) findViewById(R.id.m_asset_location);
        mAssetUnit = (EditText) findViewById(R.id.m_asset_unit);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
        case RESULT_OK:
            if (resultCode == (-1)) {
                Bundle bundle = data.getExtras();
                String result = bundle.getString("result").trim();
                getRecodeBarcode(result);
            }
            break;
        case RESULT_FIRST_USER:
            String typeName = data.getStringExtra("typeName");
            mAssetLevel.setText(typeName);
            assetLevelId = data.getStringExtra("typeId");
            break;
        case CATEGORY_RESULT_CODES://获取领用的资产类型
           
            break;
        default:
            break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constants.WATER_ADD_REMARK = "";
        Constants.ASSET_JSON.clear();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
        case R.id.m_ll_asset:
            Intent intent2 = new Intent(MainPlusAssetOrderActivity.this, MainPlusContentActivity.class);
            intent2.putExtra(Constants.MAIN_PLUS_FLAG, Constants.REMARK);
            startActivity(intent2);
            break;
        case R.id.bt_create_asset:
            if (flag == 1) {//入库登记
                assetLevelStr = mAssetLevel.getText().toString().trim();
                assetNameStr = mAssetName.getText().toString().trim();
                assetAmountStr = mAssetRegisterAmount.getText().toString().trim();
                assetNormStr = mAssetNorm.getText().toString().trim();
                assetNumStr = mAssetNum.getText().toString().trim();
                assetLocationStr = mAssetLocation.getText().toString().trim();
                assetUnitStr = mAssetUnit.getText().toString().trim();

                if (StringUtils.isEmpty(assetLevelStr)) {
                    UIUtils.showToast(MainPlusAssetOrderActivity.this, "请选择资产级别");
                    return;
                }
                if (StringUtils.isEmpty(assetNameStr)) {
                    UIUtils.showToast(MainPlusAssetOrderActivity.this, "请输入资产名称");
                    return;
                }
                if (StringUtils.isEmpty(assetAmountStr)) {
                    UIUtils.showToast(MainPlusAssetOrderActivity.this, "请输入数量");
                    return;
                }
                postAssetIn();
            }else if(flag==0){//领用登记
                
//                CommunitySDK mCommSDK = CommunityFactory.getCommSDK(MainPlusAssetOrderActivity.this);
             // 打开微社区的接口, 参数1为Context类型
//             mCommSDK.openCommunity(MainPlusAssetOrderActivity.this);
                assetUserNamelStr = mAssetUserName.getText().toString().trim();
                assetUserMobileStr = mAssetUserMobile.getText().toString().trim();
               if(Constants.ASSET_JSON !=null && Constants.ASSET_JSON.size()>0){
                   postAssetUse();
               }else {
                   UIUtils.showToast(MainPlusAssetOrderActivity.this, "请选择资产类型");
               }
            }
            break;
        case R.id.m_scan_zbar:
            Intent intents = new Intent();
            intents.setClass(MainPlusAssetOrderActivity.this, CaptureActivity.class);
            intents.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intents, SCANNIN_GREQUEST_CODES);
            break;
        case R.id.m_ll_asset_level:
            intent = new Intent();
            intent.setClass(MainPlusAssetOrderActivity.this, MainPlusAssetTypeActivity.class);
            startActivityForResult(intent, REQUEST_CODES);
            break;
        case R.id.m_ll_asset_category:
            intent = new Intent();
            intent.setClass(MainPlusAssetOrderActivity.this, AssetConsumeActivity.class);
            startActivityForResult(intent, CATEGORY_REQUEST_CODES);
            break;
        default:
            break;
        }
    }

    /**
     * 公司资产登记接口
     */
    private void postAssetIn() {

        UserInfo userInfo = DBHelper.getUserInfo(this);
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", userInfo.getUser_id());
        map.put("company_id", userInfo.getCompany_id());
        map.put("asset_type_id", assetLevelId);
        map.put("name", assetNameStr);
        map.put("total", assetAmountStr);
        map.put("price",assetUnitStr);
        // map.put("imgs", Constants.WATER_ADD_REMARK);

        AjaxParams param = new AjaxParams(map);
        new FinalHttp().post(Constants.POST_ASSET_IN_ORDER_URL, param, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                LogOut.debug("错误码：" + errorNo);
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
                            Toast.makeText(MainPlusAssetOrderActivity.this, "入库登记成功了", Toast.LENGTH_SHORT).show();
                            MainPlusAssetOrderActivity.this.finish();
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            Toast.makeText(MainPlusAssetOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(MainPlusAssetOrderActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(MainPlusAssetOrderActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(MainPlusAssetOrderActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainPlusAssetOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainPlusAssetOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    };
    /**
     * 公司资产领用记录接口
     */
    private void postAssetUse() {
        
        UserInfo userInfo = DBHelper.getUserInfo(this);
        String  mJson = new Gson().toJson(Constants.ASSET_JSON);
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", userInfo.getUser_id());
        map.put("company_id", userInfo.getCompany_id());
        map.put("asset_json", mJson);
        map.put("name", assetUserNamelStr);
        map.put("mobile", assetUserMobileStr);
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().post(Constants.POST_ASSET_IN_ORDER_URL, param, new AjaxCallBack<Object>() {
            
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                LogOut.debug("错误码：" + errorNo);
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
                            Toast.makeText(MainPlusAssetOrderActivity.this, "入库登记成功了", Toast.LENGTH_SHORT).show();
                            MainPlusAssetOrderActivity.this.finish();
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            Toast.makeText(MainPlusAssetOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(MainPlusAssetOrderActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(MainPlusAssetOrderActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(MainPlusAssetOrderActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainPlusAssetOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainPlusAssetOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    };
    /**
     * 条形码获取详细信息接口
     */
    private void getRecodeBarcode(String barcode) {
        UserInfo userInfo = DBHelper.getUserInfo(this);
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", userInfo.getUser_id());
        map.put("company_id", userInfo.getCompany_id());
        map.put("barcode", barcode);
        
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().get(Constants.GET_RECORD_BARCODE_URL, param, new AjaxCallBack<Object>() {
            
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                LogOut.debug("错误码：" + errorNo);
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
                            Gson gson = new Gson();
                            barcodeData =gson.fromJson(data,Barcode.class);
                            showBarInfo(barcodeData);
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            Toast.makeText(MainPlusAssetOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(MainPlusAssetOrderActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(MainPlusAssetOrderActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(MainPlusAssetOrderActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainPlusAssetOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainPlusAssetOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    };

    protected void showBarInfo(Barcode barcodeData) {
        mAssetName.setText(barcodeData.getName());
        mAssetNorm.setText(barcodeData.getUnit());
        mAssetUnit.setText(barcodeData.getPrice());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAssetRemarks.setText(Constants.WATER_ADD_REMARK);
            }
        });
    }
}
