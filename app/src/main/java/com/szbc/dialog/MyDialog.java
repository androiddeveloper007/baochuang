package com.szbc.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.szbc.android.R;

public class MyDialog extends Dialog {
	
	
	public enum Result
	{
		SUCCESS(0),FAIL(1);
		private int value;
		Result(int value)
		{
			this.value = value;
		}
		
	}

	private int mDuration=1000;//定时2S
	
	private MyCallback mCallback;
	private Context mContext;
	private final Handler mHandler = new Handler();  
	private String mText;
	private int mImageType = -1;

	public MyDialog(Context context,String text) {
		super(context, R.style.progress_dialog);
		
		mContext = context;
		mText = text;
	}
	
	public MyDialog(Context context,String text,int duration ) {
		super(context, R.style.progress_dialog);
		
		mContext = context;
		mText = text;
		mDuration = duration;
	}
	
	
	public MyDialog(Context context,String text,Result result) {
		super(context, R.style.progress_dialog);
		
		mContext = context;
		mText = text;
		mImageType = result.value;
	}

	public MyDialog(Context context,String text,Result result, int duration) {
		super(context, R.style.progress_dialog);
		
		mContext = context;
		mText = text;
		mImageType = result.value;
		this.mDuration = duration;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    
		View view = null;
		TextView textView = null;
		
		if(mImageType>-1)
		{
			 view = LayoutInflater.from(mContext).inflate(R.layout.dialog,
				     null);
			 textView= (TextView)view.findViewById(R.id.id_tv_loadingmsg);
//			 ImageView image = (ImageView)view.findViewById(R.id.dialog_alert_image_image);
//			 if(mImageType == 0)
//			 {
//				 image.setImageResource(R.drawable.alert_success);
//
//			 }else if(mImageType == 1)
//			 {
//				 image.setImageResource(R.drawable.alert_fail);
//			 }
		}else
		{
			view = LayoutInflater.from(mContext).inflate(R.layout.dialog,
				     null);
			 textView = (TextView)view.findViewById(R.id.id_tv_loadingmsg);
		}
	
		textView.setText(mText);
	    
		this.setCanceledOnTouchOutside(false);//对话框以外地方触摸事件不起作用
		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.gravity = Gravity.CENTER;
		window.setAttributes(params);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		
		window.setContentView(view);
	}
	
	
	public void showDialog()
	{
		this.show();
		if(mImageType>-1)
		{
	        if(mDuration>0)
	        {
	        	 mHandler.postDelayed(hideRunnable, mDuration);
	        }
		}else
		{
			mHandler.postDelayed(doingRunnable, mDuration);
		}
	}
	
	public void setDuration(int duration){
		mDuration = duration;
	}
	
	 private final Runnable hideRunnable = new Runnable() {
	        public void run() {
	            hideDialog();
	        }
	 };
	 public void hideDialog()
	{
		if(mCallback!=null)
        {
        	mCallback.callback();
        }
		this.dismiss();
	}
	 
	 private final Runnable doingRunnable = new Runnable()
	 {
		 public void run()
		 {
			 if(mCallback!=null)
		        {
		        	mCallback.doing();
		        }
		 }
	 };
	
	
	 public void setMyCallback(MyCallback callback) {  
	        this.mCallback = callback;  
	  }
	
	
}
