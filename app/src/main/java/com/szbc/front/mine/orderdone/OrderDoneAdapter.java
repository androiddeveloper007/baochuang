package com.szbc.front.mine.orderdone;

/**
 * Created by ZP on 2017/6/15.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.szbc.android.R;
import com.szbc.model.Product;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class OrderDoneAdapter extends BaseAdapter {
    List<Product> list;
    private Context context;
    private boolean isNoData;
    private int height;

    public OrderDoneAdapter(Context context, List<Product> list) {
        this.context = context;
        this.list = new ArrayList<>();
        this.list = list;
        if(list==null || list.size()==0){
            list.add(new Product());
        }
    }
    // 暂无数据
    public void getNoDataEntity(int height) {
        List<Product> list = new ArrayList<>();
        Product entity = new Product();
        this.isNoData = true;
        this.height = height;
        list.add(entity);
        this.list = list;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return list.size();
    }
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        if (isNoData) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_no_data_layout, null);
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            RelativeLayout rootView = ButterKnife.findById(convertView, R.id.rl_root_view);
            rootView.setLayoutParams(params);
            return convertView;
        }
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_detail_product, parent, false);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_done_detail_10);
            holder.tv_info = (TextView) convertView.findViewById(R.id.tv_done_detail_10_0);
            holder.tv_price = (TextView) convertView.findViewById(R.id.tv_done_detail_10_1);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.tv_name.setText(list.get(position).getGoodsInfo());
        if(list.get(position).getBuyCount()!=null)
            holder.tv_info.setText(list.get(position).getBuyCount()+"件");
        holder.tv_price.setText("¥"+list.get(position).getPrice());
        return convertView;
    }
    class Holder {
        private TextView tv_name;
        private TextView tv_info;
        private TextView tv_price;
    }
}