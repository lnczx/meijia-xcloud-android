package com.meijialife.simi.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.bean.ContactBean;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.ui.wheelview.ArrayWheelAdapter;
import com.meijialife.simi.ui.wheelview.NumericWheelAdapter;
import com.meijialife.simi.ui.wheelview.WheelView;
import com.meijialife.simi.ui.wheelview.WheelView.ItemScroListener;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;


/**
 * @description：请假
 * @author： kerryg
 * @date:2016年3月9日 
 */
public class MainPlusLeaveActivity extends BaseActivity implements OnClickListener, ItemScroListener {
    
    private PopupWindow mTimePopup;
    private WheelView remind;
    private ArrayWheelAdapter<String> arryadapter;
    public static final int NEED_SEC_DO = 1;
    public static final int NO_SEC_DO = 0;
    private int SET_SEC_DO = NO_SEC_DO;
    public static final int NEED_SEND = 1;
    public static final int NO_SEND = 0;
    private int SET_SEND = NO_SEND;

    private TextView tv_select_number, tv_xiaoxi_content, tv_meeting_time;
    public static final int GET_CONTACT = 1001;
    public static final int GET_USER = 1002;
    public static final int GET_TYPE = 1003;
    private View view_mask;

    private WheelView year;
    private WheelView month;
    private WheelView day;
    private WheelView hour;
    private WheelView minute;
    private int mYear = 2016;
    private int mMonth = 0;
    private int mDay = 1;

    private int mHour = 0;
    private int mMinute = 0;

    private Date chooseDate;
    private Date currentDate;
    private String finalTime;
    private String mJson;


    private String for_userid = "";
    private UserInfo userInfo;
  
    
    private TextView leave_type_content;//假期类型
    private TextView leave_start_day;//开始日期
    private TextView leave_end_day;//结束日期
    private TextView leave_days;//请假天数
    private TextView leave_content;//请假内容
    private TextView leave_select_who_name;//请假内容
    private TextView leave_num;//审批人数
    
    private String mCompanyId;
    private String mUserId;
    private String mLeaveType;
    private String mLeaveTypeId;
    private String mStartDate;
    private String mEndDate;
    private String mRemarks;
    private String mTotalDays;
    
