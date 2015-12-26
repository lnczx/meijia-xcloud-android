package com.meijialife.simi.adapter;

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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.CardDetailsActivity;
import com.meijialife.simi.bean.CardAttend;
import com.meijialife.simi.bean.CardExtra;
import com.meijialife.simi.bean.Cards;
import com.meijialife.simi.bean.WeatherDatas;
import com.meijialife.simi.bean.WeatherIndex;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.fra.Home1Fra;
import com.meijialife.simi.ui.CustomShareBoard;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.simi.easemob.utils.ShareConfig;

/**
 * 首页卡片列表适配器
 */
@SuppressLint("ResourceAsColor")
public class ListAdapter extends BaseAdapter {
    private ArrayList<Cards> list;
    private ArrayList<CardExtra> cardExtrasList;
    private Context context;
    private onCardUpdateListener listener;
    private SimpleDateFormat dateFormat;

    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    
    
    public ListAdapter(Context context, onCardUpdateListener listener) {
        this.context = context;
        list = new ArrayList<Cards>();
        cardExtrasList = new ArrayList<CardExtra>();
        this.listener = listener;
        dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        finalBitmap = FinalBitmap.create(context);
        defDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.ad_loading);
    }
    public void setData(ArrayList<Cards> list,ArrayList<CardExtra> cardExtraList){
        this.list = list;
        this.cardExtrasList = cardExtraList;
        notifyDataSetChanged();
    }
    public void setData(ArrayList<Cards> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == list.size() - 1) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = null;
        ViewHolder vh = null;
        if (convertView == null) {
            v = ininView(position);

            // if (getItemViewType(position) == 0) {
            // v = ininView();
            // } else {
            // v = initLastView();
            // }
        } else {
            v = convertView;
        }
        vh = (ViewHolder) v.getTag();
//        if (getItemViewType(position) == 0) {
            bindView(vh, position);
