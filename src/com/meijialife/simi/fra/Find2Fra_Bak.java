package com.meijialife.simi.fra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.BaseFragment;
import com.meijialife.simi.Constants;
import com.meijialife.simi.MainActivity;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.PartnerActivity;
import com.meijialife.simi.activity.SearchViewActivity;
import com.meijialife.simi.adapter.SecretaryAdapter;
import com.meijialife.simi.bean.Partner;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * @description：发现--秘书助理，综合服务，设计策划
 * @author： kerryg
 * @date:2015年11月13日 
 */
public class Find2Fra_Bak extends BaseFragment {

    private RadioGroup radiogroup;
    private View line_1, line_2, line_3, line_4;
    private MainActivity activity;
    private ListView listview_1;
    private SecretaryAdapter adapter;//服务商适配器
    private ArrayList<Partner> partnerList; // 所有服务商--秘书列表
    private RelativeLayout rl_total_search;//搜索框

//    private static final String URL_1 = "http://eqxiu.com/s/7r1oOxJd";//办公用品链接
    private static final String URL_1 = "http://s.click.taobao.com/x0Kmfjx";//办公用品链接
    private WebView webview;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.find_2_fra, null);
        initTab(v);
        init(v);
        return v;
    }
   
    /*
     * 初始化适配器
     */
    public void init(View v) {
        rl_total_search = (RelativeLayout)v.findViewById(R.id.rl_total_search);
        listview_1 = (ListView) v.findViewById(R.id.partner_list_view);
        adapter = new SecretaryAdapter(activity);
        listview_1.setAdapter(adapter);
        listview_1.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Partner partner = partnerList.get(position);
                Intent intent = new Intent(getActivity(),PartnerActivity.class);
                intent.putExtra("Partner",partnerList.get(position));
                startActivity(intent);
                
            }
        });
        
        rl_total_search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),SearchViewActivity.class));
            }
        });
        
    }
    public Find2Fra_Bak() {
        super();
    }
    public Find2Fra_Bak(MainActivity activity) {
        super();
        this.activity = activity;
    }
    @SuppressLint({ "JavascriptInterface", "NewApi" })
    private void initWebView(String url) {
        if (StringUtils.isEmpty(url)) {
            Toast.makeText(getActivity(), "数据错误", 0).show();
            return;
        }

        webview.loadUrl(url);
        WebSettings webSettings = webview.getSettings();
        //webSettings.setJavaScriptEnabled(true);
        webview.setBackgroundColor(Color.parseColor("#00000000"));
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);// 设置js可以直接打开窗口，如window.open()，默认为false
        webview.getSettings().setJavaScriptEnabled(true);// 是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        webview.getSettings().setSupportZoom(true);// 是否可以缩放，默认true
//        webview.getSettings().setBuiltInZoomControls(true);// 是否显示缩放按钮，默认false
        webview.getSettings().setUseWideViewPort(true);// 设置此属性，可任意比例缩放。大视图模式
        webview.getSettings().setLoadWithOverviewMode(true);// 和setUseWideViewPort(true)一起解决网页自适应问题
        webview.getSettings().setAppCacheEnabled(false);// 是否使用缓存
        webview.getSettings().setDomStorageEnabled(true);// DOM Storage
        // displayWebview.getSettings().setUserAgentString("User-Agent:Android");//设置用户代理，一般不用
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
   
        webview.getSettings().setAllowFileAccess(true);
        webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setSavePassword(false);
        webview.getSettings().setSaveFormData(false);
        
        
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                //view.loadData(url, "text/html", "utf-8");
                return true;
            }
        });

    }
    //初始化四个选项（秘书助理，综合服务，设计策划，办公用品）
    private void initTab( View v) {
        webview = (WebView) v.findViewById(R.id.partner_web_view);
        radiogroup = (RadioGroup) v.findViewById(R.id.radiogroup);
        line_1 = (View) v.findViewById(R.id.line_1);
        line_2 = (View) v.findViewById(R.id.line_2);
        line_3 = (View) v.findViewById(R.id.line_3);    
        line_4 = (View) v.findViewById(R.id.line_4);
        radiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup grop, int checkedId) {
                line_1.setVisibility(View.INVISIBLE);
                line_2.setVisibility(View.INVISIBLE);
                line_3.setVisibility(View.INVISIBLE);
                line_4.setVisibility(View.INVISIBLE);
                if (checkedId == grop.getChildAt(0).getId()) {// 秘书助理
                    line_1.setVisibility(View.VISIBLE);
                    webview.setVisibility(View.GONE);
                    getPartnerList("75,180");
                }
                if (checkedId == grop.getChildAt(1).getId()) {// 综合服务
                    line_2.setVisibility(View.VISIBLE);
                    webview.setVisibility(View.GONE);
                    getPartnerList("1,16,33,42,52,61,189,191");
                }
                if (checkedId == grop.getChildAt(2).getId()) {//设计策划
                    line_3.setVisibility(View.VISIBLE);
                    webview.setVisibility(View.GONE);
                    getPartnerList("67,79");
                   
                }
                //webview展示网页
                if (checkedId == grop.getChildAt(3).getId()) {//办公用品
                    line_4.setVisibility(View.VISIBLE);
                    webview.setVisibility(View.VISIBLE);
                    initWebView(URL_1);
                   
                }

            }
        });
        radiogroup.getChildAt(0).performClick();
    }
    
    public void getPartnerList(String service_type_ids) {
        String user_id = DBHelper.getUser(activity).getId();
        if (!NetworkUtils.isNetworkConnected(activity)) {
            Toast.makeText(activity, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id + "");
        map.put("page", "0");
        map.put("service_type_ids", service_type_ids);
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().get(Constants.URL_GET_USER_LIST, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(activity, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                dismissDialog();
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            if (StringUtils.isNotEmpty(data)) {
                                Gson gson = new Gson();
                                partnerList = gson.fromJson(data, new TypeToken<ArrayList<Partner>>() {
                                }.getType());
                                adapter.setData(partnerList);
                            } else {
                                adapter.setData(new ArrayList<Partner>());
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
                // 操作失败，显示错误信息
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    UIUtils.showToast(activity, errorMsg);
                }
            }
        });
    }
}
