/*
 * @Project: xbtrip
 * @File: OnPayListener.java
 * @Date: 2014年11月12日
 * @Copyright: 2014 www.exiaobai.com Inc. All Rights Reserved.
 */
package com.szbc.tool.payment.alipay;

/**
 * 支付监听回调
 * 
 * @description
 * @author LiangZiChao
 * @Date 2014-11-12下午5:36:33
 * @Package com.xiaobai.xbtrip.listener
 */
public abstract class OnPayListener {

	public abstract void onSuccess(String resultCode, String msg);

	public abstract void onFail(Exception e, String errorMsg);
}
