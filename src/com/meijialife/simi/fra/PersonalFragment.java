package com.meijialife.simi.fra;

import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.meijialife.simi.Constants;
import com.meijialife.simi.MainActivity;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.AccountInfoActivity;
import com.meijialife.simi.activity.ApplicationsCenterActivity;
import com.meijialife.simi.activity.DiscountCardActivity;
import com.meijialife.simi.activity.MoreActivity;
import com.meijialife.simi.activity.MyOrderActivity;
import com.meijialife.simi.activity.MyWalletActivity;
import com.meijialife.simi.activity.PointsShopActivity;
import com.meijialife.simi.activity.WebViewsActivity;
import com.meijialife.simi.bean.UserIndexData;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.ui.CustomShareBoard;
import com.meijialife.simi.ui.RoundImageView;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;
import com.simi.easemob.utils.ShareConfig;

/**
 * @description：
 * @author： kerryg
 * @date:2015年11月23日
 */
@SuppressLint("InlinedApi")
public class PersonalFragment extends Fragment implements OnClickListener {

    private RoundImageView iv_top_head;

    private TextView tv_top_nickname;// 昵称
    private TextView tv_city; // 城市
    private TextView tv_distance; // 距离
    private TextView tv_money_num; // 钱包余额
    private TextView tv_coupon_num; // 优惠券数量
    private TextView tv_score_num; // 积分数量

    public static UserIndexData user;

    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;

    private ImageButton ibtn_rq;// 右侧显示个人二维码信息

    private View v;
    private PopupWindow mPopupWindow;
    private View music_popunwindwow;
    private LinearLayout ll_rq;
    private ImageView iv_rq_left;
    private LayoutInflater layoutInflater;
    private static View layout_mask;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutInflater = inflater;
        v = inflater.inflate(R.layout.personal_fragment, null, false);
        init(inflater, v);

