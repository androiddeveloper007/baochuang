package com.szbc.front.addcar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.szbc.android.R;
import com.szbc.base.BaseActivity;
import com.szbc.base.Config;
import com.szbc.front.addcar.sortedlistview.CharacterParser;
import com.szbc.front.addcar.sortedlistview.PinyinComparator1;
import com.szbc.front.addcar.sortedlistview.SideBar;
import com.szbc.front.addcar.sortedlistview.SortAdapter;
import com.szbc.front.addcar.sortedlistview.SortModel;
import com.szbc.front.mine.MyCarsActivity;
import com.szbc.model.CarBrand;
import com.szbc.tool.StatusBarUtil;
import com.szbc.widget.xlistview.XListView;

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

/**
 * 汽车品牌选择
 */
public class CarBrandActivity extends BaseActivity implements XListView.IXListViewListener {

    @BindView(R.id.view_need_offset)
    LinearLayout viewNeedOffset;
    @BindView(R.id.lv_car_brand)
    XListView lvCarBrand;
    public static final int REQUESTCODEFINISH = 12345;
    public static final int RESPONSECODEFINISH = 12346;
    @BindView(R.id.dialog)
    TextView dialog;
    @BindView(R.id.sidrbar)
    SideBar sideBar;
    private SortAdapter adapter;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator1 pinyinComparator;
    List<CarBrand> carBrands;
    List<CarBrand> carBrandsTotal;
    private Handler mHandler = new Handler();
    int row = 1;
    private Intent extraIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carbrand_activity);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT != 19 && Build.VERSION.SDK_INT != 20)
            StatusBarUtil.setTranslucentForImageView(this, 0, viewNeedOffset);
        context = this;
        initView();
        extraIntent = getIntent();
    }

    public void initView() {
        characterParser = CharacterParser.getInstance();//实例化汉字转拼音类
        pinyinComparator = new PinyinComparator1();
        sideBar.setTextView(dialog);
        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if(position != -1){
                    lvCarBrand.setSelection(position);
                }
            }
        });
        lvCarBrand.setPullRefreshEnable(false);
        lvCarBrand.setPullLoadEnable(false);
        lvCarBrand.setAutoLoadEnable(false);
        lvCarBrand.setXListViewListener(this);
        loadData(row);
    }

    @Override
    public void onRefresh() {
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                loadData(row);
            }
        }, 200);
    }

    private void loadData(int row1) {
        String path = Config.httpIp + Config.Urls.queryCarBrandBs;
        params = new RequestParams(path);
//        params.addParameter("startRow", row1+"");
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
//                lvCarBrand.stopLoadMore();
            }

            @Override
            public boolean onCache(String arg0) {
                doResponse(arg0);
                return true;// true: 信任缓存数据, 不在发起网络请求; false不信任缓存数据.
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
//                            lvCarBrand.setPullLoadEnable(false);
//                        else
//                            lvCarBrand.setPullLoadEnable(true);
                        if (adapter == null) {
                            carBrandsTotal = carBrands;
                            Collections.sort(carBrands, pinyinComparator);
                            adapter = new SortAdapter(context, carBrands);
                            lvCarBrand.setAdapter(adapter);
                        } else {
                            carBrandsTotal.addAll(carBrands);
                            adapter.update(carBrandsTotal);
                        }
                        setOnItemClickListener();
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

    private void setOnItemClickListener() {
        lvCarBrand.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(context, CarSeriesSelectActivity.class);
                i.putExtra("brandId", carBrandsTotal.get(position - 1).getId());
                i.putExtra("brandPicture", carBrandsTotal.get(position - 1).getBrandPictureUrl());
                if (extraIntent != null && extraIntent.getStringExtra("FROM4S") != null) {
                    i.putExtra("busiShopId", extraIntent.getStringExtra("busiShopId"));
                    i.putExtra("busiShopName", extraIntent.getStringExtra("busiShopName"));
                    i.putExtra("busiShopPhone", extraIntent.getStringExtra("busiShopPhone"));
                    i.putExtra("busiShopAddress", extraIntent.getStringExtra("busiShopAddress"));
                    i.putExtra("FROM4S", "");
                } else {
                    i.putExtra("ISFROMMYCAR", "");
                }
                startActivityForResult(i, REQUESTCODEFINISH);
//                finish();
            }
        });
    }

    @OnClick(R.id.title_back)
    public void onClick() {
        this.finish();
    }

    /**
     * 为ListView填充数据
     */
    private List<SortModel> filledData(String[] date) {
        List<SortModel> mSortList = new ArrayList<SortModel>();
        for (int i = 0; i < date.length; i++) {
            SortModel sortModel = new SortModel();
            sortModel.setName(date[i]);
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(date[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }
            mSortList.add(sortModel);
        }
        return mSortList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUESTCODEFINISH && resultCode == RESPONSECODEFINISH) {
            setResult(MyCarsActivity.RESULT_CODE_EDIT);
            finish();
        }
    }
}
