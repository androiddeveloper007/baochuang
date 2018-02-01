package com.szbc.front.mine.popupwindow;

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
import com.szbc.dialog.MyCallback;
import com.szbc.dialog.MyDialog;
import com.szbc.front.LoginActivity;
import com.szbc.tool.StatusBarUtil;
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

/*
* 修改密码
* */
public class UpdatePwdActivity extends BaseActivity {

    @BindView(R.id.view_need_offset)
    LinearLayout viewNeedOffset;
    @BindView(R.id.et_change_pwd0)
    MClearEditText etChangePwd0;
    @BindView(R.id.et_change_pwd1)
    MClearEditText etChangePwd1;
    @BindView(R.id.et_change_pwd2)
    MClearEditText etChangePwd2;
    private String token;
    private EncryptedSharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT != 19 && Build.VERSION.SDK_INT != 20)
            StatusBarUtil.setTranslucentForImageView(this, 0, viewNeedOffset);
        context = this;
        sp = new EncryptedSharedPreferences(this);
        token = sp.getString("token");
        if(TextUtils.isEmpty(token)){
            showToast("请登录",2000);
            startActivity(new Intent(context,LoginActivity.class));
            finish();
            return;
        }
    }

    @OnClick({R.id.title_back, R.id.ll_update_pwd})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.ll_update_pwd:
                String str0 = etChangePwd0.getText().toString();
                String str1 = etChangePwd1.getText().toString();
                String str2 = etChangePwd2.getText().toString();
                if(TextUtils.isEmpty(str0))
                    showToast("旧密码不能为空",2000);
                if(TextUtils.isEmpty(str1))
                    showToast("新密码不能为空",2000);
                if(TextUtils.isEmpty(str2))
                    showToast("确认密码不能为空",2000);
                if(!TextUtils.equals(str1,str2))
                    showToast("新密码和确认密码不一致",2000);
                if(!TextUtils.isEmpty(str0) && !TextUtils.isEmpty(str1) && TextUtils.equals(str1,str2)){
                    updatePwd();
                }
                break;
        }
    }

    private void updatePwd() {
        String path = Config.httpIp + Config.Urls.updatePwd;
        params = new RequestParams(path);
        EncryptedSharedPreferences sp = new EncryptedSharedPreferences(this);
        params.addParameter("token", sp.getString("token"));
        params.addParameter("type", "1");
        params.addParameter("usedPwd", etChangePwd0.getText().toString());
        params.addParameter("freshPwd", etChangePwd2.getText().toString());
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
                            String message = object.getString("message");
                            if (result.equals("1")) {
                                showToast("密码修改成功！",1000);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 1000);
                            }  else if(TextUtils.equals(result,"-4")){
                                showToast(message, 2000);
                                startActivity(new Intent(context,LoginActivity.class));
                            }else {
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