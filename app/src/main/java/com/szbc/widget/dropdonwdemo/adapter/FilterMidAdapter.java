package com.szbc.widget.dropdonwdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.szbc.widget.dropdonwdemo.model.FilterTwoEntity;
import com.szbc.android.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sunfusheng on 16/4/23.
 */
public class FilterMidAdapter extends BaseListAdapter<FilterTwoEntity> {

    private FilterTwoEntity selectedEntity;

    public FilterMidAdapter(Context context, List<FilterTwoEntity> list) {
        super(context, list);
    }

    public void setSelectedEntity(FilterTwoEntity filterEntity) {
        this.selectedEntity = filterEntity;
        for (FilterTwoEntity entity : getData()) {
            entity.setSelected(entity.getList().equals(selectedEntity.getList()));
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final FilterRightAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_filter_one, null);
            holder = new FilterRightAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (FilterRightAdapter.ViewHolder) convertView.getTag();
        }

        FilterTwoEntity entity = getItem(position);

        holder.tvTitle.setText(entity.getType());
        if (entity.isSelected()) {
            holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.orange));
        } else {
            holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.font_black_3));
        }

        return convertView;
    }

    static class ViewHolder {

        @BindView(R.id.iv_image)
        ImageView ivImage;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
