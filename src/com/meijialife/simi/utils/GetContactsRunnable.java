package com.meijialife.simi.utils;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;

import com.meijialife.simi.bean.Contact;
import com.meijialife.simi.database.DBHelper;

/**
 * 获取手机联系人
 * 
 * @author baojiarui
 *
 */
public class GetContactsRunnable implements Runnable {
    
    private static final String TAG = "GetContactsRunnable";
    
    private ArrayList<Contact> contactsList; // 得到的所有手机联系人

    private Handler handler;
    private Activity activity;

    /**
     * 获取手机联系人
     * 
     * @param activity 必须传入Activity本体
     * @param handler 读取完成后利用此handler发送Message.what = 1;
     */
    public GetContactsRunnable(Activity activity, Handler handler) {
        this.activity = activity;
        this.handler = handler;
        contactsList = new ArrayList<Contact>();
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.PHOTO_ID };
        String selection = null;// ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '1'" modify by garry
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
        Cursor cursor = activity.managedQuery(uri, projection, selection, selectionArgs, sortOrder);
        Cursor phonecur = null;

        contactsList.clear();

        while (cursor != null && cursor.moveToNext()) {

            // 取得联系人名字
            int nameFieldColumnIndex = cursor.getColumnIndex(android.provider.ContactsContract.PhoneLookup.DISPLAY_NAME);
            String name = cursor.getString(nameFieldColumnIndex);
            // 取得联系人ID
            String contactId = cursor.getString(cursor.getColumnIndex(android.provider.ContactsContract.Contacts._ID));
            phonecur = activity.managedQuery(android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
            // 取得电话号码(可能存在多个号码)
            ArrayList<String> phones = new ArrayList<String>();
            while (phonecur != null && phonecur.moveToNext()) {
                String strPhoneNumber = phonecur.getString(phonecur.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER));
                if (strPhoneNumber.length() > 4) {
                    phones.add(strPhoneNumber);
                }

            }
            if(phones.size() > 0){
                contactsList.add(new Contact(contactId, name, phones.get(0)));
            }

        }
        
        DBHelper db = DBHelper.getInstance(activity);
        for (int i = 0; i < contactsList.size(); i++) {
            db.add(contactsList.get(i), contactsList.get(i).getContactId());
        }
        
        if (phonecur != null){
//            phonecur.close();
//            phonecur = null;
        }
        if(cursor != null){
//            cursor.close();
//            cursor = null;
        }

        Message msg1 = new Message();
        msg1.what = 1;
        if(handler != null){
            handler.sendMessage(msg1);
        }
    }
}