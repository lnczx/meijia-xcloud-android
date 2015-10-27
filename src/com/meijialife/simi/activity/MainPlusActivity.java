package com.meijialife.simi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.database.DBHelper;
import com.simi.easemob.EMConstant;
import com.simi.easemob.ui.ChatActivity;

public class MainPlusActivity extends Activity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_plus);

        initView();

    }

    private void initView() {

        findViewById(R.id.tv_call_mishu).setOnClickListener(this);
        findViewById(R.id.tv_plus_travel).setOnClickListener(this);
        findViewById(R.id.tv_plus_meeting).setOnClickListener(this);
        findViewById(R.id.tv_plus_affair).setOnClickListener(this);
        findViewById(R.id.tv_plus_notification).setOnClickListener(this);
        findViewById(R.id.tv_plus_morning).setOnClickListener(this);
        findViewById(R.id.tv_plus_more).setOnClickListener(this);
        findViewById(R.id.iv_plus_close).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.tv_call_mishu:
            toChit();
            break;
        case R.id.tv_plus_travel:
            startActivity(new Intent(MainPlusActivity.this, MainPlusTravelActivity.class));
            MainPlusActivity.this.finish();
            break;
        case R.id.tv_plus_meeting:
            startActivity(new Intent(MainPlusActivity.this, MainPlusMeettingActivity.class));
            MainPlusActivity.this.finish();
            break;
        case R.id.tv_plus_affair:
            startActivity(new Intent(MainPlusActivity.this, MainPlusAffairActivity.class));
            MainPlusActivity.this.finish();
            break;
        case R.id.tv_plus_notification:
            startActivity(new Intent(MainPlusActivity.this, MainPlusNotificationActivity.class));
            MainPlusActivity.this.finish();
            break;
        case R.id.tv_plus_morning:
            startActivity(new Intent(MainPlusActivity.this, MainPlusMorningActivity.class));
            MainPlusActivity.this.finish();
            break;
        case R.id.tv_plus_more:
            
            Intent  intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("url",Constants.URL_MORE_INFO);
            intent.putExtra("title", "更多服务");
            startActivity(intent);

            break;
        case R.id.iv_plus_close:
            finish();
            this.overridePendingTransition(R.anim.activity_close,0);  
            break;
        default:
            break;
        }

    }
    
    /**
     * 进入秘书or机器人助理聊天页面
     */
    private void toChit(){
        String im_sec_username;
        String im_sec_nickname;
        UserInfo userInfo = DBHelper.getUserInfo(MainPlusActivity.this);
        
        //是否有真人管家服务 1=是   0=否（用机器人管家）
        int senior = Integer.parseInt(userInfo.getIs_senior());
        if(senior == 1){
            im_sec_username = userInfo.getIm_sec_username();
            im_sec_nickname = userInfo.getIm_sec_nickname();
        }else{
            im_sec_username = userInfo.getIm_robot_username();
            im_sec_nickname = userInfo.getIm_robot_nickname();
        }
        
        Intent  intent = new Intent(MainPlusActivity.this, ChatActivity.class);
        intent.putExtra(EMConstant.EXTRA_USER_ID, im_sec_username);
        intent.putExtra(EMConstant.EXTRA_USER_NAME, im_sec_nickname);
        startActivity(intent);
    }
}
