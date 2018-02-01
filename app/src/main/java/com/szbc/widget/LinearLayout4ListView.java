package com.szbc.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

/**
 * Created by ZP on 2017/7/12.
 */

public class LinearLayout4ListView extends LinearLayout {
    private BaseAdapter adapter;
    private OnClickListener onClickListener = null;

    public LinearLayout4ListView(Context context) {
        super(context);
    }
    public LinearLayout4ListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public LinearLayout4ListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public LinearLayout4ListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void bindLinearLayout() {
        int count = adapter.getCount();
        this.removeAllViews();
        for (int i = 0; i < count; i++) {
            View v = adapter.getView(i, null, null);
            v.setOnClickListener(this.onClickListener);
            addView(v, i);
        }
        Log.v("countTAG", "" + count);
    }
}
