package com.meijialife.simi.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.meijialife.simi.BaseActivity;
import com.meijialife.simi.Constants;
import com.meijialife.simi.MainActivity;
import com.meijialife.simi.R;
import com.meijialife.simi.bean.CalendarMark;
import com.meijialife.simi.bean.User;
import com.meijialife.simi.bean.UserIndexData;
import com.meijialife.simi.bean.UserInfo;
import com.meijialife.simi.database.DBHelper;
import com.meijialife.simi.ui.RoundImageView;
import com.meijialife.simi.utils.LogOut;
import com.meijialife.simi.utils.StringUtils;
import com.meijialife.simi.utils.Utils;
import com.simi.easemob.EMDemoHelper;
import com.simi.easemob.utils.HTTPUtils;

/**
 * 账号信息
 * 
 */
public class AccountInfoActivity extends BaseActivity implements OnClickListener {

    private UserIndexData user;

    private RoundImageView iv_header;// 头像
    private EditText et_name; // 昵称
    private EditText et_mobile; // 手机
    private TextView et_gender; // 性别
    private EditText et_card; // 私密卡

    /** 请求码 **/
    private static final int IMAGE_REQUEST_CODE = 0; // 手机相册
    private static final int CAMERA_REQUEST_CODE = 1; // 拍照
    private static final int RESULT_REQUEST_CODE = 2; // 裁剪后保存
    public static final int RESULT_UPDATE_USER = 3; // 更新用户信息

    /** 头像名称 */
    private static final String IMAGE_FILE_NAME = "faceImage.jpg"; // 拍照返回的图片
    private static final String HEADER_IMAGE_NAME = "/headerImage.png"; // 保存成png的头像图片

    private FinalBitmap finalBitmap;
    private BitmapDrawable defDrawable;

