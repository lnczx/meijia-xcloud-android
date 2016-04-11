package com.meijialife.simi.fra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.BaseFragment;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.AllPartnerListActivity;
import com.meijialife.simi.activity.FriendPageActivity;
import com.meijialife.simi.activity.PointsShopActivity;
import com.meijialife.simi.activity.WebViewsActivity;
import com.meijialife.simi.adapter.HomeListAdapter;
import com.meijialife.simi.bean.AdData;
import com.meijialife.simi.bean.HomePosts;
import com.meijialife.simi.bean.HomeTag;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.ui.MyViewPager;
import com.meijialife.simi.ui.RouteUtil;
import com.meijialife.simi.ui.SignPopWindow;
import com.meijialife.simi.ui.TipPopWindow;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;
import com.umeng.update.UmengUpdateAgent;
import com.zbar.lib.CaptureActivity;

/**
 * 首页
 * 
 * @author RUI
 * 
 */
public class Home1NewFra extends BaseFragment implements OnClickListener {

    private View v;
    private View v1;
    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    private final static int SCANNIN_GREQUEST_CODES = 5;
    
    private List<AdData> adList;
    private List<ImageView> imageViews;
    private List<View> dots; // 图片标题正文的那些点
    private List<View> dotList;
    // 控件声明
    private MyViewPager adViewPager;
//    private ViewPager adViewPager;
    private int currentItem = 0; // 当前图片的索引号
    // 定义的五个指示点
    private View dot0;
    private View dot1;
    private View dot2;
    private View dot3;
    private View dot4;
    private View dot5;
    private View dot6;
    private View dot7;
    private View dot8;
    MyAdapter myAdapter;
    boolean canscoll = false;

    //列表
    private ListView mListView;
    private HomeTag homeTag;
    private List<HomePosts> homePosts;
    private List<HomePosts> allHomePosts;
    private HomeListAdapter homeListAdapter;
    private View headerView;
    private View footView;
    private Button loadMoreButton;
    private LinearLayout mLlLoadMore;
    
    private int page =1;

