package com.meijialife.simi.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

import com.meijialife.simi.R;

public class PopupMenu extends PopupWindow implements OnClickListener {

	private Activity activity;
	private View popView;

	private View v_item1;
	private View v_item2;
	private View v_item3;

	private OnItemClickListener onItemClickListener;

	/**
	 * 
	 * @author ywl5320 枚举，用于区分选择了哪个选项
	 */
	public enum MENUITEM {
		ITEM1, ITEM2, ITEM3
	}

	public PopupMenu(Activity activity) {
		super(activity);
		this.activity = activity;
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		popView = inflater.inflate(R.layout.popup_menu, null);// 加载菜单布局文件
		this.setContentView(popView);// 把布局文件添加到popupwindow中
		this.setWidth(dip2px(activity, 100));// 设置菜单的宽度（需要和菜单于右边距的距离搭配，可以自己调到合适的位置）
		this.setHeight(android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);// 获取焦点
		this.setTouchable(true); // 设置PopupWindow可触摸
		this.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
	    this.setAnimationStyle(R.style.PopupAnimation);

		ColorDrawable dw = new ColorDrawable(0x00000000);
		this.setBackgroundDrawable(dw);
		// 获取选项卡
		v_item1 = popView.findViewById(R.id.ly_item1);
		v_item2 = popView.findViewById(R.id.ly_item2);
		v_item3 = popView.findViewById(R.id.ly_item3);
		// 添加监听
		v_item1.setOnClickListener(this);
		v_item2.setOnClickListener(this);
		v_item3.setOnClickListener(this);

	}


	/**
	 * 设置显示的位置
	 * 
	 * @param resourId
	 *            这里的x,y值自己调整可以
	 */
	public void showLocation(int resourId) {
		showAsDropDown(activity.findViewById(resourId), dip2px(activity, 20),
				dip2px(activity, 10));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		MENUITEM menuitem = null;
		String str = "";
		if (v == v_item1) {
			menuitem = MENUITEM.ITEM1;
			str = "刷新";
		} else if (v == v_item2) {
			menuitem = MENUITEM.ITEM2;
			str = "分享";
		} else if (v == v_item3) {
			menuitem = MENUITEM.ITEM3;
			str = "选项卡三";
		}
		if (onItemClickListener != null) {
			onItemClickListener.onClick(menuitem, str);
		}
		dismiss();
	}

	// dip转换为px
	public int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	// 点击监听接口
	public interface OnItemClickListener {
		public void onClick(MENUITEM item, String str);
	}

	// 设置监听
	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

}
