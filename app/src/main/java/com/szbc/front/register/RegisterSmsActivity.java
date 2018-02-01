package com.szbc.front.register;

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
 * 注册、发送短信
 */
public class RegisterSmsActivity extends BaseActivity {
    @BindView(R.id.view_need_offset)
    LinearLayout viewNeedOffset;
    @BindView(R.id.et_register_sms)
    MClearEditText etRegisterSms;
    @BindView(R.id.tv_smsCode_second)
    SmsSecondView tv_smsCode_second;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_sms_activity);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT != 19 && Build.VERSION.SDK_INT != 20)
            StatusBarUtil.setTranslucentForImageView(this, 0, viewNeedOffset);
        initData();
    }

    private void initData() {
        context = this;
        tv_smsCode_second.startSend();
        if(getIntent()!=null && getIntent().getStringExtra("phone")!=null){
            phone = getIntent().getStringExtra("phone");
        }
    }

    @OnClick({R.id.title_back, R.id.ll_register_sms, R.id.tv_smsCode_second})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.ll_register_sms:
                verifyCode();
                /*showToast("验证码验证成功", 2000);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(RegisterSmsActivity.this, RegisterSetPwdActivity.class);
                        if (getIntent() != null && getIntent().getStringExtra("phone") != null) {
                            String phone = getIntent().getStringExtra("phone");
                            i.putExtra("phone", phone);
                        }
                        startActivity(i);
                        finish();
                    }
                }, 2000);*/
                break;
            case R.id.tv_smsCode_second:
                tv_smsCode_second.startSend();
                break;
            default:
                break;
        }
    }

    private void verifyCode() {
        String path = Config.httpIp + Config.Urls.verifySmsCode;
        params = new RequestParams(path);
        params.addParameter("mobiles", phone);
        params.addParameter("randomCode", etRegisterSms.getText().toString());
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
                                showToast("验证码校验成功", 2000);//，正在进入密码设置界面
                                Intent i = new Intent(RegisterSmsActivity.this, RegisterSetPwdActivity.class);
                                i.putExtra("phone", phone);
                                startActivity(i);
                                finish();
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