package com.szbc;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.szbc.base.CrashLogHandlerUtils;

import org.xutils.x;

public class App extends Application {

    public static App app;

    public static App getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        app = this;

        x.Ext.init(this);
        x.Ext.setDebug(true); // 是否输出debug日志, 开启debug会影响性能.

        CrashLogHandlerUtils.getInstance(this).setSavePath(Environment.getExternalStorageDirectory()).start();
        Log.d("保存路径", getFilesDir().toString());
    }


}
