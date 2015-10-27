package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.AutoCompleteTextViewAdapter;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.Utils;

/**
 * 添加新地址
 * 
 * @author baojiarui
 *
 */
public class AddAddress extends FragmentActivity implements OnGetPoiSearchResultListener, OnItemClickListener, OnClickListener {
    
    private TextView title;         //标题
    private ImageView btn_left;     //返回
    private RelativeLayout btn_ok;  //确定
    private EditText et_addr;       //详细地址

	/** 百度地图相关 **/
	private PoiSearch mPoiSearch = null;
	private MapView mMapView;
	private BaiduMap mBaiduMap = null;
	private LocationClient mLocClient;// 定位相关
	public MyLocationListenner myListener = new MyLocationListenner();
	private Marker mMarkerA;		//自定义图层标签，用来显示用户选择的位置
	private BitmapDescriptor bdA = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);// 初始化全局 bitmap 信息，不用时及时 recycle
	
	boolean isFirstLoc = true;// 是否首次定位
	
	/** 搜索关键字输入和联想控件 **/
	private AutoCompleteTextView keyWorldsView = null;
	private AutoCompleteTextViewAdapter adapter;
	private int load_Index = 0;
	private String currentCity = "北京";//搜索的城市
	private ArrayList<PoiInfo> poiList;//搜索出来的结果列表
	private PoiInfo userPoiInfo;//用户选择的地址

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_poisearch);
		
		initView();
		initBaidu();
		initAutoText();
		
	}
	
	private void initView(){
	    title = (TextView) findViewById(R.id.header_tv_name);
	    TextView tv_ok = (TextView) findViewById(R.id.title_btn_edit);
	    tv_ok.setBackgroundColor(Color.TRANSPARENT);
	    tv_ok.setText("确定");
	    title.setText("添加地址");
	    btn_left = (ImageView) findViewById(R.id.title_btn_left);
	    btn_ok = (RelativeLayout) findViewById(R.id.title_btn_edit_layout);
	    et_addr = (EditText) findViewById(R.id.et_addr);
	    
	    btn_left.setVisibility(View.VISIBLE);
	    btn_ok.setVisibility(View.VISIBLE);
	    btn_left.setOnClickListener(this);
	    btn_ok.setOnClickListener(this);
	}
	
	/**
	 * 初始化百度相关内容
	 */
	private void initBaidu() {
		// 初始化搜索模块，注册搜索事件监听
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}
	
	/**
	 * 初始化联想输入框
	 */
	private void initAutoText() {
		keyWorldsView = (AutoCompleteTextView) findViewById(R.id.searchkey);
		adapter = new AutoCompleteTextViewAdapter(getApplicationContext());
		keyWorldsView.setAdapter(adapter);
		keyWorldsView.setOnItemClickListener(this);

		/**
		 * 当输入关键字变化时，动态更新建议列表
		 */
		keyWorldsView.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
			}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}
			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
				if (cs.length() <= 0) {
					return;
				}

				/**
				 * 使用Poi搜索获取建议列表，结果在onGetPoiResult()中更新
				 */
				searchProcess();
			}
		});
	}
	
	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		mPoiSearch.destroy();
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	/**
	 * 通过百度Poi搜索关键字
	 * 
	 * @param v
	 */
	public void searchProcess() {
		EditText editSearchKey = (EditText) findViewById(R.id.searchkey);
		mPoiSearch.searchInCity((new PoiCitySearchOption())
				.city(currentCity)
				.keyword(editSearchKey.getText().toString())
				.pageNum(load_Index));
	}

	/**
	 * 百度Poi搜索结果
	 */
	public void onGetPoiResult(PoiResult result) {
		if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
//			Toast.makeText(AddAddress.this, "未找到结果", Toast.LENGTH_LONG).show();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			
			if(poiList == null){
				poiList = new ArrayList<PoiInfo>();
			}
			poiList = (ArrayList<PoiInfo>) result.getAllPoi();
			
			adapter.setData(poiList);
			return;
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {/*

			// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
			String strInfo = "在";
			for (CityInfo cityInfo : result.getSuggestCityList()) {
				strInfo += cityInfo.city;
				strInfo += ",";
			}
			strInfo += "找到结果";
			Toast.makeText(PoiSearchDemo.this, strInfo, Toast.LENGTH_LONG).show();
		*/}
	}

	/**
	 * 点击地图覆盖层上的Poi后搜索出来的详细信息
	 */
	public void onGetPoiDetailResult(PoiDetailResult result) {
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(AddAddress.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(AddAddress.this, result.getName() + ": " + result.getAddress(), Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 显示Poi搜索出来的地址，地图覆盖层
	 * @author baojiarui
	 *
	 */
	private class MyPoiOverlay extends PoiOverlay {

		public MyPoiOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);
			PoiInfo poi = getPoiResult().getAllPoi().get(index);
			// if (poi.hasCaterDetails) {
				mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
						.poiUid(poi.uid));
			// }
			return true;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		keyWorldsView.setText(poiList.get(position).name);
		userPoiInfo = poiList.get(position);
		et_addr.setVisibility(View.VISIBLE);
		
		updateUserOverlay(poiList.get(position).location);
	}
	
	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
			
		}
	}
	
	/**
	 * 在地图中显示用户选择的位置
	 * 
	 * @param ll	经纬度信息
	 */
	public void updateUserOverlay(LatLng ll) {
		clearOverlay(null);
		
		// add marker overlay
		OverlayOptions ooA = new MarkerOptions().position(ll).icon(bdA).zIndex(9).draggable(true);
		mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));

		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
		mBaiduMap.setMapStatus(u);

	}
	
	/**
	 * 清除所有Overlay
	 * 
	 * @param view
	 */
	public void clearOverlay(View view) {
		mBaiduMap.clear();
	}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.title_btn_left:   //返回
            finish();
            break;
        case R.id.title_btn_edit_layout://确定
            postAddress();
            break;

        default:
            break;
        }
    }
    
    /**
     * 添加地址接口
     */
    private void postAddress() {
        
        String user_id = DBHelper.getUser(this).getId();

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }
        
        if(userPoiInfo == null){
            Toast.makeText(this, "请选择地址", 0).show();
            return;
        }
        String addr = et_addr.getText().toString();
        if(StringUtils.isEmpty(addr)){
            Toast.makeText(this, "请输入详细地址", 0).show();
            return;
        }
        
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id);
        map.put("addr_id", "0");//地址ID, 0 = 新增, > 0 ，则说明是修改
        map.put("poi_type", "0");//地理位置poi类型 0 = 普通点 1 = 公交站 2 = 公交路线 3 = 地铁站 4 = 地铁线路
        map.put("name", userPoiInfo.name);
        map.put("address", userPoiInfo.address);
        map.put("latitude", userPoiInfo.location.latitude+"");
        map.put("longitude", userPoiInfo.location.longitude+"");
        map.put("city", userPoiInfo.city);
        map.put("uid", userPoiInfo.uid);
        map.put("phone", userPoiInfo.phoneNum);
        map.put("post_code", userPoiInfo.postCode);
        map.put("addr", addr);  //门牌号, urlencode
        map.put("is_default", "0");
        map.put("city_id", "");
        map.put("cell_id", "");
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().post(Constants.URL_POST_ADDRS, param, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                LogOut.debug("错误码：" + errorNo);
                dismissDialog();
                Toast.makeText(AddAddress.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                dismissDialog();
                LogOut.debug("成功:" + t.toString());

                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            Toast.makeText(AddAddress.this, "添加成功!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            Toast.makeText(AddAddress.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(AddAddress.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(AddAddress.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(AddAddress.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddAddress.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
//                    UIUtils.showToast(context, "网络错误,请稍后重试");
                }

            }
        });
    }
    
    private ProgressDialog m_pDialog;
    public void showDialog() {
        if(m_pDialog == null){
            m_pDialog = new ProgressDialog(this);
            m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            m_pDialog.setMessage("请稍等...");
            m_pDialog.setIndeterminate(false);
            m_pDialog.setCancelable(true);
        }
        m_pDialog.show();
    }

    public void dismissDialog() {
        if (m_pDialog != null && m_pDialog.isShowing()) {
            m_pDialog.hide();
        }
    }
	
}
