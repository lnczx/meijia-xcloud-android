package com.meijialife.simi.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.R;

/**
 * 订单详情
 *
 */
public class OrderDetailsActivity extends BaseActivity implements OnClickListener {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.order_details_activity);
        super.onCreate(savedInstanceState);
        
        initView();

    }

    private void initView() {
    	setTitleName("订单详情");
    	requestBackBtn();
    	
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//        case R.id.:	//
//        	Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
//            break;
         
        default:
            break;
        }
    }
}
