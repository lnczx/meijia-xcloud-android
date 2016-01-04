package com.meijialife.simi.fra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.BaseFragment;
import com.meijialife.simi.Constants;
import com.meijialife.simi.MainActivity;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.Find2DetailActivity;
import com.meijialife.simi.activity.SearchViewActivity;
import com.meijialife.simi.activity.WebViewsFindActivity;
import com.meijialife.simi.adapter.Find2Adapter;
import com.meijialife.simi.bean.ChanelBean;
import com.meijialife.simi.bean.FindBean;
import com.meijialife.simi.ui.SyncHorizontalScrollView;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * @description：发现--秘书助理，综合服务，设计策划
 * @author： kerryg
 * @date:2015年11月13日
 */
public class Find2Fra extends BaseFragment {

    private MainActivity activity;
    private ListView listview;
    private Find2Adapter adapter;// 服务商适配器
    private RelativeLayout rl_total_search;// 搜索框
    /**
     * 左右滑动控件声明
     */
    private RelativeLayout rl_nav;
    private SyncHorizontalScrollView mHsv;
    private RadioGroup rg_nav_content;
    private ImageView iv_nav_indicator;
    private ImageView iv_nav_left;
    private ImageView iv_nav_right;
    private int indicatorWidth;
    public  ArrayList<String> tabTitle ;// 标题
    private LayoutInflater mInflater;
    private int currentIndicatorLeft = 0;
    
