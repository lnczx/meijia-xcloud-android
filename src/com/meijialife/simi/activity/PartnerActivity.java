package com.meijialife.simi.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.HorizontalScrollViewAdapter;
import com.meijialife.simi.adapter.SecretaryServiceAdapter;
import com.meijialife.simi.bean.Partner;
import com.meijialife.simi.bean.PartnerDetail;
import com.meijialife.simi.bean.SecretaryImages;
import com.meijialife.simi.bean.ServicePrices;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.bean.UserTag;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.photo.activity.GalleryUrlActivity;
import com.meijialife.simi.ui.MyHorizontalScrollView;
import com.meijialife.simi.ui.MyHorizontalScrollView.OnItemClickListener;
import com.meijialife.simi.ui.RoundImageView;
import com.meijialife.simi.ui.TagGroup;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * 秘书详情页
 * 
 *
 */
public class PartnerActivity extends BaseActivity implements OnItemClickListener{

    private ListView listview;
    private SecretaryServiceAdapter adapter;
    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;
    private User user;
 
    /**
     * HorizontalScrollView实现图片左右滑动
     */
    private MyHorizontalScrollView mHorizontalScrollView;
    private HorizontalScrollViewAdapter mAdapter;
    private Partner partner;
    private List<ServicePrices> servicePricesList;
    private List<SecretaryImages> secretaryImagesList;
    private List<UserTag> userTagList;
    private PartnerDetail partnerDetail;
    
    private String partner_user_id;
    private String service_type_id;
    
    private UserInfo userInfo;
            
            
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.secretary_activity);
        super.onCreate(savedInstanceState);
        user = DBHelper.getUser(this);
        init();
    }
    private void init(){
        setTitleName("详情");
        requestBackBtn();
        partner = (Partner) getIntent().getSerializableExtra("Partner");
        partner_user_id = String.valueOf(partner.getUser_id());
        service_type_id = String.valueOf(partner.getService_type_id());
        getPartnerDetail(service_type_id, partner_user_id);  
        
        userInfo = DBHelper.getUserInfo(PartnerActivity.this);
    }
    /**
     * 获取服务人员详情
     */
    public void getPartnerDetail(String service_type_id,String partner_user_id ) {
        if (!NetworkUtils.isNetworkConnected(PartnerActivity.this)) {
            Toast.makeText(PartnerActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("service_type_id", service_type_id);
        map.put("partner_user_id", partner_user_id);
        AjaxParams param = new AjaxParams(map);

        showDialog();
        new FinalHttp().get(Constants.URL_GET_USER_DETAIL, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(PartnerActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                            if (StringUtils.isNotEmpty(data)) {
                                Gson gson = new Gson();
                                partnerDetail=gson.fromJson(data,PartnerDetail.class);
                                servicePricesList = (List<ServicePrices>) partnerDetail.getService_prices();
                                secretaryImagesList = (List<SecretaryImages>)partnerDetail.getUser_imgs();
                                userTagList = (List<UserTag>)partnerDetail.getUser_tags();
                               
                                showPartnerInfo(partnerDetail);
                                showPartnerImag(secretaryImagesList);
                                showPartnerService(servicePricesList,partnerDetail);
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
                    UIUtils.showToast(PartnerActivity.this, errorMsg);
                }
            }
        });

    }
    /*
     * 服务商信息赋值显示
     */
    @SuppressLint("ResourceAsColor")
	private void showPartnerInfo(PartnerDetail partnerDetail){
        finalBitmap = FinalBitmap.create(this);
        defDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_defult_touxiang);
        TextView item_tv_name = (TextView) findViewById(R.id.item_tv_name);
        TextView item_tv_text = (TextView) findViewById(R.id.item_tv_text);
        TextView item_tv_fav = (TextView) findViewById(R.id.item_tv_fav);
        TextView item_tv_addr_name = (TextView)findViewById(R.id.item_tv_addr_name);
        TextView item_tv_des_name = (TextView)findViewById(R.id.item_tv_des_name);
        RoundImageView item_iv_icon = (RoundImageView) findViewById(R.id.item_iv_icon);
        item_tv_name.setText(partnerDetail.getName());
        item_tv_text.setText(partnerDetail.getIntroduction());
        item_tv_fav.setText(partnerDetail.getService_type_name());
        item_tv_addr_name.setText(partnerDetail.getCity_and_region());
        item_tv_des_name.setText(partnerDetail.getResponse_time_name());
        finalBitmap.display(item_iv_icon, partnerDetail.getHead_img(), defDrawable.getBitmap(), defDrawable.getBitmap());
        
        TagGroup tg = (TagGroup) findViewById(R.id.ll_user_tags);
        userTagList = partnerDetail.getUser_tags();
        List<String> userTags =new ArrayList<String>();
        for (Iterator iterator = userTagList.iterator(); iterator.hasNext();) {
            UserTag userTag = (UserTag) iterator.next();
            userTags.add(userTag.getTag_name());
        }
        tg.setTags(userTags);
  
    }
    /**
     * 服务商图片展示
     * @param list
     */
    private void showPartnerImag(List<SecretaryImages> list){
        //获得HorizontalScrollView对象
        mHorizontalScrollView = (MyHorizontalScrollView) findViewById(R.id.id_horizontalScrollView);
        mHorizontalScrollView.setOnItemClickListener(this);
        if(list!=null && list.size()>0){
            mHorizontalScrollView.setVisibility(View.VISIBLE);
            mAdapter = new HorizontalScrollViewAdapter(PartnerActivity.this, list,1);
            //ScrollView添加适配器
            mHorizontalScrollView.initDatas(mAdapter);
        }else {
            mHorizontalScrollView.setVisibility(View.GONE);
        }
    }
    /**
     * 服务商服务报价展示
     * @param list
     */
    private void showPartnerService(List<ServicePrices> list,PartnerDetail partnerDetail){
        listview = (ListView) findViewById(R.id.listview);
        adapter = new SecretaryServiceAdapter(this);
        if(list!=null && list.size()>0){
            adapter.setData(list,partnerDetail,userInfo);
            listview.setAdapter(adapter);
        }
        
    }
    @Override
    protected void onResume() {
        super.onResume();
        getUserInfo();
        getPartnerDetail(service_type_id,partner_user_id);
    }
    /**
     * 点击进入显示大图activity
     */
    @Override
    public void onClick(View view, int pos) {
        Intent intent = new Intent(PartnerActivity.this,
                GalleryUrlActivity.class);
        intent.putExtra("tag", pos);
        Bundle b = new Bundle();
        b.putSerializable("list_img", (Serializable) secretaryImagesList);
        intent.putExtras(b);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
    private void getUserInfo() {
        if (user == null) {
            Toast.makeText(PartnerActivity.this, "用户信息错误", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!NetworkUtils.isNetworkConnected(PartnerActivity.this)) {
            Toast.makeText(PartnerActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user.getId());
        AjaxParams param = new AjaxParams(map);

        showDialog();
        new FinalHttp().get(Constants.URL_GET_USER_INFO, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(PartnerActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                            if (StringUtils.isNotEmpty(data)) {
                                Gson gson = new Gson();
                                userInfo = gson.fromJson(data, UserInfo.class);
                            } else {
                                // UIUtils.showToast(BindMobileActivity.this, "数据错误");
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
                // 操作失败，显示错误信息|
                if (!StringUtils.isEmpty(errorMsg.trim())) {
                    UIUtils.showToast(PartnerActivity.this, errorMsg);
                }
            }
        });
    }
}
