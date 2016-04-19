package com.meijialife.simi.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.ProgressBar;
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
 * 网页专用Activity
 * 
 */
public class WebViewActivity extends Activity implements OnClickListener {

    private WebView webview;
    private ImageView iv_person_left;
    private TextView tv_person_title;
    private ImageView iv_person_close;

    private String url;
    private String titleStr;
    
    private ProgressBar mProgressBar; //webView进度条
    
    //右边菜单
    private ImageView iv_menu;
    private PopupMenu popupMenu;
    private View layout_mask;
    private String titles;//页面title


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
        
        iv_person_left = (ImageView) findViewById(R.id.iv_person_left);
        iv_person_close = (ImageView) findViewById(R.id.iv_person_close);
        tv_person_title = (TextView) findViewById(R.id.tv_person_title);
        iv_person_left.setOnClickListener(this);
        iv_person_close.setOnClickListener(this);
        iv_menu = (ImageView) findViewById(R.id.iv_person_more);
        layout_mask = findViewById(R.id.layout_mask);
        popupMenu = new PopupMenu(this);
        
        mProgressBar = (ProgressBar)findViewById(R.id.myProgressBar);
     

        if (StringUtils.isEmpty(url)) {
            Toast.makeText(getApplicationContext(), "数据错误", 0).show();
            return;
        }
        //获取页面中的title
        WebChromeClient wvcc = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
               titles =title;
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
                            ShareConfig.getInstance().inits(WebViewActivity.this,url,titles);
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
