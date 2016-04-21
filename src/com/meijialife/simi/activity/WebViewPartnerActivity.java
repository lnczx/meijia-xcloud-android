package com.meijialife.simi.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.DefaultServiceData;
import com.meijialife.simi.bean.PartnerDetail;
import com.meijialife.simi.bean.ServicePrices;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.ui.CustomShareBoard;
import com.meijialife.simi.ui.PopupMenu;
import com.meijialife.simi.ui.PopupMenu.MENUITEM;
import com.meijialife.simi.ui.PopupMenu.OnItemClickListener;
import com.meijialife.simi.utils.StringUtils;
import com.simi.easemob.utils.ShareConfig;

/**
 * 秘书详情专用Activity
 * 
 */
public class WebViewPartnerActivity extends Activity implements OnClickListener {

    private WebView webview;
    
    private ImageView iv_person_left;
    private TextView tv_person_title;
    private ImageView iv_person_close;

    private String url;
    private String titleStr;
    private TextView m_tv_buy;
    private Double disPrice;
    private PartnerDetail partnerDetail;
    private ServicePrices servicePrices;//服务报价
    private TextView m_tv_money;
   
    private RelativeLayout m_ll_bottom;//底部购买按钮
    private ProgressBar mProgressBar; //webView进度条
    
    //右边菜单
    private ImageView iv_menu;
    private PopupMenu popupMenu;
    private View layout_mask;
    private String titles;//页面title
    private int flag=0;//0=发现服务详情，1=默认服务详情
    private DefaultServiceData def;//


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.webview_activity);
        super.onCreate(savedInstanceState);

        init();
    }

    @SuppressLint({ "JavascriptInterface", "NewApi", "SetJavaScriptEnabled" })
    private void init() {
        url = getIntent().getStringExtra("url");
        titleStr = getIntent().getStringExtra("title");
        disPrice = getIntent().getDoubleExtra("dis_price",0);
        flag = getIntent().getIntExtra("flag",0);
     
        findViewBy();
        UserInfo userInfo = DBHelper.getUserInfo(WebViewPartnerActivity.this);
        final String mobile = userInfo.getMobile();
        final String name = userInfo.getName();
        if(flag==0){
            partnerDetail =(PartnerDetail) getIntent().getSerializableExtra("partnerDetail");
            servicePrices =(ServicePrices) getIntent().getSerializableExtra("servicePrices");
            m_tv_buy.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(name)){//手机号为空，跳转绑定手机号
                        Intent intent = new Intent(WebViewPartnerActivity.this,BindMobileActivity.class);
                        WebViewPartnerActivity.this.startActivity(intent);
                    }else {
                        if(disPrice>0){//普通金额支付界面
                            Intent intent = new Intent(WebViewPartnerActivity.this, PayOrderActivity.class);
                            intent.putExtra("PartnerDetail",partnerDetail);
                            intent.putExtra("flag", PayOrderActivity.FROM_FIND);
                            intent.putExtra("from", PayOrderActivity.FROM_MISHU);
                            intent.putExtra("servicePrices",servicePrices);
                            WebViewPartnerActivity.this.startActivity(intent);
                        }else {//免费咨询跳转到0元支付界面
                            Intent intent = new Intent(WebViewPartnerActivity.this, PayZeroOrderActivity.class);
                            intent.putExtra("PartnerDetail",partnerDetail);
                            intent.putExtra("from", PayOrderActivity.FROM_MISHU);
                            intent.putExtra("flag", PayOrderActivity.FROM_FIND);
                            intent.putExtra("servicePrices",servicePrices);
                            WebViewPartnerActivity.this.startActivity(intent);
                        }
                      
                    }                
                }
            });
        }else if (flag==1) {//默认服务跳转到支付页面
            def = (DefaultServiceData)getIntent().getSerializableExtra("defService");
            m_tv_buy.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(name)){//手机号为空，跳转绑定手机号
                        Intent intent = new Intent(WebViewPartnerActivity.this,BindMobileActivity.class);
                        WebViewPartnerActivity.this.startActivity(intent);
                    }else {
                        if(disPrice>0){//普通金额支付界面
                            Intent intent = new Intent(WebViewPartnerActivity.this, PayOrderActivity.class);
                            intent.putExtra("flag", PayOrderActivity.FROM_FIND);
                            intent.putExtra("from", PayOrderActivity.FROM_DEF_SERVICE);
                            intent.putExtra("def",def);
                            WebViewPartnerActivity.this.startActivity(intent);
                        }else {//免费咨询跳转到0元支付界面
                            Intent intent = new Intent(WebViewPartnerActivity.this, PayZeroOrderActivity.class);
                            intent.putExtra("flag", PayOrderActivity.FROM_DEF_SERVICE);
                            intent.putExtra("def",def);
                            WebViewPartnerActivity.this.startActivity(intent);
                        }
                      
                    }                
                }
            });
        }
      
        if (StringUtils.isEmpty(url)) {
            Toast.makeText(getApplicationContext(), "数据错误", 0).show();
            return;
        }
   
        //获取页面中的title
        WebChromeClient wvcc = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
               titles  =title;
               tv_person_title.setText(title);
            }
            
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
               
                if(newProgress==100){
                    mProgressBar.setVisibility(View.INVISIBLE);
                }else {
                    if(View.INVISIBLE==mProgressBar.getVisibility()){
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                    mProgressBar.setProgress(newProgress);
                }
            }
        };
        webview = (WebView) findViewById(R.id.webview);
        webview.setWebChromeClient(wvcc);//负责显示页面title
        webview.loadUrl(url);
        webview.addJavascriptInterface(this, "Koolearn");
        webview.setBackgroundColor(Color.parseColor("#00000000"));
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);// 设置js可以直接打开窗口，如window.open()，默认为false
        webview.getSettings().setJavaScriptEnabled(true);// 是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        webview.getSettings().setSupportZoom(true);// 是否可以缩放，默认true
        webview.getSettings().setBuiltInZoomControls(true);// 是否显示缩放按钮，默认false
        webview.getSettings().setUseWideViewPort(true);// 设置此属性，可任意比例缩放。大视图模式
        webview.getSettings().setLoadWithOverviewMode(true);// 和setUseWideViewPort(true)一起解决网页自适应问题
        webview.getSettings().setAppCacheEnabled(false);// 是否使用缓存
        webview.getSettings().setDomStorageEnabled(true);// DOM Storage
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        
        iv_menu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.showLocation(R.id.iv_person_more);
                popupMenu.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onClick(MENUITEM item, String str) {

                        switch (item) {
                        case ITEM1:// 刷新
                            webview.reload();
                            break;
                        case ITEM2:// 分享
                            ShareConfig.getInstance().inits(WebViewPartnerActivity.this,url,titles);
                            postShare();
                            break;
                        default:
                            break;
                        }
                    }
                });
            }
        });
    }

    private void findViewBy(){
        iv_person_left = (ImageView) findViewById(R.id.iv_person_left);
        iv_person_close = (ImageView) findViewById(R.id.iv_person_close);
        tv_person_title = (TextView) findViewById(R.id.tv_person_title);
        m_ll_bottom = (RelativeLayout)findViewById(R.id.m_ll_bottom);
        m_tv_buy = (TextView)findViewById(R.id.m_tv_buy);
        m_tv_money = (TextView)findViewById(R.id.m_tv_money);
        mProgressBar = (ProgressBar)findViewById(R.id.myProgressBar);
        iv_menu = (ImageView) findViewById(R.id.iv_person_more);
        layout_mask = findViewById(R.id.layout_mask);
        popupMenu = new PopupMenu(this);
      
        iv_person_left.setOnClickListener(this);
        iv_person_close.setOnClickListener(this);
        m_ll_bottom.setVisibility(View.VISIBLE);
        m_tv_money.setText("￥"+disPrice);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (webview != null && (keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack();
            return true;
        } else {
            finish();
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.iv_person_close: // 返回
            finish();
            break;
        case R.id.iv_person_left: // 返回
            if (webview != null && webview.canGoBack()) {
                webview.goBack();
            } else {
                finish();
            }
            break;
        default:
            break;
        }
    }
    
    private void postShare() {
//        layout_mask.setVisibility(View.VISIBLE);
        CustomShareBoard shareBoard = new CustomShareBoard(this);
        shareBoard.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                layout_mask.setVisibility(View.GONE);
            }
        });
        shareBoard.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }
}
