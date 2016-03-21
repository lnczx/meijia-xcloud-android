package com.meijialife.simi.ui;

import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.WebViewsActivity;
import com.meijialife.simi.bean.AppHelpData;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;


public class TipPopWindow extends PopupWindow {
    private View conentView;  
    private Context contexts;
    
    private TextView mDone;
    private ImageView tip_iv_icon;
    private TextView tip_tv_title;
    private TextView tip_tv_content;
    private TextView tip_tv_more;
    private BitmapDrawable defDrawable;
    private FinalBitmap finalBitmap;
    
    public TipPopWindow(final Activity context,final AppHelpData appHelpData,final String action) { 
        
        this.contexts = context;
        //加载popWidow布局文件
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        conentView = inflater.inflate(R.layout.layout_tip_activity, null);  
        finalBitmap = FinalBitmap.create(context);
        defDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ad_loading);
        
        //获取屏幕尺寸，设置Popwidow尺寸大小
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int h  = dm.widthPixels;
        int w = dm.heightPixels;
        this.setContentView(conentView);  
        this.setWidth(LayoutParams.WRAP_CONTENT);  
        this.setHeight(LayoutParams.WRAP_CONTENT); 
        
        //获得对应的控件
        mDone = (TextView)conentView.findViewById(R.id.tip_tv_done);
        tip_tv_title = (TextView)conentView.findViewById(R.id.tip_tv_title);
        tip_tv_content = (TextView)conentView.findViewById(R.id.tip_tv_content);
        tip_tv_more = (TextView)conentView.findViewById(R.id.tip_tv_more);
        tip_iv_icon = (ImageView)conentView.findViewById(R.id.tip_iv_icon);
     
        //为控件赋值
        tip_tv_title.setText(appHelpData.getTitle());
        tip_tv_content.setText(appHelpData.getContent());
        finalBitmap.display(tip_iv_icon, appHelpData.getImg_url(), defDrawable.getBitmap(), defDrawable.getBitmap());
      
        //设置动画和点击范围
        this.setAnimationStyle(R.style.PopupAnimation); //设置 popupWindow动画样式
        this.setFocusable(true);  
        this.setOutsideTouchable(true);
        mDone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != this && TipPopWindow.this.isShowing()) {
                    TipPopWindow.this.dismiss();
                    backgroundAlpha(1f);
                    postHelp(action);
                }
            }
        });
       tip_tv_more.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
            String goto_url = appHelpData.getGoto_url();
            Intent intent = new Intent(contexts,WebViewsActivity.class);
            intent.putExtra("url",goto_url);
            contexts.startActivity(intent);
            backgroundAlpha(1f);
            TipPopWindow.this.dismiss();
            postHelp(action);
          
        }});  
    }
    /** 
     * 显示popupWindow 
     *  
     * @param parent 
     */  
    @SuppressLint("NewApi")
	public void showPopupWindow(View parent) {  
        if (!this.isShowing()) {  
            backgroundAlpha(0.5f);
            showAtLocation(parent, Gravity.CENTER_HORIZONTAL|Gravity.CENTER,20,30);
        } else {  
            this.dismiss();  
        }  
    } 
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = ((Activity) contexts).getWindow().getAttributes();
            lp.alpha = bgAlpha; //0.0-1.0
            ((Activity) contexts).getWindow().setAttributes(lp);
    }
    /**
     * 帮助-帮助点击记录接口
     * @param action
     */
    private void postHelp(String action) {
        String user_id = DBHelper.getUser(contexts).getId();
        if (!NetworkUtils.isNetworkConnected(contexts)) {
            Toast.makeText(contexts, contexts.getString(R.string.net_not_open), 0).show();
            return;
        }
        User user = DBHelper.getUser(contexts);
        Map<String, String> map = new HashMap<String, String>();
        map.put("action",action);
        map.put("user_id",""+user.getId());
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().post(Constants.URL_POST_HELP, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(contexts, contexts.getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            errorMsg = contexts.getString(R.string.servers_error);
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            errorMsg = contexts.getString(R.string.param_missing);
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            errorMsg = contexts.getString(R.string.param_illegal);
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            errorMsg = msg;
                        } else {
                            errorMsg = contexts.getString(R.string.servers_error);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = contexts.getString(R.string.servers_error);
                }
                // 操作失败，显示错误信息
                if(!StringUtils.isEmpty(errorMsg.trim())){
                    UIUtils.showToast(contexts, errorMsg);
                }
            }
        });
    }
}
