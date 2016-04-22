package com.meijialife.simi.ui;

import net.tsz.afinal.FinalBitmap;
import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.bean.AppHelpData;

public class PopWindowHelp extends PopupWindow {
    
    private View view;//
    
    private Activity context;//
    
    private PopupWindow popupWindow;
    private TextView mDone;
    private ImageView tip_iv_icon;
    private TextView tip_tv_title;
    private TextView tip_tv_content;
    private TextView tip_tv_more;
    private BitmapDrawable defDrawable;
    private FinalBitmap finalBitmap;

    public PopWindowHelp(Activity context,final AppHelpData appHelpData,View v) {
        super();
        this.context= context;
        view = context.getLayoutInflater()
                .inflate(R.layout.layout_tip_activity, null);
        if (null == popupWindow || !popupWindow.isShowing()) {
            popupWindow = new PopupWindow(view);
            popupWindow.setWidth(450);
            popupWindow.setHeight(650);
            popupWindow.setFocusable(false);
            popupWindow.setTouchable(true);
        }
        mDone = (TextView)view.findViewById(R.id.tip_tv_done);
        tip_tv_title = (TextView)view.findViewById(R.id.tip_tv_title);
        tip_tv_content = (TextView)view.findViewById(R.id.tip_tv_content);
        tip_tv_more = (TextView)view.findViewById(R.id.tip_tv_more);
        tip_iv_icon = (ImageView)view.findViewById(R.id.tip_iv_icon);
      
        tip_tv_content.setText(appHelpData.getContent());
        finalBitmap.display(tip_iv_icon, appHelpData.getImg_url(), defDrawable.getBitmap(), defDrawable.getBitmap());
        
        popupWindow.setAnimationStyle(R.style.PopupAnimation); //设置 popupWindow动画样式
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
        backgroundAlpha(0.5f);
        mDone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != popupWindow && popupWindow.isShowing()) {
                    backgroundAlpha(1f);
                    popupWindow.dismiss();
                }
            }
        });
    }

    
    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha
     */
     public void backgroundAlpha(float bgAlpha)
     {
         WindowManager.LayoutParams lp = context.getWindow().getAttributes();
             lp.alpha = bgAlpha; //0.0-1.0
             context.getWindow().setAttributes(lp);
     }
    
}
