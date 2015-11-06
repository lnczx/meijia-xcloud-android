package com.meijialife.simi.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.alerm.AlermUtils;
import com.meijialife.simi.bean.CardAttend;
import com.meijialife.simi.bean.Cards;
import com.meijialife.simi.bean.ContactBean;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.ui.ToggleButton;
import com.meijialife.simi.ui.ToggleButton.OnToggleChanged;
import com.meijialife.simi.ui.wheelview.ArrayWheelAdapter;
import com.meijialife.simi.ui.wheelview.NumericWheelAdapter;
import com.meijialife.simi.ui.wheelview.WheelView;
import com.meijialife.simi.ui.wheelview.WheelView.ItemScroListener;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * 行程规划
 * 
 * @author windows7
 * 
 */
public class MainPlusTravelActivity extends BaseActivity implements OnClickListener, ItemScroListener  {

    public static final int START_CITY_FLAG = 1001;
    public static final int END_CITY_FLAG = 1002;
    public static final int NEED_SEC_DO = 1;
    public static final int NO_SEC_DO = 0;
    private int SET_SEC_DO = NO_SEC_DO;
    private TextView tv_start_location;
    private TextView tv_mudi_location;
    private ImageView iv_switch_city;
    public static final int GET_USER = 1003;
    private View view_mask;
    private RelativeLayout view_title_bar;

    private WheelView year;
    private WheelView month;
    private WheelView day;
    private WheelView hour;
    private WheelView minute;
    private int mYear = 2015;
    private int mMonth = 0;
    private int mDay = 1;

    private int mHour = 0;
    private int mMinute = 0;

    private boolean isUpdate = false;

