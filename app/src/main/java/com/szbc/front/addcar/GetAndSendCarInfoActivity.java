package com.szbc.front.addcar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.google.gson.Gson;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.szbc.android.R;
import com.szbc.base.BaseActivity;
import com.szbc.base.Config;
import com.szbc.base.WebViewRelease;
import com.szbc.model.JsonBean;
import com.szbc.tool.GetJsonDataUtil;
import com.szbc.tool.StatusBarUtil;
import com.szbc.widget.MClearEditText;
import com.szbc.widget.SlideSwitch;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 取送车信息输入页
 */
public class GetAndSendCarInfoActivity extends BaseActivity implements OnDateSetListener {
    @BindView(R.id.view_need_offset)
    LinearLayout viewNeedOffset;
    @BindView(R.id.iv_navigation_bg)
    ImageView iv_navigation_bg;
    @BindView(R.id.tv_carrefresh_time_info_1)
    TextView tvCarrefreshTime1;
    @BindView(R.id.et_getandsend_getcar_time)
    EditText etGetandsendGetcarTime;
    @BindView(R.id.et_getandsend_city)
    MClearEditText etGetandsendCity;
    @BindView(R.id.et_getCar_address)
    MClearEditText et_getCar_address;
    @BindView(R.id.et_sendCar_address)
    MClearEditText et_sendCar_address;
    @BindView(R.id.radiobutton1)
    RadioButton radiobutton1;
    @BindView(R.id.radiobutton2)
    RadioButton radiobutton2;
    @BindView(R.id.rl_sendCar)
    RelativeLayout rlSendCar;
    @BindView(R.id.rl_address_get)
    RelativeLayout rl_address_get;
    @BindView(R.id.rb_daijia_get)
    CheckBox rbDaijiaGet;
    @BindView(R.id.rb_daijia_send)
    CheckBox rbDaijiaSend;
    @BindView(R.id.slide_get_and_send)
    SlideSwitch slideGetAndSend;
    @BindView(R.id.rl_getAndSend_root)
    RelativeLayout rlGetAndSendRoot;
    @BindView(R.id.rl_getAndSend_slide)
    RelativeLayout rl_getAndSend_slide;

    TimePickerDialog mDialogMonthDayHourMinute;
    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final int MSG_LOAD_DATA = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED = 0x0003;
    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private Thread thread;
    private boolean isLoaded = false;
//    private boolean isRadio2Checked;
    private boolean isGetAndSetSlide;
    private String fetchDeliverState;
    private boolean isRbDaijiaGet=true,isRbDaijiaSend=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getandsend_carinfo_activity);
        ButterKnife.bind(this);
        context = this;
        if (Build.VERSION.SDK_INT < 20)
            iv_navigation_bg.getLayoutParams().height = getResources().getDimensionPixelOffset(R.dimen.dimen_45);
        if (Build.VERSION.SDK_INT != 19 && Build.VERSION.SDK_INT != 20)
            StatusBarUtil.setTranslucentForImageView(this, 0, viewNeedOffset);
        mHandler.sendEmptyMessage(MSG_LOAD_DATA);
        initView();
    }

    public void initView() {
        mDialogMonthDayHourMinute = new TimePickerDialog.Builder()
                .setThemeColor(getResources().getColor(R.color.btn_color))
                .setType(Type.MONTH_DAY_HOUR_MIN)
                .setCallBack(this)
                .setTitleStringId("")
                .build();
        if (getIntent() != null && getIntent().getStringExtra("refreshTime") != null)
            tvCarrefreshTime1.setText(getIntent().getStringExtra("refreshTime"));
        fetchDeliverState="3";
        rbDaijiaGet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rl_address_get.setVisibility(View.VISIBLE);
                    isRbDaijiaGet=true;
                    if(isRbDaijiaSend){
                        rl_getAndSend_slide.setVisibility(View.VISIBLE);
                        fetchDeliverState="3";
                    }else{
                        rl_getAndSend_slide.setVisibility(View.GONE);
                        fetchDeliverState="1";
                    }
                } else {
                    rl_getAndSend_slide.setVisibility(View.GONE);
                    rl_address_get.setVisibility(View.GONE);
                    isRbDaijiaGet=false;
                    if(isRbDaijiaSend){
                        fetchDeliverState="2";
                    }else{
                        rbDaijiaGet.setChecked(true);
                        rl_address_get.setVisibility(View.VISIBLE);
                        fetchDeliverState="2";
                    }
                }
            }
        });
        rbDaijiaGet.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                switch(event.getAction()){
//                    case MotionEvent.ACTION_DOWN:
//                        break;
//                    default:break;
//                }
                if(rbDaijiaGet.isChecked() && !rbDaijiaSend.isChecked())
                    return true;
                return false;
            }
        });
        rbDaijiaSend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rlSendCar.setVisibility(View.VISIBLE);
                    isRbDaijiaSend=true;
                    if(isRbDaijiaGet) {
                        rl_getAndSend_slide.setVisibility(View.VISIBLE);
                        fetchDeliverState = "3";
                    } else {
                        rl_getAndSend_slide.setVisibility(View.GONE);
                        fetchDeliverState = "2";
                    }
                } else {
                    rl_getAndSend_slide.setVisibility(View.GONE);
                    rlSendCar.setVisibility(View.GONE);
                    isRbDaijiaSend=false;
                    if(isRbDaijiaGet)
                        fetchDeliverState="1";
                    else{
                        rbDaijiaSend.setChecked(true);
                        rlSendCar.setVisibility(View.VISIBLE);
                        fetchDeliverState="1";
                    }
                }
            }
        });
        rbDaijiaSend.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!rbDaijiaGet.isChecked() && rbDaijiaSend.isChecked())
                    return true;
                return false;
            }
        });
        slideGetAndSend.setSlideListener(new SlideSwitch.SlideListener() {
            @Override
            public void open() {
                isGetAndSetSlide = true;
                et_sendCar_address.setText(et_getCar_address.getText());//.toString()
                et_getCar_address.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        et_sendCar_address.setText(s);
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
            }
            @Override
            public void close() {
                isGetAndSetSlide = false;
            }
        });
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerDialog, long millseconds) {
        etGetandsendGetcarTime.setText(getDateToString(millseconds));
    }

    public String getDateToString(long time) {
        Date d = new Date(time);
        return sf.format(d);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DATA:
                    if (thread == null) {
                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                initJsonData();
                            }
                        });
                        thread.start();
                    }
                    break;
                case MSG_LOAD_SUCCESS:
                    isLoaded = true;
                    break;
                case MSG_LOAD_FAILED:
                    Toast.makeText(GetAndSendCarInfoActivity.this, "解析数据失败", Toast.LENGTH_SHORT).show();
                    break;
                default:break;
            }
        }
    };

    private void ShowPickerView() {
        OptionsPickerView pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String tx = options1Items.get(options1).getPickerViewText() +
                        options2Items.get(options1).get(options2) +
                        options3Items.get(options1).get(options2).get(options3);
                etGetandsendCity.setText(tx);
            }
        }).setTitleText("地址选择").setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK).setContentTextSize(20)
                .setOutSideCancelable(true).build();
