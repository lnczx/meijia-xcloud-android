package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.PointsGiftListAdapter;
import com.meijialife.simi.adapter.PointsList;
import com.meijialife.simi.bean.PointsData;
import com.meijialife.simi.bean.PointsGiftData;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;

/**
 * 积分
 *
 * @author RUI
 *
 */
public class PointsActivity extends BaseActivity implements OnClickListener {

	private LinearLayout tab_1, tab_2; // tab按钮
	private LinearLayout content_1, content_2; // tab内容
	private TextView tv_score; // 积分View
	private TextView tv_help; // 怎样获取积分
	// 加载到的页数
	private int nowPage = 1;

	/** 积分兑换列表 **/
	private ListView tab1_listview;
	private PointsGiftListAdapter tab1_adapter;
	private ArrayList<PointsGiftData> tab1_datas;

	/** 积分明细列表 **/
	private PointsList tab2_adapter;
	private ArrayList<PointsData> tab2_datas = new ArrayList<PointsData>();

	private Account account;
	private String score = ""; // 当前积分

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.points_activity);
		super.onCreate(savedInstanceState);

		init();
//		getDetaislList();

		testTab1();
		testTab2();
	}

	private void init() {
		requestBackBtn();
		setTitleName("我 的 积 分");
		score = getIntent().getStringExtra("score");
		
		tv_score = (TextView) findViewById(R.id.points_tv_score);
		tv_score.setText(score);
		tv_help = (TextView) findViewById(R.id.points_tv_help);
		tv_help.setOnClickListener(this);

		tab1_listview = (ListView) findViewById(R.id.points_tab1_listview);
		content_1 = (LinearLayout) findViewById(R.id.points_ll_content_1);
		tab_1 = (LinearLayout) findViewById(R.id.points_ll_tab_1);
		tab_1.setOnClickListener(this);

		// tab2_adapter = new PointsListAdapter(this, tab2_datas);
		// tab2_listview.setAdapter(tab2_adapter);
		tab_2 = (LinearLayout) findViewById(R.id.points_ll_tab_2);
		content_2 = (LinearLayout) findViewById(R.id.points_ll_content_2);
		tab_2.setOnClickListener(this);

		tab_1.performClick();
		setTabSelected(tab_1);
	}

	private void testTab1() {
		tab1_datas = new ArrayList<PointsGiftData>();
		PointsGiftData data1 = new PointsGiftData("云行政通用优惠券（20元）", "100");
		tab1_datas.add(data1);

		tab1_adapter = new PointsGiftListAdapter(this, account, tab1_datas);
		tab1_listview.setAdapter(tab1_adapter);
	}

	private void testTab2() {/*
							 * tab2_datas = new ArrayList<PointsData>();
							 * PointsData data1 = new PointsData("mobile",
							 * "score_id", 1, 0, "5", "add_time"); PointsData
							 * data2 = new PointsData("mobile", "score_id", 2,
							 * 1, "13", "add_time"); PointsData data3 = new
							 * PointsData("mobile", "score_id", 3, 0, "15",
							 * "add_time"); PointsData data4 = new
							 * PointsData("mobile", "score_id", 1, 1, "20",
							 * "add_time"); tab2_datas.add(data1);
							 * tab2_datas.add(data2); tab2_datas.add(data3);
							 * tab2_datas.add(data4);
							 *
							 * tab2_adapter = new PointsListAdapter(this,
							 * tab2_datas);
							 * tab2_listview.setAdapter(tab2_adapter);
							 */
	}

	/**
	 * 设置tab选中状态
	 */
	private void setTabSelected(LinearLayout tab) {
		tab_1.setSelected(false);
		tab_2.setSelected(false);

		tab.setSelected(true);
	}

	/**
	 * 积分兑换View
	 */
	private void initTabView1() {
		content_1.setVisibility(View.VISIBLE);
		content_2.setVisibility(View.GONE);
	}

	/**
	 * 积分明细View
	 */
	private void initTabView2() {
		content_1.setVisibility(View.GONE);
		content_2.setVisibility(View.VISIBLE);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.points_ll_tab_1: // 积分兑换
			setTabSelected(tab_1);
			initTabView1();
			break;
		case R.id.points_ll_tab_2: // 积分明细
			setTabSelected(tab_2);
			initTabView2();
			break;
		case R.id.points_tv_help: // 怎样获取积分
			Intent intent = new Intent(this, PointsHelpActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	/**
	 * 获取积分明细列表
	 *
	 */
	private void getDetaislList() {
		if (!NetworkUtils.isNetworkConnected(PointsActivity.this)) {
			Toast.makeText(PointsActivity.this,
					getString(R.string.net_not_open), 0).show();
			return;
		}

		Map<String, String> map = new HashMap<String, String>();
		map.put("user_id", DBHelper.getUser(this).getId()); // 
		map.put("page", "" + nowPage); // 分页页码,从 1开始
		AjaxParams param = new AjaxParams(map);

		showDialog();
		new FinalHttp().get(Constants.URL_GET_SCORE_DETAILS, param,
				new AjaxCallBack<Object>() {
					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						super.onFailure(t, errorNo, strMsg);
						dismissDialog();
						Toast.makeText(PointsActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(Object t) {
						super.onSuccess(t);
						dismissDialog();
						LogOut.i("========", "onSuccess：" + t);
						JSONObject json;
						try {
							json = new JSONObject(t.toString());
							int status = Integer.parseInt(json.getString("status"));
							String msg = json.getString("msg");
//							if (status == Constant.STATUS_SUCCESS) { // 正确
//								nowPage++;
//								parseJson(json);
//							} else if (status == Constant.STATUS_SERVER_ERROR) { // 服务器错误
//								Toast.makeText(PointsActivity.this,getString(R.string.servers_error),Toast.LENGTH_SHORT).show();
//							} else if (status == Constant.STATUS_PARAM_MISS) { // 缺失必选参数
//								Toast.makeText(PointsActivity.this,getString(R.string.param_missing),Toast.LENGTH_SHORT).show();
//							} else if (status == Constant.STATUS_PARAM_ILLEGA) { // 参数值非法
//								Toast.makeText(PointsActivity.this,getString(R.string.param_illegal),Toast.LENGTH_SHORT).show();
//							} else if (status == Constant.STATUS_OTHER_ERROR) { // 999其他错误
//								Toast.makeText(PointsActivity.this, msg,Toast.LENGTH_LONG).show();
//							} else {
//								Toast.makeText(PointsActivity.this,getString(R.string.servers_error),Toast.LENGTH_SHORT).show();
//							}

						} catch (JSONException e) {
							e.printStackTrace();
							Toast.makeText(PointsActivity.this,
									getString(R.string.servers_error),
									Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	/**
	 * 解析json数据
	 *
	 * @param json
	 */
	private void parseJson(JSONObject json) {
		tab2_datas.clear();
		try {
			JSONArray rankArray = json.getJSONArray("data");

			for (int i = 0; i < rankArray.length(); i++) {
				JSONObject obj = (JSONObject) rankArray.get(i);
				String mobile = obj.getString("mobile"); //
				String score_id = obj.getString("id"); //
				int action_id = Integer.parseInt(obj.getString("action_id")); //
				int is_consume = Integer.parseInt(obj.getString("is_consume")); //
				String score = obj.getString("score"); //
				String add_time = obj.getString("add_time"); //

				PointsData data = new PointsData(mobile, score_id, action_id,
						is_consume, score, add_time);
				tab2_datas.add(data);
			}
			// tab2_adapter.notifyDataSetChanged();
			new PointsList(this, this, tab2_datas);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(PointsActivity.this,
					getString(R.string.servers_error), Toast.LENGTH_SHORT)
					.show();
		}
	}
}
