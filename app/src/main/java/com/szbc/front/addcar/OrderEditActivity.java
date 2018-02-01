package com.szbc.front.addcar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.szbc.android.R;
import com.szbc.base.BaseActivity;
import com.szbc.base.Config;
import com.szbc.dialog.LuckyDrawDialog_1;
import com.szbc.dialog.MyCallback;
import com.szbc.dialog.MyDialog;
import com.szbc.front.mine.myorder.MyOrderActivity;
import com.szbc.model.UserCar;
import com.szbc.tool.StatusBarUtil;
import com.szbc.tool.config.EncryptedSharedPreferences;
import com.szbc.widget.CheckableImageView;
import com.szbc.widget.CoolNumberKeyboard;
import com.szbc.widget.MClearEditText;
import com.szbc.widget.OnChoiceListener;
import com.szbc.widget.SlideSwitch;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 订单信息输入页
 */
public class OrderEditActivity extends BaseActivity implements OnDateSetListener {

    @BindView(R.id.view_need_offset)
    LinearLayout viewNeedOffset;
    @BindView(R.id.iv_navigation_bg)
    ImageView iv_navigation_bg;
    @BindView(R.id.iv_sex_man)
    CheckableImageView ivSexMan;
    @BindView(R.id.iv_sex_female)
    CheckableImageView ivSexFemale;
    @BindView(R.id.iv_order_brand)
    ImageView iv_order_brand;
    @BindView(R.id.tv_order_edit_yearStyle)
    TextView tvOrderEditYearStyle;
    @BindView(R.id.tv_order_range)
    TextView tvMileage;
    @BindView(R.id.tv_order_company)
    TextView tvOrderCompany;
    @BindView(R.id.tv_order_edit_brand)
    TextView tvOrderEditBrand;
    @BindView(R.id.tv_order_address)
    TextView tvOrderAddress;
    @BindView(R.id.et_order_upkeep_time)
    EditText etOrderRefreshTime;
    @BindView(R.id.et_order_car_number)
    MClearEditText etOrderCarNumber;
    @BindView(R.id.et_order_name)
    MClearEditText etOrderName;
    @BindView(R.id.et_order_phone)
    MClearEditText etOrderPhone;
    @BindView(R.id.ll_recharge)
    RelativeLayout ll_recharge;
    @BindView(R.id.scroll_layout)
    ScrollView scrollLayout;

