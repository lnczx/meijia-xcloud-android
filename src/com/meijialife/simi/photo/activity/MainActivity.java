package com.meijialife.simi.photo.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

import com.meijialife.simi.Constants;
import com.meijialife.simi.R;
import com.meijialife.simi.activity.AccountInfoActivity;
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
 * @description:封面相册主页面
 * @author： kerryg
 * @date:2015年11月11日 
 */
public class MainActivity extends Activity {

    private GridView noScrollgridview;
    private GridAdapter adapter;
    private View parentView;
    private PopupWindow pop = null;
    private LinearLayout ll_popup;
    public static Bitmap bimap;

    private TextView m_tv_save;// 保存按钮
    private EditText m_et_introduce;// 描述
    private ImageView m_btn_left;// 左侧返回按钮
    private User user;
    private FragmentManager mFM = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Res.init(this);
        bimap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_addpic_unfocused);
        PublicWay.activityList.add(this);
        parentView = getLayoutInflater().inflate(R.layout.activity_selectimg, null);
        setContentView(parentView);
        Init();
    }

    public void Init() {
        user = DBHelper.getUser(MainActivity.this);
        pop = new PopupWindow(MainActivity.this);

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
//                System.exit(0);
                finish();
            }
        });
        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_camera);
        Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
        Button bt3 = (Button) view.findViewById(R.id.item_popupwindows_cancel);
        parent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                photo();
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
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
                    ll_popup.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.activity_translate_in));
                    pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                } else {
                    Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
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
            /*String sec_introduce = m_et_introduce.getText().toString().trim();
            if (StringUtils.isEmpty(sec_introduce)) {
                Toast.makeText(MainActivity.this, "自我介绍不能为空", Toast.LENGTH_SHORT).show();
                return;
            }*/
            if (list != null && list.size() > 0) {
                postCoverAlbum();
            } else {
                Toast.makeText(MainActivity.this, "上传照片不能为空", Toast.LENGTH_SHORT).show();
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
                m_et_introduce.setText(Constants.COVER_ALBUM_INTRODUCE_CONTENT);
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constants.COVER_ALBUM_INTRODUCE_CONTENT="";
    }
    /*
     * 上传多张照片
     */
    private void postCoverAlbum() {
        ArrayList<ImageItem> list = Bimp.tempSelectBitmap;
        List<File> lists = new ArrayList<File>();
        for (Iterator iterator = list.iterator(); iterator.hasNext();) {
            ImageItem imageItem = (ImageItem) iterator.next();
            AjaxParams params = new AjaxParams();
            try {
                params.put("user_id", user.getId());
                params.put("file", new File(imageItem.getImagePath()));
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            showDialog();
            new FinalHttp().post(Constants.URL_POST_COVER_ALBUM, params, new AjaxCallBack<Object>() {
                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    LogOut.debug("错误码：" + errorNo);
                    // dismissDialog();
                    Toast.makeText(MainActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                            if (status == Constants.STATUS_SUCCESS) { // 正确
                                Bimp.tempSelectBitmap.clear();;
                                Intent intent = new Intent(MainActivity.this, AccountInfoActivity.class);
                                startActivity(intent);
                                Toast.makeText(MainActivity.this, "保存成功!", Toast.LENGTH_SHORT).show();
                            } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                                Toast.makeText(MainActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                            } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                                Toast.makeText(MainActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                            } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                                Toast.makeText(MainActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                            } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
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
                takePhoto.setBitmap(bm);
                takePhoto.setImagePath(FileUtils.SDPATH+fileName+".JPEG");
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
            Bimp.max = 0;//不初始化就卡到爆
            this.finish();
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
