package com.meijialife.simi.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.bean.CardExtra;
import com.meijialife.simi.bean.Cards;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

@SuppressLint("SimpleDateFormat")
public class CardAlertActivity extends Activity {
    
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
    
    private Cards card;
    private CardExtra cardExtra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.card_alert_activity);
        super.onCreate(savedInstanceState);
        
        findView();
    }
    
	private void findView(){
        
        mAlertTitle = getIntent().getStringExtra("title");
        mAlertText = getIntent().getStringExtra("text");
        mCardId = getIntent().getStringExtra("card_id");
        mAlertDate = (Date) getIntent().getSerializableExtra("date");
        
        mAlert_time = new SimpleDateFormat("HH:mm").format(mAlertDate);
        mAlert_date = new SimpleDateFormat("yyyy-MM-dd").format(mAlertDate);

        
        tv_alert_title = (TextView)findViewById(R.id.tv_alert_title);
        tv_alert_time = (TextView)findViewById(R.id.tv_alert_time);
        tv_alert_date = (TextView)findViewById(R.id.tv_alert_date);
        tv_alert_text = (TextView)findViewById(R.id.tv_alert_text);
        bt_alert_detail = (Button) findViewById(R.id.bt_alert_detail);
        bt_alert_done = (Button)findViewById(R.id.bt_alert_done);
        
        initView();
    }
    
    private void initView(){
        tv_alert_title.setText(mAlertTitle.trim());
        tv_alert_time.setText(mAlert_time.trim());
        tv_alert_date.setText(mAlert_date.trim());
        tv_alert_text.setText(mAlertText.trim());
        getCardData();
        
        bt_alert_done.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bt_alert_detail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardAlertActivity.this, CardDetailsActivity.class);
                intent.putExtra("card_id",mCardId);
              /*  intent.putExtra("Cards", card);
                intent.putExtra("card_extra",cardExtra);*/
                startActivity(intent);
                CardAlertActivity.this.finish();
            }
        });
    }
    /**
     * 卡片详情列表
     */
    private void getCardData() {
        showDialog();
        String user_id = DBHelper.getUser(this).getId();

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id + "");
        map.put("card_id", mCardId);
        AjaxParams param = new AjaxParams(map);

        new FinalHttp().get(Constants.URL_GET_CARD_DETAILS, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(CardAlertActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                dismissDialog();
                LogOut.i("========", "onSuccess：" + t);
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            if (StringUtils.isNotEmpty(data)) {
                                Gson gson = new Gson();
                                card = gson.fromJson(data, Cards.class);
                                String card_extra = card.getCard_extra();
                                if(!StringUtils.isEmpty(card_extra)){
                                    cardExtra = gson.fromJson(card.getCard_extra(),CardExtra.class);
                                }else {
                                    cardExtra = new CardExtra();
                                }
                            } else {
                                // UIUtils.showToast(getActivity(), "数据错误");
                            }
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            errorMsg = getString(R.string.servers_error);
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            errorMsg = getString(R.string.param_missing);
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            errorMsg = getString(R.string.param_illegal);
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            errorMsg = msg;
                        } else {
                            errorMsg = getString(R.string.servers_error);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = getString(R.string.servers_error);

                }
                // 操作失败，显示错误信息|
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    UIUtils.showToast(CardAlertActivity.this, errorMsg);
                }
            }
        });
    }
    
    private ProgressDialog m_pDialog;
    public void showDialog() {
        if(m_pDialog == null){
            m_pDialog = new ProgressDialog(this);
            m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            m_pDialog.setMessage("请稍等...");
            m_pDialog.setIndeterminate(false);
            m_pDialog.setCancelable(true);
        }
        m_pDialog.show();
    }

    public void dismissDialog() {
        if (m_pDialog != null && m_pDialog.isShowing()) {
            // m_pDialog.hide();
            m_pDialog.dismiss();
            m_pDialog = null;
        }
    }
    
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        dismissDialog();
    
    }
}
