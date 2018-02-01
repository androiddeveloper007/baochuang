package com.szbc.front.mine.myorder;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.szbc.android.R;
import com.szbc.base.BaseActivity;
import com.szbc.base.Config;
import com.szbc.dialog.MyCallback;
import com.szbc.dialog.MyDialog;
import com.szbc.tool.StatusBarUtil;
import com.szbc.tool.payment.alipay.OnPayListener;
import com.szbc.tool.payment.alipay.Payment;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 支付订单
 */
public class MyPaymentOrderActivity extends BaseActivity {

    @BindView(R.id.view_need_offset)
    LinearLayout viewNeedOffset;
    @BindView(R.id.iv_navigation_bg)
    ImageView iv_navigation_bg;
    @BindView(R.id.radio_group_pay)
    RadioGroup radio_group_pay;
    @BindView(R.id.tv_done_detail_1)
    TextView tvDoneDetail1;
    @BindView(R.id.tv_done_detail_2)
    TextView tvDoneDetail2;
    @BindView(R.id.tv_done_detail_3)
    TextView tvDoneDetail3;
    @BindView(R.id.ll_btn_pay_finally)
    LinearLayout llBtnPayFinally;
    @BindView(R.id.tv_done_detail_20)
    TextView tvDoneDetail20;
    @BindView(R.id.tv_done_detail_20_1)
    TextView tvDoneDetail201;

    private int secondTotal = 30 * 60;
    private int hours = 0;
    private int minutes = 30;
    private int seconds = 0;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            hours = secondTotal / 60 / 60 % 60;
            minutes = secondTotal / 60 % 60;
            seconds = secondTotal % 60;
            tvDoneDetail1.setText(hours + "时");
            tvDoneDetail2.setText(minutes + "分");
            tvDoneDetail3.setText(seconds + "秒");
            secondTotal--;
            handler.postDelayed(runnable, 1000);
            if (secondTotal == -1) {
                handler.removeCallbacks(runnable);
            }
        }
    };
    private boolean payCash;
    private int payType=1;
    private String orderNum;
    private String priceTotal;
    private String partIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_order_activity);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT != 19 && Build.VERSION.SDK_INT != 20)
            StatusBarUtil.setTranslucentForImageView(this, 0, viewNeedOffset);
        if (Build.VERSION.SDK_INT < 20)
            iv_navigation_bg.getLayoutParams().height = getResources().getDimensionPixelOffset(R.dimen.dimen_45);

        context = this;
        tvDoneDetail1.setText(hours + "时");
        tvDoneDetail2.setText(minutes + "分");
        tvDoneDetail3.setText(seconds + "秒");
        handler.postDelayed(runnable, 1000);
        initView();
        initData();
    }

    private void initView() {
        radio_group_pay.clearCheck();
        radio_group_pay.check(R.id.rdb_wx);
        radio_group_pay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rdb_wx:
//                        showToast("当前选中支付方式：" + "微信", 1000);
                        payCash = false;
                        payType = 1;
                        break;
                    case R.id.rdb_bank_card:
//                        showToast("当前选中支付方式：" + "银联", 1000);
                        payCash = false;
                        payType=4;
                        break;
                    case R.id.rdb_ali:
//                        showToast("当前选中支付方式：支付宝", 1000);
                        payCash = false;
                        payType=2;
                        break;
                    case R.id.rdb_cash:
                        payCash = true;
                        payType=3;
                        break;
                    default:break;
                }
            }
        });
        orderNum = getIntent().getStringExtra("orderNum");
        priceTotal = getIntent().getStringExtra("priceTotal");
        partIds = getIntent().getStringExtra("partIds");
        tvDoneDetail20.setText("订单号码：" + orderNum);
        tvDoneDetail201.setText("¥" + priceTotal);
    }

    private void initData() {

    }

    @OnClick({R.id.title_back, R.id.ll_btn_pay_finally})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.ll_btn_pay_finally:
                //调支付宝
//                requestAliPay();
                requestChangeState();
                break;
            default:break;
        }
    }

    private void requestChangeState() {
        String path = Config.httpIp + Config.Urls.payment;//udpateOrderStatus
        params = new RequestParams(path);
        params.addParameter("orderId", getIntent().getStringExtra("orderId"));//id
        /*if(payCash)
            params.addParameter("orderState", "14");
        else
            params.addParameter("orderState", "4");*/
        if(!TextUtils.isEmpty(partIds))
            params.addParameter("partIds", partIds);
        params.addParameter("money", priceTotal);
        params.addParameter("payType", payType);
        params.setConnectTimeout(10 * 1000);
        showLog(params.toString());
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
                            showLog(arg0);
                            JSONObject object = new JSONObject(arg0);
                            String result = object.getString("code");
                            String msg = object.getString("message");
                            if (result.equals("1")) {
//                                showToast(msg, 1000);
                                showToast("支付成功！", 1200);
                                setResult(MyOrderDonePaymentActivity.RESPONSE_CODE_PAY);
                                finish();
                            } else {
                                showToast(msg, 1000);
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

    private void requestAliPay() {
        String sellerEmail = "";
        String partner = "";
        String recordId = "";
        String subject = "";
        String body = "";
        String notifyUrl = "";
        String totalFee = "";
        String rsaPrivate = "";
        Payment.startAliPay((Activity) context, Payment.getAlipayInfo(sellerEmail, partner,
                recordId, subject, body, notifyUrl, totalFee, rsaPrivate), new OnPayListener() {
            @Override
            public void onSuccess(String resultCode, String msg) {
                llBtnPayFinally.setClickable(true);
                //进入支付成功页面
                showToast("支付成功", 2000);
//                        UITools.startToNextActivity(PettyCashRechargeActivity.this, PayResultActivity.class, PayResultActivity.MONEY, money);
//                        setResult(RESULT_OK, getIntent().putExtra(IntentExtra.PAY_RESULT, Payment.SUCCESS));
                finish();
            }

            @Override
            public void onFail(Exception e, String errorMsg) {
                llBtnPayFinally.setClickable(true);
                showToast(errorMsg, 2000);
            }
        });
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        handler = null;
        runnable = null;
    }
}
