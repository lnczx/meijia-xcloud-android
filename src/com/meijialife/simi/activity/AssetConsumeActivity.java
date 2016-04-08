package com.meijialife.simi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.adapter.CatograyAdapter;
import com.meijialife.simi.adapter.GoodsAdapter;
import com.meijialife.simi.bean.AssetData;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.bean.XcompanySetting;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.ui.BadgeView;
import com.meijialife.simi.utils.AssetsDatabaseManager;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.StringUtils;

/**
 * @description：资产领用
 * @author： kerryg
 * @date:2016年4月1日
 */
public class AssetConsumeActivity extends BaseActivity implements OnClickListener {
    private ListView listView1, listView2;
    private final static int CATEGORY_RESULT_CODES = 3;//跳转类型

    private List<XcompanySetting> list;

    private List<AssetData> list2;
    private CatograyAdapter catograyAdapter;
    private GoodsAdapter goodsAdapter;
    private TextView tv_count;

    Map<String, List<AssetData>> assetMap;

    private ImageView shopCart;// 购物车
    private ViewGroup anim_mask_layout;// 动画层
    private ImageView ball;// 小圆点
    private int buyNum = 0;// 购买数量
    private BadgeView buyNumView;// 购物车上的数量标签
    private EditText mEtCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.asset_consume_activity);
        super.onCreate(savedInstanceState);
        initView();
        initListData();
        addListener();

    }

    private void initView() {
        requestBackBtn();
        setTitleName("领用类型");
        mEtCount = (EditText)findViewById(R.id.et_count);
        listView1 = (ListView) findViewById(R.id.listview_1);
        listView2 = (ListView) findViewById(R.id.listview_2);
        ImageView shopCat = (ImageView) findViewById(R.id.iv_add_cart);
        shopCart = (ImageView) findViewById(R.id.iv_add_cart);
        buyNumView = (BadgeView) findViewById(R.id.tv_count_price);
        findViewById(R.id.m_btn_use).setOnClickListener(this);

        assetMap = new HashMap<String, List<AssetData>>();
        list2 = new ArrayList<>();
        list = new ArrayList<XcompanySetting>();

    }

    private void initListData() {
        // 从数据库拿到大类列表
        AssetsDatabaseManager.initManager(getApplication());
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        SQLiteDatabase db = mg.getDatabase("simi01.db");
        list = AssetsDatabaseManager.searchAllXcompany(db);
        catograyAdapter = new CatograyAdapter(this, list);
        goodsAdapter = new GoodsAdapter(this, list2, catograyAdapter);
        listView1.setAdapter(catograyAdapter);
        listView2.setAdapter(goodsAdapter);
        getAssetList();
    }

    private void addListener() {
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                list2.clear();
//                list2 = assetMap.get(list.get(position).getId());
                list2 = list.get(position).getAssetDataList();
                if (list2 != null && list2.size() > 0) {
                    goodsAdapter.setData(list2);
                } 
            }
        });
    }

    /**
     * @Description: 创建动画层
     * @param
     * @return void
     * @throws
     */
    private ViewGroup createAnimLayout() {
        ViewGroup rootView = (ViewGroup) this.getWindow().getDecorView();
        LinearLayout animLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        animLayout.setLayoutParams(lp);
        animLayout.setId(Integer.MAX_VALUE - 1);
        animLayout.setBackgroundResource(android.R.color.transparent);
        rootView.addView(animLayout);
        return animLayout;
    }

    private View addViewToAnimLayout(final ViewGroup parent, final View view, int[] location) {
        int x = location[0];
        int y = location[1];
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = x;
        lp.topMargin = y;
        view.setLayoutParams(lp);
        return view;
    }

    public void setAnim(final View v, int[] startLocation) {
        anim_mask_layout = null;
        anim_mask_layout = createAnimLayout();
        anim_mask_layout.addView(v);// 把动画小球添加到动画层
        final View view = addViewToAnimLayout(anim_mask_layout, v, startLocation);
        int[] endLocation = new int[2];// 存储动画结束位置的X、Y坐标
        shopCart.getLocationInWindow(endLocation);// shopCart是那个购物车

        // 计算位移
        int endX = 0 - startLocation[0] + 40;// 动画位移的X坐标
        int endY = endLocation[1] - startLocation[1];// 动画位移的y坐标
        TranslateAnimation translateAnimationX = new TranslateAnimation(0, endX, 0, 0);
        translateAnimationX.setInterpolator(new LinearInterpolator());
        translateAnimationX.setRepeatCount(0);// 动画重复执行的次数
        translateAnimationX.setFillAfter(true);

        TranslateAnimation translateAnimationY = new TranslateAnimation(0, 0, 0, endY);
        translateAnimationY.setInterpolator(new AccelerateInterpolator());
        translateAnimationY.setRepeatCount(0);// 动画重复执行的次数
        translateAnimationX.setFillAfter(true);

        AnimationSet set = new AnimationSet(false);
        set.setFillAfter(false);
        set.addAnimation(translateAnimationY);
        set.addAnimation(translateAnimationX);
        set.setDuration(300);// 动画的执行时间
        view.startAnimation(set);
        // 动画监听事件
        set.setAnimationListener(new Animation.AnimationListener() {
            // 动画的开始
            @Override
            public void onAnimationStart(Animation animation) {
                v.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            // 动画的结束
            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.GONE);
                buyNum++;// 让购买数量加1
               // String num = mEtCount.getText().toString().trim();
                buyNumView.setText("0");//
                buyNumView.setBadgePosition(BadgeView.POSITION_CENTER);
                buyNumView.show();
            }
        });
    }

    /**
     * 公司资产列表接口
     * 
     * @param barcode
     */
    private void getAssetList() {
        UserInfo userInfo = DBHelper.getUserInfo(this);
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", userInfo.getUser_id());
        map.put("company_id", userInfo.getCompany_id());
        AjaxParams param = new AjaxParams(map);
        new FinalHttp().get(Constants.GET_ASSET_LIST_URL, param, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                LogOut.debug("错误码：" + errorNo);
                Toast.makeText(getApplicationContext(), getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                dismissDialog();
                LogOut.debug("成功:" + t.toString());
                try {
                    if (StringUtils.isNotEmpty(t.toString())) {
                        JSONObject obj = new JSONObject(t.toString());
                        int status = obj.getInt("status");
                        String msg = obj.getString("msg");
                        String data = obj.getString("data");
                        if (status == Constants.STATUS_SUCCESS) {
                            Gson gson = new Gson();
                            assetMap = gson.fromJson(data, new TypeToken<Map<String, List<AssetData>>>() {
                            }.getType());
                            showAssetMap(assetMap);
                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            Toast.makeText(AssetConsumeActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(AssetConsumeActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(AssetConsumeActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(AssetConsumeActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AssetConsumeActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AssetConsumeActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected void showAssetMap(Map<String, List<AssetData>> assetMaps) {
        for (Map.Entry<String, List<AssetData>> entry : assetMaps.entrySet()) {
            for (Iterator iterator = list.iterator(); iterator.hasNext();) {
                XcompanySetting xcompanySetting = (XcompanySetting) iterator.next();
                if(StringUtils.isEquals(xcompanySetting.getId(),entry.getKey())){
                   xcompanySetting.setAssetDataList(entry.getValue());
                }
            }
        }
        goodsAdapter.setData(list.get(0).getAssetDataList());
        goodsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        assetMap.clear();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.m_btn_use:
            Intent intent = new Intent();
            setResult(CATEGORY_RESULT_CODES, intent);
            AssetConsumeActivity.this.finish();
            break;
        default:
            break;
        }
    }

}
