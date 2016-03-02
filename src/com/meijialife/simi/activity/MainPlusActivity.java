package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.FindPlusAdapter;
import com.meijialife.simi.bean.AppHelpData;
import com.meijialife.simi.bean.FindPlusData;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.publish.PublishDynamicActivity;
import com.meijialife.simi.ui.SelectableRoundedImageView;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;
import com.simi.easemob.EMConstant;
import com.simi.easemob.ui.ChatActivity;

public class MainPlusActivity extends Activity implements OnClickListener {

    private FindPlusAdapter mFindPlusAdapter;
    private GridView mGvFind;
    private ArrayList<FindPlusData> mFindPlusDatas;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_find);
        v= (RelativeLayout)getLayoutInflater()
                .inflate(R.layout.layout_find, null);
        initView();
    }

    private void initView() {

        findViewById(R.id.tv_call_mishu).setOnClickListener(this);
       
        findViewById(R.id.iv_plus_close).setOnClickListener(this);
        mFindPlusDatas = new ArrayList<FindPlusData>();
        mGvFind =(GridView) findViewById(R.id.gv_find);
        mGvFind.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mFindPlusAdapter = new FindPlusAdapter(this);
        mGvFind.setAdapter(mFindPlusAdapter);
        getFindPlusIcon();
        setClick();
        
        //请求帮助接口
        finalBitmap = FinalBitmap.create(this);
        defDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ad_loading);
        getAppHelp();
    }
    
    private void setClick(){
        mGvFind.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FindPlusData findPlusData =mFindPlusDatas.get(position);
                String category = findPlusData.getOpen_type().trim();
                String goto_url = findPlusData.getUrl().trim();
                String params = findPlusData.getParams().trim();
                String action = findPlusData.getAction().trim();
                if(category.equals("h5")){
                    Intent intent = new Intent(MainPlusActivity.this,WebViewsActivity.class);
                    intent.putExtra("url",goto_url);
                    startActivity(intent);
                }else if (category.equals("app")) {
                    if(action.equals("alarm")){
                        startActivity(new Intent(MainPlusActivity.this, MainPlusAffairActivity.class));
                        MainPlusActivity.this.finish();
                    }else if(action.equals("meeting")){
                        startActivity(new Intent(MainPlusActivity.this, MainPlusMeettingActivity.class));
                        MainPlusActivity.this.finish();
                    }else if(action.equals("notice")){
                        startActivity(new Intent(MainPlusActivity.this, MainPlusMorningActivity.class));
                        MainPlusActivity.this.finish();
                    }else if(action.equals("interview")){
                        startActivity(new Intent(MainPlusActivity.this, MainPlusNotificationActivity.class));
                        MainPlusActivity.this.finish();
                    }else if(action.equals("trip")){
                        startActivity(new Intent(MainPlusActivity.this, MainPlusTravelActivity.class));
                        MainPlusActivity.this.finish();
                    }else if(action.equals("punch_dynamic")){
                        Intent intent = new Intent(MainPlusActivity.this,PublishDynamicActivity.class);
                        startActivity(intent);
                        MainPlusActivity.this.finish();
                    }
                    else if(action.equals("card")){
                        Intent intent = new Intent(MainPlusActivity.this, CardDetailsActivity.class);
                        intent.putExtra("card_id", params);
                        startActivity(intent);
                    }else if (action.equals("feed")) {
                        Intent intent = new Intent(MainPlusActivity.this, DynamicDetailsActivity.class);
                        intent.putExtra("feedId", params);
                        startActivity(intent);
                    }else if (action.equals("checkin")) {
                        
                    }else if (action.equals("friends")) {
                        
                    }else if (action.equals("im")) {
                        Intent intent = new Intent(MainPlusActivity.this, ChatActivity.class);
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
                        
                    }else if (action.equals("leave")) {//请假申请
                        
                    }else if (action.equals("leave_pass")) {//请假审批
                        
                    }else if (action.equals("punch_sign")) {//打卡签到
                        Intent intent = new Intent(MainPlusActivity.this, MainPlusSignInActivity.class);
                        startActivity(intent);
                    }else if (action.equals("application_center")) {//应用中心
                        Intent intent = new Intent(MainPlusActivity.this, MainPlusApplicationActivity.class);
                        startActivity(intent);
                    }
                  
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.tv_call_mishu:
            UserInfo userInfo = DBHelper.getUserInfo(this);
            String is_senior = userInfo.getIs_senior();
            if(StringUtils.isEquals(is_senior, "1")){
                toChit(userInfo);
            }else if(StringUtils.isEquals(is_senior, "0")){
                startActivity(new Intent(MainPlusActivity.this,FindSecretaryActivity.class));
            }
            break;
        case R.id.iv_plus_close:
            finish();
            this.overridePendingTransition(R.anim.activity_close,0);  
            break;
        default:
            break;
        }

    }
    
    /**
     * 进入秘书or机器人助理聊天页面
     * @param userInfo 
     */
    private void toChit(UserInfo userInfo){
        Intent  intent = new Intent(MainPlusActivity.this, ChatActivity.class);
        intent.putExtra(EMConstant.EXTRA_USER_ID, userInfo.getIm_sec_username());
        intent.putExtra(EMConstant.EXTRA_USER_NAME, userInfo.getIm_sec_nickname());
        startActivity(intent);
    }
    /**
     * 获得导航列表接口
     */
    private void getFindPlusIcon() {

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }
        User user = DBHelper.getUser(this);
        Map<String, String> map = new HashMap<String, String>();
        map.put("app_type", "xcloud");
        map.put("user_id", user.getId());
        AjaxParams param = new AjaxParams(map);

        new FinalHttp().get(Constants.URL_GET_APP_INDEXS, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(MainPlusActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                mFindPlusDatas = gson.fromJson(data, new TypeToken<ArrayList<FindPlusData>>() {
                                }.getType());
                                
                                //前端静态增加应用中心
                                FindPlusData findPlusData = new FindPlusData();
                                findPlusData.setName("应用中心");
                                findPlusData.setOpen_type("app");
                                findPlusData.setUrl("");
                                findPlusData.setParams("");
                                findPlusData.setAction("application_center");
                                mFindPlusDatas.add(findPlusData);
                                
                                mFindPlusAdapter.setData(mFindPlusDatas);
                            } else {
                                mFindPlusDatas = new ArrayList<FindPlusData>();
                                mFindPlusAdapter.setData(mFindPlusDatas);
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
                    UIUtils.showToast(MainPlusActivity.this, errorMsg);
                }
            }
        });
    }
    

    private PopupWindow popupWindow;
    private TextView mDone;
    private ImageView tip_iv_icon;
    private SelectableRoundedImageView selectableRoundedImageView;
    private TextView tip_tv_title;
    private TextView tip_tv_content;
    private TextView tip_tv_more;
    private BitmapDrawable defDrawable;
    private FinalBitmap finalBitmap;
    private AppHelpData appHelpData;
    private View v;
    /**
     * 弹出窗口
     */
    private void popWindow(final AppHelpData appHelpData) {
        View view = (LinearLayout)getLayoutInflater()
                .inflate(R.layout.layout_tip_activity, null);
        if (null == popupWindow || !popupWindow.isShowing()) {
            popupWindow = new PopupWindow(view,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
           /* popupWindow = new PopupWindow(view);
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
      
        tip_tv_content.setText(appHelpData.getContent());
//        finalBitmap.display(selectableRoundedImageView, appHelpData.getImg_url(), defDrawable.getBitmap(), defDrawable.getBitmap());
        finalBitmap.display(tip_iv_icon, appHelpData.getImg_url(), defDrawable.getBitmap(), defDrawable.getBitmap());
        popupWindow.setFocusable(true);  
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.PopupAnimation); //设置 popupWindow动画样式
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
        backgroundAlpha(0.8f);
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
            Intent intent = new Intent(MainPlusActivity.this,WebViewsActivity.class);
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
        WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = bgAlpha; //0.0-1.0
            getWindow().setAttributes(lp);
    }
    /*
     * 帮助接口
     */
    
    private void getAppHelp() {
        String user_id = DBHelper.getUser(this).getId();
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }
        User user = DBHelper.getUser(MainPlusActivity.this);
        Map<String, String> map = new HashMap<String, String>();
        map.put("action","work");
        map.put("user_id",""+user.getId());
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().get(Constants.URL_GET_APP_HELP_DATA, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(MainPlusActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                    UIUtils.showToast(MainPlusActivity.this, errorMsg);
                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        getFindPlusIcon();
    }
}
