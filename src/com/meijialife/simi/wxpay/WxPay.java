package com.meijialife.simi.wxpay;

import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.core.AsyncTask;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.PayOrderActivity;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WxPay {

    private static final String TAG = WxPay.class.getCanonicalName();

    private IWXAPI msgApi;
    private PayReq req;
    private Map<String, String> resultunifiedorder;
    private StringBuffer sb;
    private Context context;
    public static Activity activity;
    public static String outTradeNo;
    private String productName;
    private String productPrice;
    private int payType;

    public WxPay(Activity activity, Context context, int payType, String outTradeNo, String productName, String productPrice) {
        WxPay.activity = activity;
        this.context = context;
        this.payType = payType;
        WxPay.outTradeNo = outTradeNo;
        this.productName = productName;
        this.productPrice = productPrice;
        msgApi = WXAPIFactory.createWXAPI(context, null);
        req = new PayReq();
        sb = new StringBuffer();

        msgApi.registerApp(WxConstants.APP_ID);
        // 生成prepay_id
        GetWxPrepayId();
//        GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
//        getPrepayId.execute();

    }
    private ProgressDialog dialog;

    private String prepay_id;

    private String nonce_str;

    private String time_stamp;

    private String sign;
    /**
     * 获取预支付id
     */
    private void GetWxPrepayId() {
        
        dialog = ProgressDialog.show(context, null, context.getString(R.string.getting_prepayid));
        if (!NetworkUtils.isNetworkConnected(context)) {
            Toast.makeText(context, context.getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", DBHelper.getUserInfo(context).getUser_id());  
        map.put("order_no", outTradeNo);  
        map.put("order_type", payType+""); //订单类型 0 = 订单支付 1= 充值卡充值 2 = 私密卡购买
        AjaxParams param = new AjaxParams(map);

        new FinalHttp().post(Constants.URL_ORDER_WEIXIN_PRE, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                if (dialog != null) {
                    dialog.dismiss();
                }
                Toast.makeText(context, context.getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                if (dialog != null) {
                    dialog.dismiss();
                }
                LogOut.i("========", "onSuccess：" + t);
                JSONObject json;
                try {
                    json = new JSONObject(t.toString());
                    int status = Integer.parseInt(json.getString("status"));
                    String msg = json.getString("msg");
                    if (status == Constants.STATUS_SUCCESS) { // 正确
                        genPayReq(json);
                        sendPayReq();                        
                        
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
     * 生成签名
     * @param json 
     */

//    private String genPackageSign(List<NameValuePair> params) {
//        StringBuilder sb = new StringBuilder();
//
//        for (int i = 0; i < params.size(); i++) {
//            sb.append(params.get(i).getName());
//            sb.append('=');
//            sb.append(params.get(i).getValue());
//            sb.append('&');
//        }
//        sb.append("key=");
//        sb.append(WxConstants.API_KEY);
//
//        String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
//        Log.e(TAG, packageSign);
//        return packageSign;
//    }

//    private String genAppSign(List<NameValuePair> params) {
//        StringBuilder sb = new StringBuilder();
//
//        for (int i = 0; i < params.size(); i++) {
//            sb.append(params.get(i).getName());
//            sb.append('=');
//            sb.append(params.get(i).getValue());
//            sb.append('&');
//        }
//        sb.append("key=");
//        sb.append(WxConstants.API_KEY);
//
//        this.sb.append("sign str\n" + sb.toString() + "\n\n");
//        String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
//        Log.e(TAG, appSign);
//        return appSign;
//    }

//    private String toXml(List<NameValuePair> params) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("<xml>");
//        for (int i = 0; i < params.size(); i++) {
//            sb.append("<" + params.get(i).getName() + ">");
//
//            sb.append(params.get(i).getValue());
//            sb.append("</" + params.get(i).getName() + ">");
//        }
//        sb.append("</xml>");
//
//        Log.e(TAG, sb.toString());
//        return sb.toString();
//    }

//    private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String, String>> {
//
//        
//
//        @Override
//        protected void onPreExecute() {
//            dialog = ProgressDialog.show(context, null, context.getString(R.string.getting_prepayid));
//        }
//
//        @Override
//        protected void onPostExecute(Map<String, String> result) {
//            if (dialog != null) {
//                dialog.dismiss();
//            }
//            sb.append("prepay_id\n" + result.get("prepay_id") + "\n\n");
//            resultunifiedorder = result;
//            genPayReq();
//            sendPayReq();
//        }
//
//        @Override
//        protected void onCancelled() {
//            super.onCancelled();
//        }
//
//        @Override
//        protected Map<String, String> doInBackground(Void... params) {
//
//            String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
//            String entity = genProductArgs();
//
//            Log.e(TAG, entity);
//
//            byte[] buf = Util.httpPost(url, entity);
//
//            String content = new String(buf);
//            Log.e(TAG, content);
//            Map<String, String> xml = decodeXml(content);
//
//            return xml;
//        }
//    }

//    public Map<String, String> decodeXml(String content) {
//
//        try {
//            Map<String, String> xml = new HashMap<String, String>();
//            XmlPullParser parser = Xml.newPullParser();
//            parser.setInput(new StringReader(content));
//            int event = parser.getEventType();
//            while (event != XmlPullParser.END_DOCUMENT) {
//
//                String nodeName = parser.getName();
//                switch (event) {
//                case XmlPullParser.START_DOCUMENT:
//
//                    break;
//                case XmlPullParser.START_TAG:
//
//                    if ("xml".equals(nodeName) == false) {
//                        // 实例化student对象
//                        xml.put(nodeName, parser.nextText());
//                    }
//                    break;
//                case XmlPullParser.END_TAG:
//                    break;
//                }
//                event = parser.next();
//            }
//
//            return xml;
//        } catch (Exception e) {
//            Log.e(TAG, e.toString());
//        }
//        return null;
//
//    }

//    private String genNonceStr() {
//        Random random = new Random();
//        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
//    }
//
//    private long genTimeStamp() {
//        return System.currentTimeMillis() / 1000;
//    }
//
//    private String genOutTradNo() {
//        return outTradeNo;
//    }
//
//    //
//    private String genProductArgs() {
//        StringBuffer xml = new StringBuffer();
//
//        try {
//            String nonceStr = genNonceStr();
//
//            xml.append("</xml>");
//            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
//            packageParams.add(new BasicNameValuePair("appid", WxConstants.APP_ID));
//            packageParams.add(new BasicNameValuePair("body", this.productName));
//            packageParams.add(new BasicNameValuePair("mch_id", WxConstants.MCH_ID));
//            packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
//            // packageParams.add(new BasicNameValuePair("notify_url",
//            // "http://121.40.35.3/test"));
//            packageParams.add(new BasicNameValuePair("notify_url", com.meijialife.simi.Constants.URL_ORDER_WEIXIN_NOTIFY));
//            packageParams.add(new BasicNameValuePair("out_trade_no", genOutTradNo()));
//            packageParams.add(new BasicNameValuePair("spbill_create_ip", "127.0.0.1"));
//            packageParams.add(new BasicNameValuePair("total_fee", this.productPrice));
//            packageParams.add(new BasicNameValuePair("trade_type", "APP"));
//
//            String sign = genPackageSign(packageParams);
//            packageParams.add(new BasicNameValuePair("sign", sign));
//
//            String xmlstring = toXml(packageParams);
//
//            return xmlstring;
//
//        } catch (Exception e) {
//            Log.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
//            return null;
//        }
//
//    }
    
    //{"data":{"sign":"AB5E7A9BEEA5594C74DEE08E6DEB84EC","appId":"wx93aa45d30bf6cba3","orderNo":"656364030188851200","timeStamp":"1445324354","userId":143,"signType":"MD5","partnerId":"1246250401",
//    "package":"Sign=WXPay","nonceStr":"1f6419b1cbe79c71410cb320fc094775","prepayId":"wx20151020145914d964ffc8570692417575","notifyUrl":"http:\/\/123.57.173.36\/simi\/wxpay-notify-ordercard.do","mobile":"18037338899"},"msg":"ok","status":0}

    private void genPayReq(JSONObject json) {
        try {
            JSONObject obj = json.getJSONObject("data");
            prepay_id = obj.getString("prepayId");  
            nonce_str = obj.getString("nonceStr");  
            time_stamp = obj.getString("timeStamp");  
            sign = obj.getString("sign");  

        } catch (JSONException e) {
            e.printStackTrace();
        }

        req.appId = WxConstants.APP_ID;
        req.partnerId = WxConstants.MCH_ID;
        req.prepayId = prepay_id;
        req.packageValue = "Sign=WXPay";
        req.nonceStr = nonce_str;
        req.timeStamp = time_stamp;
        req.sign =sign;

        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

        sb.append("sign\n" + req.sign + "\n\n");

        Log.e(TAG, signParams.toString());

    }

    private void sendPayReq() {

        msgApi.registerApp(WxConstants.APP_ID);
        msgApi.sendReq(req);
    }

}
