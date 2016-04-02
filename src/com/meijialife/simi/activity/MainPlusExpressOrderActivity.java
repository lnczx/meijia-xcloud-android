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
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.bean.ExpressOutData;
import com.meijialife.simi.bean.ExpressTypeData;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.AssetsDatabaseManager;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.StringUtils;
import com.zbar.lib.CaptureActivity;

/**
 * @description：加号---快递--下单
 * @author： kerryg
 * @date:2016年3月4日
 */
public class MainPlusExpressOrderActivity extends BaseActivity implements OnClickListener {

    private UserInfo userInfo;
    private final static int SCANNIN_GREQUEST_CODES = 5;
    private final static int REQUEST_CODES = 1;


    private EditText mExpressNo;
    private TextView mExpressCompany;
    private EditText mExpressFromAddr;
    private EditText mExpressFromName;
    private EditText mExpressFromTel;
    private EditText mExpressToAddr;
    private EditText mExpressToName;
    private EditText mExpressToTel;
    private TextView mExpressRemarks;
    private LinearLayout mLlExpressCompany;//点击事件选择快递公司

    private RadioGroup mPayType;
    private RadioGroup mExpressType;
    private RadioButton mButtonPay1;
    private RadioButton mButtonPay2;
    private RadioButton mButtonExpress1;
    private RadioButton mButtonExpress2;

    private String mNo;
    private String mCompany;
    private String mFromAddr;
    private String mFromName;
    private String mFromTel;
    private String mToAddr;
    private String mToName;
    private String mToTel;
    private String mRemarks;
    private String payType;// 支付方式
    private String expressType;// 收发件方式

