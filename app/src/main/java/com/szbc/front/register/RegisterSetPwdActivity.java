package com.szbc.front.register;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.szbc.android.R;
import com.szbc.base.BaseActivity;
import com.szbc.base.Config;
import com.szbc.base.MainActivity;
import com.szbc.dialog.MyCallback;
import com.szbc.dialog.MyDialog;
import com.szbc.tool.StatusBarUtil;
import com.szbc.tool.Validate;
import com.szbc.tool.config.EncryptedSharedPreferences;
import com.szbc.widget.MClearEditText;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 注册、填写密码
 */
public class RegisterSetPwdActivity extends BaseActivity {

    @BindView(R.id.view_need_offset)
    LinearLayout viewNeedOffset;
    @BindView(R.id.et_register_setpwd)
    MClearEditText etRegisterSetpwd;
    @BindView(R.id.et_register_setpwd_again)
    MClearEditText etRegisterSetpwdAgain;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_setpwd_activity);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT != 19 && Build.VERSION.SDK_INT != 20)
            StatusBarUtil.setTranslucentForImageView(this, 0, viewNeedOffset);
        context = this;
        initView();
    }
    public void initView() {
        if(getIntent()!=null && getIntent().getStringExtra("phone")!=null){
            phone = getIntent().getStringExtra("phone");
        }
    }
    @OnClick({R.id.title_back, R.id.ll_register_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.ll_register_submit:
                String pwd = etRegisterSetpwd.getText().toString();
                String pwdAgain = etRegisterSetpwdAgain.getText().toString();
                if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwdAgain)) {
                    showToast("密码不能为空", 2000);
                    return;
                }
                if (!Validate.validatePwd(pwd)) {
                    showToast("密码格式不正确，请设置字母加数字组成的至少8位的密码", 1200);
                    return;
                }
                if (!TextUtils.equals(pwd, pwdAgain)) {
                    showToast("两次输入的密码不一致", 2000);
                    return;
                }
                if (!TextUtils.isEmpty(pwd) && TextUtils.equals(pwd, pwdAgain)) {
                    submitPwd();
                }
                break;
        }
    }

    private void submitPwd() {
        String path = Config.httpIp + Config.Urls.register;
        params = new RequestParams(path);
        params.addParameter("mobile", phone);
        params.addParameter("password", etRegisterSetpwdAgain.getText().toString());
        params.setConnectTimeout(10 * 1000);
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
                                showToast("恭喜您已注册成功！", 2000);
//                                new EncryptedSharedPreferences(context).putString("token",object.getJSONObject("data").get("token").toString());
                                EncryptedSharedPreferences sp = new EncryptedSharedPreferences(context);
                                sp.putString("token",object.getJSONObject("data").get("token").toString());
                                sp.putBooleanValue("isLogined",true);
                                sp.putString("loginedPhone",phone);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent i = new Intent(RegisterSetPwdActivity.this, MainActivity.class);
                                        i.putExtra("mine", "");
                                        startActivity(i);
                                        RegisterSetPwdActivity.this.finish();
                                    }
                                }, 1000);
                            } else {
                                showToast(object.getString("message"), 2000);
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
}