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
import android.widget.EditText;
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
 * 邀约通知
 * 
 * @author yejiu
 * 
 */
public class MainPlusNotificationActivity extends BaseActivity implements OnClickListener, ItemScroListener {
    
    private PopupWindow mTimePopup;
    private TextView tv_date, tv_chufa_time;
    private String start_city_id;
    private String end_city_id;
    private WheelView remind;
    private ArrayWheelAdapter<String> arryadapter;
    public static final int NEED_SEC_DO = 1;
    public static final int NO_SEC_DO = 0;
    private int SET_SEC_DO = NO_SEC_DO;
    public static final int NEED_SEND = 1;
    public static final int NO_SEND = 0;
    private int SET_SEND = NO_SEND;

    private RelativeLayout select_phonenumber;
    private TextView tv_select_name;
    private TextView tv_select_number, tv_xiaoxi_content, tv_meeting_time;
    public static final int GET_CONTACT = 1001;
    public static final int GET_USER = 1002;
    private ArrayList<String> contactList;
    private View view_mask;

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

    private Date chooseDate; 
    private String finalTime;
    private String uploadtime;
    private ContactBean contactBean;
    private String mJson;
    private ToggleButton slipBtn_mishuchuli, slipBtn_fatongzhi;
    private String is_senior;
    private TextView tv_beizu_content;

    private int remindAlerm = 1;// 提醒设置 0 = 不提醒 1 = 按时提醒 2 = 5分钟 3 = 15分钟 4 = 提前30分钟 5 = 提前一个小时 6 = 提前2小时 7 = 提前6小时 8 = 提前一天 9 = 提前两天
    private Button bt_create_travel;