    private ArrayList<ExpressOutData> mExpressOuts;// 外部快递单号查询

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.layout_main_plus_express);
        super.onCreate(savedInstanceState);
        userInfo = DBHelper.getUserInfo(this);
        initView();

    }

    private void initView() {

        requestBackBtn();
        setTitleName("登记");

        mExpressOuts = new ArrayList<ExpressOutData>();
        mExpressNo = (EditText) findViewById(R.id.m_express_no);
        mExpressCompany = (TextView) findViewById(R.id.m_express_company);
        mExpressFromAddr = (EditText) findViewById(R.id.m_express_from_addr);
        mExpressFromName = (EditText) findViewById(R.id.m_express_from_name);
        mExpressFromTel = (EditText) findViewById(R.id.m_express_from_tel);
        mExpressToAddr = (EditText) findViewById(R.id.m_express_to_addr);
        mExpressToName = (EditText) findViewById(R.id.m_express_to_name);
        mExpressToTel = (EditText) findViewById(R.id.m_express_to_tel);
        mExpressRemarks = (TextView) findViewById(R.id.m_tv_express_remark);
        mLlExpressCompany = (LinearLayout) findViewById(R.id.m_ll_express_company);

        mPayType = (RadioGroup) findViewById(R.id.pay_type);
        mExpressType = (RadioGroup) findViewById(R.id.m_express_type);
        mButtonExpress1 = (RadioButton) findViewById(R.id.express_type1);
        mButtonExpress2 = (RadioButton) findViewById(R.id.express_type2);
        mButtonPay1 = (RadioButton) findViewById(R.id.type1);
        mButtonPay2 = (RadioButton) findViewById(R.id.type2);

        mPayType.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == mButtonPay1.getId()) {
                    mButtonPay1.setChecked(true);
                    payType = "0";// 收件
                }
                if (checkedId == mButtonPay2.getId()) {
                    mButtonPay2.setChecked(true);
                    payType = "1";// 寄件
                }
            }
        });
        mExpressType.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == mButtonExpress1.getId()) {
                    mButtonExpress1.setChecked(true);
                    expressType = "0";// 公费
                }
                if (checkedId == mButtonExpress2.getId()) {
                    mButtonExpress2.setChecked(true);
                    expressType = "1";// 自费
                }
            }
        });
        findViewById(R.id.m_rl_remark).setOnClickListener(this);
        findViewById(R.id.bt_create_express).setOnClickListener(this);
        findViewById(R.id.m_scan_zbar).setOnClickListener(this);
        findViewById(R.id.m_ll_express_company).setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
        case RESULT_OK:
            if (resultCode == (-1)) {
                Bundle bundle = data.getExtras();
                String result = bundle.getString("result").trim();
                mExpressNo.setText(result);
                // 设置光标置后
                mExpressNo.setSelection(mExpressNo.getText().length());
                getExpressAdd(result);
            }
            break;
        case RESULT_FIRST_USER:
                String typeId = data.getStringExtra("typeId");
                String typeName = data.getStringExtra("typeName");
                mCompany = typeId;
                mExpressCompany.setText(typeName);
            break;
        default:
            break;
        }
    }

    /**
     * 快递下单接口
     */
    private void postExpressAdd() {
        showDialog();

        User user = DBHelper.getUser(this);
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user.getId() + "");
        map.put("express_no", mNo);
        map.put("express_id", mCompany);// 活动类型：0 = 不限 1=年会 2 = 拓展培训 3 = 聚会沙龙 4 = 度假休闲 5 = 其他、员工生日、 度假休闲、拓展培训、聚会沙龙、其他
        map.put("express_type", expressType);
        map.put("pay_type", payType);
        map.put("from_addr", mFromAddr);
        map.put("from_name", mFromName);
        map.put("from_tel", mFromTel);
        map.put("to_addr", mToAddr);
        map.put("to_name", mToName);
        map.put("to_tel", mToTel);
        map.put("remarks", Constants.WATER_ADD_REMARK);
        // map.put("imgs", Constants.WATER_ADD_REMARK);

        AjaxParams param = new AjaxParams(map);
        new FinalHttp().post(Constants.POST_ADD_EXPRESS_URL, param, new AjaxCallBack<Object>() {

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
                            Toast.makeText(MainPlusExpressOrderActivity.this, "下单成功了", Toast.LENGTH_SHORT).show();
                            MainPlusExpressOrderActivity.this.finish();
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            Toast.makeText(MainPlusExpressOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(MainPlusExpressOrderActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(MainPlusExpressOrderActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(MainPlusExpressOrderActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainPlusExpressOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainPlusExpressOrderActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    };

    /**
     * 对应快递查找服务商接口
     */
    private void getExpressAdd(String num) {
        showDialog();

        User user = DBHelper.getUser(this);
        Map<String, String> map = new HashMap<String, String>();
        map.put("num", num);

        AjaxParams param = new AjaxParams(map);
        new FinalHttp().get(Constants.GET_KUAIDI_OUT_URL, param, new AjaxCallBack<Object>() {
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
                if (StringUtils.isNotEmpty(t.toString())) {
                    String data = t.toString();
                    if (StringUtils.isNotEmpty(data.trim())) {
                        Gson gson = new Gson();
                        mExpressOuts = gson.fromJson(data, new TypeToken<ArrayList<ExpressOutData>>() {
                        }.getType());
                        showOutData(mExpressOuts, gson);
                    }
                }
            }
        });
    };

    protected void showOutData(ArrayList<ExpressOutData> mExpressOuts, Gson gson) {

        if (mExpressOuts != null && mExpressOuts.size() > 0) {
            ExpressOutData outData = mExpressOuts.get(0);
            mExpressCompany.setText(outData.getComCode());
            // 初始化，只需要调用一次  
            AssetsDatabaseManager.initManager(getApplication());  
            // 获取管理对象，因为数据库需要通过管理对象才能够获取  
          AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();  
          SQLiteDatabase db = mg.getDatabase("simi01.db"); 
          ExpressTypeData  typeData = AssetsDatabaseManager.searchExpressTypeByEcode(db,outData.getComCode());
          mCompany = typeData.getExpress_id();
          mExpressCompany.setText(typeData.getName());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constants.WATER_ADD_REMARK = "";
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
        /*
         * case R.id.layout_select_band: intent.putExtra("flag","99"); intent.setClass(MainPlusExpressOrderActivity.this,
         * MainPlusTeamTypeActivity.class); startActivityForResult(intent, 1); break;
         */
        case R.id.m_rl_remark:
            Intent intent2 = new Intent(MainPlusExpressOrderActivity.this, MainPlusContentActivity.class);
            intent2.putExtra(Constants.MAIN_PLUS_FLAG, Constants.REMARK);
            startActivity(intent2);
            break;
        case R.id.bt_create_express:
            mNo = mExpressNo.getText().toString();
            mFromAddr = mExpressFromAddr.getText().toString();
            mFromName = mExpressFromName.getText().toString();
            mFromTel = mExpressFromTel.getText().toString();
            mToAddr = mExpressToAddr.getText().toString();
            mToName = mExpressToName.getText().toString();
            mToTel = mExpressToTel.getText().toString();
            mRemarks = mExpressRemarks.getText().toString();

            /*
             * if (StringUtils.isEmpty(mLinkMan)) { UIUtils.showToast(MainPlusExpressOrderActivity.this, "请输入联系人"); dismissDialog(); return; } if
             * (StringUtils.isEmpty(mLinkTel)) { UIUtils.showToast(MainPlusExpressOrderActivity.this, "请输入联系电话"); return; } if
             * (StringUtils.isEmpty(mRemark)) { UIUtils.showToast(MainPlusExpressOrderActivity.this, "请输入备注"); return; }
             */
            postExpressAdd();
            break;
        case R.id.m_scan_zbar:
            intent = new Intent();
            intent.setClass(MainPlusExpressOrderActivity.this, CaptureActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, SCANNIN_GREQUEST_CODES);

            break;
        case R.id.m_ll_express_company:
            intent = new Intent();
            intent.setClass(MainPlusExpressOrderActivity.this, MainPlusExpressTypeActivity.class);
            startActivityForResult(intent, REQUEST_CODES);
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
                mExpressRemarks.setText(Constants.WATER_ADD_REMARK);
            }
        });
    }
}
