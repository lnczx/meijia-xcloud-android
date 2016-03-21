package com.meijialife.simi.fra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
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
import com.meijialife.simi.activity.Find2DetailActivity;
import com.meijialife.simi.activity.SearchViewActivity;
import com.meijialife.simi.activity.WebViewsFindActivity;
import com.meijialife.simi.adapter.Find2Adapter;
import com.meijialife.simi.adapter.FindAllAdapter;
import com.meijialife.simi.bean.AppHelpData;
import com.meijialife.simi.bean.ChanelBean;
import com.meijialife.simi.bean.FindBean;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.ui.SyncHorizontalScrollView;
import com.meijialife.simi.ui.TipPopWindow;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * @description：发现--秘书助理，综合服务，设计策划
 * @author： kerryg
 * @date:2015年11月13日
 */
@SuppressLint("ResourceAsColor")
public class Find2Fra extends BaseFragment {

    private MainActivity activity;
    private ListView listview;
    private Find2Adapter findAdapter;// 服务商适配器
    private RelativeLayout rl_total_search;// 搜索框
    /**
     * 左右滑动控件声明
     */
    private RelativeLayout rl_nav;
    private SyncHorizontalScrollView mHsv;
    private RadioGroup rg_nav_content;
    private ImageView iv_nav_indicator;
    private ImageView iv_nav_left;
    private ImageView iv_nav_right;
    private int indicatorWidth;
    public ArrayList<String> tabTitle;// 标题
    private LayoutInflater mInflater;
    private int currentIndicatorLeft = 0;

    private ArrayList<FindBean> findBeanList;
    private ArrayList<ChanelBean> chanelBeanList;
    private String title_id = "1";
    private String title_name = "发现";

    private RadioGroup myRadioGroup;
    private int _id = 1000;
    private LinearLayout layout, titleLayout;
    private TextView textView;
    private ImageView mImageView;
    private float mCurrentCheckedRadioLeft;// 当前被选中的RadioButton距离左侧的距离
    private HorizontalScrollView mHorizontalScrollView;// 上面的水平滚动控件
    private View v = null;
    
    private ArrayList<RadioButton> buttonList;
    private LinearLayout find_other_list;
    private LinearLayout find_all_list;
    private GridView gv_application1;
    private FindAllAdapter appToolsAdapter1;
    private User user;
    
    private ArrayList<FindBean> myFindBeanList;
    private ArrayList<FindBean> totalFindBeanList;
    private PullToRefreshListView mPullRefreshListView;//上拉刷新的控件 
    private int page = 1;
    private View vs;//


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.find_list, null);
        vs= (FrameLayout)getActivity().getLayoutInflater()
                .inflate(R.layout.personal_fragment, null);
        findViewById(v);
        init(v);
        setListener();
//        getFind2List(title_id,page);
        return v;
    }

    private void findViewById(View v) {
        /**
         * 标题滑动
         */
        user = DBHelper.getUser(getActivity());
        rl_nav = (RelativeLayout) v.findViewById(R.id.rl_nav);
        mHsv = (SyncHorizontalScrollView) v.findViewById(R.id.mHsv);
        rg_nav_content = (RadioGroup) v.findViewById(R.id.rg_nav_content);
        iv_nav_indicator = (ImageView) v.findViewById(R.id.iv_nav_indicator);
        iv_nav_left = (ImageView) v.findViewById(R.id.iv_nav_left);
        iv_nav_right = (ImageView) v.findViewById(R.id.iv_nav_right);
        
        buttonList = new ArrayList<RadioButton>();
        titleLayout = (LinearLayout) v.findViewById(R.id.title_lay);
        layout = (LinearLayout) v.findViewById(R.id.lay);
        mImageView = (ImageView) v.findViewById(R.id.img1);
        mHorizontalScrollView = (HorizontalScrollView) v.findViewById(R.id.horizontalScrollView);

        find_all_list = (LinearLayout)v.findViewById(R.id.find_all_list);
        find_other_list = (LinearLayout)v.findViewById(R.id.find_other_list);
        gv_application1 = (GridView) v.findViewById(R.id.gv_application1);
        appToolsAdapter1 = new FindAllAdapter(getActivity());
        gv_application1.setAdapter(appToolsAdapter1);
        
        setOnClick();
        chanelBeanList = new ArrayList<ChanelBean>();
        /**
         * 搜索+列表展现
         */
        rl_total_search = (RelativeLayout) v.findViewById(R.id.rl_total_search);
      /*  listview = (ListView) v.findViewById(R.id.find_list_view);
        adapter = new Find2Adapter(activity);
        listview.setAdapter(adapter);*/
        
        //请求帮助接口
        getAppHelp();
        initFindBeanView(v);
        getChanelList();
    }
    private void initFindBeanView(final View v){
        totalFindBeanList = new ArrayList<FindBean>();
        myFindBeanList = new ArrayList<FindBean>();
        mPullRefreshListView = (PullToRefreshListView)v.findViewById(R.id.pull_refresh_finds_list);
        findAdapter = new Find2Adapter(activity);
        mPullRefreshListView.setAdapter(findAdapter);
        mPullRefreshListView.setMode(Mode.BOTH);
        initIndicator();
        getFind2List(title_id,page);
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                page = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                int radioButtonId = myRadioGroup.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton)v.findViewById(radioButtonId);
                rb.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                ChanelBean chanelBean = (ChanelBean) rb.getTag();
                getFind2List(chanelBean.getChannel_id(),page);