//        }
        return v;
    }

    // listview的尾部
    // private View initLastView() {
    // ViewHolder vh = new ViewHolder();
    // View v = LayoutInflater.from(context).inflate(R.layout.item_calendar_notification_last, null);
    // return v;
    // }

    @SuppressLint("NewApi")
	private void bindView(ViewHolder vh, int position) {
        String title = list.get(position).getCard_type_name();
        String timeStr = list.get(position).getAdd_time_str();
        String remark = list.get(position).getService_content();
        long timeL = Long.parseLong(list.get(position).getService_time());
        String date = dateFormat.format(timeL*1000);
        
        vh.tv_title.setText(title);
        vh.tv_date_str.setText(timeStr);
        vh.tv_zan.setText(""+list.get(position).getTotal_zan());
        vh.tv_comment.setText(""+list.get(position).getTotal_comment());
        
        ArrayList<CardAttend> attends = list.get(position).getAttends();
        String attend = "";//卡片参与的所有人名
        if(attends!=null && attends.size()>0){
            for(int i = 0; i < attends.size(); i++){
                if(StringUtils.isEmpty(attends.get(i).getName())){
                    attend += attends.get(i).getMobile();
                }else {
                    attend += attends.get(i).getName();
                }
                if(i != attends.size()-1){
                    attend += ",";
                }
            }
        }
       
        
        //状态 0 = 已取消 1 = 处理中 2 = 秘书处理中 3 = 已完成.
        String statusStr = list.get(position).getStatus();
        if(statusStr!=null && !StringUtils.isEmpty(statusStr)){
        int status = Integer.parseInt(statusStr);
        if (status == 0) {
            vh.tv_status.setTextColor(context.getResources().getColor(R.color.simi_color_gray));
            vh.tv_status.setText("已取消");
        } else if (status == 1) {
            vh.tv_status.setTextColor(context.getResources().getColor(R.color.simi_color_red));
            vh.tv_status.setText("处理中");
        } else if (status == 2) {
            vh.tv_status.setTextColor(context.getResources().getColor(R.color.simi_color_red));
            vh.tv_status.setText("秘书处理中");
        } else if (status == 3) {
            vh.tv_status.setTextColor(context.getResources().getColor(R.color.simi_color_red));
            vh.tv_status.setText("已完成");
        }
        }
        String typeStr = "";
        //卡片类型 0 = 通用(保留) 1 = 会议安排 2 = 秘书叫早 3 = 事务提醒 4 = 邀约通知 5 = 差旅规划 99=天气卡片
        int type = Integer.parseInt(list.get(position).getCard_type());
        switch (type) {
        case 0://通用(保留)
            break;
        case 1://会议安排
            vh.iv_icon.setBackground(context.getResources().getDrawable(R.drawable.icon_plus_2));
            vh.iv_image.setBackground(context.getResources().getDrawable(R.drawable.card_default_huiyi));
            vh.tv_1.setText("时间：" + date);
            vh.tv_1.setVisibility(View.VISIBLE);
            vh.tv_2.setText("会议地点：" + list.get(position).getService_addr());
            vh.tv_2.setVisibility(View.VISIBLE);
            vh.tv_3.setText("提醒人：" + attend);
            vh.tv_3.setVisibility(View.VISIBLE);
            vh.tv_remark.setText(remark);
            vh.ll_weather.setVisibility(View.GONE);
            vh.ll_social.setVisibility(View.VISIBLE);
            vh.iv_default_tep.setVisibility(View.GONE);
            vh.iv_image.setVisibility(View.VISIBLE);
            typeStr = "会议安排";
            break;
        case 2://通知公告
            vh.iv_icon.setBackground(context.getResources().getDrawable(R.drawable.icon_plus_5));
            vh.iv_image.setBackground(context.getResources().getDrawable(R.drawable.card_default_mishu));
            vh.tv_1.setText("时间：" + date);
            vh.tv_1.setVisibility(View.VISIBLE);
            vh.tv_2.setText("接收人：" + attend);
            vh.tv_2.setVisibility(View.VISIBLE);
            vh.tv_3.setVisibility(View.INVISIBLE);
            vh.ll_weather.setVisibility(View.GONE);
            typeStr = "通知公告";
            vh.tv_remark.setText(remark);
            vh.ll_weather.setVisibility(View.GONE);
            vh.ll_social.setVisibility(View.VISIBLE);
            vh.iv_default_tep.setVisibility(View.GONE);
            vh.iv_image.setVisibility(View.VISIBLE);
            break;
        case 3://事务提醒
            vh.iv_icon.setBackground(context.getResources().getDrawable(R.drawable.icon_plus_3));
            vh.iv_image.setBackground(context.getResources().getDrawable(R.drawable.card_default_shiwu));
            vh.tv_1.setText("时间：" + date);
            vh.tv_1.setVisibility(View.VISIBLE);
            vh.tv_2.setText("提醒人：" + attend);
            vh.tv_2.setVisibility(View.VISIBLE);
            vh.tv_3.setVisibility(View.INVISIBLE);
            typeStr = "事务提醒";
            vh.tv_remark.setText(remark);
            vh.ll_weather.setVisibility(View.GONE);
            vh.ll_social.setVisibility(View.VISIBLE);
            vh.iv_default_tep.setVisibility(View.GONE);
            vh.iv_image.setVisibility(View.VISIBLE);
            break;
        case 4://面试邀约
            vh.iv_icon.setBackground(context.getResources().getDrawable(R.drawable.icon_plus_4));
            vh.iv_image.setBackground(context.getResources().getDrawable(R.drawable.card_default_yaoyue));
            vh.tv_1.setText("时间：" + date);
            vh.tv_1.setVisibility(View.VISIBLE);
            vh.tv_2.setText("邀约人：" + attend);
            vh.tv_2.setVisibility(View.VISIBLE);
            vh.tv_3.setVisibility(View.INVISIBLE);
            typeStr = "面试邀约";
            vh.tv_remark.setText(remark);
            vh.ll_weather.setVisibility(View.GONE);
            vh.ll_social.setVisibility(View.VISIBLE);
            vh.iv_default_tep.setVisibility(View.GONE);
            vh.iv_image.setVisibility(View.VISIBLE);
            break;
        case 5://差旅规划
            vh.iv_icon.setBackground(context.getResources().getDrawable(R.drawable.icon_plus_1));
            vh.iv_image.setBackground(context.getResources().getDrawable(R.drawable.card_default_chailv));
            String ticket_from_city_name = list.get(position).getTicket_from_city_name();
            String ticket_to_city_name = list.get(position).getTicket_to_city_name();
            vh.tv_1.setText("城市：从 " + ticket_from_city_name + " 到 " + ticket_to_city_name);
            vh.tv_1.setVisibility(View.VISIBLE);
            vh.tv_2.setText("时间：" + date);
            vh.tv_2.setVisibility(View.VISIBLE);
            vh.tv_3.setText("航班：");
            vh.tv_3.setVisibility(View.VISIBLE);
            typeStr = "差旅规划";
            vh.tv_remark.setText(remark);
            vh.ll_weather.setVisibility(View.GONE);
            vh.ll_social.setVisibility(View.VISIBLE);
            vh.iv_default_tep.setVisibility(View.GONE);
            vh.iv_image.setVisibility(View.VISIBLE);
            break;
        case 99://天气卡片
            vh.ll_weather.setVisibility(View.VISIBLE);
            vh.ll_social.setVisibility(View.GONE);
            vh.iv_default_tep.setVisibility(View.VISIBLE);
            vh.iv_image.setVisibility(View.GONE);
            vh.iv_icon.setBackground(context.getResources().getDrawable(R.drawable.iconfont_yunbaodan));
       
            //天气卡片额外信息
            ArrayList<WeatherDatas> weatherDatasList = cardExtrasList.get(position).getWeatherDatas();
            WeatherIndex weatherIndex = cardExtrasList.get(position).getWeatherIndex();
          
            vh.tv_status.setTextColor(context.getResources().getColor(R.color.simi_color_red));
            vh.tv_status.setText(cardExtrasList.get(position).getCityName());   
            if (null != weatherIndex) {
                vh.tv_remark.setText(weatherIndex.getDes());
            }
            if(weatherDatasList!=null && weatherDatasList.size()>0){
                WeatherDatas weatherDatas1 =weatherDatasList.get(0);
                WeatherDatas weatherDatas2 =weatherDatasList.get(1);
                WeatherDatas weatherDatas3 =weatherDatasList.get(2);
                WeatherDatas weatherDatas4 =weatherDatasList.get(3);
                
                vh.tv_1.setText("温度："+weatherDatas1.getTemperature() );
                vh.tv_1.setVisibility(View.VISIBLE);
                vh.tv_2.setText("风力：" + weatherDatas1.getWind());
                vh.tv_2.setVisibility(View.VISIBLE);
                vh.tv_3.setText("天气："+weatherDatas1.getWeather());
                vh.tv_3.setVisibility(View.VISIBLE);
                vh.iv_default_tep.setText(cardExtrasList.get(position).getReal_temp());
              
                vh.tv_weather1.setText(weatherDatas2.getDate());
                vh.tv_weather2.setText(weatherDatas3.getDate());
                vh.tv_weather3.setText(weatherDatas4.getDate());
                
                String url2="";
                String url3="";
                String url4="";
                Date currentTime = new Date(System.currentTimeMillis());//获取当前时间  );
                boolean  flag = DateUtils.isDayOrNight(currentTime);
                if(flag){
                    url2=weatherDatas2.getDayPictureUrl();
                    url3=weatherDatas3.getDayPictureUrl();
                    url4=weatherDatas4.getDayPictureUrl();
                }else {
                    url2=weatherDatas2.getNightPictureUrl();
                    url3=weatherDatas3.getNightPictureUrl();
                    url4=weatherDatas4.getNightPictureUrl();
                }
                finalBitmap.display(vh.iv_weather1,url2,defDrawable.getBitmap(),defDrawable.getBitmap());
                finalBitmap.display(vh.iv_weather2,url3,defDrawable.getBitmap(),defDrawable.getBitmap());
                finalBitmap.display(vh.iv_weather3,url4,defDrawable.getBitmap(),defDrawable.getBitmap());
                vh.tv_temp1.setText(weatherDatas2.getTemperature());
                vh.tv_temp2.setText(weatherDatas3.getTemperature());
                vh.tv_temp3.setText(weatherDatas4.getTemperature());
                
                typeStr = "天气预报";
            }
         
            break;

        default:
            break;
        }
        
    }

    private View ininView(final int position) {
        ViewHolder vh = new ViewHolder();
        View v = LayoutInflater.from(context).inflate(R.layout.item_home_cardlist, null);
        vh.iv_icon = (ImageView) v.findViewById(R.id.iv_icon);
        vh.iv_image = (ImageView) v.findViewById(R.id.iv_image);
        vh.tv_title = (TextView) v.findViewById(R.id.tv_title);
        vh.tv_date_str = (TextView) v.findViewById(R.id.tv_date_str);
        vh.tv_status = (TextView) v.findViewById(R.id.tv_status);
        vh.rl_status = (RelativeLayout) v.findViewById(R.id.rl_status);
        vh.tv_1 = (TextView) v.findViewById(R.id.tv_1);
        vh.tv_2 = (TextView) v.findViewById(R.id.tv_2);
        vh.tv_3 = (TextView) v.findViewById(R.id.tv_3);
        vh.tv_remark = (TextView) v.findViewById(R.id.tv_remark);
        vh.tv_zan = (TextView) v.findViewById(R.id.tv_zan);
        vh.tv_comment = (TextView) v.findViewById(R.id.tv_comment);
        vh.tv_share = (TextView) v.findViewById(R.id.tv_share);
        vh.ll_social = (LinearLayout)v.findViewById(R.id.ll_social);
        vh.ll_weather = (LinearLayout)v.findViewById(R.id.ll_weather);
        vh.tv_weather1 =(TextView)v.findViewById(R.id.tv_weather1);
        vh.tv_weather2 =(TextView)v.findViewById(R.id.tv_weather2);
        vh.tv_weather3 =(TextView)v.findViewById(R.id.tv_weather3);
        vh.iv_weather1 =(ImageView)v.findViewById(R.id.iv_weather1);
        vh.iv_weather2 =(ImageView)v.findViewById(R.id.iv_weather2);
        vh.iv_weather3 =(ImageView)v.findViewById(R.id.iv_weather3);
        vh.tv_temp1 =(TextView)v.findViewById(R.id.tv_temp1);
        vh.tv_temp2 =(TextView)v.findViewById(R.id.tv_temp2);
        vh.tv_temp3 =(TextView)v.findViewById(R.id.tv_temp3);
        vh.iv_default_tep = (TextView)v.findViewById(R.id.iv_default_tep);
        
        //赞
        vh.tv_zan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                postZan(list.get(position));
            }
        });
        //评论
        vh.tv_comment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CardDetailsActivity.class);
                intent.putExtra("Cards", list.get(position));
                context.startActivity(intent);
            }
        });
        //分享
        vh.tv_share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                context.startActivity(new Intent(context, ShareActivity.class));
