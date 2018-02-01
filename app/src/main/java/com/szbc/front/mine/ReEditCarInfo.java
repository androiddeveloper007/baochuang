package com.szbc.front.mine;

import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.szbc.android.R;
import com.szbc.base.BaseActivity;
import com.szbc.tool.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
* 修改车辆信息
* */
public class ReEditCarInfo extends BaseActivity {

    @BindView(R.id.view_need_offset)
    LinearLayout viewNeedOffset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reedit_carinfo);
        ButterKnife.bind(this);
        if(Build.VERSION.SDK_INT != 19 && Build.VERSION.SDK_INT != 20)
            StatusBarUtil.setTranslucentForImageView(this, 0, viewNeedOffset);
        initView();
    }

    public void initView() {

    }

    @OnClick(R.id.title_back)
    public void onClick() {
        this.finish();
    }
}
