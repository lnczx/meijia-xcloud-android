package com.meijialife.simi.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.utils.StringUtils;

public class MainPlusContentActivity extends BaseActivity implements OnClickListener {

    private EditText tv_input_content;
    private String flag;
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_main_plus_beizu);
        super.onCreate(savedInstanceState);
        flag = getIntent().getStringExtra(Constants.MAIN_PLUS_FLAG);

        initView();

    }

    private void initView() {
        requestBackBtn();
        requestRightBtn();
        if (StringUtils.isEquals(flag, Constants.MEETTING)) {
            setTitleName("会议内容");
            content = Constants.CARD_ADD_MEETING_CONTENT;

        } else if (StringUtils.isEquals(flag, Constants.TRAVEL)) {
            setTitleName("备注消息");
            content = Constants.CARD_ADD_TREAVEL_CONTENT;
        } else if (StringUtils.isEquals(flag, Constants.MORNING)) {
            setTitleName("叫早内容");
            content = Constants.CARD_ADD_MORNING_CONTENT;
        } else if (StringUtils.isEquals(flag, Constants.AFFAIR)) {
            setTitleName("事务提醒");
            content = Constants.CARD_ADD_AFFAIR_CONTENT;
        } else if (StringUtils.isEquals(flag, Constants.NOTIFICATION)) {
            setTitleName("邀约通知");
            content = Constants.CARD_ADD_NOTIFICATION_CONTENT;
        }

        findViewById(R.id.tv_submit).setOnClickListener(this);
        tv_input_content = (EditText) findViewById(R.id.tv_input_content);

        if (StringUtils.isNotEmpty(content)) {
            tv_input_content.setText(content);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.tv_submit:
//            Intent intent = new Intent();
//            intent.putExtra("content", cityid);
//            setResult(RESULT_OK, intent);
            String message = tv_input_content.getText().toString().trim();

            if (StringUtils.isEquals(flag, Constants.MEETTING)) {
                Constants.CARD_ADD_MEETING_CONTENT = message;
            } else if (StringUtils.isEquals(flag, Constants.TRAVEL)) {
                Constants.CARD_ADD_TREAVEL_CONTENT = message;
            } else if (StringUtils.isEquals(flag, Constants.MORNING)) {
                Constants.CARD_ADD_MORNING_CONTENT = message;
            } else if (StringUtils.isEquals(flag, Constants.AFFAIR)) {
                Constants.CARD_ADD_AFFAIR_CONTENT = message;
            } else if (StringUtils.isEquals(flag, Constants.NOTIFICATION)) {
                Constants.CARD_ADD_NOTIFICATION_CONTENT = message;
            }
            
            MainPlusContentActivity.this.finish();
            break;

        default:
            break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
