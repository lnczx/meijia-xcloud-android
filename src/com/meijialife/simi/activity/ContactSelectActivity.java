package com.meijialife.simi.activity;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.meijialife.simi.R;
import com.meijialife.simi.utils.UIUtils;

/**
 * 选择联系人
 * 
 */
public class ContactSelectActivity extends ListActivity {

    private final int UPDATE_LIST = 1;
    ArrayList<String> contactsList; // 得到的所有联系人
    ArrayList<String> getcontactsList; // 选择得到联系人
    private Button okbtn;
    private Button cancelbtn;

    Thread getcontacts;
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

        ImageView title_btn_left = (ImageView) findViewById(R.id.title_btn_left);
        TextView header_tv_name = (TextView) findViewById(R.id.header_tv_name);
        title_btn_ok = (TextView) findViewById(R.id.title_btn_ok);

        header_tv_name.setText("选择联系人");
        title_btn_left.setVisibility(View.VISIBLE);
        title_btn_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (getcontactsList != null && getcontactsList.size() > 0) {
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra("contact", getcontactsList);
                    setResult(RESULT_OK, intent);
                    ContactSelectActivity.this.finish();

                }

            }
        });

        contactsList = new ArrayList<String>();
        getcontactsList = new ArrayList<String>();

        final ListView listView = getListView();
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        showDialog();
        getcontacts = new Thread(new GetContacts());
        getcontacts.start();

        title_btn_ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                postContactData();
            }
        });

    }

    public void postContactData() {
        if (getcontactsList.size() > 10) {
            UIUtils.showToast(this, "您最多可以选择10个联系人哦");
        } else {
            Intent intent = new Intent();
            intent.putStringArrayListExtra("contact", getcontactsList);
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
        if (contactsList != null)
            setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item_multiple_choice, contactsList));

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        if (!((CheckedTextView) v).isChecked()) {

            CharSequence num = ((CheckedTextView) v).getText();
            getcontactsList.remove(num.toString());
        }
        if (((CheckedTextView) v).isChecked()) {
            CharSequence num = ((CheckedTextView) v).getText();
            if ((num.toString()).indexOf("[") > 0) {
                String phoneNum = num.toString().substring(0, (num.toString()).indexOf("\n"));
                getcontactsList.remove(phoneNum);
                Log.d("remove_num", "" + phoneNum);
            } else {
                getcontactsList.add(num.toString());
                Log.d("remove_num", "" + num.toString());
            }
        }
        super.onListItemClick(l, v, position, id);
    }

    Cursor cursor = null;
    Cursor phonecur = null;

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
            cursor = managedQuery(uri, projection, selection, selectionArgs, sortOrder);

            while (cursor.moveToNext()) {

                // 取得联系人名字
                int nameFieldColumnIndex = cursor.getColumnIndex(android.provider.ContactsContract.PhoneLookup.DISPLAY_NAME);
                String name = cursor.getString(nameFieldColumnIndex);
                // 取得联系人ID
                String contactId = cursor.getString(cursor.getColumnIndex(android.provider.ContactsContract.Contacts._ID));
                phonecur = managedQuery(android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                // 取得电话号码(可能存在多个号码)
                while (phonecur.moveToNext()) {
                    String strPhoneNumber = phonecur.getString(phonecur
                            .getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER));
                    if (strPhoneNumber.length() > 4)
                        // contactsList.add("18610011001" + "\n测试");
                        contactsList.add(name + "\n" + strPhoneNumber + "");

                }
            }
            if (phonecur != null)
                phonecur.close();
            cursor.close();

            Message msg1 = new Message();
            msg1.what = UPDATE_LIST;
            updateListHandler.sendMessage(msg1);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (phonecur != null)
                phonecur.close();
                phonecur=null;
            if (cursor != null) {
                cursor.close();
                cursor=null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onDestroy() {
        contactsList.clear();
        getcontactsList.clear();
        
        try {
            if (phonecur != null)
                phonecur.close();
                phonecur=null;
            if (cursor != null) {
                cursor.close();
                cursor=null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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
