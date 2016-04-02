package com.meijialife.simi.fra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
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
import com.meijialife.simi.BaseFragment;
import com.meijialife.simi.Constants;
import com.meijialife.simi.MainActivity;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.CompanyListActivity;
import com.meijialife.simi.activity.ContactAddFriendsActivity;
import com.meijialife.simi.activity.DynamicDetailsActivity;
import com.meijialife.simi.activity.FindSecretaryActivity;
import com.meijialife.simi.activity.FriendApplyActivity;
import com.meijialife.simi.activity.FriendPageActivity;
import com.meijialife.simi.activity.WebViewsActivity;
import com.meijialife.simi.adapter.FriendAdapter;
import com.meijialife.simi.adapter.FriendDynamicAdapter;
import com.meijialife.simi.adapter.FriendDynamicAdapter.onDynamicUpdateListener;
import com.meijialife.simi.bean.AppHelpData;
import com.meijialife.simi.bean.Friend;
import com.meijialife.simi.bean.FriendDynamicData;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.ui.RouteUtil;
import com.meijialife.simi.ui.TipPopWindow;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;
import com.zbar.lib.CaptureActivity;

/**
 * @description：好友---消息
 * @author： kerryg
 * @date:2015年12月1日
 */
public class Home3Fra extends BaseFragment implements OnClickListener, onDynamicUpdateListener {

    private RadioGroup radiogroup;// 顶部Tab
    private View line_1, line_2, line_3;

    private LinearLayout layout_msg; // 消息View
    private LinearLayout layout_friend; // 好友View
    private LinearLayout layout_dynamic; // 动态View

    /** 秘友Tab下所有控件 **/
    private FriendAdapter friendAdapter;
    private RelativeLayout rl_add; // 添加通讯录好友
    private RelativeLayout rl_find; // 寻找秘书和助理
    private RelativeLayout rl_rq; // 扫一扫加好友
    private RelativeLayout rl_apply; // 扫一扫加好友
    private RelativeLayout rl_company_contacts;
    private TextView tv_has_company;// 显示理解创建
    private final static int SCANNIN_GREQUEST_CODES = 5;

    private FriendDynamicAdapter friendDynamicAdapter;// 好友动态适配器

    private int checkedIndex = 0; // 当前选中的Tab位置, 0=消息 1=好友
    private MainActivity activity;
    private UserInfo userInfo;// 获取用户详情

    private RadioButton rb_feed;// 动态
    private RadioButton rb_friend;// 好友
    private RadioButton rb_msg;// 消息
    private RadioButton rb_app;// 申请
    private static View layout_mask;

    // 布局控件定义
    private PullToRefreshListView mPullRefreshFeedListView;// 上拉刷动态的控件
    private PullToRefreshListView mPullRefreshFriendListView;// 上拉刷动态的控件
    private int feedPage = 1;
    private int friendPage = 1;

    private ArrayList<FriendDynamicData> myFeedList;
    private ArrayList<FriendDynamicData> totalFeedList;

    private ArrayList<Friend> myFriendList;
    private ArrayList<Friend> totalFriendList;

    public Home3Fra() {
    }

