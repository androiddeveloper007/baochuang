package com.szbc.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.szbc.android.R;
import com.szbc.tool.SysApplication;

public class MyProgressDialog extends AlertDialog implements android.view.View.OnClickListener{

	
	
	
	private ProgressCallback myCallback;
	private ProgressBar mProgress;
	public MyProgressDialog(Context context, int theme) {
	    super(context, theme);
	}

	public MyProgressDialog(Context context) {
	    super(context);
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    
	   setContentView(R.layout.dialog_progress);
	   
	   mProgress = (ProgressBar)findViewById(R.id.dialog_progress1);
	  /*
	   LinearLayout lay_sure = (LinearLayout)findViewById(R.id.dialog_progress_sure);
	   lay_sure.setOnClickListener(this);
	   
	   LinearLayout lay_cancel = (LinearLayout)findViewById(R.id.dialog_progress_cancel);
	   lay_cancel.setOnClickListener(this);
	   
	   */
	   if(myCallback!=null)
		{
			myCallback.callback();
		}
		
	}

	

	@Override
	public void dismiss()
	{
		super.dismiss();
		SysApplication.getInstance().exit();
	}
	
	 public void setProgress(int progress)
	 {
		 mProgress.setProgress(progress);
	 }
	 public void setCallback(ProgressCallback myCallback) {  
	        this.myCallback = myCallback;  
	 }
	 
	 public interface ProgressCallback {

			void callback();
			
			void cancel();
			
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
}
