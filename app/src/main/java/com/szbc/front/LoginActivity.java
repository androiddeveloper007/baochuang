package com.szbc.front;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.szbc.android.R;
import com.szbc.base.BaseActivity;
import com.szbc.base.Config;
import com.szbc.dialog.MyCallback;
import com.szbc.dialog.MyDialog;
import com.szbc.fragment.Mine;
import com.szbc.front.findLoginPwd.FindPwdActivity;
import com.szbc.front.register.RegisterActivity;
import com.szbc.tool.StatusBarUtil;
import com.szbc.tool.Validate;
import com.szbc.tool.config.EncryptedSharedPreferences;
import com.szbc.widget.MClearEditText;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 登录
 */
public class LoginActivity extends BaseActivity {

    @BindView(R.id.view_need_offset)
    LinearLayout viewNeedOffset;
    @BindView(R.id.et_username)
    MClearEditText etUsername;
    @BindView(R.id.et_pwd)
    MClearEditText etPwd;
    private final static int REQUEST_REGISTER = 12345;

    //QQ登录
    private Tencent mTencent;
    private BaseUiListener mIUiListener;
    private UserInfo mInfo;
    private String APPID = "1105023417";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);
        context = this;
        if (Build.VERSION.SDK_INT != 19 && Build.VERSION.SDK_INT != 20)
            StatusBarUtil.setTranslucentForImageView(this, 0, viewNeedOffset);

        initView();
    }

    public void initView() {
        mTencent = Tencent.createInstance(APPID, this.getApplication());
        EncryptedSharedPreferences sp = new EncryptedSharedPreferences(this);
        String phone = sp.getString("loginedPhone");
        if(!TextUtils.isEmpty(phone))
            etUsername.setText(phone);
    }

    @OnClick({R.id.title_back, R.id.tv_login_lose, R.id.layout_register, R.id.layout_login, R.id.iv_wechat_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.tv_login_lose:
                startActivity(new Intent(this, FindPwdActivity.class));
                break;
            case R.id.layout_register:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
            case R.id.layout_login:
                String phone = etUsername.getText().toString();
                if(TextUtils.isEmpty(phone)){showToast("账号不能为空",1500);return;}
                if(TextUtils.isEmpty(etPwd.getText().toString())){showToast("密码不能为空",1500);return;}
                if (!Validate.validatePhone(phone)) { showToast("手机号码格式不正确", 1200); return; }
                login();
//                setResult(Mine.RESULTLOGINFROMMAIN);
//                finish();
                break;
            case R.id.iv_wechat_login:
//                mIUiListener = new BaseUiListener();
//                mTencent.login(this, "get_user_info", mIUiListener);
                break;
        }
    }

    private void login() {
        String path = Config.httpIp + Config.Urls.login;
        params = new RequestParams(path);
        params.addParameter("deviceId", Config.getDeviceId(this));
        params.addParameter("mobile", etUsername.getText().toString());
        params.addParameter("password", etPwd.getText().toString());
        params.setConnectTimeout(10 * 1000);
        showLog(params.toString());
        mDialog = new MyDialog(this, "加载中...");
        mDialog.setDuration(300);
        mDialog.setMyCallback(new MyCallback() {
            @Override
            public void callback() { }
            @Override
            public void doing() {
                x.http().post(params, new Callback.CacheCallback<String>() {
                    @Override
                    public void onSuccess(String arg0) {
                        try {
                            JSONObject object = new JSONObject(arg0);
                            String result = object.getString("code");
                            if (result.equals("1")) {
//                                showToast("登录成功，跳转到个人中心",1000);
                                EncryptedSharedPreferences sp = new EncryptedSharedPreferences(context);
                                sp.putString("token",object.getJSONObject("data").get("token").toString());
                                sp.putBooleanValue("isLogined",true);
                                sp.putString("loginedPhone",etUsername.getText().toString());
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(getIntent()!=null && getIntent().getStringExtra("fromStore")!=null) {

                                        }else {
                                            setResult(Mine.RESULTLOGINFROMMAIN);
                                        }
                                        finish();
                                    }
                                }, 1000);
                            }  else {
                                showToast(object.getString("message"), 1000);
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
                        if (mDialog != null) {
                            mDialog.hideDialog();
                            d = null;
                        }
                    }
                    @Override
                    public boolean onCache(String result) {
                        return false;
                    }
                });
            }
        });
        mDialog.showDialog();
    }

    private class BaseUiListener implements IUiListener {
        @Override
        public void onComplete(Object response) {
            Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
            showLog("response:" + response);
            JSONObject jo = (JSONObject) response;
            try {
                String openID = jo.getString("openid");
                String accessToken = jo.getString("access_token");
                String expires = jo.getString("expires_in");
                mTencent.setOpenId(openID);
                mTencent.setAccessToken(accessToken, expires);
                QQToken qqToken = mTencent.getQQToken();
                mInfo = new UserInfo(getApplicationContext(), qqToken);
                mInfo.getUserInfo(new IUiListener() {
                    @Override
                    public void onComplete(Object response) {
                        showLog("成功" + response.toString());
                    }

                    @Override
                    public void onError(UiError uiError) {
                        showLog("失败" + uiError.toString());
                    }

                    @Override
                    public void onCancel() {
                        showLog("取消");
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {
            Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(getApplicationContext(), "登录取消", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN ||
                requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data, mIUiListener);
        }
        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode==REQUEST_REGISTER && resultCode == RESULT_OK){
//            setResult(Mine.RESULTLOGINFROMMAIN);
//            finish();
//        }
    }
}