    // 定时任务
    private ScheduledExecutorService scheduledExecutorService;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            adViewPager.setCurrentItem(currentItem);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.home_1, null);
        v1 = inflater.inflate(R.layout.home_1, null);
        headerView =inflater.inflate(R.layout.home1_banner, null);
        footView = inflater.inflate(R.layout.load_more, null);  


        // 设置友盟更新
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.update(getActivity());
        
        initView(v);
        getMsgList(page);
        getAdList();
        return v;
    }

    private void initView(View v) {
        //初始化首页广告
        finalBitmap = FinalBitmap.create(getActivity());
        defDrawable = (BitmapDrawable) getActivity().getResources().getDrawable(R.drawable.ad_loading);
       
        initListView(v);
        setListener(v);
    }
    
    
    private void setListener(View v){
        v.findViewById(R.id.m_home1).setOnClickListener(this);
        v.findViewById(R.id.m_home2).setOnClickListener(this);
        v.findViewById(R.id.m_home3).setOnClickListener(this);
        v.findViewById(R.id.m_home4).setOnClickListener(this);
        v.findViewById(R.id.btn_saoma).setOnClickListener(this);
        
    }
    private void initListView(View v){
        homePosts = new ArrayList<HomePosts>();
        allHomePosts = new ArrayList<HomePosts>();
        mListView = (ListView)v.findViewById(R.id.m_lv_home);
        mLlLoadMore = (LinearLayout)v.findViewById(R.id.m_ll_load_more);
        mListView.addHeaderView(headerView);
        mListView.addFooterView(footView);
        loadMoreButton = (Button) v.findViewById(R.id.loadMoreButton);
        homeListAdapter = new HomeListAdapter(getActivity());
        mListView.setAdapter(homeListAdapter);
      
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int p = position-1;
                HomePosts homePost = allHomePosts.get(p);
                Intent intent = new Intent(getActivity(), WebViewsActivity.class);
                intent.putExtra("url", homePost.getUrl());
                getActivity().startActivity(intent);            }
        });
        //加载更多
        loadMoreButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                page+=1;
                getMsgList(page);
            }
        });
   
    }

    private void initBanner(View v){
       
        adList = new ArrayList<AdData>();
        imageViews = new ArrayList<ImageView>();
        //点
        dots = new ArrayList<View>();
        dotList = new ArrayList<View>();
        dot0 = v.findViewById(R.id.v_dot0);
        dot1 = v.findViewById(R.id.v_dot1);
        dot2 = v.findViewById(R.id.v_dot2);
        dot3 = v.findViewById(R.id.v_dot3);
        dot4 = v.findViewById(R.id.v_dot4);
        dot5 = v.findViewById(R.id.v_dot5);
        dot6 = v.findViewById(R.id.v_dot6);
        dot7 = v.findViewById(R.id.v_dot7);
        dot8 = v.findViewById(R.id.v_dot8);
        dots.add(dot0);
        dots.add(dot1);
        dots.add(dot2);
        dots.add(dot3);
        dots.add(dot4);
        dots.add(dot5);
        dots.add(dot6);
        dots.add(dot7);
        dots.add(dot8);

        adViewPager = (MyViewPager) v.findViewById(R.id.vp);
        myAdapter = new MyAdapter();
        adViewPager.setAdapter(myAdapter);// 设置填充ViewPager页面的适配器
        adViewPager.setOnPageChangeListener(new MyPageChangeListener());
    }
    /**
     * 动态添加图片和下面指示的圆点
     */
    private void addDynamicView(List<AdData> adList) {
        // 初始化图片资源
        for (int i = 0; i < adList.size(); i++) {
            ImageView imageView = new ImageView(getActivity());
            // 异步加载图片
            finalBitmap.display(imageView, adList.get(i).getImg_url(),defDrawable.getBitmap(),defDrawable.getBitmap());
           
            imageView.setScaleType(ScaleType.FIT_XY);
            imageViews.add(imageView);
            dots.get(i).setVisibility(View.VISIBLE);
            dotList.add(dots.get(i));
        }
    }

    private void startAd() {
        // 当Activity显示出来后，每两秒切换一次图片显示
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 2, TimeUnit.SECONDS);
    }

    private class ScrollTask implements Runnable {
        @Override
        public void run() {
            synchronized (adViewPager) {
                currentItem = (currentItem + 1) % imageViews.size();
                handler.obtainMessage().sendToTarget();
            }
        }
    }
    
    /**
     * page改变时的事件
     * @description：
     * @author： kerryg
     * @date:2016年4月6日
     */
    private class MyPageChangeListener implements OnPageChangeListener {

        private int oldPosition = 0;

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int position) {
            currentItem = position;
          /*  AdData adData = adList.get(position);
            Intent intent = new Intent(getActivity(), WebViewsActivity.class);
            intent.putExtra("url", adData.getGoto_url());
            getActivity().startActivity(intent); */
            dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
            dots.get(position).setBackgroundResource(R.drawable.dot_focused);
            oldPosition = position;
        }
    }
    /**
     * PageAdapter适配器
     * @description：
     * @author： kerryg
     * @date:2016年4月6日
     */
    private class MyAdapter extends PagerAdapter {
        
        private List<AdData> adLists;
        private List<ImageView> imageView;
        
        public MyAdapter() {
            this.adLists = new ArrayList<AdData>();
            this.imageView = new ArrayList<ImageView>();
        }
        public void setData(List<AdData> adList, List<ImageView> imageViews){
            this.adLists = adList;
            this.imageView = imageViews;
            notifyDataSetChanged();
        }
        @Override
        public int getCount() {
            return adLists.size();
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView iv = imageView.get(position);
            ((ViewPager) container).addView(iv);
            final AdData adDomain = adLists.get(position);
            // 在这个方法里面设置图片的点击事件
            iv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), WebViewsActivity.class);
                    intent.putExtra("url",adDomain.getGoto_url());
                    getActivity().startActivity(intent); 
                }
            });
            return iv;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {

        }

        @Override
        public void finishUpdate(View arg0) {

        }

    }

    @Override
    public void onStop() {
        super.onStop();
        scheduledExecutorService.shutdown();
    }

    /**
     * 获得首页广告位接口
     */
    public void getAdList() {
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("ad_type", 0 + "");// 广告类型ID 0 = 首页 其他为应用中心的ID
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().get(Constants.GET_HOME1_BANNERS_URL, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(getActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                adList = gson.fromJson(data, new TypeToken<ArrayList<AdData>>() {
                                }.getType());
                                showBanner(adList);
                            } else {
                                adList = new ArrayList<AdData>();
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

    protected void showBanner(List<AdData> adList) {
        initBanner(headerView);
        addDynamicView(adList);
        myAdapter.setData(adList, imageViews);
        startAd();
    }
    
    
    /**
     * 获得所有首页列表接口
     */
    public void getMsgList(int page) {
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("json","get_tag_posts");
        map.put("count", "5");
        map.put("order", "ASC");
        map.put("slug", "%E9%A6%96%E9%A1%B5%E7%B2%BE%E9%80%89");
        map.put("include", "id,title,modified,url,thumbnail,custom_fields");
        map.put("page", page+"");
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().post(Constants.GET_HOME1_MSG_URL, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(getActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        String status = obj.getString("status");
                        int count = obj.getInt("count");
                        String data = obj.getString("pages");
                        String tag = obj.getString("tag");
                        String posts = obj.getString("posts");
                        if (StringUtils.isEquals(status,"ok")) { // 正确
                            if (StringUtils.isNotEmpty(data)) {
                                Gson gson = new Gson();
                                homePosts = gson.fromJson(posts, new TypeToken<ArrayList<HomePosts>>() {
                                }.getType());
                                homeTag = gson.fromJson(tag,HomeTag.class);
                                showData(homePosts, homeTag);
                                if(homePosts.size()<5){
                                    loadMoreButton.setText("没有更多");
                                    loadMoreButton.setTextColor(getActivity().getResources().getColor(R.color.simi_color_gray));
                                    loadMoreButton.setClickable(false);
                                }else {
                                    loadMoreButton.setText("查看更多");
                                    loadMoreButton.setClickable(true);
                                }
                                }
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
    
    private void showData(List<HomePosts> homePosts,HomeTag homeTag) {
        if (homePosts != null && homePosts.size() > 0) {
            if (page == 1) {
                allHomePosts.clear();
            }
            for (HomePosts homePost : homePosts) {
                allHomePosts.add(homePost);
            }
        }
        homeListAdapter.setData(allHomePosts, homeTag);
    }

    /**
     * 首页扫描加好友
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

        // showDialog();
        new FinalHttp().get(Constants.URL_GET_ADD_FRIEND, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                // dismissDialog();
                Toast.makeText(getActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                // dismissDialog();
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

    
    @Override
    public void onClick(View v) {
         Intent intent;
        switch (v.getId()) {
         case R.id.m_home1:
             intent = new Intent(getActivity(), WebViewsActivity.class);
             intent.putExtra("url", Constants.ZHI_SHI_XUE_YUAN_URL);
             getActivity().startActivity(intent);   
          break;
          
         case R.id.m_home2:
             intent = new Intent(getActivity(),AllPartnerListActivity.class);
             getActivity().startActivity(intent);
             break;
             
         case R.id.m_home3:
             postSign();
             break;
             
         case R.id.m_home4://积分商城
             Intent intent6 = new Intent();
             intent6.setClass(getActivity(), PointsShopActivity.class);
             intent6.putExtra("navColor", "#E8374A");    //配置导航条的背景颜色，请用#ffffff长格式。
             intent6.putExtra("titleColor", "#ffffff");    //配置导航条标题的颜色，请用#ffffff长格式。
             intent6.putExtra("url", Constants.URL_POST_SCORE_SHOP+"?user_id="+DBHelper.getUserInfo(getActivity()).getUser_id());    //配置自动登陆地址，每次需服务端动态生成。
             getActivity().startActivity(intent6);
             break;
         case R.id.btn_saoma://二维码
             Intent intents = new Intent();
             intents.setClass(getActivity(), CaptureActivity.class);
             intents.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             startActivityForResult(intents, SCANNIN_GREQUEST_CODES);
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
                    //http://www.51xingzheng.cn/d/open.html?category=app&action=feed&params=&goto_url=
                    if(!StringUtils.isEmpty(result) && result.contains("category=app")){
                    String category="",action="",params="",goto_url="";
                    if(result.contains("params") && result.contains("goto_url")){//两个参数都有
                        String temp[] = result.split("&");
                        category = temp[0].substring(temp[0].lastIndexOf("=")+1,temp[0].length());
                        action = temp[1].substring(temp[1].lastIndexOf("=")+1,temp[1].length());
                        params = temp[2].substring(temp[2].lastIndexOf("=")+1,temp[2].length());
                        goto_url = temp[3].substring(temp[3].lastIndexOf("=")+1,temp[3].length());
                        
                    }else if (result.contains("params") && !result.contains("goto_url")) {//只有参数params
                        String temp[] = result.split("&");
                        category = temp[0].substring(temp[0].lastIndexOf("=")+1,temp[0].length());
                        action = temp[1].substring(temp[1].lastIndexOf("=")+1,temp[1].length());
                        params = temp[2].substring(temp[2].lastIndexOf("=")+1,temp[2].length());
                        
                    }else if (result.contains("goto_url") && !result.contains("params")) {//只有参数goto_url
                        String temp[] = result.split("&");
                        category = temp[0].substring(temp[0].lastIndexOf("=")+1,temp[0].length());
                        action = temp[1].substring(temp[1].lastIndexOf("=")+1,temp[1].length());
                        goto_url = temp[2].substring(temp[2].lastIndexOf("=")+1,temp[2].length());
                    }else {
                        String temp[] = result.split("&");
                        category = temp[0].substring(temp[0].lastIndexOf("=")+1,temp[0].length());
                        action = temp[1].substring(temp[1].lastIndexOf("=")+1,temp[1].length());
                    }
                        if (!StringUtils.isEmpty(result)) {
                           RouteUtil routeUtil =new  RouteUtil(getActivity());
                           routeUtil.Routing(category, action, goto_url, params);
                        }
                    }else {
                        Intent intent = new Intent(getActivity(),WebViewsActivity.class);
                        intent.putExtra("url",result);
                        startActivity(intent);
                    }
                } else {//非内部app扫描，webView显示
                    Intent intent = new Intent(getActivity(),WebViewsActivity.class);
                    intent.putExtra("url",result);
                    startActivity(intent);
                }
            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    /**
     * 签到获取积分接口
     */
    private void postSign() {
        String user_id = DBHelper.getUser(getActivity()).getId();
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.net_not_open), 0).show();
            return;
        }
        User user = DBHelper.getUser(getActivity());
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id",""+user.getId());
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().post(Constants.POST_DAY_SIGN, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(getActivity(), getActivity().getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                            if(!StringUtils.isEmpty(data)){
                                SignPopWindow signPopWindow = new SignPopWindow(getActivity(),msg,data);  
                                signPopWindow.showPopupWindow(v1);
                            }
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            errorMsg = getActivity().getString(R.string.servers_error);
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            errorMsg = getActivity().getString(R.string.param_missing);
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            errorMsg = getActivity().getString(R.string.param_illegal);
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            errorMsg = msg;
                        } else {
                            errorMsg = getActivity().getString(R.string.servers_error);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = getActivity().getString(R.string.servers_error);
                }
                // 操作失败，显示错误信息
                if(!StringUtils.isEmpty(errorMsg.trim())){
                    UIUtils.showToast(getActivity(), errorMsg);
                }
            }
        });
    }

}