        return v;
    }

    private void init(LayoutInflater inflater, View view) {
        finalBitmap = FinalBitmap.create(getActivity());
        defDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_defult_touxiang);

        iv_top_head = (RoundImageView) view.findViewById(R.id.iv_top_head);

        layout_mask = v.findViewById(R.id.layout_mask);
        tv_top_nickname = (TextView) view.findViewById(R.id.tv_top_nickname);
        tv_city = (TextView) view.findViewById(R.id.tv_city);
        tv_distance = (TextView) view.findViewById(R.id.tv_distance);
        tv_money_num = (TextView) view.findViewById(R.id.tv_card_num);
        tv_coupon_num = (TextView) view.findViewById(R.id.tv_coupon_num);
        tv_score_num = (TextView) view.findViewById(R.id.tv_friend_num);
        view.findViewById(R.id.item_qianbao).setOnClickListener(this);
        view.findViewById(R.id.item_youhui).setOnClickListener(this);
        view.findViewById(R.id.item_jifen).setOnClickListener(this);

        ibtn_rq = (ImageButton) view.findViewById(R.id.ibtn_rq);
        ibtn_rq.setVisibility(View.VISIBLE);
        music_popunwindwow = inflater.inflate(R.layout.mine_rq_layout, null);
        ll_rq = (LinearLayout) music_popunwindwow.findViewById(R.id.ll_rq);
        iv_rq_left = (ImageView) music_popunwindwow.findViewById(R.id.iv_rq_left);

        // 为每一栏增加点击事件
        view.findViewById(R.id.rl_person_items1).setOnClickListener(this);
        view.findViewById(R.id.rl_person_items2).setOnClickListener(this);
        view.findViewById(R.id.rl_person_items3).setOnClickListener(this);
        view.findViewById(R.id.rl_person_items4).setOnClickListener(this);
        view.findViewById(R.id.rl_person_items5).setOnClickListener(this);
        // 为每种类别增加点击事件
        view.findViewById(R.id.rl_person_wallet).setOnClickListener(this);
        view.findViewById(R.id.rl_person_coupon).setOnClickListener(this);
        view.findViewById(R.id.rl_person_order).setOnClickListener(this);
        view.findViewById(R.id.rl_person_score).setOnClickListener(this);
        view.findViewById(R.id.rl_person_shop).setOnClickListener(this);
        view.findViewById(R.id.rl_person_attest).setOnClickListener(this);
        view.findViewById(R.id.rl_person_money).setOnClickListener(this);
        view.findViewById(R.id.rl_person_train).setOnClickListener(this);

        /**
         * 沉浸式状态栏(像ios那样的状态栏与应用统一颜色样式)android.4.4支持
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); // 透明状态栏
//            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);// 透明导航栏
        }
        
        ll_rq.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closePopWindow();
            }
        });
        ibtn_rq.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popRqCode();
            }
        });
        iv_rq_left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closePopWindow();
            }
        });

        iv_top_head.setOnClickListener(this);
        ImageButton ibtn_message = (ImageButton) view.findViewById(R.id.ibtn_message);
        ImageButton ibtn_person = (ImageButton) view.findViewById(R.id.ibtn_person);
        ibtn_message.setOnClickListener(this);
        ibtn_person.setOnClickListener(this);

        // inflater = LayoutInflater.from(getActivity());

    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); // 透明状态栏
//            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);// 透明导航栏
        }

        getUserData();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        dismissDialog();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View arg0) {
        Intent intent = null;
        switch (arg0.getId()) {
        case R.id.iv_top_head:
            intent = new Intent(getActivity(), AccountInfoActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            break;
        case R.id.ibtn_person:
            MainActivity.slideMenu();
            break;
        case R.id.item_qianbao:// 钱包
            startActivity(new Intent(getActivity(), MyWalletActivity.class));
            break;
        case R.id.item_youhui:// 优惠券
            startActivity(new Intent(getActivity(), DiscountCardActivity.class));
            break;
        case R.id.item_jifen:// 积分
//            startActivity(new Intent(getActivity(), PointsActivity.class));
            Intent intent6 = new Intent();
            intent6.setClass(getActivity(), PointsShopActivity.class);
            intent6.putExtra("navColor", "#E8374A"); // 配置导航条的背景颜色，请用#ffffff长格式。
            intent6.putExtra("titleColor", "#ffffff"); // 配置导航条标题的颜色，请用#ffffff长格式。
            intent6.putExtra("url", Constants.URL_POST_SCORE_SHOP + "?user_id=" + DBHelper.getUserInfo(getActivity()).getUser_id()); // 配置自动登陆地址，每次需服务端动态生成。
            startActivity(intent6);
            break;
        case R.id.rl_person_items1:// 工具箱--更多
             startActivity(new Intent(getActivity(),ApplicationsCenterActivity.class));
            break;
        case R.id.rl_person_items2:// 我的成长--LV
            break;
        case R.id.rl_person_items3:// 推荐给好友
//            startActivity(new Intent(getActivity(), ShareActivity.class));
//          context.startActivity(new Intent(context, ShareActivity.class));
            ShareConfig.getInstance().init(getActivity());;
            postShare();
            break;
        case R.id.rl_person_items4:// 更多
            startActivity(new Intent(getActivity(), MoreActivity.class));
            break;
        case R.id.rl_person_items5:// 我是商家
            intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "4001691615"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            break;
        case R.id.rl_person_wallet:// 钱包
            startActivity(new Intent(getActivity(), MyWalletActivity.class));
            break;
        case R.id.rl_person_coupon:// 优惠券
            startActivity(new Intent(getActivity(), DiscountCardActivity.class));
            break;
        case R.id.rl_person_order:// 订单
            startActivity(new Intent(getActivity(), MyOrderActivity.class));
            break;
        case R.id.rl_person_score:// 积分商城
            Intent intent1 = new Intent();
            intent1.setClass(getActivity(), PointsShopActivity.class);
            intent1.putExtra("navColor", "#E8374A"); // 配置导航条的背景颜色，请用#ffffff长格式。
            intent1.putExtra("titleColor", "#ffffff"); // 配置导航条标题的颜色，请用#ffffff长格式。
            intent1.putExtra("url", Constants.URL_POST_SCORE_SHOP + "?user_id=" + DBHelper.getUserInfo(getActivity()).getUser_id()); // 配置自动登陆地址，每次需服务端动态生成。
            startActivity(intent1);
            break;
        case R.id.rl_person_shop:// 知识库
//            popWebView(Constants.SHOP_URL);
            intent = new Intent(getActivity(),WebViewsActivity.class);
            intent.putExtra("url", Constants.SHOP_URL);
            startActivity(intent);
            break;
        case R.id.rl_person_attest:// 认证考试
//            popWebView(Constants.ATTEST_URL);
            intent = new Intent(getActivity(),WebViewsActivity.class);
            intent.putExtra("url", Constants.ATTEST_URL);
            startActivity(intent);
            break;
        case R.id.rl_person_money:// 兼职赚钱
//            popWebView(Constants.MONEY_URL);
            intent = new Intent(getActivity(),WebViewsActivity.class);
            intent.putExtra("url", Constants.MONEY_URL);
            startActivity(intent);
            break;
        case R.id.rl_person_train:// 培训
//            popWebView(Constants.TRAIN_URL);
            intent = new Intent(getActivity(),WebViewsActivity.class);
            intent.putExtra("url", Constants.TRAIN_URL);
            startActivity(intent);
            break;
        default:
            break;
        }
    }
    private void postShare() {
        PersonalFragment.showMask();
        CustomShareBoard shareBoard = new CustomShareBoard(getActivity());
        shareBoard.setOnDismissListener(new OnDismissListener() {
            
            @Override
            public void onDismiss() {
                PersonalFragment.GoneMask(); 
            }
        });
        shareBoard.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    public static void showMask() {
        layout_mask.setVisibility(View.VISIBLE);
    }

    public static void GoneMask() {
        layout_mask.setVisibility(View.GONE);
    }

    
    
    private PopupWindow popupWindow;
    private WebView webview;
    private ImageView iv_person_left;
    private TextView tv_person_title;
    private ImageView iv_person_close;

    /**
     * 弹出显示webview H5页面
     * 
     * @param url
     */
    @SuppressLint("JavascriptInterface")
    private void popWebView(String url) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService("layout_inflater");
        View popupWindow_view = layoutInflater.inflate(R.layout.webview_personal_fragment, null);
        View view = inflater.inflate(R.layout.personal_fragment, null);

        if (null == popupWindow || !popupWindow.isShowing()) {
            popupWindow = new PopupWindow(popupWindow_view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
            popupWindow.setFocusable(false);
        }
        if (StringUtils.isEmpty(url)) {
            Toast.makeText(getActivity(), "数据错误", 0).show();
            return;
        }
        iv_person_left = (ImageView) popupWindow_view.findViewById(R.id.iv_person_left);
        iv_person_close = (ImageView) popupWindow_view.findViewById(R.id.iv_person_close);
        tv_person_title = (TextView) popupWindow_view.findViewById(R.id.tv_person_title);
        webview = (WebView) popupWindow_view.findViewById(R.id.webview);

        WebChromeClient wvcc = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                tv_person_title.setText(title);
            }

        };
        // 设置WebChromeClinent对象
        webview.setWebChromeClient(wvcc);
        webview.loadUrl(url);
        WebSettings webSettings = webview.getSettings();
        webview.addJavascriptInterface(this, "Koolearn");
        webview.setBackgroundColor(Color.parseColor("#00000000"));
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);// 设置js可以直接打开窗口，如window.open()，默认为false
        webview.getSettings().setJavaScriptEnabled(true);// 是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        webview.getSettings().setSupportZoom(true);// 是否可以缩放，默认true
        // popwindow显示webview不能设置缩放按钮，否则触屏就会报错。
        // webview.getSettings().setBuiltInZoomControls(true);// 是否显示缩放按钮，默认false
        webview.getSettings().setUseWideViewPort(true);// 设置此属性，可任意比例缩放。大视图模式
        webview.getSettings().setLoadWithOverviewMode(true);// 和setUseWideViewPort(true)一起解决网页自适应问题
        webview.getSettings().setAppCacheEnabled(false);// 是否使用缓存
        webview.getSettings().setDomStorageEnabled(true);// DOM Storage
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        popupWindow.showAtLocation(v, Gravity.LEFT, 0, 0);
        iv_person_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != popupWindow && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
        iv_person_left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webview != null && webview.canGoBack()) {
                    webview.goBack();
                } else {
                    if (null != popupWindow && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                }
            }
        });
    }

    private void popRqCode() {
        if (null == mPopupWindow || !mPopupWindow.isShowing()) {
            mPopupWindow = new PopupWindow(music_popunwindwow, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            mPopupWindow.showAtLocation(v.findViewById(R.id.fl_main), Gravity.RIGHT | Gravity.BOTTOM, 0, 0);
            getMyRqCode();
        }
    }

    private void closePopWindow() {
        if (null != mPopupWindow && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    /**
     * 获取个人信息数据
     */
    private void getUserData() {
        String user_id = DBHelper.getUser(getActivity()).getId();

        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id + "");
        map.put("view_user_id", user_id + "");
        AjaxParams param = new AjaxParams(map);

        showDialog();
        new FinalHttp().get(Constants.URL_GET_USER_INDEX, param, new AjaxCallBack<Object>() {
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
                                user = gson.fromJson(data, UserIndexData.class);
                                showData();
                            } else {
                                // UIUtils.showToast(getActivity(), "数据错误");
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

    private void showData() {
        if (user == null) {
            return;
        }

        String nickName = user.getName();
        if (StringUtils.isEmpty(nickName.trim())) {
            nickName = user.getMobile();
        }

        UserInfo userInfo = DBHelper.getUserInfo(getActivity());

        tv_top_nickname.setText(nickName);
        tv_city.setText(user.getProvince_name());
        if (StringUtils.isNotEmpty(user.getPoi_distance())) {
            tv_distance.setText("距离你" + user.getPoi_distance() + "米");
        }
        tv_money_num.setText("￥" + userInfo.getRest_money() + "");
        tv_coupon_num.setText(user.getTotal_coupon() + "");
        tv_score_num.setText(userInfo.getScore() + "");

        finalBitmap.display(iv_top_head, user.getHead_img(), defDrawable.getBitmap(), defDrawable.getBitmap());

    }

    /**
     * 获取我的二维码名片
     */
    private void getMyRqCode() {

        String user_id = DBHelper.getUser(getActivity()).getId();

        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id + "");
        AjaxParams param = new AjaxParams(map);

        showDialog();
        new FinalHttp().get(Constants.URL_GET_MY_RQ_CODE, param, new AjaxCallBack<Object>() {
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
                                String rq_url = data;
                                finalBitmap.display(music_popunwindwow.findViewById(R.id.iv_rq_code), rq_url, defDrawable.getBitmap(),
                                        defDrawable.getBitmap());
                            } else {
                                Toast.makeText(getActivity(), "您的二维码还没有生存", Toast.LENGTH_SHORT).show();
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

    private ProgressDialog m_pDialog;

    public void showDialog() {
        if (m_pDialog == null) {
            m_pDialog = new ProgressDialog(getActivity());
            m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            m_pDialog.setMessage("请稍等...");
            m_pDialog.setIndeterminate(false);
            m_pDialog.setCancelable(false);
        }
        m_pDialog.show();
    }

    public void dismissDialog() {
        if (m_pDialog != null && m_pDialog.isShowing()) {
            // m_pDialog.hide();
            m_pDialog.dismiss();
            m_pDialog = null;
        }
    }

}
