package com.szbc.tool.payment.alipay;

import android.app.Activity;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alipay.sdk.app.PayTask;

import java.net.URLEncoder;

/**
 * @description 调用支付
 * @author LiangZiChao
 * @Date 2014-9-2上午11:37:24
 * @Package com.xiaobai.xbtrip.payment
 */
public class Payment {

	private static final int SDK_PAY_FLAG = 1;

	/** 支付成功 */
	public static final String SUCCESS = "9999";

	public static final int ALIPAY = 1100;

	/**
	 * 唤起支付宝支付
	 * 
	 * @param thisActivity
	 * @param alipayInfo
	 * @param onPayListener
	 *            回调函数
	 */
	public static void startAliPay(final Activity thisActivity, String alipayInfo, final OnPayListener onPayListener) {
		if (TextUtils.isEmpty(alipayInfo) && onPayListener != null) {
			onPayListener.onFail(null, AliPayResult.getResultMessage("4001"));
			return;
		}

		final String payInfo = alipayInfo;

		final MyHandler mHandler = new MyHandler(thisActivity) {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case SDK_PAY_FLAG:
					try {
						AliPayResult payResult = new AliPayResult((String) msg.obj);

						// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
						// String resultInfo = payResult.getResult();

						String resultStatus = payResult.getResultStatus();

						if (onPayListener != null) {
							// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
							if (TextUtils.equals(resultStatus, AliPayResult.SUCCESS)) {
								onPayListener.onSuccess(resultStatus, payResult.getResultMessage());
							} else {
								// 判断resultStatus 为非“9000”则代表可能支付失败
								// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
								if (TextUtils.equals(resultStatus, AliPayResult.WAIT_CONFIRM)) {
									onPayListener.onSuccess(resultStatus, payResult.getResultMessage());
								} else {
									// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
									onPayListener.onFail(null, TextUtils.isEmpty(payResult.getResult()) ? payResult.getResultMessage() : payResult.getResult());
								}
							}
						}
					} catch (Exception e) {
						if (onPayListener != null)
							onPayListener.onFail(e,"没有支付成功哦，再试一次吧");
					}
					break;
				}
			};
		};

		ThreadPoolUtils.execute(new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(thisActivity);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		});
	}

	/**
	 * 获取支付宝支付参数
	 * 
	 * @param seller
	 *            卖家账号
	 * @param partner
	 *            合作者身份ID
	 * @param tradeNo
	 *            订单号
	 * @param subject
	 *            商品名称
	 * @param body
	 *            商品描述
	 * @param notifyUrl
	 *            通知URL
	 * @param price
	 *            价格
	 * @param rsaPrivate
	 *            私钥
	 * @return
	 */
	public static String getAlipayInfo(String seller, String partner, String tradeNo, String subject, String body, String notifyUrl, String price, String rsaPrivate) {
		String mAlipayInfo = null;
		try {
			String orderInfo = getOrderInfo(seller, partner, tradeNo, subject, body, notifyUrl, price);
			if (!TextUtils.isEmpty(rsaPrivate)) {
				// 对订单做RSA 签名
				String sign = sign(orderInfo, rsaPrivate);
				if (!TextUtils.isEmpty(sign)) {
					// 仅需对sign 做URL编码
					sign = URLEncoder.encode(sign, "UTF-8");
					// 完整的符合支付宝参数规范的订单信息
					mAlipayInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();
				}else{
					Log.e("zp","sign fail !");
				}
			} else {
				Log.e("zp","rsaPrivate is empty \n orderInfo:" + orderInfo);
			}
		} catch (Exception e) {
			Log.e(e.getMessage(), e.toString());
		}
		return mAlipayInfo;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public static String sign(String content, String rsaPrivate) {
		return SignUtils.sign(content, rsaPrivate);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public static String getSignType() {
		return "sign_type=\"RSA\"";
	}

	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	public static String getOrderInfo(String seller, String partner, String tradeNo, String subject, String body, String notifyUrl, String price) {

		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + partner + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + seller + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + tradeNo + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + notifyUrl + "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		// orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		// orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}
}
