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
import com.szbc.front.addcar.sortedlistview.SortSeriesAdapter;
import com.szbc.model.CarSeries;
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
 * 汽车系列选择
 */
public class CarSeriesSelectActivity extends BaseActivity implements XListView.IXListViewListener {
    @BindView(R.id.view_need_offset)
    LinearLayout viewNeedOffset;
    @BindView(R.id.lv_car_series_0)
    XListView lvCarSeries0;
    List<CarSeries> carSeries;
    List<CarSeries> carSeriesTotal;
    private Handler mHandler = new Handler();
    private int row0 = 1;
    private SortSeriesAdapter adapter;
    String ExtraBrandId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carseries_select_activity);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT != 19 && Build.VERSION.SDK_INT != 20)
            StatusBarUtil.setTranslucentForImageView(this, 0, viewNeedOffset);
        context = this;
        initView();
        loadData(row0);
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
    public void initView() {
        if (getIntent() != null && getIntent().getStringExtra("brandId") != null) {
            ExtraBrandId = getIntent().getStringExtra("brandId");
        }
        lvCarSeries0.setPullRefreshEnable(false);
        lvCarSeries0.setPullLoadEnable(true);
        lvCarSeries0.setAutoLoadEnable(true);
        lvCarSeries0.setXListViewListener(this);
    }

    private void loadData(int row) {
        String path = Config.httpIp + Config.Urls.queryCarModleBs;
        params = new RequestParams(path);
        params.addParameter("startRow", row);
        params.addParameter("rows", 20);
        params.addParameter("brandId", ExtraBrandId);
//        params.addParameter("brandTypeId", "");
        params.setConnectTimeout(10 * 1000);
        params.setCacheMaxAge(1000 * 60 * 60 * 24 * 70);
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
                lvCarSeries0.stopLoadMore();
            }
            @Override
            public boolean onCache(String result) {
                return false;
            }
        });
    }
    private void doResponse(String arg0) {
        try {
            JSONObject object = new JSONObject(arg0);
            String result = object.getString("code");
            if (result.equals("1")) {
                if (object.getString("data") != null) {
                    String str = object.getString("data");
                    carSeries = new Gson().fromJson(str, new TypeToken<List<CarSeries>>() {}.getType());
                    if (carSeries.size() < 20)
                        lvCarSeries0.setPullLoadEnable(false);
                    else
                        lvCarSeries0.setPullLoadEnable(true);
                    if(adapter==null){
                        carSeriesTotal = carSeries;
                        adapter = new SortSeriesAdapter(context,carSeries);
                        lvCarSeries0.setAdapter(adapter);
                    }else{
                        carSeriesTotal.addAll(carSeries);
                        adapter.update(carSeriesTotal);
                    }
                    row0++;
                    setOnItemClickListener();
                }
            } else {
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
        lvCarSeries0.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(context, CarConfigerationSelectActivity.class);
                i.putExtra("brandId", carSeriesTotal.get(position-1).getBrandId());
                i.putExtra("brandTypeName", carSeriesTotal.get(position-1).getBrandTypeName());
                i.putExtra("brandTypeId", carSeriesTotal.get(position-1).getBrandTypeId());
                i.putExtra("modleId", carSeriesTotal.get(position-1).getModleId());
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