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
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.utils.GetContactsRunnable;
import com.meijialife.simi.utils.UIUtils;
/**
 * @description：创建卡片选择通讯录好友
 * @author： kerryg
 * @date:2015年12月9日 
 */
public class ContactSelectActivity extends Activity {

    private final int UPDATE_LIST = 1;
    ArrayList<String> contactsList; // 得到的所有联系人
    private Button okbtn;
    private Button cancelbtn;
    
    private ArrayList<String> mobileList;
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

        contactsList = new ArrayList<String>();
        mobileList = new ArrayList<String>();
        companyAdapter = new ContactSelectAdapter(this);


        ImageView title_btn_left = (ImageView) findViewById(R.id.title_btn_left);
        TextView header_tv_name = (TextView) findViewById(R.id.header_tv_name);
        title_btn_ok = (TextView) findViewById(R.id.title_btn_ok);
        header_tv_name.setText("选择联系人");
        title_btn_left.setVisibility(View.VISIBLE);
        title_btn_left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //&& Constants.finalContactList.size() > 0
                if (Constants.finalContactList != null ) {
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
        getMobileList(Constants.finalContactList);
        
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> l, View view, int position, long id) {
              
                cb = (CheckBox) view.findViewById(R.id.cb_check_box);
                tv_name = (TextView) view.findViewById(R.id.item_tv_name);
                tv_mobile = (TextView)view.findViewById(R.id.item_tv_mobile);
                tv_id = (TextView) view.findViewById(R.id.item_tv_id);
                tv_temp = (TextView)view.findViewById(R.id.item_tv_temp);
                if (cb.isChecked() == false) {
                    cb.setChecked(true);
                    CharSequence num = tv_temp.getText();
                    Constants.finalContactList.add(num.toString());
                } else {
                    cb.setChecked(false);
                    CharSequence num = tv_temp.getText();
                    Constants.finalContactList.remove(num.toString());
                }
            }
        });
        title_btn_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                postContactData();
            }
        });
    }
    //获得选中的手机号
    private void getMobileList(ArrayList<String> list){
        for (int i = 0; i < list.size(); i++) {
            String temp = list.get(i);
            String mobile = temp.substring(temp.indexOf("\n")+1,temp.lastIndexOf("\n"));
            mobileList.add(mobile);
        }
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
        if (Constants.finalContactList.size() > 10) {
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
        contactsList = new ArrayList<String>();
        contactsList.add(user.getName() + "\n" + user.getMobile()+"\n"+0);// 增加本人到列表中
        for (int i = 0; i < contacts.size(); i++) {
            contactsList.add(contacts.get(i).getName() + "\n" + contacts.get(i).getPhoneNum()+"\n"+0);
        }
        if (contactsList != null)
            companyAdapter.setData(contactsList, Constants.finalContactList);
//            setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item_multiple_choice, contactsList));
    }

    
    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        contactsList.clear();
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
