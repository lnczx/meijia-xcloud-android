package com.meijialife.simi.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;

import com.meijialife.simi.BaseListActivity;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.ContactAddFriendsAdapter;
import com.meijialife.simi.bean.Contact;
import com.meijialife.simi.bean.Friend;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.GetContactsRunnable;

/**
 * 手机通讯录加好友
 * 
 */
public class ContactAddFriendsActivity extends BaseListActivity {
    
    private static final String TAG = "ContactAddFriendsActivity";

    private final int UPDATE_LIST = 1;
    private ArrayList<Contact> contactsList; // 得到的所有联系人
    private ContactAddFriendsAdapter adapter;
    
    private ArrayList<Friend> friendList;//现有好友列表

    Handler updateListHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

            case UPDATE_LIST:
                contactsList = (ArrayList<Contact>) DBHelper.getContacts(ContactAddFriendsActivity.this);
                dismissDialog();
                updateList();
            }
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_contacts_addfirends_list);
        super.onCreate(savedInstanceState);

        init();
        getContacts();
    }
    
    private void init(){
        friendList = (ArrayList<Friend>) getIntent().getSerializableExtra("friendList");
        setTitleName("手机通讯录加好友");
        requestBackBtn();
        
        contactsList = new ArrayList<Contact>();
    }
    
    private void getContacts(){
        contactsList = (ArrayList<Contact>) DBHelper.getContacts(this);
        if(contactsList == null || contactsList.size() == 0){
            Thread getContactsThread = new Thread(new GetContactsRunnable(this, updateListHandler));
            getContactsThread.start();
            showDialog();
        }else{
            updateList();
        }
        
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void updateList() {
        if (contactsList != null && friendList != null) {
            adapter = new ContactAddFriendsAdapter(this);
            setListAdapter(adapter);
            adapter.setData(contactsList, friendList);
        }

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
//        Contact contnact = contactsList.get(position);
        super.onListItemClick(l, v, position, id);
    }

    
}
