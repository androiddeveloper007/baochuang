package com.szbc.widget;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.szbc.android.R;

/**
 * Created by ZP on 2017/6/2.
 */

public class ProgressDialog extends Dialog {
    public ProgressDialog(Context context) {
        super(context, R.style.progress_dialog);
        setContentView(R.layout.dialog);
        setCancelable(true);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView msg = (TextView) findViewById(R.id.id_tv_loadingmsg);
        msg.setText("卖力加载中");
        show();
    }

    public ProgressDialog(Context context, int themeResId) {
        super(context, R.style.progress_dialog);
        setContentView(R.layout.dialog);
        setCancelable(true);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView msg = (TextView) findViewById(R.id.id_tv_loadingmsg);
        msg.setText("卖力加载中");
        show();
    }

    protected ProgressDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
