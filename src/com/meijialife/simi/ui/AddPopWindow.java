package com.meijialife.simi.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.CompanyListsActivity;
import com.meijialife.simi.activity.WebViewsActivity;


public class AddPopWindow extends PopupWindow {
    private View conentView;  
    private Context contexts;
    public AddPopWindow(final Activity context) {  
        this.contexts = context;
        LayoutInflater inflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        conentView = inflater.inflate(R.layout.add_popup_dialog, null);  
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int h  = dm.widthPixels;
        int w = dm.heightPixels;
        // 设置SelectPicPopupWindow的View  
        this.setContentView(conentView);  
        // 设置SelectPicPopupWindow弹出窗体的宽  
        this.setWidth(w / 5 );  
        // 设置SelectPicPopupWindow弹出窗体的高  
        this.setHeight(LayoutParams.WRAP_CONTENT);  
        // 设置SelectPicPopupWindow弹出窗体可点击  
        this.setFocusable(true);  
        this.setOutsideTouchable(true);  
        // 刷新状态  
        this.update();  
        // 实例化一个ColorDrawable颜色为半透明  
        ColorDrawable dw = new ColorDrawable(0000000000);  
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作  
        this.setBackgroundDrawable(dw);  
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);  
        // 设置SelectPicPopupWindow弹出窗体动画效果  
       /* this.setAnimationStyle(R.style.AnimationPreview);  */
        LinearLayout companyAdd = (LinearLayout) conentView  
                .findViewById(R.id.m_company_add);  
        LinearLayout companyCreate = (LinearLayout) conentView  
                .findViewById(R.id.m_company_create);  
        companyAdd.setOnClickListener(new OnClickListener() {  
  
            @Override  
            public void onClick(View arg0) {  
                Intent intent = new Intent(contexts, WebViewsActivity.class);
                intent.putExtra("url", Constants.HAS_COMPANY);
                contexts.startActivity(intent);
                AddPopWindow.this.dismiss();  
            }  
        });  
  
        companyCreate.setOnClickListener(new OnClickListener() {  
  
            @Override  
            public void onClick(View v) {  
                Intent intent = new Intent(contexts, CompanyListsActivity.class);
                contexts.startActivity(intent);
                AddPopWindow.this.dismiss();  
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
            // 以下拉方式显示popupwindow  
//            this.showAsDropDown(parent, -(parent.getLayoutParams().width / 5), 18);  
            this.showAsDropDown(parent, -20,0, Gravity.RIGHT);
        } else {  
            this.dismiss();  
        }  
    } 

}
