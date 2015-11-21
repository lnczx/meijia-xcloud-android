package com.meijialife.simi.fra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.joda.time.LocalDate;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.BaseFragment;
import com.meijialife.simi.Constants;
import com.meijialife.simi.MainActivity;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.CardDetailsActivity;
import com.meijialife.simi.adapter.ListAdapter;
import com.meijialife.simi.adapter.ListAdapter.onCardUpdateListener;
import com.meijialife.simi.bean.CalendarMark;
import com.meijialife.simi.bean.Cards;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.ui.CollapseCalendarView;
import com.meijialife.simi.ui.CollapseCalendarView.OnDateSelect;
import com.meijialife.simi.ui.calendar.CalendarManager;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.SpFileUtil;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

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

    private TextView tv_tips;// 没有数据时的提示
    private ImageView iv_no_card;//没有数据时提示 图片

    private int card_from; // 0 = 所有卡片 1 = 我发布的 2 = 我参与的,默认为0

    private static View layout_mask, layout_guide;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.index_1, null);
        // if(this.getResources().getConfiguration().orientation ==Configuration.ORIENTATION_LANDSCAPE){
        // getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // }
        init(v);
        initCalendar(v);
        initListView(v);

        return v;
    }

    private void init(View v) {
        v.findViewById(R.id.btn_chakan).setOnClickListener(this);
        v.findViewById(R.id.ibtn_person).setOnClickListener(this);
        v.findViewById(R.id.btn_rili).setOnClickListener(this);
        
        layout_mask = v.findViewById(R.id.layout_mask);
    }

    /**
     * 初始化日历
     * 
     * @param v
     */
    private void initCalendar(View v) {
        today_date = LocalDate.now().toString();

        calendarManager = new CalendarManager(LocalDate.now(), CalendarManager.State.WEEK, LocalDate.now().minusYears(1), LocalDate.now().plusYears(
                1));

        calendarView = (CollapseCalendarView) v.findViewById(R.id.layout_calendar);
        calendarView.init(calendarManager, getActivity(), this);

        calendarView.setListener(new OnDateSelect() {
            @Override
            public void onDateSelected(LocalDate date) {
                today_date = date.toString();
                getCardListData(today_date, card_from);
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
        iv_no_card =(ImageView) v.findViewById(R.id.iv_no_card);
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
        // TODO Auto-generated method stub
        super.onResume();

        getTotalByMonth();
        getCardListData(today_date, card_from);

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
                                tv_tips.setVisibility(View.GONE);
                                iv_no_card.setVisibility(View.GONE);
                                // UIUtils.showToast(getActivity(), "有数据");
                            } else {
                                adapter.setData(new ArrayList<Cards>());
                                tv_tips.setVisibility(View.VISIBLE);
                                iv_no_card.setVisibility(View.VISIBLE);
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
        if(!date.contains("年")){
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
                            
                          //先清除这个月的旧数据
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
                            
                          //刷新日历UI
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
            if(calendarManager.getState() == CalendarManager.State.MONTH){
                calendarManager = new CalendarManager(selectedDay, CalendarManager.State.WEEK, LocalDate.now().minusYears(1), LocalDate.now().plusYears(1));
            }else{
                calendarManager = new CalendarManager(selectedDay, CalendarManager.State.MONTH, LocalDate.now().minusYears(1), LocalDate.now().plusYears(1));
            }
            calendarView.init(calendarManager, getActivity(), this);
            
            break;

        default:
            break;
        }
    }

    @Override
    public void onCardUpdate() {
        // TODO Auto-generated method stub
        getCardListData(today_date, card_from);
    }

}
