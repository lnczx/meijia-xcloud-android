package com.meijialife.simi;

import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.meijialife.simi.activity.CarAlertActivity;
import com.meijialife.simi.activity.CardAlertActivity;
import com.meijialife.simi.activity.CardDetailsActivity;
import com.meijialife.simi.activity.LoginActivity;
import com.meijialife.simi.activity.NoticeActivity;
import com.meijialife.simi.activity.SplashActivity;
import com.meijialife.simi.alerm.AlermUtils;
import com.meijialife.simi.bean.ReceiverBean;
import com.meijialife.simi.utils.AndroidUtil;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.StringUtils;

public class MyPushReceiver extends BroadcastReceiver {

    /**
     * 应用未启动, 个推 service已经被唤醒,保存在该时间段内离线消息(此时 GetuiSdkDemoActivity.tLogView == null)
     */
    public static StringBuilder payloadData = new StringBuilder();
    private Context mContext;
    private ReceiverBean receiverBean;
    private Date fdate;
    private final String ACTION_SETCLOCK="s";
    private final String ACTION_ALARM="a";
    private final String ACTION_MSG="m";
    private final String ACTION_CAR_MSG="car-msg";

    @SuppressWarnings("deprecation")
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
//                UIUtils.showToastLong(context, "接收到透传消息:" + data);

