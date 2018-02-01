package com.szbc.widget.dropdonwdemo.view;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.szbc.widget.dropdonwdemo.adapter.FilterLeftAdapter;
import com.szbc.widget.dropdonwdemo.adapter.FilterMidAdapter;
import com.szbc.widget.dropdonwdemo.adapter.FilterOneAdapter;
import com.szbc.widget.dropdonwdemo.adapter.FilterRightAdapter;
import com.szbc.widget.dropdonwdemo.model.FilterData;
import com.szbc.widget.dropdonwdemo.model.FilterEntity;
import com.szbc.widget.dropdonwdemo.model.FilterThreeEntity;
import com.szbc.widget.dropdonwdemo.model.FilterTwoEntity;
import com.szbc.android.R;
import com.szbc.base.Config;
import com.szbc.tool.ToastMakeText;
import com.szbc.widget.xlistview.XListView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterView extends LinearLayout implements View.OnClickListener {

    @BindView(R.id.tv_filter)
    TextView tvFilter;
    @BindView(R.id.iv_filter_arrow)
    ImageView ivFilterArrow;
    @BindView(R.id.ll_filter)
    LinearLayout llFilter;
    @BindView(R.id.tv_sort)
    TextView tvSort;
    @BindView(R.id.iv_sort_arrow)
    ImageView ivSortArrow;
    @BindView(R.id.ll_sort)
    LinearLayout llSort;
    @BindView(R.id.tv_category)
    TextView tvCategory;
    @BindView(R.id.iv_category_arrow)
    ImageView ivCategoryArrow;
    @BindView(R.id.ll_category)
    LinearLayout llCategory;
    @BindView(R.id.ll_head_layout)
    LinearLayout llHeadLayout;
    @BindView(R.id.view_mask_bg)
    View viewMaskBg;
    @BindView(R.id.lv_left)
    ListView lvLeft;
    @BindView(R.id.lv_mid)
    ListView lvMid;
    @BindView(R.id.lv_right)
    ListView lvRight;
    @BindView(R.id.lv_brand)
    public XListView lv_brand;
    @BindView(R.id.ll_content_list_view)
    LinearLayout llContentListView;
    private Context mContext;
    private Activity mActivity;
    private boolean isShowing = false;
    private int filterPosition = -1;
    private int panelHeight;
    private FilterData filterDataAddress;

     private FilterThreeEntity selectedAddressEntity; // 被选择的分类项
    private FilterEntity selectedFilterEntity; // 被选择的筛选项

    private FilterLeftAdapter leftAdapter;
    private FilterMidAdapter midAdapter;
    private FilterRightAdapter rightAdapter;
    public FilterOneAdapter filterAdapter;
    public loadMore loadMoreListener;
    public refreshPull refreshListener;
    private FilterTwoEntity aaa;
    private FilterTwoEntity ccc;

    public FilterView(Context context) {
        super(context);
        init(context);
    }

    public FilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FilterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        this.mActivity = (Activity) context;
        View view = LayoutInflater.from(context).inflate(R.layout.view_filter_layout, this);
        ButterKnife.bind(this, view);
        initView();
        initListener();
    }

    private void initView() {
        viewMaskBg.setVisibility(GONE);
        llContentListView.setVisibility(GONE);
    }

    private void initListener() {
        llCategory.setOnClickListener(this);
        llSort.setOnClickListener(this);
        llFilter.setOnClickListener(this);
        viewMaskBg.setOnClickListener(this);
        llContentListView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_filter:
                filterPosition = 0;
                if(isShowing){
                    hide();
                    break;
                }
                showFilterLayout(filterPosition);
                if (onFilterClickListener != null) {
                    onFilterClickListener.onFilterClick(filterPosition);
                }
                break;
            case R.id.ll_category:
                filterPosition = 1;
                if(isShowing){
                    hide();
                    break;
                }
                showFilterLayout(filterPosition);
                if (onFilterClickListener != null) {
                    onFilterClickListener.onFilterClick(filterPosition);
                }
                break;
            case R.id.view_mask_bg:
                hide();
                break;
            default:break;
        }
    }

    // 复位筛选的显示状态
    public void resetFilterStatus() {
        tvCategory.setTextColor(mContext.getResources().getColor(R.color.font_black_2));
        ivCategoryArrow.setImageResource(R.mipmap.home_down_arrow);

        tvSort.setTextColor(mContext.getResources().getColor(R.color.font_black_2));
        ivSortArrow.setImageResource(R.mipmap.home_down_arrow);

        tvFilter.setTextColor(mContext.getResources().getColor(R.color.font_black_2));
        ivFilterArrow.setImageResource(R.mipmap.home_down_arrow);
    }

    // 复位所有的状态
    public void resetAllStatus() {
        resetFilterStatus();
        hide();
    }

    // 显示筛选布局
    public void showFilterLayout(int position) {
//        resetFilterStatus();
        switch (position) {
            case 0:
                lvLeft.setVisibility(GONE);
                lvMid.setVisibility(GONE);
                lvRight.setVisibility(GONE);
                lv_brand.setVisibility(VISIBLE);
                if(filterAdapter!=null){

                }else{
                    setFilterAdapter();
                }
                break;
            case 1:
                lv_brand.setVisibility(GONE);
                lvLeft.setVisibility(VISIBLE);
                lvMid.setVisibility(VISIBLE);
                lvRight.setVisibility(VISIBLE);
                if(leftAdapter!=null){

                }else{
                    setCategoryAdapter();
                }
                break;
            case 2:
                break;
            default:break;
        }
        if (isShowing) return;
        show();
    }

    // 设置分类数据
    private void setCategoryAdapter() {
        if(mContext==null)
            ToastMakeText.showToast(mActivity,"tvCategory is null",1000);
        if(tvCategory==null)
            ToastMakeText.showToast(mActivity,"tvCategory is null",1000);
//        tvCategory.setTextColor(mContext.getResources().getColor(R.color.orange));
//        ivCategoryArrow.setImageResource(R.mipmap.home_up_arrow);

        if (selectedAddressEntity == null && filterDataAddress!=null && filterDataAddress.getAddress()!=null) {
            selectedAddressEntity = filterDataAddress.getAddress().get(0);
        }else{
            if(Config.isDebug)Log.e("zp","地址数据获取失败");
            return;
        }
        leftAdapter = new FilterLeftAdapter(mContext, filterDataAddress.getAddress());
        lvLeft.setAdapter(leftAdapter);

        //从保存的数据中获取选择的数据
//        leftAdapter.setSelectedEntity(selectedAddressEntity);

        // 如果右边有选中的数据，设置
        aaa = filterDataAddress.getAddress().get(0).getList().get(0);
        midAdapter = new FilterMidAdapter(mContext, filterDataAddress.getAddress().get(0).getList());
        lvMid.setAdapter(midAdapter);
//        rightAdapter.setSelectedEntity(filterDataAddress.getAddress().get(0).getList().get(0));

        //如果第三项有数据设置
        rightAdapter = new FilterRightAdapter(mContext, filterDataAddress.getAddress().get(0).getList().get(0).getList());
        lvRight.setAdapter(rightAdapter);
//        threeAdapter.setSelectedEntity(filterDataAddress.getAddress().get(0).getList().get(0).getList().get(0));
        //设置初始值

        lvLeft.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedAddressEntity = filterDataAddress.getAddress().get(position);
//                leftAdapter.setSelectedEntity(selectedAddressEntity);

                // 右边列表视图
                midAdapter = new FilterMidAdapter(mContext, selectedAddressEntity.getList());//.getList()
//                rightAdapter.setSelectedEntity(selectedAddressEntity.getList().get(0));
                lvMid.setAdapter(midAdapter);
                if(onItemProvinceClickListener!=null){
                    onItemProvinceClickListener.onItemProvinceClick(selectedAddressEntity);
                }
                //区
                rightAdapter = new FilterRightAdapter(mContext,selectedAddressEntity.getList().get(0).getList());
                lvRight.setAdapter(rightAdapter);
                lvMid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        aaa = selectedAddressEntity.getList().get(position);
                        rightAdapter = new FilterRightAdapter(mContext, aaa.getList());
                        lvRight.setAdapter(rightAdapter);
                        if(onItemCityClickListener!=null){
                            onItemCityClickListener.onItemCityClick(aaa);
                        }
                        lvRight.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                FilterEntity b = aaa.getList().get(position);
                                tvCategory.setText(b.getKey());
                                if(onItemAreaClickListener!=null){
                                    onItemAreaClickListener.onItemAreaClick(b);
                                }
                                hide();
                            }
                        });
                    }
                });
                lvRight.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        FilterEntity b = selectedAddressEntity.getList().get(0).getList().get(position);
                        tvCategory.setText(b.getKey());
                        if(onItemAreaClickListener!=null){
                            onItemAreaClickListener.onItemAreaClick(b);
                        }
                        hide();
                    }
                });
            }
        });
        lvMid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ccc = selectedAddressEntity.getList().get(position);
                if(onItemCityClickListener!=null){
                    onItemCityClickListener.onItemCityClick(ccc);
                }
                rightAdapter = new FilterRightAdapter(mContext, ccc.getList());
                lvRight.setAdapter(rightAdapter);
                lvRight.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        FilterEntity b = ccc.getList().get(position);
                        tvCategory.setText(b.getKey());
                        hide();
                    }
                });
            }
        });
        lvRight.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FilterEntity b = selectedAddressEntity.getList().get(0).getList().get(position);
                tvCategory.setText(b.getKey());
                if(onItemAreaClickListener!=null){
                    onItemAreaClickListener.onItemAreaClick(b);
                }
                hide();
            }
        });
    }

    // 设置筛选数据
    public void setFilterAdapter() {
//        tvFilter.setTextColor(mActivity.getResources().getColor(R.color.orange));
//        ivFilterArrow.setImageResource(R.mipmap.home_up_arrow);
        lv_brand.setPullRefreshEnable(false);
        lv_brand.setPullLoadEnable(false);
        lv_brand.setAutoLoadEnable(false);
        lv_brand.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                if(refreshListener!=null){
                    refreshListener.refresh();
                }
            }
            @Override
            public void onLoadMore() {
                if(loadMoreListener!=null){
                    loadMoreListener.load();
                }
            }
        });
        if(filterDataAddress==null || filterDataAddress.getFilters()==null){
            ToastMakeText.showToast(mActivity,"品牌数据为空",2000);
            return;
        }
        filterAdapter = new FilterOneAdapter(mContext, filterDataAddress.getFilters());
        lv_brand.setAdapter(filterAdapter);
        lv_brand.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedFilterEntity = filterDataAddress.getFilters().get((int)id);
                filterAdapter.setSelectedEntity(selectedFilterEntity);
                tvFilter.setText(selectedFilterEntity.getKey());
                hide();
                if (onItemFilterClickListener != null) {
                    onItemFilterClickListener.onItemFilterClick(selectedFilterEntity);
                }
            }
        });
    }
    public void resetTitleText(){
        tvFilter.setText("品牌");
        tvCategory.setText("区域");
    }

    public void setOnLoadMore(loadMore load){
        this.loadMoreListener = load;
    }

    public void setOnRefresh(refreshPull refreshListener){
        this.refreshListener = refreshListener;
    }

    public interface loadMore{
        void load();
    }
    public interface refreshPull{
        void refresh();
    }
    // 动画显示
    private void show() {
        isShowing = true;
        viewMaskBg.setVisibility(VISIBLE);
        llContentListView.setVisibility(VISIBLE);
        llContentListView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                llContentListView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                panelHeight = llContentListView.getHeight();
//                Toast.makeText(mContext, "高度：" + panelHeight , Toast.LENGTH_LONG).show();
                ObjectAnimator.ofFloat(llContentListView, "translationY", -panelHeight, 0).setDuration(200).start();
            }
        });
    }

    // 隐藏动画
    public void hide() {
        isShowing = false;
        resetFilterStatus();
        viewMaskBg.setVisibility(View.GONE);
        ObjectAnimator.ofFloat(llContentListView, "translationY", 0, -panelHeight).setDuration(200).start();
    }

    // 设置筛选数据
    public void setFilterData(Activity activity, FilterData filterData) {
        this.mActivity = activity;
        this.filterDataAddress = filterData;
    }

    // 是否显示
    public boolean isShowing() {
        return isShowing;
    }

    // 筛选视图点击
    private OnFilterClickListener onFilterClickListener;

    public void setOnFilterClickListener(OnFilterClickListener onFilterClickListener) {
        this.onFilterClickListener = onFilterClickListener;
    }

    public interface OnFilterClickListener {
        void onFilterClick(int position);
    }

    //  省item点击
    private OnItemProvinceClickListener onItemProvinceClickListener;

    public void setOnItemProvinceClickListener(OnItemProvinceClickListener onItemProvinceClickListener) {
        this.onItemProvinceClickListener = onItemProvinceClickListener;
    }
    //  市item点击
    private OnItemCityClickListener onItemCityClickListener;
    public void setOnItemCityClickListener(OnItemCityClickListener onItemCityClickListener) {
        this.onItemCityClickListener = onItemCityClickListener;
    }
    //  区item点击
    private OnItemAreaClickListener onItemAreaClickListener;
    public void setOnItemAreaClickListener(OnItemAreaClickListener onItemAreaClickListener) {
        this.onItemAreaClickListener = onItemAreaClickListener;
    }

    public interface OnItemProvinceClickListener {
        void onItemProvinceClick(FilterThreeEntity entity);
    }

    public interface OnItemCityClickListener {
        void onItemCityClick(FilterTwoEntity entity);
    }

    public interface OnItemAreaClickListener {
        void onItemAreaClick(FilterEntity entity);
    }

    // 排序Item点击
    private OnItemSortClickListener onItemSortClickListener;

    public void setOnItemSortClickListener(OnItemSortClickListener onItemSortClickListener) {
        this.onItemSortClickListener = onItemSortClickListener;
    }

    public interface OnItemSortClickListener {
        void onItemSortClick(FilterEntity entity);
    }

    // 筛选Item点击
    private OnItemFilterClickListener onItemFilterClickListener;

    public void setOnItemFilterClickListener(OnItemFilterClickListener onItemFilterClickListener) {
        this.onItemFilterClickListener = onItemFilterClickListener;
    }

    public interface OnItemFilterClickListener {
        void onItemFilterClick(FilterEntity entity);
    }

    private void resetAdapterFilter(){

    }
    public void setFilterListViewPullLoad(boolean b){
        lv_brand.setPullLoadEnable(b);
    }
}
