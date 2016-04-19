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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;

import com.meijialife.simi.R;
import com.meijialife.simi.ui.CustomShareBoard;
import com.meijialife.simi.ui.PopupMenu;
import com.meijialife.simi.ui.PopupMenu.MENUITEM;
import com.meijialife.simi.ui.PopupMenu.OnItemClickListener;
import com.meijialife.simi.utils.StringUtils;
import com.simi.easemob.utils.ShareConfig;


/**
 * @description：发现页面--应用中心专属webView
 * @author： kerryg
 * @date:2015年12月3日 
 */
public class WebViewsFindActivity  extends Activity{

    private String url;//跳转url
    private String service_type_ids;//服务大类集合
    private String title_name;//大类标题
    private Button bt_button;//咨询+购买按钮
    /**
     * webView标题栏控件初始化
     */
    private ImageView iv_person_left;
    private TextView tv_person_title;
    private ImageView iv_person_close;
    private RelativeLayout rl_button;
    private ProgressBar mProgressBar; //webView进度条
    //右边菜单
    private ImageView iv_menu;
    private PopupMenu popupMenu;
    private View layout_mask;
    private String titles;//页面title

    /**
     * webView初始化
     */
    private WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.webview_find_activity);
        super.onCreate(savedInstanceState);
        findView();
        init();
    }
    private void findView(){
        url = getIntent().getStringExtra("url");
        service_type_ids = getIntent().getStringExtra("service_type_ids");
        title_name = getIntent().getStringExtra("title_name");

        iv_person_left = (ImageView) findViewById(R.id.iv_person_left);
        iv_person_close = (ImageView)findViewById(R.id.iv_person_close);
        tv_person_title = (TextView)findViewById(R.id.tv_person_title);
        rl_button = (RelativeLayout)findViewById(R.id.rl_button);
        bt_button =(Button)findViewById(R.id.bt_button);
        mProgressBar = (ProgressBar)findViewById(R.id.myProgressBar);
        if(!StringUtils.isEmpty(service_type_ids)){
            rl_button.setVisibility(View.VISIBLE);
        }
        webview = (WebView)findViewById(R.id.webview);
        iv_menu = (ImageView) findViewById(R.id.iv_person_more);
        layout_mask = findViewById(R.id.layout_mask);
        popupMenu = new PopupMenu(this);
       
    }
    @SuppressLint({ "JavascriptInterface", "NewApi" })
    private void init() {
        if (StringUtils.isEmpty(url)) {
            Toast.makeText(getApplicationContext(), "数据错误", 0).show();
            return;
        }
        WebChromeClient wvcc = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                titles = title;
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
        // 设置WebChromeClinent对象
        webview.setWebChromeClient(wvcc);
        webview.loadUrl(url);
        WebSettings webSettings = webview.getSettings();
        webview.addJavascriptInterface(this, "Koolearn");
        webview.setBackgroundColor(Color.parseColor("#00000000"));
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);// 设置js可以直接打开窗口，如window.open()，默认为false
        webview.getSettings().setJavaScriptEnabled(true);// 是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        webview.getSettings().setSupportZoom(true);// 是否可以缩放，默认true
        // popwindow显示webview不能设置缩放按钮，否则触屏就会报错。
        // webview.getSettings().setBuiltInZoomControls(true);// 是否显示缩放按钮，默认false
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

        iv_person_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_person_left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webview != null && webview.canGoBack()) {
                    webview.goBack();
                } else {
                    finish();
                }
            }
        });
        bt_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WebViewsFindActivity.this,Find2DetailActivity.class);
                intent.putExtra("service_type_ids", service_type_ids);
                intent.putExtra("title_name",title_name);
                startActivity(intent);
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
                            ShareConfig.getInstance().inits(WebViewsFindActivity.this,url,titles);
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
