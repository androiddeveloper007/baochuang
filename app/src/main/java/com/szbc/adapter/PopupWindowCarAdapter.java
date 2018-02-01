package com.szbc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.szbc.android.R;
import com.szbc.model.UserCar;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

/**
 * Created by ZP on 2017/4/26.
 */

public class PopupWindowCarAdapter extends RecyclerView.Adapter<PopupWindowCarAdapter.MyHolder> implements View.OnClickListener{
    private Context context;
    private List<UserCar> userCars;
    private OnItemClickListener mOnItemClickListener;

    public static interface OnItemClickListener {
        void onItemClick(View view , int position);
    }

    public PopupWindowCarAdapter(Context c, List<UserCar> list) {
        this.context = c;
        this.userCars = list;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pop_up_item_car, null);
        MyHolder h = new MyHolder(view);
        h.itemView.setBackgroundResource(R.drawable.pw_item_bg);
        view.setOnClickListener(this);
        return h;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, final int position) {
        //将position保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(position);
        ImageOptions options = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.icon_stub)
                .setFailureDrawableId(R.drawable.icon_error)
                .build();
        x.image().bind(holder.iv, userCars.get(position).getBrandPictureUrl(), options);
        holder.tv_carBrand.setText(userCars.get(position).getCarBrand());
        holder.tv_carModel.setText(userCars.get(position).getCarYearStyle());
    }

    @Override
    public int getItemCount() {
        return userCars.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(v,(int)v.getTag());
        }
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    public static class MyHolder extends RecyclerView.ViewHolder{
        TextView tv_carBrand, tv_carModel;
        ImageView iv;
        MyHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.item_icon);
            tv_carBrand = (TextView) itemView.findViewById(R.id.tv_carBrand);
            tv_carModel = (TextView) itemView.findViewById(R.id.tv_carModel);
        }
    }
}