                try {
                    receiverBean = new Gson().fromJson(data, new TypeToken<ReceiverBean>() {}.getType());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (null != receiverBean) {
                    if(!AndroidUtil.isRunningForeground(context)){ 
                        /**
                         * app处于后台 勾选理解提醒 声音+通知
                         * 准点弹出大屏+声音
                         */
                        if (StringUtils.isEquals(receiverBean.getIs(), "true") && StringUtils.isEquals(receiverBean.getAc(), ACTION_MSG)) {
                            //推送通知
                            setNotification(receiverBean);
                        } else if (StringUtils.isEquals(receiverBean.getAc(), ACTION_SETCLOCK)) {
                            //is_show=true表示显示通知栏，=false不显示通知栏
                            if(StringUtils.isEquals(receiverBean.getIs(), "true")){
                                setNotification3(receiverBean);
                                AlermUtils.playAudio(context);
                            }
                        }else if(StringUtils.isEquals(receiverBean.getAc(), ACTION_ALARM)){
                          //push-alarm准点推送弹出大屏
                          //is_show=true表示显示通知栏，=false不显示通知栏
                           /* if(StringUtils.isEquals(receiverBean.getIs_show(), "true")){
                                setNotification3(receiverBean);
                                AlermUtils.playAudio(context);
                            }*/
                            AlermUtils.playAudio(context);
                            intent = new Intent(mContext, CardAlertActivity.class);
                              long remindTime = Long.parseLong((receiverBean.getRe()*1000)+"");
                              fdate = new Date(remindTime);
                              intent.putExtra("title",receiverBean.getRt());
                              intent.putExtra("text",receiverBean.getRc());
                              intent.putExtra("date",fdate);
                              intent.putExtra("card_id",receiverBean.getCi());
                              intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                              context.startActivity(intent);
                        }else if ( StringUtils.isEquals(receiverBean.getIs(), "false") &&
                            StringUtils.isEquals(receiverBean.getAc(), ACTION_CAR_MSG)) {
                            Intent intent1 = new Intent(mContext, CarAlertActivity.class);
                            intent1.putExtra("bean",receiverBean);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent1);
                          
                        }
                    }else {
                      /**
                       * app处于前台，创建勾选立即提醒，弹出小窗口+声音+通知
                       * 到点弹出大屏+声音
                       */
                        if (StringUtils.isEquals(receiverBean.getIs(), "true") && StringUtils.isEquals(receiverBean.getAc(), ACTION_MSG)) {
                            //推送通知
                            setNotification(receiverBean);
                        } else if (StringUtils.isEquals(receiverBean.getAc(), ACTION_SETCLOCK)) {
                            //is_show=true表示显示通知栏，=false不显示通知栏
                            if(StringUtils.isEquals(receiverBean.getIs(), "true")){
                                setNotification3(receiverBean);
                                AlermUtils.playAudio(context);
                                
                                intent = new Intent(mContext, NoticeActivity.class);
                                intent.putExtra("receiverBean", receiverBean);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                              //设置本地闹钟
                            /* long remindTime = Long.parseLong(receiverBean.getRemind_time());
                            fdate = new Date(remindTime);
                            if (!receiverBean.getCard_id().equals("0")) {
                                AlermUtils.initAlerm(context, 1, fdate, receiverBean.getRemind_title(), receiverBean.getRemind_content(),
                                        receiverBean.getCard_id());
                            }*/
                        }else if(StringUtils.isEquals(receiverBean.getAc(), ACTION_ALARM)){
                          //push-alarm准点推送弹出大屏
                          //is_show=true表示显示通知栏，=false不显示通知栏
                          /*  if(StringUtils.isEquals(receiverBean.getIs_show(), "true")){
                                setNotification3(receiverBean);
                                AlermUtils.playAudio(context);
                            }*/
                            AlermUtils.playAudio(context);
                            intent = new Intent(mContext, CardAlertActivity.class);
                            long remindTime = Long.parseLong((receiverBean.getRe()*1000)+"");
                            fdate = new Date(remindTime);
                            intent.putExtra("title",receiverBean.getRt());
                            intent.putExtra("text",receiverBean.getRc());
                            intent.putExtra("date",fdate);
                            intent.putExtra("card_id",receiverBean.getCi());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }else if ( StringUtils.isEquals(receiverBean.getIs(), "false") &&
                            StringUtils.isEquals(receiverBean.getAc(), ACTION_CAR_MSG)) {
                            Intent intent1 = new Intent(mContext, CarAlertActivity.class);
                            intent1.putExtra("bean",receiverBean);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent1);
                          
                        }
                    }
                    
                   
                }
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
            SplashActivity.clientid = cid;
            LogOut.debug("百度推送cid:" + cid);

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
     * 点击弹框进入app
     * @param receiverBean
     */
    private void setNotification(ReceiverBean receiverBean) {
        //NotificationManager状态通知的管理类，必须通过getSystemService()方法来获取
        NotificationManager manager = (NotificationManager) mContext.getSystemService(android.content.Context.NOTIFICATION_SERVICE);

        //点击通知负责页面跳转
//        Intent intent = new Intent(mContext, SplashActivity.class);
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //android3.0以后采用NotificationCompat构建
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        builder.setContentTitle(receiverBean.getRt());
        builder.setContentText(receiverBean.getRc());
        builder.setSmallIcon(R.drawable.ic_launcher_logo);
        builder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher_logo));
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        // builder.setSubText("点击进入播放...");
        // builder.setTicker(channelNanme + "的" + programName + "还有五分钟就要开播了！" + "点击进入播放...");
        manager.notify(1, builder.build());

    }
    /**
     * 点击弹框进入大屏
     * @param receiverBean
     */
    private void setNotification2(ReceiverBean receiverBean) {
        //NotificationManager状态通知的管理类，必须通过getSystemService()方法来获取
        NotificationManager manager = (NotificationManager) mContext.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        long remindTime = Long.parseLong((receiverBean.getRe()*1000)+"");
        fdate = new Date(remindTime);
        //点击通知负责页面跳转
        Intent intent = new Intent(mContext, CardAlertActivity.class);
//        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra("title",receiverBean.getRt());
        intent.putExtra("text",receiverBean.getRc());
        intent.putExtra("date",fdate);
        intent.putExtra("card_id",receiverBean.getCi());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //android3.0以后采用NotificationCompat构建
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        builder.setContentTitle(receiverBean.getRt());
        builder.setContentText(receiverBean.getRc());
        builder.setSmallIcon(R.drawable.ic_launcher_logo);
        builder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher_logo));
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        manager.notify(1, builder.build());
    }
    /**
     * 点击通知进入卡片详情
     * @param receiverBean
     */
    private void setNotification3(ReceiverBean receiverBean) {
        //NotificationManager状态通知的管理类，必须通过getSystemService()方法来获取
        NotificationManager manager = (NotificationManager) mContext.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        //点击通知负责页面跳转
        Intent intent = new Intent(mContext, CardDetailsActivity.class);
//        Intent intent = new Intent(mContext, MoreActivity.class);
        intent.putExtra("card_id",receiverBean.getCi());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //android3.0以后采用NotificationCompat构建
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        builder.setContentTitle(receiverBean.getRt());
        builder.setContentText(receiverBean.getRc());
        builder.setSmallIcon(R.drawable.ic_launcher_logo);
        builder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher_logo));
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        manager.notify(1, builder.build());
    }
    
    private void setNotification4(ReceiverBean receiverBean) {
        //NotificationManager状态通知的管理类，必须通过getSystemService()方法来获取
        NotificationManager manager = (NotificationManager) mContext.getSystemService(android.content.Context.NOTIFICATION_SERVICE);

        //点击通知负责页面跳转
//        Intent intent = new Intent(mContext, SplashActivity.class);
        Intent intent = new Intent(mContext, CarAlertActivity.class);
        intent.putExtra("bean",receiverBean);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //android3.0以后采用NotificationCompat构建
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        builder.setContentTitle(receiverBean.getRt());
        builder.setContentText(receiverBean.getRc());
        builder.setSmallIcon(R.drawable.ic_launcher_logo);
        builder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher_logo));
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        manager.notify(1, builder.build());

    }
    
}
