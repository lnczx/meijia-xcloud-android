package com.meijialife.simi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.ReceiverBean;
import com.meijialife.simi.ui.XCRoundRectImageView;

public class NoticeActivity extends Activity implements OnClickListener {
    
    
    private XCRoundRectImageView m_iv_icon;//显示图标
    private TextView m_tv_content;//显示内容
    private TextView m_tv_title;//显示标题
    
    private ReceiverBean receiverBean;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        //设置锁屏状态弹屏幕
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        
        setContentView(R.layout.notice_activity);
        super.onCreate(savedInstanceState);
        
        receiverBean = (ReceiverBean)getIntent().getSerializableExtra("receiverBean");
        m_tv_content = (TextView)findViewById(R.id.m_tv_content);
        m_tv_title = (TextView)findViewById(R.id.m_tv_title);
        findViewById(R.id.m_btn1).setOnClickListener(this);
        findViewById(R.id.m_btn2).setOnClickListener(this);
        
        m_tv_content.setText(receiverBean.getRc());
        m_tv_title.setText(receiverBean.getRt());
    
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.m_btn1:
            Intent intent = new Intent(NoticeActivity.this, CardDetailsActivity.class);
            intent.putExtra("card_id",receiverBean.getCi());
            startActivity(intent);
            finish();
            break;
        case R.id.m_btn2:
            finish();
            break;
        default:
            break;
        }
    }
    
    
}
