package com.meijialife.simi.fra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.joda.time.LocalDate;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.BaseFragment;
import com.meijialife.simi.Constants;
import com.meijialife.simi.MainActivity;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.CardDetailsActivity;
import com.meijialife.simi.activity.Find2DetailActivity;
import com.meijialife.simi.activity.FriendPageActivity;
import com.meijialife.simi.activity.WebViewsActivity;
import com.meijialife.simi.activity.WebViewsFindActivity;
import com.meijialife.simi.adapter.ImgAdapter;
import com.meijialife.simi.adapter.ListAdapter;
import com.meijialife.simi.adapter.ListAdapter.onCardUpdateListener;
import com.meijialife.simi.bean.CalendarMark;
import com.meijialife.simi.bean.Cards;
import com.meijialife.simi.bean.FindBean;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.ui.CollapseCalendarView;
import com.meijialife.simi.ui.CollapseCalendarView.OnDateSelect;
import com.meijialife.simi.ui.MyGallery;
import com.meijialife.simi.ui.calendar.CalendarManager;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;
import com.meijialife.simi.zxing.code.MipcaActivityCapture;

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
    private Cards[] cards;

    private TextView tv_tips;// 没有数据时的提示
    private ImageView iv_no_card;// 没有数据时提示 图片

    private int card_from; // 0 = 所有卡片 1 = 我发布的 2 = 我参与的,默认为0

    private static View layout_mask, layout_guide;
    private final static int SCANNIN_GREQUEST_CODES = 5;
    private View v;
    private UserInfo userInfo;
    private ArrayList<FindBean> findBeanList;
    
    private TextView tv_ad_des;//广告说明
    private ImageView iv_ad_icon;//广告图片
    private TextView  tv_ad_goto_type;//跳转方式
    private TextView  tv_ad_goto_url;//跳转url
    private TextView tv_ad_share;//分享
    TextView tv_service_type_ids;//服务大类集合
    
    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    private LinearLayout ll_ad;//显示广告位控件
    
    private MyGallery gallery = null;
    private LinearLayout ll_focus_indicator_container = null;
    private ArrayList<Integer> imgList;
    private ArrayList<ImageView> portImg;
    private int preSelImgIndex = 0;
    private ImgAdapter imgAdapter;
    private String title_name = "发现";
    
    private Boolean card_flag = false;
    private Boolean ad_flag = false;
    
    //获取当前位置经纬度
    private LocationClient locationClient = null;
    private static final int UPDATE_TIME = 5000;
    private static int LOCATION_COUTNS = 0;
    private String longitude ="";//经度
    private String latitude ="";//纬度
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.index_1, null);
        // if(this.getResources().getConfiguration().orientation ==Configuration.ORIENTATION_LANDSCAPE){
        // getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // }
        
