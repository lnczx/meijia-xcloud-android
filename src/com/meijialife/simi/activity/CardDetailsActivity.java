package com.meijialife.simi.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.CardCommentAdapter;
import com.meijialife.simi.adapter.CardZanAdapter;
import com.meijialife.simi.bean.CardAttend;
import com.meijialife.simi.bean.CardComment;
import com.meijialife.simi.bean.CardExtra;
import com.meijialife.simi.bean.Cards;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.ui.CustomShareBoard;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;
import com.simi.easemob.utils.ShareConfig;

/**
 * 卡片详情
 * 
 */
@SuppressLint("ResourceAsColor")
public class CardDetailsActivity extends BaseActivity implements OnClickListener {

    private TextView tv_tips; // 没有评论时的提示
    private ListView listview; // 评论list
    private CardCommentAdapter listAdapter;

    private GridView gridview; // 被赞列表
    private CardZanAdapter gridAdapter;

    private ImageView iv_icon;
    private ImageView iv_image;
    private TextView tv_title;
    private TextView tv_status;// 状态
    private TextView tv_1, tv_2, tv_3;// 中间文字内容
    private TextView tv_remark;// 备注
    private TextView tv_date_str;
    private TextView tv_zan;// 被赞数量
    private TextView tv_share;// 分享

    private EditText et_comment;// 评论输入框
    private Button btn_send; // 评论

    private Cards card;// 卡片数据
    private CardExtra cardExtra;// 卡片数据
    private View layout_mask;
    private TextView tv_tongji_zan;
    private LinearLayout layout_dianzan;
    private RelativeLayout btn_edit_layout;
    private RelativeLayout btn_cancel_layout;
    private TextView tv_edit;
    private TextView tv_cancel;
    
