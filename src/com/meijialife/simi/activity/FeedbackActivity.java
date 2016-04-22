package com.meijialife.simi.activity;

import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.AlertWindow;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;

/**
 * 反馈
 *
 */
public class FeedbackActivity extends BaseActivity implements OnClickListener{
	private Context context = this;

	private TextView tv_text;
	private Button btn_submit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.feedback_activity);
		super.onCreate(savedInstanceState);

		init();
	}

	private void init() {

		setTitleName("意 见 反 馈");
		requestBackBtn();

		tv_text = (TextView)findViewById(R.id.feedback_tv_text);
		btn_submit = (Button)findViewById(R.id.feedback_btn_submit);
		btn_submit.setOnClickListener(this);
	}

	@Override
    public void onClick(View v) {
		switch (v.getId()) {
		case R.id.feedback_btn_submit:
			postFeedback();
			break;

		default:
			break;
		}
	}

	/**
     * 提交反馈接口
     *
     */
    private void postFeedback() {

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }
        String text = tv_text.getText().toString();
		if(StringUtils.isEmpty(text)){
			Toast.makeText(this, "请输入反馈信息！", 1).show();
			return;
		}

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", DBHelper.getUser(this).getId());	//
        map.put("content", text); 			// 意见反馈. urlencode , 注意，仅允许 200个汉字
        AjaxParams param = new AjaxParams(map);

        showDialog();
        new FinalHttp().post(Constants.URL_POST_FEEDBACK, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(FeedbackActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                dismissDialog();
                LogOut.i("========", "onSuccess：" + t);
                JSONObject json;
                try {
                    json = new JSONObject(t.toString());
                    int status = Integer.parseInt(json.getString("status"));
                    String msg = json.getString("msg");

//                    if (status == Constant.STATUS_SUCCESS) { // 正确
//                    	parseJson(json);
//                    } else if (status == Constant.STATUS_SERVER_ERROR) { // 服务器错误
//                        Toast.makeText(FeedbackActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
//                    } else if (status == Constant.STATUS_PARAM_MISS) { // 缺失必选参数
//                        Toast.makeText(FeedbackActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
//                    } else if (status == Constant.STATUS_PARAM_ILLEGA) { // 参数值非法
//                        Toast.makeText(FeedbackActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
//                    } else if (status == Constant.STATUS_OTHER_ERROR) { // 999其他错误
//                        Toast.makeText(FeedbackActivity.this, msg, Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(FeedbackActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
//                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(FeedbackActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 解析json数据
     *
     * @param json
     */
    private void parseJson(JSONObject json) {
    	AlertWindow.dialog(context, "", "您的建议已收到，我们会尽快查找原因并尽快解决。非常感谢对有个管家的关注！", "不客气", new android.content.DialogInterface.OnClickListener() {
			@Override
            public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
        //Toast.makeText(FeedbackActivity.this, "提交成功！", 1).show();

    }
}
