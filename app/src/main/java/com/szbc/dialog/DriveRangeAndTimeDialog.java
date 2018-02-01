package com.szbc.dialog;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.szbc.android.R;

import java.util.Calendar;
import java.util.Date;

public class DriveRangeAndTimeDialog extends BaseDialog {

	private TimePickerView pvCustomTime;
	Context mContext;
	public DriveRangeAndTimeDialog(Context context) {
		super(context);
		mContext = context;
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View v = layoutInflater.inflate(R.layout.driverange_andtime_activity, null, false);
		super.setContentView(v);
		Window window = getWindow();
		WindowManager.LayoutParams attributesParams = window.getAttributes();
		attributesParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		attributesParams.dimAmount = 0.4f;

		initCustomTimePicker();
		EditText et_car_time = (EditText) v.findViewById(R.id.et_car_time);
		et_car_time.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				pvCustomTime.show();
			}
		});
	}


	private void initCustomTimePicker() {
		Calendar selectedDate = Calendar.getInstance();//系统当前时间
		Calendar startDate = Calendar.getInstance();
		startDate.set(2014, 1, 23);
		Calendar endDate = Calendar.getInstance();
		endDate.set(2027, 2, 28);
		//时间选择器 ，自定义布局
		pvCustomTime = new TimePickerView.Builder(mContext, new TimePickerView.OnTimeSelectListener() {
			@Override
			public void onTimeSelect(Date date, View v) {//选中事件回调
//                btn_CustomTime.setText(getTime(date));
			}
		}).setDate(selectedDate)
				.setRangDate(startDate, endDate)
				.setLayoutRes(R.layout.pickerview_custom_time, new CustomListener() {

					@Override
					public void customLayout(View v) {
						final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
						ImageView ivCancel = (ImageView) v.findViewById(R.id.iv_cancel);
						tvSubmit.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								pvCustomTime.returnData();
							}
						});
						ivCancel.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								pvCustomTime.dismiss();
							}
						});
					}
				})
				.setType(TimePickerView.Type.YEAR_MONTH_DAY)
				.isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
				.setDividerColor(Color.RED)
				.build();
	}

}