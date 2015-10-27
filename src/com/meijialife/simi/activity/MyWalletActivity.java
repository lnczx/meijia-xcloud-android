package com.meijialife.simi.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.MyWalletAdapter;
import com.meijialife.simi.bean.MyWalletData;

/**
 * 我的钱包
 * 
 */
public class MyWalletActivity extends BaseActivity implements OnClickListener {

    private Button btn_recharge;// 充值

    private ListView listview;
    private MyWalletAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.my_wallet_activity);
        super.onCreate(savedInstanceState);

        initView();
        test();

    }

    private void initView() {
        setTitleName("我的钱包");
        requestBackBtn();

        btn_recharge = (Button) findViewById(R.id.btn_recharge);
        btn_recharge.setOnClickListener(this);

        listview = (ListView) findViewById(R.id.listview);
        adapter = new MyWalletAdapter(this);
        listview.setAdapter(adapter);
    }

    /**
     * 测试数据
     */
    private void test() {
        ArrayList<MyWalletData> list = new ArrayList<MyWalletData>();
        MyWalletData data1 = new MyWalletData("收到技能卡费用", "11-29 03:24", "+105", "1866***6666");
        MyWalletData data2 = new MyWalletData("收到两个月秘书费", "11-29 03:24", "+500", "1866***6666");
        MyWalletData data3 = new MyWalletData("支付奖励", "11-29 03:24", "+500", "1866***6666");
        MyWalletData data4 = new MyWalletData("收到车费", "11-29 03:24", "+500", "1866***6666");
        MyWalletData data5 = new MyWalletData("支付奖励", "11-29 03:24", "+500", "1866***6666");
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
        case R.id.btn_recharge: // 充值
            startActivity(new Intent(MyWalletActivity.this, AccountRechangeActivity.class));
            break;

        default:
            break;
        }
    }
}
