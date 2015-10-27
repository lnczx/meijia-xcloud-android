package com.meijialife.simi.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.DiscountCardAdapter;
import com.meijialife.simi.adapter.MyWalletAdapter;
import com.meijialife.simi.bean.DiscountCardData;
import com.meijialife.simi.bean.MyWalletData;

/**
 * 优惠卡券
 *
 */
public class DiscountCardActivity extends BaseActivity implements OnClickListener {
	
	private Button btn_exchange;//兑换
	
	private ListView listview;
    private DiscountCardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.discount_card_activity);
        super.onCreate(savedInstanceState);
        
        initView();
        test();

    }

    private void initView() {
    	setTitleName("我的优惠券");
    	requestBackBtn();
    	
    	btn_exchange = (Button)findViewById(R.id.btn_exchange);
    	btn_exchange.setOnClickListener(this);

    	listview = (ListView)findViewById(R.id.listview);
    	adapter = new DiscountCardAdapter(this);
    	listview.setAdapter(adapter);
    }
    
    /**
     * 测试数据
     */
    private void test(){
    	ArrayList<DiscountCardData> list = new ArrayList<DiscountCardData>();
    	DiscountCardData data1 = new DiscountCardData("汽車保养", "", "", "");
    	DiscountCardData data2 = new DiscountCardData("洗衣做饭", "", "", "");
    	DiscountCardData data3 = new DiscountCardData("擦玻璃", "", "", "");
    	DiscountCardData data4 = new DiscountCardData("沙发清洗", "", "", "");
    	DiscountCardData data5 = new DiscountCardData("小时工", "", "", "");
    	list.add(data1);
    	list.add(data2);
    	list.add(data3);
    	list.add(data4);
    	list.add(data5);
    	
    	adapter.setData(list);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_exchange:	//兑换
        	Toast.makeText(this, "兑换", Toast.LENGTH_SHORT).show();
            break;
         
        default:
            break;
        }
    }
}
