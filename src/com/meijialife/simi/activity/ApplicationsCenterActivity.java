package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.database.DBHelper;

/**
 * @description：应用中心
 * @author： kerryg
 * @date:2015年12月3日
 */
public class ApplicationsCenterActivity extends BaseActivity {

    private GridView gv_application1;
    private GridView gv_application2;

    private SimpleAdapter simpleAdapter1;
    private SimpleAdapter simpleAdapter2;

    private int[] icon1 = { R.drawable.iconfont_qianbao, R.drawable.iconfont_youhuiquan, R.drawable.iconfont_dingdan, R.drawable.iconfont_jifen,
            R.drawable.iconfont_dakashijian,R.drawable.iconfont_huilvhuiyishi,
            R.drawable.iconfont_shui,R.drawable.iconfont_jiatingbaoji,
            R.drawable.iconfont_wuliuguanliicon,R.drawable.iconfont_flowerpot};
    private int[] icon2 = { R.drawable.iconfont_zhishiku, R.drawable.iconfont_renzhengkaoshi, R.drawable.iconfont_peixun,
            R.drawable.iconfont_jianzhizhuanqian, };

    private String[] iconName1 = { "钱包", "优惠券", "订单", "积分商城","云考勤","会议室","送水","保洁","快递","绿植" };
    private String[] iconName2 = { "知识库", "认证考试", "培训", "积分赚钱" };

    private List<Map<String, Object>> data_list1;
    private List<Map<String, Object>> data_list2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.applications_center_activity);
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setTitleName("应用中心");
        requestBackBtn();

        gv_application1 = (GridView) findViewById(R.id.gv_application1);
        gv_application2 = (GridView) findViewById(R.id.gv_application2);

        data_list1 = new ArrayList<Map<String, Object>>();
        data_list2 = new ArrayList<Map<String, Object>>();

        getData1();
        getData2();

        String[] from = { "iv_application_icon", "tv_application_name" };
        int[] to = { R.id.iv_application_icon, R.id.tv_application_name };
        simpleAdapter1 = new SimpleAdapter(this, data_list1, R.layout.application_center_item, from, to);
        gv_application1.setAdapter(simpleAdapter1);
        gv_application1.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv_application_name = (TextView)view.findViewById(R.id.tv_application_name);
                String name  = tv_application_name.getText().toString().trim();
                if(name.equals("钱包")){
                    startActivity(new Intent(ApplicationsCenterActivity.this, MyWalletActivity.class));
                }else if(name.equals("优惠券")){
                    startActivity(new Intent(ApplicationsCenterActivity.this, DiscountCardActivity.class));
                }else if (name.equals("订单")) {
                    startActivity(new Intent(ApplicationsCenterActivity.this, MyOrderActivity.class));
                } else if (name.equals("积分商城")) {
                    Intent intent6 = new Intent();
                    intent6.setClass(ApplicationsCenterActivity.this, PointsShopActivity.class);
                    intent6.putExtra("navColor", "#E8374A"); // 配置导航条的背景颜色，请用#ffffff长格式。
                    intent6.putExtra("titleColor", "#ffffff"); // 配置导航条标题的颜色，请用#ffffff长格式。
                    intent6.putExtra("url", Constants.URL_POST_SCORE_SHOP + "?user_id=" + DBHelper.getUserInfo(ApplicationsCenterActivity.this).getUser_id()); // 配置自动登陆地址，每次需服务端动态生成。
                    startActivity(intent6);
                } else if(name.equals("云考勤")){
                    Intent intent = new Intent(ApplicationsCenterActivity.this,WebViewsActivity.class);
                    intent.putExtra("url", Constants.YUN_KAO_QIN);
                    startActivity(intent);
                }else if(name.equals("会议室")){
                    Intent intent = new Intent(ApplicationsCenterActivity.this,WebViewsActivity.class);
                    intent.putExtra("url", Constants.HUI_YI_SHI);
                    startActivity(intent);
                }else if (name.equals("送水")) {
                    Intent intent = new Intent(ApplicationsCenterActivity.this,WebViewsActivity.class);
                    intent.putExtra("url", Constants.SONG_SHUI);
                    startActivity(intent);
                } else if (name.equals("保洁")) {
                    Intent intent = new Intent(ApplicationsCenterActivity.this,WebViewsActivity.class);
                    intent.putExtra("url", Constants.BAO_JIE);
                    startActivity(intent);
                } else if(name.equals("快递")){
                    Intent intent = new Intent(ApplicationsCenterActivity.this,WebViewsActivity.class);
                    intent.putExtra("url", Constants.KUAI_DI);
                    startActivity(intent);
                }else if(name.equals("绿植")){
                    Intent intent = new Intent(ApplicationsCenterActivity.this,WebViewsActivity.class);
                    intent.putExtra("url", Constants.LV_ZHI);
                    startActivity(intent);
                }
            }
        });

        simpleAdapter2 = new SimpleAdapter(this, data_list2, R.layout.application_center_item, from, to);
        gv_application2.setAdapter(simpleAdapter2);
        
        gv_application2.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv_application_name = (TextView)view.findViewById(R.id.tv_application_name);
                String name  = tv_application_name.getText().toString().trim();
                
                if(name.equals("知识库")){
                    Intent intent = new Intent(ApplicationsCenterActivity.this,WebViewsActivity.class);
                    intent.putExtra("url", Constants.SHOP_URL);
                    startActivity(intent);
                }else if (name.equals("认证考试")) {
                    Intent intent = new Intent(ApplicationsCenterActivity.this,WebViewsActivity.class);
                    intent.putExtra("url", Constants.ATTEST_URL);
                    startActivity(intent);
                }else if (name.equals("培训")) {
                    Intent intent = new Intent(ApplicationsCenterActivity.this,WebViewsActivity.class);
                    intent.putExtra("url", Constants.TRAIN_URL);
                    startActivity(intent);
                }else if (name.equals("积分赚钱")) {
                    Intent intent = new Intent(ApplicationsCenterActivity.this,WebViewsActivity.class);
                    intent.putExtra("url", Constants.MONEY_URL);
                    startActivity(intent);
                }
            }
        });
    }

    private List<Map<String, Object>> getData1() {
        for (int i = 0; i < icon1.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("iv_application_icon", icon1[i]);
            map.put("tv_application_name", iconName1[i]);
            data_list1.add(map);
        }
        return data_list1;
    }

    private List<Map<String, Object>> getData2() {
        for (int i = 0; i < icon2.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("iv_application_icon", icon2[i]);
            map.put("tv_application_name", iconName2[i]);
            data_list2.add(map);
        }
        return data_list2;
    }

}