//        initLocation();
        init(v);
        initCalendar(v);
        initListView(v);
        //如果广告和卡片有一个有值，则不显示
        if(card_flag || ad_flag){
            tv_tips.setVisibility(View.GONE);
            iv_no_card.setVisibility(View.GONE);
        }else {
            tv_tips.setVisibility(View.VISIBLE);
            iv_no_card.setVisibility(View.VISIBLE);
        }
        return v;
    }

    private void initLocation(){
        locationClient = new LocationClient(getActivity());
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);        //是否打开GPS
        option.setCoorType("bd09ll");       //设置返回值的坐标类型。
        option.setPriority(LocationClientOption.NetWorkFirst);  //设置定位优先级
        option.setProdName("LocationDemo"); //设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
        option.setScanSpan(UPDATE_TIME);    //设置定时定位的时间间隔。单位毫秒
        locationClient.setLocOption(option);
        
        locationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                if(location==null){
                    return ;
                }
                latitude =  location.getLatitude()+"";//纬度
                longitude = location.getLongitude()+"";//经度
            }
        });
        Toast.makeText(getActivity(),latitude+"--"+longitude,Toast.LENGTH_LONG).show();
        
        
    }
    
    private void init(View v) {
        v.findViewById(R.id.btn_chakan).setOnClickListener(this);
        v.findViewById(R.id.ibtn_person).setOnClickListener(this);
        v.findViewById(R.id.btn_rili).setOnClickListener(this);
        v.findViewById(R.id.btn_saoma).setOnClickListener(this);
        
        imgAdapter = new ImgAdapter(getActivity());
        
        finalBitmap = FinalBitmap.create(getActivity());
        defDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ad_loading);
    
       
        ll_focus_indicator_container = (LinearLayout) v.findViewById(R.id.ll_focus_indicator_container);
        gallery = (MyGallery) v.findViewById(R.id.gallery);
        gallery.setAdapter(imgAdapter);
        gallery.setFocusable(true);
        gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int selIndex, long arg3) {
                selIndex = selIndex % findBeanList.size();
                // 修改上一次选中项的背景
                portImg.get(preSelImgIndex).setImageResource(R.drawable.ic_focus);
                // 修改当前选中项的背景
                portImg.get(selIndex).setImageResource(R.drawable.ic_focus_select);
                preSelImgIndex = selIndex;
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        gallery.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv_gotoType =(TextView)view.findViewById(R.id.tv_goto_type);
                TextView tv_gotoUrl =(TextView)view.findViewById(R.id.tv_goto_url);
                TextView tv_service_type_ids =(TextView)view.findViewById(R.id.tv_service_type_ids);
                
                String goto_type = tv_gotoType.getText().toString().trim();
                String goto_url = tv_gotoUrl.getText().toString().trim();
                String service_type_ids = tv_service_type_ids.getText().toString().trim();
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
        });
        
        getAdListByChannelId("0");//首页广告位显示

        tv_ad_des = (TextView) v.findViewById(R.id.tv_ad_des);
        tv_ad_goto_type = (TextView) v.findViewById(R.id.tv_goto_type);
        tv_ad_goto_url = (TextView) v.findViewById(R.id.tv_goto_url);
        iv_ad_icon = (ImageView) v.findViewById(R.id.iv_ad_icon);
        tv_ad_share = (TextView)v.findViewById(R.id.tv_share);
        tv_service_type_ids = (TextView)v.findViewById(R.id.tv_service_type_ids);