    private int flag=0;//0=开始日期，1=结束日期
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_main_plus_leave);
        super.onCreate(savedInstanceState);
        userInfo = DBHelper.getUserInfo(this);
        
        initView();

    }

    private void initView() {
        requestBackBtn();
        setTitleName("请假");
        
        findView();

    }
    
    private void findView(){
        leave_type_content = (TextView)findViewById(R.id.leave_type_content);
        leave_start_day = (TextView)findViewById(R.id.leave_start_day);
        leave_end_day = (TextView)findViewById(R.id.leave_end_day);
        leave_days =  (TextView)findViewById(R.id.leave_total_days);
        leave_content = (TextView)findViewById(R.id.tv_beizu_content);
        leave_select_who_name =  (TextView)findViewById(R.id.leave_select_who_name);
        leave_num =  (TextView)findViewById(R.id.leave_num);

        view_mask = (View) findViewById(R.id.view_mask);

        
        findViewById(R.id.layout_start_day).setOnClickListener(this);;
        findViewById(R.id.layout_leave_type).setOnClickListener(this);;
        findViewById(R.id.layout_end_day).setOnClickListener(this);;
        findViewById(R.id.layout_select_who).setOnClickListener(this);;
        findViewById(R.id.layout_leave_content).setOnClickListener(this);;
        findViewById(R.id.bt_create_leave).setOnClickListener(this);;
    }

    @Override
    public void onClick(View v) {
        Intent intent ;
        switch (v.getId()) {
        case R.id.layout_leave_content:
            intent = new Intent(MainPlusLeaveActivity.this, MainPlusContentActivity.class);
            intent.putExtra(Constants.MAIN_PLUS_FLAG, Constants.LEAVE);
            startActivity(intent);
            break;
        case R.id.layout_leave_type:
            intent = new Intent(MainPlusLeaveActivity.this, MainPlusLeaveTypeActivity.class);
            startActivityForResult(intent, GET_TYPE);
            break;
        case R.id.layout_end_day:
            flag = 1;
            showDateWindow();
            break;
        case R.id.layout_start_day:
            flag = 0;
            showDateWindow();
            break;
        case R.id.bt_create_leave:
            postLeave();
            break;
        case R.id.layout_select_who:
            if (userInfo.getHas_company() == 0) {
                Intent intent1 = new Intent(this, WebViewActivity.class);
                intent1.putExtra("title", "企业通讯录");
                intent1.putExtra("url", Constants.HAS_COMPANY);
                startActivity(intent1);
            } else {
                intent = new Intent(MainPlusLeaveActivity.this,CompanyListActivity.class);
                intent.putExtra("flag",1);
                startActivity(intent);
            }
            
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
        View v = inflater.inflate(R.layout.item_popup_datapick, null, false);
        
        if(flag==0){
            InitStartDataPick(v);
        }else if (flag==1) {
            InitEndDataPick(v);
        }

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

    private View InitStartDataPick(View view) {
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

        year.setVisibleItems(7);// 设置显示行数
        month.setVisibleItems(7);
        day.setVisibleItems(7);
      

        year.setCurrentItem(norYear - 2016);
        month.setCurrentItem(curMonth - 1);
        day.setCurrentItem(curDate - 1);
        TextView bt = (TextView) view.findViewById(R.id.tv_get_time);
        bt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                int mYear = year.getCurrentItem() + 2016;
                int mMonth = month.getCurrentItem() + 1;
                int mDay = day.getCurrentItem() + 1;
                String date = mYear + "-" + mMonth + "-" + mDay;
             try {
              String currentDateString = DateUtils.getStringByPattern(new Date().getTime(), "yyyy-MM-dd"); 
                  currentDate = new SimpleDateFormat("yyyy-MM-dd").parse(currentDateString);
                  chooseDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
              } catch (ParseException e) {
                  e.printStackTrace();
              }
              if(chooseDate.getTime() < currentDate.getTime()){
                    UIUtils.showToast(MainPlusLeaveActivity.this, "您只能选择未来时间进行请假哦！");
                }else{
                    finalTime = date ;
                     leave_start_day.setText(finalTime);
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
     * @param view
     * @return
     */
    private View InitEndDataPick(View view) {
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

        year.setVisibleItems(7);// 设置显示行数
        month.setVisibleItems(7);
        day.setVisibleItems(7);
      

        year.setCurrentItem(norYear - 2016);
        month.setCurrentItem(curMonth - 1);
        day.setCurrentItem(curDate - 1);
        TextView bt = (TextView) view.findViewById(R.id.tv_get_time);
        bt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                int mYear = year.getCurrentItem() + 2016;
                int mMonth = month.getCurrentItem() + 1;
                int mDay = day.getCurrentItem() + 1;
                String date = mYear + "-" + mMonth + "-" + mDay;
             try {
              String currentDateString = DateUtils.getStringByPattern(new Date().getTime(), "yyyy-MM-dd"); 
                  currentDate = new SimpleDateFormat("yyyy-MM-dd").parse(currentDateString);
                  chooseDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
              } catch (ParseException e) {
                  e.printStackTrace();
              }
             
             String startDate = leave_start_day.getText().toString().trim();
             if(!StringUtils.isEmpty(startDate) && !StringUtils.isEquals(startDate,"点击选择开始时间" )){
                 if(chooseDate.getTime() < currentDate.getTime()){
                     UIUtils.showToast(MainPlusLeaveActivity.this, "您只能选择未来时间进行请假哦！");
                 }else{
                    Date start = new Date();
                    Date end = new Date();
                  try {
                    start = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
                    end = new SimpleDateFormat("yyyy-MM-dd").parse(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(start.getTime()>=end.getTime()){
                        UIUtils.showToast(MainPlusLeaveActivity.this, "结束日期必须大于开始日期！");
                    }else {
                        Long endSecond =(end.getTime()/86400000);
                        Long startSecond = start.getTime()/86400000;
                        int totalDays = (endSecond.intValue()-startSecond.intValue());
                        leave_end_day.setText(date);
                        leave_days.setText((totalDays+1)+"天");
                        if (null != mTimePopup) {
                            mTimePopup.dismiss();
                        }
                    }
                 }
             }else {
                 UIUtils.showToast(MainPlusLeaveActivity.this, "开始日期不能为空！");
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

  

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
            case GET_TYPE:
                mLeaveType = data.getExtras().getString("leaveTypeName");
                mLeaveTypeId = data.getExtras().getString("leaveTypeId");
                Constants.LEAVE_TYPE_NAME = mLeaveType ;
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
    private void postLeave() {

        mStartDate = leave_start_day.getText().toString().trim();
        mEndDate = leave_end_day.getText().toString().trim();
        mLeaveType = leave_type_content.getText().toString().trim();
        mRemarks = leave_content.getText().toString().trim();
        mStartDate = leave_start_day.getText().toString().trim();
        mEndDate = leave_end_day.getText().toString().trim();
      
        if(StringUtils.isEmpty(mLeaveType)){
            Toast.makeText(MainPlusLeaveActivity.this, "请假类型不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(StringUtils.isEmpty(mStartDate) || StringUtils.isEquals(mStartDate, "点击选择开始时间")){
            Toast.makeText(MainPlusLeaveActivity.this, "请选择开始时间",Toast.LENGTH_SHORT).show();
            return;
        }
        if(StringUtils.isEmpty(mEndDate) || StringUtils.isEquals(mEndDate, "点击选择结束时间")){
            Toast.makeText(MainPlusLeaveActivity.this, "请选择结束时间",Toast.LENGTH_SHORT).show();
            return;
        }
     
        if(StringUtils.isEmpty(mRemarks)){
            Toast.makeText(MainPlusLeaveActivity.this, "请假内容不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(contactBeanList==null || contactBeanList.size()<=0){
            Toast.makeText(MainPlusLeaveActivity.this, "请选择审批人",Toast.LENGTH_SHORT).show();
            return;
        }
        Gson gson = new Gson();
        mJson = gson.toJson(contactBeanList);
        
        showDialog();
        Map<String, String> map = new HashMap<String, String>();
        map.put("company_id", userInfo.getCompany_id());
        map.put("user_id", userInfo.getUser_id());
        map.put("leave_type", mLeaveTypeId);
        map.put("start_date", mStartDate);
        map.put("end_date", mEndDate);
        map.put("total_days", leave_days.getText().toString().trim());
        map.put("remarks", mRemarks);
        map.put("pass_users", mJson);

        AjaxParams param = new AjaxParams(map);
        new FinalHttp().post(Constants.POST_LEAVE_ORDER_URL, param, new AjaxCallBack<Object>() {

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
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) {
                            Toast.makeText(MainPlusLeaveActivity.this, "申请成功", Toast.LENGTH_SHORT).show();
                            MainPlusLeaveActivity.this.finish();
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            Toast.makeText(MainPlusLeaveActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(MainPlusLeaveActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(MainPlusLeaveActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(MainPlusLeaveActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainPlusLeaveActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainPlusLeaveActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    };

    private ArrayList<ContactBean> contactBeanList = new ArrayList<ContactBean>();
    private String appMan ="";
    
    @Override
    protected void onResume() {
        appMan="";
        HashMap<String,ContactBean> map = Constants.finalContacBeantMap;
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
              Object key = entry.getKey();
              ContactBean val =(ContactBean) entry.getValue();
              appMan=appMan+val.getName()+",";
              contactBeanList.add(val);
      }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                leave_content.setText(Constants.LEAVE_TYPE_REMARK);
                leave_type_content.setText(Constants.LEAVE_TYPE_NAME);
                if(null !=contactBeanList && contactBeanList.size()>0){
                    appMan = appMan.substring(0, appMan.lastIndexOf(","));
                    leave_select_who_name.setText(appMan);
                    leave_num.setText(contactBeanList.size()+"人");
                }
            }
        });
        super.onResume();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constants.LEAVE_TYPE_NAME="";
        Constants.LEAVE_TYPE_REMARK="";
        Constants.finalContactList.clear();
        Constants.finalContacBeantMap.clear();;
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
