package com.szbc.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.szbc.android.R;
import com.szbc.tool.ProgressWebView;
import com.szbc.tool.StatusBarUtil;

/**
 * 公用打开Web地址
 * @author zzp
 */
public class Web extends Activity implements OnClickListener {
    private TextView title_tv;
    private ImageView title_back;
    private ProgressWebView _webview;
    private String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT > 22)
            StatusBarUtil.setColor(this, getResources().getColor(R.color.window_background), 0);
        setContentView(R.layout.commom_web);
        initview();
    }

    /**
     * 初始化控件
     */
    @SuppressLint("SetJavaScriptEnabled")
    public void initview() {
        title_tv = (TextView) findViewById(R.id.title_tv);
        title_back = (ImageView) findViewById(R.id.title_back);
        title_back.setVisibility(View.VISIBLE);
        _webview = (ProgressWebView) findViewById(R.id.webview);
        Intent intent = getIntent();
        if (intent != null) {
            title_tv.setText(intent.getStringExtra("title"));
            url = intent.getStringExtra("url");
        }
        _webview.getSettings().setJavaScriptEnabled(true);
        //JS映射
//        _webview.addJavascriptInterface(this, "android");
        _webview.loadUrl(url);
        _webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
//	         view.loadUrl(url);
                return false;
            }
//		@Override
//		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
//			//handler.cancel(); 默认的处理方式，WebView变成空白页
//			handler.proceed();	//接受证书
//		}
        });
        listener();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && _webview.canGoBack()) {
            _webview.goBack();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK && !_webview.canGoBack()) {
            setResult(RESULT_OK);
            Web.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 控件的事件的监听
     */
    public void listener() {
        title_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                setResult(RESULT_OK);
                Web.this.finish();
                break;
            default:
                break;
        }
    }
}
