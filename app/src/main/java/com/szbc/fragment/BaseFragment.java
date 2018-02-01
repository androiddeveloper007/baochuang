package com.szbc.fragment;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.szbc.base.Config;
import com.szbc.dialog.MyDialog;
import com.szbc.tool.ToastMakeText;
import com.szbc.widget.ProgressDialog;

import org.xutils.http.RequestParams;

/**
 * Created by ZP on 2017/5/19.
 */

public abstract  class BaseFragment extends Fragment {

    public ProgressDialog d;
    public RequestParams params,params0;
    public MyDialog mDialog;
    protected boolean isVisible;//用于标记视图是否初始化
    public void showToast(String str, int time){
            ToastMakeText.showToast(getActivity(),str,time);
    }
    public void showLog(String str){
        if(Config.isDebug)
            Log.e("zp", "fragment打印的日志:" + str);
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        //在onCreate方法之前调用，用来判断Fragment的UI是否是可见的
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }
    /**
     * 视图可见
     * */
    protected void onVisible(){
        lazyLoad();
    }
    /**
     * 自定义抽象加载数据方法
     * */
    protected abstract void lazyLoad();
    /**
     * 视图不可见
     * */
    protected void onInvisible(){}
}