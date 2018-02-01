package com.szbc.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.PoiItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.szbc.widget.dropdonwdemo.adapter.TravelingAdapter;
import com.szbc.widget.dropdonwdemo.model.FilterData;
import com.szbc.widget.dropdonwdemo.model.FilterEntity;
import com.szbc.widget.dropdonwdemo.model.FilterThreeEntity;
import com.szbc.widget.dropdonwdemo.model.FilterTwoEntity;
import com.szbc.widget.dropdonwdemo.model.TravelingEntity;
import com.szbc.widget.dropdonwdemo.model.addressBean;
import com.szbc.widget.dropdonwdemo.utils.DensityUtil;
import com.szbc.widget.dropdonwdemo.utils.ModelUtil;
import com.szbc.widget.dropdonwdemo.view.FilterView;
import com.szbc.android.R;
import com.szbc.base.Config;
import com.szbc.front.addcar.sortedlistview.PinyinComparator1;
import com.szbc.front.index.CitySelectionActivity;
import com.szbc.front.index.FourSStoreDetail;
import com.szbc.model.CarBrand;
import com.szbc.model.Store;
import com.szbc.tool.GetJsonDataUtil;
import com.szbc.tool.config.EncryptedSharedPreferences;
import com.szbc.widget.xlistview.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.szbc.front.addcar.CarBrandActivity.REQUESTCODEFINISH;
import static com.szbc.front.mine.MyCarsActivity.RESULT_CODE_EDIT;

/**
 * 首页
 * 核心需求：本次需求功能主要是面对用户使用APP对4s店进行预约的功能，其中提供上门取送车服务，以及查询历史维修记录.
 * 维修服务主要是车辆在4S店检查结果为主要依据。再根据结果进行交易的“后维护模式”
 */
public class Index extends BaseFragment implements XListView.IXListViewListener, AMapLocationListener {
    @BindView(R.id.lv_test)
    XListView smoothListView;
    @BindView(R.id.fv_top_filter)
    FilterView fvTopFilter;
    @BindView(R.id.rl_navigation)
    RelativeLayout rlNavigation;
    @BindView(R.id.rl_navigation1)
    RelativeLayout rlNavigation1;
    private View view;
    private FilterData filterData; // 筛选数据
    private int mScreenHeight; // 屏幕高度
    private TravelingAdapter mAdapter; // 主页数据
    private List<TravelingEntity> travelingList = new ArrayList<>(); // ListView数据
    private List<PoiItem> indexList = new ArrayList<>(); // 汽车ListView数据
    private boolean isPrepared;// 标志fragment是否初始化完成

