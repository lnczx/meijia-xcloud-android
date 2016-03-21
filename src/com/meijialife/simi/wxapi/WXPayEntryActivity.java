package com.meijialife.simi.wxapi;

import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.MyWalletActivity;
import com.meijialife.simi.activity.OrderDetailsActivity;
import com.meijialife.simi.activity.PayOrderActivity;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;
import com.meijialife.simi.wxpay.WxConstants;
import com.meijialife.simi.wxpay.WxPay;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = WXPayEntryActivity.class.getCanonicalName();

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this, WxConstants.APP_ID);

        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    public void onReq(BaseReq req) {
    }

    public void onResp(BaseResp resp) {
        Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
    
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//             AlertDialog.Builder builder = new AlertDialog.Builder(this);
//             builder.setTitle(R.string.app_tip);
//             builder.setMessage(getString(R.string.pay_result_callback_msg,
//             resp.errStr + ";code=" + String.valueOf(resp.errCode)));
//             builder.show();
            if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
//                PayOrderActivity.postCardOnlinePay(this, this, WxPay.outTradeNo, "2", "SUCCESS");
                PayOrderActivity.parseCardOnlineJson(this,this);
                if(Constants.USER_CHARGE_TYPE==99){
                    Intent intent = new Intent(this,MyWalletActivity.class); 
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(this,OrderDetailsActivity.class); 
                    intent.putExtra("orderId",PayOrderActivity.orderId);
                    startActivity(intent);
                }
                WxPay.activity.finish();
            }
            WXPayEntryActivity.this.finish();
        }
    }
}