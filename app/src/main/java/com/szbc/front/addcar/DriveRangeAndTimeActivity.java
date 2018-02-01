package com.szbc.front.addcar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.szbc.android.R;
import com.szbc.base.BaseActivity;
import com.szbc.base.Config;
import com.szbc.dialog.MyCallback;
import com.szbc.dialog.MyDialog;
import com.szbc.model.UserCar;
import com.szbc.tool.StatusBarUtil;
import com.szbc.tool.config.EncryptedSharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.szbc.front.addcar.CarBrandActivity.REQUESTCODEFINISH;
import static com.szbc.front.addcar.CarBrandActivity.RESPONSECODEFINISH;
import static com.szbc.front.mine.MyCarsActivity.RESULT_CODE_REEDIT;

/**
 * 汽车行程和购买时间输入
 */
public class DriveRangeAndTimeActivity extends BaseActivity {
    @BindView(R.id.view_need_offset)
    LinearLayout viewNeedOffset;
    @BindView(R.id.et_car_time)
    EditText etCarTime;
    @BindView(R.id.et_range)
    EditText etRange;
    @BindView(R.id.iv_driverange_logo)
    ImageView ivDriverangeLogo;

    private TimePickerView pvCustomTime;
    String brandId, modleId, yearstyleId, brandName, modleName, yearstyleName, brandTypeName, brandTypeId, brandPicture;
    private UserCar userCar;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driverange_andtime_activity);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT != 19 && Build.VERSION.SDK_INT != 20)
            StatusBarUtil.setTranslucentForImageView(this, 0, viewNeedOffset);
        context = this;
        token = new EncryptedSharedPreferences(context).getString("token");
        initView();
        initCustomTimePicker();
    }

    public void initView() {
        if (getIntent() != null && getIntent().getStringExtra("brandId") != null)
            brandId = getIntent().getStringExtra("brandId");
        if (getIntent() != null && getIntent().getStringExtra("modleId") != null)
            modleId = getIntent().getStringExtra("modleId");
        if (getIntent() != null && getIntent().getStringExtra("yearstyleId") != null)
            yearstyleId = getIntent().getStringExtra("yearstyleId");
        if (getIntent() != null && getIntent().getStringExtra("brandName") != null)
            brandName = getIntent().getStringExtra("brandName");
        if (getIntent() != null && getIntent().getStringExtra("modleName") != null)
            modleName = getIntent().getStringExtra("modleName");
        if (getIntent() != null && getIntent().getStringExtra("yearstyleName") != null)
            yearstyleName = getIntent().getStringExtra("yearstyleName");
        if (getIntent() != null && getIntent().getStringExtra("brandTypeName") != null)
            brandTypeName = getIntent().getStringExtra("brandTypeName");
        if (getIntent() != null && getIntent().getStringExtra("brandTypeId") != null)
            brandTypeId = getIntent().getStringExtra("brandTypeId");
        if (getIntent() != null && getIntent().getStringExtra("brandPicture") != null) {
            brandPicture = getIntent().getStringExtra("brandPicture");
            Config.loadImageXUtils(ivDriverangeLogo,brandPicture);
        }
    }

    @OnClick({R.id.et_car_time, R.id.Layout_driverangeandtime_cancel, R.id.Layout_driverangeandtime_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_car_time:
                pvCustomTime.show();
                break;
            case R.id.Layout_driverangeandtime_cancel:
                finish();
                break;
            case R.id.Layout_driverangeandtime_save:
                if (getIntent() != null && getIntent().getStringExtra("isFromEditPage") != null) {
                    if(TextUtils.isEmpty(etCarTime.getText())){showToast("请填写购车时间！",1200);return;}
                    if(TextUtils.isEmpty(etRange.getText())){showToast("请填写行驶里程！",1200);return;}
                    updataCarInfo();//修改车辆信息
                } else {
                    requestAddCar();//调添加车辆的接口
                }
                break;
            default: break;
        }
    }

    private void initCustomTimePicker() {
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(1970, 1, 1);
        Calendar endDate = Calendar.getInstance();
        endDate.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        pvCustomTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                etCarTime.setText(getTime(date));
            }
        }).setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setLayoutRes(R.layout.pickerview_custom_time, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        ImageView ivCancel = (ImageView) v.findViewById(R.id.iv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime.returnData();
                                pvCustomTime.dismiss();
                            }
                        });
                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime.dismiss();
                            }
                        });
                    }
                })
                .setType(TimePickerView.Type.YEAR_MONTH_DAY)
                .isCenterLabel(false)
                .isCyclic(true)
                .setDividerColor(Color.RED)
                .build();
    }

    private void requestAddCar() {
        String path = Config.httpIp + Config.Urls.addCar;
        params = new RequestParams(path);
        JSONObject js_request0 = new JSONObject();
        try {
            js_request0.put("token", token);
            JSONObject js_request1 = new JSONObject();
            js_request1.put("brandId", brandId);
            js_request1.put("modleId", modleId);
            js_request1.put("yearstyleId", yearstyleId);
            js_request1.put("brandName", brandName);
            js_request1.put("modleName", modleName);
            js_request1.put("yearstyleName", yearstyleName);
            js_request1.put("carBuyTime", etCarTime.getText().toString());
            js_request1.put("carRunKm", etRange.getText().toString());
            js_request1.put("brandTypeName", brandTypeName);
            js_request1.put("brandTypeId", brandTypeId);
            js_request0.put("userCar", js_request1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.setAsJsonContent(true);
        params.setBodyContent(js_request0.toString());
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
                            String msg = object.getString("message");
                            if (result.equals("1")) {
                                showToast(msg, 2000);
                                if (getIntent() != null && getIntent().getStringExtra("ISFROMMYCAR") != null) {//我的座驾中添加车辆
                                    setResult(RESPONSECODEFINISH);
                                } else {//从4S店中添加
//                                    Intent i = new Intent(context, OrderEditActivity.class);
//                                    Bundle bundle = new Bundle();
//                                    userCar = new UserCar();
//                                    userCar.setCarBrandId(brandId);
//                                    userCar.setCarModleId(modleId);
//                                    userCar.setCarYearstyleId(yearstyleId);
//                                    userCar.setCarBrand(brandName);
//                                    userCar.setCarModle(modleName);
//                                    userCar.setCarYearStyle(yearstyleName);
//                                    userCar.setCarBuyTime(etCarTime.getText().toString());
//                                    userCar.setCarRunKm(etRange.getText().toString());
//                                    userCar.setCarBrandType(brandTypeName);
//                                    userCar.setCarBrandId(brandTypeId);
//                                    userCar.setBrandPictureUrl(brandTypeId);
//                                    bundle.putParcelable("userCar", userCar);
//                                    i.putExtras(bundle);
//                                    i.putExtra("busiShopId", getIntent().getStringExtra("busiShopId"));
//                                    i.putExtra("busiShopName", getIntent().getStringExtra("busiShopName"));
//                                    i.putExtra("busiShopPhone", getIntent().getStringExtra("busiShopPhone"));
//                                    i.putExtra("busiShopAddress", getIntent().getStringExtra("busiShopAddress"));
//                                    i.putExtra("brandPicture", brandPicture);
//                                    startActivity(i);
                                    new EncryptedSharedPreferences(context).putString("brandIdSelected",brandId);
                                    setResult(RESPONSECODEFINISH);
                                }
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
                        showToast("服务器连接失败", 2000);
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

    private void updataCarInfo() {
        String path = Config.httpIp + Config.Urls.updateUserCarLsit;
        params0 = new RequestParams(path);
        params0.addParameter("token", token);
        params0.addParameter("id", getIntent().getStringExtra("id"));
        params0.addParameter("carRunKm", etRange.getText().toString());
        params0.addParameter("carBuyTime", etCarTime.getText().toString());
        params0.setConnectTimeout(10 * 1000);
        mDialog0 = new MyDialog(this, "加载中...");
        mDialog0.setDuration(300);
        mDialog0.setMyCallback(new MyCallback() {
            @Override
            public void callback() {
            }

            @Override
            public void doing() {
                x.http().post(params0, new Callback.CacheCallback<String>() {
                    @Override
                    public void onSuccess(String arg0) {
                        try {
                            JSONObject object = new JSONObject(arg0);
                            String result = object.getString("code");
                            String msg = object.getString("message");
                            if (result.equals("1")) {
                                showToast(msg, 1200);
                                setResult(RESULT_CODE_REEDIT);
                                finish();
                            } else {
                                showToast(msg, 1200);
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
                        if (mDialog0 != null && mDialog0.isShowing()) {
                            mDialog0.hideDialog();
                            mDialog0 = null;
                            params0 = null;
                        }
                    }

                    @Override
                    public boolean onCache(String result) {
                        return false;
                    }
                });
            }
        });
        mDialog0.showDialog();
    }

    private String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUESTCODEFINISH && resultCode == RESPONSECODEFINISH) {
            setResult(RESPONSECODEFINISH);
            finish();
        }
    }
}