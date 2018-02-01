package com.szbc.front.findLoginPwd;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.szbc.android.R;
import com.szbc.base.BaseActivity;
import com.szbc.base.Config;
import com.szbc.dialog.MyCallback;
import com.szbc.dialog.MyDialog;
import com.szbc.tool.StatusBarUtil;
import com.szbc.tool.Validate;
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
 * 重新设置密码
 */
public class ResetPwdActivity extends BaseActivity {
    @BindView(R.id.view_need_offset)
    LinearLayout viewNeedOffset;
    @BindView(R.id.et_resetpwd_new)
    MClearEditText etResetpwdNew;
    @BindView(R.id.et_resetpwd_confirm)
    MClearEditText etResetpwdConfirm;
    private String phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_pwd_activity);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT != 19 && Build.VERSION.SDK_INT != 20)
            StatusBarUtil.setTranslucentForImageView(this, 0, viewNeedOffset);
        context = this;
        if(getIntent()!=null && getIntent().getStringExtra("phone")!=null){
            phone = getIntent().getStringExtra("phone");
        }
    }
    @OnClick({R.id.title_back, R.id.ll_resetpwd_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.ll_resetpwd_next:
                String pwd = etResetpwdNew.getText().toString();
                String pwdAgain = etResetpwdConfirm.getText().toString();
                if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwdAgain)) {
                    showToast("密码不能为空", 2000);
                    return;
                }
                if (!Validate.validatePwd(pwd)) {
                    showToast("密码格式不正确", 1200);
                    return;
                }
                if (!TextUtils.equals(pwd, pwdAgain)) {
                    showToast("两次输入的密码不一致", 2000);
                    return;
                }
                if (!TextUtils.isEmpty(pwd) && TextUtils.equals(pwd, pwdAgain)) {
                    resetPwd();
                    /*ToastMakeText.showToast(this, "修改成功,回到登录页", 2000);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ResetPwdActivity.this.finish();
                        }
                    }, 2000);*/
                }
                break;
        }
    }
    private void resetPwd() {
        String path = Config.httpIp + Config.Urls.updatePwd;
        params = new RequestParams(path);
//        params.addParameter("usedPwd", "aaaa1111");
        params.addParameter("mobile", phone);
        params.addParameter("freshPwd", etResetpwdNew.getText().toString());
//        params.addParameter("token", new EncryptedSharedPreferences(this).getString("token"));
        params.addParameter("type", "2");
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
                                showToast("密码修改成功",1000);
                                finish();
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
}