    public Home3Fra(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // if(this.getResources().getConfiguration().orientation ==Configuration.ORIENTATION_LANDSCAPE){
        // getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // }

        View v = inflater.inflate(R.layout.index_3, null);
        vs = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.index_3, null);
        init(v);// 初始化
        initTab(v);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Constants.checkedIndex == 0) {
            getFriendDynamicList(feedPage);
        } else if (Constants.checkedIndex == 1) {
            getFriendList(friendPage);
        }
        getUserInfo();
    }

    private void init(View v) {
        userInfo = DBHelper.getUserInfo(getActivity());
        layout_msg = (LinearLayout) v.findViewById(R.id.layout_msg);
        layout_friend = (LinearLayout) v.findViewById(R.id.layout_friend);
        layout_dynamic = (LinearLayout) v.findViewById(R.id.layout_friend_dynamic);
        tv_has_company = (TextView) v.findViewById(R.id.tv_has_company);
        layout_mask = (View) v.findViewById(R.id.layout_mask);

        initFriendView(v);
        // 初始化好友动态列表
        initFeedView(v);

        rl_add = (RelativeLayout) v.findViewById(R.id.rl_add);
        rl_find = (RelativeLayout) v.findViewById(R.id.rl_find);
        rl_rq = (RelativeLayout) v.findViewById(R.id.rl_rq);
        rl_apply = (RelativeLayout) v.findViewById(R.id.rl_apply);
        rl_company_contacts = (RelativeLayout) v.findViewById(R.id.rl_company_contacts);
        rl_add.setOnClickListener(this);
        rl_find.setOnClickListener(this);
        rl_rq.setOnClickListener(this);
        rl_company_contacts.setOnClickListener(this);
        rl_apply.setOnClickListener(this);
        // 请求帮助接口
        getAppHelp();
    }

    private void initFriendView(View v) {
        totalFriendList = new ArrayList<Friend>();
        myFriendList = new ArrayList<Friend>();
        mPullRefreshFriendListView = (PullToRefreshListView) v.findViewById(R.id.pull_refresh_lists);
        friendAdapter = new FriendAdapter(getActivity());
        mPullRefreshFriendListView.setAdapter(friendAdapter);
        mPullRefreshFriendListView.setMode(Mode.BOTH);
        initIndicators();
        mPullRefreshFriendListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                friendPage = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getFriendList(friendPage);
                friendAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if (myFriendList != null && myFriendList.size() >= 10) {
                    friendPage = friendPage + 1;
                    getFriendList(friendPage);
                    friendAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "请稍后，没有更多加载数据", Toast.LENGTH_SHORT).show();
                    mPullRefreshFriendListView.onRefreshComplete();
                }
            }
        });
        mPullRefreshFriendListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), FriendPageActivity.class);
                intent.putExtra("friend_id", myFriendList.get(position).getFriend_id());
                startActivity(intent);
            }
        });
    }

    /**
     * 初始化好友动态列表
     * 
     * @param v
     */
    private void initFeedView(View v) {
        totalFeedList = new ArrayList<FriendDynamicData>();
        myFeedList = new ArrayList<FriendDynamicData>();
        mPullRefreshFeedListView = (PullToRefreshListView) v.findViewById(R.id.pull_refresh_list);
        friendDynamicAdapter = new FriendDynamicAdapter(getActivity(), this);
        mPullRefreshFeedListView.setAdapter(friendDynamicAdapter);
        mPullRefreshFeedListView.setMode(Mode.BOTH);

        initIndicator();
        getFriendDynamicList(feedPage);
        mPullRefreshFeedListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                feedPage = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getFriendDynamicList(feedPage);
                friendDynamicAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if (myFeedList != null && myFeedList.size() >= 10) {
                    feedPage = feedPage + 1;
                    getFriendDynamicList(feedPage);
                    friendDynamicAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "请稍后，没有更多加载数据", Toast.LENGTH_SHORT).show();
                    mPullRefreshFeedListView.onRefreshComplete();
                }
            }
        });
        mPullRefreshFeedListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DynamicDetailsActivity.class);
                String feedId = totalFeedList.get(position).getFid();
                intent.putExtra("feedId", feedId);
                startActivity(intent);
            }
        });
    }

    /**
     * 设置下拉刷新提示
     */
    private void initIndicator() {
        ILoadingLayout startLabels = mPullRefreshFeedListView.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在刷新...");// 刷新时
        startLabels.setReleaseLabel("释放更新");// 下来达到一定距离时，显示的提示

        ILoadingLayout endLabels = mPullRefreshFeedListView.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("上拉加载");
        endLabels.setRefreshingLabel("正在刷新...");// 刷新时
        endLabels.setReleaseLabel("释放加载");// 下来达到一定距离时，显示的提示
    }

    /**
     * 设置下拉刷新提示
     */
    private void initIndicators() {
        ILoadingLayout startLabels = mPullRefreshFriendListView.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在刷新...");// 刷新时
        startLabels.setReleaseLabel("释放更新");// 下来达到一定距离时，显示的提示

        ILoadingLayout endLabels = mPullRefreshFriendListView.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("上拉加载");
        endLabels.setRefreshingLabel("正在刷新...");// 刷新时
        endLabels.setReleaseLabel("释放加载");// 下来达到一定距离时，显示的提示
    }

    private void initTab(View v) {
        radiogroup = (RadioGroup) v.findViewById(R.id.radiogroup);
        line_1 = (View) v.findViewById(R.id.line_1);
        line_2 = (View) v.findViewById(R.id.line_2);
        line_3 = (View) v.findViewById(R.id.line_3);

        rb_feed = (RadioButton) v.findViewById(R.id.rb_dynamic);
        rb_friend = (RadioButton) v.findViewById(R.id.rb_friend);
        rb_msg = (RadioButton) v.findViewById(R.id.rb_msg);

        radiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup grop, int checkedId) {
                line_1.setVisibility(View.INVISIBLE);
                line_2.setVisibility(View.INVISIBLE);
                line_3.setVisibility(View.INVISIBLE);

                if (checkedId == rb_feed.getId()) {
                    Constants.checkedIndex = 0;
                    line_1.setVisibility(View.VISIBLE);
                    rb_feed.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    rb_msg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                    rb_friend.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                    layout_dynamic.setVisibility(View.VISIBLE);
                    layout_msg.setVisibility(View.GONE);
                    layout_friend.setVisibility(View.GONE);
                    getFriendDynamicList(feedPage);
                }

                if (checkedId == rb_friend.getId()) {
                    Constants.checkedIndex = 1;
                    line_2.setVisibility(View.VISIBLE);
                    rb_friend.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    rb_feed.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                    rb_msg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                    layout_msg.setVisibility(View.GONE);
                    layout_dynamic.setVisibility(View.GONE);
                    layout_friend.setVisibility(View.VISIBLE);
                    getFriendList(friendPage);
                }
                if (checkedId == rb_msg.getId()) {
                    /*
                     * checkedIndex = 1; line_2.setVisibility(View.VISIBLE); layout_msg.setVisibility(View.GONE);
                     * layout_friend.setVisibility(View.VISIBLE); getFriendList(true);
                     */
                    // 跳转到消息View
                    if (activity != null) {
                        ((MainActivity) activity).change2IM();
                    }
                }
            }
        });
        radiogroup.getChildAt(Constants.checkedIndex).performClick();
    }

    public static void showMask() {
        layout_mask.setVisibility(View.VISIBLE);
    }

    public static void GoneMask() {
        layout_mask.setVisibility(View.GONE);
    }

    /**
     * 获取好友列表
     */
    public void getFriendList(int friendPage) {

        showDialog();
        String user_id = DBHelper.getUser(getActivity()).getId();

        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id + "");
        map.put("page", "" + friendPage);
        AjaxParams param = new AjaxParams(map);

        showDialog();
        new FinalHttp().get(Constants.URL_GET_FRIENDS, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(getActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                showFriendData(myFriendList);
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
                    UIUtils.showToast(getActivity(), errorMsg);
                }
            }
        });
    }

    /**
     * 获取好友动态列表接口
     */
    public void getFriendDynamicList(int feedPage) {
        showDialog();
        String user_id = DBHelper.getUser(getActivity()).getId();
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id + "");
        map.put("feed_from", "0");
        map.put("page", "" + feedPage);
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().get(Constants.URL_GET_FRIEND_DYNAMIC_LIST, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(getActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                myFeedList = gson.fromJson(data, new TypeToken<ArrayList<FriendDynamicData>>() {
                                }.getType());
                                showData(myFeedList);
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
                    UIUtils.showToast(getActivity(), errorMsg);
                }
            }
        });

    }

    /**
     * 处理数据加载的方法
     * 
     * @param list
     */
    private void showData(List<FriendDynamicData> myFeedList) {
        if (feedPage == 1) {
            totalFeedList.clear();
        }
        for (FriendDynamicData myFeed : myFeedList) {
            totalFeedList.add(myFeed);
        }
        // 给适配器赋值
        friendDynamicAdapter.setData(totalFeedList);
        mPullRefreshFeedListView.onRefreshComplete();
    }

    /**
     * 处理数据加载的方法
     * 
     * @param list
     */
    private void showFriendData(List<Friend> myFriendList) {
        if (friendPage == 1) {
            totalFriendList.clear();
        }
        for (Friend myFriend : myFriendList) {
            totalFriendList.add(myFriend);
        }
        // 给适配器赋值
        friendAdapter.setData(totalFriendList);
        mPullRefreshFriendListView.onRefreshComplete();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
        case R.id.rl_add: // 添加通讯录好友
            intent = new Intent(getActivity(), ContactAddFriendsActivity.class);
            intent.putExtra("friendList", totalFriendList);
            startActivity(intent);
            break;
        case R.id.rl_find: // 寻找秘书和助理
            startActivity(new Intent(getActivity(), FindSecretaryActivity.class));
            break;
        case R.id.rl_rq:// 扫一扫加好友
            intent = new Intent();
            intent.setClass(getActivity(), CaptureActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, SCANNIN_GREQUEST_CODES);
            break;
        case R.id.rl_company_contacts:// 企业通讯录
            // 跳转到企业通讯录
            if (userInfo.getHas_company() == 0) {
                intent = new Intent(getActivity(), WebViewsActivity.class);
                intent.putExtra("url", Constants.HAS_COMPANY);
                startActivity(intent);
            } else {
                intent = new Intent(getActivity(), CompanyListActivity.class);
                intent.putExtra("flag", 2);
                startActivity(intent);
            }
            break;
        case R.id.rl_apply:
            intent = new Intent(getActivity(), FriendApplyActivity.class);
            startActivity(intent);
            break;
        default:
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case SCANNIN_GREQUEST_CODES:
            if (resultCode == (-1)) {
                Bundle bundle = data.getExtras();
                String result = bundle.getString("result").trim();
                if (!StringUtils.isEmpty(result) && result.contains(Constants.RQ_IN_APP)) {// 判断是否为云行政二维码
                    // http://www.51xingzheng.cn/d/open.html?category=app&action=feed&params=&goto_url=
                    if (!StringUtils.isEmpty(result) && result.contains("category=app")) {
                        String category = "", action = "", params = "", goto_url = "";
                        if (result.contains("params") && result.contains("goto_url")) {// 两个参数都有
                            String temp[] = result.split("&");
                            category = temp[0].substring(temp[0].lastIndexOf("=") + 1, temp[0].length());
                            action = temp[1].substring(temp[1].lastIndexOf("=") + 1, temp[1].length());
                            params = temp[2].substring(temp[2].lastIndexOf("=") + 1, temp[2].length());
                            goto_url = temp[3].substring(temp[3].lastIndexOf("=") + 1, temp[3].length());

                        } else if (result.contains("params") && !result.contains("goto_url")) {// 只有参数params
                            String temp[] = result.split("&");
                            category = temp[0].substring(temp[0].lastIndexOf("=") + 1, temp[0].length());
                            action = temp[1].substring(temp[1].lastIndexOf("=") + 1, temp[1].length());
                            params = temp[2].substring(temp[2].lastIndexOf("=") + 1, temp[2].length());

                        } else if (result.contains("goto_url") && !result.contains("params")) {// 只有参数goto_url
                            String temp[] = result.split("&");
                            category = temp[0].substring(temp[0].lastIndexOf("=") + 1, temp[0].length());
                            action = temp[1].substring(temp[1].lastIndexOf("=") + 1, temp[1].length());
                            goto_url = temp[2].substring(temp[2].lastIndexOf("=") + 1, temp[2].length());
                        } else {
                            String temp[] = result.split("&");
                            category = temp[0].substring(temp[0].lastIndexOf("=") + 1, temp[0].length());
                            action = temp[1].substring(temp[1].lastIndexOf("=") + 1, temp[1].length());
                        }
                        if (!StringUtils.isEmpty(result)) {
                            RouteUtil routeUtil = new RouteUtil(getActivity());
                            routeUtil.Routing(category, action, goto_url, params);
                        }
                    } else {
                        Intent intent = new Intent(getActivity(), WebViewsActivity.class);
                        intent.putExtra("url", result);
                        startActivity(intent);
                    }
                } else {// 非内部app扫描，webView显示
                    Intent intent = new Intent(getActivity(), WebViewsActivity.class);
                    intent.putExtra("url", result);
                    startActivity(intent);
                }
            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 添加好友接口
     * 
     * @param friend_id
     */
    public void addFriend(final String friend_id) {

        String user_id = DBHelper.getUser(getActivity()).getId();

        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id + "");
        map.put("friend_id", friend_id);
        AjaxParams param = new AjaxParams(map);

        showDialog();
        new FinalHttp().get(Constants.URL_GET_ADD_FRIEND, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(getActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                            // 添加成功，跳转到好友界面
                            Intent intent = new Intent(getActivity(), FriendPageActivity.class);
                            intent.putExtra("friend_id", friend_id);
                            startActivity(intent);
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
                    UIUtils.showToast(getActivity(), errorMsg);
                }
            }
        });
    }

    private void getUserInfo() {
        if (userInfo == null) {
            Toast.makeText(getActivity(), "用户信息错误", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.net_not_open), 0).show();
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
                Toast.makeText(getActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                DBHelper.updateUserInfo(getActivity(), userInfo);
                            } else {
                                // UIUtils.showToast(getActivity(), "数据错误");
                            }
                            hasCompany();
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
                    UIUtils.showToast(getActivity(), errorMsg);
                }
            }
        });
    }

    /**
     * 访问接口判断是否注册公司
     */
    private void hasCompany() {
        if (userInfo.getHas_company() == 0) {
            tv_has_company.setVisibility(View.VISIBLE);
        } else {
            tv_has_company.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDynamicUpdate() {
        getFriendDynamicList(feedPage);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        feedPage = 1;
        friendPage = 1;

        myFeedList = new ArrayList<FriendDynamicData>();
        totalFeedList = new ArrayList<FriendDynamicData>();

        myFriendList = new ArrayList<Friend>();
        totalFriendList = new ArrayList<Friend>();
    }

    private AppHelpData appHelpData;
    private View vs;

    /*
     * 帮助接口
     */

    private void getAppHelp() {
        String user_id = DBHelper.getUser(getActivity()).getId();
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.net_not_open), 0).show();
            return;
        }
        final String action = "sns";
        User user = DBHelper.getUser(getActivity());
        Map<String, String> map = new HashMap<String, String>();
        map.put("action", action);
        map.put("user_id", "" + user.getId());
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().get(Constants.URL_GET_APP_HELP_DATA, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(getActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                appHelpData = gson.fromJson(data, AppHelpData.class);
                                TipPopWindow addPopWindow = new TipPopWindow(getActivity(), appHelpData, action);
                                addPopWindow.showPopupWindow(vs);
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
                    UIUtils.showToast(getActivity(), errorMsg);
                }
            }
        });
    }

}
