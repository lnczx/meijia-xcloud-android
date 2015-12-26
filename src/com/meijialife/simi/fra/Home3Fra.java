package com.meijialife.simi.fra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
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
import com.meijialife.simi.activity.CompanyListActivity;
import com.meijialife.simi.activity.ContactAddFriendsActivity;
import com.meijialife.simi.activity.ContactSelectActivity;
import com.meijialife.simi.activity.DynamicDetailsActivity;
import com.meijialife.simi.activity.FindSecretaryActivity;
import com.meijialife.simi.activity.FriendPageActivity;
import com.meijialife.simi.activity.WebViewActivity;
import com.meijialife.simi.adapter.FriendAdapter;
import com.meijialife.simi.adapter.FriendDynamicAdapter;
import com.meijialife.simi.adapter.FriendDynamicAdapter.onDynamicUpdateListener;
import com.meijialife.simi.bean.Friend;
import com.meijialife.simi.bean.FriendDynamicData;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;
import com.meijialife.simi.zxing.code.MipcaActivityCapture;

/**
 * @description：好友---消息
 * @author： kerryg
 * @date:2015年12月1日
 */
public class Home3Fra extends BaseFragment implements OnItemClickListener, OnClickListener,onDynamicUpdateListener {

    private RadioGroup radiogroup;// 顶部Tab
    private View line_1, line_2,line_3;

    private LinearLayout layout_msg; // 消息View
    private LinearLayout layout_friend; // 好友View
    private LinearLayout layout_dynamic; // 动态View

    /** 秘友Tab下所有控件 **/
    private ListView listview;
    private FriendAdapter adapter;
    private ArrayList<Friend> friendList = new ArrayList<Friend>();
    private RelativeLayout rl_add; // 添加通讯录好友
    private RelativeLayout rl_find; // 寻找秘书和助理
    private RelativeLayout rl_rq; // 扫一扫加好友
    private RelativeLayout rl_company_contacts;
    private TextView tv_has_company;// 显示理解创建
    private final static int SCANNIN_GREQUEST_CODES = 5;
    
    private ListView lv_friend_dynamic;
    private FriendDynamicAdapter friendDynamicAdapter;//好友动态适配器
    private ArrayList<FriendDynamicData> friendDynamicDatas = new ArrayList<FriendDynamicData>();
    
 
    private int checkedIndex = 0; // 当前选中的Tab位置, 0=消息 1=好友
    
    private MainActivity activity;
    private UserInfo userInfo;// 获取用户详情
    
    
    private RadioButton rb_feed;//动态
    private RadioButton rb_friend;//好友
    private RadioButton rb_msg;//消息
    

    public Home3Fra() {
    }

