package com.szbc.front.mine;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.szbc.android.R;
import com.szbc.base.BaseActivity;
import com.szbc.dialog.LuckyDrawDialog_1;
import com.szbc.tool.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 关于我们
 */
public class AboutUsActivity extends BaseActivity {

    @BindView(R.id.view_need_offset)
    LinearLayout viewNeedOffset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutus_activity);
        ButterKnife.bind(this);
        if(Build.VERSION.SDK_INT != 19 && Build.VERSION.SDK_INT != 20)
            StatusBarUtil.setTranslucentForImageView(this, 0, viewNeedOffset);

        initData();
        initView();
    }

    public void initView() {
    }

    private void initData() {

    }

    @OnClick({R.id.title_back,R.id.about_us_phone})
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.title_back:
                this.finish();
                break;
            case R.id.about_us_phone:
                LuckyDrawDialog_1 d = new LuckyDrawDialog_1(this, true, 0.7);
                d.setMessage("0755-25625428", true);
                d.setBtn1Text("取消");
                d.setBtn2Text("呼叫");
                d.setTitleVisible(false, "");
                d.setTextIvestVisible(false);
                d.show();
                d.setOnBtn2ClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent var7;
                        var7 = new Intent("android.intent.action.CALL", Uri.parse("tel:" + "0755-25625428"));
                        startActivity(var7);
                    }
                });
                break;
            default:break;
        }
    }

}