    private ArrayList<FindBean> findBeanList;
    private ArrayList<ChanelBean> chanelBeanList;
    private String  title_id = "1";
    private String  title_name="发现";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.find_list, null);
        findViewById(v);
        init(v);
        setListener();
        getFind2List(title_id);
        return v;
    }

    private void findViewById(View v) {
        /**
         * 标题滑动
         */
        rl_nav = (RelativeLayout) v.findViewById(R.id.rl_nav);
        mHsv = (SyncHorizontalScrollView) v.findViewById(R.id.mHsv);
        rg_nav_content = (RadioGroup) v.findViewById(R.id.rg_nav_content);
        iv_nav_indicator = (ImageView) v.findViewById(R.id.iv_nav_indicator);
        iv_nav_left = (ImageView) v.findViewById(R.id.iv_nav_left);
        iv_nav_right = (ImageView) v.findViewById(R.id.iv_nav_right);
        
        chanelBeanList = new ArrayList<ChanelBean>();

        /**
         * 搜索+列表展现
         */
        rl_total_search = (RelativeLayout) v.findViewById(R.id.rl_total_search);
        listview = (ListView) v.findViewById(R.id.find_list_view);
        adapter = new Find2Adapter(activity);
        listview.setAdapter(adapter);
        getChanelList();
    }
    /*
     * 初始化适配器
     */
    public void init(View v) {
        //获取屏幕宽度
      
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            indicatorWidth = dm.widthPixels / 4;
            
            LayoutParams cursor_Params = (LayoutParams) iv_nav_indicator.getLayoutParams();
            cursor_Params.width = indicatorWidth;// 初始化滑动下标的宽
            iv_nav_indicator.setLayoutParams(cursor_Params);
            mHsv.setSomeParam(rl_nav, iv_nav_left, iv_nav_right, getActivity());
            mInflater = LayoutInflater.from(getActivity());
        
    }
    private void setListener() {
        rg_nav_content.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton)rg_nav_content.getChildAt((checkedId));
                if (rb != null) {
                    TranslateAnimation animation = new TranslateAnimation(currentIndicatorLeft, ((RadioButton) rg_nav_content.getChildAt(checkedId))
                            .getLeft(), 0f, 0f);
                    animation.setInterpolator(new LinearInterpolator());
                    animation.setDuration(100);
                    animation.setFillAfter(true);
                    // 执行位移动画
                    iv_nav_indicator.startAnimation(animation);
                    // 记录当前 下标的距最左侧的 距离
                    currentIndicatorLeft = ((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft();
                    mHsv.smoothScrollTo((checkedId > 1 ? ((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft() : 0)
                            - ((RadioButton) rg_nav_content.getChildAt(2)).getLeft(), 0);
                    title_name = rb.getText().toString().trim();
                    getFind2List((checkedId+1)+"");
                }
            }
        });

        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                
                TextView tv_gotoType =(TextView)view.findViewById(R.id.tv_goto_type);
                TextView tv_gotoUrl =(TextView)view.findViewById(R.id.tv_goto_url);
                TextView tv_service_type_ids =(TextView)view.findViewById(R.id.tv_service_type_ids);
                
                String goto_type = tv_gotoType.getText().toString().trim();
                String goto_url = tv_gotoUrl.getText().toString().trim();
                String service_type_ids = tv_service_type_ids.getText().toString().trim();
                if(goto_type.equals("h5")){
                    Intent intent = new Intent(getActivity(),WebViewsFindActivity.class);
                    intent.putExtra("url",goto_url);
                    intent.putExtra("title_name","");
                    intent.putExtra("service_type_ids", "");
                    startActivity(intent);
                }else if (goto_type.equals("app")) {
                   Intent intent = new Intent(getActivity(),Find2DetailActivity.class);
                   intent.putExtra("service_type_ids", service_type_ids);
                   intent.putExtra("title_name",title_name);
                   startActivity(intent);
                }else if (goto_type.equals("h5+list")) {
                    Intent intent = new Intent(getActivity(),WebViewsFindActivity.class);
                    intent.putExtra("url",goto_url);
                    intent.putExtra("title_name",title_name);
                    intent.putExtra("service_type_ids", service_type_ids);
                    startActivity(intent);
                }
            }
        });

        rl_total_search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SearchViewActivity.class));
            }
        });

    }
	private void initNavigationHSV(ArrayList<ChanelBean> chanelBeanList) {
        rg_nav_content.removeAllViews();
        for (Iterator iterator = chanelBeanList.iterator(); iterator.hasNext();) {
            ChanelBean chanelBean = (ChanelBean) iterator.next();
            RadioButton rb = (RadioButton) mInflater.inflate(R.layout.nav_radiogroup_item, null);
            int id = new Integer(chanelBean.getChannel_id()).intValue();
            rb.setId((id-1));
            rb.setText(chanelBean.getName());
            rb.setLayoutParams(new LayoutParams(indicatorWidth, LayoutParams.MATCH_PARENT));
            rg_nav_content.addView(rb);
        }
        rg_nav_content.check(0);//默认选中第一个RadioButton
    }
    public Find2Fra() {
        super();
    }

    public Find2Fra(MainActivity activity) {
        super();
        this.activity = activity;
    }
    /**
     * 获得频道中广告信息接口（图片+文字）
     */
    public void getFind2List(String channel_id) {
        if (!NetworkUtils.isNetworkConnected(activity)) {
            Toast.makeText(activity, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("channel_id", channel_id);
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().get(Constants.URL_GET_ADS_LIST, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(activity, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                dismissDialog();
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            if (StringUtils.isNotEmpty(data)) {
                                Gson gson = new Gson();
                                findBeanList = gson.fromJson(data, new TypeToken<ArrayList<FindBean>>() {
                                }.getType());
                                adapter.setData(findBeanList);
                            } else {
                                adapter.setData(new ArrayList<FindBean>());
                            }
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            errorMsg = getString(R.string.servers_error);
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            errorMsg = getString(R.string.param_missing);
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            errorMsg = getString(R.string.param_illegal);
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            errorMsg = msg;
                        } else {
                            errorMsg = getString(R.string.servers_error);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg = getString(R.string.servers_error);
                }
                // 操作失败，显示错误信息
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    UIUtils.showToast(activity, errorMsg);
                }
            }
        });
    }
    /**
     * 获得频道列表接口
     */
    public void getChanelList() {
        if (!NetworkUtils.isNetworkConnected(activity)) {
            Toast.makeText(activity, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().get(Constants.URL_GET_CHANEL_LIST, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(activity, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                dismissDialog();
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            if (StringUtils.isNotEmpty(data)) {
                                Gson gson = new Gson();
                                chanelBeanList = gson.fromJson(data, new TypeToken<ArrayList<ChanelBean>>() {
                                }.getType());
                            }else {
                                chanelBeanList = new ArrayList<ChanelBean>();
                            }
                            initNavigationHSV(chanelBeanList);
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            errorMsg = getString(R.string.servers_error);
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            errorMsg = getString(R.string.param_missing);
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            errorMsg = getString(R.string.param_illegal);
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            errorMsg = msg;
                        } else {
                            errorMsg = getString(R.string.servers_error);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if(isAdded()){//在Fragment中使用系统资源必须增加判断
                        errorMsg = getString(R.string.servers_error);
                    }
                }
                // 操作失败，显示错误信息
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    UIUtils.showToast(activity, errorMsg);
                }
            }
        });
    }
    
}
