package com.meijialife.simi.fra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.BaseFragment;
import com.meijialife.simi.Constants;
import com.meijialife.simi.MainActivity;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.ContactAddFriendsActivity;
import com.meijialife.simi.activity.FindSecretaryActivity;
import com.meijialife.simi.activity.FriendPageActivity;
import com.meijialife.simi.adapter.FriendAdapter;
import com.meijialife.simi.bean.Friend;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * home3
 * @author RUI
 *
 */
public class Home3Fra extends BaseFragment implements OnItemClickListener, OnClickListener{
    
    private RadioGroup radiogroup;//顶部Tab
    private View line_1, line_2;
    
    private LinearLayout layout_msg;    //消息View
    private LinearLayout layout_friend; //秘友View
    
    /** 秘友Tab下所有控件 **/
    private ListView listview;
    private FriendAdapter adapter;
    private ArrayList<Friend> friendList = new ArrayList<Friend>();
    private RelativeLayout rl_add;  //添加通讯录好友
    private RelativeLayout rl_find; //寻找秘书和助理
    
    private int checkedIndex = 1;   //当前选中的Tab位置, 0=消息   1=秘友
    private MainActivity activity;
    
    public Home3Fra(){}
    
    public Home3Fra(MainActivity activity){
        this.activity = activity;
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
//	    if(this.getResources().getConfiguration().orientation ==Configuration.ORIENTATION_LANDSCAPE){
//            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//         }
	    
		View v = inflater.inflate(R.layout.index_3, null);

		init(v);// 初始化
		initTab(v);
		
		return v;
	}
	
	@Override
	public void onResume() {
	    // TODO Auto-generated method stub
	    super.onResume();
	    if(checkedIndex == 0){
	        
	    }else if(checkedIndex == 1){
	        getFriendList(false);
	    }
	}

	private void init(View v) {
	    layout_msg = (LinearLayout)v.findViewById(R.id.layout_msg);
	    layout_friend = (LinearLayout)v.findViewById(R.id.layout_friend);
	    
	    listview = (ListView)v.findViewById(R.id.listview);
        listview.setOnItemClickListener(this);
        adapter = new FriendAdapter(getActivity());
        listview.setAdapter(adapter);
        
        rl_add = (RelativeLayout)v.findViewById(R.id.rl_add);
        rl_find = (RelativeLayout)v.findViewById(R.id.rl_find);
        rl_add.setOnClickListener(this);
        rl_find.setOnClickListener(this);
	}
	
	private void initTab(View v){
        radiogroup = (RadioGroup)v.findViewById(R.id.radiogroup);
        line_1 = (View)v.findViewById(R.id.line_1);
        line_2 = (View)v.findViewById(R.id.line_2);

        radiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(RadioGroup grop, int checkedId) {
                line_1.setVisibility(View.INVISIBLE);
                line_2.setVisibility(View.INVISIBLE);
                
                if(checkedId == grop.getChildAt(0).getId()){
                    /*checkedIndex = 0;
                    line_1.setVisibility(View.VISIBLE);
                    layout_msg.setVisibility(View.VISIBLE);
                    layout_friend.setVisibility(View.GONE);*/
                    
                    //跳转到消息View
                    if(activity != null){
                        ((MainActivity)activity).change2IM();
                    }
                }
                if(checkedId == grop.getChildAt(1).getId()){
                    checkedIndex = 1;
                    line_2.setVisibility(View.VISIBLE);
                    layout_msg.setVisibility(View.GONE);
                    layout_friend.setVisibility(View.VISIBLE);
                    getFriendList(true);
                }
                
            }
        });
        
        radiogroup.getChildAt(1).performClick();
    }
	
	/**
	 * 获取好友列表
	 */
    public void getFriendList(boolean isShowDlg) {

        String user_id = DBHelper.getUser(getActivity()).getId();

        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user_id+"");
        map.put("page", "1");
        AjaxParams param = new AjaxParams(map);

        if(isShowDlg){
            showDialog();
        }
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
                            if(StringUtils.isNotEmpty(data)){
                                Gson gson = new Gson();
                                friendList = gson.fromJson(data, new TypeToken<ArrayList<Friend>>() {
                                }.getType());
                                adapter.setData(friendList);
//                                tv_tips.setVisibility(View.GONE);
                            }else{
                                adapter.setData(new ArrayList<Friend>());
//                                tv_tips.setVisibility(View.VISIBLE);
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

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//        Toast.makeText(getActivity(), ""+friendList.get(arg2).getName(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), FriendPageActivity.class);
        intent.putExtra("friend", friendList.get(arg2));
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.rl_add:   //添加通讯录好友
            Intent intent = new Intent(getActivity(), ContactAddFriendsActivity.class);
            intent.putExtra("friendList", friendList);
            startActivity(intent);
            break;
        case R.id.rl_find:   //寻找秘书和助理
            startActivity(new Intent(getActivity(), FindSecretaryActivity.class));
            break;

        default:
            break;
        }
    }
	
}
