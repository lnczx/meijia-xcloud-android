package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
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
import com.meijialife.simi.bean.CompanyDetail;
import com.meijialife.simi.bean.Friend;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.SpFileUtil;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * @description：企业员工通讯录
 * @author： kerryg
 * @date:2015年12月5日
 */
public class StaffListActivity extends Activity implements OnClickListener {

    private ContactsAdapter adapter;
    /**
     * 列表Item显示初始化
     */
    private CheckBox cb;// 选择框
    private TextView header_tv_name;
    private ImageView title_btn_left;
    private TextView title_btn_ok;

    /**
     * 获得传递的参数
     */
    private String company_id;
    private String company_name;
    private int flag;
    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;

    private ImageButton ibtn_rq;// 右侧显示个人二维码信息
    private TextView mRqTitle;// 二维码标题
    private PopupWindow mPopupWindow;
    private View music_popunwindwow;
    private LinearLayout ll_rq;
    private ImageView iv_rq_left;
    private LayoutInflater layoutInflater;

    private ArrayList<Friend> myFriendList;
    private ArrayList<Friend> totalFriendList;
    private PullToRefreshListView mPullRefreshListView;// 上拉刷新的控件
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_staff_list);
        super.onCreate(savedInstanceState);

        initView();
    }

    @SuppressLint("UseSparseArrays")
    private void initView() {
        /**
         * 获取传递参数
         */
        company_id = getIntent().getStringExtra("company_id");
        company_name = getIntent().getStringExtra("company_name");
        flag = getIntent().getIntExtra("flag", 0);

        finalBitmap = FinalBitmap.create(this);
        defDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_defult_touxiang);
        music_popunwindwow = LayoutInflater.from(this).inflate(R.layout.mine_rq_layout, null);
        ll_rq = (LinearLayout) music_popunwindwow.findViewById(R.id.ll_rq);
        iv_rq_left = (ImageView) music_popunwindwow.findViewById(R.id.iv_rq_left);
        ibtn_rq = (ImageButton) findViewById(R.id.ibtn_rq);
        mRqTitle = (TextView) music_popunwindwow.findViewById(R.id.m_rq_title);
        mRqTitle.setText("企业二维码名片");

        title_btn_left = (ImageView) findViewById(R.id.title_btn_left);
        header_tv_name = (TextView) findViewById(R.id.header_tv_name);
        header_tv_name.setText(company_name);
        title_btn_ok = (TextView) findViewById(R.id.title_btn_ok);

        if (flag == 0) {
            ibtn_rq.setVisibility(View.VISIBLE);
            title_btn_ok.setVisibility(View.GONE);
        } else {
            ibtn_rq.setVisibility(View.GONE);
            title_btn_ok.setVisibility(View.VISIBLE);
        }
        title_btn_left.setOnClickListener(this);
        title_btn_ok.setOnClickListener(this);
        ibtn_rq.setOnClickListener(this);
        iv_rq_left.setOnClickListener(this);
        ll_rq.setOnClickListener(this);

        initStaffView();
    }

    private void initStaffView() {
        totalFriendList = new ArrayList<Friend>();
        myFriendList = new ArrayList<Friend>();
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_staff_list);
        adapter = new ContactsAdapter(this);
        mPullRefreshListView.setAdapter(adapter);
        mPullRefreshListView.setMode(Mode.BOTH);
        initIndicator();
        getStaffList(Constants.finalContactList, page);
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                page = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getStaffList(Constants.finalContactList, page);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if (myFriendList != null && myFriendList.size() >= 10) {
                    page = page + 1;
                    getStaffList(Constants.finalContactList, page);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(StaffListActivity.this, "请稍后，没有更多加载数据", Toast.LENGTH_SHORT).show();
                    mPullRefreshListView.onRefreshComplete();
                }
            }
        });
        mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cb = (CheckBox) view.findViewById(R.id.cb_check_box);
                UserInfo userInfo = DBHelper.getUserInfo(StaffListActivity.this);
                Friend friend1 = totalFriendList.get(position);
                if (cb.isChecked()) {
                    if (!StringUtils.isEmpty(friend1.getMobile())) {
                        SpFileUtil.saveBoolean(StaffListActivity.this, SpFileUtil.KEY_CHECKED_FRIENDS, friend1.getMobile(), false);
                    } else {
                        SpFileUtil.saveBoolean(StaffListActivity.this, SpFileUtil.KEY_CHECKED_FRIENDS, friend1.getFriend_id(), false);
                    }
                    for (int i = 0; i < Constants.TEMP_FRIENDS.size(); i++) {
                        Friend friend2 = Constants.TEMP_FRIENDS.get(i);
                        if (StringUtils.isEquals(friend1.getMobile(), friend2.getMobile())
                                || StringUtils.isEquals(friend1.getFriend_id(), friend2.getFriend_id())) {
                            Constants.TEMP_FRIENDS.remove(i);
                            --i;
                        }
                    }

                } else {
                    if (!StringUtils.isEmpty(friend1.getMobile())) {
                        SpFileUtil.saveBoolean(StaffListActivity.this, SpFileUtil.KEY_CHECKED_FRIENDS, friend1.getMobile(), true);
                    } else {
                        SpFileUtil.saveBoolean(StaffListActivity.this, SpFileUtil.KEY_CHECKED_FRIENDS, friend1.getFriend_id(), true);
                    }
                    Constants.TEMP_FRIENDS.add(friend1);
                }
                adapter.notifyDataSetChanged();
            }
        });
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
     * 处理数据加载的方法
     * 
     * @param list
     */
    private void showData(ArrayList<Friend> myFriendLis, int flag) {
        if (myFriendList != null && myFriendList.size() > 0) {
            if (page == 1) {
                totalFriendList.clear();
            }
            for (Friend friend : myFriendList) {
                totalFriendList.add(friend);
            }
            // 给适配器赋值
            adapter.setData(totalFriendList, Constants.TEMP_FRIENDS, flag);
        }
        mPullRefreshListView.onRefreshComplete();
    }

    private void popRqCode() {
        if (null == mPopupWindow || !mPopupWindow.isShowing()) {
            mPopupWindow = new PopupWindow(music_popunwindwow, android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
            mPopupWindow.showAtLocation(this.findViewById(R.id.ll_staff_list), Gravity.RIGHT | Gravity.BOTTOM, 0, 0);
            getMyRqCode();
        }
    }

    private void getMyRqCode() {
        String user_id = DBHelper.getUser(this).getId();
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id + "");
        map.put("company_id", company_id);
        AjaxParams param = new AjaxParams(map);

        showDialog();
        new FinalHttp().get(Constants.URL_GET_COMPANY_DETAIL, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(StaffListActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                Log.d("data", data);
                                CompanyDetail companyDetail = gson.fromJson(data, CompanyDetail.class);
                                String rq_url = companyDetail.getQrCode();
                                finalBitmap.display(music_popunwindwow.findViewById(R.id.iv_rq_code), rq_url, defDrawable.getBitmap(),
                                        defDrawable.getBitmap());
                            } else {
                                Toast.makeText(StaffListActivity.this, "二维码还没有生成", Toast.LENGTH_SHORT).show();
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
                    UIUtils.showToast(StaffListActivity.this, errorMsg);
                }
            }
        });
    }

    private void closePopWindow() {
        if (null != mPopupWindow && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    /**
     * 获得企业员工列表接口
     */
    public void getStaffList(final ArrayList<String> staffList, int page) {

        String user_id = DBHelper.getUser(StaffListActivity.this).getId();

        if (!NetworkUtils.isNetworkConnected(StaffListActivity.this)) {
            Toast.makeText(StaffListActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id + "");
        map.put("company_id", company_id);
        map.put("page", page + "");
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().get(Constants.URL_GET_STAFF_LIST, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(StaffListActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                myFriendList = gson.fromJson(data, new TypeToken<ArrayList<Friend>>() {
                                }.getType());
                                showData(myFriendList, flag);
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
                    UIUtils.showToast(StaffListActivity.this, errorMsg);
                }
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
        case R.id.title_btn_left:
            this.finish();
            break;
        case R.id.title_btn_ok:
            /*
             * intent = new Intent(StaffListActivity.this,ContactChooseActivity.class); startActivity(intent);
             */
            CompanyListActivity.instance.finish();
            StaffListActivity.this.finish();
            break;
        case R.id.ibtn_rq:
            popRqCode();// 弹出企业二维码
            break;
        case R.id.iv_rq_left:
            closePopWindow();// 返回关闭二维码
            break;
        case R.id.ll_rq:
            closePopWindow();// 触屏关闭二维码
            break;
        default:
            break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        page = 1;

    }

    /**
     * 进度条使用
     */
    private ProgressDialog m_pDialog;

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
}
