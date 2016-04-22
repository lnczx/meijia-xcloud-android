package com.meijialife.simi.activity;

import java.util.Stack;

import com.meijialife.simi.R;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 积分
 *
 * @author RUI
 *
 */
@SuppressLint("NewApi")
public class PointsShopActivity extends Activity {
    private static String ua;
    private static Stack<PointsShopActivity> activityStack;
    public static final String VERSION="1.0.7";
    public static CreditsListener creditsListener;

    public interface CreditsListener{
        /**
         * 当点击分享按钮被点击
         * @param shareUrl 分享的地址
         * @param shareThumbnail 分享的缩略图
         * @param shareTitle 分享的标题
         * @param shareSubtitle 分享的副标题
         */
        public void onShareClick(WebView webView, String shareUrl,String shareThumbnail, String shareTitle,  String shareSubtitle);

        /**
         * 当点击登录
         * @param webView 用于登录成功后返回到当前的webview并刷新。
         * @param currentUrl 当前页面的url
         */
        public void onLoginClick(WebView webView,String currentUrl);

        /**
         * 当点复制券码时
         * @param webView webview对象。
         * @param code 复制的券码
         */
        public void onCopyCode(WebView mWebView, String code);

        /**
         * 通知本地，刷新积分
         * @param mWebView
         * @param credits
         */
        public void onLocalRefresh(WebView mWebView, String credits);
    }

    protected String url;
    protected String shareUrl;          //分享的url
    protected String shareThumbnail;    //分享的缩略图
    protected String shareTitle;        //分享的标题
    protected String shareSubtitle;     //分享的副标题
    protected Boolean ifRefresh = false;
    protected Boolean delayRefresh = false;
    protected String navColor;
    protected String titleColor;
    protected Long shareColor;
    
    protected WebView mWebView;
    protected LinearLayout mLinearLayout;
    protected RelativeLayout mNavigationBar;
    protected TextView mTitle;
    protected ImageView mBackView;
    protected TextView mShare;

    private int RequestCode=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 锁定竖屏显示
        url = getIntent().getStringExtra("url");
        if (url == null) {
            throw new RuntimeException("url can't be blank");
        }

        // 管理匿名类栈，用于模拟原生应用的页面跳转。
        if (activityStack == null) {
            activityStack = new Stack<PointsShopActivity>();
        }
        activityStack.push(this);

        // 配置导航条文本颜色
        titleColor = getIntent().getStringExtra("titleColor");
        String titleColorTemp = "0xff" + titleColor.substring(1, titleColor.length());
        Long titlel = Long.parseLong(titleColorTemp.substring(2), 16);
        // 配置分享文案颜色,同title
        shareColor = titlel;
        // 配置导航栏背景颜色
        navColor = getIntent().getStringExtra("navColor");
        String navColorTemp = "0xff" + navColor.substring(1, navColor.length());
        Long navl = Long.parseLong(navColorTemp.substring(2), 16);
        // 初始化页面
        initView();
        setContentView(mLinearLayout);
        
