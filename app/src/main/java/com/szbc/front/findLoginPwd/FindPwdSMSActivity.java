package com.szbc.front.findLoginPwd;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.szbc.android.R;
import com.szbc.base.BaseActivity;
import com.szbc.base.Config;
import com.szbc.dialog.MyCallback;
import com.szbc.dialog.MyDialog;
import com.szbc.tool.StatusBarUtil;
import com.szbc.widget.MClearEditText;
import com.szbc.widget.SmsSecondView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 找回密码、验证码
 */
public class FindPwdSMSActivity extends BaseActivity {
    @BindView(R.id.view_need_offset)
    LinearLayout viewNeedOffset;
    @BindView(R.id.tv_smsCode_second)
    SmsSecondView tv_smsCode_second;
    @BindView(R.id.et_findpwd_sms_pwd)
    MClearEditText etFindpwdSmsPwd;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findpwd_sms_activity);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT != 19 && Build.VERSION.SDK_INT != 20)
            StatusBarUtil.setTranslucentForImageView(this, 0, viewNeedOffset);
        context = this;
        if (getIntent() != null && getIntent().getStringExtra("phone") != null) {
            phone = getIntent().getStringExtra("phone");
        }
        tv_smsCode_second.startSend();
    }

    @OnClick({R.id.title_back, R.id.ll_findpwd_sms_next, R.id.tv_smsCode_second})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.ll_findpwd_sms_next:
                //后台校验短信验证码，成功后跳到设置密码页
                verifyCode();
                /*showToast("校验验证码成功，跳往设置密码页", 1000);
                Intent i = new Intent(context, ResetPwdActivity.class);
                i.putExtra("phone", phone);
                startActivity(i);
                finish();*/
                break;
            case R.id.tv_smsCode_second:
                tv_smsCode_second.startSend();
                break;
            default:break;
        }
    }

    private void verifyCode() {
        String path = Config.httpIp + Config.Urls.verifySmsCode;
        params = new RequestParams(path);
        params.addParameter("randomCode", etFindpwdSmsPwd.getText().toString());
        params.addParameter("mobiles", phone);
        params.setConnectTimeout(10 * 1000);
        mDialog = new MyDialog(this, "加载中...");
        mDialog.setDuration(300);
        mDialog.setMyCallback(new MyCallback() {
            @Override
            public void callback() {
            }
            @Override
            public void doing() {
                x.http().post(params, new Callback.CacheCallback<String>() {
                    @Override
                    public void onSuccess(String arg0) {
                        try {
                            JSONObject object = new JSONObject(arg0);
                            String result = object.getString("code");
                            if (result.equals("1")) {
                                showToast("校验验证码成功", 1200);
                                Intent i = new Intent(context, ResetPwdActivity.class);
                                i.putExtra("phone", phone);
                                startActivity(i);
                                finish();
                            } else {
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