    private int remindAlerm = 1;// 提醒设置 0 = 不提醒 1 = 按时提醒 2 = 5分钟 3 = 15分钟 4 = 提前30分钟 5 = 提前一个小时 6 = 提前2小时 7 = 提前6小时 8 = 提前一天 9 = 提前两天
    private TextView tv_select_who_name;
    private String for_userid = "";
    private UserInfo userInfo;
    private RelativeLayout layout_select_who;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_main_plus_trevel);
        super.onCreate(savedInstanceState);
        userInfo = DBHelper.getUserInfo(this);

        card = (Cards) getIntent().getSerializableExtra("cards");
        initView(card);

    }

    private void initView(Cards card) {
        requestBackBtn();
        requestRightBtn();
        setTitleName("差旅规划");

        view_title_bar = (RelativeLayout) findViewById(R.id.view_title_bar);

        bt_create_travel = (Button) findViewById(R.id.bt_create_travel);
        bt_create_travel.setOnClickListener(this);

        findViewById(R.id.iv_switch_city).setOnClickListener(this);
        findViewById(R.id.layout_start_city).setOnClickListener(this);
        findViewById(R.id.layout_end_city).setOnClickListener(this);
        findViewById(R.id.layout_set_date).setOnClickListener(this);
        findViewById(R.id.layout_set_time).setOnClickListener(this);
        findViewById(R.id.layout_beizhu_messsage).setOnClickListener(this);
        findViewById(R.id.layout_remind).setOnClickListener(this);
        layout_select_who = (RelativeLayout) findViewById(R.id.layout_select_who);
        layout_select_who.setOnClickListener(this);

        tv_start_location = (TextView) findViewById(R.id.tv_start_location);
        tv_mudi_location = (TextView) findViewById(R.id.tv_mudi_location);
        tv_date = (TextView) findViewById(R.id.tv_date);// 设置时间
        tv_chufa_time = (TextView) findViewById(R.id.tv_chufa_time);// 设置时间
        tv_xiaoxi_content = (TextView) findViewById(R.id.tv_xiaoxi_content);
        tv_beizu_content = (TextView) findViewById(R.id.tv_beizu_content);
        tv_senser_tip = (TextView) findViewById(R.id.tv_senser_tip);

        tv_select_who_name = (TextView) findViewById(R.id.tv_select_who_name);
        view_mask = (View) findViewById(R.id.view_mask);

        slipBtn_dingjipiao = (ToggleButton) findViewById(R.id.slipBtn_dingjipiao);
        slipBtn_mishuchuli = (ToggleButton) findViewById(R.id.slipBtn_mishuchuli);

        UserInfo userInfo = DBHelper.getUserInfo(this);
        is_senior = userInfo.getIs_senior();
        String user_type = userInfo.getUser_type();
      isUsersenior = StringUtils.isEquals(user_type, "1");
        if (isUsersenior) {
            layout_select_who.setVisibility(View.VISIBLE);
        } else {
            layout_select_who.setVisibility(View.GONE);
        }

        slipBtn_mishuchuli.setOnToggleChanged(new OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on) {
                    if (StringUtils.isEquals(is_senior, "1")) {// 有秘书

                        String date = tv_date.getText().toString();
                        String time = tv_chufa_time.getText().toString();
                        if (!date.contains("-") || !time.contains(":")) {
                            slipBtn_mishuchuli.setToggleOff();
                            SET_SEC_DO = NO_SEC_DO;
                            UIUtils.showToast(MainPlusTravelActivity.this, "请选择时间");
                            return;
                        }

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Date chooseDate = null;
                        try {
                            chooseDate = format.parse(date + " " + time);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (chooseDate == null) {
                            slipBtn_mishuchuli.setToggleOff();
                            SET_SEC_DO = NO_SEC_DO;
                            UIUtils.showToast(MainPlusTravelActivity.this, "请选择时间");
                            return;
                        }

                        // 提前2天可设置
                        if (DateUtils.is2Days(chooseDate)) {
                            slipBtn_mishuchuli.setToggleOn();
                            SET_SEC_DO = NEED_SEC_DO;
                            tv_senser_tip.setVisibility(View.GONE);
                        }
                        // 选择时间不对的情况走else
                        else {
                            slipBtn_mishuchuli.setToggleOff();
                            SET_SEC_DO = NO_SEC_DO;
                            tv_senser_tip.setVisibility(View.VISIBLE);
                            UIUtils.showActionDialog(MainPlusTravelActivity.this, "提醒", "请提前2天设置", "确定", null, null, null);
                        }

                    } else {
                        slipBtn_mishuchuli.setToggleOff();
                        SET_SEC_DO = NO_SEC_DO;
                        tv_senser_tip.setVisibility(View.GONE);
                        startActivity(new Intent(MainPlusTravelActivity.this, FindSecretaryActivity.class));
                        Toast.makeText(MainPlusTravelActivity.this, "你没有购买秘书卡", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    tv_senser_tip.setVisibility(View.GONE);
                }
            }
        });

        slipBtn_dingjipiao.setOnToggleChanged(new OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on) {
                    if (StringUtils.isEquals(is_senior, "1")) {// 有秘书
                        slipBtn_dingjipiao.setToggleOn();
                        SET_SEC_DO = NEED_SEC_DO;
                    } else {
                        SET_SEC_DO = NO_SEC_DO;
                        slipBtn_dingjipiao.setToggleOff();
                        startActivity(new Intent(MainPlusTravelActivity.this, FindSecretaryActivity.class));
                        Toast.makeText(MainPlusTravelActivity.this, "你没有购买秘书卡", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // 卡片更新
        if (null != card) {
            isUpdate = true;

            Constants.CARD_ADD_TREAVEL_CONTENT = card.getService_content();

            tv_start_location.setText(card.getTicket_from_city_name());
            tv_mudi_location.setText(card.getTicket_to_city_name());

            start_city_id = card.getTicket_from_city_id();
            end_city_id = card.getTicket_to_city_id();

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
            long Service_time = Long.valueOf(card.getService_time()) * 1000;

            String uYear = format.format(Service_time);
            String uTime = format2.format(Service_time);

            tv_date.setText(uYear);
            tv_chufa_time.setText(uTime);

            bt_create_travel.setText("更新");

            // 处理提醒任务
            int set_remind = Integer.valueOf(card.getSet_remind());
            remindAlerm = set_remind;
            String[] reminItems = new String[10];
            reminItems[0] = "不提醒";
            reminItems[1] = "按时提醒";
            reminItems[2] = "提前5分钟";
            reminItems[3] = "提前15分钟";
            reminItems[4] = "提前30分钟";
            reminItems[5] = "提前1小时";
            reminItems[6] = "提前2小时";
            reminItems[7] = "提前6小时";
            reminItems[8] = "提前1天";
            reminItems[9] = "提前2天";
            tv_xiaoxi_content.setText(reminItems[remindAlerm]);

            // 处理秘书处理
            int sec_do = Integer.valueOf(card.getSet_sec_do());
            LogOut.debug("是否需要秘书处理:" + sec_do);

            if (sec_do == 1) {// 有秘书
                slipBtn_mishuchuli.setToggleOn();
                slipBtn_dingjipiao.setToggleOn();
                SET_SEC_DO = NEED_SEC_DO;
            } else if (sec_do == 0) {// 无秘书
                slipBtn_mishuchuli.setToggleOff();
                slipBtn_dingjipiao.setToggleOff();
                SET_SEC_DO = NO_SEC_DO;
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.iv_switch_city:// 切换城市
            String startcity = tv_start_location.getText().toString().trim();
            String endcity = tv_mudi_location.getText().toString().trim();
            String start_temp = null;
            String end_temp = null;
            if (StringUtils.isNotEmpty(startcity) && StringUtils.isNotEmpty(endcity)) {
                tv_start_location.setText(endcity);
                tv_mudi_location.setText(startcity);
                start_city_id = end_city_id;

                start_temp = start_city_id;
                end_temp = end_city_id;
                end_city_id = start_temp;
                start_city_id = end_temp;
            }
            break;
        case R.id.layout_start_city:// 出发城市
            startActivityForResult(new Intent(MainPlusTravelActivity.this, CityListActivity.class), START_CITY_FLAG);

            break;
        case R.id.layout_end_city:// 目的城市
            startActivityForResult(new Intent(MainPlusTravelActivity.this, CityListActivity.class), END_CITY_FLAG);

            break;
        case R.id.layout_set_date:// 设置日期
            showDateWindow();
            break;
        case R.id.layout_set_time:// 设置time
            showTimeWindow();
            break;
        case R.id.layout_remind:// 设置提醒
            showRemindWindow();
            break;
        case R.id.layout_beizhu_messsage: // 备注消息

            Intent intent2 = new Intent(MainPlusTravelActivity.this, MainPlusContentActivity.class);
            intent2.putExtra(Constants.MAIN_PLUS_FLAG, Constants.TRAVEL);
            startActivity(intent2);
            break;
        case R.id.bt_create_travel:
            // MainPlusTravelActivity.this.finish();
            // Toast.makeText(MainPlusTravelActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
            createTrevel();

            break;
        case R.id.layout_select_who:
            Intent mintent = new Intent(MainPlusTravelActivity.this, CreateForWhoActivity.class);
            startActivityForResult(mintent, GET_USER);
            break;

        default:
            break;
        }

    }

    @Override
    protected void onResume() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_beizu_content.setText(Constants.CARD_ADD_TREAVEL_CONTENT);
            }
        });
        super.onResume();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
            case START_CITY_FLAG:
                Bundle extras = data.getExtras();
                String start_cityname = extras.getString("city_name");
                start_city_id = extras.getString("city_id");

                tv_start_location.setText(start_cityname);
                break;
            case END_CITY_FLAG:
                Bundle bundle = data.getExtras();
                String end_cityname = bundle.getString("city_name");
                end_city_id = bundle.getString("city_id");
                tv_mudi_location.setText(end_cityname);
                break;
            case GET_USER:
                for_userid = data.getExtras().getString("for_userid");
                String for_name = data.getExtras().getString("for_name");

                tv_select_who_name.setText(for_name);
                break;

            default:
                break;
            }

        }

    }

    /**
     * 时间选择器
     */
    public void showTimeWindow() {
        view_mask.setVisibility(View.VISIBLE);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.item_popup_timepick, null, false);

        // v.findViewById(id);
        InitTimePick(v);

        mTimePopup = new PopupWindow(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mTimePopup.setOutsideTouchable(true);
        mTimePopup.setBackgroundDrawable(new BitmapDrawable());
        mTimePopup.setAnimationStyle(R.style.PostBarShareAnim);

        mTimePopup.showAtLocation(view_title_bar, Gravity.BOTTOM, 0, 0);

        mTimePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                view_mask.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 日期选择器
     */
    public void showDateWindow() {
        view_mask.setVisibility(View.VISIBLE);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.item_popup_datapick, null, false);

        // v.findViewById(id);
        InitDataPick(v);

        mTimePopup = new PopupWindow(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mTimePopup.setOutsideTouchable(true);
        mTimePopup.setBackgroundDrawable(new BitmapDrawable());
        mTimePopup.setAnimationStyle(R.style.PostBarShareAnim);

        mTimePopup.showAtLocation(view_title_bar, Gravity.BOTTOM, 0, 0);

        mTimePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                view_mask.setVisibility(View.GONE);
            }
        });
    }

    public void showRemindWindow() {
        view_mask.setVisibility(View.VISIBLE);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.item_popup_remind, null, false);

        // v.findViewById(id);
        InitTimeRemind(v);

        mTimePopup = new PopupWindow(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mTimePopup.setOutsideTouchable(true);
        mTimePopup.setBackgroundDrawable(new BitmapDrawable());
        mTimePopup.setAnimationStyle(R.style.PostBarShareAnim);

        mTimePopup.showAtLocation(view_title_bar, Gravity.BOTTOM, 0, 0);

        mTimePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                view_mask.setVisibility(View.GONE);
            }
        });
    }

    private View InitTimeRemind(View view) {

        remind = (WheelView) view.findViewById(R.id.remind);
        String[] items = new String[10];
        items[0] = "不提醒";
        items[1] = "按时提醒";
        items[2] = "提前5分钟";
        items[3] = "提前15分钟";
        items[4] = "提前30分钟";
        items[5] = "提前1小时";
        items[6] = "提前2小时";
        items[7] = "提前6小时";
        items[8] = "提前1天";
        items[9] = "提前2天";

        arryadapter = new ArrayWheelAdapter<>(this, items);
        remind.setViewAdapter(arryadapter);
        remind.setCyclic(false);// 是否可循环滑动
        remind.setVisibleItems(items.length);// 设置显示行数
        remind.setCurrentItem(1);
        arryadapter.setTextColor(getResources().getColor(R.color.simi_color_black));
        TextView bt = (TextView) view.findViewById(R.id.tv_get_time);
        bt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = remind.getCurrentItem();
                remindAlerm = currentItem;
                String itemText = (String) arryadapter.getItemText(currentItem);
                tv_xiaoxi_content.setText(itemText);
                if (null != mTimePopup) {
                    mTimePopup.dismiss();
                }
                // Toast.makeText(MainPlusTrevelActivity.this, time, 1).show();
            }
        });
        TextView cancel = (TextView) view.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mTimePopup) {
                    mTimePopup.dismiss();
                }
            }
        });
        return view;
    }

    private View InitTimePick(View view) {

        hour = (WheelView) view.findViewById(R.id.hour);
        NumericWheelAdapter numericWheelAdapter1 = new NumericWheelAdapter(this, 0, 23, "%02d");
        numericWheelAdapter1.setLabel("时");
        hour.setViewAdapter(numericWheelAdapter1);
        hour.setCyclic(false);// 是否可循环滑动
        // hour.addScrollingListener(scrollListener);

        minute = (WheelView) view.findViewById(R.id.minute);
        NumericWheelAdapter numericWheelAdapter2 = new NumericWheelAdapter(this, 0, 59, "%02d");
        numericWheelAdapter2.setLabel("分");
        minute.setViewAdapter(numericWheelAdapter2);
        minute.setCyclic(false);
        // minute.addScrollingListener(scrollListener);

        // day = (WheelView) view.findViewById(R.id.day);
        // initDay(curYear, curMonth);
        // day.setCyclic(false);

        hour.setVisibleItems(7);// 设置显示行数
        minute.setVisibleItems(7);
        // day.setVisibleItems(7);

        hour.setCurrentItem(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        minute.setCurrentItem(Calendar.getInstance().get(Calendar.MINUTE));
        // day.setCurrentItem(curDate - 1);

        TextView bt = (TextView) view.findViewById(R.id.tv_get_time);
        bt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int mhour = hour.getCurrentItem();
                int mMinu = minute.getCurrentItem();

                String time = (mhour < 10 ? "0" + mhour : mhour) + ":" + (mMinu < 10 ? "0" + mMinu : mMinu);
                // Calendar cal = Calendar.getInstance();
                // int hour = cal.get(Calendar.HOUR_OF_DAY);
                // int minute = cal.get(Calendar.MINUTE);

                Date chooseDate = null;
                Date curDate = null;
                Date currentTime = new Date();

                String timeString = new SimpleDateFormat("HH:mm").format(currentTime.getTime());

                try {
                    chooseDate = new SimpleDateFormat("HH:mm").parse(time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                try {
                    curDate = new SimpleDateFormat("HH:mm").parse(timeString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (chooseDate.getTime() < curDate.getTime()) {
                    UIUtils.showToast(MainPlusTravelActivity.this, "您只能选择未来时间进行提醒哦！");
                } else {
                    tv_chufa_time.setText(time);
                    if (null != mTimePopup) {
                        mTimePopup.dismiss();
                    }
                }
            }
        });
        TextView cancel = (TextView) view.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mTimePopup) {
                    mTimePopup.dismiss();
                }
            }
        });
        return view;
    }

    private View InitDataPick(View view) {
        Calendar c = Calendar.getInstance();
        int norYear = c.get(Calendar.YEAR);
        int curMonth = c.get(Calendar.MONTH) + 1;// 通过Calendar算出的月数要+1
        int curDate = c.get(Calendar.DATE);

        // int curYear = mYear;
        // int curMonth = mMonth + 1;
        // int curDate = mDay;

        year = (WheelView) view.findViewById(R.id.year);

        NumericWheelAdapter numericWheelAdapter1 = new NumericWheelAdapter(this, norYear, 2065);
        numericWheelAdapter1.setLabel("年");
        year.setViewAdapter(numericWheelAdapter1);
        year.setCyclic(false);// 是否可循环滑动
        // year.addScrollingListener(scrollListener);
        year.setItemScrolistener(this);

        month = (WheelView) view.findViewById(R.id.month);
        NumericWheelAdapter numericWheelAdapter2 = new NumericWheelAdapter(this, 1, 12, "%02d");
        numericWheelAdapter2.setLabel("月");
        month.setViewAdapter(numericWheelAdapter2);
        month.setCyclic(false);
        // month.addScrollingListener(scrollListener);
        month.setItemScrolistener(this);

        day = (WheelView) view.findViewById(R.id.day);
        initDay(norYear, curMonth);
        day.setCyclic(false);

        year.setVisibleItems(7);// 设置显示行数
        month.setVisibleItems(7);
        day.setVisibleItems(7);

        year.setCurrentItem(norYear - 2015);
        month.setCurrentItem(curMonth - 1);
        day.setCurrentItem(curDate - 1);

        TextView bt = (TextView) view.findViewById(R.id.tv_get_time);
        bt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                int mYear = year.getCurrentItem() + 2015;
                int mMonth = month.getCurrentItem() + 1;
                int mDay = day.getCurrentItem() + 1;
                String date = mYear + "-" + mMonth + "-" + mDay;

                // Calendar cal = Calendar.getInstance();
                // int day = cal.get(Calendar.DATE); // 日
                // int month = cal.get(Calendar.MONTH) + 1;// 月
                // int year = cal.get(Calendar.YEAR); // 年

                Date chooseDate = null;
                Date curDate = null;
                Date currentDate = new Date();

                String curdateString = new SimpleDateFormat("yyyy-MM-dd").format(currentDate.getTime());

                try {
                    chooseDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                try {
                    curDate = new SimpleDateFormat("yyyy-MM-dd").parse(curdateString);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (chooseDate.getTime() < curDate.getTime()) {
                    UIUtils.showToast(MainPlusTravelActivity.this, "您只能选择未来时间进行提醒哦！");
                } else {
                    tv_date.setText(date);
                    if (null != mTimePopup) {
                        mTimePopup.dismiss();
                    }
                }

            }
        });
        TextView cancel = (TextView) view.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mTimePopup) {
                    mTimePopup.dismiss();
                }
            }
        });
        return view;
    }

    // OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
    // @Override
    // public void onScrollingStarted(WheelView wheel) {
    //
    // }
    //
    // @Override
    // public void onScrollingFinished(WheelView wheel) {
    // int n_year = year.getCurrentItem() + 1950;// 年
    // int n_month = month.getCurrentItem() + 1;// 月
    //
    // initDay(n_year, n_month);
    //
    // String birthday = new StringBuilder().append((year.getCurrentItem() + 1950)).append("-")
    // .append((month.getCurrentItem() + 1) < 10 ? "0" + (month.getCurrentItem() + 1) : (month.getCurrentItem() + 1)).append("-")
    // .append(((day.getCurrentItem() + 1) < 10) ? "0" + (day.getCurrentItem() + 1) : (day.getCurrentItem() + 1)).toString();
    // // tv1.setText("年龄             "+calculateDatePoor(birthday)+"岁");
    // // tv2.setText("星座             "+getAstro(month.getCurrentItem() + 1,day.getCurrentItem()+1));
    // }
    // };
    private PopupWindow mTimePopup;
    private TextView tv_date, tv_chufa_time;
    private String start_city_id;
    private String end_city_id;
    private WheelView remind;
    private ArrayWheelAdapter<String> arryadapter;
    private TextView tv_xiaoxi_content;
    private String cultime;
    private String is_senior;
    private ToggleButton slipBtn_mishuchuli;
    private ToggleButton slipBtn_dingjipiao;
    private TextView tv_beizu_content;
    private Cards card;
    private Button bt_create_travel;
    private TextView tv_senser_tip;
    private boolean isUsersenior;

    /**
     * 
     * @param year
     * @param month
     * @return
     */
    private int getDay(int year, int month) {
        int day = 30;
        boolean flag = false;
        switch (year % 4) {
        case 0:
            flag = true;
            break;
        default:
            flag = false;
            break;
        }
        switch (month) {
        case 1:
        case 3:
        case 5:
        case 7:
        case 8:
        case 10:
        case 12:
            day = 31;
            break;
        case 2:
            day = flag ? 29 : 28;
            break;
        default:
            day = 30;
            break;
        }
        return day;
    }

    /**
     */
    private void initDay(int arg1, int arg2) {
        NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(this, 1, getDay(arg1, arg2), "%02d");
        numericWheelAdapter.setLabel("日");
        day.setViewAdapter(numericWheelAdapter);
    }

    // create xingcheng
    /**
     * 创建行程
     */
    private void createTrevel() {
        showDialog();

        String c_id = DBHelper.getUser(MainPlusTravelActivity.this).getId();

        String date = tv_date.getText().toString();
        String time = tv_chufa_time.getText().toString();
        String mtime = " " + date + " " + time + "";
        Date mdate = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            mdate = format.parse(mtime);
            cultime = mdate.getTime() / 1000 + "";
            LogOut.debug("cultime:" + cultime);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        final Date fdate = mdate;

        if (StringUtils.isEquals( userInfo.getUser_type(), "1") && StringUtils.isEmpty(for_userid)) {
            UIUtils.showToast(MainPlusTravelActivity.this, "请选择为谁创建");
        }

        if (StringUtils.isEmpty(start_city_id) || StringUtils.isEmpty(end_city_id)) {
            UIUtils.showToast(MainPlusTravelActivity.this, "请选择差旅城市");
            dismissDialog();
            return;
        }
        if (StringUtils.isEmpty(date) || StringUtils.isEmpty(time)) {
            UIUtils.showToast(MainPlusTravelActivity.this, "请选择出发时间和日期");
            dismissDialog();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("card_id", "0");
        map.put("card_type", "5");
        map.put("create_user_id", c_id + "");
        map.put("user_id", isUsersenior?for_userid:c_id);
        map.put("service_time", cultime);
        map.put("service_content", Constants.CARD_ADD_TREAVEL_CONTENT);
        map.put("set_remind", remindAlerm + "");
        map.put("set_now_send", "0");
        map.put("set_sec_do", SET_SEC_DO + "");
        map.put("ticket_from_city_id", start_city_id);
        map.put("ticket_to_city_id", end_city_id);
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().post(Constants.URL_GET_ADD_CARD, param, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                LogOut.debug("错误码：" + errorNo);
                dismissDialog();
                Toast.makeText(getApplicationContext(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                dismissDialog();
                LogOut.debug("成功:" + t.toString());

                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) {
                            Toast.makeText(MainPlusTravelActivity.this, "创建成功了", Toast.LENGTH_SHORT).show();
                            Constants.CARD_ADD_TREAVEL_CONTENT = "";
                            MainPlusTravelActivity.this.finish();

                            // 初始化本地提醒闹钟
                            AlermUtils.initAlerm(MainPlusTravelActivity.this, remindAlerm, fdate, "差旅规划", Constants.CARD_ADD_TREAVEL_CONTENT);
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            Toast.makeText(MainPlusTravelActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(MainPlusTravelActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(MainPlusTravelActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(MainPlusTravelActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainPlusTravelActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainPlusTravelActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    };

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Constants.CARD_ADD_TREAVEL_CONTENT = "";
    }

    @Override
    public void onFinished() {
        int mYear = year.getCurrentItem() + 2015;
        int mMonth = month.getCurrentItem() + 1;
        
        int maxIndex = getDay(mYear, mMonth);
        int index = day.getCurrentItem();
        if(index > maxIndex-1){
            index = maxIndex;
            day.setCurrentItem(index-1);
        }
        
        initDay(mYear, mMonth);
    }
}