    public Home3Fra(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // if(this.getResources().getConfiguration().orientation ==Configuration.ORIENTATION_LANDSCAPE){
        // getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // }

        View v = inflater.inflate(R.layout.index_3, null);

        init(v);// 初始化
        initTab(v);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Constants.checkedIndex == 0) {
            getFriendDynamicList();
        } else if (Constants.checkedIndex == 1) {
            getFriendList(false);
        }
    }

    private void init(View v) {
        layout_msg = (LinearLayout) v.findViewById(R.id.layout_msg);
        layout_friend = (LinearLayout) v.findViewById(R.id.layout_friend);
        layout_dynamic = (LinearLayout)v.findViewById(R.id.layout_friend_dynamic);
        tv_has_company = (TextView) v.findViewById(R.id.tv_has_company);

        listview = (ListView) v.findViewById(R.id.listview);
        listview.setOnItemClickListener(this);
        adapter = new FriendAdapter(getActivity());
        listview.setAdapter(adapter);
        
        lv_friend_dynamic = (ListView)v.findViewById(R.id.lv_friend_dynamic);
        friendDynamicAdapter = new FriendDynamicAdapter(getActivity(),this);
        lv_friend_dynamic.setAdapter(friendDynamicAdapter);
        lv_friend_dynamic.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DynamicDetailsActivity.class);
                intent.putExtra("friendDynamic", friendDynamicDatas.get(position));
                startActivity(intent);
            }
        });

        userInfo = DBHelper.getUserInfo(getActivity());
        if (userInfo.getHas_company() == 0) {
            tv_has_company.setVisibility(View.VISIBLE);
        } else {
            tv_has_company.setVisibility(View.GONE);
        }
        rl_add = (RelativeLayout) v.findViewById(R.id.rl_add);
        rl_find = (RelativeLayout) v.findViewById(R.id.rl_find);
        rl_rq = (RelativeLayout) v.findViewById(R.id.rl_rq);
        rl_company_contacts = (RelativeLayout) v.findViewById(R.id.rl_company_contacts);
        rl_add.setOnClickListener(this);
        rl_find.setOnClickListener(this);
        rl_rq.setOnClickListener(this);
        rl_company_contacts.setOnClickListener(this);
    }

    private void initTab(View v) {
        radiogroup = (RadioGroup) v.findViewById(R.id.radiogroup);
        line_1 = (View) v.findViewById(R.id.line_1);
        line_2 = (View) v.findViewById(R.id.line_2);
        line_3 = (View) v.findViewById(R.id.line_3);
        
        rb_feed = (RadioButton)v.findViewById(R.id.rb_dynamic);
        rb_friend = (RadioButton)v.findViewById(R.id.rb_friend);
        rb_msg = (RadioButton)v.findViewById(R.id.rb_msg);

        radiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup grop, int checkedId) {
                line_1.setVisibility(View.INVISIBLE);
                line_2.setVisibility(View.INVISIBLE);
                line_3.setVisibility(View.INVISIBLE);
                
                if (checkedId == rb_feed.getId()) {
                    Constants.checkedIndex = 0;
                    line_1.setVisibility(View.VISIBLE);
                    layout_dynamic.setVisibility(View.VISIBLE);
                    layout_msg.setVisibility(View.GONE);
                    layout_friend.setVisibility(View.GONE);
                    getFriendDynamicList();
                }

                if (checkedId == rb_friend.getId()) {
                    Constants.checkedIndex = 1;
                    line_2.setVisibility(View.VISIBLE);
                    layout_msg.setVisibility(View.GONE);
                    layout_dynamic.setVisibility(View.GONE);
                    layout_friend.setVisibility(View.VISIBLE);
                    getFriendList(false);
                }
                if (checkedId == rb_msg.getId()) {
                    /*
                     * checkedIndex = 1; line_2.setVisibility(View.VISIBLE); layout_msg.setVisibility(View.GONE);
                     * layout_friend.setVisibility(View.VISIBLE); getFriendList(true);
                     */
                    // 跳转到消息View
                    if (activity != null) {
                        ((MainActivity) activity).change2IM();
                    }
                }
            
            }
        });
        radiogroup.getChildAt(Constants.checkedIndex).performClick();
    }

    /**
     * 获取好友列表
     */
    public void getFriendList(boolean isShowDlg) {

        showDialog();
        String user_id = DBHelper.getUser(getActivity()).getId();

        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id + "");
        map.put("page", "1");
        AjaxParams param = new AjaxParams(map);

        showDialog();
        new FinalHttp().get(Constants.URL_GET_FRIENDS, param, new AjaxCallBack<Object>() {
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
                            if (StringUtils.isNotEmpty(data)) {
                                Gson gson = new Gson();
                                friendList = gson.fromJson(data, new TypeToken<ArrayList<Friend>>() {
                                }.getType());
                                adapter.setData(friendList);
                                // tv_tips.setVisibility(View.GONE);
                            } else {
                                adapter.setData(new ArrayList<Friend>());
                                // tv_tips.setVisibility(View.VISIBLE);
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
                    UIUtils.showToast(getActivity(), errorMsg);
                }
            }
        });
    }
  /**
   * 获取好友动态列表接口
   */
    public void getFriendDynamicList() {
        showDialog();
        String user_id = DBHelper.getUser(getActivity()).getId();
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id + "");
        map.put("feed_from", "0");
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().get(Constants.URL_GET_FRIEND_DYNAMIC_LIST, param, new AjaxCallBack<Object>() {
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
                            if (StringUtils.isNotEmpty(data)) {
                                Gson gson = new Gson();
                                friendDynamicDatas = gson.fromJson(data, new TypeToken<ArrayList<FriendDynamicData>>() {
                                }.getType());
                                friendDynamicAdapter.setData(friendDynamicDatas);
                            } else {
                                friendDynamicAdapter.setData(new ArrayList<FriendDynamicData>());
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
                    UIUtils.showToast(getActivity(), errorMsg);
                }
            }
        });
        
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // Toast.makeText(getActivity(), ""+friendList.get(arg2).getName(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), FriendPageActivity.class);
        // intent.putExtra("friend", friendList.get(arg2));
        intent.putExtra("friend_id", friendList.get(arg2).getFriend_id());
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.rl_add: // 添加通讯录好友
            Intent intent = new Intent(getActivity(), ContactAddFriendsActivity.class);
            intent.putExtra("friendList", friendList);
            startActivity(intent);
            
          /*  intent = new Intent(getActivity(), ContactSelectActivity.class);
            startActivityForResult(intent, GET_CONTACTS);*/
            break;
        case R.id.rl_find: // 寻找秘书和助理
            startActivity(new Intent(getActivity(), FindSecretaryActivity.class));
            break;
        case R.id.rl_rq:// 扫一扫加好友
            Intent intents = new Intent();
            intents.setClass(getActivity(), MipcaActivityCapture.class);
            intents.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intents, SCANNIN_GREQUEST_CODES);
            break;
        case R.id.rl_company_contacts:// 企业通讯录
            // 跳转到企业通讯录
            if (userInfo.getHas_company() == 0) {
                Intent intent1 = new Intent(getActivity(), WebViewActivity.class);
                intent1.putExtra("title", "企业通讯录");
                intent1.putExtra("url", Constants.HAS_COMPANY);
                startActivity(intent1);
            } else {
                intent = new Intent(getActivity(),CompanyListActivity.class);
                startActivity(intent);
            }
            break;
        default:
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case SCANNIN_GREQUEST_CODES:
            if (resultCode == (-1)) {
                Bundle bundle = data.getExtras();
                String result = bundle.getString("result").trim();
                if (result.contains(Constants.RQ_TAG_FRIEND)
                        || result.contains(Constants.RQ_TAG_OTHER)) {// 判断是否为云行政二维码
                    if(result.contains(Constants.RQ_TAG_FRIEND)){
                        String str = result.substring(result.indexOf("&")+1,result.length());
                        String friend_id = str.substring(str.indexOf("=")+1,str.indexOf("&"));
                        addFriend(friend_id);
                    }else if (result.contains(Constants.RQ_TAG_OTHER)) {
                        Toast.makeText(getActivity(), "请扫描添加好友二维码", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "非本APP内容，可能存在风险", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 添加好友接口
     * 
     * @param friend_id
     */
    public void addFriend(final String friend_id) {

        String user_id = DBHelper.getUser(getActivity()).getId();

        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id + "");
        map.put("friend_id", friend_id);
        AjaxParams param = new AjaxParams(map);

        showDialog();
        new FinalHttp().get(Constants.URL_GET_ADD_FRIEND, param, new AjaxCallBack<Object>() {
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
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) { // 正确
                            // 添加成功，跳转到好友界面
                            Intent intent = new Intent(getActivity(), FriendPageActivity.class);
                            intent.putExtra("friend_id", friend_id);
                            startActivity(intent);
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
                    UIUtils.showToast(getActivity(), errorMsg);
                }
            }
        });
    }

    @Override
    public void onDynamicUpdate() {
        getFriendDynamicList();        
    }

}
