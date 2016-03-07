package com.meijialife.simi.fra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.joda.time.LocalDate;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
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
import com.meijialife.simi.activity.CardDetailsActivity;
import com.meijialife.simi.activity.DynamicDetailsActivity;
import com.meijialife.simi.activity.Find2DetailActivity;
import com.meijialife.simi.activity.FriendPageActivity;
import com.meijialife.simi.activity.MainPlusWaterActivity;
import com.meijialife.simi.activity.OrderDetailsActivity;
import com.meijialife.simi.activity.WebViewsActivity;
import com.meijialife.simi.activity.WebViewsFindActivity;
import com.meijialife.simi.adapter.ListAdapter;
import com.meijialife.simi.adapter.ListAdapter.onCardUpdateListener;
import com.meijialife.simi.adapter.UserMsgListAdapter;
import com.meijialife.simi.bean.AppHelpData;
import com.meijialife.simi.bean.CalendarMark;
import com.meijialife.simi.bean.CardExtra;
import com.meijialife.simi.bean.Cards;
import com.meijialife.simi.bean.FindBean;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.bean.UserMsg;
import com.meijialife.simi.bean.WaterData;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.ui.CollapseCalendarView;
import com.meijialife.simi.ui.CollapseCalendarView.OnDateSelect;
import com.meijialife.simi.ui.ImageCycleView;
import com.meijialife.simi.ui.ImageCycleView.ImageCycleViewListener;
import com.meijialife.simi.ui.SelectableRoundedImageView;
import com.meijialife.simi.ui.calendar.CalendarManager;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.simi.easemob.EMConstant;
import com.simi.easemob.ui.ChatActivity;
import com.zbar.lib.CaptureActivity;

/**
 * 首页
 * 
 * @author RUI
 * 
 */
public class Home1Fra extends BaseFragment implements OnClickListener, onCardUpdateListener {

    public String today_date;
    private CollapseCalendarView calendarView;// 日历View
    private CalendarManager calendarManager;// 日历管理
    public static ArrayList<CalendarMark> calendarMarks;// 日历当月所有有卡片的日期

    private ListView listview;
    private ListAdapter adapter;
    private ArrayList<Cards> cardlist;// 卡片数据
    private ArrayList<CardExtra> cardExtrasList;

    private TextView tv_tips;// 没有数据时的提示
    private ImageView iv_no_card;// 没有数据时提示 图片

    private int card_from; // 0 = 所有卡片 1 = 我发布的 2 = 我参与的,默认为0

    private static View layout_mask, layout_guide;
    private final static int SCANNIN_GREQUEST_CODES = 5;
    private View v;
    private UserInfo userInfo;
    private ArrayList<FindBean> findBeanList;
    
    TextView tv_service_type_ids;//服务大类集合
    
    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
  
    private String title_name = "发现";
    private boolean card_flag = false;
    private boolean ad_flag = false;
    
    /**
     * 获取当前位置经纬度
     */
    private LocationClient locationClient = null;
    private static final int UPDATE_TIME = 5000;
    private String longitude ="";//经度
    private String latitude ="";//纬度
    private String addString ="";//返回地址
    
    /**
     * 广告轮播控件
     */
    private ImageCycleView mAdView;
    
    /**
     * 用户消息
     */
    private List<UserMsg> userMsgs;
    private ArrayList<UserMsg> totalUserMsgList;
    private PullToRefreshListView mPullRefreshListView;//上拉刷新的控件 
    private int page = 1;
    private UserMsgListAdapter userMsgAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.index_1, null);
        initLocation();
        init(v);
        initCalendar(v);
        initUserMsgView(v);
