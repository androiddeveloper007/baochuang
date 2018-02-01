package com.szbc.front.index;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.szbc.widget.dropdonwdemo.model.addressBean;
import com.szbc.android.R;
import com.szbc.base.BaseActivity;
import com.szbc.base.Config;
import com.szbc.dialog.MyCallback;
import com.szbc.dialog.MyDialog;
import com.szbc.front.addcar.sortedlistview.CharacterParser;
import com.szbc.front.addcar.sortedlistview.PinyinComparatorCity;
import com.szbc.front.addcar.sortedlistview.SideBar;
import com.szbc.tool.GetJsonDataUtil;
import com.szbc.tool.StatusBarUtil;
import com.szbc.tool.config.EncryptedSharedPreferences;
import com.szbc.widget.xlistview.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.szbc.fragment.Index.MSG_LOAD_DATA;
import static com.szbc.fragment.Index.MSG_LOAD_FAILED;
import static com.szbc.fragment.Index.MSG_LOAD_SUCCESS;


/**
 * 城市选择
 */
public class CitySelectionActivity extends BaseActivity implements AMapLocationListener {

    @BindView(R.id.view_need_offset)
    LinearLayout viewNeedOffset;
    @BindView(R.id.lv_city_selection)
    XListView lvCitySelection;
    @BindView(R.id.sidrbar)
    SideBar sideBar;
    @BindView(R.id.dialog)
    TextView dialog;
    @BindView(R.id.tv_location1)
    TextView tvLocation1;
    @BindView(R.id.tv_location0)
    TextView tvLocation0;

    private EncryptedSharedPreferences sp;
    private SortCityAdapter adapter;
    private CharacterParser characterParser;
    private PinyinComparatorCity pinyinComparator;
    private Thread thread;
    List<addressBean.city> cities;
    public AMapLocationClient mLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_selection);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT != 19 && Build.VERSION.SDK_INT != 20)
            StatusBarUtil.setTranslucentForImageView(this, 0, viewNeedOffset);
        context = this;

        sp = new EncryptedSharedPreferences(this);
        handler.sendEmptyMessage(MSG_LOAD_DATA);//初始化地址数据
        getCity();
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparatorCity();
        sideBar.setTextView(dialog);
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    lvCitySelection.setSelection(position);
                }
            }
        });
        lvCitySelection.setPullRefreshEnable(false);
        lvCitySelection.setPullLoadEnable(false);
    }

    private void initData() {
        String path = Config.httpIp + Config.Urls.queryBusiShopsDetails;
        params = new RequestParams(path);
        params.addParameter("id", "");
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
                                if (object.getString("data") != null) {
                                    String data = object.getString("data");
                                }
                            } else {
                                showToast(msg, 2000);
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

    @OnClick({R.id.title_back, R.id.tv_location1, R.id.btn_beijing, R.id.btn_shanghai, R.id.btn_guangzhou,
            R.id.btn_sz, R.id.btn_wh, R.id.btn_cd, R.id.btn_cq, R.id.btn_xa, R.id.rl_city1})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.tv_location1:
                break;
            case R.id.btn_beijing:
                break;
            case R.id.btn_shanghai:
                break;
            case R.id.btn_guangzhou:
                break;
            case R.id.btn_sz:
                break;
            case R.id.btn_wh:
                break;
            case R.id.btn_cd:
                break;
            case R.id.btn_cq:
                break;
            case R.id.btn_xa:
                break;
            case R.id.rl_city1:
                break;
            default:
                break;
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DATA:
                    if (thread == null) {
                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                showLog("开始解析JSON数据");
                                initJsonData();
                            }
                        });
                        thread.start();
                    }
                    break;
                case MSG_LOAD_SUCCESS:
                    showLog("解析JSON数据完成");
                    Collections.sort(cities, pinyinComparator);
                    adapter = new SortCityAdapter(context, cities);
                    lvCitySelection.setAdapter(adapter);
                    break;
                case MSG_LOAD_FAILED:
                    Toast.makeText(context, "解析数据失败", Toast.LENGTH_LONG).show();
                    break;
                default:break;
            }
        }
    };

    private void initJsonData() {
        String JsonData = new GetJsonDataUtil().getJson(context, "address.json");
        ArrayList<addressBean> jsonBean = parseData(JsonData);
        cities = new ArrayList<>();
        for (addressBean a : jsonBean) {
            List<addressBean.city> b = a.getCity();
            cities.addAll(b);
        }
        handler.sendEmptyMessage(MSG_LOAD_SUCCESS);
    }

    public ArrayList<addressBean> parseData(String result) {
        ArrayList<addressBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                addressBean entity = gson.fromJson(data.optJSONObject(i).toString(), addressBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            handler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                String cityCurrent = amapLocation.getCity();
                showLog("location city:" + cityCurrent);
                mLocationClient.stopLocation();
                //定位成功后显示城市名到布局
                tvLocation0.setText("当前城市：");
                tvLocation1.setVisibility(View.VISIBLE);
                tvLocation1.setText(cityCurrent);
                tvLocation1.setClickable(false);
            } else {
                showLog("location Error, ErrCode:" + amapLocation.getErrorCode() + ", errInfo:" + amapLocation.getErrorInfo());
                //定位失败时，显示失败字样
                tvLocation0.setText("定位失败，");
                tvLocation1.setText("点击重试");
                tvLocation1.setClickable(true);
                mLocationClient.onDestroy();
            }
        }
    }

    //高德地图定位当前城市
    private void getCity() {
        tvLocation1.setText("定位中...");
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(this);
        AMapLocationClientOption mLocationOption;
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
    }

    @OnClick(R.id.tv_location1)
    public void onClick() {
        getCity();
    }
}