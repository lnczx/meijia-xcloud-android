package com.meijialife.simi.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

@SuppressLint("ResourceAsColor")
public class OffcutView extends View {

    private String Text ="";
    private int textSize = 22;
    
  private int radius=20;

  public OffcutView(Context context) {
      super(context);
  }

  public  OffcutView(Context context, AttributeSet attrs) {
      super(context, attrs);
  }

  public OffcutView(Context context, AttributeSet attrs, int defStyle) {
      super(context, attrs, defStyle);
  }
  @SuppressLint("DrawAllocation")
  protected void onDraw(Canvas canvas) {
      if(Text==null)
      {
          return;
      }
      int w = getWidth(), h = getHeight();
      //初始化画笔
      Paint paint = new Paint();//获得画笔
      paint.setAntiAlias(true);//消除锯齿
      paint.setStyle(Paint.Style.FILL);//设置实心
      paint.setColor(Color.argb(255, 255, 0, 0));//设置画笔颜色
//      paint.setColor(Color.argb(255, 69, 112, 234));
      canvas.drawCircle(radius, radius, 10, paint);
      Path path = new Path();
      path.moveTo(radius, 0);//将起始轮廓点移动到x,y坐标点
      path.lineTo(w, 0);//从当前轮廓点绘制一条线段到x,y
      path.lineTo(0, h);
      path.lineTo(0, radius);
      path.close();
      canvas.drawPath(path, paint);
      canvas.save();
      TextPaint textPaint = new TextPaint();
      textPaint.setTextSize(50);
      textPaint.setTypeface(Typeface.DEFAULT_BOLD);
      textPaint.setColor(Color.argb(255, 255, 255, 255));
      textPaint.setAntiAlias(true);
      textPaint.setTextSize(w/4);
      float x,y;
      y=w*0.707f;
      y=(y*8)/10;
      x=(float) (-textPaint.measureText(Text))/2;
      canvas.rotate(-45);
      canvas.drawText(Text, x , y, textPaint);
      canvas.restore();
  }
  public void setText(String str) {
      Text = str==null?"":str;
      invalidate();
  }
}
