package com.amap;


import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.amap.overlay.DrivingRouteOverlay;
import com.amap.tool.AMapUtil;
import com.amap.tool.LatLngUtils;
import com.amap.tool.ToastUtil;
import com.szbc.android.R;
import com.szbc.base.BaseActivity;
import com.szbc.base.Config;
import com.szbc.tool.StatusBarUtil;
import com.szbc.tool.ToastMakeText;

import java.io.File;
import java.net.URISyntaxException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;

/**
 * 导航页面
 */
public class LocationModeSourceActivity extends BaseActivity implements AMap.OnMyLocationChangeListener
        , AMap.InfoWindowAdapter, RouteSearch.OnRouteSearchListener {

    @BindView(R.id.view_need_offset)
    LinearLayout viewNeedOffset;
    @BindView(R.id.bottom_layout)
    RelativeLayout mBottomLayout;
    @BindView(R.id.firstline)
    TextView mRotueTimeDes;
    @BindView(R.id.secondline)
    TextView mRouteDetailDes;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    private AMap aMap;
    private MapView mapView;
    private MyLocationStyle myLocationStyle;
    private LatLonPoint lp = new LatLonPoint(22.536564, 114.051711);
    private RouteSearch mRouteSearch;
    private DriveRouteResult mDriveRouteResult;
    private final int ROUTE_TYPE_DRIVE = 2;
    private String lat;
    private String lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locationmodesource_activity);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT != 19 && Build.VERSION.SDK_INT != 20)
            StatusBarUtil.setTranslucentForImageView(this, 0, viewNeedOffset);

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
        if (getIntent() != null && getIntent().getStringExtra("lat") != null) {
            lat = getIntent().getStringExtra("lat");
            lon = getIntent().getStringExtra("lon");
        }
        if(getIntent()!=null && getIntent().hasExtra("title")){
            setTitle(getIntent().getStringExtra("title"));
            mBottomLayout.setVisibility(GONE);
        }
    }

    @OnClick(R.id.title_back)
    public void onClick() {
        finish();
    }

    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        //设置SDK 自带定位消息监听
        aMap.setOnMyLocationChangeListener(this);
        aMap.setInfoWindowAdapter(this);
        aMap.getUiSettings().setZoomControlsEnabled(false);
        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 如果要设置定位的默认状态，可以在此处进行设置
        myLocationStyle = new MyLocationStyle();
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE));
    }

    @Override
    public void onMyLocationChange(Location location) {
        // 定位回调监听
        if (location != null) {
            if (Config.isDebug)
                Log.e("amap", "onMyLocationChange 定位成功， lat: " + location.getLatitude() + " lon: " + location.getLongitude());
            Bundle bundle = location.getExtras();
            if (bundle != null) {
                int errorCode = bundle.getInt(MyLocationStyle.ERROR_CODE);
                String errorInfo = bundle.getString(MyLocationStyle.ERROR_INFO);
                // 定位类型，可能为GPS WIFI等，具体可以参考官网的定位SDK介绍
                int locationType = bundle.getInt(MyLocationStyle.LOCATION_TYPE);
                if (Config.isDebug)
                    Log.e("amap", "定位信息， code: " + errorCode + " errorInfo: " + errorInfo + " locationType: " + locationType);
            } else {
                if (Config.isDebug) Log.e("amap", "定位信息， bundle is null ");
            }
            lp.setLatitude(location.getLatitude());
            lp.setLongitude(location.getLongitude());
            searchRouteResult(ROUTE_TYPE_DRIVE, RouteSearch.DrivingDefault);
        } else {
            if (Config.isDebug) Log.e("amap", "定位失败");
        }
    }

    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(int routeType, int mode) {
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                lp, new LatLonPoint(Double.parseDouble(lat), Double.parseDouble(lon)));//poiItems.get(0).getLatLonPoint()
        if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mode, null,
                    null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
        //unused
    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mDriveRouteResult = result;
                    final DrivePath drivePath = mDriveRouteResult.getPaths()
                            .get(0);
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                            this, aMap, drivePath,
                            mDriveRouteResult.getStartPos(),
                            mDriveRouteResult.getTargetPos(), null);
                    drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                    drivingRouteOverlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();
                    mBottomLayout.setVisibility(View.VISIBLE);
                    int dis = (int) drivePath.getDistance();
                    int dur = (int) drivePath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur) + "(" + AMapUtil.getFriendlyLength(dis) + ")";
                    mRotueTimeDes.setText(des);
                    mRouteDetailDes.setVisibility(View.VISIBLE);
                    int taxiCost = (int) mDriveRouteResult.getTaxiCost();
                    mRouteDetailDes.setText("打车约" + taxiCost + "元");
                    mBottomLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                if (isInstallByread("com.autonavi.minimap")) {
                                    Intent intent = Intent.getIntent("androidamap://route?sourceApplication=softname&slat="
                                            + lp.getLatitude() + "&slon=" + lp.getLongitude() +
                                            "&sname=" + "我的位置" + "&dlat=" + lat + "&dlon=" + lon
                                            + "&dname=" + "目标位置" + "&dev=0&m=0&t=1");
                                    startActivity(intent);
                                    if (Config.isDebug) Log.e("ZP", "高德地图客户端已经安装");
                                } else if (isInstallByread("com.baidu.BaiduMap")) {
                                    LatLng latLng = LatLngUtils.Gaode2Baidu(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon)));
                                    Intent intent = Intent.getIntent("intent://map/direction?origin=latlng:" + lp.getLatitude() + "," + lp.getLongitude() +
                                            "|name:当前位置&destination=latlng:" + latLng.latitude + "," + latLng.longitude +
                                            "|name:4S店&mode=driving&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                                    startActivity(intent);
                                    if (Config.isDebug) Log.e("ZP", "百度地图客户端已安装");
                                } else {
                                    ToastMakeText.showToast(LocationModeSourceActivity.this, "未检测到设备已安装高德或百度地图客户端", 2000);
                                    return;
                                }
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.show(this, R.string.no_result);
                }
            } else {
                ToastUtil.show(this, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this.getApplicationContext(), errorCode);
        }
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {
        //unused
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {
        //unused
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * 判断是否安装目标应用
     *
     * @param packageName 目标应用安装后的包名
     * @return 是否已安装目标应用
     */
    private boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

    private void setTitle(String str){
        tvTitle.setText(str);
    }
}
