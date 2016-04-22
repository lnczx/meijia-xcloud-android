package com.meijialife.simi.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.HorizontalScrollViewAdapter;
import com.meijialife.simi.adapter.SecretaryServiceAdapter;
import com.meijialife.simi.bean.SecretaryImages;
import com.meijialife.simi.bean.SecretarySenior;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.photo.activity.GalleryUrlActivity;
import com.meijialife.simi.ui.MyHorizontalScrollView;
import com.meijialife.simi.ui.MyHorizontalScrollView.OnItemClickListener;
import com.meijialife.simi.ui.RoundImageView;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.NetworkUtils;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.UIUtils;

/**
 * 秘书详情页
 * 
 *
 */
public class SecretaryActivity extends BaseActivity implements OnItemClickListener{

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
    private ImageView mImg;
    private List<Integer> mDatas =  new ArrayList<Integer>(Arrays.asList(R.drawable.mishutupian01, R.drawable.mishutupian02, R.drawable.mishutupian03,
            R.drawable.mishutupian04));
    private List<SecretaryImages> mLists;
    private ArrayList<SecretaryImages> secImageData;
            
            
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.secretary_activity);
        super.onCreate(savedInstanceState);
        user = DBHelper.getUser(this);
        initSecDefaultPhotos();
        initView();
        
    }
    
    private void initSecDefaultPhotos(){
        mLists = new ArrayList<SecretaryImages>();
        SecretaryImages si1 = new SecretaryImages(user.getId(),R.drawable.mishutupian01);
        SecretaryImages si2 = new SecretaryImages(user.getId(),R.drawable.mishutupian02);
        SecretaryImages si3 = new SecretaryImages(user.getId(),R.drawable.mishutupian03);
        SecretaryImages si4 = new SecretaryImages(user.getId(),R.drawable.mishutupian04);
        mLists.add(si1);
        mLists.add(si2);
        mLists.add(si4);
        mLists.add(si3);
    }
    
    
    
    private void initView() {
        setTitleName("服务订单");
        requestBackBtn();

        finalBitmap = FinalBitmap.create(this);
        defDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_defult_touxiang);

        String sec_id = getIntent().getExtras().getString("sec_id");
        String sec_name = getIntent().getExtras().getString("sec_name");
        String sec_description = getIntent().getExtras().getString("sec_description");
        String sec_img = getIntent().getExtras().getString("sec_img");

        TextView item_tv_name = (TextView) findViewById(R.id.item_tv_name);
        TextView item_tv_text = (TextView) findViewById(R.id.item_tv_text);
        RoundImageView item_iv_icon = (RoundImageView) findViewById(R.id.item_iv_icon);

        item_tv_name.setText(sec_name);
        item_tv_text.setText(sec_description);
        finalBitmap.display(item_iv_icon, sec_img, defDrawable.getBitmap(), defDrawable.getBitmap());

        listview = (ListView) findViewById(R.id.listview);
        //adapter = new SecretaryServiceAdapter(this, sec_id);
        
        
        //获得HorizontalScrollView对象
        mHorizontalScrollView = (MyHorizontalScrollView) findViewById(R.id.id_horizontalScrollView);
        mHorizontalScrollView.setOnItemClickListener(this);
        getSecretarySenior();
        
        getSecretaryPhoto();//获取秘书生活照图片
    }
    
    public void getSecretaryPhoto(){
        if (!NetworkUtils.isNetworkConnected(SecretaryActivity.this)) {
            Toast.makeText(SecretaryActivity.this,getString(R.string.net_not_open),0).show();
        }
        String sec_id = getIntent().getExtras().getString("sec_id");
        Map<String,String> map = new HashMap<String,String>();
        map.put("user_id",sec_id);
        AjaxParams params = new AjaxParams(map);
        
        showDialog();
        new FinalHttp().get(Constants.URL_GET_USER_IMAGES, params, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(SecretaryActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                            Gson gson = new Gson();
                            secImageData = gson.fromJson(data, new TypeToken<ArrayList<SecretaryImages>>() {
                            }.getType());
                            //if (StringUtils.isNotEmpty(data)) {
                            if (secImageData!=null && secImageData.size()>0) {
                                mHorizontalScrollView.setVisibility(View.VISIBLE);
                                mAdapter = new HorizontalScrollViewAdapter(SecretaryActivity.this, secImageData,1);
                                //ScrollView添加适配器
                                mHorizontalScrollView.initDatas(mAdapter);
                            } else {
                                //适配器初始化数据
                               // mAdapter = new HorizontalScrollViewAdapter(SecretaryActivity.this, mLists,0);
                                //ScrollView添加适配器
                               // mHorizontalScrollView.initDatas(mAdapter);
                                mHorizontalScrollView.setVisibility(View.GONE);
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
                    UIUtils.showToast(SecretaryActivity.this, errorMsg);
                }
            }
        });
    }
    /**
     * 获取秘书列表
     */
    public void getSecretarySenior() {

        if (!NetworkUtils.isNetworkConnected(SecretaryActivity.this)) {
            Toast.makeText(SecretaryActivity.this, getString(R.string.net_not_open), 0).show();
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        AjaxParams param = new AjaxParams(map);

        showDialog();
        new FinalHttp().get(Constants.URL_GET_SENIOR, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                dismissDialog();
                Toast.makeText(SecretaryActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                                ArrayList<SecretarySenior> secData = gson.fromJson(data, new TypeToken<ArrayList<SecretarySenior>>() {
                                }.getType());
                               // adapter.setData(secData);
                                listview.setAdapter(adapter);
                                // tv_tips.setVisibility(View.GONE);
                            } else {
                                //adapter.setData(new ArrayList<SecretarySenior>());
                                listview.setAdapter(adapter);
                                // tv_tips.setVisibility(View.VISIBLE);
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
                    UIUtils.showToast(SecretaryActivity.this, errorMsg);
                }
            }
        });

    }
    /**
     * 点击进入显示大图activity
     */
    @Override
    public void onClick(View view, int pos) {
        Intent intent = new Intent(SecretaryActivity.this,
                GalleryUrlActivity.class);
        intent.putExtra("tag", pos);
        Bundle b = new Bundle();
        b.putSerializable("list_img", secImageData);
        intent.putExtras(b);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

}
