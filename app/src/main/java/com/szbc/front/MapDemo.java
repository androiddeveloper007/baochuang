package com.szbc.front;

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
* 高德地图demo
* */
public class MapDemo extends BaseActivity {

    @BindView(R.id.view_need_offset)
    LinearLayout viewNeedOffset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapdemo);
        ButterKnife.bind(this);
        if(Build.VERSION.SDK_INT != 19 && Build.VERSION.SDK_INT != 20)
        StatusBarUtil.setTranslucentForImageView(this, 0, viewNeedOffset);

        initview();

    }

    /**
     * 初始化控件
     */
    public void initview() {
    }

    @OnClick(R.id.title_back)
    public void onClick() {
        this.finish();
    }
}
