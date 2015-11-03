package com.meijialife.simi.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;

/**
 * 分享
 * 
 * 
 */
public class ShareActivity extends BaseActivity implements OnClickListener {
    private final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
    private IWXAPI api;
    private Button btn_share; // 分享

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.share_activity);
        super.onCreate(savedInstanceState);

        regToWx();
        // api = WXAPIFactory.createWXAPI(this, Constant.WX_APP_ID);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("分享", "onResume");
    }

    private void init() {
        setTitleName("分 享");
        requestBackBtn();

        btn_share = (Button) findViewById(R.id.share_btn_share);
        btn_share.setOnClickListener(this);
    }

    public void onClick(View arg0) {
        switch (arg0.getId()) {
        case R.id.share_btn_share: // 分享
            // shareWx();
            mController.directShare(ShareActivity.this, SHARE_MEDIA.WEIXIN, new SnsPostListener() {

                @Override
                public void onStart() {
                }

                @Override
                public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
                    String showText = "分享成功";
                    if (eCode != StatusCode.ST_CODE_SUCCESSED) {
                        showText = "分享失败 [" + eCode + "]";
                    }
                    Toast.makeText(ShareActivity.this, showText, Toast.LENGTH_SHORT).show();
                }
            });

            break;

        default:
            break;
        }
    }

    /**
     * 注册到微信
     */
    private void regToWx() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID);
        // 将该app注册到微信
        api.registerApp(Constants.WX_APP_ID);
    }


}
