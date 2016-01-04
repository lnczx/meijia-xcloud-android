package com.meijialife.simi.adapter;

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
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.DynamicDetailsActivity;
import com.meijialife.simi.bean.DynamicImaData;
import com.meijialife.simi.bean.FriendDynamicData;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.fra.Home3Fra;
import com.meijialife.simi.publish.NineGridlayout;
import com.meijialife.simi.ui.CustomShareBoard;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.simi.easemob.utils.ShareConfig;

/**
 * 首页卡片列表适配器
 */
@SuppressLint("ResourceAsColor")
public class FriendDynamicAdapter extends BaseAdapter {
    private ArrayList<FriendDynamicData> friendDynamicDatas;
    private ArrayList<DynamicImaData> dynamicImaDatas;
    
    private Context context;
    private onDynamicUpdateListener listener;

    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    private DynamicImgAdapter dynamicImgAdapter;


    public FriendDynamicAdapter(Context context, onDynamicUpdateListener listener) {
        this.context = context;
        this.friendDynamicDatas = new ArrayList<FriendDynamicData>();
        dynamicImaDatas = new ArrayList<DynamicImaData>();
        this.listener = listener;
        finalBitmap = FinalBitmap.create(context);
        defDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ad_loading);
    }

    public FriendDynamicAdapter(Context context) {
        this.context = context;
        friendDynamicDatas = new ArrayList<FriendDynamicData>();
        dynamicImaDatas = new ArrayList<DynamicImaData>();
        finalBitmap = FinalBitmap.create(context);
        defDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ad_loading);
    }

    public void setData(ArrayList<FriendDynamicData> list) {
        this.friendDynamicDatas = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return friendDynamicDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return friendDynamicDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_friend_dynamic, null);
            vh = new ViewHolder();
            vh.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            vh.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            vh.tv_date_str = (TextView) convertView.findViewById(R.id.tv_date_str);
            vh.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
            vh.tv_remark = (TextView) convertView.findViewById(R.id.tv_remark);

            vh.tv_zan = (TextView) convertView.findViewById(R.id.tv_zan);
            vh.tv_comment = (TextView)convertView.findViewById(R.id.tv_comment);
            vh.tv_share = (TextView) convertView.findViewById(R.id.tv_share);
            vh.iv_more = (NineGridlayout)convertView.findViewById(R.id.iv_more);
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();
            vh.iv_more.setDefaultWidth(width*3/5);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        bindView(vh, position);
        return convertView;
    }

    @SuppressLint("NewApi")
    private void bindView(ViewHolder vh, final int position) {

        dynamicImaDatas = friendDynamicDatas.get(position).getFeed_imgs();
        vh.tv_name.setText(friendDynamicDatas.get(position).getName());
        finalBitmap.display(vh.iv_icon,friendDynamicDatas.get(position).getHead_img(),defDrawable.getBitmap(), defDrawable.getBitmap());
        vh.tv_remark.setText(friendDynamicDatas.get(position).getTitle());
        vh.tv_date_str.setText(friendDynamicDatas.get(position).getAdd_time_str());
        vh.tv_zan.setText("" + friendDynamicDatas.get(position).getTotal_zan());
        vh.tv_comment.setText("" + friendDynamicDatas.get(position).getTotal_comment());
        
        if(dynamicImaDatas!=null && dynamicImaDatas.size()>0){
            vh.iv_more.setVisibility(View.VISIBLE);
            handlerOneImage(vh, dynamicImaDatas,position);
        }else {
            vh.iv_more.setVisibility(View.GONE);
        }
        vh.iv_more.setClickable(false);
        // 赞
        vh.tv_zan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                postZan(friendDynamicDatas.get(position));
            }
        });
        // 评论
        vh.tv_comment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DynamicDetailsActivity.class);
                intent.putExtra("friendDynamic", friendDynamicDatas.get(position));
                context.startActivity(intent);
            }
        });
        // 分享
        vh.tv_share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareConfig.getInstance().init((Activity) context);
                postShare();
            }
        });
    }

    private void handlerOneImage(ViewHolder vh, ArrayList<DynamicImaData> dynamicImaDatas,final int pos) {
        dynamicImgAdapter = new DynamicImgAdapter(context, dynamicImaDatas);
        vh.iv_more.setAdapter(dynamicImgAdapter);
        vh.iv_more.setOnItemClickListerner(new NineGridlayout.OnItemClickListerner() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(context, DynamicDetailsActivity.class);
                intent.putExtra("friendDynamic", friendDynamicDatas.get(pos));
                context.startActivity(intent);
            }
        });
    }
    @SuppressLint("CutPasteId")
	private View ininView(final int position) {
        ViewHolder vh = new ViewHolder();
        View v = LayoutInflater.from(context).inflate(R.layout.item_friend_dynamic, null);
        vh.iv_icon = (ImageView) v.findViewById(R.id.iv_icon);
        vh.tv_name = (TextView) v.findViewById(R.id.tv_name);
        vh.tv_date_str = (TextView) v.findViewById(R.id.tv_date_str);
        vh.iv_image = (ImageView) v.findViewById(R.id.iv_image);
        vh.tv_remark = (TextView) v.findViewById(R.id.tv_remark);

        vh.tv_zan = (TextView) v.findViewById(R.id.tv_zan);
        vh.tv_comment = (TextView) v.findViewById(R.id.tv_comment);
        vh.tv_share = (TextView) v.findViewById(R.id.tv_share);
        vh.iv_more = (NineGridlayout)v.findViewById(R.id.iv_more);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        vh.iv_more.setDefaultWidth(width*3/5);
        // 赞
        vh.tv_zan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                postZan(friendDynamicDatas.get(position));
            }
        });
        // 评论
        vh.tv_comment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DynamicDetailsActivity.class);
                intent.putExtra("friendDynamic", friendDynamicDatas.get(position));
                context.startActivity(intent);
            }
        });
        // 分享
        vh.tv_share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareConfig.getInstance().init((Activity) context);
                postShare();
            }
        });

        v.setTag(vh);
        return v;
    }

    class ViewHolder {
        private ImageView iv_icon;
        private ImageView iv_image;// 动态--图片
        private TextView tv_name;// 动态--名字
        private TextView tv_date_str;

        private TextView tv_remark; // 动态内容
        private TextView tv_zan; // 被赞数量
        private TextView tv_comment;// 评论数量
        private TextView tv_share;// 分享
        private NineGridlayout iv_more;//
        
    }

    
  public interface onDynamicUpdateListener{
        /**
         * 朋友动态数据有变动时用来数据显示
         */
        public void onDynamicUpdate();
        
    }
    private void postShare() {
        Home3Fra.showMask();
        CustomShareBoard shareBoard = new CustomShareBoard((Activity) context);
        shareBoard.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                Home3Fra.GoneMask();
            }
        });
        shareBoard.showAtLocation(((Activity) context).getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    /**
     * 点赞接口
     */
    private void postZan(final FriendDynamicData friendDynamicData) {

        String user_id = DBHelper.getUser(context).getId();

        if (!NetworkUtils.isNetworkConnected(context)) {
            Toast.makeText(context, context.getString(R.string.net_not_open), 0).show();
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
                Toast.makeText(context, context.getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                            listener.onDynamicUpdate();
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            Toast.makeText(context, context.getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(context, context.getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(context, context.getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, context.getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // UIUtils.showToast(context, "网络错误,请稍后重试");
                }

            }
        });

    }

    private ProgressDialog m_pDialog;

    public void showDialog() {
        if (m_pDialog == null) {
            m_pDialog = new ProgressDialog(context);
            m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            m_pDialog.setMessage("请稍等...");
            m_pDialog.setIndeterminate(false);
            m_pDialog.setCancelable(true);
        }
        m_pDialog.show();
    }

    public void dismissDialog() {
        if (m_pDialog != null && m_pDialog.isShowing()) {
            m_pDialog.hide();
        }
    }
}