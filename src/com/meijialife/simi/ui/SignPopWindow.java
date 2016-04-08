package com.meijialife.simi.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.meijialife.simi.R;


public class SignPopWindow extends PopupWindow {
    private View conentView;  
    private Context contexts;
    
    private LinearLayout mDone;
    private TextView tip_tv_title;
    private TextView tip_tv_content;
    private TextView tip_tv_more;
    
    public SignPopWindow(final Activity context,String msg,String data) { 
        
        this.contexts = context;
        //加载popWidow布局文件
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        conentView = inflater.inflate(R.layout.sign_tip_activity, null);  
        
        //获取屏幕尺寸，设置Popwidow尺寸大小
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int h  = dm.widthPixels;
        int w = dm.heightPixels;
        this.setContentView(conentView);  
        this.setWidth(LayoutParams.WRAP_CONTENT);  
        this.setHeight(LayoutParams.WRAP_CONTENT);
        
        //获得对应的控件
        mDone = (LinearLayout)conentView.findViewById(R.id.ll_status);
        tip_tv_title = (TextView)conentView.findViewById(R.id.tip_tv_title);
        tip_tv_content = (TextView)conentView.findViewById(R.id.tip_tv_content);
     
        //为控件赋值
        tip_tv_title.setText(msg);
        tip_tv_content.setText(data);
      
        //设置动画和点击范围
        this.setAnimationStyle(R.style.PopupAnimation); //设置 popupWindow动画样式
        this.setFocusable(true);  
        this.setOutsideTouchable(true);
        mDone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != this && SignPopWindow.this.isShowing()) {
                    SignPopWindow.this.dismiss();
                    backgroundAlpha(1f);
                }
            }
        });
    }
    /** 
     * 显示popupWindow 
     *  
     * @param parent 
     */  
    @SuppressLint("NewApi")
	public void showPopupWindow(View parent) {  
        if (!this.isShowing()) {  
            backgroundAlpha(0.4f);
            showAtLocation(parent,Gravity.CENTER,0,0);
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
  
}
