package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.CardCommentAdapter;
import com.meijialife.simi.adapter.DynamicImgAdapter;
import com.meijialife.simi.adapter.DynamicZanAdapter;
import com.meijialife.simi.bean.CardComment;
import com.meijialife.simi.bean.DynamicImaData;
import com.meijialife.simi.bean.FriendDynamicData;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.publish.NineGridlayout;
import com.meijialife.simi.ui.CustomShareBoard;
import com.meijialife.simi.ui.RoundImageView;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;
import com.simi.easemob.utils.ShareConfig;

/**
 * @description：好友动态详情
 * @author： kerryg
 * @date:2015年12月24日
 */
@SuppressLint("ResourceAsColor")
public class DynamicDetailsActivity extends BaseActivity implements OnClickListener {

    // 评论控件声明
    private TextView tv_tips; // 没有评论时的提示
    private ListView listview; // 评论list
    private CardCommentAdapter listAdapter;
    private EditText et_comment;// 评论输入框
    private Button btn_send; // 评论

    // 赞控件声明
    private GridView gridview; // 被赞列表
    private DynamicZanAdapter gridAdapter;
    private TextView tv_zan;// 被赞数量
    private TextView tv_tongji_zan;
    private LinearLayout layout_dianzan;

    // 分享控件声明
    private TextView tv_share;// 分享
    private View layout_mask;

    // 动态控件声明
    private TextView tv_content;// 动态文字内容
    private TextView tv_name;// 名称
    private TextView tv_time;// 发表时间
    private RoundImageView iv_icon;// 用户头像
    private NineGridlayout iv_more;// 动态图片控件

    private FriendDynamicData friendDynamic;// 好友动态实体
    private ArrayList<DynamicImaData> dynamicImaDatas;// 动态图片列表
    private DynamicImgAdapter dynamicImgAdapter;// 动态图片适配器

    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dynamic_details_activity);
        super.onCreate(savedInstanceState);

        init();
        initView();
        // getDynamicDetail();
        getCommentList();

    }

    private void init() {
        friendDynamic = (FriendDynamicData) getIntent().getSerializableExtra("friendDynamic");
        finalBitmap = FinalBitmap.create(this);
        defDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_defult_touxiang);
    }

    private void initView() {
        setTitleName("");
        requestBackBtn();

        // 评论
        tv_tips = (TextView) findViewById(R.id.tv_tips);
        listview = (ListView) findViewById(R.id.listview);
        listAdapter = new CardCommentAdapter(this);
        listview.setAdapter(listAdapter);

        // 赞
        gridview = (GridView) findViewById(R.id.gridview);
        gridAdapter = new DynamicZanAdapter(this);
        gridview.setAdapter(gridAdapter);

        // 动态=内容+名字+时间+图片
        tv_content = (TextView) findViewById(R.id.tv_content);
        iv_more = (NineGridlayout) findViewById(R.id.iv_more);
        dynamicImaDatas = friendDynamic.getFeed_imgs();
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_time = (TextView) findViewById(R.id.tv_time);
        iv_icon = (RoundImageView) findViewById(R.id.iv_icon);

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

    }

    @SuppressLint("NewApi")
    private void showData() {
        int size = friendDynamic.getZan_top10().size();
        if (size > 0) {
            gridAdapter.setData(friendDynamic.getZan_top10());
            tv_tongji_zan.setText("共" + friendDynamic.getZan_top10().size() + "人");
            layout_dianzan.setVisibility(View.VISIBLE);
        } else {
            layout_dianzan.setVisibility(View.GONE);
        }
        String total_zan = String.valueOf(friendDynamic.getTotal_zan().intValue());
        tv_zan.setText(total_zan);
        tv_content.setText(friendDynamic.getTitle());
        tv_name.setText(friendDynamic.getName());
        tv_time.setText(friendDynamic.getAdd_time_str());
        finalBitmap.display(iv_icon, friendDynamic.getHead_img(), defDrawable.getBitmap(), defDrawable.getBitmap());

        if (dynamicImaDatas != null && dynamicImaDatas.size() > 0) {
            iv_more.setVisibility(View.VISIBLE);
            handlerOneImage(dynamicImaDatas);
        } else {
            iv_more.setVisibility(View.GONE);
        }
    }

    private void handlerOneImage(ArrayList<DynamicImaData> dynamicImaDatas) {
        dynamicImgAdapter = new DynamicImgAdapter(this, dynamicImaDatas);
        iv_more.setAdapter(dynamicImgAdapter);
        /*
         * viewHolder.ivMore.setOnItemClickListerner(new NineGridlayout.OnItemClickListerner() {
         * 
         * @Override public void onItemClick(View view, int position) { //do some thing L.d("onItemClick : " + position); } });
         */
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_send: // 评论
            postComment();
            break;
        case R.id.tv_zan: // 赞
            postZan(friendDynamic);
            break;
        case R.id.tv_share: // 分享
            ShareConfig.getInstance().init(this);
            ;
            postShare();
            break;
        default:
            break;
        }
    }

    /**
     * 动态分享
     */
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

    @Override
    protected void onResume() {
        super.onResume();
        getDynamicDetail();
    }

    /**
     * 获取动态详情
     * 
     * @param date
     */
    private void getDynamicDetail() {
        String user_id = DBHelper.getUser(this).getId();

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("fid", friendDynamic.getFid());
        map.put("user_id", user_id);
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().get(Constants.URL_GET_DYNAMIC_DETAIL, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(DynamicDetailsActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                friendDynamic = gson.fromJson(data, FriendDynamicData.class);
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
                    UIUtils.showToast(DynamicDetailsActivity.this, errorMsg);
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
        map.put("fid", friendDynamic.getFid());
        map.put("page", "0");
        AjaxParams param = new AjaxParams(map);

        new FinalHttp().get(Constants.URL_GET_DYNAMIC_COMMENT_LIST, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(DynamicDetailsActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                    UIUtils.showToast(DynamicDetailsActivity.this, errorMsg);
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
        map.put("fid", friendDynamic.getFid());
        map.put("user_id", user_id);
        map.put("comment", comment);
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().post(Constants.URL_POST_DYNAMIC_COMMENT, param, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                LogOut.debug("错误码：" + errorNo);
                dismissDialog();
                Toast.makeText(DynamicDetailsActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                            // 评论成功，收起键盘
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                            getCommentList();
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            Toast.makeText(DynamicDetailsActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(DynamicDetailsActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(DynamicDetailsActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(DynamicDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DynamicDetailsActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
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
    private void postZan(final FriendDynamicData friendDynamicData) {

        String user_id = DBHelper.getUser(this).getId();

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("fid", friendDynamicData.getFid());
        map.put("user_id", user_id);
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().post(Constants.URL_POST_FEED_ZAN, param, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                LogOut.debug("错误码：" + errorNo);
                dismissDialog();
                Toast.makeText(DynamicDetailsActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                            getDynamicDetail();
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            Toast.makeText(DynamicDetailsActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(DynamicDetailsActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(DynamicDetailsActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(DynamicDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DynamicDetailsActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
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
