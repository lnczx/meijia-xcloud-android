package com.meijialife.simi.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.CityListAdapter;
import com.meijialife.simi.bean.CityData;
import com.meijialife.simi.database.DBHelper;

/**
 * 常用地址
 * 
 */
public class CityListActivity extends BaseActivity {

    private ListView listview;
    private CityListAdapter adapter;
    private List<CityData> citys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_city_list);
        super.onCreate(savedInstanceState);

        initView();

    }

    private void initView() {
        setTitleName("城市列表");
        requestBackBtn();

        listview = (ListView) findViewById(R.id.listview);
        adapter = new CityListAdapter(this);
        citys = DBHelper.getCitys(this);
        if (citys == null || citys.size() < 0) {
            return;
        }
        adapter.setData(citys);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CityData cityData = citys.get(position);
                String cityname = cityData.getName();
                String cityid = cityData.getCity_id();
                Intent intent = new Intent();
                intent.putExtra("city_name", cityname);
                intent.putExtra("city_id", cityid);
                setResult(RESULT_OK, intent);
                finish();

            }
        });
    }

}