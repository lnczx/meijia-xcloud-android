package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.ListAdapter;
import com.meijialife.simi.adapter.ListAdapter.onCardUpdateListener;
import com.meijialife.simi.bean.Cards;
import com.meijialife.simi.bean.Friend;
import com.meijialife.simi.bean.UserIndexData;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.ui.NoScrollViewPager;
import com.meijialife.simi.ui.RoundImageView;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;
import com.simi.easemob.EMConstant;
import com.simi.easemob.ui.ChatActivity;

/**
 * 好友的个人中心页
 * 
 * 
 */
public class FriendPageActivity extends BaseActivity implements OnClickListener, onCardUpdateListener  {
    
    private LayoutInflater inflater;
    private NoScrollViewPager vp_main;
  
    private Button btn_my_send;
    private Button btn_my_in;
    
    private List<View> ViewPagerList = new ArrayList<View>();
    public static int MY_SEND = 0;
    public static int MY_IN = 1;
    private int current_pageIndex = MY_SEND;
    
    /** 我发布的list控件**/
    private ListView listview_1;
    private ListAdapter adapter_1;
    private ArrayList<Cards> cardlist_1;//卡片数据
    private TextView tv_tips_1;//没有数据时的提示
    
    /** 我参与的list控件**/
    private ListView listview_2;
    private ListAdapter adapter_2;
    private ArrayList<Cards> cardlist_2;//卡片数据
    private TextView tv_tips_2;//没有数据时的提示
    
    TitleClickListener titleClickListener=new TitleClickListener();
    private RoundImageView iv_top_head;
    
    private TextView tv_top_nickname;//昵称
    private TextView btn_add;       //添加好友
    private TextView btn_msg;       //私聊
    private TextView tv_card_num;   //卡片数量
    private TextView tv_coupon_num;   //优惠券数量
    private TextView tv_friend_num; //好友数量
    private RelativeLayout rl_top;//好友主页背景
    
    private UserIndexData user;
    
    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    
    private Friend friend;//当前查看的好友数据
    private String friend_id;//获取好友Id
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        setContentView(R.layout.friend_page_activity);
        super.onCreate(savedInstanceState);
        
