package com.szbc.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.szbc.android.R;
import com.szbc.fragment.Index;
import com.szbc.fragment.Mine;
import com.szbc.tool.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.vp)
    ViewPager vp;
    @BindView(R.id.bnve)
    BottomNavigationViewEx bnve;
    @BindView(R.id.activity_with_view_pager)
    CoordinatorLayout activityWithViewPager;

    private List<Fragment> fragments;
    private VpAdapter adapter;
    private long clickTime;
    public static final long TIME_SPACE = 2000;
    private Index index;
    private Mine mine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.main);
        ButterKnife.bind(this);
        if(Build.VERSION.SDK_INT != 19 && Build.VERSION.SDK_INT != 20)
            StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, null);
        initView();
    }

    public void initView() {
        fragments = new ArrayList<>(2);
        index = new Index();
        mine = new Mine();
        fragments.add(index);
        fragments.add(mine);
        adapter = new VpAdapter(getSupportFragmentManager(), fragments);
        vp.setOffscreenPageLimit(1);
        vp.setAdapter(adapter);
        bnve.setupWithViewPager(vp);
        /*vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    default:
                        resetFragmentView(fragments.get(position));
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });*/
        resetFragmentView(fragments.get(0));
        resetFragmentView(fragments.get(1));

        if (getIntent() != null && getIntent().getStringExtra("index") != null) {
            bnve.setCurrentItem(0);
        } else if (getIntent() != null && getIntent().getStringExtra("mine") != null) {
            bnve.setCurrentItem(1);
            mine.initData();
        }
    }

    private static class VpAdapter extends FragmentPagerAdapter {
        private List<Fragment> data;
        public VpAdapter(FragmentManager fm, List<Fragment> data) {
            super(fm);
            this.data = data;
        }
        @Override
        public int getCount() {
            return data.size();
        }
        @Override
        public Fragment getItem(int position) {
            return data.get(position);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if ((System.currentTimeMillis() - clickTime) > TIME_SPACE) {
                    Toast.makeText(getApplicationContext(), "再按一次退出程序",
                            Toast.LENGTH_SHORT).show();
                    clickTime = System.currentTimeMillis();
                    return true;
                }
                finish();
                return true;
            default:break;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void resetFragmentView(Fragment fragment) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View contentView = findViewById(android.R.id.content);
            if (contentView != null) {
                ViewGroup rootView;
                rootView = (ViewGroup) ((ViewGroup) contentView).getChildAt(0);
                if (rootView.getPaddingTop() != 0) {
                    rootView.setPadding(0, 0, 0, 0);
                }
            }
            if (fragment.getView() != null) fragment.getView().setPadding(0, getStatusBarHeight(this), 0, 0);
        }
    }

    private static int getStatusBarHeight(Context context) { // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }
}