    /** 秘书身份UI **/
    private RelativeLayout sec_layout;//秘书接单最外层layout
    private ImageView sec_icon; //头像
    private TextView sec_title; //昵称
    private TextView sec_text;
    private Button sec_btn_accept;//接单
    private Button sec_btn_complete;//完成
    
    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.card_details_activity);
        super.onCreate(savedInstanceState);

        init();
        initView();

        getCommentList();

    }

    private void init() {
        card = (Cards) getIntent().getSerializableExtra("Cards");
        cardExtra = (CardExtra) getIntent().getSerializableExtra("card_extra");
        user = DBHelper.getUser(this);
        
        finalBitmap = FinalBitmap.create(this);
        defDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_defult_touxiang);
    }

    private void initView() {
        setTitleName(card.getCard_type_name());
        requestBackBtn();

        btn_edit_layout = (RelativeLayout) findViewById(R.id.btn_edit_layout);
        btn_cancel_layout = (RelativeLayout) findViewById(R.id.btn_cancel_layout);

        tv_edit = (TextView) findViewById(R.id.tv_edit);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
       
        
        tv_tips = (TextView) findViewById(R.id.tv_tips);
        listview = (ListView) findViewById(R.id.listview);
        listAdapter = new CardCommentAdapter(this);
        listview.setAdapter(listAdapter);

        gridview = (GridView) findViewById(R.id.gridview);
        gridAdapter = new CardZanAdapter(this);
        gridview.setAdapter(gridAdapter);

        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        iv_image = (ImageView) findViewById(R.id.iv_image);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_1 = (TextView) findViewById(R.id.tv_1);
        tv_2 = (TextView) findViewById(R.id.tv_2);
        tv_3 = (TextView) findViewById(R.id.tv_3);
        tv_remark = (TextView) findViewById(R.id.tv_remark);
        tv_date_str = (TextView) findViewById(R.id.tv_date_str);
        tv_zan = (TextView) findViewById(R.id.tv_zan);
        tv_share = (TextView) findViewById(R.id.tv_share);
        et_comment = (EditText) findViewById(R.id.et_comment);
        btn_send = (Button) findViewById(R.id.btn_send);
        tv_tongji_zan = (TextView) findViewById(R.id.tv_tongji_zan);
        layout_mask = (View) findViewById(R.id.layout_mask);
        layout_dianzan = (LinearLayout) findViewById(R.id.layout_dianzan);

        btn_send.setOnClickListener(this);
        tv_zan.setOnClickListener(this);
        tv_share.setOnClickListener(this);

        tv_1.setVisibility(View.GONE);
        tv_2.setVisibility(View.GONE);
        tv_3.setVisibility(View.GONE);

        btn_edit_layout.setOnClickListener(this);
        btn_cancel_layout.setOnClickListener(this);
        
        if(Integer.valueOf(card.getStatus())==0){
            btn_edit_layout.setClickable(false);
            tv_edit.setClickable(false);
            tv_edit.setTextColor(getResources().getColor(R.color.simi_color_gray));
            btn_cancel_layout.setClickable(false);
            tv_cancel.setClickable(false);
            tv_cancel.setTextColor(getResources().getColor(R.color.simi_color_gray));
        }
        
        sec_layout = (RelativeLayout)findViewById(R.id.sec_layout);
        int userType = Integer.parseInt(user.getUser_type());//用户类型 0 = 普通用户 1= 秘书 2 = 服务商
        if(userType == 1){
            //如果是秘书
            int secDo = Integer.parseInt(card.getSet_sec_do());//秘书处理 0 = 否 1 = 是
            if(secDo == 1){
                //如果需要秘书处理
                sec_icon = (ImageView)findViewById(R.id.sec_icon);
                sec_title = (TextView)findViewById(R.id.sec_title);
                sec_text = (TextView)findViewById(R.id.sec_text);

                sec_btn_accept = (Button)findViewById(R.id.sec_btn_accept);
                sec_btn_complete = (Button)findViewById(R.id.sec_btn_complete);
                sec_btn_accept.setVisibility(View.INVISIBLE);
                sec_btn_complete.setVisibility(View.INVISIBLE);
                
                // 状态 0 = 已取消 1 = 处理中 2 = 秘书处理中 3 = 已完成.
                int status = Integer.parseInt(card.getStatus());
                if (status == 1) {//处理中
                    sec_btn_accept.setVisibility(View.VISIBLE);
                } else if (status == 2) {//秘书处理中
                    sec_btn_complete.setVisibility(View.VISIBLE);
                }
                
                sec_btn_accept.setOnClickListener(this);
                sec_btn_complete.setOnClickListener(this);
            }else{
                sec_layout.setVisibility(View.GONE);
            }
        }else{
            sec_layout.setVisibility(View.GONE);
        }

    }

    @SuppressLint("NewApi")
    private void showData() {
        int size = card.getZan_top10().size();
        if (size > 0) {
            gridAdapter.setData(card.getZan_top10());
            tv_tongji_zan.setText("共" + card.getZan_top10().size() + "人");
            layout_dianzan.setVisibility(View.VISIBLE);
        } else {
            layout_dianzan.setVisibility(View.GONE);
        }

        long timeL = Long.parseLong(card.getService_time());
        String time = new SimpleDateFormat("HH:mm").format(timeL * 1000);
        String date = new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(timeL * 1000);
        String remark = card.getService_content();
        String total_zan = String.valueOf(card.getTotal_zan());
        String timeStr = card.getAdd_time_str();
        
        if(sec_icon != null){
            finalBitmap.display(sec_icon,  card.getUser_head_img(), defDrawable.getBitmap(), defDrawable.getBitmap());
        }
        if(sec_title != null){
            sec_title.setText(card.getUser_name());
        }

        tv_title.setText(time);
        tv_remark.setText(remark);
        tv_date_str.setText(timeStr);
        tv_zan.setText(total_zan);

        ArrayList<CardAttend> attends = card.getAttends();
        String attend = "";// 卡片参与的所有人名
        for (int i = 0; i < attends.size(); i++) {
             if(StringUtils.isEmpty(attends.get(i).getName())){
                 attend += attends.get(i).getMobile();
             }else {
                 attend += attends.get(i).getName();
            }
            if (i != attends.size() - 1) {
                attend += ",";
            }
        }

        // 状态 0 = 已取消 1 = 处理中 2 = 秘书处理中 3 = 已完成.
        int status = Integer.parseInt(card.getStatus());

        if (status == 0) {
            tv_status.setTextColor(this.getResources().getColor(R.color.simi_color_gray));
            tv_status.setText("已取消");
        } else if (status == 1) {
            tv_status.setTextColor(this.getResources().getColor(R.color.simi_color_red));
            tv_status.setText("处理中");
        } else if (status == 2) {
            tv_status.setTextColor(this.getResources().getColor(R.color.simi_color_red));
            tv_status.setText("秘书处理中");
        } else if (status == 3) {
            tv_status.setTextColor(this.getResources().getColor(R.color.simi_color_red));
            tv_status.setText("已完成");
        }

        // 卡片类型 0 = 通用(保留) 1 = 会议安排 2 = 秘书叫早 3 = 事务提醒 4 = 邀约通知 5 = 差旅规划
        int type = Integer.parseInt(card.getCard_type());
        switch (type) {
        case 0:// 通用(保留)

            break;
        case 1:// 会议安排
            iv_icon.setBackground(getResources().getDrawable(R.drawable.icon_plus_2));
            iv_image.setBackground(getResources().getDrawable(R.drawable.card_default_huiyi));
            tv_1.setText("时间：" + time);
            tv_1.setVisibility(View.VISIBLE);
            tv_2.setText("会议地点：" + card.getService_addr());
            tv_2.setVisibility(View.VISIBLE);
            tv_3.setText("提醒人：" + attend);
            tv_3.setVisibility(View.VISIBLE);
            break;
        case 2:// 通知公告
            iv_icon.setBackground(getResources().getDrawable(R.drawable.icon_plus_5));
            iv_image.setBackground(getResources().getDrawable(R.drawable.card_default_mishu));
            tv_1.setText("时间：" + date);
            tv_1.setVisibility(View.VISIBLE);
            tv_2.setText("接收人：" + attend);
            tv_2.setVisibility(View.VISIBLE);
            break;
        case 3:// 事务提醒
            iv_icon.setBackground(getResources().getDrawable(R.drawable.icon_plus_3));
            iv_image.setBackground(getResources().getDrawable(R.drawable.card_default_shiwu));
            tv_1.setText("时间：" + date);
            tv_1.setVisibility(View.VISIBLE);
            tv_2.setText("提醒人：" + attend);
            tv_2.setVisibility(View.VISIBLE);
            break;
        case 4:// 面试邀约
            iv_icon.setBackground(getResources().getDrawable(R.drawable.icon_plus_4));
            iv_image.setBackground(getResources().getDrawable(R.drawable.card_default_yaoyue));
            tv_1.setText("时间：" + date);
            tv_1.setVisibility(View.VISIBLE);
            tv_2.setText("邀约人：" + attend);
            tv_2.setVisibility(View.VISIBLE);
            break;
        case 5:// 差旅规划
            iv_icon.setBackground(getResources().getDrawable(R.drawable.icon_plus_1));
            iv_image.setBackground(getResources().getDrawable(R.drawable.card_default_chailv));
            String ticket_from_city_name = card.getTicket_from_city_name();
            String ticket_to_city_name = card.getTicket_to_city_name();
            tv_1.setText("城市：从 " + ticket_from_city_name + " 到 " + ticket_to_city_name);
            tv_1.setVisibility(View.VISIBLE);
            tv_2.setText("时间：" + date);
            tv_2.setVisibility(View.VISIBLE);
            tv_3.setText("航班：");
            tv_3.setVisibility(View.VISIBLE);
            break;

        default:
            break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_send: // 评论
            postComment();
            break;
        case R.id.sec_btn_accept:  //秘书接单
            postSecDo(2);
            break;
        case R.id.sec_btn_complete:  //秘书完成
            postSecDo(3);
            break;
        case R.id.tv_zan: // 赞
            postZan(card);
            break;
        case R.id.tv_share: // 分享
            ShareConfig.getInstance().init(this,card.getCard_id());;
            postShare();
            break;
        case R.id.btn_cancel_layout: // 取消
            showCancelDlg();
            break;
        case R.id.btn_edit_layout: // 编辑
            showEditDlg();
            break;
            
        default:
            break;
        }
    }
    
    /**
     * 取消对话框
     */
    private void showCancelDlg(){
        if(isExpired()){
            Toast.makeText(CardDetailsActivity.this, "卡片已完成，您操作得太晚喽！", Toast.LENGTH_LONG).show();
            return;
        }
        AlertDialog.Builder dialog;
        dialog = new AlertDialog.Builder(this);
        dialog.setTitle("提示");
        // dialog.setIcon(R.drawable.ic_launcher_logo);
        dialog.setMessage("取消本卡片后，会通知相关人员，请确定是否取消");
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                CancelCardData();
            }
        });
        dialog.show();
    }
    
    /**
     * 编辑对话框
     */
    private void showEditDlg(){
        if(isExpired()){
            Toast.makeText(CardDetailsActivity.this, "卡片已完成，您操作得太晚喽！", Toast.LENGTH_LONG).show();
            return;
        }
        AlertDialog.Builder dialog2;
        dialog2 = new AlertDialog.Builder(this);
        dialog2.setTitle("提示");
        // dialog.setIcon(R.drawable.ic_launcher_logo);
        dialog2.setMessage("修改更新本卡片后，会再次通知相关人员，请确定是否继续修改更新");
        dialog2.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 卡片类型 0 = 通用(保留) 1 = 会议安排 2 = 秘书叫早 3 = 事务提醒 4 = 邀约通知 5 = 差旅规划
                int type = Integer.parseInt(card.getCard_type());
                switch (type) {
                case 0:// 通用(保留)

                    break;
                case 1:// 会议安排
                    Intent intent = new Intent(CardDetailsActivity.this, MainPlusMeettingActivity.class);
                    intent.putExtra("cards", card);
                    startActivity(intent);

                    break;
                case 2://通知公告
                    Intent intent2 = new Intent(CardDetailsActivity.this, MainPlusMorningActivity.class);
                    intent2.putExtra("cards", card);
                    startActivity(intent2);
                    break;
                case 3:// 事务提醒
                    Intent intent3 = new Intent(CardDetailsActivity.this, MainPlusAffairActivity.class);
                    intent3.putExtra("cards", card);
                    startActivity(intent3);
                    break;
                case 4:// 面试邀约
                    Intent intent4 = new Intent(CardDetailsActivity.this, MainPlusNotificationActivity.class);
                    intent4.putExtra("cards", card);
                    startActivity(intent4);
                    break;
                case 5:// 差旅规划
                    Intent intent5 = new Intent(CardDetailsActivity.this, MainPlusTravelActivity.class);
                    intent5.putExtra("cards", card);
                    startActivity(intent5);
                    break;
                }
            }
        });
        dialog2.show();
    }
    
    /**
     * 卡片是否过期
     */
    private boolean isExpired(){
        Date currentTime = new Date();
        Date serviceTime = new Date(Long.parseLong(card.getService_time()) * 1000);

        if(serviceTime.getTime() < currentTime.getTime()){
          //已经过期
            return true;
        }
        
        return false;
    }

    private void postShare() {
        layout_mask.setVisibility(View.VISIBLE);
        CustomShareBoard shareBoard = new CustomShareBoard(this);
        shareBoard.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                layout_mask.setVisibility(View.GONE);
            }
        });
        shareBoard.showAtLocation((this).getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    /**
     * 取消卡片
     * 
     * @param date
     */
    private void CancelCardData() {
        showDialog();
        String user_id = DBHelper.getUser(this).getId();

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id + "");
        map.put("card_id", card.getCard_id());
        map.put("status", "0");
        AjaxParams param = new AjaxParams(map);

        new FinalHttp().post(Constants.URL_GET_CANCEL_CARD, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(CardDetailsActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                            UIUtils.showToast(CardDetailsActivity.this, "取消成功");
                            getCardData();
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
                    UIUtils.showToast(CardDetailsActivity.this, errorMsg);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        getCardData();
    }

    /**
     * 获取卡片详情
     * 
     * @param date
     */
    private void getCardData() {
        showDialog();
        String user_id = DBHelper.getUser(this).getId();

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id + "");
        map.put("card_id", card.getCard_id());
        AjaxParams param = new AjaxParams(map);

        new FinalHttp().get(Constants.URL_GET_CARD_DETAILS, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(CardDetailsActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                card = gson.fromJson(data, Cards.class);
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
                    UIUtils.showToast(CardDetailsActivity.this, errorMsg);
                }
            }
        });

    }

    /**
     * 获取评论列表
     */
    private void getCommentList() {
        showDialog();
        String user_id = DBHelper.getUser(this).getId();

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id + "");
        map.put("card_id", card.getCard_id());
        map.put("page", "0");
        AjaxParams param = new AjaxParams(map);

        new FinalHttp().get(Constants.URL_GET_CARD_COMMENT_LIST, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(CardDetailsActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                ArrayList<CardComment> comments = gson.fromJson(data, new TypeToken<ArrayList<CardComment>>() {
                                }.getType());
                                listAdapter.setData(comments);
                                tv_tips.setVisibility(View.GONE);
                            } else {
                                tv_tips.setVisibility(View.VISIBLE);
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
                    UIUtils.showToast(CardDetailsActivity.this, errorMsg);
                }
            }
        });

    }

    /**
     * 发送评论接口
     */
    private void postComment() {
        String comment = et_comment.getText().toString();
        if (StringUtils.isEmpty(comment.trim())) {
            Toast.makeText(this, "还没有输入评论内容哦~", Toast.LENGTH_SHORT).show();
            return;
        }

        String user_id = DBHelper.getUser(this).getId();

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("card_id", card.getCard_id());
        map.put("user_id", user_id);
        map.put("comment", comment);
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().post(Constants.URL_POST_CARD_COMMENT, param, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                LogOut.debug("错误码：" + errorNo);
                dismissDialog();
                Toast.makeText(CardDetailsActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            et_comment.setText("");
                            //评论成功，收起键盘
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
                            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  
                            getCommentList();
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            Toast.makeText(CardDetailsActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(CardDetailsActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(CardDetailsActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(CardDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CardDetailsActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // UIUtils.showToast(CardDetailsActivity.this, "网络错误,请稍后重试");
                }

            }
        });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 点赞接口
     */
    private void postZan(final Cards card) {

        String user_id = DBHelper.getUser(this).getId();

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("card_id", card.getCard_id());
        map.put("user_id", user_id);
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().post(Constants.URL_POST_CARD_ZAN, param, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                LogOut.debug("错误码：" + errorNo);
                dismissDialog();
                Toast.makeText(CardDetailsActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            getCardData();
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            Toast.makeText(CardDetailsActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(CardDetailsActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(CardDetailsActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(CardDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CardDetailsActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // UIUtils.showToast(context, "网络错误,请稍后重试");
                }

            }
        });
    }
    
    /**
     * 秘书处理卡片接口
     * 
     * @param cardStatus 状态 0 = 已取消 1 = 处理中 2 = 秘书处理中 3 = 已完成.
     */
    private void postSecDo(final int cardStatus) {

        String user_id = DBHelper.getUser(this).getId();

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("card_id", card.getCard_id());
        map.put("sec_id", user_id);
        map.put("status", cardStatus+"");   //状态 0 = 已取消 1 = 处理中 2 = 秘书处理中 3 = 已完成.
        map.put("sec_remarks", "");     //处理内容
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().post(Constants.URL_POST_SEC_DO, param, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                LogOut.debug("错误码：" + errorNo);
                dismissDialog();
                Toast.makeText(CardDetailsActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            
                            if(cardStatus == 2){//秘书处理中
                                sec_btn_accept.setVisibility(View.INVISIBLE);
                                sec_btn_complete.setVisibility(View.VISIBLE);
                            } else if(cardStatus == 3){//已完成
                                sec_btn_accept.setVisibility(View.INVISIBLE);
                                sec_btn_complete.setVisibility(View.INVISIBLE);
                            } 
                            getCardData();
                            
//                            Toast.makeText(CardDetailsActivity.this, "请求成功", Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            Toast.makeText(CardDetailsActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(CardDetailsActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(CardDetailsActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(CardDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CardDetailsActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // UIUtils.showToast(context, "网络错误,请稍后重试");
                }

            }
        });
    }

}
