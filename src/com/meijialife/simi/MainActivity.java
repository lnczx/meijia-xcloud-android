package com.meijialife.simi;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMMessage;
import com.easemob.util.EMLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.activity.AccountInfoActivity;
import com.meijialife.simi.activity.AddressActivity;
import com.meijialife.simi.activity.DiscountCardActivity;
import com.meijialife.simi.activity.LoginActivity;
import com.meijialife.simi.activity.MainPlusActivity;
import com.meijialife.simi.activity.MoreActivity;
import com.meijialife.simi.activity.MyOrderActivity;
import com.meijialife.simi.activity.MyWalletActivity;
import com.meijialife.simi.activity.PointsShopActivity;
import com.meijialife.simi.activity.ShareActivity;
import com.meijialife.simi.activity.WebViewActivity;
import com.meijialife.simi.alerm.AlermUtils;
import com.meijialife.simi.bean.CalendarMark;
import com.meijialife.simi.bean.Contact;
import com.meijialife.simi.bean.Remind;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.fra.Find2Fra;
import com.meijialife.simi.fra.Home1Fra;
import com.meijialife.simi.fra.Home3Fra;
import com.meijialife.simi.fra.PersonalFragment;
import com.meijialife.simi.fra.PersonalPageFragment;
import com.meijialife.simi.ui.RoundImageView;
import com.meijialife.simi.ui.SlideMenu;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.GetContactsRunnable;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.SpFileUtil;
import com.meijialife.simi.utils.StringUtils;
import com.simi.easemob.EMConstant;
import com.simi.easemob.EMDemoHelper;
import com.simi.easemob.ui.ConversationListFragment;
import com.simi.easemob.ui.EMBaseActivity;
import com.simi.easemob.ui.EMLoginActivity;
import com.simi.easemob.utils.ShareConfig;

/**
 * fragment 的切换类
 * 
 * @author RUI
 * 
 */
public class MainActivity extends EMBaseActivity implements OnClickListener, EMEventListener {

    protected static final String TAG = "MainActivity";

    private TextView tv_header;
    private LinearLayout mBt1, mBt2, mBt3, mBt4, mBt5;
    private FragmentManager mFM = null;
    private int currentTabIndex; // 1=首页 2=发现 3=秘友 4=我的

    private static SlideMenu slideMenu;// 侧边栏
    private RoundImageView left_menu_header_im;// 侧边栏用户头像
    private TextView tv_user_name;// 侧边栏用户昵称
    private RelativeLayout item_0, item_1, item_2, item_3, item_4, item_5, item_6, item_7, item_8;// 侧边栏内控件

    public static Activity activity;

    // =========================以下环信相关内容==========================
    private TextView unreadLabel;// 未读消息textview
    public boolean isConflict = false;// 账号在别处登录
    private boolean isCurrentAccountRemoved = false;// 账号被移除
    private android.app.AlertDialog.Builder conflictBuilder;
    private android.app.AlertDialog.Builder accountRemovedBuilder;
    private boolean isConflictDialogShow;
    private boolean isAccountRemovedDialogShow;
    private BroadcastReceiver internalDebugReceiver;
    private ConversationListFragment conversationListFragment;// 会话列表Fragment
    private BroadcastReceiver broadcastReceiver;
    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    GetContactsRunnable getContactsRunnable;

    /**
     * 检查当前用户是否被删除
     */
    public boolean getCurrentAccountRemoved() {
        return isCurrentAccountRemoved;
    }

    // =========================环信结束==========================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.main);
        super.onCreate(savedInstanceState);
         

        init();
        initLeft();
        mBt1.performClick();

        initIM(savedInstanceState);

        // 分享页面初始化
        ShareConfig.getInstance().init(this);

        getReminds();

        layout_mask = findViewById(R.id.layout_mask);
        layout_guide = findViewById(R.id.layout_guide);

