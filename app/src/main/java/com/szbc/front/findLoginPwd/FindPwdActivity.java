package com.szbc.front.findLoginPwd;

import android.content.Intent;
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
 * 找回密码
 */
public class FindPwdActivity extends BaseActivity {
    @BindView(R.id.view_need_offset)
    LinearLayout viewNeedOffset;
    @BindView(R.id.et_find_pwd)
    MClearEditText etFindPwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findpwd_activity);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT != 19 && Build.VERSION.SDK_INT != 20)
            StatusBarUtil.setTranslucentForImageView(this, 0, viewNeedOffset);
        context = this;
    }
    @OnClick({R.id.title_back, R.id.ll_findpwd_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.ll_findpwd_next:
                String phone = etFindPwd.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    showToast("手机号码不能为空", 1200);
                    return;
                }
                if (!Validate.validatePhone(phone)) {
                    showToast("手机号码格式不正确", 1200);
                    return;
                }
                //请求后台得到短信验证码
                requestSmsCode();
                /*showToast("请求验证码成功，跳往填写页",1000);
                Intent i = new Intent(context, FindPwdSMSActivity.class);
                i.putExtra("phone",etFindPwd.getText().toString());
                startActivity(i);
                finish();*/
                break;
            default:break;
        }
    }
    private void requestSmsCode() {
        String path = Config.httpIp + Config.Urls.sendSMSCode;
        params = new RequestParams(path);
        params.addParameter("mobiles", etFindPwd.getText().toString());
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
                                showToast("验证码发送成功",1000);
                                Intent i = new Intent(context, FindPwdSMSActivity.class);
                                i.putExtra("phone",etFindPwd.getText().toString());
                                startActivity(i);
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