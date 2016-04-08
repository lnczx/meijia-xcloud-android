package com.meijialife.simi.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager {
    
    int mLastMotionY;  
    int mLastMotionX;  
      
    public MyViewPager(Context context) {  
        super(context);  
    }  
      
    public MyViewPager(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
      
    //拦截 TouchEvent  
    @Override  
    public boolean onInterceptTouchEvent(MotionEvent arg0) {  
        // TODO Auto-generated method stub  
        return super.onInterceptTouchEvent(arg0);  
    }  
      
    //处理 TouchEvent   
    @Override  
    public boolean onTouchEvent(MotionEvent arg0) {  
        // TODO Auto-generated method stub  
        return super.onTouchEvent(arg0);  
    }  
      
  
    //因为这个执行的顺序是  父布局先得到 action_down的事件  
      
    /* 
     * onInterceptTouchEvent(MotionEvent ev)方法，这个方法只有ViewGroup类有 
     * 如LinearLayout，RelativeLayout等    可以包含子View的容器的 
     * 
     * 用来分发 TouchEvent 
     * 此方法    返回true    就交给本 View的 onTouchEvent处理 
     * 此方法    返回false   就交给本View的 onInterceptTouchEvent 处理 
     */  
    @Override  
    public boolean dispatchTouchEvent(MotionEvent ev) {  
          
        //让父类不拦截触摸事件就可以了。  
        this.getParent().requestDisallowInterceptTouchEvent(true);   
        return super.dispatchTouchEvent(ev);  
     
    }  

}