//        pvOptions.setPicker(options1Items, options2Items);
        pvOptions.setPicker(options1Items, options2Items, options3Items);
        pvOptions.show();
    }

    private void initJsonData() {
        String JsonData = new GetJsonDataUtil().getJson(this, "province.json");
        ArrayList<JsonBean> jsonBean = parseData(JsonData);
        options1Items = jsonBean;
        for (int i = 0; i < jsonBean.size(); i++) {
            ArrayList<String> CityList = new ArrayList<>();
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();
            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);
                ArrayList<String> City_AreaList = new ArrayList<>();
                if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    City_AreaList.add("");
                } else {
                    for (int d = 0; d < jsonBean.get(i).getCityList().get(c).getArea().size(); d++) {
                        String AreaName = jsonBean.get(i).getCityList().get(c).getArea().get(d);
                        City_AreaList.add(AreaName);
                    }
                }
                Province_AreaList.add(City_AreaList);
            }
            options2Items.add(CityList);
            options3Items.add(Province_AreaList);
        }
        mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);
    }

    public ArrayList<JsonBean> parseData(String result) {
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }

    @OnClick({R.id.et_getandsend_city, R.id.ll_getandsend_save, R.id.title_back, R.id.et_getandsend_getcar_time, R.id.tv_getAndSendInfo_explain})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_getandsend_city:
                if (isLoaded) {
                    ShowPickerView();
                } else {
                    Toast.makeText(GetAndSendCarInfoActivity.this, "城市数据解析中，请等待", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ll_getandsend_save:
                if (TextUtils.isEmpty(etGetandsendGetcarTime.getText())&&isRbDaijiaGet) {showToast("请填写取车时间", 1200);return;}
                if (TextUtils.isEmpty(etGetandsendCity.getText())) {showToast("请填写地区", 1200);return;}
                Intent i = new Intent();
                i.putExtra("carFetchTime", etGetandsendGetcarTime.getText().toString());
                i.putExtra("carFetchAddress", etGetandsendCity.getText().toString() + et_getCar_address.getText().toString());
                if (!isGetAndSetSlide) {//isRadio2Checked
                    if (TextUtils.isEmpty(et_sendCar_address.getText().toString())&&isRbDaijiaSend) {
                        showToast("请填写送车地址", 2000);
                        return;
                    } else
                        i.putExtra("carDeliverAddress", etGetandsendCity.getText().toString() + et_sendCar_address.getText().toString());
                }
                i.putExtra("isAddressAlike", isGetAndSetSlide);//!isRadio2Checked
                i.putExtra("fetchDeliverState", fetchDeliverState);
                setResult(OrderEditActivity.RESULT_SLIDE_GOHOME, i);
                finish();
                break;
            case R.id.et_getandsend_getcar_time:
                mDialogMonthDayHourMinute.show(getSupportFragmentManager(), "month_day_hour_minute");
                break;
            case R.id.title_back:
                finish();
                break;
            case R.id.tv_getAndSendInfo_explain:
                Intent intent = new Intent(context, WebViewRelease.class);
                Bundle bundle = new Bundle();
                bundle.putString("url", Config.Urls.bbbb);
                bundle.putString("title", "代驾说明");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            default:break;
        }
    }
}

        /*rlGetAndSendRoot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                rlGetAndSendRoot.getWindowVisibleDisplayFrame(rect);
                int mainInvisibleHeight = rlGetAndSendRoot.getRootView().getHeight()
                        - rect.bottom;
                if (mainInvisibleHeight > 100) {
                    int[] location = new int[2];
                    rl_getAndSend_slide.getLocationInWindow(location);
                    int scrollHeight = (location[1] + rl_getAndSend_slide.getHeight())
                            - rect.bottom;
                    rlGetAndSendRoot.scrollTo(0, scrollHeight);
                } else {
                    rlGetAndSendRoot.scrollTo(0, 0);
                }
            }
        });*//*
        radiobutton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rlSendCar.setVisibility(View.VISIBLE);
                    isRadio2Checked = true;
                } else {
                    rlSendCar.setVisibility(View.GONE);
                    isRadio2Checked = false;
                }
            }
        });*/