package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.ListAdapter;
import com.meijialife.simi.adapter.ListAdapter.onCardUpdateListener;
import com.meijialife.simi.bean.CardExtra;
import com.meijialife.simi.bean.Cards;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * @description：应用中心卡片列表
 * @author： kerryg
 * @date:2016年3月3日 
 */
public class CardListActivity extends Activity implements onCardUpdateListener{
    

    private ListView listview;
    private ListAdapter adapter;
    private ArrayList<Cards> cardlist;// 卡片数据
    private ArrayList<CardExtra> cardExtrasList;
    
    private String mToday;//今天日期(格式：yyyy-MM-dd)
    
    private ImageView mCardBack;
    private TextView mCardTitle;
    private LinearLayout mLlCard;
    private RelativeLayout mRlCard;
    private LinearLayout mAffairCardTitle;
    
    //创建卡片
    private TextView mTvCreate;
    private LinearLayout mLlCreate;
    private HashMap<String,String> mCardTitleColor;
    private String mCardType;
    
    //下拉刷新
    private ArrayList<Cards> totalCardList;
    private PullToRefreshListView mPullRefreshListView;//上拉刷新的控件 
    private int page = 1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.card_list_activity);
        super.onCreate(savedInstanceState);
        
        initView();
        
    }
    
    private void initView(){
        
        //接收参数
        mCardType = getIntent().getStringExtra("cardType");

        mLlCard = (LinearLayout)findViewById(R.id.m_ll_card);
        mRlCard = (RelativeLayout)findViewById(R.id.view_card_title_bar);
        //新建(控件)
        mTvCreate = (TextView)findViewById(R.id.m_tv_create_card);
        mLlCreate = (LinearLayout)findViewById(R.id.m_ll_create_card);
        mTvCreate.setText("新建");
        //标题+返回(控件)
        mCardBack = (ImageView) findViewById(R.id.m_iv_card_back);
        mCardTitle = (TextView) findViewById(R.id.m_tv_card_title);
        mAffairCardTitle = (LinearLayout)findViewById(R.id.m_affair_card_title);
       
        setOnClick();//设置点击事件
        setCardTitleColor(mCardType);//设置标题颜色
        setTitleBarColor();//设置沉浸栏样式
        
        Date date = new Date();
        mToday = DateUtils.getStringByPattern(date.getTime(), "yyyy-MM-dd");
        initDiscountView();
       
    }
    
    private void initDiscountView(){
        totalCardList = new ArrayList<Cards>();
        mPullRefreshListView = (PullToRefreshListView)findViewById(R.id.m_card_list);
        adapter = new ListAdapter(CardListActivity.this, this);
        mPullRefreshListView.setAdapter(adapter);
        mPullRefreshListView.setMode(Mode.BOTH);
        initIndicator();
        getCardListData(page,mToday, mCardType);
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //下拉刷新任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                page = 1;
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getCardListData(page,mToday, mCardType);
                adapter.notifyDataSetChanged(); 
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载任务
                String label = DateUtils.getStringByPattern(System.currentTimeMillis(),
                        "MM_dd HH:mm");
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if(cardlist!=null && cardlist.size()>=10){
                    page = page+1;
                    getCardListData(page,mToday, mCardType);
                    adapter.notifyDataSetChanged(); 
                }else {
                    Toast.makeText(CardListActivity.this,"请稍后，没有更多加载数据",Toast.LENGTH_SHORT).show();
                    mPullRefreshListView.onRefreshComplete(); 
                }
            }
        });
        mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!cardlist.get(position).getCard_type().equals("99")){
                    Intent intent = new Intent(CardListActivity.this, CardDetailsActivity.class);
                    intent.putExtra("card_id", cardlist.get(position).getCard_id());
                    intent.putExtra("Cards", cardlist.get(position));
                    intent.putExtra("card_extra",cardExtrasList.get(position));
                    startActivity(intent);
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
    
    /*
     * 设置点击事件
     */
    private void setOnClick(){
        mCardBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mLlCreate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mCardType) {
                case "1"://会议安排
                  startActivity(new Intent(CardListActivity.this, MainPlusMeettingActivity.class));
                    break;
                case "2"://通知公告
                  startActivity(new Intent(CardListActivity.this, MainPlusMorningActivity.class));
                    break;
                case "3"://事务提醒
                  startActivity(new Intent(CardListActivity.this, MainPlusAffairActivity.class));
                    break;
                case "4"://面试邀约
                  startActivity(new Intent(CardListActivity.this, MainPlusNotificationActivity.class));
                    break;
                case "5"://差旅规划
                  startActivity(new Intent(CardListActivity.this, MainPlusTravelActivity.class));
                    break;
                default:
                    break;
                }                
            }
        });
        
        mAffairCardTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent ;
                switch (mCardType) {
                case "1"://会议安排
                    intent = new Intent(CardListActivity.this, WebViewsActivity.class);
                    intent.putExtra("url", Constants.CARD_MEETING_HELP_URL);
                    startActivity(intent);
                    break;
                case "2"://通知公告
                    intent = new Intent(CardListActivity.this, WebViewsActivity.class);
                    intent.putExtra("url", Constants.CARD_NOTICE_HELP_URL);
                    startActivity(intent);
                    break;
                case "3"://事务提醒
                    intent = new Intent(CardListActivity.this, WebViewsActivity.class);
                    intent.putExtra("url", Constants.CARD_ALARM_HELP_URL);
                    startActivity(intent);
                    break;
                case "4"://面试邀约
                    intent = new Intent(CardListActivity.this, WebViewsActivity.class);
                    intent.putExtra("url", Constants.CARD_INTERVIEW_HELP_URL);
                    startActivity(intent);
                    break;
                case "5"://差旅规划
                    intent = new Intent(CardListActivity.this, WebViewsActivity.class);
                    intent.putExtra("url", Constants.CARD_TRIP_HELP_URL);
                    startActivity(intent);
                    break;
                default:
                    break;
                }   
            }
        });
    }
    /**
     * 设置沉浸栏样式
     */
    private void setTitleBarColor(){
        /**
         * 沉浸栏方式实现(android4.4以上)
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {// 4.4以上
            // 透明状态栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        
    }
    
    /**
     * 设置标题颜色
     * @param cardType
     */
    private void setCardTitleColor(String cardType){
        switch (cardType) {
        case "1"://会议安排
            mCardTitle.setText("会议安排");
//            mTvCreate.setText("会议安排");
            mLlCreate.setBackgroundColor(getResources().getColor(R.color.card_hui_yi));
            mLlCard.setBackgroundColor(getResources().getColor(R.color.card_hui_yi));
            mRlCard.setBackgroundColor(getResources().getColor(R.color.card_hui_yi));
            break;
        case "2"://通知公告
            mCardTitle.setText("通知公告");
//            mTvCreate.setText("通知公告");
            mLlCreate.setBackgroundColor(getResources().getColor(R.color.card_tong_zhi));
            mLlCard.setBackgroundColor(getResources().getColor(R.color.card_tong_zhi));
            mRlCard.setBackgroundColor(getResources().getColor(R.color.card_tong_zhi));
            break;
        case "3"://事务提醒
            mCardTitle.setText("事务提醒");
//            mTvCreate.setText("事务提醒");
            mLlCreate.setBackgroundColor(getResources().getColor(R.color.card_shi_wu));
            mLlCard.setBackgroundColor(getResources().getColor(R.color.card_shi_wu));
            mRlCard.setBackgroundColor(getResources().getColor(R.color.card_shi_wu));
            break;
        case "4"://面试邀约
            mCardTitle.setText("面试邀约");
//            mTvCreate.setText("面试邀约");
            mLlCreate.setBackgroundColor(getResources().getColor(R.color.card_mian_shi));
            mLlCard.setBackgroundColor(getResources().getColor(R.color.card_mian_shi));
            mRlCard.setBackgroundColor(getResources().getColor(R.color.card_mian_shi));
            break;
        case "5"://差旅规划
            mCardTitle.setText("差旅规划");
//            mTvCreate.setText("差旅规划");
            mLlCreate.setBackgroundColor(getResources().getColor(R.color.card_chai_lv));
            mLlCard.setBackgroundColor(getResources().getColor(R.color.card_chai_lv));
            mRlCard.setBackgroundColor(getResources().getColor(R.color.card_chai_lv));
            break;
        default:
            break;
        }
        
    }
    
    private Map<String,String> setCardTitleColor(){
        mCardTitleColor.put("1","#56abe4");//会议安排
        mCardTitleColor.put("2","#56abe4");//通知公告
        mCardTitleColor.put("3","#56abe4");//事务提醒
        mCardTitleColor.put("4","#56abe4");//面试邀约
        mCardTitleColor.put("5","#56abe4");//差旅规划
        return mCardTitleColor;
    }

    @Override
    public void onCardUpdate() {
        getCardListData(page,mToday, mCardType);        
    }
    
    /**
     * 获取卡片数据
     * 
     * @param date
     * @param card_from
     *            0 = 所有卡片 1 = 我发布的 2 = 我参与的,默认为0
     */
    public void getCardListData(int page,String date, String card_type) {

        String user_id = DBHelper.getUser(this).getId();

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("service_date", "");
        map.put("user_id", user_id + "");
        map.put("card_from", "0" );
        map.put("card_type", card_type);
        map.put("lat","");
        map.put("lng", "");
        map.put("page",page+"");
        AjaxParams param = new AjaxParams(map);

//        showDialog();
        new FinalHttp().get(Constants.URL_GET_CARD_LIST, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
//                dismissDialog();
                Toast.makeText(CardListActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
//                dismissDialog();
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data").trim();
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            if (StringUtils.isNotEmpty(data.trim())) {
                                Gson gson = new Gson();
                                cardlist = new ArrayList<Cards>();
                                cardExtrasList = new ArrayList<CardExtra>();
                                cardlist = gson.fromJson(data, new TypeToken<ArrayList<Cards>>() {
                                }.getType());
                                showData(cardlist,gson);
                            } else {
                                adapter.setData(new ArrayList<Cards>(),new ArrayList<CardExtra>());
                                mPullRefreshListView.onRefreshComplete();
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
                    UIUtils.showToast(CardListActivity.this, errorMsg);
                }
            }
        });
    }
    
    /**
     * 处理数据加载的方法
     * @param list
     */
    private void showData(List<Cards> myCardList,Gson gson){
        if(page==1){
            totalCardList.clear();
            cardExtrasList.clear();
            for (Cards card : myCardList) {
                totalCardList.add(card);
            }
            for (int i = 0; i < cardlist.size(); i++) {
                Cards cards2 = cardlist.get(i);
                CardExtra cardExtra = new CardExtra();
               cardExtra = gson.fromJson(cards2.getCard_extra(),CardExtra.class);
               cardExtrasList.add(cardExtra);
            }
        }
        if(page>=2){
            for (Cards card : myCardList) {
                totalCardList.add(card);
            }
            for (int i = 0; i < cardlist.size(); i++) {
                Cards cards2 = cardlist.get(i);
                CardExtra cardExtra = new CardExtra();
               cardExtra = gson.fromJson(cards2.getCard_extra(),CardExtra.class);
               cardExtrasList.add(cardExtra);
            }
        }
        //给适配器赋值
        adapter.setData(totalCardList,cardExtrasList);
        mPullRefreshListView.onRefreshComplete();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        page =1;
        totalCardList = new ArrayList<Cards>();
        cardlist = new ArrayList<Cards>();
        cardExtrasList = new ArrayList<CardExtra>();
        
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        getCardListData(page,mToday, mCardType);
    }

}
