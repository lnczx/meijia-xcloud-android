package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.AppToolsAdapter;
import com.meijialife.simi.bean.AppToolsData;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * @description：应用中心
 * @author： kerryg
 * @date:2015年12月3日
 */
public class ApplicationsCenterActivity extends BaseActivity {

    private GridView gv_application1;
    private GridView gv_application2;
    private TextView tv_menu_type1;
    private TextView tv_menu_type2;

    private AppToolsAdapter appToolsAdapter1;
    private AppToolsAdapter appToolsAdapter2;

    private ArrayList<AppToolsData> t_menu_List;// t=工具与服务
    private ArrayList<AppToolsData> d_menu_List;// d=成长与赚钱
    private ArrayList<AppToolsData> appToolsDatas;// 所有数据
    
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.applications_center_activity);
        super.onCreate(savedInstanceState);
        initView();
    }
  
    private void initView() {
        setTitleName("应用中心");
        requestBackBtn();
        user = DBHelper.getUser(this);

        gv_application1 = (GridView) findViewById(R.id.gv_application1);
        gv_application2 = (GridView) findViewById(R.id.gv_application2);
        tv_menu_type1 = (TextView) findViewById(R.id.tv_menu_type1);
        tv_menu_type2 = (TextView) findViewById(R.id.tv_menu_type2);

        appToolsAdapter1 = new AppToolsAdapter(this);
        appToolsAdapter2 = new AppToolsAdapter(this);
        gv_application1.setAdapter(appToolsAdapter1);
        gv_application2.setAdapter(appToolsAdapter2);

        appToolsDatas = new ArrayList<AppToolsData>();
        t_menu_List = new ArrayList<AppToolsData>();
        d_menu_List = new ArrayList<AppToolsData>();

        setOnClick();
        getAppTools();
    }

    private void setOnClick() {
        gv_application1.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppToolsData appToolsData = t_menu_List.get(position);
                String open_type = appToolsData.getOpen_type().trim();
                String url = appToolsData.getUrl().trim();
                String auth_url = appToolsData.getAuth_url().trim();
                String name = appToolsData.getName().trim();
                if (open_type.equals("h5")) {
                    Intent intent = new Intent(ApplicationsCenterActivity.this, WebViewsActivity.class);
                    intent.putExtra("url", url+"?user_id="+user.getId());
                    startActivity(intent);
                } else if (open_type.equals("app")) {
                    if (name.equals("钱包")) {
                        startActivity(new Intent(ApplicationsCenterActivity.this, MyWalletActivity.class));
                    } else if (name.equals("优惠券")) {
                        startActivity(new Intent(ApplicationsCenterActivity.this, DiscountCardActivity.class));
                    } else if (name.equals("订单")) {
                        startActivity(new Intent(ApplicationsCenterActivity.this, MyOrderActivity.class));
                    } else if (name.equals("积分商城")) {
                        Intent intent6 = new Intent();
                        intent6.setClass(ApplicationsCenterActivity.this, PointsShopActivity.class);
                        intent6.putExtra("navColor", "#E8374A"); 
                        // 配置导航条的背景颜色，请用#ffffff长格式。
                        intent6.putExtra("titleColor", "#ffffff");
                        // 配置导航条标题的颜色，请用#ffffff长格式。
                        intent6.putExtra("url", Constants.URL_POST_SCORE_SHOP + "?user_id="
                                + DBHelper.getUserInfo(ApplicationsCenterActivity.this).getUser_id()); 
                        // 配置自动登陆地址，每次需服务端动态生成。
                        startActivity(intent6);
                    } 
                } else {
                    Intent intent = new Intent(ApplicationsCenterActivity.this, WebViewsActivity.class);
                    intent.putExtra("url", auth_url);
                    startActivity(intent);
                }
            }
        });
        gv_application2.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AppToolsData appToolsData = d_menu_List.get(position);
                String open_type = appToolsData.getOpen_type().trim();
                String url = appToolsData.getUrl().trim();
                String auth_url = appToolsData.getAuth_url().trim();
                if (open_type.equals("h5")) {
                    Intent intent = new Intent(ApplicationsCenterActivity.this, WebViewsActivity.class);
                    intent.putExtra("url", url+"?user_id="+user.getId());
                    startActivity(intent);
                } else if (open_type.equals("app")) {

                } else {
                    Intent intent = new Intent(ApplicationsCenterActivity.this, WebViewsActivity.class);
                    intent.putExtra("url", auth_url);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 获得应用列表接口
     */
    private void getAppTools() {

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("app_type", "xcloud");
        AjaxParams param = new AjaxParams(map);

        showDialog();
        new FinalHttp().get(Constants.URL_GET_APP_TOOLS, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(ApplicationsCenterActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                appToolsDatas = gson.fromJson(data, new TypeToken<ArrayList<AppToolsData>>() {
                                }.getType());
                                showData(appToolsDatas);
                                appToolsAdapter1.setData(t_menu_List);
                                appToolsAdapter2.setData(d_menu_List);
                            } else {
                                appToolsDatas = new ArrayList<AppToolsData>();
                                appToolsAdapter1.setData(new ArrayList<AppToolsData>());
                                appToolsAdapter2.setData(new ArrayList<AppToolsData>());
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
                    UIUtils.showToast(ApplicationsCenterActivity.this, errorMsg);
                }
            }
        });
    }

    private void showData(ArrayList<AppToolsData> appToolsDatas) {
        for (Iterator iterator = appToolsDatas.iterator(); iterator.hasNext();) {
            AppToolsData appToolsData = (AppToolsData) iterator.next();
            String menu_type = appToolsData.getMenu_type().trim();
            if (menu_type.equals(Constants.MENU_TYPE_T)) {
                t_menu_List.add(appToolsData);
                tv_menu_type1.setText("工具服务");
            } else if (menu_type.equals(Constants.MENU_TYPE_D)) {
                d_menu_List.add(appToolsData);
                tv_menu_type2.setText("成长赚钱");
            }
        }
    }

}
