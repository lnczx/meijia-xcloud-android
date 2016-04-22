package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.ContactsAdapter;
import com.meijialife.simi.bean.Friend;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.SpFileUtil;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * @description：卡片中选择联系人
 * @author： kerryg
 * @date:2015年11月27日
 */
public class ContactChooseActivity extends Activity implements OnClickListener {

    ArrayList<String> getcontactsList; // 选择得到联系人

    private RelativeLayout rl_company_contacts;// 企业通讯录
    private RelativeLayout rl_friend_contacts;// 好友通讯录
    private UserInfo userInfo;
    private ArrayList<Friend> friendList = new ArrayList<Friend>();
    private TextView tv_contacts_list;// 显示勾选联系人

    private ListView listview;
    private ContactsAdapter adapter;
    private CheckBox cb;
    private TextView tv_name;
    private TextView tv_mobile;
    private TextView tv_id;
    private TextView tv_temp;

    private TextView tv_has_company;// 显示是否拥有企业
    private int flag = 1;// 1=来自卡片页面0=来自企业通讯录
    private ArrayList<String> mobileList;

    private ArrayList<Friend> totalFriendList;
    // 布局控件定义
    private PullToRefreshListView mPullRefreshListView;// 上拉刷新的控件
    private int page = 1;
    
  

