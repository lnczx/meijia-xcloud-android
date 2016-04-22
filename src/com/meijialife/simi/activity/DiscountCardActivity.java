package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.DiscountCardAdapter;
import com.meijialife.simi.bean.MyDiscountCard;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * 优惠卡券
 *
 */
public class DiscountCardActivity extends BaseActivity implements OnClickListener,OnItemClickListener {
	
	private Button btn_exchange;//兑换
	
	private ListView listview;
    private DiscountCardAdapter adapter;
    private MyDiscountCard myDiscountCard;
    
    private User user;
    private EditText et_card_passwd;
    private String card_password;
    private int flag = 0;//0=其他页面进入 1=支付页面进入(执行事件)

    private ArrayList<MyDiscountCard> myDiscountCardList;
    private ArrayList<MyDiscountCard> totalDiscountCardList;
    //布局控件定义
    private PullToRefreshListView mPullRefreshListView;//上拉刷新的控件 
    private int page = 1;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.discount_card_activity);
        super.onCreate(savedInstanceState);
        initView();

    }

    private void initView() {
    	setTitleName("我的优惠券");
    	requestBackBtn();
    	
    	et_card_passwd =(EditText)findViewById(R.id.et_card_passwd);
    	
    	btn_exchange = (Button)findViewById(R.id.btn_exchange);
    	btn_exchange.setOnClickListener(this);
    	flag = getIntent().getIntExtra("flag",0);

    	
    /*	listview = (ListView)findViewById(R.id.listview);
    	adapter = new DiscountCardAdapter(this);
    	listview.setAdapter(adapter);
    	
    	listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyDiscountCard myDiscountCard = (MyDiscountCard)myDiscountCardList.get(position);
                if(flag==1){
                    Intent intent = new Intent();
                    intent.putExtra("myDiscountCard",myDiscountCard);
                    setResult(RESULT_FIRST_USER, intent);
                    finish();
                }
            }
        });*/
    	initDiscountView();
    	
    }
    
    private void initDiscountView(){
        totalDiscountCardList = new ArrayList<MyDiscountCard>();
        mPullRefreshListView = (PullToRefreshListView)findViewById(R.id.pull_refresh_discount_list);
        mPullRefreshListView.setOnItemClickListener(this);
        adapter = new DiscountCardAdapter(this);
        mPullRefreshListView.setAdapter(adapter);
        mPullRefreshListView.setMode(Mode.BOTH);
        initIndicator();
        getMyDiscountCardList(page);
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                page = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getMyDiscountCardList(page);
                adapter.notifyDataSetChanged(); 
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if(myDiscountCardList!=null && myDiscountCardList.size()>=10){
                    page = page+1;
                    getMyDiscountCardList(page);
                    adapter.notifyDataSetChanged(); 
                }else {
                    Toast.makeText(DiscountCardActivity.this,"请稍后，没有更多加载数据",Toast.LENGTH_SHORT).show();
                    mPullRefreshListView.onRefreshComplete(); 
                }
            }
        });
    }
    /**
     * 设置下拉刷新提示
     */
    private void initIndicator()  
    {  
        ILoadingLayout startLabels = mPullRefreshListView  
                .getLoadingLayoutProxy(true, false);  
        startLabels.setPullLabel("下拉刷新");// 刚下拉时，显示的提示  
        startLabels.setRefreshingLabel("正在刷新...");// 刷新时  
        startLabels.setReleaseLabel("释放更新");// 下来达到一定距离时，显示的提示  
  
        ILoadingLayout endLabels = mPullRefreshListView.getLoadingLayoutProxy(  
                false, true);  
        endLabels.setPullLabel("上拉加载");
        endLabels.setRefreshingLabel("正在刷新...");// 刷新时  
        endLabels.setReleaseLabel("释放加载");// 下来达到一定距离时，显示的提示  
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_exchange:	//兑换
            String card_password = et_card_passwd.getText().toString().trim();
            if(StringUtils.isEmpty(card_password)){
                Toast.makeText(this, "兑换码不能为空！", Toast.LENGTH_SHORT).show();
                return;
            }else {
                exchangeDiscountCard(card_password);
            }
            break;
        default:
            break;
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        //getMyDiscountCardList();
    }
    /**
     * 兑换优惠券接口
     */
    public void exchangeDiscountCard(String card_password ){
        if (!NetworkUtils.isNetworkConnected(DiscountCardActivity.this)) {
            Toast.makeText(DiscountCardActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }
        user = DBHelper.getUser(this);
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user.getId());
        map.put("card_passwd",card_password);
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().post(Constants.URL_POST_EXCHANGE_DISCOUNT_CARD, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(DiscountCardActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                myDiscountCard = gson.fromJson(data,MyDiscountCard.class);
                                getMyDiscountCardList(page);
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
                    UIUtils.showToast(DiscountCardActivity.this, errorMsg);
                }
            }
        });
    }
    
    /**
     * 获取我的优惠券列表
     */
    public void getMyDiscountCardList(int page) {
        
        if (!NetworkUtils.isNetworkConnected(DiscountCardActivity.this)) {
            Toast.makeText(DiscountCardActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }
        user = DBHelper.getUser(DiscountCardActivity.this);
        Map<String,String> map = new HashMap<String,String>();
        map.put("user_id",user.getId());
        map.put("page",""+page);
        AjaxParams params = new AjaxParams(map);
        showDialog();
        new FinalHttp().get(Constants.URL_GET_MY_DISCOUNT_CARD_LIST, params, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(DiscountCardActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                //json字符串转为集合对象
                                myDiscountCardList = gson.fromJson(data, new TypeToken<ArrayList<MyDiscountCard>>() {
                                }.getType());
                                showData(myDiscountCardList);
//                             adapter.setData(myDiscountCardList);
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
                    mPullRefreshListView.onRefreshComplete();
                    UIUtils.showToast(DiscountCardActivity.this, errorMsg);
                }
            }
        });
    }
    /**
     * 处理数据加载的方法
     * @param list
     */
    private void showData(List<MyDiscountCard> myDiscountCardList){
        if(page==1){
            totalDiscountCardList.clear();
            for (MyDiscountCard myDiscountCard : myDiscountCardList) {
                totalDiscountCardList.add(myDiscountCard);
            }
        }
        if(page>=2){
            for (MyDiscountCard myDiscountCard : myDiscountCardList) {
                totalDiscountCardList.add(myDiscountCard);
            }
        }
        //给适配器赋值
        adapter.setData(totalDiscountCardList);
        mPullRefreshListView.onRefreshComplete();
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MyDiscountCard myDiscountCard = myDiscountCardList.get(position);
        if(flag==1){
            Intent intent = new Intent();
            intent.putExtra("myDiscountCard",myDiscountCard);
            setResult(RESULT_FIRST_USER, intent);
            finish();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        page = 1;
        totalDiscountCardList = new ArrayList<MyDiscountCard>();
        myDiscountCardList = new ArrayList<MyDiscountCard>();
    }
}
