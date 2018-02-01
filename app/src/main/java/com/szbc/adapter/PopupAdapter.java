package com.szbc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.szbc.android.R;

/**
 * Created by ZP on 2017/6/16.
 */

public class PopupAdapter extends RecyclerView.Adapter<PopupAdapter.MyViewHolder> {
    private int[] id;
    private String[] text;
    public View.OnClickListener[] onClickListeners;
    private Context context;

    public PopupAdapter(Context context, int[] id, String[] text, View.OnClickListener[] onClickListeners) {
        this.id = id;
        this.text = text;
        this.onClickListeners = onClickListeners;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pop_up_item, null);
        MyViewHolder h = new MyViewHolder(view);
        h.itemView.setBackgroundResource(R.drawable.pw_item_bg);
        return h;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.iv.setImageResource(id[position]);
        holder.tv.setText(text[position]);
        holder.itemView.setOnClickListener(onClickListeners[position]);
    }

    @Override
    public int getItemCount() {
        return Math.min(id.length, text.length);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        ImageView iv;

        MyViewHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.item_icon);
            tv = (TextView) itemView.findViewById(R.id.item_name);
        }
    }
}