//                findAdapter.notifyDataSetChanged(); 
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if(myFindBeanList!=null && myFindBeanList.size()>=10){
                    page = page+1;
                    int radioButtonId = myRadioGroup.getCheckedRadioButtonId();
                    RadioButton rb = (RadioButton)v.findViewById(radioButtonId);
                    rb.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                    ChanelBean chanelBean = (ChanelBean) rb.getTag();
                    getFind2List(chanelBean.getChannel_id(),page);
//                    findAdapter.notifyDataSetChanged(); 
                }else {
                    Toast.makeText(activity,"请稍后，没有更多加载数据",Toast.LENGTH_SHORT).show();
                    mPullRefreshListView.onRefreshComplete(); 
                }
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
    private void setOnClick(){
        gv_application1.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FindBean findBean = myFindBeanList.get(position);
              
                String title_name = findBean.getTitle().trim();
                String goto_type = findBean.getGoto_type().trim();
                String goto_url = findBean.getGoto_url().trim();
                String service_type_ids = findBean.getService_type_ids().toString().trim();
                if (goto_type.equals("h5")) {
                    Intent intent = new Intent(getActivity(), WebViewsFindActivity.class);
                    intent.putExtra("url", goto_url);
                    intent.putExtra("title_name", "");
                    intent.putExtra("service_type_ids", "");
                    startActivity(intent);
                } else if (goto_type.equals("app")) {
                    Intent intent = new Intent(getActivity(), Find2DetailActivity.class);
                    intent.putExtra("service_type_ids", service_type_ids);
                    intent.putExtra("title_name", title_name);
                    startActivity(intent);
                } else if (goto_type.equals("h5+list")) {
                    Intent intent = new Intent(getActivity(), WebViewsFindActivity.class);
                    intent.putExtra("url", goto_url);
                    intent.putExtra("title_name", title_name);
                    intent.putExtra("service_type_ids", service_type_ids);
                    startActivity(intent);
                }
            }
        });
    }


    /*
     * 初始化适配器
     */
    public void init(View v) {
        //获取屏幕宽度
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        indicatorWidth = dm.widthPixels / 4;
        
       /* 获得屏幕宽度的另一种方法
        * DisplayMetrics dm1 = getResources().getDisplayMetrics();
            dm1.heightPixels;
        */
        LayoutParams cursor_Params = (LayoutParams) iv_nav_indicator.getLayoutParams();
        cursor_Params.width = indicatorWidth;// 初始化滑动下标的宽
        iv_nav_indicator.setLayoutParams(cursor_Params);

        mHsv.setSomeParam(rl_nav, iv_nav_left, iv_nav_right, getActivity());
        // 获取布局填充器
        // mInflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        mInflater = LayoutInflater.from(getActivity());
        
    }

    private void setListener() {
        rg_nav_content.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton)rg_nav_content.getChildAt((checkedId));
                if (rb != null) {
                    TranslateAnimation animation = new TranslateAnimation(currentIndicatorLeft, ((RadioButton) rg_nav_content.getChildAt(checkedId))
                            .getLeft(), 0f, 0f);
                    animation.setInterpolator(new LinearInterpolator());
                    animation.setDuration(100);
                    animation.setFillAfter(true);
                    // 执行位移动画
                    iv_nav_indicator.startAnimation(animation);
                    // 记录当前 下标的距最左侧的 距离
                    currentIndicatorLeft = ((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft();
                    mHsv.smoothScrollTo((checkedId > 1 ? ((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft() : 0)
                            - ((RadioButton) rg_nav_content.getChildAt(2)).getLeft(), 0);
                    title_name = rb.getText().toString().trim();
                    getFind2List((checkedId+1)+"",page);
                }
            }
        });

        mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView tv_gotoType = (TextView) view.findViewById(R.id.tv_goto_type);
                TextView tv_gotoUrl = (TextView) view.findViewById(R.id.tv_goto_url);
                TextView tv_service_type_ids = (TextView) view.findViewById(R.id.tv_service_type_ids);

                String goto_type = tv_gotoType.getText().toString().trim();
                String goto_url = tv_gotoUrl.getText().toString().trim();
                String service_type_ids = tv_service_type_ids.getText().toString().trim();
                if (goto_type.equals("h5")) {
                    Intent intent = new Intent(getActivity(), WebViewsFindActivity.class);
                    intent.putExtra("url", goto_url);
                    intent.putExtra("title_name", "");
                    intent.putExtra("service_type_ids", "");
                    startActivity(intent);
                } else if (goto_type.equals("app")) {
                    Intent intent = new Intent(getActivity(), Find2DetailActivity.class);
                    intent.putExtra("service_type_ids", service_type_ids);
                    intent.putExtra("title_name", title_name);
                    startActivity(intent);
                } else if (goto_type.equals("h5+list")) {
                    Intent intent = new Intent(getActivity(), WebViewsFindActivity.class);
                    intent.putExtra("url", goto_url);
                    intent.putExtra("title_name", title_name);
                    intent.putExtra("service_type_ids", service_type_ids);
                    startActivity(intent);
                }
            }
        });

        rl_total_search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SearchViewActivity.class));
            }
        });

    }

    private void initGroup(ArrayList<ChanelBean> titleList) {

        myRadioGroup = new RadioGroup(getActivity());
        myRadioGroup.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        myRadioGroup.setOrientation(LinearLayout.HORIZONTAL);
        layout.addView(myRadioGroup);
        for (int i = 0; i < titleList.size(); i++) {
            ChanelBean chanelBean = titleList.get(i);
            RadioButton radio = (RadioButton) mInflater.inflate(R.layout.nav_radiogroup_item, null);
            radio.setId(_id + i);
            radio.setText(chanelBean.getName());
            radio.setTag(chanelBean);
            if (i == 0) {
                radio.setChecked(true);
                int itemWidth = (int) radio.getPaint().measureText(chanelBean.getName());
                mImageView.setLayoutParams(new LinearLayout.LayoutParams(itemWidth + radio.getPaddingLeft() + radio.getPaddingRight(), 4));
            }
            myRadioGroup.addView(radio);
            buttonList.add(radio);
        }
        myRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
            /*    
                if (checkedId==5) {//全部
                    find_all_list.setVisibility(View.VISIBLE);
                    find_other_list.setVisibility(View.GONE);
                }else {
                    find_all_list.setVisibility(View.GONE);
                    find_other_list.setVisibility(View.VISIBLE);
                }*/
                for (Iterator iterator = buttonList.iterator(); iterator.hasNext();) {
                    RadioButton button = (RadioButton) iterator.next();
                    int buttonId =button.getId();
                    if(checkedId==buttonId){
                        int radioButtonId = group.getCheckedRadioButtonId();
                        // 根据ID获取RadioButton的实例
                        RadioButton rb = (RadioButton) v.findViewById(radioButtonId);
                        rb.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                        ChanelBean chanelBean = (ChanelBean) rb.getTag();
                        AnimationSet animationSet = new AnimationSet(true);
                        TranslateAnimation translateAnimation;
                        translateAnimation = new TranslateAnimation(mCurrentCheckedRadioLeft, rb.getLeft(), 0f, 0f);
                        animationSet.addAnimation(translateAnimation);
                        animationSet.setFillBefore(true);
                        animationSet.setFillAfter(true);
                        animationSet.setDuration(300);

                        mImageView.startAnimation(animationSet);// 开始上面蓝色横条图片的动画切换
                        mCurrentCheckedRadioLeft = rb.getLeft();// 更新当前蓝色横条距离左边的距离
                        mHorizontalScrollView.smoothScrollTo((int) mCurrentCheckedRadioLeft - (int) getResources().getDimension(R.dimen.rdo2), 0);
                        mImageView.setLayoutParams(new LinearLayout.LayoutParams(rb.getRight() - rb.getLeft(), 4));
                        getFind2List(chanelBean.getChannel_id(),page);
                    }else {
                        RadioButton rb = (RadioButton)group.findViewById(buttonId);  
                        rb.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
                    }
                }
            }
        });

    }

    public Find2Fra() {
        super();
    }

    public Find2Fra(MainActivity activity) {
        super();
        this.activity = activity;
    }

    /**
     * 获得频道中广告信息接口（图片+文字）
     */
    public void getFind2List(final String channel_id,int page) {
        if (!NetworkUtils.isNetworkConnected(activity)) {
            Toast.makeText(activity, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("channel_id", channel_id);
        map.put("page", ""+page);
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().get(Constants.URL_GET_ADS_LIST, param, new AjaxCallBack<Object>() {
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
                                myFindBeanList = gson.fromJson(data, new TypeToken<ArrayList<FindBean>>() {
                                }.getType());
                                if(channel_id.equals("99")){//99=全部
                                    find_all_list.setVisibility(View.VISIBLE);
                                    find_other_list.setVisibility(View.GONE);
                                    appToolsAdapter1.setData(myFindBeanList);
                                }else {
                                    find_all_list.setVisibility(View.GONE);
                                    find_other_list.setVisibility(View.VISIBLE);
//                                    adapter.setData(findBeanList);
                                    showData(myFindBeanList);
                                }
                            } else {
                                if(channel_id.equals("99")){//99=全部
                                    find_all_list.setVisibility(View.VISIBLE);
                                    find_other_list.setVisibility(View.GONE);
                                    appToolsAdapter1.setData(new ArrayList<FindBean>());
                                }else {
                                    find_all_list.setVisibility(View.GONE);
                                    find_other_list.setVisibility(View.VISIBLE);
//                                    adapter.setData(new ArrayList<FindBean>());
                                    showData(new ArrayList<FindBean>());
                                }
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
    @Override
    public void onDestroy() {
        super.onDestroy();
        buttonList =null;
        page = 1;
        myFindBeanList = null;
        totalFindBeanList = null;
    }

    /**
     * 获得频道列表接口
     */
    public void getChanelList() {
        if (!NetworkUtils.isNetworkConnected(activity)) {
            Toast.makeText(activity, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().get(Constants.URL_GET_CHANEL_LIST, param, new AjaxCallBack<Object>() {
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
                                chanelBeanList = gson.fromJson(data, new TypeToken<ArrayList<ChanelBean>>() {
                                }.getType());
                            } else {
                                chanelBeanList = new ArrayList<ChanelBean>();
                            }
                            initGroup(chanelBeanList);
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
                    if (isAdded()) {// 在Fragment中使用系统资源必须增加判断
                        errorMsg = getString(R.string.servers_error);
                    }
                }
                // 操作失败，显示错误信息
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    UIUtils.showToast(activity, errorMsg);
                }
            }
        });
    }
    /**
     * 处理数据加载的方法
     * @param list
     */
    private void showData(List<FindBean> myFindBeanList){
        if(myFindBeanList!=null && myFindBeanList.size()>0){
            if(page==1){
                totalFindBeanList.clear();
            }
            for (FindBean findBean : myFindBeanList) {
                totalFindBeanList.add(findBean);
            }
            //给适配器赋值
            findAdapter.setData(totalFindBeanList);
        }
        mPullRefreshListView.onRefreshComplete();
    }
    
    

 
    private AppHelpData appHelpData;
  
    /*
     * 帮助接口
     */
    
    private void getAppHelp() {
        String user_id = DBHelper.getUser(getActivity()).getId();
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.net_not_open), 0).show();
            return;
        }
        final String action ="discover";
        User user = DBHelper.getUser(getActivity());
        Map<String, String> map = new HashMap<String, String>();
        map.put("action","discover");
        map.put("user_id",""+user.getId());
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
                            if(StringUtils.isNotEmpty(data)){
                                Gson gson = new Gson();
                                appHelpData = gson.fromJson(data, AppHelpData.class); 
                                TipPopWindow addPopWindow = new TipPopWindow(getActivity(),appHelpData,action);  
                                addPopWindow.showPopupWindow(v); 
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
                if(!StringUtils.isEmpty(errorMsg.trim())){
                    UIUtils.showToast(getActivity(), errorMsg);
                }
            }
        });
    }
    
}
