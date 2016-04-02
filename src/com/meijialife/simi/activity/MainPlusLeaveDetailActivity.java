package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.PassUsersAdapter;
import com.meijialife.simi.bean.LeaveDetailData;
import com.meijialife.simi.bean.PassUsersData;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.ui.RoundImageView;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.OrderTypeUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * @description：请假详情
 * @author： kerryg
 * @date:2016年3月10日 
 */
public class MainPlusLeaveDetailActivity extends BaseActivity implements OnClickListener{
    
    private TextView mStartDate;//开始日期
    private TextView mEndDate;//结束日期
    private TextView mRemarks;//请假理由
    private TextView mTotalDays;//请假总天数
    private TextView mLeaveType;//请假类型
    private TextView mLeaveName;//用户名称
    private TextView mLeaveStatus;//审批状态
    private RoundImageView mHeadImg;//用户头像
    private ListView mPassUser;//审批进度
    private LinearLayout mPassLl;//审批按钮
    private LinearLayout mPassLl2;//审批按钮
    
    //接口请求参数
    private String mLeaveId;
    private LeaveDetailData mLeaveDetail;
    private int leave_from;//0=我发起的 1=我审批的
    
    
    private FinalBitmap finalBitmap;//
    private BitmapDrawable defDrawable;
    private PassUsersAdapter passUsersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.leave_detail_activity);
        super.onCreate(savedInstanceState);
        
        initView();
    }
    
    private void initView(){
        
        requestBackBtn();
        setTitleName("请假审批");
        
        finalBitmap = FinalBitmap.create(this);
        defDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.ad_loading);
        leave_from = getIntent().getIntExtra("flag",0);
        mLeaveId = getIntent().getStringExtra("leave_id");
        findView();
        
        getLeaveDetial(mLeaveId);
    }
    
    private void findView(){
        mStartDate = (TextView)findViewById(R.id.m_leave_start_date);
        mEndDate = (TextView)findViewById(R.id.m_leave_end_date);
        mRemarks = (TextView)findViewById(R.id.m_leave_remarks);
        mTotalDays = (TextView)findViewById(R.id.m_leave_total_days);
        mLeaveType = (TextView)findViewById(R.id.m_leave_type);
        mLeaveName = (TextView)findViewById(R.id.m_leave_name);
        mLeaveStatus = (TextView)findViewById(R.id.m_leave_statue_name);
        mHeadImg = (RoundImageView)findViewById(R.id.m_leave_head_img);
        mPassUser = (ListView)findViewById(R.id.m_leave_pass_users);
        mPassLl = (LinearLayout)findViewById(R.id.m_pass_ll);
        mPassLl2 = (LinearLayout)findViewById(R.id.m_pass_ll2);
        findViewById(R.id.m_leave_btn_agree).setOnClickListener(this);
        findViewById(R.id.m_leave_btn_refuse).setOnClickListener(this);
        findViewById(R.id.m_leave_btn_revocation).setOnClickListener(this);
        
        
        passUsersAdapter = new PassUsersAdapter(this);
        mPassUser.setAdapter(passUsersAdapter);
        
    }
    
    /**
     * 获得用户请详情
     */
    private void getLeaveDetial(String leave_id) {

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }
        User user = DBHelper.getUser(this);
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user.getId());
        map.put("leave_id", leave_id);
        AjaxParams param = new AjaxParams(map);

        showDialog();
        new FinalHttp().get(Constants.GET_LEAVE_DETAIL_URL, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(MainPlusLeaveDetailActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                mLeaveDetail = gson.fromJson(data, LeaveDetailData.class);
                                showData(mLeaveDetail);
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
                    UIUtils.showToast(MainPlusLeaveDetailActivity.this, errorMsg);
                }
            }
        });
    }
    private void showData(LeaveDetailData leaveDetailData){
        mStartDate.setText(leaveDetailData.getStart_date());
        mEndDate.setText(leaveDetailData.getEnd_date());
        mRemarks.setText(leaveDetailData.getRemarks());
        mTotalDays.setText(leaveDetailData.getTotal_days());
        mLeaveName.setText(leaveDetailData.getName());
        mLeaveStatus.setText(leaveDetailData.getStatus_name());
        mLeaveType.setText(OrderTypeUtils.getLeaveTypeName(leaveDetailData.getLeave_type()));
        finalBitmap.display(mHeadImg,leaveDetailData.getHead_img() ,defDrawable.getBitmap(), defDrawable.getBitmap());
        int status = leaveDetailData.getStatus();
        //status=1(审批通过),2(审批不通过),3(撤销)不显示按钮
        if(status==1 || status==2 || status==3){
            mPassLl.setVisibility(View.GONE);
            mPassLl2.setVisibility(View.GONE);
        }else {
            if(leave_from==0){//我发起的
                mPassLl.setVisibility(View.GONE);
                mPassLl2.setVisibility(View.VISIBLE);
            }else if (leave_from==1) {//待我审批的
                mPassLl.setVisibility(View.VISIBLE);
                mPassLl2.setVisibility(View.GONE);
            }            
        }
        ArrayList<PassUsersData> list = leaveDetailData.getPass_users();
        passUsersAdapter.setData(list);
        mLeaveId = leaveDetailData.getLeave_id();
        
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.m_leave_btn_agree://同意
            postLeavePass(mLeaveId,"1"); 
            break;
        case R.id.m_leave_btn_refuse://拒绝
            postLeavePass(mLeaveId,"2");
            break;
        case R.id.m_leave_btn_revocation://撤销
            postLeaveCancle(mLeaveId);
            break;
        default:
            break;
        }
    }
    /**
     * 请假审批接口
     * @param passUsersData
     */
     private void postLeavePass(String leaveId,String status) {
            if (!NetworkUtils.isNetworkConnected(this)) {
                Toast.makeText(MainPlusLeaveDetailActivity.this, getString(R.string.net_not_open), 0).show();
                return;
            }
            User user = DBHelper.getUser(this);
            Map<String, String> map = new HashMap<String, String>();
            map.put("leave_id", leaveId);
            map.put("status",status);//1=审批通过，2=审批不通过
            map.put("pass_user_id",user.getId());
            AjaxParams param = new AjaxParams(map);

            new FinalHttp().post(Constants.POSE_LEAVE_PASS_URL, param, new AjaxCallBack<Object>() {
                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    Toast.makeText(MainPlusLeaveDetailActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                getLeaveDetial(mLeaveId);
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
                        UIUtils.showToast(MainPlusLeaveDetailActivity.this, errorMsg);
                    }
                }
            });
        }
     /**
      * 请假撤销接口
      * @param passUsersData
      */
     private void postLeaveCancle(String leave_id) {
         
         if (!NetworkUtils.isNetworkConnected(this)) {
             Toast.makeText(MainPlusLeaveDetailActivity.this, getString(R.string.net_not_open), 0).show();
             return;
         }
         User user = DBHelper.getUser(this);
         Map<String, String> map = new HashMap<String, String>();
         map.put("user_id", user.getId());
         map.put("leave_id", leave_id);
         map.put("pass_user_id",user.getId());

         AjaxParams param = new AjaxParams(map);
         
         new FinalHttp().post(Constants.POSE_LEAVE_CANCEL_URL, param, new AjaxCallBack<Object>() {
             @Override
             public void onFailure(Throwable t, int errorNo, String strMsg) {
                 super.onFailure(t, errorNo, strMsg);
                 Toast.makeText(MainPlusLeaveDetailActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                             getLeaveDetial(mLeaveId);
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
                     UIUtils.showToast(MainPlusLeaveDetailActivity.this, errorMsg);
                 }
             }
         });
     }
}
