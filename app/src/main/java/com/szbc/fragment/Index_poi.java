package com.szbc.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.tool.ToastUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.szbc.widget.dropdonwdemo.adapter.TravelingAdapter;
import com.szbc.widget.dropdonwdemo.model.FilterData;
import com.szbc.widget.dropdonwdemo.model.FilterEntity;
import com.szbc.widget.dropdonwdemo.model.TravelingEntity;
import com.szbc.widget.dropdonwdemo.utils.DensityUtil;
import com.szbc.widget.dropdonwdemo.utils.ModelUtil;
import com.szbc.widget.dropdonwdemo.view.FilterView;
import com.szbc.android.R;
import com.szbc.base.Config;
import com.szbc.front.index.FourSStoreDetail;
import com.szbc.model.Store;
import com.szbc.tool.ToastMakeText;
import com.szbc.tool.config.EncryptedSharedPreferences;
import com.szbc.widget.xlistview.XListView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 首页
 * 核心需求：本次需求功能主要是面对用户使用APP对4s店进行预约的功能，其中提供上门取送车服务，以及查询历史维修记录.
 * 维修服务主要是车辆在4S店检查结果为主要依据。再根据结果进行交易的“后维护模式”
 */
public class Index_poi extends BaseFragment implements XListView.IXListViewListener,AMapLocationListener, PoiSearch.OnPoiSearchListener {//
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
    private Handler mHandler = new Handler();
    private boolean isPrepared;// 标志fragment是否初始化完成

    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;
    private PoiResult poiResult; // poi返回的结果
    private List<PoiItem> poiItems;// poi数据
    private AMapLocation lp;
    private String cityCurrent;
    private int row = 1;
    private EncryptedSharedPreferences sp;
    private String  brandIdSelected="";
    private List<Store> store;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.index, container, false);
            ButterKnife.bind(this, view);
            if (Build.VERSION.SDK_INT > 20) {
                ViewGroup.LayoutParams lp = rlNavigation.getLayoutParams();
                lp.height = getActivity().getResources().getDimensionPixelOffset(R.dimen.dimen_70);
                rlNavigation.setLayoutParams(lp);
                RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp1.topMargin = getActivity().getResources().getDimensionPixelOffset(R.dimen.dimen_30);
                rlNavigation1.setLayoutParams(lp1);
            }
            sp = new EncryptedSharedPreferences(getContext());
            initView();
            isPrepared = true;
            lazyLoad();
            getCity();
        }
        return view;
    }

    private void loadData(int i,double lat,double lon) {
        String path = Config.httpIp + Config.Urls.queryBusiShopsList;
        params0 = new RequestParams(path);
        JSONObject js_request0 = new JSONObject();
        try {
            String token = sp.getString("token");
            js_request0.put("token", token);
            js_request0.put("lat", lat);
            js_request0.put("lng", lon);
            js_request0.put("brandId", brandIdSelected);
            js_request0.put("province", "");//1951
            js_request0.put("city", "");//1976
            js_request0.put("area", "");//1978
            js_request0.put("startRow", ""+i);
            js_request0.put("rows", "20");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params0.setAsJsonContent(true);
        params0.setBodyContent(js_request0.toString());
        params0.setConnectTimeout(10 * 1000);
        x.http().post(params0, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String arg0) {
                try {
                    JSONObject object = new JSONObject(arg0);
                    String result = object.getString("code");
                    String msg = object.getString("message");
                    if (result.equals("1")) {
                        showToast(msg, 2000);
                        if(object.has("data")){
                            String data = object.getString("data");
                            store = new Gson().fromJson(data, new TypeToken<List<Store>>() {}.getType());
                            if(store!=null && store.size()>0){
                                //处理集合并显示到列表中
                                mAdapter = new TravelingAdapter(getActivity(),store);// poiItems, lp
                                smoothListView.setAdapter(mAdapter);
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
            }
            @Override
            public boolean onCache(String result) {
                return false;
            }
        });
    }

    private void initView() {
//        View view = LayoutInflater.from(getActivity()).inflate(R.layout.header_filter_layout, smoothListView, false);
//        smoothListView.addHeaderView(view);
        smoothListView.setPullRefreshEnable(true);
        smoothListView.setPullLoadEnable(false);
        smoothListView.setXListViewListener(this);
    }

    /*  1.未登录状态：默认用户定位筛选出最近的4s店（近距离的排第一）
        2.登录状态未添加车辆信息：默认用户定位筛选出最近的4s店（近距离的排第一）
        3.登录状态已添加车辆信息：默认用户定位并筛选默认车辆品牌最近的4s店（近距离的排第一）*/
    private void initData() {
        mScreenHeight = DensityUtil.getWindowHeight(getActivity());
        // ListView数据
        travelingList = ModelUtil.getTravelingData();//模拟list view数据
        // 设置ListView数据
//        mAdapter = new TravelingAdapter(getActivity(), travelingList);
//        smoothListView.setAdapter(mAdapter);
        // 筛选数据
        filterData = new FilterData();
        filterData.setBrands(ModelUtil.getFilterData());//List<FilterEntity>
//        filterData.setAddress(ModelUtil.getCategoryData());//List<FilterTwoEntity>

        // 设置筛选数据
        fvTopFilter.setFilterData(getActivity(), filterData);

        // 分类Item点击
//        fvTopFilter.setOnItemCategoryClickListener(new FilterView.OnItemCategoryClickListener() {
//            @Override
//            public void onItemCategoryClick(FilterTwoEntity entity) {
//                fillAdapter(ModelUtil.getCategoryTravelingData(entity));
//            }
//        });

        // 筛选Item点击
        fvTopFilter.setOnItemFilterClickListener(new FilterView.OnItemFilterClickListener() {
            @Override
            public void onItemFilterClick(FilterEntity entity) {
                fillAdapter(ModelUtil.getFilterTravelingData(entity));
            }
        });

        smoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), FourSStoreDetail.class);
                if(store!=null && store.size()>0)
                    i.putExtra("busiShopId",store.get(position).getId());
                startActivity(i);
            }
        });
    }

    // 填充数据
    private void fillAdapter(List<TravelingEntity> list) {
        if (list == null || list.size() == 0) {
            int height = mScreenHeight - DensityUtil.dip2px(getActivity(), 95);
//            mAdapter.setData(ModelUtil.getNoDataEntity(height));
        } else {
//            mAdapter.setData(list);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                smoothListView.stopRefresh();
            }
        }, 600);
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                smoothListView.stopLoadMore();
                smoothListView.setPullLoadEnable(false);
            }
        }, 600);
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
        initData();
    }

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //高德地图定位当前城市
    private void getCity() {
        //声明定位回调监听器
//        AMapLocationListener mLocationListener = new AMapLocationListener();
        //初始化定位
        mLocationClient = new AMapLocationClient(getActivity().getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        AMapLocationClientOption mLocationOption;
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                lp = amapLocation;
                //可在其中解析amapLocation获取相应内容。
                ToastMakeText.showToast(getActivity(),amapLocation.getCity(), 2000);
                cityCurrent = amapLocation.getCity();
                mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
                //定位成功后开始请求数据
                double lat = lp.getLatitude();
                double lon = lp.getLongitude();
                loadData(row,lat,lon);
//                doPoiSearch();
            }else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("AmapError","location Error, ErrCode:"+ amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
                //定位失败时，经纬度传空值，城市写死深圳

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
    }

    @Override
    public void onPoiSearched(PoiResult result, int rcode) {
        if (rcode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems != null && poiItems.size() > 0) {
                        //处理集合并显示到列表中
//                        mAdapter = new TravelingAdapter(getActivity(), poiItems, lp);
                        smoothListView.setAdapter(mAdapter);
                    } else if (suggestionCities != null && suggestionCities.size() > 0) {
                    } else {
                        ToastUtil.show(getActivity(),
                                R.string.no_result);
                    }
                }
            } else {
                ToastUtil.show(getActivity(), R.string.no_result);
            }
        } else {
            ToastUtil.showerror(getActivity(), rcode);
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    private void doPoiSearch(){
        String keyWord = "4s店";
        currentPage = 0;
        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query = new PoiSearch.Query(keyWord, "", cityCurrent);
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页


        if (lp != null) {
            poiSearch = new PoiSearch(getActivity(), query);
            poiSearch.setOnPoiSearchListener(this);
            //设置中心点和范围
            PoiSearch.SearchBound searchBound = new PoiSearch.SearchBound(new LatLonPoint(lp.getLatitude(),lp.getLongitude()),2000);
            poiSearch.setBound(searchBound);
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }
}