        boolean fristFlag = getFristFlag();
        if (fristFlag) {
            layout_mask.setVisibility(View.VISIBLE);
            layout_guide.setVisibility(View.VISIBLE);
        }
        layout_guide.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                layout_mask.setVisibility(View.GONE);
                layout_guide.setVisibility(View.GONE);
                updateFristFlag();
            }
        });
        
      //更新手机联系人数据库
        getContactsRunnable = new GetContactsRunnable(this, null);
        List<Contact> contactList = DBHelper.getContacts(this);
        if(contactList==null){
            new Thread(getContactsRunnable).start();
        }
        /**
         * 手机联系人注册内容监听器--当
         */
        ContactDBObserver observer = new ContactDBObserver(new Handler());
        getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, observer);
        
    }
    
    class ContactDBObserver extends ContentObserver{
        public ContactDBObserver(Handler handler) {
            super(handler);
        }
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            new Thread(getContactsRunnable).start();
        }
    }
    /**
     * 检查是否第一次打开应用
     * 
     * @return
     */
    private boolean getFristFlag() {

        boolean FristFlag = SpFileUtil.getBoolean(this, SpFileUtil.FILE_UI_PARAMETER, SpFileUtil.KEY_FIRST_INTO, true);

        return FristFlag;
    }

    /**
     * 更新第一次打开应用的标识
     */
    private void updateFristFlag() {
        SpFileUtil.saveBoolean(this, SpFileUtil.FILE_UI_PARAMETER, SpFileUtil.KEY_FIRST_INTO, false);

    }

    /**
     * 初始化环信
     */
    private void initIM(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getBoolean(EMConstant.ACCOUNT_REMOVED, false)) {
            // 防止被移除后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
            // 三个fragment里加的判断同理
            EMDemoHelper.getInstance().logout(true, null);
            finish();
            startActivity(new Intent(this, EMLoginActivity.class));
            return;
        } else if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
            // 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
            // 三个fragment里加的判断同理
            finish();
            startActivity(new Intent(this, EMLoginActivity.class));
            return;
        }

        if (getIntent().getBooleanExtra(EMConstant.ACCOUNT_CONFLICT, false) && !isConflictDialogShow) {
            showConflictDialog();
        } else if (getIntent().getBooleanExtra(EMConstant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
            showAccountRemovedDialog();
        }

        conversationListFragment = new ConversationListFragment(this);
        // 添加显示第一个fragment
        // getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, conversationListFragment)
        // .show(conversationListFragment)
        // .commit();
    }

    private void init() {
        activity = this;
        view_title_bar = (RelativeLayout) findViewById(R.id.view_title_bar);
        tv_header = (TextView) findViewById(R.id.header_tv_name);
        mBt1 = (LinearLayout) findViewById(R.id.tab_bt_1);
        mBt2 = (LinearLayout) findViewById(R.id.tab_bt_2);
        mBt3 = (LinearLayout) findViewById(R.id.tab_bt_3);
        mBt4 = (LinearLayout) findViewById(R.id.tab_bt_4);
        mBt5 = (LinearLayout) findViewById(R.id.tab_bt_5);
        unreadLabel = (TextView) findViewById(R.id.unread_msg_number);

        mBt1.setOnClickListener(this);
        mBt2.setOnClickListener(this);
        mBt3.setOnClickListener(this);
        mBt4.setOnClickListener(this);
        mBt5.setOnClickListener(this);

    }

    /**
     * 初始化左侧边栏控件
     */
    private void initLeft() {
        finalBitmap = FinalBitmap.create(this);
        defDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_defult_touxiang);

        slideMenu = (SlideMenu) findViewById(R.id.slide_menu);
        left_menu_header_im = (RoundImageView) findViewById(R.id.left_menu_header_im);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);

        item_0 = (RelativeLayout) findViewById(R.id.item_0);
        item_1 = (RelativeLayout) findViewById(R.id.item_1);
        item_2 = (RelativeLayout) findViewById(R.id.item_2);
        item_3 = (RelativeLayout) findViewById(R.id.item_3);
        item_4 = (RelativeLayout) findViewById(R.id.item_4);
        item_5 = (RelativeLayout) findViewById(R.id.item_5);
        item_6 = (RelativeLayout) findViewById(R.id.item_6);
        item_7 = (RelativeLayout) findViewById(R.id.item_7);
        item_8 = (RelativeLayout) findViewById(R.id.item_8);

        item_0.setOnClickListener(this);
        item_1.setOnClickListener(this);
        item_2.setOnClickListener(this);
        item_3.setOnClickListener(this);
        item_4.setOnClickListener(this);
        item_5.setOnClickListener(this);
        item_6.setOnClickListener(this);
        item_7.setOnClickListener(this);
        item_8.setOnClickListener(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

    }

    @Override
    public void onClick(View arg0) {
        Intent intent;
        switch (arg0.getId()) {
        case R.id.tab_bt_1: // 首页
            currentTabIndex = 1;
            if (!slideMenu.isMainScreenShowing()) {
                return;
            }
            change(new Home1Fra());
            setSelected(mBt1);
            updateTitle(1);
            slideMenu.isUse = false;
            view_title_bar.setVisibility(View.GONE);
            break;
        case R.id.tab_bt_2: // 发现
            currentTabIndex = 2;
            if (!slideMenu.isMainScreenShowing()) {
                return;
            }
            view_title_bar.setVisibility(View.GONE);
            //change(new Home2Fra());
            change(new Find2Fra(this));
            setSelected(mBt2);
            updateTitle(2);
            slideMenu.isUse = false;
            break;
        case R.id.tab_bt_3: // 圈子
            currentTabIndex = 3;
            change2Contacts();
            break;
        case R.id.tab_bt_4: // 我的
            currentTabIndex = 4;
            change(new PersonalFragment());
//            change(new PersonalPageFragment());
            setSelected(mBt4);
            // updateTitle(4);
            slideMenu.isUse = true;
            view_title_bar.setVisibility(View.GONE);
            break;
        case R.id.tab_bt_5: // 加号
            Intent intent2 = new Intent(this, MainPlusActivity.class);
            this.startActivity(intent2);
            this.overridePendingTransition(R.anim.activity_open, 0);
            break;
        case R.id.item_0: // 个人信息
            intent = new Intent(this, AccountInfoActivity.class);
            intent.putExtra("user", PersonalPageFragment.user);
            startActivity(intent);
            break;
        case R.id.item_1: // 我的钱包
            startActivity(new Intent(this, MyWalletActivity.class));
            break;
        case R.id.item_2: // 优惠卡券
            startActivity(new Intent(this, DiscountCardActivity.class));
            break;
        case R.id.item_3: // 我的订单
            startActivity(new Intent(this, MyOrderActivity.class));
            break;
        case R.id.item_4: // 常用地址
            startActivity(new Intent(this, AddressActivity.class));
            break;
        case R.id.item_5: // 卡牌集市
            // Toast.makeText(this, "敬请期待", 0).show();
            // startActivity(new Intent(this, NullWaitActivity.class));
            Intent intent5 = new Intent(this, WebViewActivity.class);
            intent5.putExtra("url", Constants.URL_XUEYUAN);
            intent5.putExtra("title", "行政人智库");
            startActivity(intent5);
            break;
        case R.id.item_6: // 积分商城
//            startActivity(new Intent(this, PointsActivity.class));

//            Intent intent6 = new Intent(this, WebViewActivity.class);
//            intent6.putExtra("url", Constants.URL_POST_SCORE_SHOP+"?user_id="+DBHelper.getUserInfo(MainActivity.this).getUser_id());
//            intent6.putExtra("title", "积分兑换");
//            startActivity(intent6);
            
            
            Intent intent6 = new Intent();
            intent6.setClass(MainActivity.this, PointsShopActivity.class);
            intent6.putExtra("navColor", "#E8374A");    //配置导航条的背景颜色，请用#ffffff长格式。
            intent6.putExtra("titleColor", "#ffffff");    //配置导航条标题的颜色，请用#ffffff长格式。
            intent6.putExtra("url", Constants.URL_POST_SCORE_SHOP+"?user_id="+DBHelper.getUserInfo(MainActivity.this).getUser_id());    //配置自动登陆地址，每次需服务端动态生成。
            startActivity(intent6);
            
            break;
        case R.id.item_7: // 推荐有奖
            startActivity(new Intent(this, ShareActivity.class));
            break;
        case R.id.item_8: // 更多
            startActivity(new Intent(this, MoreActivity.class));
            break;
        default:
            break;
        }
    }

    /**
     * 切换到消息Fragment
     */
    public void change2IM() {
        if (!slideMenu.isMainScreenShowing()) {
            return;
        }
        view_title_bar.setVisibility(View.VISIBLE);
        conversationListFragment = new ConversationListFragment(this);
        change(conversationListFragment);
        setSelected(mBt3);
        updateTitle(3);
        slideMenu.isUse = false;
    }

    /**
     * 切换到秘友Fragment
     */
    public void change2Contacts() {
        if (!slideMenu.isMainScreenShowing()) {
            return;
        }
        view_title_bar.setVisibility(View.VISIBLE);
        change(new Home3Fra(this));
        setSelected(mBt3);
        updateTitle(3);
        slideMenu.isUse = false;
    }

    /**
     * 切换fragement
     */
    private void change(Fragment fragment) {
        if (null == mFM)
            mFM = getSupportFragmentManager();
        FragmentTransaction ft = mFM.beginTransaction();
        ft.replace(R.id.content_container, fragment);
        ft.commit();
    }

    private void setSelected(LinearLayout btn) {
        mBt1.setSelected(false);
        mBt2.setSelected(false);
        mBt3.setSelected(false);
        mBt4.setSelected(false);

        btn.setSelected(true);
    }

    /**
     * 更新标题内容
     */
    private void updateTitle(int index) {
        switch (index) {
        case 1:
            tv_header.setText("首页");
            break;
        case 2:
            tv_header.setText("发现");
            break;
        case 3:
            tv_header.setText("圈子");
            break;
        case 4:
            tv_header.setText("我的");
            break;

        default:
            break;
        }
    }

    /**
     * 打开或关闭侧边栏
     */
    public static void slideMenu() {
        if (slideMenu.isMainScreenShowing()) {
            slideMenu.openMenu();
        } else {
            slideMenu.closeMenu();
        }
    }

    private static Boolean isQuit = false;
    private Timer timer = new Timer();
    private RelativeLayout view_title_bar;

    private View layout_mask;

    private View layout_guide;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (!slideMenu.isMainScreenShowing()) {
                slideMenu.closeMenu();
                return false;
            }

            if (isQuit == false) {
                isQuit = true;
                Toast.makeText(getBaseContext(), "再次点击确定退出软件", Toast.LENGTH_SHORT).show();
                TimerTask task = null;
                task = new TimerTask() {
                    @Override
                    public void run() {
                        isQuit = false;
                    }
                };
                timer.schedule(task, 2000);
            } else {
                moveTaskToBack(false);
                finish();
            }
        } else {
        }
        return false;
    }

    // =========================以下全部是环信相关内容==========================

    /**
     * 监听事件
     */
    @Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
        case EventNewMessage: // 普通消息
        {
            EMMessage message = (EMMessage) event.getData();

            // 提示新消息
            EMDemoHelper.getInstance().getNotifier().onNewMsg(message);

            refreshUIWithMessage();
            break;
        }

        case EventOfflineMessage: {
            refreshUIWithMessage();
            break;
        }

        case EventConversationListChanged: {
            refreshUIWithMessage();
            break;
        }

        default:
            break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isConflict && !isCurrentAccountRemoved) {
            updateUnreadLabel();
        }

        // unregister this event listener when this activity enters the
        // background
        EMDemoHelper sdkHelper = EMDemoHelper.getInstance();
        sdkHelper.pushActivity(this);

        // register the event listener when enter the foreground
        EMChatManager.getInstance().registerEventListener(
                this,
                new EMNotifierEvent.Event[] { EMNotifierEvent.Event.EventNewMessage, EMNotifierEvent.Event.EventOfflineMessage,
                        EMNotifierEvent.Event.EventConversationListChanged });

        UserInfo userInfo = DBHelper.getInstance(this).getUserInfo(this);
        if (null != userInfo) {
            String head_img = userInfo.getHead_img();
            String name = userInfo.getName();
            tv_user_name.setText(name);
            finalBitmap.display(left_menu_header_im, head_img, defDrawable.getBitmap(), defDrawable.getBitmap());
        }
    }

    @Override
    protected void onStop() {
        EMChatManager.getInstance().unregisterEventListener(this);
        EMDemoHelper sdkHelper = EMDemoHelper.getInstance();
        sdkHelper.popActivity(this);

        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isConflict", isConflict);
        outState.putBoolean(EMConstant.ACCOUNT_REMOVED, isCurrentAccountRemoved);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra(EMConstant.ACCOUNT_CONFLICT, false) && !isConflictDialogShow) {
            showConflictDialog();
        } else if (intent.getBooleanExtra(EMConstant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
            showAccountRemovedDialog();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (conflictBuilder != null) {
            conflictBuilder.create().dismiss();
            conflictBuilder = null;
        }

        try {
            unregisterReceiver(internalDebugReceiver);
        } catch (Exception e) {
        }
    }

    private void refreshUIWithMessage() {
        runOnUiThread(new Runnable() {
            public void run() {
                // 刷新bottom bar消息未读数
                updateUnreadLabel();
                if (currentTabIndex == 3) {
                    // 当前页面如果为聊天历史页面，刷新此页面
                    if (conversationListFragment != null) {
                        conversationListFragment.refresh();
                    }
                }
            }
        });
    }

    /**
     * 刷新未读消息数
     */
    public void updateUnreadLabel() {
        int count = getUnreadMsgCountTotal();
        if (count > 0) {
            unreadLabel.setText(String.valueOf(count));
            unreadLabel.setVisibility(View.VISIBLE);
        } else {
            unreadLabel.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 获取未读消息数
     * 
     * @return
     */
    public int getUnreadMsgCountTotal() {
        int unreadMsgCountTotal = 0;
        int chatroomUnreadMsgCount = 0;
        unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
        for (EMConversation conversation : EMChatManager.getInstance().getAllConversations().values()) {
            if (conversation.getType() == EMConversationType.ChatRoom)
                chatroomUnreadMsgCount = chatroomUnreadMsgCount + conversation.getUnreadMsgCount();
        }
        return unreadMsgCountTotal - chatroomUnreadMsgCount;
    }

    /**
     * 显示帐号在别处登录dialog
     */
    private void showConflictDialog() {
        isConflictDialogShow = true;
        EMDemoHelper.getInstance().logout(false, null);
        logOut();

        String st = getResources().getString(R.string.Logoff_notification);
        if (!MainActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (conflictBuilder == null)
                    conflictBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
                conflictBuilder.setTitle(st);
                conflictBuilder.setMessage(R.string.connect_conflict);
                conflictBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        conflictBuilder = null;

                    }
                });
                conflictBuilder.setCancelable(false);
                conflictBuilder.create().show();
                isConflict = true;
            } catch (Exception e) {
                EMLog.e(TAG, "---------color conflictBuilder error" + e.getMessage());
            }

        }

    }

    /**
     * 帐号被移除的dialog
     */
    private void showAccountRemovedDialog() {
        isAccountRemovedDialogShow = true;
        EMDemoHelper.getInstance().logout(true, null);
        String st5 = getResources().getString(R.string.Remove_the_notification);
        if (!MainActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (accountRemovedBuilder == null)
                    accountRemovedBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
                accountRemovedBuilder.setTitle(st5);
                accountRemovedBuilder.setMessage(R.string.em_user_remove);
                accountRemovedBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        accountRemovedBuilder = null;

                        logOut();
                    }
                });
                accountRemovedBuilder.setCancelable(false);
                accountRemovedBuilder.create().show();
                isCurrentAccountRemoved = true;
            } catch (Exception e) {
                EMLog.e(TAG, "---------color userRemovedBuilder error" + e.getMessage());
            }

        }

    }

    /**
     * 退出登陆
     */
    private void logOut() {
        DBHelper.getInstance(MainActivity.this).deleteAll(User.class);
        DBHelper.getInstance(MainActivity.this).deleteAll(UserInfo.class);
        DBHelper.getInstance(MainActivity.this).deleteAll(CalendarMark.class);
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        this.finish();
    }

    /**
     * 获取提醒闹钟的卡片列表
     * 
     */
    public void getReminds() {

        String user_id = DBHelper.getUser(this).getId();

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id + "");
        AjaxParams param = new AjaxParams(map);

        // showDialog();
        new FinalHttp().get(Constants.URL_GET_REMINDS, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                // dismissDialog();
                // Toast.makeText(MainActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                // dismissDialog();
                LogOut.i("========", "onSuccess For getReminds：" + t);
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            if (StringUtils.isNotEmpty(data)) {
                                Gson gson = new Gson();
                                ArrayList<Remind> reminds = gson.fromJson(data, new TypeToken<ArrayList<Remind>>() {
                                }.getType());

                                for (int i = 0; i < reminds.size(); i++) {
                                    resetAlerm(reminds.get(i));
                                }
                            } else {

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
                    // UIUtils.showToast(MainActivity.this, errorMsg);
                }
            }
        });
    }

    /**
     * 重新注册提醒闹钟，避免被硬件重启杀掉
     */
    private void resetAlerm(Remind remind) {
        long remindTime = Long.parseLong(remind.getRemind_time());
        Date mdate = new Date(remindTime);

        // if(isExpired(mdate, remindAlerm)){
        // //如果提醒时间已过，则不处理
        // return;
        // }

        // 初始化本地提醒闹钟
        AlermUtils.initAlerm(MainActivity.this, mdate, remind.getCard_type_name(), remind.getRemind_content());

    }

    /**
     * 该卡片提醒是否过期
     * 
     * @param date
     *            卡片服务时间
     * @param remindAlerm
     *            提醒设置 0 = 不提醒 1 = 按时提醒 2 = 5分钟 3 = 15分钟 4 = 提前30分钟 5 = 提前一个小时 6 = 提前2小时 7 = 提前6小时 8 = 提前一天 9 = 提前两天
     * @return
     */
    private boolean isExpired(Date date, int remindAlerm) {
        if (date == null) {
            return true;
        }

        Date now = new Date();
        Date remindDate = null;// 提醒时间

        switch (remindAlerm) {
        case 0:// 不提醒
            break;
        case 1:// 按时提醒
            remindDate = date;
            break;
        case 2:// 5分钟
            remindDate = DateUtils.getDate5(date);
            break;
        case 3:// 15分钟
            remindDate = DateUtils.getDate15(date);
            break;
        case 4:// 提前30分钟
            remindDate = DateUtils.getDate30(date);
            break;
        case 5:// 提前一个小时
            remindDate = DateUtils.getDate1(date);
            break;
        case 6:// 提前2小时
            remindDate = DateUtils.getDate2(date);
            break;
        case 7:// 提前6小时
            remindDate = DateUtils.getDate6(date);
            break;
        case 8:// 提前一天
            remindDate = DateUtils.getDate1d(date);
            break;
        case 9:// 提前两天
            remindDate = DateUtils.getDate2d(date);
            break;

        default:
            break;
        }

        if (remindDate == null) {
            return true;
        }

        // LogOut.i("======", "now  "+now.getTime());
        // LogOut.i("======", "rem  "+remindDate.getTime());

        if (now.getTime() > remindDate.getTime()) {
            // 已经过时
            return true;
        } else {
            // 当前还在提醒时间之前
            return false;
        }
    }

}
