package com.meijialife.simi.activity;

import java.util.ArrayList;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.ContactSelectAdapter;
import com.meijialife.simi.bean.Contact;
import com.meijialife.simi.bean.Friend;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.GetContactsRunnable;
import com.meijialife.simi.utils.SpFileUtil;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;
/**
 * @description：创建卡片选择通讯录好友
 * @author： kerryg
 * @date:2015年12月9日 
 */
public class ContactSelectActivity extends Activity {

    private final int UPDATE_LIST = 1;
    
    ArrayList<Contact> contactList;//获得联系人列表实体
    
    private ContactSelectAdapter companyAdapter;
    private ListView listView;
    /**
     * Item中控件布局声明
     */
    private CheckBox cb;
    private TextView tv_name;
    private TextView tv_mobile;
    private TextView tv_id;
    private TextView tv_temp;

    User user;
    Handler updateListHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case UPDATE_LIST:
                if (m_pDialog != null) {
                    dismissDialog();
                }
                updateList();
            }
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_contactslist);
        user = DBHelper.getUser(this);

        companyAdapter = new ContactSelectAdapter(this);
        contactList = new ArrayList<Contact>();

        ImageView title_btn_left = (ImageView) findViewById(R.id.title_btn_left);
        TextView header_tv_name = (TextView) findViewById(R.id.header_tv_name);
        title_btn_ok = (TextView) findViewById(R.id.title_btn_ok);
        header_tv_name.setText("选择联系人");
        title_btn_left.setVisibility(View.VISIBLE);
        title_btn_left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //&& Constants.finalContactList.size() > 0
                if (Constants.TEMP_FRIENDS != null ) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    ContactSelectActivity.this.finish();
                }
            }
        });

        listView = (ListView)findViewById(R.id.lv_contact_listview);
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(companyAdapter);

        getContacts();
        
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> l, View view, int position, long id) {
              
                cb = (CheckBox) view.findViewById(R.id.cb_check_box);
                tv_name = (TextView) view.findViewById(R.id.item_tv_name);
                tv_mobile = (TextView)view.findViewById(R.id.item_tv_mobile);
                tv_id = (TextView) view.findViewById(R.id.item_tv_id);
                tv_temp = (TextView)view.findViewById(R.id.item_tv_temp);
                
                Contact contact = contactList.get(position);
                if(cb.isChecked()){
                    SpFileUtil.saveBoolean(ContactSelectActivity.this, SpFileUtil.KEY_CHECKED_FRIENDS,contact.getPhoneNum(),false);
                    for (int i = 0; i < Constants.TEMP_FRIENDS.size(); i++) {
                        Friend friend2 = Constants.TEMP_FRIENDS.get(i);
                        if (StringUtils.isEquals(contact.getPhoneNum(), friend2.getMobile())) {
                            Constants.TEMP_FRIENDS.remove(i);
                            --i;
                        }
                    }
                }else {
                    SpFileUtil.saveBoolean(ContactSelectActivity.this, SpFileUtil.KEY_CHECKED_FRIENDS,contact.getPhoneNum(),true);
                    Friend friend = new Friend(contact.getContactId(),contact.getName(),contact.getPhoneNum());
                    Constants.TEMP_FRIENDS.add(friend); 
                }
                companyAdapter.notifyDataSetChanged();
            }
        });
        title_btn_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                postContactData();
            }
        });
    }
    private void getContacts() {
        ArrayList<Contact> contacts = (ArrayList<Contact>) DBHelper.getContacts(this);
        if (contacts == null || contacts.size() == 0) {
            Thread getContactsThread = new Thread(new GetContactsRunnable(this, updateListHandler));
            getContactsThread.start();
            showDialog();
        } else {
            updateList();
        }
    }

    public void postContactData() {
        if (Constants.TEMP_FRIENDS.size() > 10) {
            UIUtils.showToast(this, "您最多可以选择10个联系人哦");
        } else {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            ContactSelectActivity.this.finish();
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

    void updateList() {
        ArrayList<Contact> contacts = (ArrayList<Contact>) DBHelper.getContacts(this);
        for (int i = 0; i < contacts.size(); i++) {
            Contact contact = new Contact(contacts.get(i).getContactId(),contacts.get(i).getName(),contacts.get(i).getPhoneNum());
            contactList.add(contact);
        }
        if (contactList != null)
            companyAdapter.setData(contactList, Constants.TEMP_FRIENDS);
//            setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item_multiple_choice, contactsList));
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
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
