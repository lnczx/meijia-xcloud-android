package com.meijialife.simi.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.utils.StringUtils;

public class MainPlusContentActivity extends BaseActivity implements OnClickListener {

    private EditText tv_input_content;
    private String flag;
    private String content;
    private String remindContent;//提示内容

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
            remindContent ="会议内容";
        } else if (StringUtils.isEquals(flag, Constants.TRAVEL)) {
            setTitleName("备注消息");
            content = Constants.CARD_ADD_TREAVEL_CONTENT;
            remindContent ="备注内容";
        } else if (StringUtils.isEquals(flag, Constants.MORNING)) {
            setTitleName("通知公告");
            content = Constants.CARD_ADD_MORNING_CONTENT;
            remindContent ="内容";
        } else if (StringUtils.isEquals(flag, Constants.AFFAIR)) {
            setTitleName("事务提醒");
            content = Constants.CARD_ADD_AFFAIR_CONTENT;
            remindContent ="提醒内容";
        } else if (StringUtils.isEquals(flag, Constants.NOTIFICATION)) {
            setTitleName("邀约通知");
            content = Constants.CARD_ADD_NOTIFICATION_CONTENT;
            remindContent ="邀约内容";
        }else if (StringUtils.isEquals(flag, Constants.REMARK)) {
            setTitleName("备注");
            content = Constants.WATER_ADD_REMARK;
            remindContent ="备注";
        }


        findViewById(R.id.tv_submit).setOnClickListener(this);
        tv_input_content = (EditText) findViewById(R.id.tv_input_content);

        if (StringUtils.isNotEmpty(content)) {
            tv_input_content.setText(content);
        }else {
            tv_input_content.setHint("请输入"+remindContent);
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
            }else if (StringUtils.isEquals(flag, Constants.REMARK)) {
                Constants.WATER_ADD_REMARK = message;
            }
            //点击提交收回键盘
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
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