    SlideSwitch mSlide_pattern;
    TimePickerDialog mDialogMonthDayHourMinute;
    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    CoolNumberKeyboard mKeyboard;
    final int SLIDE_GOHOME = 10000;
    public static final int RESULT_SLIDE_GOHOME = 10001;
    @BindView(R.id.ll_order_edit_commit)
    LinearLayout llOrderEditCommit;
    private EncryptedSharedPreferences sp;
    private UserCar userCar;
    private String busiShopId, busiShopName, busiShopPhone, busiShopAddress;
    private String carFetchTime;
    private String carFetchAddress;
    private String carDeliverAddress;
    private String fetchDeliverState;
    private boolean isAddressAlike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderedit_activity);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT != 19 && Build.VERSION.SDK_INT != 20)
            StatusBarUtil.setTranslucentForImageView(this, 0, viewNeedOffset);
        if (Build.VERSION.SDK_INT < 20)
            iv_navigation_bg.getLayoutParams().height = getResources().getDimensionPixelOffset(R.dimen.dimen_45);
        sp = new EncryptedSharedPreferences(this);
        context = this;
        initView();
        initData();
        /*ll_recharge.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                ll_recharge.getWindowVisibleDisplayFrame(rect);
                int mainInvisibleHeight = ll_recharge.getRootView().getHeight()
                        - rect.bottom;
                if (mainInvisibleHeight > 100) {
                    int[] location = new int[2];
                    etOrderPhone.getLocationInWindow(location);
                    int scrollHeight = (location[1]+etOrderPhone.getHeight())
                            -rect.bottom;
                    ll_recharge.scrollTo(0,scrollHeight);
                }else{
                    ll_recharge.scrollTo(0,0);
                }
            }
        });*/
    }

    public void initView() {
        mSlide_pattern = (SlideSwitch) findViewById(R.id.slide_pattern);
        mSlide_pattern.setSlideListener(new SlideSwitch.SlideListener() {
            @Override
            public void open() {
                Intent i = new Intent(OrderEditActivity.this, GetAndSendCarInfoActivity.class);
                i.putExtra("refreshTime", etOrderRefreshTime.getText().toString());
                startActivityForResult(i, SLIDE_GOHOME);
            }
            @Override
            public void close() {
            }
        });
        mDialogMonthDayHourMinute = new TimePickerDialog.Builder()
                .setThemeColor(getResources().getColor(R.color.btn_color))
                .setType(Type.MONTH_DAY_HOUR_MIN)
                .setCallBack(this)
                .setTitleStringId("")
                .build();
        setCarNumberEdit();
//        ivSexMan.setChecked(true);
        etOrderRefreshTime.setText(getDateToString(System.currentTimeMillis()));
        if(!TextUtils.isEmpty(sp.getString("loginedPhone")))
            etOrderPhone.setText(sp.getString("loginedPhone"));
    }

    private void setCarNumberEdit() {
        etOrderCarNumber.setHint("填写车牌号");
        etOrderCarNumber.setEnabled(true);
        mKeyboard = new CoolNumberKeyboard(this, false,
                new OnChoiceListener<String>() {
                    @Override
                    public void onChoice(String s) {
                        etOrderCarNumber.setText(s);
                    }
                });
    }

    private void initData() {
        if (getIntent() != null) {
            if (getIntent().getStringExtra("CarRange") != null) {
                String intentExtraCarRange = getIntent().getStringExtra("CarRange");
                if (!TextUtils.isEmpty(intentExtraCarRange))
                    tvMileage.setText("里程数：" + intentExtraCarRange);
                else
                    tvMileage.setVisibility(View.GONE);
            }
            if (getIntent().getParcelableExtra("userCar") != null) {
                userCar = getIntent().getParcelableExtra("userCar");
                //设置订单页面的数据
                showLog(userCar.getCarBrand());
                tvOrderEditBrand.setText(userCar.getCarBrand() + userCar.getCarModle());
                tvOrderEditYearStyle.setText(userCar.getCarYearStyle());
                tvMileage.setText("里程数:" + userCar.getCarRunKm());
                Config.loadImageXUtils(iv_order_brand, userCar.getBrandPictureUrl());
            }
            if (getIntent().getStringExtra("busiShopId") != null) {
                busiShopId = getIntent().getStringExtra("busiShopId");
            }
            if (getIntent().getStringExtra("busiShopName") != null) {
                busiShopName = getIntent().getStringExtra("busiShopName");
                tvOrderCompany.setText(busiShopName);
            }
            if (getIntent().getStringExtra("busiShopPhone") != null) {
                busiShopPhone = getIntent().getStringExtra("busiShopPhone");
            }
            if (getIntent().getStringExtra("busiShopAddress") != null) {
                busiShopAddress = getIntent().getStringExtra("busiShopAddress");
                tvOrderAddress.setText(busiShopAddress);
            }
            if (getIntent().getStringExtra("brandPicture") != null) {
                String brandPicture = getIntent().getStringExtra("brandPicture");
                Config.loadImageXUtils(iv_order_brand, brandPicture);
            }
        }
    }

    @OnClick({R.id.iv_sex_man, R.id.iv_sex_female, R.id.title_back, R.id.ll_order_edit_commit, R.id.et_order_upkeep_time, R.id.et_order_car_number})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.iv_sex_man:
                ivSexMan.setChecked(true);
                ivSexFemale.setChecked(false);
                break;
            case R.id.iv_sex_female:
                ivSexMan.setChecked(false);
                ivSexFemale.setChecked(true);
                break;
            case R.id.ll_order_edit_commit:
                if (TextUtils.isEmpty(etOrderRefreshTime.getText())) {showToast("请填写保养时间", 1200);return;}
                if (TextUtils.isEmpty(etOrderCarNumber.getText())) {showToast("请填写车牌号", 1200);return;}
                if (TextUtils.isEmpty(etOrderName.getText())) {showToast("请填写联系人姓名", 1200);return;}
                if (TextUtils.isEmpty(etOrderPhone.getText())) {showToast("请填写联系电话", 1200);return;}
                //立即预约
                requestOrder(true);
                break;
            case R.id.et_order_upkeep_time:
                mDialogMonthDayHourMinute.show(getSupportFragmentManager(), "month_day_hour_minute");
                break;
            case R.id.et_order_car_number:
                mKeyboard.showKeyBoard(getWindow().getDecorView()
                        .getRootView(), etOrderCarNumber.getText().toString());
                break;
            default:
                break;
        }
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerDialog, long millseconds) {
        etOrderRefreshTime.setText(getDateToString(millseconds));
    }

    public String getDateToString(long time) {
        Date d = new Date(time);
        return sf.format(d);
    }

    private void requestOrder(final boolean b) {
        if(b){
            mDialog = new MyDialog(this, "加载中...");
            mDialog.setDuration(300);
            mDialog.setMyCallback(new MyCallback() {
                @Override
                public void callback() {
                }
                @Override
                public void doing() {
                    postData();
                }
            });
            if (mDialog != null)
                mDialog.showDialog();
        }else{
            postData();
        }
    }

    private void postData() {
        String path = Config.httpIp + Config.Urls.saveOrderMaintList;
        params0 = new RequestParams(path);
        JSONObject js_request0 = new JSONObject();
        try {
            String token = sp.getString("token");
            if (TextUtils.isEmpty(token)) {showToast("请先登录", 2000);return;}
            if (userCar == null) {showToast("usercar is null", 2000);return;}
            js_request0.put("token", token);
            js_request0.put("busiShopId", busiShopId);
            js_request0.put("busiShopName", busiShopName);
            js_request0.put("busiShopPhone", busiShopPhone);
            js_request0.put("receiverName", etOrderName.getText().toString());
            js_request0.put("receiverPhone", etOrderPhone.getText().toString());//
            js_request0.put("isDoorService", mSlide_pattern.getState() ? "1" : "2");//
            js_request0.put("userCarId", userCar.getId());
            js_request0.put("carBrandName", userCar.getCarBrand());
            js_request0.put("carBrandType", userCar.getCarBrandType());
            js_request0.put("carModleName", userCar.getCarModle());
            js_request0.put("carYearstyleName", userCar.getCarYearStyle());
            js_request0.put("carNumber", etOrderCarNumber.getText().toString());
            js_request0.put("carRunKm", userCar.getCarRunKm());
            js_request0.put("maintTime", etOrderRefreshTime.getText().toString());
            js_request0.put("orderInputType", "1");//1:android
            js_request0.put("receiverSex", ivSexMan.isChecked() ? "1" : "2");
            if (mSlide_pattern.getState()) {
                js_request0.put("carFetchTime", carFetchTime);
                js_request0.put("isAddressAlike", isAddressAlike ? "1" : "2");
                js_request0.put("carFetchAddress", carFetchAddress);
                js_request0.put("carDeliverAddress", carDeliverAddress);
                js_request0.put("fetchDeliverState", fetchDeliverState);//送车状态 1：代驾取车 2：代驾还车 3：代驾取车还车'
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        showLog(js_request0.toString());
        params0.setAsJsonContent(true);
        params0.setBodyContent(js_request0.toString());
        params0.setConnectTimeout(10 * 1000);
        x.http().post(params0, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String arg0) {
                try {
                    final JSONObject object = new JSONObject(arg0);
                    String result = object.getString("code");
                    String msg = object.getString("message");
                    if (TextUtils.equals("1", result)) {
                        showToast("下单成功！", 1200);//msg
                        //进入我的订单
                        Intent i = new Intent(context, MyOrderActivity.class);
                        startActivity(i);
                        finish();
                    } else if (TextUtils.equals("-1", result)) {
                        showToast(msg, 1200);
                    } else if (TextUtils.equals("-3", result)) {
                        showToast(msg, 1200);
                        LuckyDrawDialog_1 d = new LuckyDrawDialog_1(context, true, 0.8);
                        d.setMessage("确认取消订单？", true, R.dimen.dimen_20);
                        d.setBtn1Text("取消");
                        d.setBtn2Text("确认");
                        d.setTitleVisible(false, "");
                        d.setTextIvestVisible(false);
                        d.show();
                        d.setOnBtn2ClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //请求后台，取消当前订单
                                if (object.has("data")) {
                                    try {
                                        JSONObject data = new JSONObject(object.getString("data"));
                                        String id = "";
                                        if (data.has("id"))
                                            id = data.getString("id");
                                        requestCancelOrder(id);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
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
            }
            @Override
            public boolean onCache(String result) {
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SLIDE_GOHOME) {
            if (resultCode == RESULT_SLIDE_GOHOME && data != null) {
                mSlide_pattern.initState(true);
                if (data.getStringExtra("carFetchTime") != null && !TextUtils.isEmpty(data.getStringExtra("carFetchTime")))
                    carFetchTime = data.getStringExtra("carFetchTime");
                if (data.getStringExtra("carFetchAddress") != null && !TextUtils.isEmpty(data.getStringExtra("carFetchAddress")))
                    carFetchAddress = data.getStringExtra("carFetchAddress");
                if (data.getStringExtra("carDeliverAddress") != null && !TextUtils.isEmpty(data.getStringExtra("carDeliverAddress")))
                    carDeliverAddress = data.getStringExtra("carDeliverAddress");
                if (data.hasExtra("isAddressAlike"))
                    isAddressAlike = data.getBooleanExtra("isAddressAlike", false);
                if (data.hasExtra("fetchDeliverState"))
                    fetchDeliverState = data.getStringExtra("fetchDeliverState");
            } else {
                mSlide_pattern.initState(false);
            }
        }
    }

    private void requestCancelOrder(String orderId) {
        String path = Config.httpIp + Config.Urls.udpateOrderStatus;
        params = new RequestParams(path);
        params.addParameter("id", orderId);
        params.addParameter("orderState", "9");
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
                            JSONObject object = new JSONObject(arg0);
                            String result = object.getString("code");
                            String msg = object.getString("message");
                            if (result.equals("1")) {
                                showToast(msg, 2000);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDialog=null;
    }
}