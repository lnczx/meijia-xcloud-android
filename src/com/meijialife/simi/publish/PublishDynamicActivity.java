package com.meijialife.simi.publish;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.photo.util.Bimp;
import com.meijialife.simi.photo.util.FileUtils;
import com.meijialife.simi.photo.util.ImageItem;
import com.meijialife.simi.photo.util.PublicWay;
import com.meijialife.simi.photo.util.Res;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.StringUtils;

/**
 * @description：发动态--文字+图片（多张）
 * @author： kerryg
 * @date:2015年12月23日 
 */
public class PublishDynamicActivity  extends Activity{
    
    private GridView noScrollgridview;
    private GridAdapter adapter;
    private View parentView;
    private PopupWindow pop = null;
    private LinearLayout ll_popup;
    public static Bitmap bimap;

    private TextView m_tv_save;// 保存按钮
    private EditText m_et_introduce;// 描述
    private ImageView m_btn_left;// 左侧返回按钮
    private TextView title_tv_name;//发布动态标题
    private User user;
    private FragmentManager mFM = null;
    private String sec_introduce = "";
    
    /**
     * 获取当前地理位置
     */
    private String longitude ="";//经度
    private String latitude ="";//纬度
    private String addrStr = "";//位置
    private LocationClient locationClient = null;
    private static final int UPDATE_TIME = 5000;
    private static int LOCATION_COUTNS = 0;
    private static int count =0;
    private static int flag = 0;
    

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Res.init(this);
        bimap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_addpic_unfocused);
        PublicWay.activityList.add(this);
        parentView = getLayoutInflater().inflate(R.layout.activity_selectimg, null);
        setContentView(parentView);
        Init();
        initLocation();
    }
    
    /**
     * 获取用户当前位置的经纬度
     */
    private void initLocation(){
        locationClient = new LocationClient(this);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);        //是否打开GPS
        option.setCoorType("bd09ll");       //设置返回值的坐标类型。
        option.setPriority(LocationClientOption.NetWorkFirst);  //设置定位优先级
        option.setProdName("Secretary"); //设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
        option.setScanSpan(UPDATE_TIME);    //设置定时定位的时间间隔。单位毫秒
        locationClient.setLocOption(option);
        locationClient.start();
        
        locationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                if(location==null){
                    return ;
                }
                latitude =  location.getLatitude()+"";//纬度
                longitude = location.getLongitude()+"";//经度
                addrStr =location.getAddrStr();
            }
        });
    }

    public void Init() {
        user = DBHelper.getUser(PublishDynamicActivity.this);
        pop = new PopupWindow(PublishDynamicActivity.this);
        
        View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);
        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);

        
        pop.setWidth(LayoutParams.MATCH_PARENT);
        pop.setHeight(LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);

        m_tv_save = (TextView) findViewById(R.id.activity_selectimg_send);
        m_et_introduce = (EditText) findViewById(R.id.et_introduce);
        m_btn_left = (ImageView) findViewById(R.id.title_btn_left);
        title_tv_name = (TextView)findViewById(R.id.title_tv_name);
        m_tv_save.setText("发布");
        title_tv_name.setText("发布动态");
        m_et_introduce.setHint("发布你的动态吧！！！");
        if(!StringUtils.isEmpty(Constants.FEED_TITLE)){
            m_et_introduce.setText(Constants.FEED_TITLE);
        }
        m_tv_save.setOnClickListener(new savePhotoAndIntroduce());

        m_btn_left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bimp.tempSelectBitmap.clear();
                Bimp.max = 0;
                    for (int i = 0; i < PublicWay.activityList.size(); i++) {
                         if (null != PublicWay.activityList.get(i)) {
                            PublicWay.activityList.get(i).finish();
                        }
                    }
            }
        });
        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_camera);
        Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
        Button bt3 = (Button) view.findViewById(R.id.item_popupwindows_cancel);
        parent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                flag = 1;//设置表明拍照图片
                photo();
                Constants.FEED_TITLE = m_et_introduce.getText().toString().trim();
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(PublishDynamicActivity.this, AlbumActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
                Constants.FEED_TITLE = m_et_introduce.getText().toString().trim();
                flag = 0;//设置表明相册图片
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });

        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        adapter.update();
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (arg2 == Bimp.tempSelectBitmap.size()) {
                    ll_popup.startAnimation(AnimationUtils.loadAnimation(PublishDynamicActivity.this, R.anim.activity_translate_in));
                    pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                } else {
                    Intent intent = new Intent(PublishDynamicActivity.this, GalleryActivity.class);
                    intent.putExtra("position", "1");
                    intent.putExtra("ID", arg2);
                    startActivity(intent);
                }
            }
        });
    }

    public class savePhotoAndIntroduce implements OnClickListener {
        @Override
        public void onClick(View v) {
            ArrayList<ImageItem> list = Bimp.tempSelectBitmap;
            sec_introduce = m_et_introduce.getText().toString().trim();
            if((list!=null && list.size()>0) || !StringUtils.isEmpty(sec_introduce)){
                if(list.size()>9){
                    Toast.makeText(PublishDynamicActivity.this, "最多可以上传九张图片", Toast.LENGTH_SHORT).show();
                    return;
                }
                count = 0;
                postFriendDynamic();
            }else {
                Toast.makeText(PublishDynamicActivity.this, "不能发布空的动态！", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                m_et_introduce.setText(Constants.FEED_TITLE);
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constants.FEED_TITLE="";
    }
    /**
     * 发表动态接口
     * 
     */
    private void postFriendDynamic() {
        showDialog();
            AjaxParams params = new AjaxParams();
                params.put("user_id", user.getId());
                params.put("title",sec_introduce);
                params.put("lat",latitude);
                params.put("lng",longitude);
                params.put("poi_name",addrStr);
            new FinalHttp().post(Constants.URL_POST_FRIEND_DYNAMIC, params, new AjaxCallBack<Object>() {
                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    LogOut.debug("错误码：" + errorNo);
                     dismissDialog();
                    Toast.makeText(PublishDynamicActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onSuccess(Object t) {
                    super.onSuccess(t);
                    dismissDialog();
                    try {
                        if (StringUtils.isNotEmpty(t.toString())) {
                            JSONObject obj = new JSONObject(t.toString());
                            int status = obj.getInt("status");
                            String msg = obj.getString("msg");
                            String data = obj.getString("data");
                            if (status == Constants.STATUS_SUCCESS) { // 正确
                                //调用图片上传接口
                                postFeedImgs(data.trim());
                            } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                                Toast.makeText(PublishDynamicActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                            } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                                Toast.makeText(PublishDynamicActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                            } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                                Toast.makeText(PublishDynamicActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                            } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                                Toast.makeText(PublishDynamicActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(PublishDynamicActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        // UIUtils.showToast(context, "网络错误,请稍后重试");
                    }
                }
            });
    }
    /**
     * 动态上传图片接口
     */
    private void postFeedImgs(String fid) {
        showDialog();
        ArrayList<ImageItem> list = Bimp.tempSelectBitmap;
        for (Iterator iterator = list.iterator(); iterator.hasNext();) {
            ImageItem imageItem = (ImageItem) iterator.next();
            AjaxParams params = new AjaxParams();
            try {
                params.put("user_id", user.getId());
                params.put("fid",fid);
                params.put("feed_imgs", new File(imageItem.getImagePath()));
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            new FinalHttp().post(Constants.URL_POST_FEED_IMGS, params, new AjaxCallBack<Object>() {
                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    LogOut.debug("错误码：" + errorNo);
                    disProgressError();
                }
                @Override
                public void onSuccess(Object t) {
                    super.onSuccess(t);
                    LogOut.debug("成功:" + t.toString());
                    try {
                        if (StringUtils.isNotEmpty(t.toString())) {
                            JSONObject obj = new JSONObject(t.toString());
                            int status = obj.getInt("status");
                            String msg = obj.getString("msg");
                            if (status == Constants.STATUS_SUCCESS) { // 正确
                                disProgressSuccess();
                            } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                                Toast.makeText(PublishDynamicActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                            } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                                Toast.makeText(PublishDynamicActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                            } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                                Toast.makeText(PublishDynamicActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                            } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                                Toast.makeText(PublishDynamicActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(PublishDynamicActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        // UIUtils.showToast(context, "网络错误,请稍后重试");
                    }
                }
            });
        }
    }
    private void disProgressError(){
        ArrayList<ImageItem>  list= Bimp.tempSelectBitmap;
        count++;
        if(count == list.size()){
            dismissDialog();
            Toast.makeText(PublishDynamicActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
            for (int i = 0; i < PublicWay.activityList.size(); i++) {
                if (null != PublicWay.activityList.get(i)) {
                    PublicWay.activityList.get(i).finish();
                }
            }
            Bimp.tempSelectBitmap.clear();
            Bimp.max = 0;
        }
    }
    
    private void disProgressSuccess(){
        ArrayList<ImageItem>  list= Bimp.tempSelectBitmap;
        count++;
        if(count == list.size()){
            dismissDialog();
            Toast.makeText(PublishDynamicActivity.this, "保存成功!", Toast.LENGTH_SHORT).show();
            for (int i = 0; i < PublicWay.activityList.size(); i++) {
                if (null != PublicWay.activityList.get(i)) {
                    PublicWay.activityList.get(i).finish();
                }
            }
            Bimp.tempSelectBitmap.clear();
            Bimp.max = 0;
        }
    }
    
    
    @SuppressLint("HandlerLeak")
    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int selectedPosition = -1;
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update() {
            loading();
        }

        public int getCount() {
            if (Bimp.tempSelectBitmap.size() == 9) {
                return 9;
            }
            return (Bimp.tempSelectBitmap.size() + 1);
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_published_grida, parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView.findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position == Bimp.tempSelectBitmap.size()) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_addpic_unfocused));
                if (position == 9) {
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position).getBitmap());
            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case 1:
                    adapter.notifyDataSetChanged();
                    break;
                }
                super.handleMessage(msg);
            }
        };

        public void loading() {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        if (Bimp.max == Bimp.tempSelectBitmap.size()) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        } else {
                            Bimp.max += 1;
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    }
                }
            }).start();
        }
    }

    public String getString(String s) {
        String path = null;
        if (s == null)
            return "";
        for (int i = s.length() - 1; i > 0; i++) {
            s.charAt(i);
        }
        return path;
    }

    protected void onRestart() {
        adapter.update();
        super.onRestart();
    }

    private static final int TAKE_PICTURE = 0x000001;

    public void photo() {
        String SDState = Environment.getExternalStorageState();
        if(SDState.equals(Environment.MEDIA_MOUNTED))
        {
            Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(openCameraIntent, TAKE_PICTURE);
        }else {
            Toast.makeText(this,"内存卡不存在", Toast.LENGTH_LONG).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case TAKE_PICTURE:
            if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {

                String fileName = String.valueOf(System.currentTimeMillis());
                Bitmap bm = (Bitmap) data.getExtras().get("data");
                FileUtils.saveBitmap(bm, fileName);

                ImageItem takePhoto = new ImageItem();
                takePhoto.setImagePath(FileUtils.SDPATH+fileName+".JPEG");
                takePhoto.setBitmap(bm);
                Bimp.tempSelectBitmap.add(takePhoto);
            }
            break;
        }
    }

    
    
    
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            for (int i = 0; i < PublicWay.activityList.size(); i++) {
                 if (null != PublicWay.activityList.get(i)) {
                    PublicWay.activityList.get(i).finish();
                }
            }
            Bimp.tempSelectBitmap.clear();
            Bimp.max = 0;
//            System.exit(0);
        }
        return true;
    }
    private ProgressDialog m_pDialog;
    public void showDialog() {
        if(m_pDialog == null){
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

}