    @Override
    @SuppressLint("UseSparseArrays")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_choose_list);


        mobileList = new ArrayList<String>();
        ImageView title_btn_left = (ImageView) findViewById(R.id.title_btn_left);
        TextView header_tv_name = (TextView) findViewById(R.id.header_tv_name);
        title_btn_ok = (TextView) findViewById(R.id.title_btn_ok);
        rl_company_contacts = (RelativeLayout) findViewById(R.id.rl_company_contacts);
        rl_friend_contacts = (RelativeLayout) findViewById(R.id.rl_friend_contacts);
        tv_contacts_list = (TextView) findViewById(R.id.tv_contacts_list); 
        
       
        tv_has_company = (TextView) findViewById(R.id.tv_has_company);
        userInfo = DBHelper.getUserInfo(this);
        if (userInfo.getHas_company() == 0) {
            tv_has_company.setVisibility(View.VISIBLE);
        } else {
            tv_has_company.setVisibility(View.GONE);
        }
        title_btn_ok.setOnClickListener(this);
        title_btn_left.setOnClickListener(this);
        rl_company_contacts.setOnClickListener(this);
        rl_friend_contacts.setOnClickListener(this);

        header_tv_name.setText("选择接收人");
        title_btn_left.setVisibility(View.VISIBLE);

        initContactView();
    }

    private void initContactView() {
        totalFriendList = new ArrayList<Friend>();
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_contact_list);
        adapter = new ContactsAdapter(this);
        mPullRefreshListView.setAdapter(adapter);
        mPullRefreshListView.setMode(Mode.BOTH);
        initIndicator();
        getFriendList(page);
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                page = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getFriendList(page);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if (friendList != null && friendList.size() >= 10) {
                    page = page + 1;
                    getFriendList(page);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ContactChooseActivity.this, "请稍后，没有更多加载数据", Toast.LENGTH_SHORT).show();
                    mPullRefreshListView.onRefreshComplete();
                }
            }
        });
        mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                cb = (CheckBox) view.findViewById(R.id.cb_check_box);
                tv_name = (TextView) view.findViewById(R.id.item_tv_name);
                tv_mobile = (TextView) view.findViewById(R.id.item_tv_mobile);
                tv_id = (TextView) view.findViewById(R.id.item_tv_id);
                tv_temp = (TextView) view.findViewById(R.id.item_tv_temp);
                Friend friend1 = totalFriendList.get(position);
                if (cb.isChecked()) {
                    if (!StringUtils.isEmpty(friend1.getMobile())) {
                        SpFileUtil.saveBoolean(ContactChooseActivity.this, SpFileUtil.KEY_CHECKED_FRIENDS, friend1.getMobile(), false);
                    } else {
                        SpFileUtil.saveBoolean(ContactChooseActivity.this, SpFileUtil.KEY_CHECKED_FRIENDS, friend1.getFriend_id(), false);
                    }
                    if (Constants.TEMP_FRIENDS != null && Constants.TEMP_FRIENDS.size() > 0) {
                        for (int i = 0; i < Constants.TEMP_FRIENDS.size(); i++) {
                            Friend friend2 = Constants.TEMP_FRIENDS.get(i);
                            if (StringUtils.isEquals(friend1.getMobile(), friend2.getMobile())
                                    || StringUtils.isEquals(friend1.getFriend_id(), friend2.getFriend_id())) {
                                Constants.TEMP_FRIENDS.remove(i);
                                --i;
                            }
                        }
                    }
                } else {
                    if (!StringUtils.isEmpty(friend1.getMobile())) {
                        SpFileUtil.saveBoolean(ContactChooseActivity.this, SpFileUtil.KEY_CHECKED_FRIENDS, friend1.getMobile(), true);
                    } else {
                        SpFileUtil.saveBoolean(ContactChooseActivity.this, SpFileUtil.KEY_CHECKED_FRIENDS, friend1.getFriend_id(), true);
                    }
                    Constants.TEMP_FRIENDS.add(friend1);
                }
                adapter.notifyDataSetChanged();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showAllNames();
                    }
                });
            }
        });
    }

    /**
     * 负责显示选中的联系人
     */
    private void showAllNames() {
        String str = "";
        StringBuilder sb = new StringBuilder();
        if (Constants.TEMP_FRIENDS != null && Constants.TEMP_FRIENDS.size() > 0) {
            for (int i = 0; i < Constants.TEMP_FRIENDS.size(); i++) {
                Friend myFriend = Constants.TEMP_FRIENDS.get(i);
                if (!StringUtils.isEmpty(myFriend.getName())) {
                    sb.append(myFriend.getName() + ",");
                } else {
                    sb.append(myFriend.getMobile() + ",");
                }
            }
        }
        str = sb.toString();
        if (!StringUtils.isEmpty(str)) {
            str = str.substring(0, str.lastIndexOf(","));
        }
        tv_contacts_list.setText(str);
    }

    /**
     * 设置下拉刷新提示
     */
    private void initIndicator() {
        ILoadingLayout startLabels = mPullRefreshListView.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在刷新...");// 刷新时
        startLabels.setReleaseLabel("释放更新");// 下来达到一定距离时，显示的提示

        ILoadingLayout endLabels = mPullRefreshListView.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("上拉加载");
        endLabels.setRefreshingLabel("正在刷新...");// 刷新时
        endLabels.setReleaseLabel("释放加载");// 下来达到一定距离时，显示的提示
    }

    /**
     * 处理singleTask下不能接受Intent传值
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
        case R.id.rl_company_contacts:// 企业通讯录
            if (userInfo.getHas_company() == 0) {
                Intent intent1 = new Intent(this, WebViewActivity.class);
                intent1.putExtra("title", "企业通讯录");
                intent1.putExtra("url", Constants.HAS_COMPANY);
                startActivity(intent1);
            } else {
                intent = new Intent(ContactChooseActivity.this, CompanyListActivity.class);
                intent.putExtra("flag", 0);
                startActivity(intent);
            }
            break;
        case R.id.rl_friend_contacts:// 选择接收人--->选择联系人
            intent = new Intent(this, ContactSelectActivity.class);
            startActivityForResult(intent, GET_CONTACTS);
            break;
        case R.id.title_btn_ok:// 确定按钮
            postContactData();
            break;
        case R.id.title_btn_left:// 左侧返回按钮
            // && Constants.finalContactList.size() > 0
            if (Constants.finalContactList != null) {
                intent = new Intent();
                setResult(RESULT_OK, intent);
                ContactChooseActivity.this.finish();
            }
            break;
        default:
            break;
        }

    }

    public static final int GET_CONTACTS = 1003;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
            case GET_CONTACTS:
                if (Constants.finalContactList != null && Constants.finalContactList.size() > 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            StringBuilder sb = new StringBuilder();
                            String name = null;
                            for (int i = 0; i < Constants.finalContactList.size(); i++) {
                                String bean = Constants.finalContactList.get(i).toString();
                                if (name != null) {
                                    name = bean.substring(0, bean.indexOf("\n"));
                                    if (name.equals("") || name.length() <= 0) {
                                        name = bean.substring(bean.indexOf("\n") + 1, bean.lastIndexOf("\n"));
                                    }
                                } else {
                                    name = bean.substring(0, bean.indexOf("\n"));
                                    if (name.equals("") || name.length() <= 0) {
                                        name = bean.substring(bean.indexOf("\n") + 1, bean.lastIndexOf("\n"));
                                    }
                                }
                                sb.append(name + ",");
                            }
                            tv_contacts_list.setText(sb.toString());
                        }
                    });
                }
                break;
            default:
                break;
            }
        }
    }

    /**
     * 获取好友列表
     * 
     * @param isShowDlg
     */
    public void getFriendList(int page) {
        String user_id = DBHelper.getUser(ContactChooseActivity.this).getId();

        if (!NetworkUtils.isNetworkConnected(ContactChooseActivity.this)) {
            Toast.makeText(ContactChooseActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id + "");
        map.put("page", "" + page);
        AjaxParams param = new AjaxParams(map);

        showDialog();
        new FinalHttp().get(Constants.URL_GET_FRIENDS, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(ContactChooseActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                friendList = gson.fromJson(data, new TypeToken<ArrayList<Friend>>() {
                                }.getType());
                                showData(friendList, flag);
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
                    UIUtils.showToast(ContactChooseActivity.this, errorMsg);
                }
            }
        });

    }

    /**
     * 获取用户详情接口---公司注册之后更新用户信息
     */
    private void getUserInfo() {
        if (!NetworkUtils.isNetworkConnected(ContactChooseActivity.this)) {
            Toast.makeText(ContactChooseActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", userInfo.getId());
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().get(Constants.URL_GET_USER_INFO, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(ContactChooseActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                userInfo = gson.fromJson(data, UserInfo.class);
                                DBHelper.updateUserInfo(ContactChooseActivity.this, userInfo);
                            } else {
                                // UIUtils.showToast(getActivity().this, "数据错误");
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
                // 操作失败，显示错误信息|
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    UIUtils.showToast(ContactChooseActivity.this, errorMsg);
                }
            }
        });
    }

    public void postContactData() {
        if (Constants.TEMP_FRIENDS.size() > 10) {
            UIUtils.showToast(this, "您最多可以选择10个联系人哦");
        } else {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            ContactChooseActivity.this.finish();
        }
    }

    private ProgressDialog m_pDialog;
    private TextView title_btn_ok;

    public void showDialog() {
        if (m_pDialog == null) {
            m_pDialog = new ProgressDialog(this);
            m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            m_pDialog.setMessage("请稍等...");
            m_pDialog.setIndeterminate(false);
            m_pDialog.setCancelable(true);
        }
        m_pDialog.show();
    }

    public void dismissDialog() {
        if (m_pDialog != null && m_pDialog.isShowing()) {
            m_pDialog.hide();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserInfo();
        getFriendList(page);
    }

    /**
     * 处理数据加载的方法
     * 
     * @param list
     */
    private void showData(ArrayList<Friend> friendList, int flag) {
        if (friendList != null && friendList.size() > 0) {
            if (page == 1) {
                totalFriendList.clear();
            }
            for (Friend friend : friendList) {
                totalFriendList.add(friend);
            }
            // 适配器列表中判断显示选中的联系人
            adapter.setData(totalFriendList, Constants.TEMP_FRIENDS, flag);
            showAllNames();
        }
        mPullRefreshListView.onRefreshComplete();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        page = 1;
        friendList = null;
        totalFriendList = null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            postContactData();
        }
        return super.onKeyDown(keyCode, event);
    }
}
