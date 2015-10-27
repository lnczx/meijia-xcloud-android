package com.meijialife.simi.fra;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.Constants;
import com.meijialife.simi.MainActivity;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.AccountInfoActivity;
import com.meijialife.simi.activity.CardDetailsActivity;
import com.meijialife.simi.adapter.ListAdapter;
import com.meijialife.simi.adapter.ListAdapter.onCardUpdateListener;
import com.meijialife.simi.alerm.AlermUtils;
import com.meijialife.simi.bean.Cards;
import com.meijialife.simi.bean.UserIndexData;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.ui.NoScrollViewPager;
import com.meijialife.simi.ui.RoundImageView;
import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;
import com.simi.easemob.ui.EMMainActivity;
import com.simi.easemob.ui.EMMainActivityForTest;

/**
 * 我的
 * 
 * @author RUI
 * 
 */
public class PersonalPageFragment extends Fragment implements OnClickListener, onCardUpdateListener  {
    
    private LayoutInflater inflater;
    private NoScrollViewPager vp_main;
  
    private Button btn_my_send;
    private Button btn_my_in;
    
    private List<View> ViewPagerList = new ArrayList<View>();
    public static int MY_SEND = 0;
    public static int MY_IN = 1;
    public static int card_from = 1;// 1 = 我发布的 2 = 我参与的
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
    private TextView tv_city;       //城市
    private TextView tv_distance;   //距离
    private TextView tv_card_num;   //卡片数量
    private TextView tv_coupon_num;   //优惠券数量
    private TextView tv_friend_num; //好友数量
    
    public static UserIndexData user;
    
    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.index_4, null);
        
//        if(this.getResources().getConfiguration().orientation ==Configuration.ORIENTATION_LANDSCAPE){
//            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//         }
        init(inflater,v);
        
        return v;
    }
    
    private void init(LayoutInflater inflater,View view) {
        finalBitmap = FinalBitmap.create(getActivity());
        defDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_defult_touxiang);

        iv_top_head = (RoundImageView) view.findViewById(R.id.iv_top_head);
        btn_my_send = (Button) view.findViewById(R.id.btn_my_send);
        btn_my_in = (Button) view.findViewById(R.id.btn_my_in);
        btn_my_send.setOnClickListener(titleClickListener);
        btn_my_in.setOnClickListener(titleClickListener);
        tv_top_nickname = (TextView)view.findViewById(R.id.tv_top_nickname);
        tv_city = (TextView)view.findViewById(R.id.tv_city);
        tv_distance = (TextView)view.findViewById(R.id.tv_distance);
        tv_card_num = (TextView)view.findViewById(R.id.tv_card_num);
        tv_coupon_num = (TextView)view.findViewById(R.id.tv_coupon_num);
        tv_friend_num = (TextView)view.findViewById(R.id.tv_friend_num);
        
        iv_top_head.setOnClickListener(this);
        ImageButton ibtn_message = (ImageButton) view.findViewById(R.id.ibtn_message);
        ImageButton ibtn_person = (ImageButton) view.findViewById(R.id.ibtn_person);
        ibtn_message.setOnClickListener(this);
        ibtn_person.setOnClickListener(this);

        vp_main = (NoScrollViewPager) view.findViewById(R.id.vp_main);
        
        inflater = LayoutInflater.from(getActivity());
        View tab_mysend = inflater.inflate(R.layout.item_page_mysend, null, false);
        tv_tips_1 = (TextView) tab_mysend.findViewById(R.id.tv_tips);
        listview_1 = (ListView) tab_mysend.findViewById(R.id.listview);
        adapter_1 = new ListAdapter(getActivity(), this);
        listview_1.setAdapter(adapter_1);
        listview_1.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = new Intent(getActivity(), CardDetailsActivity.class);
                intent.putExtra("Cards", cardlist_1.get(arg2));
                startActivity(intent);
            }
        });
        btn_my_send.performClick();
        
        View tab_myin = inflater.inflate(R.layout.item_page_myin, null, false);
        tv_tips_2 = (TextView) tab_myin.findViewById(R.id.tv_tips);
        listview_2 = (ListView) tab_myin.findViewById(R.id.listview);
        adapter_2 = new ListAdapter(getActivity(), this);
        listview_2.setAdapter(adapter_2);
        listview_2.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = new Intent(getActivity(), CardDetailsActivity.class);
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
        
        getUserData();
    }
    
    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
        case R.id.iv_top_head:
            Intent intent = new Intent(getActivity(),AccountInfoActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            break;
        case R.id.ibtn_message:
            UIUtils.showToast(getActivity(), "点击消息");
//            startActivity(new Intent(getActivity(), EMMainActivity.class));
            break;
        case R.id.ibtn_person:
            MainActivity.slideMenu();
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
                card_from = 1;
                vp_main.setCurrentItem(MY_SEND, false);
                changeViewPager(MY_SEND);
                getCardListData(card_from);
                break;
            case R.id.btn_my_in:    //我参与的
                card_from = 2;
                vp_main.setCurrentItem(MY_IN, false);
                changeViewPager(MY_IN);
                getCardListData(card_from);
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
        String user_id = DBHelper.getUser(getActivity()).getId();

        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id+"");
        map.put("view_user_id", user_id+"");
        AjaxParams param = new AjaxParams(map);

        showDialog();
        new FinalHttp().get(Constants.URL_GET_USER_INDEX, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(getActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
//                                UIUtils.showToast(getActivity(), "数据错误");
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
                    UIUtils.showToast(getActivity(), errorMsg);
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
        tv_city.setText(user.getProvince_name());
        tv_distance.setText("距离你" + user.getPoi_distance() + "米");
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

        String user_id = DBHelper.getUser(getActivity()).getId();

        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("service_date", "");
        map.put("user_id", user_id+"");
        map.put("card_from", card_from+"");
        AjaxParams param = new AjaxParams(map);

        showDialog();
        new FinalHttp().get(Constants.URL_GET_CARD_LIST, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(getActivity(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                    UIUtils.showToast(getActivity(), errorMsg);
                }
            }
        });
    }
    
    private ProgressDialog m_pDialog;
    public void showDialog() {
        if(m_pDialog == null){
            m_pDialog = new ProgressDialog(getActivity());
            m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            m_pDialog.setMessage("请稍等...");
            m_pDialog.setIndeterminate(false);
            m_pDialog.setCancelable(false);
        }
        m_pDialog.show();
    }

    public void dismissDialog() {
        if (m_pDialog != null && m_pDialog.isShowing()) {
            m_pDialog.hide();
        }
    }

    @Override
    public void onCardUpdate() {
        getCardListData(card_from);
    }

}


