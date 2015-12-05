package com.meijialife.simi.activity;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.meijialife.simi.bean.Contact;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.GetContactsRunnable;
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

    private ArrayList<Integer> getcontactIntList;//

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

        contactsList = new ArrayList<String>();
        getcontactsList = new ArrayList<String>();
        getcontactIntList = new ArrayList<Integer>();

        ImageView title_btn_left = (ImageView) findViewById(R.id.title_btn_left);
        TextView header_tv_name = (TextView) findViewById(R.id.header_tv_name);
        title_btn_ok = (TextView) findViewById(R.id.title_btn_ok);
        getcontactIntList = getIntent().getIntegerArrayListExtra("getcontactIntList");
        header_tv_name.setText("选择联系人");
        title_btn_left.setVisibility(View.VISIBLE);
        title_btn_left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getcontactsList != null && getcontactsList.size() > 0) {
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra("contact", getcontactsList);
                    intent.putIntegerArrayListExtra("getcontactIntList", getcontactIntList);
                    setResult(RESULT_OK, intent);
                    ContactSelectActivity.this.finish();

                }

            }
        });

        final ListView listView = getListView();
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        getContacts();
       /* int temp = listView.getCount();
        for (int i = 1; i <  listView.getChildCount(); i++) {
            CheckedTextView cv = (CheckedTextView) listView.getChildAt(i);
            for (int j = 0; j < getcontactIntList.size(); j++) {
                if (cv.getId() == (int) getcontactIntList.get(j)) {
                    cv.setChecked(true);
                }
            }
        }*/
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
        if (getcontactsList.size() > 10) {
            UIUtils.showToast(this, "您最多可以选择10个联系人哦");
        } else {
            Intent intent = new Intent();
            intent.putStringArrayListExtra("contact", getcontactsList);
            intent.putIntegerArrayListExtra("getcontactIntList", getcontactIntList);
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
        contactsList = new ArrayList<String>();
        contactsList.add(user.getName() + "\n" + user.getMobile());// 增加本人到列表中
        for (int i = 0; i < contacts.size(); i++) {
            contactsList.add(contacts.get(i).getName() + "\n" + contacts.get(i).getPhoneNum());
        }

        if (contactsList != null)
            setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item_multiple_choice, contactsList));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        int p = position;
        if (!((CheckedTextView) v).isChecked()) {

            CharSequence num = ((CheckedTextView) v).getText();
            getcontactsList.remove(num.toString());
           // getcontactIntList.remove(v.getId());
        }
        if (((CheckedTextView) v).isChecked()) {
            int ids = v.getId();
           // getcontactIntList.add(v.getId());
            CharSequence num = ((CheckedTextView) v).getText();
            if ((num.toString()).indexOf("[") > 0) {
                String phoneNum = num.toString().substring(0, (num.toString()).indexOf("\n"));
                getcontactsList.remove(phoneNum);
            } else {
                getcontactsList.add(num.toString());
            }
        }
        super.onListItemClick(l, v, position, id);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        contactsList.clear();
        getcontactsList.clear();
        getcontactIntList.clear();

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
