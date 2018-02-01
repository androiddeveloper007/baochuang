package com.szbc.front.addcar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.szbc.android.R;
import com.szbc.base.BaseActivity;
import com.szbc.base.Config;
import com.szbc.front.addcar.sortedlistview.CarConfAdapter;
import com.szbc.model.CarConfiguration;
import com.szbc.tool.StatusBarUtil;
import com.szbc.widget.xlistview.XListView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.szbc.front.addcar.CarBrandActivity.REQUESTCODEFINISH;
import static com.szbc.front.addcar.CarBrandActivity.RESPONSECODEFINISH;

/**
 * 汽车年款选择
 */
public class CarConfigerationSelectActivity extends BaseActivity implements XListView.IXListViewListener {
    @BindView(R.id.view_need_offset)
    LinearLayout viewNeedOffset;
    String brandId, brandTypeId,brandTypeName,modleId;
    @BindView(R.id.lv_car_config)
    XListView lvCarConfig;
    List<CarConfiguration> carConfiguration;
    List<CarConfiguration> carConfTotal;
    private Handler mHandler = new Handler();
    private int row0 = 1;
    private CarConfAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_configeration_select_activity);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT != 19 && Build.VERSION.SDK_INT != 20)
            StatusBarUtil.setTranslucentForImageView(this, 0, viewNeedOffset);
        context = this;
        initView();
        loadData(row0);
    }
    public void initView() {
        if (getIntent() != null && getIntent().getStringExtra("brandId") != null) {
            brandId = getIntent().getStringExtra("brandId");
        }
        if (getIntent() != null && getIntent().getStringExtra("brandTypeId") != null) {
            brandTypeId = getIntent().getStringExtra("brandTypeId");
        }
        if (getIntent() != null && getIntent().getStringExtra("brandTypeName") != null) {
            brandTypeName = getIntent().getStringExtra("brandTypeName");
        }
        if (getIntent() != null && getIntent().getStringExtra("modleId") != null) {
            modleId = getIntent().getStringExtra("modleId");
        }
        lvCarConfig.setPullRefreshEnable(false);
        lvCarConfig.setPullLoadEnable(true);
        lvCarConfig.setAutoLoadEnable(true);
        lvCarConfig.setXListViewListener(this);
    }
    @Override
    public void onRefresh() {
    }
    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData(row0);
            }
        }, 200);
    }
    private void loadData(int row) {
        String path = Config.httpIp + Config.Urls.carConfiguration;
        params = new RequestParams(path);
        params.addParameter("startRow", row);
        params.addParameter("rows", 20);
        params.addParameter("brandId", brandId);
        params.addParameter("brandTypeId", brandTypeId);
        params.addParameter("modleId", modleId);
        params.setConnectTimeout(10 * 1000);
        params.setCacheMaxAge(1000 * 60 * 60 *24 *70);
        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String arg0) {
                doResponse(arg0);
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
                lvCarConfig.stopLoadMore();
            }
            @Override
            public boolean onCache(String arg0) {
//                doResponse(arg0);
                return false;
            }
        });
    }
    private void doResponse(String arg0) {
        try {
            JSONObject object = new JSONObject(arg0);
            String result = object.getString("code");
            if (result.equals("1")) {
                if(object.getString("data") != null){
                    String str = object.getString("data");
                    carConfiguration = new Gson().fromJson(str, new TypeToken<List<CarConfiguration>>() {}.getType());
                    if (carConfiguration.size() < 20)
                        lvCarConfig.setPullLoadEnable(false);
                    else
                        lvCarConfig.setPullLoadEnable(true);
                    if(adapter==null){
                        carConfTotal = carConfiguration;
                        adapter = new CarConfAdapter(context,carConfiguration);
                        lvCarConfig.setAdapter(adapter);
                    }else{
                        carConfTotal.addAll(carConfiguration);
                        adapter.update(carConfTotal);
                    }
                    row0++;
                    setOnItemClickListener();
                }
            }  else {
                showToast(object.getString("message"), 1000);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @OnClick(R.id.title_back)
    public void onClick() {
        this.finish();
    }
    private void setOnItemClickListener() {
        lvCarConfig.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(context, DriveRangeAndTimeActivity.class);
                i.putExtra("brandId", carConfTotal.get(position-1).getBrandId());
                i.putExtra("modleId", carConfTotal.get(position-1).getModleId());
                i.putExtra("yearstyleId", carConfTotal.get(position-1).getYearstyleId());
                i.putExtra("brandName", carConfTotal.get(position-1).getBrandName());
                i.putExtra("modleName", carConfTotal.get(position-1).getModleName());
                i.putExtra("yearstyleName", carConfTotal.get(position-1).getYearstyleName());
                i.putExtra("yearstyleName", carConfTotal.get(position-1).getYearstyleName());
                i.putExtra("brandTypeName", brandTypeName);
                i.putExtra("brandTypeId", brandTypeId);
                i.putExtra("brandPicture",getIntent().getStringExtra("brandPicture"));
                if(getIntent()!=null && getIntent().getStringExtra("ISFROMMYCAR")!=null)
                    i.putExtra("ISFROMMYCAR","");
                else{
                    i.putExtra("busiShopId",getIntent().getStringExtra("busiShopId"));
                    i.putExtra("busiShopName",getIntent().getStringExtra("busiShopName"));
                    i.putExtra("busiShopPhone",getIntent().getStringExtra("busiShopPhone"));
                    i.putExtra("busiShopAddress",getIntent().getStringExtra("busiShopAddress"));
                    i.putExtra("FROM4S", "");
                }
                startActivityForResult(i,REQUESTCODEFINISH);
//                finish();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUESTCODEFINISH && resultCode == RESPONSECODEFINISH) {
            setResult(RESPONSECODEFINISH);
            finish();
        }
    }
}