    private int currentPage = 0;// 当前页面，从0开始计数
    private AMapLocation lp;
    private String cityCurrent;
    private int row = 1;
    private EncryptedSharedPreferences sp;
    private String brandIdSelected = "";
    private List<Store> store, storeTotal;
    List<CarBrand> carBrands;
    List<CarBrand> carBrandsTotal;
    private Handler mHandler = new Handler();
    private int row0 = 1;
    private PinyinComparator1 pinyinComparator;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient;
    public static final int MSG_LOAD_DATA = 0x0001;
    public static final int MSG_LOAD_SUCCESS = 0x0002;
    public static final int MSG_LOAD_FAILED = 0x0003;
    private Thread thread;
    private ArrayList<addressBean> provinces;
    private String cityId;
    private String provinceId;
    private String areaId;
    private double lat;
    private double lon;
    private List<FilterEntity> entity;
    private boolean firstLoaded;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.index, container, false);
            ButterKnife.bind(this, view);
            if (Build.VERSION.SDK_INT > 20) {
                ViewGroup.LayoutParams lp = rlNavigation.getLayoutParams();
                lp.height = getActivity().getResources().getDimensionPixelOffset(R.dimen.dimen_65);
                rlNavigation.setLayoutParams(lp);
                RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp1.topMargin = getActivity().getResources().getDimensionPixelOffset(R.dimen.dimen_30);
                rlNavigation1.setLayoutParams(lp1);
            }
            sp = new EncryptedSharedPreferences(getContext());
            initView();
            isPrepared = true;
            handler.sendEmptyMessage(MSG_LOAD_DATA);
            lazyLoad();
            brandIdSelected = sp.getString("brandIdSelected");
            getCity();
        }
        return view;
    }

    private void initView() {
        smoothListView.setPullRefreshEnable(true);
        smoothListView.setPullLoadEnable(true);
        smoothListView.setAutoLoadEnable(false);
        smoothListView.setXListViewListener(this);
        smoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (storeTotal != null && storeTotal.size() > 0) {
                    Intent i = new Intent(getActivity(), FourSStoreDetail.class);
                    i.putExtra("busiShopId", storeTotal.get(position - 1).getId());
                    startActivityForResult(i, REQUESTCODEFINISH);
                }
            }
        });
        mScreenHeight = DensityUtil.getWindowHeight(getActivity());
    }

    private void loadData(final int i, double lat, double lon, String brandId, String provinceId, String cityId, String areaId) {
        store = null;
        String path = Config.httpIp + Config.Urls.queryBusiShopsList;
        params0 = new RequestParams(path);
        JSONObject js_request0 = new JSONObject();
        try {
            String token = sp.getString("token");
            js_request0.put("token", token);
            if (lat == 0 && lon == 0) {
                js_request0.put("lat", "");
                js_request0.put("lng", "");
            } else {
                js_request0.put("lat", lat);
                js_request0.put("lng", lon);
            }
            js_request0.put("brandId", brandId);
            js_request0.put("province", provinceId);
            js_request0.put("city", cityId);
            js_request0.put("area", areaId);
            js_request0.put("startRow", "" + i);
            js_request0.put("rows", "20");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params0.setAsJsonContent(true);
        params0.setBodyContent(js_request0.toString());
        params0.setConnectTimeout(10 * 1000);
        showLog(js_request0.toString());
        x.http().post(params0, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String arg0) {
                try {
                    JSONObject object = new JSONObject(arg0);
                    String result = object.getString("code");
                    String msg = object.getString("message");
                    if (result.equals("1")) {
                        if (object.has("data")) {
                            String data = object.getString("data");
                            store = new Gson().fromJson(data, new TypeToken<List<Store>>() {
                            }.getType());
                            showLog(store.size() + "条数据");

                            if (store.size() < 20)
                                smoothListView.setPullLoadEnable(false);
                            else
                                smoothListView.setPullLoadEnable(true);

                            if (store == null || store.size() == 0) {
                                if (storeTotal != null && storeTotal.size() > 0) {
                                    if (i == 1)
                                        storeTotal.clear();//当非上拉加载时，将之前内存中数据清空
                                } else {
                                    mAdapter = new TravelingAdapter(getActivity(), store);
                                    smoothListView.setAdapter(mAdapter);
                                    int height = mScreenHeight - getActivity().getResources().getDimensionPixelOffset(R.dimen.dimen_170);
                                    mAdapter.getNoDataEntity(height);
                                }
                            }
                            if (mAdapter == null) {
                                mAdapter = new TravelingAdapter(getActivity(), store);
                                smoothListView.setAdapter(mAdapter);
                                storeTotal = store;
                            } else {
                                storeTotal.addAll(store);
                                mAdapter.update(storeTotal);
                            }
//                            initData();
                        } else {
                            //没有查询到商家数据
                            if (storeTotal != null && storeTotal.size() > 0) {
                                storeTotal.clear();
                                mAdapter = new TravelingAdapter(getActivity(), storeTotal);
                                smoothListView.setAdapter(mAdapter);
                                int height = mScreenHeight - getActivity().getResources().getDimensionPixelOffset(R.dimen.dimen_170);
                                mAdapter.getNoDataEntity(height);
                            }
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
                //无论成功或失败，在此开始请求品牌数据
                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();
                getBrandList(row0);
                if (store == null || store.size() == 0) {
                    smoothListView.setPullLoadEnable(false);
                    store = new ArrayList<>();
                    mAdapter = new TravelingAdapter(getActivity(), store);
                    smoothListView.setAdapter(mAdapter);
                    int height = mScreenHeight - getActivity().getResources().getDimensionPixelOffset(R.dimen.dimen_140);
                    mAdapter.getNoDataEntity(height);
                    if (i == 1) {
                        storeTotal.clear();//当非上拉加载时，将之前内存中数据清空
                    }
                }
            }

            @Override
            public boolean onCache(String result) {
                return false;
            }
        });
    }

    private void initData(List<FilterEntity> list) {
        //获取汽车品牌
        filterData = new FilterData();
        if (list != null && list.size() > 0) {
            filterData.setBrands(list);//设置品牌
        } else {
            filterData.setBrands(ModelUtil.getFilterData());//设置品牌
        }
        if (provinces != null && provinces.size() > 0) //&& getAddressData()!=null && getAddressData().size()>0
            filterData.setAddress(getAddressData());//设置地址

        fvTopFilter.setFilterData(getActivity(), filterData);//设置品牌和地址数据
//        getBrandList(row0);
        fvTopFilter.setOnLoadMore(new FilterView.loadMore() {
            @Override
            public void load() {
                getBrandList(row0++);
            }
        });
        fvTopFilter.setOnRefresh(new FilterView.refreshPull() {
            @Override
            public void refresh() {
                row0 = 1;
                fvTopFilter.filterAdapter = null;
                getBrandList(row0);
            }
        });

        // 筛选汽车品牌点击
        fvTopFilter.setOnItemFilterClickListener(new FilterView.OnItemFilterClickListener() {
            @Override
            public void onItemFilterClick(FilterEntity entity) {
                brandIdSelected = entity.getValue();
                row = 1;
                mAdapter = null;
                loadData(row, lat, lon, entity.getValue(), provinceId, cityId, areaId);
            }
        });
        //筛选地区点击
        fvTopFilter.setOnItemProvinceClickListener(new FilterView.OnItemProvinceClickListener() {
            @Override
            public void onItemProvinceClick(FilterThreeEntity entity) {
                provinceId = entity.getId();
                cityId = "";
                areaId = "";
                row = 1;
                mAdapter = null;
                loadData(row, lat, lon, brandIdSelected, provinceId, cityId, areaId);
            }
        });
        fvTopFilter.setOnItemCityClickListener(new FilterView.OnItemCityClickListener() {
            @Override
            public void onItemCityClick(FilterTwoEntity entity) {
                cityId = entity.getId();
                areaId = "";
                row = 1;
                mAdapter = null;
                loadData(row, lat, lon, brandIdSelected, provinceId, cityId, areaId);
            }
        });
        fvTopFilter.setOnItemAreaClickListener(new FilterView.OnItemAreaClickListener() {
            @Override
            public void onItemAreaClick(FilterEntity entity) {
                areaId = entity.getValue();
                row = 1;
                mAdapter = null;
                loadData(row, lat, lon, brandIdSelected, provinceId, cityId, areaId);
            }
        });
    }

    public List<FilterThreeEntity> getAddressData() {
        List<FilterThreeEntity> list = new ArrayList<>();
        if (provinces != null && provinces.size() > 0) {
            for (int i = 0, length = provinces.size(); i < length; i++) {
                list.add(new FilterThreeEntity(provinces.get(i).getProvinceName(), provinces.get(i).getProvinceId(), getFilterDataCity(i)));
            }
        }
        return list;
    }

    public List<FilterTwoEntity> getFilterDataCity(int i) {
        List<FilterTwoEntity> list = new ArrayList<>();
        if (provinces != null && provinces.size() > 0) {
            List<addressBean.city> b = provinces.get(i).getCity();
            int length = b.size();
            for (int j = 0; j < length; j++) {
                String str = b.get(j).getCityName();
                String id = b.get(j).getCityId();
                list.add(new FilterTwoEntity(str, id, getFilterDataArea(i, j)));
            }
        }
        return list;
    }

    public List<FilterEntity> getFilterDataArea(int i, int j) {
        List<FilterEntity> list = new ArrayList<>();
        if (provinces != null && provinces.size() > 0) {
            if (provinces.get(i).getCity().get(j).getArea() == null) {
                showLog("ProvinceName:" + provinces.get(i).getProvinceName() + ",cityName:" + provinces.get(i).getCity().get(j).getCityName());
                return list;
            }
            List<addressBean.area> b = provinces.get(i).getCity().get(j).getArea();
            int length = b.size();
            for (int k = 0; k < length; k++) {
                String str = b.get(k).getAreaName();
                String id = b.get(k).getAreaId();
                list.add(new FilterEntity(str, id));
            }
            return list;
        } else {
            return null;
        }
    }

    public void getBrandList(int row) {
        String path = Config.httpIp + Config.Urls.queryCarBrandBs;
        params = new RequestParams(path);
//        params.addParameter("startRow", row+"");
//        params.addParameter("rows", "20");
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
                fvTopFilter.lv_brand.stopLoadMore();
                fvTopFilter.lv_brand.stopRefresh();
            }

            @Override
            public boolean onCache(String arg0) {
                doResponse(arg0);
                return true;
            }
        });
    }

    private void doResponse(String arg0) {
        try {
            if (arg0 == null || TextUtils.isEmpty(arg0)) return;
            JSONObject object = new JSONObject(arg0);
            String result = object.getString("code");
            if (result.equals("1")) {
                if (object.has("data")) {
                    try {
                        String str = object.getString("data");
                        carBrands = new Gson().fromJson(str, new TypeToken<List<CarBrand>>() {
                        }.getType());
//                        if (carBrands.size() < 20)
//                            fvTopFilter.setFilterListViewPullLoad(false);
//                        else
//                            fvTopFilter.setFilterListViewPullLoad(true);
                        if (fvTopFilter.filterAdapter == null) {
                            carBrandsTotal = carBrands;
                            entity = new ArrayList<>();
                            for (CarBrand brand : carBrands) {
                                entity.add(new FilterEntity(brand.getBrandName(), brand.getId()));
                            }
                            if (entity != null && entity.size() > 0) {
//                                filterData.setBrands(entity);
//                                fvTopFilter.setFilterAdapter();
                                entity.add(new FilterEntity("不限", ""));
                                initData(entity);
                            }
                        } else {
//                            carBrandsTotal.addAll(carBrands);
                            List<FilterEntity> entity1 = new ArrayList<>();
                            for (CarBrand brand : carBrands) {//carBrandsTotal
                                entity1.add(new FilterEntity(brand.getBrandName(), brand.getId()));
                            }
                            if (entity1 != null && entity1.size() > 0)
                                filterData.setBrands(entity1);
                            fvTopFilter.setFilterData(getActivity(), filterData);
                            fvTopFilter.filterAdapter.update(entity1);
                        }
//                        row0++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                showToast(object.getString("message"), 1000);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        row = 1;
        mAdapter = null;
        provinceId = "";
        cityId = "";
        areaId = "";
        //重置下拉栏
        fvTopFilter.resetTitleText();
        loadData(row, lat, lon, brandIdSelected, provinceId, cityId, areaId);
    }

    @Override
    public void onLoadMore() {
        row++;
        loadData(row, lat, lon, "", provinceId, cityId, areaId);
    }

    @Override
    protected void lazyLoad() {
        showLog("index lazyload");
        if (!isPrepared || !isVisible) {
            return;
        }
        initData(null);
        if (firstLoaded) {
            if (sp.getString("brandIdSelected") == null) return;
            brandIdSelected = sp.getString("brandIdSelected");
            showLog("此时获取到的brandId" + brandIdSelected);
            smoothListView.autoRefresh();
        }
    }

    //高德地图定位当前城市
    private void getCity() {
        mLocationClient = new AMapLocationClient(getActivity().getApplicationContext());
        mLocationClient.setLocationListener(this);
        AMapLocationClientOption mLocationOption;
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                lp = amapLocation;
//                ToastMakeText.showToast(getActivity(),amapLocation.getCity(), 2000);
                cityCurrent = amapLocation.getCity();
                mLocationClient.stopLocation();
                //定位成功后开始请求数据
                lat = lp.getLatitude();
                lon = lp.getLongitude();
                loadData(row, lat, lon, brandIdSelected, provinceId, cityId, areaId);
                firstLoaded = true;
            } else {
                showLog("location Error, ErrCode:" + amapLocation.getErrorCode() + ", errInfo:" + amapLocation.getErrorInfo());
                //定位失败时，经纬度传空值，城市写死深圳
                loadData(row, 0, 0, brandIdSelected, provinceId, cityId, areaId);
                //只定位一次之后就不再请求定位服务
                mLocationClient.onDestroy();
                firstLoaded = true;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
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
//                    Toast.makeText(getActivity(), "解析数据成功", Toast.LENGTH_LONG).show();
                    showLog("解析JSON数据完成");
                    filterData.setAddress(getAddressData());
                    fvTopFilter.setFilterData(getActivity(), filterData);
//                    initData();
                    break;
                case MSG_LOAD_FAILED:
                    Toast.makeText(getActivity(), "解析数据失败", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };

    private void initJsonData() {
        String JsonData = new GetJsonDataUtil().getJson(getActivity(), "address.json");
        ArrayList<addressBean> jsonBean = parseData(JsonData);
        provinces = new ArrayList<>();
        for (addressBean a : jsonBean) {
            provinces.add(a);
        }
        mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);
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
            mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODEFINISH && resultCode == RESULT_CODE_EDIT) {
            brandIdSelected = sp.getString("brandIdSelected");
            smoothListView.autoRefresh();
        }
    }

    @OnClick(R.id.ll_city_selection)
    public void onClick() {
        startActivity(new Intent(getActivity(), CitySelectionActivity.class));
    }
}
