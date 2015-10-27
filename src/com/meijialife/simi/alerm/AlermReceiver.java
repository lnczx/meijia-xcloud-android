package com.meijialife.simi.alerm;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.view.WindowManager;

import com.meijialife.simi.R;
import com.meijialife.simi.utils.LogOut;

/**
 * 提醒接收者
 * 
 */
public class AlermReceiver extends BroadcastReceiver {
    
    public static final String TAG = "Alerm";

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        this.context = context;

        playAudio();
        
        // NotifyUtils.bigNotify(context, 100, "私人秘书", "会议时间到啦~~~~");
        LogOut.i(TAG, "提醒进来啦。。。");
        Bundle bundle = intent.getExtras();
        String title = bundle.getString("title");
        String text = bundle.getString("text");
        LogOut.i(TAG, "=="+title+":"+text);
        
        showDlg(context, title, text);

    }
    
    private void showDlg(Context context, String title, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(context.getResources().getDrawable(R.drawable.ic_launcher_logo));
        builder.setTitle("私人秘书");
        builder.setMessage(title + ":\n\n" + text).setCancelable(false).
        setPositiveButton("知道了", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
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