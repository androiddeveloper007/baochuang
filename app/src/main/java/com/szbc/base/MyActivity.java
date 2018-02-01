package com.szbc.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class MyActivity extends BaseActivity {

    public int SMS_TIME = 91;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }


    private Handler handler = new Handler();
    private static int count = 90;

    private Runnable myRunnable = new Runnable() {
        public void run() {

            handler.postDelayed(this, 1000);
            count--;
            if (count == 0) {
                count = SMS_TIME;
                stopSendButton();
            }
            reflushSendButton(count);

        }
    };

    public void stopSendButton() {
        handler.removeCallbacks(myRunnable);
        count = SMS_TIME;
    }

    public void reflushSendButton(int count) {

    }

    @Override
    protected void onDestroy() {
        stopSendButton();
        super.onDestroy();
    }

    /**
     * 隐藏软键盘
     *
     * @param view
     */
    @SuppressLint("ClickableViewAccessibility")
    public void hideSoftInput(View view) {
        if (!(view instanceof EditText)) {

            view.setOnTouchListener(new OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard();
                    return false;
                }

            });
        }

        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {

                View innerView = viewGroup.getChildAt(i);

                hideSoftInput(innerView);
            }
        }
    }

    public void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    /**
     * 弹出软键盘
     */
    public void showSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 弹出软键盘
     */
    public void showSoftKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, 0);
    }

    public void showLogs(String tag, String str) {
        Log.e(tag, str);
    }

    public void showLog(String str) {
        Log.e("WXCF", str);
    }
}
