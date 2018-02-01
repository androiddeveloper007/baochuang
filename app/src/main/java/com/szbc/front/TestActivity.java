package com.szbc.front;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.szbc.widget.dropdonwdemo.adapter.TravelingAdapter;
import com.szbc.widget.dropdonwdemo.model.FilterData;
import com.szbc.widget.dropdonwdemo.model.FilterEntity;
import com.szbc.widget.dropdonwdemo.model.TravelingEntity;
import com.szbc.widget.dropdonwdemo.utils.DensityUtil;
import com.szbc.widget.dropdonwdemo.utils.ModelUtil;
import com.szbc.widget.dropdonwdemo.view.FilterView;
import com.szbc.android.R;
import com.szbc.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestActivity extends BaseActivity {

    @BindView(R.id.fv_top_filter)
    FilterView fvTopFilter;
    @BindView(R.id.lv_test)
    ListView smoothListView;
    private FilterData filterData; // 筛选数据

    private Activity mActivity;
    private Context mContext;
    private int mScreenHeight; // 屏幕高度

    private TravelingAdapter mAdapter; // 主页数据
    private List<TravelingEntity> travelingList = new ArrayList<>(); // ListView数据

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);
        ButterKnife.bind(this);

        mActivity = TestActivity.this;
        mContext = this;
        initView();
        initData();
    }

    private void initView()
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.header_filter_layout, smoothListView, false);
        smoothListView.addHeaderView(view);

    }

    private void initData()
    {
        mScreenHeight = DensityUtil.getWindowHeight(this);
        // ListView数据
        travelingList = ModelUtil.getTravelingData();
        // 设置ListView数据
//        mAdapter = new TravelingAdapter(this, travelingList);
        smoothListView.setAdapter(mAdapter);

        // 筛选数据
        filterData = new FilterData();
//        filterData.setCategory(ModelUtil.getCategoryData());
//        filterData.setSorts(ModelUtil.getSortData());
//        filterData.setFilters(ModelUtil.getFilterData());

        // 设置筛选数据
        fvTopFilter.setFilterData(mActivity, filterData);
//        fvTopFilter.showFilterLayout(0);

        // 分类Item点击
//        fvTopFilter.setOnItemCategoryClickListener(new FilterView.OnItemCategoryClickListener()
//        {
//            @Override
//            public void onItemCategoryClick(FilterTwoEntity entity)
//            {
//                fillAdapter(ModelUtil.getCategoryTravelingData(entity));
//            }
//        });

        // 排序Item点击
        fvTopFilter.setOnItemSortClickListener(new FilterView.OnItemSortClickListener()
        {
            @Override
            public void onItemSortClick(FilterEntity entity)
            {
                fillAdapter(ModelUtil.getSortTravelingData(entity));
            }
        });

        // 筛选Item点击
        fvTopFilter.setOnItemFilterClickListener(new FilterView.OnItemFilterClickListener()
        {
            @Override
            public void onItemFilterClick(FilterEntity entity)
            {
                fillAdapter(ModelUtil.getFilterTravelingData(entity));
            }
        });
    }

    // 填充数据
    private void fillAdapter(List<TravelingEntity> list)
    {
        if (list == null || list.size() == 0)
        {
            int height = mScreenHeight - DensityUtil.dip2px(mContext, 95); // 95 = 标题栏高度 ＋ FilterView的高度
//            mAdapter.setData(ModelUtil.getNoDataEntity(height));
        } else {
//            mAdapter.setData(list);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (!fvTopFilter.isShowing()) {
            super.onBackPressed();
        } else {
            fvTopFilter.resetAllStatus();
        }
    }

}
