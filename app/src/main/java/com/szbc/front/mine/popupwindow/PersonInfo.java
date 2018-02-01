package com.szbc.front.mine.popupwindow;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.szbc.android.R;
import com.szbc.tool.base64.Base64ImageUtil;
import com.szbc.base.BaseActivity;
import com.szbc.base.Config;
import com.szbc.fragment.Mine;
import com.szbc.front.LoginActivity;
import com.szbc.model.User;
import com.szbc.tool.AsyncTaskUtil;
import com.szbc.tool.StatusBarUtil;
import com.szbc.tool.config.EncryptedSharedPreferences;
import com.szbc.widget.iconupload.CircleImageView;
import com.szbc.widget.iconupload.ClipImageActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 个人资料
 */
public class PersonInfo extends BaseActivity {
    //请求相机
    private static final int REQUEST_CAPTURE = 100;
    //请求相册
    private static final int REQUEST_PICK = 101;
    //请求截图
    private static final int REQUEST_CROP_PHOTO = 102;
    //请求访问外部存储
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 103;
    //请求写入外部存储
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 104;
    //调用照相机返回图片临时文件
    private File tempFile;
    @BindView(R.id.iv_mapdemo)
    CircleImageView ivMapdemo;
    @BindView(R.id.view_need_offset)
    LinearLayout viewNeedOffset;
    @BindView(R.id.tv_person_info_phone)
    TextView tvPersonInfoPhone;
    private EncryptedSharedPreferences sp;
    AsyncTaskUtil<String, RequestParams> mAsyncTaskUtils;
    private Callback.Cancelable cancelable;
    private boolean success=false;//时候成功设置头像
    private String token;
    private String cropImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_info);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT != 19 && Build.VERSION.SDK_INT != 20)
            StatusBarUtil.setTranslucentForImageView(this, 0, viewNeedOffset);
        context = this;
        sp = new EncryptedSharedPreferences(this);
        if(sp.getString("token")!=null)
            token = sp.getString("token");

        if(TextUtils.isEmpty(token)){
            showToast("请登录",2000);
            startActivity(new Intent(context,LoginActivity.class));
            finish();
            return;
        }
        //创建拍照存储的临时文件
        createCameraTempFile(savedInstanceState);
        initView();
    }

    public void initView() {
        //根据个人中心获取的user对象，显示个人资料
        sp = new EncryptedSharedPreferences(this);
        User user = sp.getUser();
        if(user == null){
            showToast("user对象为空",2000);
            return;
        }
        Glide.with(this)
                .load(user.getUserPictureUrl())
                .placeholder(R.drawable.icon_stub)
                .error(R.drawable.icon_stub)
                .into(ivMapdemo);
        tvPersonInfoPhone.setText(user.getUserPhone());
    }

    private void uploadHeadImage() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_popupwindow, null);
        TextView btnCarema = (TextView) view.findViewById(R.id.btn_camera);
        TextView btnPhoto = (TextView) view.findViewById(R.id.btn_photo);
        TextView btnCancel = (TextView) view.findViewById(R.id.btn_cancel);
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
        popupWindow.setOutsideTouchable(true);
        View parent = LayoutInflater.from(this).inflate(R.layout.activity_person_info, null);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        //popupWindow在弹窗的时候背景半透明
        final WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 0.5f;
        getWindow().setAttributes(params);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                params.alpha = 1.0f;
                getWindow().setAttributes(params);
            }
        });

        btnCarema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //权限判断
                if (ContextCompat.checkSelfPermission(PersonInfo.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(PersonInfo.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                } else {
                    //跳转到调用系统相机
                    gotoCamera();
                }
                popupWindow.dismiss();
            }
        });
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //权限判断
                if (ContextCompat.checkSelfPermission(PersonInfo.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请READ_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(PersonInfo.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            READ_EXTERNAL_STORAGE_REQUEST_CODE);
                } else {
                    //跳转到调用系统图库
                    gotoPhoto();
                }
                popupWindow.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    private void uploadImage0(final String imgUrl) {
        String url = Config.httpIp + Config.Urls.uploadImage;
        final RequestParams params = new RequestParams(url);
        params.setMultipart(true);
        params.addBodyParameter("token", token);
        params.addBodyParameter("pictureUrl", new File(imgUrl), null);
        cancelable = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                showLog("result:"+result);
                try {
                    JSONObject object = new JSONObject(result);
                    String code = object.getString("code");
                    String msg = object.getString("message");
                    if (code.equals("1")) {
                        showToast(msg, 2000);
                        String imgUrl = object.getString("data");
                        Glide.with(context)
                                .load(imgUrl)
                                .error(R.drawable.icon_stub)
                                .into(ivMapdemo);
                        success = true;
                    } else {
                        showToast(msg, 2000);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                showToast("服务器连接失败", 2000);
            }
            @Override
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
                success=true;
                new EncryptedSharedPreferences(context).putString("userImgUri",imgUrl);
                Glide.with(context)
                        .load("file://" + imgUrl)
                        .error(R.drawable.mine_user)
                        .into(ivMapdemo);
            }
        });
    }

    private void uploadImage(String url){
        String code = Base64ImageUtil.getImageStrByImgPath(url);
        String path = Config.httpIp + Config.Urls.updateUserMess;
        params = new RequestParams(path);
        params.addBodyParameter("text", code);
        params.addBodyParameter("token", token);
        params.setConnectTimeout(10 * 1000);
        showLog(params.toString());
        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String arg0) {
                try {
                    showLog(arg0);
                    JSONObject object = new JSONObject(arg0);
                    String code = object.getString("code");
                    String msg = object.getString("message");
                    if (code.equals("1")) {
                        showToast(msg, 2000);
                        String imgUrl = object.getString("data");
                        Glide.with(context)
                                .load(imgUrl)
                                .error(R.drawable.icon_stub)
                                .into(ivMapdemo);
                        success = true;
                    } else {
                        showToast(msg, 2000);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                showToast("服务器连接失败", 1000);
            }
            @Override
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
            }
            @Override
            public boolean onCache(String result) {
                return false;
            }
        });
    }
    @OnClick({R.id.title_back,R.id.rl_user_icon_setting})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back:
                if(success){
                    Intent i = new Intent();
                    i.putExtra("imgUrl",cropImagePath);
                    setResult(Mine.RESULT_INFO_CODE,i);
                }
                this.finish();
                break;
            case R.id.rl_user_icon_setting:
                uploadHeadImage();
                break;
            default:break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQUEST_CAPTURE: //调用系统相机返回
                if (resultCode == RESULT_OK) {
                    gotoClipActivity(Uri.fromFile(tempFile));
                }
                break;
            case REQUEST_PICK:  //调用系统相册返回
                if (resultCode == RESULT_OK) {
                    Uri uri = intent.getData();
                    gotoClipActivity(uri);
                }
                break;
            case REQUEST_CROP_PHOTO:  //剪切图片返回
                if (resultCode == RESULT_OK) {
                    final Uri uri = intent.getData();
                    if (uri == null) {
                        return;
                    }
                    //得到的剪切后的图片uri
                    cropImagePath = getRealFilePathFromUri(getApplicationContext(), uri);
                    Bitmap bitMap = BitmapFactory.decodeFile(cropImagePath);
                    ivMapdemo.setImageBitmap(bitMap);
                    //此处后面可以将bitMap转为二进制上传后台网络
                    uploadImage(cropImagePath);
                }
                break;
        }
    }

    /**
     * 访问网络AsyncTask,访问网络在子线程进行并返回主线程通知访问的结果
     */
    class UploadImgTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            return doAsyncPost(params[0]);
        }
        @Override
        protected void onPostExecute(String result) {
            if(!"error".equals(result)) {
                Glide.with(context)
                        .load(result)
                        .into(ivMapdemo);
            }
        }
    }

    private String doAsyncPost(String param) {
        return null;
    }

    /**
     * 打开截图界面
     */
    public void gotoClipActivity(Uri uri) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(this, ClipImageActivity.class);
        intent.putExtra("type", 1);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_CROP_PHOTO);
    }

    public static String getRealFilePathFromUri(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("tempFile", tempFile);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                gotoCamera();
            } else {
            }
        } else if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                gotoPhoto();
            } else {
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(success){
                Intent i = new Intent();
                i.putExtra("imgUrl",cropImagePath);
                setResult(Mine.RESULT_INFO_CODE,i);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void gotoCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        startActivityForResult(intent, REQUEST_CAPTURE);
    }

    /**
     * 跳转到相册
     */
    private void gotoPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "请选择图片"), REQUEST_PICK);
    }

    /**
     * 创建调用系统照相机待存储的临时文件
     *
     * @param savedInstanceState
     */
    private void createCameraTempFile(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey("tempFile")) {
            tempFile = (File) savedInstanceState.getSerializable("tempFile");
        } else {
            tempFile = new File(checkDirPath(Environment.getExternalStorageDirectory().getPath() + "/image/"),
                    System.currentTimeMillis() + ".jpg");
        }
    }

    /**
     * 检查文件是否存在
     */
    private static String checkDirPath(String dirPath) {
        if (TextUtils.isEmpty(dirPath)) {
            return "";
        }
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath;
    }
}