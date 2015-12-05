package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.android.phone.mrpc.core.p;
import com.alipay.android.phone.mrpc.core.t;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.ContactsAdapter;
import com.meijialife.simi.bean.ContactBean;
import com.meijialife.simi.bean.Friend;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * @description：卡片中选择联系人
 * @author： kerryg
 * @date:2015年11月27日
 */
public class ContactChooseActivity extends Activity implements OnClickListener {

    ArrayList<String> getcontactsList; // 选择得到联系人

    private RelativeLayout rl_company_contacts;// 企业通讯录
    private RelativeLayout rl_friend_contacts;// 好友通讯录
    private ArrayList<Friend> friendList = new ArrayList<Friend>();
    private TextView tv_contacts_list;// 显示勾选联系人

    private ListView listview;
    private ContactsAdapter adapter;
    private HashMap<Integer, Boolean> isSelected;
    private CheckBox cb;
    private TextView tv_name;
    private TextView tv_mobile;
    private TextView tv_id;
    private HashMap<Integer,String> currentMap;//
    private HashMap<Integer,String> contactBeanMap;
    private ArrayList<String> list;

    @SuppressLint("UseSparseArrays")
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_choose_list);

        ImageView title_btn_left = (ImageView) findViewById(R.id.title_btn_left);
        TextView header_tv_name = (TextView) findViewById(R.id.header_tv_name);
        title_btn_ok = (TextView) findViewById(R.id.title_btn_ok);
        rl_company_contacts = (RelativeLayout) findViewById(R.id.rl_company_contacts);
        rl_friend_contacts = (RelativeLayout) findViewById(R.id.rl_friend_contacts);
        tv_contacts_list = (TextView) findViewById(R.id.tv_contacts_list);

        title_btn_ok.setOnClickListener(this);
        title_btn_left.setOnClickListener(this);
        rl_company_contacts.setOnClickListener(this);
        rl_friend_contacts.setOnClickListener(this);
        
        contactList = new ArrayList<String>();
        currentMap = new HashMap<Integer,String>();
        getcontactIntList = new ArrayList<Integer>();
        contactBeanMap = new HashMap<Integer,String>();
        list = new ArrayList<String>();
        
        
        currentMap= (HashMap<Integer, String>)getIntent().getSerializableExtra("currentMap");
        list= getIntent().getStringArrayListExtra("list");
        contactBeanMap= (HashMap<Integer, String>)getIntent().getSerializableExtra("contactBeanMap");
        contactList= getIntent().getStringArrayListExtra("contactList");

        StringBuilder sb = new StringBuilder();
        Iterator iter = currentMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            sb.append(entry.getValue()+",");
        }
        tv_contacts_list.setText(sb.toString());
        
        header_tv_name.setText("选择接收人");
        title_btn_left.setVisibility(View.VISIBLE);
        listview = (ListView) findViewById(R.id.lv_list_view);
        adapter = new ContactsAdapter(this);
        isSelected = adapter.getIsSelected();
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listview.setAdapter(adapter);
        listview.setItemsCanFocus(false);
        
      

        getFriendList(currentMap);

        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cb = (CheckBox) view.findViewById(R.id.cb_check_box);
                tv_name = (TextView) view.findViewById(R.id.item_tv_name);
                tv_mobile = (TextView)view.findViewById(R.id.item_tv_mobile);
                tv_id = (TextView) view.findViewById(R.id.item_tv_id);
                if (cb.isChecked() == false) {
                    cb.setChecked(true);
                    ContactBean contactBean = new ContactBean();
                    String name = tv_name.getText().toString().trim();
                    String mobile = tv_mobile.getText().toString().trim();
                    contactBean.setName(name);
                    contactBean.setMobile(mobile);
                    contactBeanMap.put(position,name+"\n"+mobile);
                    currentMap.put(position,name);
                } else {
                    cb.setChecked(false);
                    currentMap.remove(position);
                    contactBeanMap.remove(position);
                }
                //name+mobile
                list = new ArrayList<String>();
                changeList(contactList,list);//通讯录同步
                mapToList(contactBeanMap, list);//好友同步
               
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        StringBuilder sb = new StringBuilder();
                        String name = null;
                        for (int i = 0; i < list.size(); i++) {
                            String bean = list.get(i).toString();
                            if (name != null) {
                                name = bean.substring(0, bean.indexOf("\n"));
                            } else {
                                name = bean.substring(0, bean.indexOf("\n"));
                            }
                            sb.append(name+",");
                        }
                        tv_contacts_list.setText(sb.toString());
                    }
                });
            }
        });
    }

    private void changeList(ArrayList<String> orgin,ArrayList<String> target){
        for (int i = 0; i < orgin.size(); i++) {
            target.add(orgin.get(i));
        }
    }
    private void mapToList(HashMap<Integer,String> map,ArrayList<String> list){
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            list.add(entry.getValue().toString());
        }
    }
       
    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
        case R.id.rl_company_contacts:// 企业通讯录
            break;
        case R.id.rl_friend_contacts:// 选择接收人--->选择联系人
            contactList.clear();    
            intent = new Intent(this, ContactSelectActivity.class);
            intent.putIntegerArrayListExtra("getcontactIntList",getcontactIntList);
            startActivityForResult(intent, GET_CONTACTS);
            break;
        case R.id.title_btn_ok:// 确定按钮
            postContactData();
            break;
        case R.id.title_btn_left:// 左侧返回按钮
            if (list != null && list.size() > 0) {
                intent = new Intent();
                intent.putExtra("currentMap", currentMap);
                intent.putStringArrayListExtra("list", list);
                intent.putExtra("contactBeanMap",contactBeanMap);
                intent.putStringArrayListExtra("contactList",contactList);
                setResult(RESULT_OK, intent);
                ContactChooseActivity.this.finish();
            }
            break;
        default:
            break;
        }

    }

    public static final int GET_CONTACTS = 1003;
    private ArrayList<String> contactList;
    private ArrayList<Integer> getcontactIntList;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
            case GET_CONTACTS:
                contactList = data.getExtras().getStringArrayList("contact");
                getcontactIntList = data.getExtras().getIntegerArrayList("getcontactIntList");
                if (contactList != null && contactList.size() > 0) {
                    //name+mobile
                    list = new ArrayList<String>();
                    mapToList(contactBeanMap, list);//好友同步
                    changeList(contactList,list);//通讯录同步
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            StringBuilder sb = new StringBuilder();
                            String name = null;
                            for (int i = 0; i < list.size(); i++) {
                                String bean = list.get(i).toString();
                                if (name != null) {
                                    name = bean.substring(0, bean.indexOf("\n"));
                                } else {
                                    name = bean.substring(0, bean.indexOf("\n"));
                                }
                                sb.append(name+",");
                            }
                            tv_contacts_list.setText(sb.toString());
                        }
                    });
                }
                break;
            default:
                break;
            }
        }
    }

    /**
     * 获取好友列表
     * 
     * @param isShowDlg
     */
    public void getFriendList(final HashMap<Integer,String> hashMap) {
        String user_id = DBHelper.getUser(ContactChooseActivity.this).getId();

        if (!NetworkUtils.isNetworkConnected(ContactChooseActivity.this)) {
            Toast.makeText(ContactChooseActivity.this, getString(R.string.net_not_open), 0).show();
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
                Toast.makeText(ContactChooseActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                adapter.setData(friendList,hashMap);
                            } else {
                                adapter.setData(friendList,hashMap);
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
                    UIUtils.showToast(ContactChooseActivity.this, errorMsg);
                }
            }
        });

    }

    public void postContactData() {
        if (list.size() > 10) {
            UIUtils.showToast(this, "您最多可以选择10个联系人哦");
        } else {
            Intent intent = new Intent();
            intent.putExtra("currentMap", currentMap);
            intent.putStringArrayListExtra("list", list);
            intent.putExtra("contactBeanMap",contactBeanMap);
            intent.putStringArrayListExtra("contactList",contactList);
            setResult(RESULT_OK, intent);
            ContactChooseActivity.this.finish();
        }
    }

    private ProgressDialog m_pDialog;
    private TextView title_btn_ok;

    public void showDialog() {
        if (m_pDialog == null) {
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

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        currentMap.clear();
        list.clear();
        contactBeanMap.clear();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            postContactData();
        }
        return super.onKeyDown(keyCode, event);
    }
}
