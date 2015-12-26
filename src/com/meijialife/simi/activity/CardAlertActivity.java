package com.meijialife.simi.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.meijialife.simi.R;

public class CardAlertActivity extends Activity {
    
    private TextView tv_alert_title;
    private TextView tv_alert_time;
    private TextView tv_alert_date;
    private Button bt_alert_detail;
    private Button bt_alert_done;
    
    private String mAlertTitle="";
    private String mAlertTime ="";
    private String mAlertDate ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.card_alert_activity);
        super.onCreate(savedInstanceState);
        
        findView();
    }
    
    private void findView(){
        
        mAlertTitle = getIntent().getStringExtra("title");
        mAlertTime = getIntent().getStringExtra("time");
        mAlertDate = getIntent().getStringExtra("date");
        
        tv_alert_title = (TextView)findViewById(R.id.tv_alert_title);
        tv_alert_time = (TextView)findViewById(R.id.tv_alert_time);
        tv_alert_date = (TextView)findViewById(R.id.tv_alert_date);
        bt_alert_detail = (Button) findViewById(R.id.bt_alert_detail);
        bt_alert_done = (Button)findViewById(R.id.bt_alert_done);
        
        initView();
    }
    
    private void initView(){
        tv_alert_title.setText(mAlertTitle.trim());
        tv_alert_time.setText("");
        tv_alert_date.setText("");
        
        
        bt_alert_done.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
       /* bt_alert_detail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardAlertActivity.this, CardDetailsActivity.class);
                intent.putExtra("Cards", 0);
                intent.putExtra("card_extra",1);
                startActivity(intent);
            }
        });*/
        
    }
}