    private boolean isUpdate = false;
    private Cards card;
    private Date fdate;
    private TextView tv_senser_tip;
    private TextView tv_select_who_name;
    private String for_userid = "";
    private UserInfo userInfo;
    private RelativeLayout layout_select_who;
    private boolean isUsersenior;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_main_plus_notification);
        super.onCreate(savedInstanceState);
        userInfo = DBHelper.getUserInfo(this);
        
        card = (Cards) getIntent().getSerializableExtra("cards");
        initView(card);

    }

    private void initView(Cards card) {
        requestBackBtn();
        requestRightBtn();
        setTitleName("邀约通知");

        findViewById(R.id.layout_select_time).setOnClickListener(this);
        findViewById(R.id.layout_select_phonenumber).setOnClickListener(this);
        findViewById(R.id.layout_meeting_content).setOnClickListener(this);
        findViewById(R.id.layout_message_tongzhi).setOnClickListener(this);
   layout_select_who = (RelativeLayout) findViewById(R.id.layout_select_who);
        layout_select_who.setOnClickListener(this);

        bt_create_travel = (Button) findViewById(R.id.bt_create_travel);
        bt_create_travel.setOnClickListener(this);

        tv_select_name = (TextView) findViewById(R.id.tv_select_name);
        tv_select_number = (TextView) findViewById(R.id.tv_select_number);
        tv_xiaoxi_content = (TextView) findViewById(R.id.tv_xiaoxi_content);
        tv_meeting_time = (TextView) findViewById(R.id.tv_meeting_time);
        tv_beizu_content = (TextView) findViewById(R.id.tv_beizu_content);
        tv_senser_tip = (TextView) findViewById(R.id.tv_senser_tip);
 tv_select_who_name = (TextView) findViewById(R.id.tv_select_who_name);
        view_mask = (View) findViewById(R.id.view_mask);

        slipBtn_mishuchuli = (ToggleButton) findViewById(R.id.slipBtn_mishuchuli);
        slipBtn_fatongzhi = (ToggleButton) findViewById(R.id.slipBtn_fatongzhi);

        is_senior = userInfo.getIs_senior();
        String user_type = userInfo.getUser_type();
       isUsersenior = StringUtils.isEquals(user_type, "1");
        if (isUsersenior) {
            layout_select_who.setVisibility(View.VISIBLE);
        }else{
            layout_select_who.setVisibility(View.GONE);
        }

        slipBtn_mishuchuli.setOnToggleChanged(new OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on) {
                    if (StringUtils.isEquals(is_senior, "1")) {// 有秘书
                        if (chooseDate == null) {
                            slipBtn_mishuchuli.setToggleOff();
                            SET_SEC_DO = NO_SEC_DO;
                            UIUtils.showToast(MainPlusNotificationActivity.this, "请选择时间");
                            return;
                        }
                        
                        //0:00-15:00
                        if(DateUtils.isTime15Before(chooseDate) && DateUtils.isOperatingTimeIn(chooseDate)){
                            slipBtn_mishuchuli.setToggleOn();
                            SET_SEC_DO = NEED_SEC_DO;
                            tv_senser_tip.setVisibility(View.GONE);
                        } 
                        //15:01-0:00
                        else if(DateUtils.isTime15Later(chooseDate) && DateUtils.isOperatingTimeIn(chooseDate)){
                            slipBtn_mishuchuli.setToggleOn();
                            SET_SEC_DO = NEED_SEC_DO;
                            tv_senser_tip.setVisibility(View.GONE);
                        } 
                        //选择时间不对的情况走else
                        else{
                            slipBtn_mishuchuli.setToggleOff();
                            SET_SEC_DO = NO_SEC_DO;
                            tv_senser_tip.setVisibility(View.VISIBLE);
                            UIUtils.showActionDialog(MainPlusNotificationActivity.this, "提醒", "秘书工作时间为7:00～19:00，请在此时间内设置秘书提醒时间，0:01—15:00可以设置当天11:00之后的提醒；15:01至0:00可以设置次日7:00之后的提醒。","确定", null, null, null);
                        }
                        
                    } else {
                        slipBtn_mishuchuli.setToggleOff();
                        SET_SEC_DO = NO_SEC_DO;
                        tv_senser_tip.setVisibility(View.GONE);
                        startActivity(new Intent(MainPlusNotificationActivity.this, FindSecretaryActivity.class));
                        Toast.makeText(MainPlusNotificationActivity.this, "你没有购买秘书卡", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    tv_senser_tip.setVisibility(View.GONE);
                }
            }
        });
        slipBtn_fatongzhi.setOnToggleChanged(new OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on) {
                    SET_SEND = NEED_SEND;
                } else {
                    SET_SEND = NO_SEND;
                }
            }
        });
        if (null != card) {
            isUpdate = true;

            Constants.CARD_ADD_NOTIFICATION_CONTENT = card.getService_content();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            long Service_time = Long.valueOf(card.getService_time()) * 1000;

            tv_meeting_time.setText(format.format(Service_time));
            ArrayList<CardAttend> attends = card.getAttends();
            String name = null;
            ArrayList<ContactBean> arrayList = new ArrayList<>();
            if (attends.size() > 0) {
                for (int i = 0; i < attends.size(); i++) {
                    contactBean = new ContactBean();
                    contactBean.setMobile(attends.get(i).getMobile());
                    contactBean.setName(attends.get(i).getName());
                    arrayList.add(contactBean);
                    if (name != null) {
                        name += "," + attends.get(i).getName();
                    } else {
                        name = attends.get(i).getName();
                    }
                }
                tv_select_name.setText(name);
                tv_select_number.setText(attends.size() + "位");

                mJson = new Gson().toJson(arrayList);
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
                    SET_SEC_DO = NEED_SEC_DO;
                } else if (sec_do == 0) {// 无秘书
                    slipBtn_mishuchuli.setToggleOff();
                    SET_SEC_DO = NO_SEC_DO;
                }

                // 立即给相关人员发送消息
                int now_send = Integer.valueOf(card.getSet_now_send());
                LogOut.debug("是否通知所有人:" + now_send);

                if (now_send == 1) {// 发送
                    slipBtn_fatongzhi.setToggleOn();
                    SET_SEND = NEED_SEND;
                } else if (now_send == 0) {// 不发送
                    slipBtn_fatongzhi.setToggleOff();
                    SET_SEND = NO_SEND;
                }

            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.layout_select_phonenumber:// 选择通讯录
            Intent intent = new Intent(MainPlusNotificationActivity.this, ContactSelectActivity.class);
            startActivityForResult(intent, GET_CONTACT);

            break;
        case R.id.layout_meeting_content:
            Intent intent2 = new Intent(MainPlusNotificationActivity.this, MainPlusContentActivity.class);
            intent2.putExtra(Constants.MAIN_PLUS_FLAG, Constants.NOTIFICATION);
            startActivity(intent2);
            break;
        case R.id.layout_message_tongzhi:
            showRemindWindow();
            break;
        case R.id.layout_select_time:
            showDateWindow();
            break;
        case R.id.bt_create_travel:
            createMeetingCard(isUpdate);
            break;
        case R.id.layout_select_who:
            Intent mintent = new Intent(MainPlusNotificationActivity.this, CreateForWhoActivity.class);
            startActivityForResult(mintent, GET_USER);
            break;

        default:
            break;
        }

    }

    /**
     * 日期选择器
     */
    public void showDateWindow() {
        view_mask.setVisibility(View.VISIBLE);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.item_popup_fulldatapick, null, false);

        InitDataPick(v);

        mTimePopup = new PopupWindow(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mTimePopup.setOutsideTouchable(true);
        mTimePopup.setBackgroundDrawable(new BitmapDrawable());
        mTimePopup.setAnimationStyle(R.style.PostBarShareAnim);

        mTimePopup.showAtLocation(view_mask, Gravity.BOTTOM, 0, 0);

        mTimePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                view_mask.setVisibility(View.GONE);
            }
        });
    }

    private View InitDataPick(View view) {
        Calendar c = Calendar.getInstance();
        int norYear = c.get(Calendar.YEAR);
        int curMonth = c.get(Calendar.MONTH) + 1;// 通过Calendar算出的月数要+1
        int curDate = c.get(Calendar.DATE);

        year = (WheelView) view.findViewById(R.id.year);

        NumericWheelAdapter numericWheelAdapter1 = new NumericWheelAdapter(this, norYear, 2065);
        numericWheelAdapter1.setLabel("年");
        year.setViewAdapter(numericWheelAdapter1);
        year.setCyclic(false);// 是否可循环滑动

        month = (WheelView) view.findViewById(R.id.month);
        NumericWheelAdapter numericWheelAdapter2 = new NumericWheelAdapter(this, 1, 12, "%02d");
        numericWheelAdapter2.setLabel("月");
        month.setViewAdapter(numericWheelAdapter2);
        month.setCyclic(false);

        day = (WheelView) view.findViewById(R.id.day);
        initDay(norYear, curMonth);
        day.setCyclic(false);

        hour = (WheelView) view.findViewById(R.id.hour);
        NumericWheelAdapter hourAdpter = new NumericWheelAdapter(this, 0, 23, "%02d");
        hourAdpter.setLabel("时");
        hour.setViewAdapter(hourAdpter);
        hour.setCyclic(false);// 是否可循环滑动
        // hour.addScrollingListener(scrollListener);

        minute = (WheelView) view.findViewById(R.id.minute);
        NumericWheelAdapter minuteAdater = new NumericWheelAdapter(this, 0, 59, "%02d");
        minuteAdater.setLabel("分");
        minute.setViewAdapter(minuteAdater);
        minute.setCyclic(false);

        year.setVisibleItems(7);// 设置显示行数
        month.setVisibleItems(7);
        day.setVisibleItems(7);
        hour.setVisibleItems(7);// 设置显示行数
        minute.setVisibleItems(7);

        hour.setCurrentItem( Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        minute.setCurrentItem(Calendar.getInstance().get(Calendar.MINUTE));
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
                int mhour = hour.getCurrentItem();
                int mMinu = minute.getCurrentItem();
                String date = mYear + "-" + mMonth + "-" + mDay;
                String time = mhour + ":" + mMinu ;

//              Calendar cal = Calendar.getInstance();
//              int day = cal.get(Calendar.DATE); // 日
//              int month = cal.get(Calendar.MONTH) + 1;// 月
//              int year = cal.get(Calendar.YEAR); // 年
//              int hour = cal.get(Calendar.HOUR_OF_DAY);
//              int minute = cal.get(Calendar.MINUTE);
              
              Date currentDate = new Date();;
              try {
                  chooseDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date + " " + time);
              } catch (ParseException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
              }

//              if (mYear < year || mMonth < month || mDay < day || mhour < hour || mMinu < minute) {
              if(chooseDate.getTime() < currentDate.getTime()){
                    UIUtils.showToast(MainPlusNotificationActivity.this, "您只能选择未来时间进行提醒哦！");
                }else{
                    String cultime = (mhour < 10 ? "0" + mhour : mhour) + ":" + (mMinu < 10 ? "0" + mMinu : mMinu);
                    finalTime = date + " " + cultime;

                    tv_meeting_time.setText(finalTime);
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

        mTimePopup.showAtLocation(view_mask, Gravity.BOTTOM, 0, 0);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
            case GET_CONTACT:
                contactList = data.getExtras().getStringArrayList("contact");

                if (contactList != null && contactList.size() > 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String name = null;
                            for (int i = 0; i < contactList.size(); i++) {
                                String bean = contactList.get(i).toString();
                                if (name != null) {
                                    name += "," + bean.substring(0, bean.indexOf("\n"));
                                } else {
                                    name = bean.substring(0, bean.indexOf("\n"));
                                }
                            }

                            tv_select_name.setText("已选择：" + name);
                            tv_select_number.setText(contactList.size() + "位");
                        }
                    });

                }
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
     * 发起会议
     * 
     * @param isUpdate2
     */
    private void createMeetingCard(boolean update) {
        showDialog();

        if (!isUpdate) {// 如果不是更新的
            if (null != contactList && contactList.size() > 0) {

                ArrayList<ContactBean> arrayList = new ArrayList<>();
                for (int i = 0; i < contactList.size(); i++) {
                    contactBean = new ContactBean();
                    String bean = contactList.get(i).toString();
                    String name = bean.substring(0, bean.indexOf("\n"));
                    String number = bean.substring(bean.indexOf("\n") + 1, bean.length());
                    contactBean.setMobile(number);
                    contactBean.setName(name);
                    arrayList.add(contactBean);

                }
                mJson = new Gson().toJson(arrayList);
                LogOut.debug("json:" + mJson);
            } else {
                UIUtils.showToast(MainPlusNotificationActivity.this, "请选择参会人员");
                dismissDialog();
                return;
            }

        }

       

        String c_id = userInfo.getId();

        String mtime = " " + finalTime + "";

        String meetingtime = tv_meeting_time.getText().toString();
        if (StringUtils.isEmpty(finalTime) && StringUtils.isNotEmpty(meetingtime) && isUpdate) {// 如果时间不为空，并且是更新过来的。则采用现有的时间。
            uploadtime = card.getService_time();

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m:s");
            long Service_time = Long.valueOf(uploadtime) * 1000;
            try {
                fdate = format.parse(format.format(Service_time));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else {
            Date mdate = null;
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                mdate = format.parse(mtime);
                uploadtime = mdate.getTime() / 1000 + "";
                LogOut.debug("cultime:" + uploadtime);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }

            fdate = mdate;

        }

        if (StringUtils.isEquals( userInfo.getUser_type(), "1") && StringUtils.isEmpty(for_userid)) {
            UIUtils.showToast(MainPlusNotificationActivity.this, "请选择为谁创建");
        }
        if (StringUtils.isEmpty(mtime)) {
            UIUtils.showToast(MainPlusNotificationActivity.this, "请选择邀约时间");
            dismissDialog();
            return;
        }
       

        Map<String, String> map = new HashMap<String, String>();
        map.put("card_id", update ? card.getCard_id() : "0");
        map.put("card_type", "4");
        map.put("create_user_id", c_id + "");
        map.put("user_id",  isUsersenior?for_userid:c_id);
        map.put("attends", mJson);
        map.put("service_time", uploadtime);
        map.put("service_content", Constants.CARD_ADD_NOTIFICATION_CONTENT);
        map.put("set_remind", remindAlerm + "");
        map.put("set_now_send", SET_SEND +"");
        map.put("set_sec_do", SET_SEC_DO + "");

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
                            Toast.makeText(MainPlusNotificationActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
                            Constants.CARD_ADD_NOTIFICATION_CONTENT="";
                            MainPlusNotificationActivity.this.finish();

                            // 初始化本地提醒闹钟
                            AlermUtils.initAlerm(MainPlusNotificationActivity.this, remindAlerm, fdate, "邀约通知",
                                    Constants.CARD_ADD_NOTIFICATION_CONTENT);
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            Toast.makeText(MainPlusNotificationActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(MainPlusNotificationActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(MainPlusNotificationActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(MainPlusNotificationActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainPlusNotificationActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainPlusNotificationActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    };

    @Override
    protected void onResume() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_beizu_content.setText(Constants.CARD_ADD_NOTIFICATION_CONTENT);
            }
        });
        super.onResume();
    }
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Constants.CARD_ADD_NOTIFICATION_CONTENT="";
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
