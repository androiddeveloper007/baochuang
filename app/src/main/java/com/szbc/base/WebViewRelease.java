package com.szbc.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.szbc.android.R;
import com.szbc.front.LoginActivity;
import com.szbc.front.register.RegisterActivity;
import com.szbc.tool.StatusBarUtil;
import com.szbc.tool.WebViewEx;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 公用打开Web地址
 *
 * @author zhuzhipeng
 */
public class WebViewRelease extends Activity {
    @BindView(R.id.iv_navigation_bg)
    ImageView ivNavigationBg;
    @BindView(R.id.webview)
    WebViewEx mWebView;
    @BindView(R.id.view_need_offset)
    LinearLayout viewNeedOffset;
    @BindView(R.id.tv_title)
    TextView title_tv;
    private String url = "";
    //    String mUrl2 = "http://192.168.1.249:8082/";// file:///android_asset/test.html index.html
    String mUrl2 = "http://m.wx.loc/activity/inDraw?vname=eggswap";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.commom_web_release);
        ButterKnife.bind(this);
        initView();
        if (Build.VERSION.SDK_INT < 20) {
            ivNavigationBg.getLayoutParams().height = getResources().getDimensionPixelOffset(R.dimen.dimen_45);
        }
        if (Build.VERSION.SDK_INT != 19 && Build.VERSION.SDK_INT != 20)
            StatusBarUtil.setTranslucentForImageView(this, 0, viewNeedOffset);
    }

    /**
     * 初始化控件
     */
    @SuppressLint("SetJavaScriptEnabled")
    public void initView() {
        Intent intent = getIntent();
        if (intent != null) {
            title_tv.setText(intent.getStringExtra("title"));
            url = intent.getStringExtra("url");
        }

        mWebView.getSettings().setJavaScriptEnabled(true);

        // 设置可以支持缩放
        mWebView.getSettings().setSupportZoom(true);
//        // 设置出现缩放工具
        mWebView.getSettings().setBuiltInZoomControls(true);
//        //设置可在大视野范围内上下左右拖动，并且可以任意比例缩放
        mWebView.getSettings().setUseWideViewPort(true);
//        //设置默认加载的可视范围是大视野范围
        mWebView.getSettings().setLoadWithOverviewMode(true);
//        //自适应屏幕(导致活动页面显示出错了)
//        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        mWebView.addJavascriptInterface(new JSInterface(), "jsInterface");

        //判断是否登录，已登录拼接token
//        SharedPreferencesData sp = new SharedPreferencesData(this);
//        if(sp.getBoolean("hasLogin") && !TextUtils.isEmpty(sp.getValue("token"))) {
//            if(!url.contains("?"))
//                url += "?token=" + sp.getValue("token");
//            else
//                url += "&token=" + sp.getValue("token");
//        }

        mWebView.loadUrl(url);// mUrl2

        if (Config.isDebug) Log.e("WXCF", url);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
//	         view.loadUrl(url);
                return false;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                if (error.getPrimaryError() == SslError.SSL_DATE_INVALID
                        || error.getPrimaryError() == SslError.SSL_EXPIRED
                        || error.getPrimaryError() == SslError.SSL_INVALID
                        || error.getPrimaryError() == SslError.SSL_UNTRUSTED) {
                    handler.proceed();//接受证书
                } else {
                    handler.cancel();//默认的处理方式，WebView变成空白页
                }
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK && !mWebView.canGoBack()) {
            setResult(RESULT_OK);
            WebViewRelease.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick(R.id.title_back)
    public void onClick() {
        setResult(RESULT_OK);
        finish();
    }

    class JSInterface {
        @JavascriptInterface
        public void login() {
            Intent intent = new Intent(WebViewRelease.this, LoginActivity.class);
            WebViewRelease.this.startActivity(intent);
            WebViewRelease.this.finish();
        }

        @JavascriptInterface
        public void register() {
            Intent intent = new Intent(WebViewRelease.this, RegisterActivity.class);
            WebViewRelease.this.startActivity(intent);
            WebViewRelease.this.finish();
        }

        @JavascriptInterface
        public void finishWebViewActivity() {
            WebViewRelease.this.finish();
        }

        @JavascriptInterface
        public void gotoProduct() {
            Intent intent = new Intent(WebViewRelease.this, MainActivity.class);
            intent.putExtra("product", "");
            WebViewRelease.this.startActivity(intent);
            WebViewRelease.this.finish();
        }
    }
}
