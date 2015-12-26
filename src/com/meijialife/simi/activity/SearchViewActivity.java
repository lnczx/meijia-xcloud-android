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
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.SecretaryAdapter;
import com.meijialife.simi.bean.Partner;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.ui.TagGroup;
import com.meijialife.simi.ui.TagGroup.OnTagClickListener;
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

        partner_list_view = (ListView) findViewById(R.id.partner_list_view);
        adapter = new SecretaryAdapter(this);
        partner_list_view.setAdapter(adapter);
        // 列表增加点击事件
        partner_list_view.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Partner partner = partnerList.get(position);
                Intent intent = new Intent(SearchViewActivity.this, PartnerActivity.class);
                intent.putExtra("Partner", partnerList.get(position));
                startActivity(intent);

            }
        });
        getHotKwList();
        // 搜索增加点击事件
        tv_search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                String kw = et_search_kw.getText().toString();
                if (!StringUtils.isEmpty(kw)) {
                    searchPartnerByKw(kw);
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
                    searchPartnerByKw(tag);
                }
            }
        });
    }

    /**
     * 根据关键字搜索服务商
     * 
     * @param service_type_ids
     */
    public void searchPartnerByKw(String kw) {
        String user_id = DBHelper.getUser(SearchViewActivity.this).getId();
        if (!NetworkUtils.isNetworkConnected(SearchViewActivity.this)) {
            Toast.makeText(SearchViewActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id + "");
        map.put("page", "0");
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
