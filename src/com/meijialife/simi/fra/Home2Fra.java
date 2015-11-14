package com.meijialife.simi.fra;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.utils.StringUtils;

/**
 * 发现
 * @author RUI
 *
 */
public class Home2Fra extends Fragment implements OnClickListener {

	private WebView webview;
	
	private static final String URL_1 = "http://51xingzheng.cn/shop/shop-cat-fuwu.html";
	private static final String URL_2 = "http://51xingzheng.cn/shop/shop-cat-shiwu.html";
	private static final String URL_3 = "http://51xingzheng.cn/shop/shop-cat-peixun.html";
	private static final String URL_4 = "http://s.click.taobao.com/t?e=m%3D2%26s%3DcirY%2FJeVTuUcQipKwQzePDAVflQIoZepK7Vc7tFgwiFRAdhuF14FMUOY7BkY%2Fmh1lovu%2FCElQOv5VWKsCTst%2Bl%2Fh3Bzd%2B3corM0I4mel6fJLYcTC6xWsK7Mio2xy9pekxgxdTc00KD8%3D";
//	private static final String URL_4 = "http://s.click.taobao.com/x0Kmfjx";
//	private static final String URL_4 = "http://51xingzheng.cn/shop/shop-cat-qita.html";
	
	private RadioGroup radiogroup;
	private View line_1, line_2, line_3, line_4;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.index_2, null);
//		 if(this.getResources().getConfiguration().orientation ==Configuration.ORIENTATION_LANDSCAPE){
//	            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//	         }
		
		init(v);
		initTab(v);
		initWebView(URL_1);
		return v;
	}
	
	private void init(View v){
	    webview = (WebView) v.findViewById(R.id.webview);
	}
	
	@SuppressLint({ "JavascriptInterface", "NewApi" })
	private void initWebView(String url) {
		if(StringUtils.isEmpty(url)){
			Toast.makeText(getActivity(), "数据错误", 0).show();
			return;
		}
		
		webview.loadUrl(url);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webview.setBackgroundColor(Color.parseColor("#00000000"));
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        webview.getSettings().setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        webview.getSettings().setSupportZoom(true);//是否可以缩放，默认true
        webview.getSettings().setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
        webview.getSettings().setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
        webview.getSettings().setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
        webview.getSettings().setAppCacheEnabled(false);//是否使用缓存
        webview.getSettings().setDomStorageEnabled(true);//DOM Storage
        // displayWebview.getSettings().setUserAgentString("User-Agent:Android");//设置用户代理，一般不用
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
		
	}
	
	private void initTab(View v){
	    radiogroup = (RadioGroup)v.findViewById(R.id.radiogroup);
	    line_1 = (View)v.findViewById(R.id.line_1);
	    line_2 = (View)v.findViewById(R.id.line_2);
	    line_3 = (View)v.findViewById(R.id.line_3);
	    line_4 = (View)v.findViewById(R.id.line_4);

	    radiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(RadioGroup grop, int checkedId) {
                line_1.setVisibility(View.INVISIBLE);
                line_2.setVisibility(View.INVISIBLE);
                line_3.setVisibility(View.INVISIBLE);
                line_4.setVisibility(View.INVISIBLE);
                
                if(checkedId == grop.getChildAt(0).getId()){
                    line_1.setVisibility(View.VISIBLE);
                    initWebView(URL_1);
                }
                if(checkedId == grop.getChildAt(1).getId()){
                    line_2.setVisibility(View.VISIBLE);
                    initWebView(URL_2);
                }
                if(checkedId == grop.getChildAt(2).getId()){
                    line_3.setVisibility(View.VISIBLE);
                    initWebView(URL_3);
                }
                if(checkedId == grop.getChildAt(3).getId()){
                    line_4.setVisibility(View.VISIBLE);
                    initWebView(URL_4);
                }
                
            }
        });
	    
	    radiogroup.getChildAt(0).performClick();
	}

	@Override
	public void onClick(View v) {
	}

}
