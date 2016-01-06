package com.meijialife.simi.dialog;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.meijialife.simi.MainActivity;
import com.meijialife.simi.R;

@SuppressLint("SimpleDateFormat")
public class AlermDialog extends Dialog {
    
    private Context context;
    private String title;
    private String msg;
    
    private TextView tv_alert_title;//提醒标题
    private TextView tv_alert_time;//提醒时间
    private TextView tv_alert_date;//提醒日期
    private TextView tv_alert_text;//提醒内容
    
    private Button bt_alert_detail;
    private Button bt_alert_done;
    private String mCardId;//卡片Id
    private String mAlertTitle="";
    private String mAlertText ="";
    private String mAlert_date= "";
    private String mAlert_time= "";
    private Date mAlertDate = new Date();
    
    /**
     * 
     * @param context
     * @param theme dialog主题
     * @param title 标题
     * @param msg   消息
     */
    public AlermDialog(Context context, String title, String msg,Date date) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        setContentView(R.layout.card_alert_activity);
//        setContentView(R.layout.layout_alerm_dialog);
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//设置系统权限，否则在服务中弹不出来
        
        this.context = context;
        this.title = title;
        this.msg = msg;
        this.mAlertDate = date;
        
        initView();
    }

	private void initView(){
      /*  TextView tv_title = (TextView)findViewById(R.id.tv_title);
        TextView tv_msg = (TextView)findViewById(R.id.tv_msg);
        Button btn_ok = (Button)findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(okListener);
        
        tv_title.setText(title);
        tv_msg.setText(msg);*/
        
      
        
        mAlert_time = new SimpleDateFormat("HH:mm:ss").format(this.mAlertDate);
        mAlert_date = new SimpleDateFormat("yyyy-MM-dd").format(this.mAlertDate);
        
        tv_alert_title = (TextView)findViewById(R.id.tv_alert_title);
        tv_alert_time = (TextView)findViewById(R.id.tv_alert_time);
        tv_alert_date = (TextView)findViewById(R.id.tv_alert_date);
        tv_alert_text = (TextView)findViewById(R.id.tv_alert_text);
        bt_alert_detail = (Button) findViewById(R.id.bt_alert_detail);
        bt_alert_done = (Button)findViewById(R.id.bt_alert_done);
        
        tv_alert_title.setText(this.title.trim());
        tv_alert_time.setText(mAlert_time.trim());
        tv_alert_date.setText(mAlert_date.trim());
        tv_alert_text.setText(this.msg.trim());
        
        bt_alert_detail.setOnClickListener(detailListener);
        bt_alert_done.setOnClickListener(doneListener);
        }
    
    
    
    /**
     * 确认按钮监听事件
     */
/*    View.OnClickListener okListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    }*/;
    
    View.OnClickListener doneListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };
    
    View.OnClickListener detailListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("context",context.toString());
            Intent intent = new Intent();
            intent.setClass(context,MainActivity.class);
            context.startActivity(intent);
            ((Activity)context).finish();
            dismiss();
/*          Intent intent = new Intent(CardAlertActivity.this, CardAlertActivity.class);
//          intent.putExtra("card_id",mCardId);
          intent.putExtra("Cards", card);
          intent.putExtra("card_extra",cardExtra);
          startActivity(intent);
          CardAlertActivity.this.finish();
*/        }
    };
    
    

}
