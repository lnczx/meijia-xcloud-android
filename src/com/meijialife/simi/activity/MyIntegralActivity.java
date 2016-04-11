package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import org.json.JSONObject;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.meijialife.simi.adapter.MyScoreAdapter;
import com.meijialife.simi.bean.MyScore;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * @description：我的积分明细列表
 * @author： kerryg
 * @date:2016年3月3日
 */
public class MyIntegralActivity extends Activity implements OnClickListener {

    private MyScoreAdapter adapter;// 积分适配器
    private ArrayList<MyScore> scorelist;// 卡片数据
    private ArrayList<MyScore> totalScoreList;// 所有积分数据

    // 控件声明
    private ImageView mCardBack;
    private TextView mCardTitle;
    private LinearLayout mLlCard;
    private RelativeLayout mRlCard;
    private ImageView mTitleIcon;
    private TextView mTextScore;// 积分
    private PullToRefreshListView mPullRefreshListView;// 上拉刷新的控件

    // 基本类型声明
    private int page = 1;// 分页page
    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.my_integral_activity);
        super.onCreate(savedInstanceState);
        userInfo = DBHelper.getUserInfo(MyIntegralActivity.this);
        initView();

    }

    private void initView() {

        findView();
        setOnClick();// 设置点击事件
        setTitleColor();// 设置标题颜色
        setTitleBarColor();// 设置沉浸栏样式
        initListView();// 初始化列表
    }

    private void findView() {
        mLlCard = (LinearLayout) findViewById(R.id.m_ll_card);
        mRlCard = (RelativeLayout) findViewById(R.id.view_card_title_bar);
        mTitleIcon = (ImageView) findViewById(R.id.m_title_icon);// 标题问号
        mTextScore = (TextView) findViewById(R.id.tv_money);// 积分
        mCardTitle = (TextView) findViewById(R.id.m_tv_card_title);// 标题
        mTitleIcon.setVisibility(View.GONE);
        mTextScore.setText(userInfo.getScore() + "分");
    }

    private void initListView() {
        scorelist = new ArrayList<MyScore>();
        totalScoreList = new ArrayList<MyScore>();
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.my_score_list);
        adapter = new MyScoreAdapter(MyIntegralActivity.this);
        mPullRefreshListView.setAdapter(adapter);
        mPullRefreshListView.setMode(Mode.BOTH);
        initIndicator();
        getScoreListData(page);
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                page = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getScoreListData(page);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(), "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if (scorelist != null && scorelist.size() >= 10) {
                    page = page + 1;
                    getScoreListData(page);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MyIntegralActivity.this, "请稍后，没有更多加载数据", Toast.LENGTH_SHORT).show();
                    mPullRefreshListView.onRefreshComplete();
                }
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
     * 设置点击事件
     */
    private void setOnClick() {
        findViewById(R.id.m_iv_card_back).setOnClickListener(this);
        findViewById(R.id.btn_recharge).setOnClickListener(this);
        findViewById(R.id.m_score_help).setOnClickListener(this);
    }

    /**
     * 设置沉浸栏样式
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setTitleBarColor() {
        /**
         * 沉浸栏方式实现(android4.4以上)
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {// 4.4以上
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

    }

    /**
     * 设置标题颜色
     * 
     * @param cardType
     */
    private void setTitleColor() {
        mCardTitle.setText("我的积分");
        mLlCard.setBackgroundColor(getResources().getColor(R.color.score_title_bg));
        mRlCard.setBackgroundColor(getResources().getColor(R.color.score_title_bg));
    }

    public void getScoreListData(int page) {
        String user_id = DBHelper.getUser(this).getId();
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id + "");
        map.put("page", page + "");
        AjaxParams param = new AjaxParams(map);

        new FinalHttp().get(Constants.URL_GET_SCORE_DETAILS, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(MyIntegralActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                        String data = obj.getString("data").trim();
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            if (StringUtils.isNotEmpty(data.trim())) {
                                Gson gson = new Gson();
                                scorelist = new ArrayList<MyScore>();
                                scorelist = gson.fromJson(data, new TypeToken<ArrayList<MyScore>>() {
                                }.getType());
                                showData(scorelist);
                            } else {
                                adapter.setData(new ArrayList<MyScore>());
                                mPullRefreshListView.onRefreshComplete();
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
                    mPullRefreshListView.onRefreshComplete();
                    UIUtils.showToast(MyIntegralActivity.this, errorMsg);
                }
            }
        });
    }

    /**
     * 处理数据加载的方法
     * 
     * @param list
     */
    private void showData(List<MyScore> myScoreList) {
        if (page == 1) {
            totalScoreList.clear();
            for (MyScore myScore : myScoreList) {
                totalScoreList.add(myScore);
            }
        }
        if (page >= 2) {
            for (MyScore myScore : myScoreList) {
                totalScoreList.add(myScore);
            }
        }
        // 给适配器赋值
        adapter.setData(totalScoreList);
        mPullRefreshListView.onRefreshComplete();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        page = 1;
        totalScoreList = new ArrayList<MyScore>();
        scorelist = new ArrayList<MyScore>();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getScoreListData(page);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
        case R.id.btn_recharge:
            intent = new Intent();
            intent.setClass(this, PointsShopActivity.class);
            intent.putExtra("navColor", "#E8374A"); // 配置导航条的背景颜色，请用#ffffff长格式。
            intent.putExtra("titleColor", "#ffffff"); // 配置导航条标题的颜色，请用#ffffff长格式。
            intent.putExtra("url", Constants.URL_POST_SCORE_SHOP + "?user_id=" + DBHelper.getUserInfo(this).getUser_id()); // 配置自动登陆地址，每次需服务端动态生成。
            startActivity(intent);
            break;
        case R.id.m_score_help:
            intent = new Intent(this, WebViewsActivity.class);
            intent.putExtra("url", Constants.SCORE_HELP_URL);
            startActivity(intent);
            break;
        case R.id.m_iv_card_back:
            finish();
            break;
        default:
            break;
        }
    }

}
