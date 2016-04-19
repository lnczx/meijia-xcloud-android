package com.meijialife.simi.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.GeolocationPermissions.Callback;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.meijialife.simi.R;
import com.meijialife.simi.utils.StringUtils;


/**
 * @description：工具箱--更多--应用中心专属webView
 * @author： kerryg
 * @date:2015年12月3日 
 */
public class WebViewsActivity  extends Activity{

    private String url;

    private WebView webview;
    private ImageView iv_person_left;
    private TextView tv_person_title;
    private ImageView iv_person_close;
    private ProgressBar mProgressBar; //webView进度条

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.webview_personal_fragment);
        super.onCreate(savedInstanceState);

        init();
    }

    @SuppressLint({ "JavascriptInterface", "NewApi", "SetJavaScriptEnabled" })
    private void init() {
        url = getIntent().getStringExtra("url");

        iv_person_left = (ImageView) findViewById(R.id.iv_person_left);
        iv_person_close = (ImageView)findViewById(R.id.iv_person_close);
        tv_person_title = (TextView)findViewById(R.id.tv_person_title);
        mProgressBar = (ProgressBar)findViewById(R.id.myProgressBar);
        webview = (WebView)findViewById(R.id.webview);

        if (StringUtils.isEmpty(url)) {
            Toast.makeText(getApplicationContext(), "数据错误", 0).show();
            return;
        }

        WebChromeClient wvcc = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
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
            
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, Callback callback) {
                super.onGeolocationPermissionsShowPrompt(origin, callback);
                callback.invoke(origin, true,false);
            }
            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
            }
            
        };
        // 设置WebChromeClinent对象

        webview.setWebChromeClient(wvcc);
       /* webview.requestFocusFromTouch();
        webview.requestFocus(View.FOCUS_DOWN);*/

        WebSettings webSettings = webview.getSettings();
        webview.addJavascriptInterface(this, "Koolearn");
        webview.setBackgroundColor(Color.parseColor("#00000000"));
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);// 设置js可以直接打开窗口，如window.open()，默认为false
        webSettings.setJavaScriptEnabled(true);// 是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        webSettings.setSupportZoom(true);// 是否可以缩放，默认true
        // popwindow显示webview不能设置缩放按钮，否则触屏就会报错。
        // webview.getSettings().setBuiltInZoomControls(true);// 是否显示缩放按钮，默认false
        webSettings.setUseWideViewPort(true);// 设置此属性，可任意比例缩放。大视图模式
        webSettings.setLoadWithOverviewMode(true);// 和setUseWideViewPort(true)一起解决网页自适应问题
        webSettings.setAppCacheEnabled(false);// 是否使用缓存
        webSettings.setDomStorageEnabled(true);// DOM Storage
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        //webview设置启用数据库
        webSettings.setDatabaseEnabled(true);
        //设置定位的数据库路径
        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE)
                .getPath();
        webSettings.setGeolocationDatabasePath(dir);
        //启用地理定位
        webSettings.setGeolocationEnabled(true);
        //开启DomStorage缓存
        webSettings.setDomStorageEnabled(true);
        
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
        
        webview.loadUrl(url);
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

}
