package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.SecretaryAdapter;
import com.meijialife.simi.bean.Partner;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.ui.TagGroup;
import com.meijialife.simi.ui.TagGroup.OnTagClickListener;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * @description：发现---实现搜索列表展现和热搜词搜索功能
 * @author： kerryg
 * @date:2015年12月5日
 */
public class SearchViewActivity extends BaseActivity {

    private TextView tv_search;// 搜索按钮
    private EditText et_search_kw;// 编辑框
    private ListView partner_list_view;// 显示搜索的列表
    private TagGroup tg;// 显示热搜标签

    private ArrayList<Partner> partnerList; // 所有服务商--秘书列表
    private ArrayList<String> hotKwList; // 热搜词列表
    private SecretaryAdapter adapter;// 服务商适配器

    private ArrayList<Partner> myPartnerList;
    private ArrayList<Partner> totalPartnerList;
    private PullToRefreshListView mPullRefreshListView;//上拉刷新的控件 
    public static String key="";
    
    
    private int page = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.search_view_activity);
        super.onCreate(savedInstanceState);

        initView();
    }

    private void initView() {
        tv_search = (TextView) findViewById(R.id.tv_search);
        et_search_kw = (EditText) findViewById(R.id.et_search_kw);
        tg = (TagGroup) findViewById(R.id.ll_user_tags);

        getHotKwList();
        initSearchView();
        // 搜索增加点击事件
        tv_search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                String kw = et_search_kw.getText().toString();
                if (!StringUtils.isEmpty(kw)) {
                    searchPartnerByKw(kw,page);
                } else {
                    et_search_kw.setHint("请输入搜索内容");
                    return;
                }
            }
        });
        // 热搜标签增加点击事件
        tg.setOnTagClickListener(new OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                if (!StringUtils.isEmpty(tag)) {
                    // 如果输入法打开则关闭，如果没有打开则打开
                    InputMethodManager m = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    key = tag;
                    searchPartnerByKw(tag,page);
                }
            }
        });
    }
    private void initSearchView(){
        totalPartnerList = new ArrayList<Partner>();
        myPartnerList = new ArrayList<Partner>();
        mPullRefreshListView = (PullToRefreshListView)findViewById(R.id.pull_refresh_search_list);
        adapter = new SecretaryAdapter(this);
        mPullRefreshListView.setAdapter(adapter);
        mPullRefreshListView.setMode(Mode.BOTH);
        initIndicator();
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                page = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                searchPartnerByKw(key,page);
                adapter.notifyDataSetChanged(); 
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if(myPartnerList!=null && myPartnerList.size()>=10){
                    page = page+1;
                    searchPartnerByKw(key,page);
                    adapter.notifyDataSetChanged(); 
                }else {
                    Toast.makeText(SearchViewActivity.this,"请稍后，没有更多加载数据",Toast.LENGTH_SHORT).show();
                    mPullRefreshListView.onRefreshComplete(); 
                }
            }
        });
        mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchViewActivity.this, PartnerActivity.class);
                intent.putExtra("Partner", myPartnerList.get(position));
                startActivity(intent);          
            }
        });
    }
    /**
     * 设置下拉刷新提示
     */
    private void initIndicator()  
    {  
        ILoadingLayout startLabels = mPullRefreshListView  
                .getLoadingLayoutProxy(true, false);  
        startLabels.setPullLabel("下拉刷新");// 刚下拉时，显示的提示  
        startLabels.setRefreshingLabel("正在刷新...");// 刷新时  
        startLabels.setReleaseLabel("释放更新");// 下来达到一定距离时，显示的提示  
  
        ILoadingLayout endLabels = mPullRefreshListView.getLoadingLayoutProxy(  
                false, true);  
        endLabels.setPullLabel("上拉加载");
        endLabels.setRefreshingLabel("正在刷新...");// 刷新时  
        endLabels.setReleaseLabel("释放加载");// 下来达到一定距离时，显示的提示  
    }
    /**
     * 根据关键字搜索服务商
     * 
     * @param service_type_ids
     */
    public void searchPartnerByKw(String kw,int page) {
        String user_id = DBHelper.getUser(SearchViewActivity.this).getId();
        if (!NetworkUtils.isNetworkConnected(SearchViewActivity.this)) {
            Toast.makeText(SearchViewActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id + "");
        map.put("page", page+"");
        map.put("keyword", kw);
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().get(Constants.URL_GET_PARTNER_LIST_BY_KW, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(SearchViewActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                myPartnerList = gson.fromJson(data, new TypeToken<ArrayList<Partner>>() {
                                }.getType());
//                                adapter.setData(partnerList);
                                showData(myPartnerList);
                            } else {
//                                adapter.setData(new ArrayList<Partner>());
                                showData(new ArrayList<Partner>());
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
                    mPullRefreshListView.onRefreshComplete();
                    errorMsg = getString(R.string.servers_error);
                }
                // 操作失败，显示错误信息
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    mPullRefreshListView.onRefreshComplete();
                    UIUtils.showToast(SearchViewActivity.this, errorMsg);
                }
            }
        });
    }

    /**
     * 获得热搜字段列表接口
     */
    public void getHotKwList() {
        if (!NetworkUtils.isNetworkConnected(SearchViewActivity.this)) {
            Toast.makeText(SearchViewActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().get(Constants.URL_GET_HOT_KW_LIST, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(SearchViewActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                hotKwList = gson.fromJson(data, new TypeToken<ArrayList<String>>() {
                                }.getType());
                                showHotKw(hotKwList);
                            } else {
                                showHotKw(new ArrayList<String>());
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
                    UIUtils.showToast(SearchViewActivity.this, errorMsg);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        page = 1;
        key="";
        myPartnerList = null;
        totalPartnerList= null;
    }
    /**
     * 处理数据加载的方法
     * @param list
     */
    private void showData(List<Partner> myPartnerList){
        if(myPartnerList!=null && myPartnerList.size()>0){
            if(page==1){
                totalPartnerList.clear();
            }
            for (Partner partner : myPartnerList) {
                totalPartnerList.add(partner);
            }
            //给适配器赋值
            adapter.setData(totalPartnerList);
        }
        mPullRefreshListView.onRefreshComplete();
    }
    /**
     * 显示所有的热搜标签
     * 
     * @param hotKwList
     */
    private void showHotKw(ArrayList<String> hotKwList) {
        List<String> userTags = new ArrayList<String>();
        for (Iterator iterator = hotKwList.iterator(); iterator.hasNext();) {
            String kw = (String) iterator.next();
            userTags.add(kw);
        }
        tg.setTags(userTags);
    }

}
