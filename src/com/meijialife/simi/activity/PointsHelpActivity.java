package com.meijialife.simi.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.R;

/**
 * 怎样获取积分页面
 * @author RUI
 *
 */
public class PointsHelpActivity extends BaseActivity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.points_help_activity);
		super.onCreate(savedInstanceState);

		init();
	}

	private void init(){
		setTitleName("怎样获取积分");
		requestBackBtn();
	}

	public void onClick(View v) {
		switch (v.getId()) {
//		case R.id.:
//			break;

		default:
			break;
		}
	}
}
