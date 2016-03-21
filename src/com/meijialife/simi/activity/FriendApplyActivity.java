package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.ApplyAdapter;
import com.meijialife.simi.bean.FriendApplyData;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.inter.ListItemClickHelp;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

public class FriendApplyActivity extends BaseActivity implements ListItemClickHelp {

    private int applyPage = 1;
    // 布局控件定义
    private PullToRefreshListView mPullRefreshApplyListView;// 上拉刷动态的控件
    private ApplyAdapter applyAdapter;// 好友申请适配器

    private ArrayList<FriendApplyData> myApplyList;
    private ArrayList<FriendApplyData> totalApplyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_friend_apply);
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {

        requestBackBtn();
        setTitleName("好友申请");

        totalApplyList = new ArrayList<FriendApplyData>();
        myApplyList = new ArrayList<FriendApplyData>();
        mPullRefreshApplyListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list_apply);
        applyAdapter = new ApplyAdapter(FriendApplyActivity.this, this);
        mPullRefreshApplyListView.setAdapter(applyAdapter);
        mPullRefreshApplyListView.setMode(Mode.BOTH);
        getApplyList(applyPage);
        initIndicators();
        mPullRefreshApplyListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                applyPage = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getApplyList(applyPage);
                applyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if (myApplyList != null && myApplyList.size() >= 10) {
                    applyPage = applyPage + 1;
                    getApplyList(applyPage);
                    applyAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(FriendApplyActivity.this, "请稍后，没有更多加载数据", Toast.LENGTH_SHORT).show();
                    mPullRefreshApplyListView.onRefreshComplete();
                }
            }
        });
        mPullRefreshApplyListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*
                 * Intent intent = new Intent(getActivity(), FriendPageActivity.class); intent.putExtra("friend_id",
                 * myFriendList.get(position).getFriend_id()); startActivity(intent);
                 */
            }
        });
    }

    /**
     * 设置下拉刷新提示
     */
    private void initIndicators() {
        ILoadingLayout startLabels = mPullRefreshApplyListView.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在刷新...");// 刷新时
        startLabels.setReleaseLabel("释放更新");// 下来达到一定距离时，显示的提示

        ILoadingLayout endLabels = mPullRefreshApplyListView.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("上拉加载");
        endLabels.setRefreshingLabel("正在刷新...");// 刷新时
        endLabels.setReleaseLabel("释放加载");// 下来达到一定距离时，显示的提示
    }

    /**
     * 好友申请列表接口
     * 
     * @param friendPage
     */
    public void getApplyList(int applyPage) {

        String user_id = DBHelper.getUser(this).getId();

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id + "");
        map.put("page", "" + applyPage);
        AjaxParams param = new AjaxParams(map);

        new FinalHttp().get(Constants.URL_GET_FRIEND_REQS, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(FriendApplyActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            if (StringUtils.isNotEmpty(data)) {
                                Gson gson = new Gson();
                                myApplyList = gson.fromJson(data, new TypeToken<ArrayList<FriendApplyData>>() {
                                }.getType());
                                showApplyData(myApplyList);
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
                    UIUtils.showToast(FriendApplyActivity.this, errorMsg);
                }
            }
        });
    }

    private void showApplyData(List<FriendApplyData> myApplyList) {
        if (applyPage == 1) {
            totalApplyList.clear();
        }
        for (FriendApplyData myApply : myApplyList) {
            totalApplyList.add(myApply);
        }
        // 给适配器赋值
        applyAdapter.setData(totalApplyList);
        mPullRefreshApplyListView.onRefreshComplete();
    }

    @Override
    public void onClick() {
        getApplyList(applyPage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        applyPage = 1;
        totalApplyList = new ArrayList<FriendApplyData>();
        myApplyList = new ArrayList<FriendApplyData>();
    }

}
