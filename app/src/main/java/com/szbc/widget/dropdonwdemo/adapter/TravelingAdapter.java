package com.szbc.widget.dropdonwdemo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.szbc.android.R;
import com.szbc.model.Store;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TravelingAdapter extends BaseListAdapter<Store> {

    private boolean isNoData;
    private int height;
    private List<Store> list;
    Context mContext;

    public TravelingAdapter(Context context, List<Store> list) {
        super(context,list);
        this.mContext = context;
        if(list==null || list.size()==0){
            list.add(new Store());
        }
        this.list = list;
    }

    public void update(List<Store> list) {
        this.list = list;
        notifyDataSetChanged();
    }
    // 暂无数据
    public void getNoDataEntity(int height) {
        List<Store> list = new ArrayList<>();
        Store entity = new Store();
        this.isNoData = true;
        this.height = height;
        list.add(entity);
        update(list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (isNoData) {
            convertView = mInflater.inflate(R.layout.item_no_data_layout, null);
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            RelativeLayout rootView = ButterKnife.findById(convertView, R.id.rl_root_view);
            rootView.setLayoutParams(params);
            return convertView;
        }
        // 正常数据
        final ViewHolder holder;
        if (convertView != null && convertView instanceof LinearLayout) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = mInflater.inflate(R.layout.item_travel, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        Store store = getItem(position);
        holder.llRootView.setVisibility(View.VISIBLE);
        mImageManager.loadUrlImage(store.getBusiShopPicUrl(), holder.ivImage);
        holder.tv_company.setText(store.getBusiShopName());
        holder.tv_address.setText(store.getAddressDetail());
        if(holder.tv_mileage!=null) {
            holder.tv_mileage.setText(store.getDistance()+"公里");
        }
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.ll_root_view)
        LinearLayout llRootView;
        @BindView(R.id.iv_image)
        ImageView ivImage;
        @BindView(R.id.tv_company)
        TextView tv_company;
        @BindView(R.id.tv_address)
        TextView tv_address;
        @BindView(R.id.tv_mileage_index)
        TextView tv_mileage;
        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