    private RelativeLayout title_btn_edit_layout;// 编辑按钮layout
    private TextView title_btn_edit;// 编辑按钮
    private boolean isEdit; // 是否正在编辑

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_account_info_activity);
        super.onCreate(savedInstanceState);

        initView();
        init();
        showData();

    }

    private void initView() {
        setTitleName("账号信息");
        requestBackBtn();

        RelativeLayout item_0 = (RelativeLayout) findViewById(R.id.rl_item_0);// 头像
        RelativeLayout item_3 = (RelativeLayout) findViewById(R.id.rl_item_3);// 性别
        RelativeLayout item_4 = (RelativeLayout) findViewById(R.id.rl_item_4);// 私秘卡
        TextView tv_logout = (TextView) findViewById(R.id.tv_account_logout);// 退出登陆

        item_0.setOnClickListener(this);
        item_3.setOnClickListener(this);
        item_4.setOnClickListener(this);
        tv_logout.setOnClickListener(this);

        title_btn_edit_layout = (RelativeLayout) findViewById(R.id.title_btn_edit_layout);
        title_btn_edit = (TextView) findViewById(R.id.title_btn_edit);
        iv_header = (RoundImageView) findViewById(R.id.iv_header);
        et_name = (EditText) findViewById(R.id.et_name);
        et_mobile = (EditText) findViewById(R.id.et_mobile);
        et_gender = (TextView) findViewById(R.id.et_gender);
        //et_card = (EditText) findViewById(R.id.et_card);

        title_btn_edit_layout.setVisibility(View.VISIBLE);
        title_btn_edit_layout.setOnClickListener(this);

    }

    private void init() {
        user = (UserIndexData) getIntent().getSerializableExtra("user");

        finalBitmap = FinalBitmap.create(this);
        defDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_defult_touxiang);

    }

    private void showData() {
        if (user == null) {
            return;
        }
        et_name.setText(user.getName());
        et_mobile.setText(user.getMobile());
        et_gender.setText(user.getSex());

        String is_senior = DBHelper.getUserInfo(this).getIs_senior();
       /* if (StringUtils.isEquals(is_senior, "1")) {
            et_card.setText("您已购买秘书服务");
        } else {
            et_card.setText("您还没购买秘书服务");
        }*/

        finalBitmap.display(iv_header, user.getHead_img(), defDrawable.getBitmap(), defDrawable.getBitmap());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.title_btn_edit_layout: // 编辑
            onEditClick();
            break;
        case R.id.rl_item_0: // 头像
            showHeaderDlg();
            break;
        case R.id.rl_item_3: // 性别
            showGenderDlg();
            break;
        case R.id.rl_item_4: // 封面相册
            Intent intent = new Intent(AccountInfoActivity.this,com.meijialife.simi.photo.activity.MainActivity.class);
            startActivity(intent);
            
           // showCoverAlbum();
            break;
        case R.id.tv_account_logout: // 退出登陆
            logOut();
            break;

        default:
            break;
        }
    }

    /**
     * 点击右上角编辑按钮
     */
    private void onEditClick() {
        isEdit = !isEdit;
        if (isEdit) {
            // 开始编辑
            et_name.setEnabled(true);
            title_btn_edit.setBackgroundColor(getResources().getColor(R.color.transparent));
            ;
            title_btn_edit.setText("保存");
        } else {
            // 编辑完成，post修改信息接口
            postUserinfo();
        }
    }

    /**
     * 用户信息修改接口
     */
    private void postUserinfo() {

        String name = et_name.getText().toString();
        String gender = et_gender.getText().toString();

        if (StringUtils.isEmpty(name)) {
            Toast.makeText(AccountInfoActivity.this, "昵称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringUtils.isEmpty(gender) || gender.equals("0")) {
            Toast.makeText(AccountInfoActivity.this, "请选择性别", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", user.getId());
        map.put("name", name);
        map.put("sex", gender);
        // map.put("mobile", mobile);
        // map.put("head_img", "");
        AjaxParams param = new AjaxParams(map);
        showDialog();
        new FinalHttp().post(Constants.URL_POST_USERINFO, param, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                LogOut.debug("错误码：" + errorNo);
                dismissDialog();
                Toast.makeText(AccountInfoActivity.this, getString(R.string.network_failure), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(AccountInfoActivity.this, "保存成功!", Toast.LENGTH_SHORT).show();
                            et_name.setEnabled(false);
                            title_btn_edit.setBackgroundResource(R.drawable.icon_plus_mishuchuli);
                            title_btn_edit.setText("");

                        } else if (status == Constants.STATUS_SERVER_ERROR) { // 服务器错误
                            Toast.makeText(AccountInfoActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_MISS) { // 缺失必选参数
                            Toast.makeText(AccountInfoActivity.this, getString(R.string.param_missing), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_PARAM_ILLEGA) { // 参数值非法
                            Toast.makeText(AccountInfoActivity.this, getString(R.string.param_illegal), Toast.LENGTH_SHORT).show();
                        } else if (status == Constants.STATUS_OTHER_ERROR) { // 999其他错误
                            Toast.makeText(AccountInfoActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AccountInfoActivity.this, getString(R.string.servers_error), Toast.LENGTH_SHORT).show();
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
     * 封面相册显示上传头像对话框
     */
    private void showCoverAlbum() {
        if (!isEdit) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(AccountInfoActivity.this);
        builder.setCancelable(true);
        final String[] sex = { "拍照", "手机相册" };
        builder.setSingleChoiceItems(sex, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // 判断存储卡是否可以用，可用进行存储
                    if (Utils.isExistSD()) {

                        intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                    }

                    startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
                }
                if (which == 1) {
                    Intent intentFromGallery = new Intent();
                    intentFromGallery.setType("image/*"); // 设置文件类型
                    intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    /**
     * 修改头像对话框
     */
    private void showHeaderDlg() {
        if (!isEdit) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(AccountInfoActivity.this);
        builder.setCancelable(true);
        final String[] sex = { "拍照", "手机相册" };
        builder.setSingleChoiceItems(sex, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // 判断存储卡是否可以用，可用进行存储
                    if (Utils.isExistSD()) {

                        intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                    }

                    startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
                }
                if (which == 1) {
                    Intent intentFromGallery = new Intent();
                    intentFromGallery.setType("image/*"); // 设置文件类型
                    intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    /**
     * 修改性别对话框
     */
    private void showGenderDlg() {
        if (!isEdit) {
            return;
        }
        String gender = et_gender.getText().toString();
        int checkedItem = -1;
        if ("男".equals(gender)) {
            checkedItem = 0;
        } else if ("女".equals(gender)) {
            checkedItem = 1;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(AccountInfoActivity.this);
        builder.setCancelable(true);
        // builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("请选择性别");
        final String[] sex = { "男", "女" };
        builder.setSingleChoiceItems(sex, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                et_gender.setText(sex[which]);
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    /**
     * 退出登陆
     */
    private void logOut() {
        DBHelper.getInstance(AccountInfoActivity.this).deleteAll(User.class);
        DBHelper.getInstance(AccountInfoActivity.this).deleteAll(UserInfo.class);
        DBHelper.getInstance(AccountInfoActivity.this).deleteAll(CalendarMark.class);

        showDialog();
        EMDemoHelper.getInstance().logout(false, new EMCallBack() {
            @Override
            public void onSuccess() {
                AccountInfoActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        dismissDialog();
                        // 重新显示登陆页面
                        startActivity(new Intent(AccountInfoActivity.this, LoginActivity.class));
                        if (MainActivity.activity != null) {
                            MainActivity.activity.finish();
                        }
                        AccountInfoActivity.this.finish();
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                AccountInfoActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        dismissDialog();
                        Toast.makeText(AccountInfoActivity.this, "unbind devicetokens failed", Toast.LENGTH_SHORT).show();

                        // 重新显示登陆页面
                        startActivity(new Intent(AccountInfoActivity.this, LoginActivity.class));
                        if (MainActivity.activity != null) {
                            MainActivity.activity.finish();
                        }
                        AccountInfoActivity.this.finish();

                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 结果码不等于取消时候
        if (resultCode != RESULT_CANCELED) {

            switch (requestCode) {
            case IMAGE_REQUEST_CODE: // 从手机相册返回的
                startPhotoZoom(data.getData());
                break;
            case CAMERA_REQUEST_CODE: // 拍照返回的
                if (Utils.isExistSD()) {
                    File tempFile = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
                    startPhotoZoom(Uri.fromFile(tempFile));
                } else {
                    Toast.makeText(getApplicationContext(), "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
                }

                break;
            case RESULT_REQUEST_CODE:
                if (data != null) {
                    getImageToView(data);
                }
                break;
            case RESULT_UPDATE_USER:
                // 更新用户信息
                // updateUser(getUser());
                break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     * 
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }

    /**
     * 保存裁剪之后的图片数据
     * 
     * @param picdata
     */
    private void getImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo);
            iv_header.setImageDrawable(drawable);
            if (saveMyBitmap(photo)) {// 上传头像
                /*** 头像上传位置 **/
                showDialog();
                new Thread(uploadRunnable).start();
            }
        }
    }

    /**
     * 保存头像 转换成png格式
     * 
     * @param mBitmap
     * @return
     */
    public boolean saveMyBitmap(Bitmap mBitmap) {
        File filedir = new File(Constants.PATH_ROOT);
        if (!filedir.exists()) {
            filedir.mkdirs();
        }
        /*** 保存bitmap ***/
        File file = new File(Constants.PATH_ROOT, HEADER_IMAGE_NAME);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "保存图片时出错", Toast.LENGTH_SHORT).show();

            return false;
        }
        /*** bitmap to png ***/
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 头像上传线程
     */
    Runnable uploadRunnable = new Runnable() {

        @Override
        public void run() {
            Message msg = new Message();
            Bundle data = new Bundle();
            if (user == null) {
                Log.e("===", "error");
                return;
            }

            String user_id = user.getId();
            final Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", user_id);

            final Map<String, File> files = new HashMap<String, File>();
            File file = new File(Constants.PATH_ROOT + HEADER_IMAGE_NAME);
            files.put("file", file);
            String request = "";
            try {
                request = HTTPUtils.uploadPost(Constants.URL_POST_USERIMG, params, files);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                uploadHandler.sendEmptyMessage(0);
            }

            Log.e("HTTPUTILS 传头像", "头像上传----request:" + request);

            data.putString("request", request);
            msg.what = 1;
            msg.setData(data);
            uploadHandler.sendMessage(msg);
        }
    };

    Handler uploadHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
            case 0: // 上传头像时IO异常
                dismissDialog();
                Toast.makeText(getApplicationContext(), "上传失败", Toast.LENGTH_SHORT).show();
                break;
            case 1: // 请求成功，解析返回的Json
                dismissDialog();
                String request = msg.getData().getString("request");
                parseJsonUpload(request);
                break;

            default:
                break;
            }
        };
    };

    /**
     * 解析上传头像返回的信息
     */
    private void parseJsonUpload(String request) {
        JSONObject json;
        try {
            json = new JSONObject(request.toString());
            int status = Integer.parseInt(json.getString("status"));
            String data = json.getString("data");
            if (StringUtils.isNotEmpty(data)) {
                JSONObject obj = new JSONObject(data);
                String headImg = obj.getString("head_img");
                UserInfo userInfo = DBHelper.getUserInfo(AccountInfoActivity.this);
                userInfo.setHead_img(headImg);
                DBHelper.updateUserInfo(AccountInfoActivity.this, userInfo);
            }

            Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "服务器异常", Toast.LENGTH_SHORT).show();
        }
    }
}