        init(inflater);
        getUserData();
    }
    
    private void init(LayoutInflater inflater) {
        //friend = (Friend)getIntent().getSerializableExtra("friend");
        friend_id = getIntent().getStringExtra("friend_id");

        setTitleName("个人主页");
        requestBackBtn();
        
        finalBitmap = FinalBitmap.create(FriendPageActivity.this);
        defDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_defult_touxiang);
        
        //设置好友主页背景
        rl_top = (RelativeLayout)findViewById(R.id.rl_top);
        finalBitmap.display(rl_top,Constants.FRIEND_ICON_URL);
        
        iv_top_head = (RoundImageView) findViewById(R.id.iv_top_head);
        btn_my_send = (Button) findViewById(R.id.btn_my_send);
        btn_my_in = (Button) findViewById(R.id.btn_my_in);
        btn_my_send.setOnClickListener(titleClickListener);
        btn_my_in.setOnClickListener(titleClickListener);
        tv_top_nickname = (TextView)findViewById(R.id.tv_top_nickname);
        btn_add = (TextView)findViewById(R.id.tv_add);
        btn_msg = (TextView)findViewById(R.id.tv_msg);
        tv_card_num = (TextView)findViewById(R.id.tv_card_num);
        tv_coupon_num = (TextView)findViewById(R.id.tv_coupon_num);
        tv_friend_num = (TextView)findViewById(R.id.tv_friend_num);
        
        iv_top_head.setOnClickListener(this);
        //btn_add.setOnClickListener(this);
        btn_msg.setOnClickListener(this);

        vp_main = (NoScrollViewPager) findViewById(R.id.vp_main);
        
        inflater = LayoutInflater.from(FriendPageActivity.this);
        View tab_mysend = inflater.inflate(R.layout.item_page_mysend, null, false);
        tv_tips_1 = (TextView) tab_mysend.findViewById(R.id.tv_tips);
        listview_1 = (ListView) tab_mysend.findViewById(R.id.listview);
        adapter_1 = new ListAdapter(FriendPageActivity.this, this);
        listview_1.setAdapter(adapter_1);
        listview_1.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = new Intent(FriendPageActivity.this, CardDetailsActivity.class);
                intent.putExtra("Cards", cardlist_1.get(arg2));
                startActivity(intent);
            }
        });
        btn_my_send.performClick();
        
        View tab_myin = inflater.inflate(R.layout.item_page_myin, null, false);
        tv_tips_2 = (TextView) tab_myin.findViewById(R.id.tv_tips);
        listview_2 = (ListView) tab_myin.findViewById(R.id.listview);
        adapter_2 = new ListAdapter(FriendPageActivity.this, this);
        listview_2.setAdapter(adapter_2);
        listview_2.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = new Intent(FriendPageActivity.this, CardDetailsActivity.class);
                intent.putExtra("Cards", cardlist_2.get(arg2));
                startActivity(intent);
            }
        });

        ViewPagerList.add(tab_mysend);
        ViewPagerList.add(tab_myin);
        
        // behind the init
        changeViewPager(current_pageIndex);
        vp_main.setCurrentItem(current_pageIndex, false);
        
        PagerAdapter adapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return ViewPagerList.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(ViewPagerList.get(position));
            }

            @Override
            public int getItemPosition(Object object) {

                return super.getItemPosition(object);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(ViewPagerList.get(position));
                return ViewPagerList.get(position);
            }
        };
        vp_main.setAdapter(adapter);
   
    }
    
    @Override
    public void onResume() {
        super.onResume();
    }
    
    @Override
    public void onClick(View arg0) {
        Intent intent;
        switch (arg0.getId()) {
        case R.id.iv_top_head:
//            intent = new Intent(FriendPageActivity.this,AccountInfoActivity.class);
//            intent.putExtra("user", user);
//            startActivity(intent);
            break;
        case R.id.tv_add:
            Toast.makeText(this, "添加", Toast.LENGTH_SHORT).show();
            break;
        case R.id.tv_msg:
            //进入聊天页面
            if(user == null){
                Toast.makeText(FriendPageActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                return;
            }
            String nickName = user.getName();
            if(StringUtils.isEmpty(nickName.trim())){
                nickName = user.getMobile();
            }
            
            intent = new Intent(FriendPageActivity.this, ChatActivity.class);
            intent.putExtra(EMConstant.EXTRA_USER_ID, user.getIm_user_name());
            intent.putExtra(EMConstant.EXTRA_USER_NAME, nickName);
            startActivity(intent);
            break;

        default:
            break;
        }
    }

    class TitleClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.btn_my_send:  //我发布的
                vp_main.setCurrentItem(MY_SEND, false);
                changeViewPager(MY_SEND);
                getCardListData(1);
                break;
            case R.id.btn_my_in:    //我参与的
                vp_main.setCurrentItem(MY_IN, false);
                changeViewPager(MY_IN);
                getCardListData(2);
                break;
            default:
                break;
            }
        }
    }

    private void changeViewPager(int pos) {
        if (pos == MY_SEND) {
            btn_my_send.setSelected(true);
            btn_my_in.setSelected(false);
              
        } else if (pos == MY_IN) {
            
            btn_my_send.setSelected(false);
            btn_my_in.setSelected(true);

        }
        current_pageIndex = pos;
    }
    
    /**
     * 获取个人信息数据
     */
    private void getUserData() {
        String user_id = DBHelper.getUser(FriendPageActivity.this).getId();

        if (!NetworkUtils.isNetworkConnected(FriendPageActivity.this)) {
            Toast.makeText(FriendPageActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id+"");
//        map.put("view_user_id", friend.getFriend_id()+"");
        map.put("view_user_id", friend_id);
        AjaxParams param = new AjaxParams(map);

        showDialog();
        new FinalHttp().get(Constants.URL_GET_USER_INDEX, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(FriendPageActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                dismissDialog();
                LogOut.i("========", "onSuccess：" + t);
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            if(StringUtils.isNotEmpty(data)){
                                Gson gson = new Gson();
                                user = gson.fromJson(data, UserIndexData.class);
                                showData();
                            }else{
//                                UIUtils.showToast(FriendPageActivity.this, "数据错误");
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
                // 操作失败，显示错误信息|
                if(!StringUtils.isEmpty(errorMsg.trim())){
                    UIUtils.showToast(FriendPageActivity.this, errorMsg);
                }
            }
        });

    }
    
    private void showData(){
        if(user == null){
            return;
        }
        
        String nickName = user.getName();
        if(StringUtils.isEmpty(nickName.trim())){
            nickName = user.getMobile();
        }
        
        tv_top_nickname.setText(nickName);
        tv_card_num.setText(user.getTotal_card()+"");
        tv_coupon_num.setText(user.getTotal_coupon()+"");
        tv_friend_num.setText(user.getTotal_friends()+"");
        
        finalBitmap.display(iv_top_head, user.getHead_img(), defDrawable.getBitmap(), defDrawable.getBitmap());
    }
    
    /**
     * 获取卡片数据
     * @param card_from 0 = 所有卡片  1 = 我发布的 2 = 我参与的,默认为0
     */
    private void getCardListData(final int card_from) {

//        String user_id = DBHelper.getUser(FriendPageActivity.this).getId();

        if (!NetworkUtils.isNetworkConnected(FriendPageActivity.this)) {
            Toast.makeText(FriendPageActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("service_date", "");
        map.put("user_id", friend_id);
        map.put("card_from", card_from+"");
        AjaxParams param = new AjaxParams(map);

        showDialog();
        new FinalHttp().get(Constants.URL_GET_CARD_LIST, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(FriendPageActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String errorMsg = "";
                dismissDialog();
                LogOut.i("========", "onSuccess：" + t);
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            if(StringUtils.isNotEmpty(data)){
                                Gson gson = new Gson();
                                if (card_from == 1) {
                                    // 我发布的
                                    cardlist_1 = gson.fromJson(data, new TypeToken<ArrayList<Cards>>() {
                                    }.getType());
                                    adapter_1.setData(cardlist_1);
                                    tv_tips_1.setVisibility(View.GONE);
                                }else if(card_from == 2){
                                    //我参与的
                                    cardlist_2 = gson.fromJson(data, new TypeToken<ArrayList<Cards>>() {
                                    }.getType());
                                    adapter_2.setData(cardlist_2);
                                    tv_tips_2.setVisibility(View.GONE);
                                }
                            }else{
                                if (card_from == 1) {
                                 // 我发布的
                                    adapter_1.setData(new ArrayList<Cards>());
                                    tv_tips_1.setVisibility(View.VISIBLE);
                                }else if(card_from == 2){
                                    //我参与的
                                    adapter_2.setData(new ArrayList<Cards>());
                                    tv_tips_2.setVisibility(View.VISIBLE);
                                }
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
                if(!StringUtils.isEmpty(errorMsg.trim())){
                    UIUtils.showToast(FriendPageActivity.this, errorMsg);
                }
            }
        });

    }

    @Override
    public void onCardUpdate() {
        // TODO Auto-generated method stub
        
    }

}


