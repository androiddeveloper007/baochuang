package com.szbc.front;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.szbc.adapter.ViewPagerAdapter;
import com.szbc.android.R;
import com.szbc.tool.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

/*
* 版本引导页
* */
public class GuideActivity extends FragmentActivity implements OnPageChangeListener {

	private ViewPager vp;
	private ViewPagerAdapter vpAdapter;
	private List<View> views;

	// 底部小点图片
	private ImageView[] dots;

	// 记录当前选中位置
	private int currentIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if(Build.VERSION.SDK_INT > 22)
			StatusBarUtil.setColor(this, getResources().getColor(R.color.window_background), 0);
		setContentView(R.layout.guide);

		// 初始化页面
		initViews();

		// 初始化底部小点
//		initDots();
	}

	private void initViews() {
		LayoutInflater inflater = LayoutInflater.from(this);

		views = new ArrayList<View>();
		// 初始化引导图片列表
//		views.add(inflater.inflate(R.layout.what_new_one, null));
//		views.add(inflater.inflate(R.layout.what_new_two, null));
//		views.add(inflater.inflate(R.layout.what_new_three, null));
//		views.add(inflater.inflate(R.layout.what_new_four, null));
		views.add(inflater.inflate(R.layout.what_new_gesture, null));

		// 初始化Adapter
		vpAdapter = new ViewPagerAdapter(views, this);
		
		vp = (ViewPager) findViewById(R.id.viewpager);
		vp.setAdapter(vpAdapter);
		// 绑定回调
		vp.addOnPageChangeListener(this);
	}

	private void initDots() {
		LinearLayout ll = (LinearLayout) findViewById(R.id.ll);

		dots = new ImageView[views.size()];

		// 循环取得小点图片
		for (int i = 0; i < views.size(); i++) {
			dots[i] = (ImageView) ll.getChildAt(i);
			dots[i].setEnabled(false);// 都设为灰色
		}

		currentIndex = 0;
		dots[currentIndex].setEnabled(true);// 设置为白色，即选中状态
	}

	private void setCurrentDot(int position) {
		if (position < 0 || position > views.size() - 1
				|| currentIndex == position) {
			return;
		}

		dots[position].setEnabled(true);
		dots[currentIndex].setEnabled(false);

		currentIndex = position;
	}

	// 当滑动状态改变时调用
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	// 当当前页面被滑动时调用
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	// 当新的页面被选中时调用
	@Override
	public void onPageSelected(int arg0) {
		// 设置底部小点选中状态
		setCurrentDot(arg0);
	}

}
