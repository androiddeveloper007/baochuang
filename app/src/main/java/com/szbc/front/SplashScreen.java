package com.szbc.front;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

import com.szbc.base.Config;
import com.szbc.base.MainActivity;

/*
* 启动页
* */
public class SplashScreen extends Activity {

    private static final int GO_HOME = 1000;
    private static final int GO_GUIDE = 1001;
    private static final int DENY_KEY_BACK = 1002;

    private boolean isBackKeyEnable = false;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_HOME:
                    goHome();
                    break;
                case GO_GUIDE:
                    goGuide();
                    break;
                case DENY_KEY_BACK:
                    isBackKeyEnable = true;
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        if (!this.isTaskRoot()) {
            Intent mainIntent = getIntent();
            String action = mainIntent.getAction();
            if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }

//		setContentView(R.layout.splash);

        load();

        mHandler.sendEmptyMessageDelayed(DENY_KEY_BACK, 3000);
    }

    private void load() {
        mHandler.sendEmptyMessageDelayed(GO_HOME, Config.SPLASH_DELAY_MILLIS);
    }

    private void goHome() {
        Intent intent = new Intent();
        intent.setClass(SplashScreen.this, MainActivity.class);
//        intent.setClass(SplashScreen.this, MyPaymentOrderActivity.class);
        startActivity(intent);
        finish();
    }

    private void goGuide() {
        Intent intent = new Intent(SplashScreen.this, GuideActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (!isBackKeyEnable) {
                return false;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}