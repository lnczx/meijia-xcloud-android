package com.meijialife.simi.alerm;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.WindowManager;
import android.widget.Toast;

import com.meijialife.simi.R;
import com.meijialife.simi.activity.CardAlertActivity;
import com.meijialife.simi.utils.LogOut;

/**
 * 提醒接收者
 * 
 */
@SuppressLint("NewApi")
public class AlermReceiver extends BroadcastReceiver {

    public static final String TAG = "Alerm";

    Context context;

    private WakeLock mWakelock;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        this.context = context;

        playAudio();
        // NotifyUtils.bigNotify(context, 100, "私人秘书", "会议时间到啦~~~~");
        LogOut.i(TAG, "提醒进来啦。。。");
//       Toast.makeText(context, "提醒进来了",Toast.LENGTH_SHORT).show();
        Bundle bundle = intent.getExtras();
        String title = bundle.getString("title");
        String text = bundle.getString("text");
        LogOut.i(TAG, "==" + title + ":" + text);

        showDlg(context, title, text);
        
//        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
//        mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.SCREEN_DIM_WAKE_LOCK, "SimpleTimer");
//        mWakelock.acquire();
       /* Intent intent2 = new Intent(context,CardAlertActivity.class);
        intent2.putExtra("title",title);
        intent2.putExtra("time",text);
        intent2.putExtra("date",text);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent2);*/
        
    }

    
    
    
    
    private void showDlg(Context context, String title, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(context.getResources().getDrawable(R.drawable.ic_launcher_logo_alerm));
        builder.setTitle(title);
        builder.setMessage(text).setCancelable(false).setPositiveButton("知道了", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
//                if(null!=mWakelock){
//                    mWakelock.release();
//                }
            }
        });
        builder.setOnDismissListener(new OnDismissListener() {
            
            @Override
            public void onDismiss(DialogInterface dialog) {
//                if(null!=mWakelock){
//                    mWakelock.release();
//                }
                
            }
        });
        AlertDialog alert = builder.create();
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
        LogOut.i(TAG, "成功弹出提示框！");
    }

    /**
     * 播放通知
     */
    private void playAudio() {
        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}