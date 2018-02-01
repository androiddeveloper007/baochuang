package com.szbc.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;

import com.szbc.android.R;

public class BaseDialog extends Dialog {
	
	@SuppressLint("InlinedApi")
	public BaseDialog(Context context) {
		super(context);
		getContext().setTheme(R.style.CustomDialogStyle);//Theme_Holo_InputMethod
	}

}