//                ShareConfig.getInstance(list.get(position).getCard_id()).init((Activity)context);;
                ShareConfig.getInstance().init((Activity)context,list.get(position).getCard_id());;
                postShare();
            }
        });
        
        vh.tv_1.setVisibility(View.GONE);
        vh.tv_2.setVisibility(View.GONE);
        vh.tv_3.setVisibility(View.GONE);
        
        v.setTag(vh);
        return v;
    }

    class ViewHolder {
        private ImageView iv_icon;
        private ImageView iv_image;
        private TextView tv_title;
        private TextView tv_date_str;
        private TextView tv_status;
        private RelativeLayout rl_status;//
        private TextView tv_1;
        private TextView tv_2;
        private TextView tv_3;
        private TextView tv_remark; // 备注
        private TextView tv_zan;    // 被赞数量
        private TextView tv_comment;// 评论数量
        private TextView tv_share;// 分享
        private LinearLayout ll_social;//底部分享
        private LinearLayout ll_weather;//底部天气
        private TextView tv_weather1;
        private ImageView iv_weather1;
        private TextView tv_temp1;
        private TextView tv_weather2;
        private ImageView iv_weather2;
        private TextView tv_temp2;
        private TextView tv_weather3;
        private ImageView iv_weather3;
        private TextView tv_temp3;
        private TextView iv_default_tep;//显示默认温度

    }
    
    private void postShare() {
        Home1Fra.showMask();
        CustomShareBoard shareBoard = new CustomShareBoard((Activity) context);
        shareBoard.setOnDismissListener(new OnDismissListener() {
            
            @Override
            public void onDismiss() {
                Home1Fra.GoneMask(); 
            }
        });
        shareBoard.showAtLocation(((Activity) context).getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    
    /**
     * 点赞接口
     */
    private void postZan(final Cards card) {
        
        String user_id = DBHelper.getUser(context).getId();

        if (!NetworkUtils.isNetworkConnected(context)) {
            Toast.makeText(context, context.getString(R.string.net_not_open), 0).show();
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
                            listener.onCardUpdate();
                            
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
//                    UIUtils.showToast(context, "网络错误,请稍后重试");
                }

            }
        });

    }
    
    public interface onCardUpdateListener{
        
        /**
         * 卡片数据有变动时用来数据显示
         */
        public void onCardUpdate();
        
    }
    
    private ProgressDialog m_pDialog;
    public void showDialog() {
        if(m_pDialog == null){
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