        // api11以上的系统隐藏系统默认的ActionBar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
        }

        mTitle.setTextColor(titlel.intValue());
        mTitle.setTextColor(this.getResources().getColor(R.color.title_text_color));
        //mNavigationBar.setBackgroundColor(navl.intValue());
        mNavigationBar.setBackgroundColor(this.getResources().getColor(R.color.simi_color_white));
        mBackView.setPadding(10, 10, 10, 10);
        mBackView.setClickable(true);

        // 添加后退按钮监听事件
        mBackView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackClick();
            }
        });
        // 添加分享按钮的监听事件
        if (mShare != null) {
            mShare.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (creditsListener != null) {
                        creditsListener.onShareClick(mWebView, shareUrl, shareThumbnail, shareTitle, shareSubtitle);
                    }
                }
            });
        }

        //js调java代码接口。
        mWebView.addJavascriptInterface(new Object(){
//          //用于回传分享url和title。（V1.05之后改用url拦截方式处理。）
//            @JavascriptInterface
//            public void shareInfo(String content) {
//                if(content!=null){
//                    String[] dd=content.split("\\|");
//                    if(dd.length==4){
//                        setShareInfo(dd[0],dd[1],dd[2],dd[3]);
//                        mShare.setVisibility(View.VISIBLE);
//                    }
//                }
//            }
            
            //用于跳转用户登录页面事件。
            @JavascriptInterface
            public void login(){
                if(creditsListener!=null){
                    mWebView.post(new Runnable() {
                        @Override
                        public void run() {
                            creditsListener.onLoginClick(mWebView, mWebView.getUrl());
                        }
                    });
                }
            }
            
            //复制券码
            @JavascriptInterface
            public void copyCode(final String code){
                if(creditsListener!=null){
                    mWebView.post(new Runnable() {
                        @Override
                        public void run() {
                            creditsListener.onCopyCode(mWebView, code);
                        }
                    });
                }
            }
            
            //客户端本地触发刷新积分。
            @JavascriptInterface
            public void localRefresh(final String credits){
                if(creditsListener!=null){
                    mWebView.post(new Runnable() {
                        @Override
                        public void run() {
                            creditsListener.onLocalRefresh(mWebView, credits);
                        }
                    });
                }
            }
            
        },"duiba_app");
        
        if (ua == null) {
            ua = mWebView.getSettings().getUserAgentString() + " Duiba/" + VERSION;
        }
        mWebView.getSettings().setUserAgentString(ua);

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                PointsShopActivity.this.onReceivedTitle(view, title);
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return shouldOverrideUrlByDuiba(view, url);
            }
        });

        mWebView.loadUrl(url);
    }
    
    protected void onBackClick(){
        Intent intent=new Intent();
        setResult(99,intent);
        finishActivity(this);
    }

    // 初始化页面
    protected void initView() {
        mLinearLayout = new LinearLayout(this);
        mLinearLayout.setBackgroundColor(Color.GRAY);
        mLinearLayout.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        mLinearLayout.setOrientation(LinearLayout.VERTICAL);

        int height50dp = dip2px(this, 50);
        // 自定义导航栏
        initNavigationBar();

        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, height50dp);

        mLinearLayout.addView(mNavigationBar, mLayoutParams);
        // 初始化WebView配置
        initWebView();

        mLinearLayout.addView(mWebView);

    }
    
  //自定义导航栏，包含 后退按钮，页面标题，分享按钮（默认隐藏）
    protected void initNavigationBar(){
        int dp200 = dip2px(this,200);
        int dp100 = dip2px(this,100);
        int dp30 = dip2px(this,30);
        int dp20 = dip2px(this,20);
        int dp10 = dip2px(this,10);
        int dp15 = dip2px(this,15);
        
        mNavigationBar=new RelativeLayout(this);
        mNavigationBar.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT,dp20));

        mTitle=new TextView(this);
        mTitle.setMaxWidth(dp200);
        mTitle.setLines(1);
        mTitle.setTextSize(20.0f);
        mNavigationBar.addView(mTitle);
        android.widget.RelativeLayout.LayoutParams lp=(android.widget.RelativeLayout.LayoutParams)mTitle.getLayoutParams();
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        
        mBackView = new ImageView(this);
        mBackView.setImageDrawable(getResources().getDrawable(R.drawable.title_left_back));
        
        //mBackView.setImageResource(R.drawable.title_left_back);
        RelativeLayout.LayoutParams mBackLayout=new RelativeLayout.LayoutParams(dp30,dp30);
        mBackLayout.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        mBackLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT); 
        mBackLayout.setMargins(dp20, 0, 0, 0);
        mNavigationBar.addView(mBackView,mBackLayout);
        
        //在导航栏的右侧添加分享按钮（无分享信息的页面隐藏）
        mShare=new TextView(this);
        mShare.setLines(1);
        mShare.setTextSize(20.0f);
        mShare.setText("分享");
        mShare.setPadding(0, 0, dp10, 0);
        mShare.setTextColor(shareColor.intValue());
        mNavigationBar.addView(mShare);
        android.widget.RelativeLayout.LayoutParams shareLp=(android.widget.RelativeLayout.LayoutParams)mShare.getLayoutParams();
        shareLp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        shareLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        //设置为默认不显示
        mShare.setVisibility(View.INVISIBLE);
        mShare.setClickable(false);
    }

    //初始化WebView配置
    protected void initWebView(){
        mWebView=new WebView(this);
        mWebView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        WebSettings settings = mWebView.getSettings();

        // User settings
        settings.setJavaScriptEnabled(true);    //设置webview支持javascript
        settings.setLoadsImagesAutomatically(true); //支持自动加载图片
        settings.setUseWideViewPort(true);  //设置webview推荐使用的窗口，使html界面自适应屏幕
        settings.setLoadWithOverviewMode(true); 
        settings.setSaveFormData(true); //设置webview保存表单数据
        settings.setSavePassword(true); //设置webview保存密码
        settings.setDefaultZoom(ZoomDensity.MEDIUM);    //设置中等像素密度，medium=160dpi
        settings.setSupportZoom(true);  //支持缩放

        CookieManager.getInstance().setAcceptCookie(true);

        if (Build.VERSION.SDK_INT > 8) {
            settings.setPluginState(PluginState.ON_DEMAND);
        }

        // Technical settings
        settings.setSupportMultipleWindows(true);
        mWebView.setLongClickable(true);
        mWebView.setScrollbarFadingEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setDrawingCacheEnabled(true);

        settings.setAppCacheEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
    }

    protected void onReceivedTitle(WebView view,String title){
        mTitle.setText(title);
    }

    /**
     * 拦截url请求，根据url结尾执行相应的动作。 （重要）
     * 用途：模仿原生应用体验，管理页面历史栈。
     * @param view
     * @param url
     * @return
     */
    protected boolean shouldOverrideUrlByDuiba(WebView view, String url) {
        Uri uri = Uri.parse(url);
        if (this.url.equals(url)) {
            view.loadUrl(url);
            return true;
        }
        // 处理电话链接，启动本地通话应用。
        if (url.startsWith("tel:")) {
            Intent intent = new Intent(Intent.ACTION_DIAL, uri);
            startActivity(intent);
            return true;
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return false;
        }
        // 截获页面分享请求，分享数据
        if ("/client/dbshare".equals(uri.getPath())) {
            String content = uri.getQueryParameter("content");
            if (creditsListener != null && content != null) {
                String[] dd = content.split("\\|");
                if (dd.length == 4) {
                    setShareInfo(dd[0], dd[1], dd[2], dd[3]);
                    mShare.setVisibility(View.VISIBLE);
                    mShare.setClickable(true);
                }
            }
            return true;
        }
        // 截获页面唤起登录请求。（目前暂时还是用js回调的方式，这里仅作预留。）
        if ("/client/dblogin".equals(uri.getPath())) {
            if (creditsListener != null) {
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        creditsListener.onLoginClick(mWebView, mWebView.getUrl());
                    }
                });
            }
            return true;
        }
        if (url.contains("dbnewopen")) { // 新开页面
            Intent intent = new Intent();
            intent.setClass(PointsShopActivity.this, PointsShopActivity.this.getClass());
            intent.putExtra("navColor", navColor);
            intent.putExtra("titleColor", titleColor);
            url = url.replace("dbnewopen", "none");
            intent.putExtra("url", url);
            startActivityForResult(intent, RequestCode);
        } else if (url.contains("dbbackrefresh")) { // 后退并刷新
            url = url.replace("dbbackrefresh", "none");
            Intent intent = new Intent();
            intent.putExtra("url", url);
            intent.putExtra("navColor", navColor);
            intent.putExtra("titleColor", titleColor);
            setResult(RequestCode, intent);
            finishActivity(this);
        } else if (url.contains("dbbackrootrefresh")) { // 回到积分商城首页并刷新
            url = url.replace("dbbackrootrefresh", "none");
            if (activityStack.size() == 1) {
                finishActivity(this);
            } else {
                activityStack.get(0).ifRefresh = true;
                finishUpActivity();
            }
        } else if (url.contains("dbbackroot")) { // 回到积分商城首页
            url = url.replace("dbbackroot", "none");
            if (activityStack.size() == 1) {
                finishActivity(this);
            } else {
                finishUpActivity();
            }
        } else if (url.contains("dbback")) { // 后退
            url = url.replace("dbback", "none");
            finishActivity(this);
        } else {
            if (url.endsWith(".apk") || url.contains(".apk?")) { // 支持应用链接下载
                Intent viewIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(viewIntent);
                return true;
            }
            if (url.contains("autologin") && activityStack.size() > 1) { // 未登录用户登录后返回，所有历史页面置为待刷新
                // 将所有已开Activity设置为onResume时刷新页面。
                setAllActivityDelayRefresh();
            }
            view.loadUrl(url);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(resultCode==100){
            if(intent.getStringExtra("url")!=null){
                this.url=intent.getStringExtra("url");
                mWebView.loadUrl(this.url);
                ifRefresh = false;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ifRefresh) {
            this.url = getIntent().getStringExtra("url");
            mWebView.loadUrl(this.url);
            ifRefresh = false;
        } else if (delayRefresh) {
            mWebView.reload();
            delayRefresh = false;
        } else {
            // 返回页面时，如果页面含有onDBNewOpenBack()方法,则调用该js方法。
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mWebView.evaluateJavascript("if(window.onDBNewOpenBack){onDBNewOpenBack()}", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) { }
                });
            } else {
                mWebView.loadUrl("javascript:if(window.onDBNewOpenBack){onDBNewOpenBack()}");
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            onBackClick();
            return true;
        }else{
            return super.onKeyDown(keyCode, event);
        }
    }
    
    //--------------------------------------------以下为工具方法----------------------------------------------

    /**
     * 配置分享信息
     */
    protected void setShareInfo(String shareUrl,String shareThumbnail,String shareTitle,String shareSubtitle){
        this.shareUrl = shareUrl;
        this.shareThumbnail = shareThumbnail;
        this.shareSubtitle = shareSubtitle;
        this.shareTitle = shareTitle;
    }
    
    /**
     * 结束除了最底部一个以外的所有Activity
     */
    public void finishUpActivity() {
        int size = activityStack.size();
        for (int i = 0;i < size-1; i++) {
            activityStack.pop().finish();
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
        }
    }
    
    /**
     * 设置栈内所有Activity为返回待刷新。
     * 作用：未登录用户唤起登录后，将所有栈内的Activity设置为onResume时刷新页面。
     */
    public void setAllActivityDelayRefresh(){
        int size = activityStack.size();
        for (int i = 0;i < size; i++) {
            if(activityStack.get(i)!=this){
                activityStack.get(i).delayRefresh = true;
            }
        }
    }
    
    /** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  

}