//        ll_ad = (LinearLayout)v.findViewById(R.id.ll_ad);

        layout_mask = v.findViewById(R.id.layout_mask);
        //沉浸式状态栏(像ios那样的状态栏与应用统一颜色样式)android.4.4支持 
      /*  getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); // 透明状态栏
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);// 透明导航栏
*/
        
        userInfo = DBHelper.getUserInfo(getActivity());
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
                getCardListData(today_date, card_from);
                //如果广告和卡片有一个有值，则不显示
                if(card_flag || ad_flag){
                    tv_tips.setVisibility(View.GONE);
                    iv_no_card.setVisibility(View.GONE);
                }else {
                    tv_tips.setVisibility(View.VISIBLE);
                    iv_no_card.setVisibility(View.VISIBLE);
                }
            }

        });

    }

    public static void showMask() {
        layout_mask.setVisibility(View.VISIBLE);
    }

    public static void GoneMask() {
        layout_mask.setVisibility(View.GONE);
    }

    private void initListView(View v) {
        listview = (ListView) v.findViewById(R.id.listview);
        tv_tips = (TextView) v.findViewById(R.id.tv_tips);
        iv_no_card = (ImageView) v.findViewById(R.id.iv_no_card);
        
        /*
         * ArrayList<String> list = new ArrayList<String>(); for (int i = 0; i < 4; i++) { list.add("今日无安排" + i); }
         */
        adapter = new ListAdapter(getActivity(), this);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = new Intent(getActivity(), CardDetailsActivity.class);
                intent.putExtra("Cards", cardlist.get(arg2));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        getTotalByMonth();
        getCardListData(today_date, card_from);
        getAdListByChannelId("0");
        //如果广告和卡片有一个有值，则不显示
        if(card_flag || ad_flag){
            tv_tips.setVisibility(View.GONE);
            iv_no_card.setVisibility(View.GONE);
        }else {
            tv_tips.setVisibility(View.VISIBLE);
            iv_no_card.setVisibility(View.VISIBLE);
        }
        
        getUserInfo();

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

        showDialog();
        new FinalHttp().get(Constants.URL_GET_CARD_LIST, param, new AjaxCallBack<Object>() {
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
                LogOut.i("========", "onSuccess：" + t);
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            if (StringUtils.isNotEmpty(data)) {
                                Gson gson = new Gson();
                                cardlist = gson.fromJson(data, new TypeToken<ArrayList<Cards>>() {
                                }.getType());
                                
                                adapter.setData(cardlist);
                                card_flag = true;
                              /*  tv_tips.setVisibility(View.GONE);
                                iv_no_card.setVisibility(View.GONE);*/
                                // UIUtils.showToast(getActivity(), "有数据");
                            } else {
                                adapter.setData(new ArrayList<Cards>());
                                card_flag = false;
                               /* tv_tips.setVisibility(View.VISIBLE);
                                iv_no_card.setVisibility(View.VISIBLE);*/
                                // UIUtils.showToast(getActivity(), "没有数据");
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

        showDialog();
        new FinalHttp().get(Constants.URL_GET_TOTAL_BY_MONTH, param, new AjaxCallBack<Object>() {
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
        showDialog();
        new FinalHttp().get(Constants.URL_GET_ADS_LIST, param, new AjaxCallBack<Object>() {
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
                                findBeanList = gson.fromJson(data, new TypeToken<ArrayList<FindBean>>() {
                                }.getType());
                                imgAdapter.setData(findBeanList);
                                ad_flag = true;
//                                showAdView(findBeanList);
//                                adapter.setData(findBeanList);
//                                tv_tips.setVisibility(View.GONE);
//                                iv_no_card.setVisibility(View.GONE);
                            } else {
//                                showAdView(findBeanList);
//                                adapter.setData(new ArrayList<FindBean>());
                                imgAdapter.setData(new ArrayList<FindBean>());
                                ad_flag = false;
//                                tv_tips.setVisibility(View.VISIBLE);
//                                iv_no_card.setVisibility(View.VISIBLE);
                            }
                            InitFocusIndicatorContainer();
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

/*    private void showAdView(ArrayList<FindBean> findBeanList){
        if(findBeanList!=null && findBeanList.size()>0){
            ll_ad.setVisibility(View.VISIBLE);
            FindBean findBean =findBeanList.get(0);
            tv_ad_des.setText(findBean.getTitle());
            String url = findBean.getImg_url();
            finalBitmap.display(iv_ad_icon, url, defDrawable.getBitmap(), defDrawable.getBitmap());
            tv_tips.setVisibility(View.GONE);
            iv_no_card.setVisibility(View.GONE);
        }else {
            ll_ad.setVisibility(View.GONE);
            tv_tips.setVisibility(View.GONE);
            iv_no_card.setVisibility(View.GONE);
        }
      
        
    }*/
    
    
    // /**
    // * 日历当天是否有卡片
    // */
    // public static boolean isMark(String date){
    // if(calendarMarks == null){
    // return false;
    // }
    // for(int i = 0; i < calendarMarks.size(); i++){
    // if(calendarMarks.get(i).getService_date().equals(date)){
    // return true;
    // }
    // }
    // return false;
    // }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_chakan: // 查看
            if (card_from == 0) {
                card_from = 1;
            } else {
                card_from = 0;
            }
            getCardListData(today_date, card_from);
         
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
            intents.setClass(getActivity(), MipcaActivityCapture.class);
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
    @Override
    public void onCardUpdate() {
        getCardListData(today_date, card_from);
        //如果广告和卡片有一个有值，则不显示
        if(card_flag || ad_flag){
            tv_tips.setVisibility(View.GONE);
            iv_no_card.setVisibility(View.GONE);
        }else {
            tv_tips.setVisibility(View.VISIBLE);
            iv_no_card.setVisibility(View.VISIBLE);
        }
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
                    UIUtils.showToast(getActivity(), errorMsg);
                }
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        gallery.destroy();
       /* if (locationClient != null && locationClient.isStarted()) {
            locationClient.stop();
            locationClient = null;
        }*/
    }
    
    private void InitFocusIndicatorContainer() {
        portImg = new ArrayList<ImageView>();
        for (int i = 0; i < findBeanList.size(); i++) {
            ImageView localImageView = new ImageView(getActivity());
            localImageView.setId(i);
            ImageView.ScaleType localScaleType = ImageView.ScaleType.FIT_XY;
            localImageView.setScaleType(localScaleType);
            LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(
                    24, 24);
            localImageView.setLayoutParams(localLayoutParams);
            localImageView.setPadding(5, 5, 5, 5);
            localImageView.setImageResource(R.drawable.ic_focus);
            portImg.add(localImageView);
            this.ll_focus_indicator_container.addView(localImageView);
        }
    }

}