//      initListView(v);
       
        return v;
    }
    /**
     * 获取用户当前位置的经纬度
     */
    private void initLocation(){
        locationClient = new LocationClient(getActivity());
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);        //是否打开GPS
        option.setCoorType("bd09ll");       //设置返回值的坐标类型。
        option.setPriority(LocationClientOption.NetWorkFirst);  //设置定位优先级
        option.setProdName("Secretary"); //设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
        option.setScanSpan(UPDATE_TIME);    //设置定时定位的时间间隔。单位毫秒
        option.setIsNeedAddress(true);//设置返回城市
        locationClient.setLocOption(option);
        locationClient.start();
        locationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                if(location==null){
                    return ;
                }
                latitude =  location.getLatitude()+"";//纬度
                longitude = location.getLongitude()+"";//经度
                addString = location.getAddrStr();
            }
        });
    }
    @SuppressLint("ResourceAsColor")
	private void init(View v) {
        v.findViewById(R.id.btn_chakan).setOnClickListener(this);
        v.findViewById(R.id.ibtn_person).setOnClickListener(this);
        v.findViewById(R.id.btn_rili).setOnClickListener(this);
        v.findViewById(R.id.btn_saoma).setOnClickListener(this);
        
        finalBitmap = FinalBitmap.create(getActivity());
        defDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ad_loading);
    
        tv_service_type_ids = (TextView)v.findViewById(R.id.tv_service_type_ids);
        layout_mask = v.findViewById(R.id.layout_mask);
        userInfo = DBHelper.getUserInfo(getActivity());
        
        //请求帮助接口
        finalBitmap = FinalBitmap.create(getActivity());
        defDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ad_loading);
        getAppHelp();
    }

    /**
     * 初始化日历
     * 
     * @param v
     */
    private void initCalendar(View v) {
        today_date = LocalDate.now().toString();
        calendarManager = new CalendarManager(LocalDate.now(), CalendarManager.State.WEEK, LocalDate.now().minusYears(1), LocalDate.now()
                .plusYears(1));
        calendarView = (CollapseCalendarView) v.findViewById(R.id.layout_calendar);
        calendarView.init(calendarManager, getActivity(), this);

        calendarView.setListener(new OnDateSelect() {
            @Override
            public void onDateSelected(LocalDate date) {
                today_date = date.toString();
//                getAdListByChannelId("0");
                if(findBeanList!=null && findBeanList.size()>0){
                    ad_flag = true;
                }
                userMsgs.clear();
                totalUserMsgList.clear();
//                getCardListData(today_date, card_from);
                getUserMsgListData(today_date,page);
                //如果广告和卡片有一个有值，则不显示
            }

        });

    }

    public static void showMask() {
        layout_mask.setVisibility(View.VISIBLE);
    }

    public static void GoneMask() {
        layout_mask.setVisibility(View.GONE);
    }

    
    private ImageCycleViewListener mAdCycleViewListener = new ImageCycleViewListener() {
        @Override
        public void onImageClick(FindBean info, int position, View view) {
            String goto_type = info.getGoto_type().trim();
            String goto_url = info.getGoto_url().trim();
            String service_type_ids = info.getService_type_ids().trim();
            if(goto_type.equals("h5")){
                Intent intent = new Intent(getActivity(),WebViewsFindActivity.class);
                intent.putExtra("url",goto_url);
                intent.putExtra("title_name","");
                intent.putExtra("service_type_ids", "");
                startActivity(intent);
            }else if (goto_type.equals("app")) {
               Intent intent = new Intent(getActivity(),Find2DetailActivity.class);
               intent.putExtra("service_type_ids", service_type_ids);
               intent.putExtra("title_name",title_name);
               startActivity(intent);
            }else if (goto_type.equals("h5+list")) {
                Intent intent = new Intent(getActivity(),WebViewsFindActivity.class);
                intent.putExtra("url",goto_url);
                intent.putExtra("title_name",title_name);
                intent.putExtra("service_type_ids", service_type_ids);
                startActivity(intent);
            }
        
        }
        @Override
        public void displayImage(String imageURL, ImageView imageView) {
            ImageLoader.getInstance().displayImage(imageURL, imageView);// 使用ImageLoader对图片进行加装！
        }
     
    };
    @SuppressWarnings("static-access")
    private void initListView(View v) {
        
//      listview = (ListView) v.findViewById(R.id.listview);
        tv_tips = (TextView) v.findViewById(R.id.tv_tips);
        iv_no_card = (ImageView) v.findViewById(R.id.iv_no_card);
        //广告位轮播的另一种方式
 /*       RelativeLayout ll = (RelativeLayout) v.inflate(getActivity(), R.layout.activity_ad_cycle, null);
        listview.addHeaderView(ll);
        mAdView = (ImageCycleView)ll.findViewById(R.id.ad_view);
*/
        
//        getAdListByChannelId("0");//首页广告位显示
        
        /*
         * ArrayList<String> list = new ArrayList<String>(); for (int i = 0; i < 4; i++) { list.add("今日无安排" + i); }
         */
        
        /*
        adapter = new ListAdapter(getActivity(), this);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                int p = position-1;
                if(!cardlist.get(p).getCard_type().equals("99")){
                    Intent intent = new Intent(getActivity(), CardDetailsActivity.class);
                    intent.putExtra("card_id", cardlist.get(p).getCard_id());
                    intent.putExtra("Cards", cardlist.get(p));
                    intent.putExtra("card_extra",cardExtrasList.get(p));
                    startActivity(intent);
                }
            }
        });*/
    }
    
    private void initUserMsgView(View v){
        totalUserMsgList = new ArrayList<UserMsg>();
        userMsgs = new ArrayList<UserMsg>();
        mPullRefreshListView = (PullToRefreshListView)v.findViewById(R.id.pull_refresh_msg_list);
        userMsgAdapter = new UserMsgListAdapter(getActivity());
        mPullRefreshListView.setAdapter(userMsgAdapter);
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
                getUserMsgListData(today_date, page);
                userMsgAdapter.notifyDataSetChanged(); 
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if(userMsgs!=null && userMsgs.size()>=10){
                    page = page+1;
                    getUserMsgListData(today_date, page);
                    userMsgAdapter.notifyDataSetChanged(); 
                }else {
                    Toast.makeText(getActivity(),"请稍后，没有更多加载数据",Toast.LENGTH_SHORT).show();
                    mPullRefreshListView.onRefreshComplete(); 
                }
            }
        });
        mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserMsg userMsg =userMsgs.get(position);
                String category = userMsg.getCategory().trim();
                String goto_url = userMsg.getGoto_url().trim();
                String params = userMsg.getParams().trim();
                String action = userMsg.getAction().trim();
                if(category.equals("h5")){
                    Intent intent = new Intent(getActivity(),WebViewsActivity.class);
                    intent.putExtra("url",goto_url);
                    startActivity(intent);
                }else if (category.equals("app")) {
                    if(action.equals("card")){
                        Intent intent = new Intent(getActivity(), CardDetailsActivity.class);
                        intent.putExtra("card_id", params);
                        startActivity(intent);
                    }else if (action.equals("feed")) {
                        Intent intent = new Intent(getActivity(), DynamicDetailsActivity.class);
                        intent.putExtra("feedId", params);
                        startActivity(intent);
                    }else if (action.equals("checkin")) {
                        
                    }else if (action.equals("friends")) {
                        
                    }else if (action.equals("im")) {
                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                      /*  if(conversation.isGroup()){
                            if(conversation.getType() == EMConversationType.ChatRoom){
                                // it's group chat
                                intent.putExtra(EMConstant.EXTRA_CHAT_TYPE, EMConstant.CHATTYPE_CHATROOM);
                            }else{
                                intent.putExtra(EMConstant.EXTRA_CHAT_TYPE, EMConstant.CHATTYPE_GROUP);
                            }
                            
                        }*/
                        intent.putExtra(EMConstant.EXTRA_USER_ID, params);
                        startActivity(intent);
                        
                    }else if (action.equals("leave")) {
                        
                    }else if (action.equals("leave_pass")) {
                        
                    }else if(action.equals("water")){
                        Intent intent = new Intent(getActivity(),OrderDetailsActivity.class);
                        intent.putExtra("orderId", params);
                        intent.putExtra("orderType", 99);
                        startActivity(intent);
                    }
                  
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

    @Override
    public void onResume() {
        super.onResume();
        LinearLayout ll = (LinearLayout) v.inflate(getActivity(), R.layout.home1_list_item, null);
        if(locationClient != null && !locationClient.isStarted()){
            locationClient.start();
        }
        getUserMsgListData(today_date,page);

//        mAdView.startImageCycle();//广告轮播
//        getTotalByMonth();
//        getCardListData(today_date, card_from);
//        getAdListByChannelId("0");
//        getUserInfo();

    }
    @Override
    public void onStop() {
        super.onStop();
        if (locationClient != null && locationClient.isStarted()) {
            locationClient.stop();
        } 
//        mAdView.pushImageCycle();
    }

    public void isShowDefaultCard(boolean card_flag,boolean ad_flag){
        if(card_flag || ad_flag){
            tv_tips.setVisibility(View.GONE);
            iv_no_card.setVisibility(View.GONE);
        }else {
            tv_tips.setVisibility(View.VISIBLE);
            iv_no_card.setVisibility(View.VISIBLE);
        }
    }
    
    /**
     * 获取卡片数据
     * 
     * @param date
     * @param card_from
     *            0 = 所有卡片 1 = 我发布的 2 = 我参与的,默认为0
     */
    public void getCardListData(String date, int card_from) {

        String user_id = DBHelper.getUser(getActivity()).getId();

        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("service_date", date);
        map.put("user_id", user_id + "");
        map.put("card_from", "" + card_from);
        map.put("lat",latitude);
        map.put("lng", longitude);
        map.put("page","1");
        AjaxParams param = new AjaxParams(map);

//        showDialog();
        new FinalHttp().get(Constants.URL_GET_CARD_LIST, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
//                dismissDialog();
                Toast.makeText(getActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
//                dismissDialog();
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data").trim();
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            if (StringUtils.isNotEmpty(data.trim())) {
                                Gson gson = new Gson();
                                cardlist = new ArrayList<Cards>();
                                cardExtrasList = new ArrayList<CardExtra>();
                                cardlist = gson.fromJson(data, new TypeToken<ArrayList<Cards>>() {
                                }.getType());
                                for (int i = 0; i < cardlist.size(); i++) {
                                    Cards cards2 = cardlist.get(i);
                                    CardExtra cardExtra = new CardExtra();
                                   cardExtra = gson.fromJson(cards2.getCard_extra(),CardExtra.class);
                                   cardExtrasList.add(cardExtra);
                                }
                               /* JsonArray array = new JsonParser().parse(data).getAsJsonArray();
                                for (final JsonElement elem : array) {
                                    cardlist.add(new Gson().fromJson(elem, Cards.class));
                                }*/
                                adapter.setData(cardlist,cardExtrasList);
                                isShowDefaultCard(true,ad_flag);
                            } else {
                                adapter.setData(new ArrayList<Cards>(),new ArrayList<CardExtra>());
                                isShowDefaultCard(false,ad_flag);
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
     * 获得用户消息列表接口
     * @param date
     * @param card_from
     */
    public void getUserMsgListData(String date, int page) {
        
        String user_id = DBHelper.getUser(getActivity()).getId();
        
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.net_not_open), 0).show();
            return;
        }
        
        Map<String, String> map = new HashMap<String, String>();
        map.put("service_date", date);
        map.put("user_id", user_id + "");
        map.put("page",""+page);
        AjaxParams param = new AjaxParams(map);
        
//        showDialog();
        new FinalHttp().get(Constants.URL_GET_USER_MSG_LIST, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
//                dismissDialog();
                Toast.makeText(getActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
//                dismissDialog();
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data").trim();
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            if (StringUtils.isNotEmpty(data.trim())) {
                                Gson gson = new Gson();
                                userMsgs = gson.fromJson(data, new TypeToken<ArrayList<UserMsg>>() {
                                }.getType());
                                showData(userMsgs);
//                                userMsgAdapter.setData(userMsgs);
                            } else {
                                showData(new ArrayList<UserMsg>());
//                                userMsgAdapter.setData(new ArrayList<UserMsg>());
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
     * 按月份查看卡片日期分布接口（更新日历圆点标签）
     * 
     * @param year
     *            年份，格式为 YYYY
     * @param month
     *            月份，格式为 MM
     */
    public void getTotalByMonth() {

        String date = calendarManager.getHeaderText();
        if (!date.contains("年")) {
            return;
        }

        date = date.replace("年", "").replace("月", "");
        final String year = date.substring(0, 4);
        final String month = date.substring(date.length() - 3, date.length());

        String user_id = DBHelper.getUser(getActivity()).getId();

        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id + "");
        map.put("year", year);
        map.put("month", month);
        AjaxParams param = new AjaxParams(map);

//        showDialog();
        new FinalHttp().get(Constants.URL_GET_TOTAL_BY_MONTH, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
//                dismissDialog();
                Toast.makeText(getActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
//                dismissDialog();
                LogOut.i("========", "onSuccess：" + t);
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确

                            // 先清除这个月的旧数据
                            DBHelper.clearCalendarMark(getActivity(), year, month);

                            if (StringUtils.isNotEmpty(data)) {
                                Gson gson = new Gson();
                                calendarMarks = gson.fromJson(data, new TypeToken<ArrayList<CalendarMark>>() {
                                }.getType());

                                DBHelper db = DBHelper.getInstance(getActivity());
                                for (int i = 0; i < calendarMarks.size(); i++) {
                                    db.add(calendarMarks.get(i), calendarMarks.get(i).getService_date());
                                }

                            }

                            // 刷新日历UI
                            calendarView.updateUI();

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
     * 根据渠道获取广告位
     * @param channel_id
     */
    public void getAdListByChannelId(String channel_id) {
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("channel_id", channel_id);
        AjaxParams param = new AjaxParams(map);
//        showDialog();
        new FinalHttp().get(Constants.URL_GET_ADS_LIST, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
//                dismissDialog();
                Toast.makeText(getActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
//                dismissDialog();
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            if (StringUtils.isNotEmpty(data)) {
                                Gson gson = new Gson();
                                findBeanList = gson.fromJson(data, new TypeToken<ArrayList<FindBean>>() {
                                }.getType());
//                                mAdView.setImageResources(findBeanList, mAdCycleViewListener);
                                isShowDefaultCard(card_flag,true);
                            } else {
                                isShowDefaultCard(card_flag,false);
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

    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_chakan: // 查看
            if (card_from == 0) {
                card_from = 1;
            } else {
                card_from = 0;
            }
//            getCardListData(today_date, card_from);
            getUserMsgListData(today_date,page);
            break;
        case R.id.ibtn_person: // 侧边栏
            MainActivity.slideMenu();
            break;
        case R.id.btn_rili: // 日历展开/收起
            LocalDate selectedDay = calendarManager.getSelectedDay();
            if (calendarManager.getState() == CalendarManager.State.MONTH) {
                calendarManager = new CalendarManager(selectedDay, CalendarManager.State.WEEK, LocalDate.now().minusYears(1), LocalDate.now()
                        .plusYears(1));
            } else {
                calendarManager = new CalendarManager(selectedDay, CalendarManager.State.MONTH, LocalDate.now().minusYears(1), LocalDate.now()
                        .plusYears(1));
            }
            calendarView.init(calendarManager, getActivity(), this);
            break;
        case R.id.btn_saoma:
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
                if (result.contains(Constants.RQ_TAG_FRIEND)
                        || result.contains(Constants.RQ_TAG_OTHER)) {//判断是否为云行政二维码
                    if(result.contains(Constants.RQ_TAG_OTHER)){//本公司其他扫描
                        String url_temp =result.substring(result.lastIndexOf("/")+1,result.length()); 
                        String url  = Constants.ROOT_URL+"o.json?"+url_temp+"&uid="+userInfo.getId();
                        Intent intent = new Intent(getActivity(),WebViewsActivity.class);
                        intent.putExtra("url",url);
                        startActivity(intent);
                    }else if (result.contains(Constants.RQ_TAG_FRIEND)) {
                        String str = result.substring(result.indexOf("&")+1,result.length());
                        String friend_id = str.substring(str.indexOf("=")+1,str.indexOf("&"));
                        addFriend(friend_id);
                    }
                } else {
                    Toast.makeText(getActivity(), "非本APP内容，可能存在风险", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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

//        showDialog();
        new FinalHttp().get(Constants.URL_GET_ADD_FRIEND, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
//                dismissDialog();
                Toast.makeText(getActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
//                dismissDialog();
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
    public void onCardUpdate() {
//        getCardListData(today_date, card_from);
        //如果广告和卡片有一个有值，则不显示
    }
    /**
     * 获取用户详情
     */
    private void getUserInfo() {
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", userInfo.getId());
        AjaxParams param = new AjaxParams(map);
//        showDialog();
        new FinalHttp().get(Constants.URL_GET_USER_INFO, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
//                dismissDialog();
                Toast.makeText(getActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
//                dismissDialog();
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
                    mPullRefreshListView.onRefreshComplete();
                    errorMsg = getString(R.string.servers_error);

                }
                // 操作失败，显示错误信息|
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    mPullRefreshListView.onRefreshComplete();
                    UIUtils.showToast(getActivity(), errorMsg);
                }
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //关闭定位
        if (locationClient != null && locationClient.isStarted()) {
            locationClient.stop();
            locationClient = null;
        } 
        page = 1;
        userMsgs =null;
        totalUserMsgList = null;
//        mAdView.pushImageCycle();

    }
    
    /**
     * 处理数据加载的方法
     * @param list
     */
    private void showData(List<UserMsg> userMsgs){
        if(userMsgs!=null && userMsgs.size()>0){
            if(page==1){
                totalUserMsgList.clear();
            }
            for (UserMsg userMsg : userMsgs) {
                totalUserMsgList.add(userMsg);
            }
            //给适配器赋值
//            userMsgAdapter.setData(totalUserMsgList);
        }
        userMsgAdapter.setData(totalUserMsgList);
        mPullRefreshListView.onRefreshComplete();
    }
    
    

    private PopupWindow popupWindow;
    private TextView mDone;
    private ImageView tip_iv_icon;
    private SelectableRoundedImageView selectableRoundedImageView;
    private TextView tip_tv_title;
    private TextView tip_tv_content;
    private TextView tip_tv_more;
    private AppHelpData appHelpData;
    /**
     * 弹出窗口
     */
    private void popWindow(final AppHelpData appHelpData) {
        View view = (LinearLayout)getActivity().getLayoutInflater()
                .inflate(R.layout.layout_tip_activity, null);
        if (null == popupWindow || !popupWindow.isShowing()) {
            popupWindow = new PopupWindow(view,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
          /*  popupWindow = new PopupWindow(view);
            popupWindow.setWidth(450);
            popupWindow.setHeight(650);*/
            popupWindow.setFocusable(false);
            popupWindow.setTouchable(true);
        }
        mDone = (TextView)view.findViewById(R.id.tip_tv_done);
        tip_tv_title = (TextView)view.findViewById(R.id.tip_tv_title);
        tip_tv_content = (TextView)view.findViewById(R.id.tip_tv_content);
        tip_tv_more = (TextView)view.findViewById(R.id.tip_tv_more);
//        selectableRoundedImageView = (SelectableRoundedImageView)view.findViewById(R.id.tip_iv_icon);
        tip_iv_icon = (ImageView)view.findViewById(R.id.tip_iv_icon);
        tip_tv_title.setText(appHelpData.getTitle());
        tip_tv_content.setText(appHelpData.getContent());
//        finalBitmap.display(selectableRoundedImageView, appHelpData.getImg_url(), defDrawable.getBitmap(), defDrawable.getBitmap());
        finalBitmap.display(tip_iv_icon, appHelpData.getImg_url(), defDrawable.getBitmap(), defDrawable.getBitmap());
        popupWindow.setFocusable(true);  
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.PopupAnimation); //设置 popupWindow动画样式
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
        backgroundAlpha(0.5f);
        mDone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != popupWindow && popupWindow.isShowing()) {
                    backgroundAlpha(1f);
                    popupWindow.dismiss();
                }
            }
        });
       tip_tv_more.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
            String goto_url = appHelpData.getGoto_url();
            String action = appHelpData.getAction().trim();
            Intent intent = new Intent(getActivity(),WebViewsActivity.class);
            intent.putExtra("url",goto_url);
            startActivity(intent);
            backgroundAlpha(1f);
            popupWindow.dismiss();
        }            
    });
    }
    /**
    * 设置添加屏幕的背景透明度
    * @param bgAlpha
    */
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
            lp.alpha = bgAlpha; //0.0-1.0
            getActivity().getWindow().setAttributes(lp);
    }
    /*
     * 帮助接口
     */
    
    private void getAppHelp() {
        String user_id = DBHelper.getUser(getActivity()).getId();
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.net_not_open), 0).show();
            return;
        }
        User user = DBHelper.getUser(getActivity());
        Map<String, String> map = new HashMap<String, String>();
        map.put("action","index");
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
                                popWindow(appHelpData);
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
