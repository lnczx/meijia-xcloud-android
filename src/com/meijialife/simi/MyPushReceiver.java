package com.meijialife.simi;

import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.meijialife.simi.activity.CardDetailsActivity;
import com.meijialife.simi.activity.LoginActivity;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

public class MyPushReceiver extends BroadcastReceiver {

    /**
     * 应用未启动, 个推 service已经被唤醒,保存在该时间段内离线消息(此时 GetuiSdkDemoActivity.tLogView == null)
     */
    public static StringBuilder payloadData = new StringBuilder();
    private Context mContext;


    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;

        Bundle bundle = intent.getExtras();
        Log.d("GetuiSdkDemo", "onReceive() action=" + bundle.getInt("action"));

        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
        case PushConsts.GET_MSG_DATA:
            // 获取透传数据
            // String appid = bundle.getString("appid");
            byte[] payload = bundle.getByteArray("payload");

            String taskid = bundle.getString("taskid");
            String messageid = bundle.getString("messageid");

            // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
            boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
            System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));

            if (payload != null) {
                String data = new String(payload);

                Log.d("GetuiSdkDemo", "receiver payload : " + data);

                payloadData.append(data);
                payloadData.append("\n");

                // if (GetuiSdkDemoActivity.tLogView != null) {
                // GetuiSdkDemoActivity.tLogView.append(data + "\n");
                // }
                LogOut.debug("pushdata:" + data + "\n");
                UIUtils.showToastLong(context, "接收到透传消息:" + data);

            }
            break;

        case PushConsts.GET_CLIENTID:
            // 获取ClientID(CID)
            // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
            String cid = bundle.getString("clientid");
            // if (GetuiSdkDemoActivity.tView != null) {
            // GetuiSdkDemoActivity.tView.setText(cid);
            // }
            LoginActivity.clientid = cid;
            LogOut.debug("cid:" + cid);
            bind_user(cid);

            break;

        case PushConsts.THIRDPART_FEEDBACK:
            /*
             * String appid = bundle.getString("appid"); String taskid = bundle.getString("taskid"); String actionid = bundle.getString("actionid");
             * String result = bundle.getString("result"); long timestamp = bundle.getLong("timestamp");
             * 
             * Log.d("GetuiSdkDemo", "appid = " + appid); Log.d("GetuiSdkDemo", "taskid = " + taskid); Log.d("GetuiSdkDemo", "actionid = " +
             * actionid); Log.d("GetuiSdkDemo", "result = " + result); Log.d("GetuiSdkDemo", "timestamp = " + timestamp);
             */
            break;

        default:
            break;
        }
    }

    /**
     * 绑定接口
     * 
     * @param date
     */
    private void bind_user(String client_id) {
        String user_id = null;
         String clientid=null;
        try {
            clientid = DBHelper.getUser(mContext).getClient_id();
        } catch (Exception e1) {
            e1.printStackTrace();
            clientid=null;
        }
        
        if(StringUtils.isNotEmpty(clientid)){
            return;
        }
        
        try {
            user_id = DBHelper.getUserInfo(mContext).getUser_id();
        } catch (Exception e1) {
            e1.printStackTrace();
            user_id=null;
        }
        if (StringUtils.isEmpty(user_id)) {
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id + "");
        map.put("device_type", "android");
        map.put("client_id", client_id);
        AjaxParams param = new AjaxParams(map);

        new FinalHttp().post(Constants.URL_POST_PUSH_BIND, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(mContext, mContext.getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                LogOut.i("========", "onSuccess：" + t);
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
//                            UIUtils.showToast(mContext, "推送绑定成功");
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            errorMsg = mContext.getString(R.string.servers_error);
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            errorMsg = mContext.getString(R.string.param_missing);
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            errorMsg = mContext.getString(R.string.param_illegal);
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            errorMsg = msg;
                        } else {
                            errorMsg = mContext.getString(R.string.servers_error);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = mContext.getString(R.string.servers_error);

                }
                // 操作失败，显示错误信息|
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    UIUtils.showToast(mContext, errorMsg);
                }
            }
        });

    }
}
