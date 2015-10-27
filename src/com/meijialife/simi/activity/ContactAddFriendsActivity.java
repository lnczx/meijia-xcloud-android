package com.meijialife.simi.activity;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ListView;

import com.meijialife.simi.BaseListActivity;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.ContactAddFriendsAdapter;
import com.meijialife.simi.bean.Contact;
import com.meijialife.simi.bean.Friend;

/**
 * 手机通讯录加好友
 * 
 */
public class ContactAddFriendsActivity extends BaseListActivity {

    private final int UPDATE_LIST = 1;
    private ArrayList<Contact> contactsList; // 得到的所有联系人
    private ContactAddFriendsAdapter adapter;
    private ProgressDialog proDialog;
    
    private ArrayList<Friend> friendList;//现有好友列表

    private Thread getContactsThread;
    Handler updateListHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

            case UPDATE_LIST:
                if (proDialog != null) {
                    proDialog.dismiss();
                }
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
        getContactsThread = new Thread(new GetContacts());
        getContactsThread.start();
        proDialog = ProgressDialog.show(ContactAddFriendsActivity.this, "", "请稍等...", true, true);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    void updateList() {
        if (contactsList != null && friendList != null){
            adapter = new ContactAddFriendsAdapter(this);
            setListAdapter(adapter);
            adapter.setData(contactsList, friendList);
        }

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
//        Contact contnact = contactsList.get(position);
//        Toast.makeText(this, ""+contnact.getName(), Toast.LENGTH_SHORT).show();
        super.onListItemClick(l, v, position, id);
    }

    class GetContacts implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            Uri uri = ContactsContract.Contacts.CONTENT_URI;
            String[] projection = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.PHOTO_ID };
            String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '1'";
            String[] selectionArgs = null;
            String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
            Cursor cursor = managedQuery(uri, projection, selection, selectionArgs, sortOrder);
            Cursor phonecur = null;

            contactsList.clear();
            
            while (cursor.moveToNext()) {

                // 取得联系人名字
                int nameFieldColumnIndex = cursor.getColumnIndex(android.provider.ContactsContract.PhoneLookup.DISPLAY_NAME);
                String name = cursor.getString(nameFieldColumnIndex);
                // 取得联系人ID
                String contactId = cursor.getString(cursor.getColumnIndex(android.provider.ContactsContract.Contacts._ID));
                phonecur = managedQuery(android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                // 取得电话号码(可能存在多个号码)
                ArrayList<String> phones = new ArrayList<String>();
                while (phonecur.moveToNext()) {
                    String strPhoneNumber = phonecur.getString(phonecur
                            .getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER));
                    if (strPhoneNumber.length() > 4) {
                        phones.add(strPhoneNumber);
                    }

                }
                contactsList.add(new Contact(contactId, name, phones));
            }
            if (phonecur != null)
                phonecur.close();
            cursor.close();

            Message msg1 = new Message();
            msg1.what = UPDATE_LIST;
            updateListHandler.sendMessage(msg1);
        }
    }
    
}
