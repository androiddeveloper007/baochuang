package com.szbc.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.ContactsContract.Contacts;
import android.provider.Settings;
import android.text.Editable;
import android.text.NoCopySpan;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.szbc.android.R;
import com.szbc.tool.PxUtil;

import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 工具箱
 * 
 * @author LiangZiChao
 * @Data 2015年6月11日
 */
@SuppressLint({ "SimpleDateFormat", "DefaultLocale", "HandlerLeak" })
public class UITools {

	private static LoadingDialog mProgressDialog;
	@SuppressWarnings("rawtypes")
	private static Dialog mDialog;

	private static Toast mToast;

	private static Notification mUpdateNotification = null;
	private static NotificationManager mUpdateNotificationManager = null;

	private static boolean isBtnAutoClose = true; // 是否点击按钮自动关闭（支持返回按钮和dismis）

	/**
	 * toast 提示的长度持续时间
	 * 
	 * @param context
	 * @param textResId
	 */
	public static void showToastLongDuration(Context context, int textResId) {
		showToastLongDuration(context, context.getText(textResId));
	}

	/**
	 * toast 提示的长度持续时间
	 * 
	 * @param context
	 */
	public static void showToastLongDuration(Context context, CharSequence text) {
		showToast(context, text, Toast.LENGTH_LONG);
	}

	/**
	 * toast 提示的短的持续时间
	 * 
	 * @param context
	 * @param textResId
	 */
	public static void showToastShortDuration(Context context, int textResId) {
		showToast(context, context.getText(textResId), Toast.LENGTH_SHORT);
	}

	/**
	 * toast 提示的短的持续时间
	 * 
	 * @param context
	 */
	public static void showToastShortDuration(Context context, CharSequence text) {
		showToast(context, text, Toast.LENGTH_SHORT);
	}

	/**
	 * toast 提示
	 * 
	 * @param context
	 */
	public static void showToastDuration(Context context, CharSequence text, int duration) {
		showToast(context, text, duration);
	}

	/**
	 * 
	 * @param ctx
	 * @param msg
	 * @param duration
	 */
	public static void showToast(Context ctx, int msg, int duration) {
		showToast(ctx, ctx.getText(msg), duration);
	}

	/**
	 * 
	 * @param ctx
	 * @param duration
	 */
	public static void showToast(Context ctx, View view, int duration) {
		if (mToast == null) {
			mToast = new Toast(ctx);
		}
		mToast.setDuration(duration);
		mToast.setView(view);
		mToast.show();
	}

	/**
	 * 
	 * @param ctx
	 * @param msg
	 * @param duration
	 */
	public static void showToast(final Context ctx, final CharSequence msg, final int duration) {
		if (Looper.getMainLooper() == Looper.myLooper()) {
			showOrCreateToast(ctx, msg, duration);
		} else
			((Activity) ctx).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					showOrCreateToast(ctx, msg, duration);
				}
			});
	}

	/**
	 * 显示Toast
	 * 
	 * @param ctx
	 * @param msg
	 * @param duration
	 */
	private static void showOrCreateToast(Context ctx, CharSequence msg, int duration) {
		if (mToast == null)
			mToast = Toast.makeText(ctx, msg, duration);
		else {
			View mView = mToast.getView();
			if (mView == null) {
				mToast = Toast.makeText(ctx, msg, duration);
			} else {
				View mTextView = mView.findViewById(android.R.id.message);
				if (mTextView == null) {
					mToast = Toast.makeText(ctx, msg, duration);
				} else {
					mToast.setText(msg);
					mToast.setDuration(duration);
				}
			}
		}
		mToast.show();
	}



	/**
	 * 获取通知管理器
	 * 
	 * @param context
	 * @return
	 */
	public static NotificationManager getNotificationManager(Context context) {
		if (mUpdateNotificationManager == null)
			mUpdateNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		return mUpdateNotificationManager;
	}

	/**
	 * 根据ID，清楚通知
	 * 
	 * @param notifyId
	 */
	public static void cancelNotification(int notifyId) {
		try {
			if (mUpdateNotificationManager != null)
				mUpdateNotificationManager.cancel(notifyId);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 根据ID，清楚通知
	 * 
	 */
	public static void cancelAllNotification() {
		try {
			if (mUpdateNotificationManager != null)
				mUpdateNotificationManager.cancelAll();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 清楚Toast
	 */
	public static void cancelToast() {
		if (mToast != null) {
			mToast.cancel();
		}
	}

	/**
	 * 展示加载loading
	 * 
	 * @param context
	 */
	public static LoadingDialog showDialogLoading(Context context) {
		return showDialogLoading(context, null, true);
	}

	/**
	 * 展示加载loading (默认文本：正在加载中...)
	 * 
	 * @param context
	 */
	public static LoadingDialog showDialogLoadingDefault(Context context) {
		return showDialogLoading(context, context.getString(R.string.action_settings), true);
	}

	/**
	 * 展示加载loading
	 * 
	 * @param context
	 * @param msg
	 */
	public static LoadingDialog showDialogLoading(Context context, String msg) {
		return showDialogLoading(context, msg, true);
	}

	/**
	 * 展示加载loading
	 * 
	 * @param context
	 * @param msg
	 */
	public static LoadingDialog showDialogLoading(Context context, int msg) {
		return showDialogLoading(context, context.getString(msg), true);
	}

	/**
	 * 展示加载loading
	 * 
	 * @param context
	 * @param msg
	 */
	public static LoadingDialog createDialogLoading(Context context, String msg, boolean cancelDismiss) {
		dismissLoading();
		mProgressDialog = new LoadingDialog(context, msg);
		mProgressDialog.setCancelable(cancelDismiss);
		mProgressDialog.setCanceledOnTouchOutside(false);
		return mProgressDialog;
	}

	/**
	 * 展示加载loading
	 * 
	 * @param context
	 * @param msg
	 */
	public static LoadingDialog showDialogLoading(Context context, String msg, boolean cancelDismiss) {
		try {
			mProgressDialog = createDialogLoading(context, msg, cancelDismiss);
			mProgressDialog.show();
			return mProgressDialog;
		} catch (Exception e) {
			return null;
		}
	}


	/**
	 * 是否正在加载
	 */
	public static boolean isLoading() {
		if (mProgressDialog != null) {
			return mProgressDialog.isShowing();
		}
		return false;
	}

	/**
	 * 消除加载进度
	 */
	public static void dismissLoading() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
		mProgressDialog = null;
	}


	/**
	 * 创建自定义位置的Dialog
	 * 
	 * @param context
	 * @param layoutResID
	 * @param gravity
	 * @return
	 */
	public static Dialog createCustomDialog(Context context, int layoutResID, int gravity) {
		return createCustomDialog(context, layoutResID, gravity, R.style.animPopupWindow);
	}

	/**
	 * 创建自定义位置的Dialog
	 * 
	 * @param context
	 * @param layoutResID
	 * @param gravity
	 * @return
	 */
	public static Dialog createCustomDialog(Context context, int layoutResID, int gravity, int animationStyle) {
		Dialog customDialog = new Dialog(context, R.style.DialogStyle);
		customDialog.setContentView(layoutResID);
		customDialog.setCanceledOnTouchOutside(true);
		customDialog.setOwnerActivity((Activity) context);
		Window dialogWindow = customDialog.getWindow();
		dialogWindow.setWindowAnimations(animationStyle);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.width = PxUtil.getScreenWidth(context);
		dialogWindow.setAttributes(lp);
		dialogWindow.setGravity(gravity);
		return customDialog;
	}

	/**
	 * 创建自定义位置的Dialog
	 * 
	 * @param context
	 * @param gravity
	 * @return
	 */
	public static Dialog createCustomDialog(Context context, View cententView, int gravity) {
		return createCustomDialog(context, cententView, PxUtil.getScreenWidth(context), 0, gravity);
	}

	/**
	 * 创建自定义位置的Dialog
	 * 
	 * @param context
	 * @param gravity
	 * @return
	 */
	public static Dialog createCustomDialog(Context context, View cententView, int dialogStyle, int gravity) {
		return createCustomDialog(context, cententView, dialogStyle, PxUtil.getScreenWidth(context), 0, gravity);
	}

	/**
	 * 创建自定义位置的Dialog
	 * 
	 * @param context
	 * @param cententView
	 *            centerView
	 * @param gravity
	 * @return
	 */
	public static Dialog createCustomDialog(Context context, View cententView, int width, int height, int gravity) {
		return createCustomDialog(context, cententView, R.style.DialogStyle, width, height, gravity);
	}

	/**
	 * 创建自定义位置的Dialog
	 * 
	 * @param context
	 * @param cententView
	 *            centerView
	 * @param gravity
	 * @return
	 */
	public static Dialog createCustomDialog(Context context, View cententView, int dialogStyle, int width, int height, int gravity) {
		Dialog customDialog = new Dialog(context, dialogStyle);
		customDialog.setContentView(cententView);
		customDialog.setCanceledOnTouchOutside(true);
		customDialog.setOwnerActivity((Activity) context);
		Window dialogWindow = customDialog.getWindow();
		dialogWindow.setWindowAnimations(R.style.animPopupWindow);
		if (width != 0 || height != 0) {
			WindowManager.LayoutParams lp = dialogWindow.getAttributes();
			if (width != 0)
				lp.width = width;
			if (height != 0)
				lp.height = height;
			dialogWindow.setAttributes(lp);
		}
		dialogWindow.setGravity(gravity);
		return customDialog;
	}

	/**
	 * 创建PopupWindow
	 * 
	 * @param centerView
	 * @return
	 */
	public static PopupWindow createCustomPopupWindow(View centerView) {
		return createCustomPopupWindow(centerView, R.style.animPopupWindow);
	}

	/**
	 * 创建PopupWindow
	 * 
	 * @param centerView
	 * @param animationStyle
	 *            动画
	 * @return
	 */
	public static PopupWindow createCustomPopupWindow(View centerView, int animationStyle) {
		return createCustomPopupWindow(centerView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, animationStyle);
	}

	/**
	 * 创建PopupWindow
	 * 
	 * @param centerView
	 * @param width
	 * @param height
	 * @return
	 */
	public static PopupWindow createCustomPopupWindow(View centerView, int width, int height) {
		return createCustomPopupWindow(centerView, width, height, R.style.animPopupWindow);
	}

	/**
	 * 创建PopupWindow
	 * 
	 * @param centerView
	 * @param width
	 * @param height
	 * @param animationStyle
	 *            动画
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static PopupWindow createCustomPopupWindow(View centerView, int width, int height, int animationStyle) {
		PopupWindow popupWindow = new PopupWindow(centerView, width, height);
		popupWindow.setAnimationStyle(animationStyle);
		popupWindow.setFocusable(true);
		// 点击popupWindow之外的地方能让其消失
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		popupWindow.update(); // 更新窗口的状态
		return popupWindow;
	}


	/**
	 * 动态设置listview高度
	 * 
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	/**
	 * 空白编辑框的错误显示
	 * 
	 * @param context
	 * @param editText
	 */
	public static void blankEditTextError(Context context, EditText editText, int stringId) {
		// 内容不正确，请检查你的填写内容
		String error = context.getResources().getString(stringId);
		blankEditTextError(context, editText, error);
	}

	/**
	 * 空白编辑框的错误显示
	 * 
	 * @param context
	 * @param editText
	 */
	public static void blankEditTextError(Context context, EditText editText, String message) {
		if (editText.getVisibility() != View.VISIBLE)
			return;
		// 内容不正确，请检查你的填写内容
		SpannableStringBuilder style = new SpannableStringBuilder(message);
		// 错误提醒信息的颜色
		style.setSpan(new ForegroundColorSpan(Color.RED), 0, message.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		editText.setError(style);
		editText.requestFocus();
	}

	/**
	 * 跳转到另外一个activity
	 * 
	 * @param nextActivity
	 *            下一个Activity
	 *            数据包
	 */
	public static void startToNextActivity(Context context, Class<?> nextActivity) {
		Intent intent = new Intent(context, nextActivity);
		context.startActivity(intent);
	}

	/**
	 * 跳转到另外一个activity
	 * 
	 * @param nextActivity
	 *            下一个Activity
	 *            数据包
	 */
	public static void startToNextActivityForResult(Activity activity, Class<?> nextActivity, int requestCode) {
		Intent intent = new Intent(activity, nextActivity);
		activity.startActivityForResult(intent, requestCode);
	}

	/**
	 * 跳转到另外一个activity
	 * 
	 * @param nextActivity
	 *            下一个Activity
	 *            数据包
	 */
	public static void startToNextActivity(Context context, Class<?> nextActivity, String key, Serializable serializable) {
		Intent intent = new Intent(context, nextActivity);
		intent.putExtra(key, serializable);
		context.startActivity(intent);
	}

	/**
	 * 跳转到另外一个activity
	 * 
	 * @param nextActivity
	 *            下一个Activity
	 *            数据包
	 */
	public static void startToNextActivityForResult(Activity activity, Class<?> nextActivity, String key, Serializable serializable, int requestCode) {
		Intent intent = new Intent(activity, nextActivity);
		intent.putExtra(key, serializable);
		if (requestCode > 0)
			activity.startActivityForResult(intent, requestCode);
		else
			activity.startActivity(intent);
	}

	/**
	 * 跳转到另外一个activity
	 * 
	 * @param nextActivity
	 *            下一个Activity
	 *            数据包
	 */
	public static void startToNextActivity(Context context, Class<?> nextActivity, String[] keys, Serializable[] serializables) {
		Intent intent = new Intent(context, nextActivity);
		int length = keys.length;
		for (int i = 0; i < length; i++) {
			intent.putExtra(keys[i], serializables[i]);
		}
		context.startActivity(intent);
	}

	/**
	 * 跳转到另外一个activity
	 * 
	 * @param nextActivity
	 *            下一个Activity
	 *            数据包
	 */
	public static void startToNextActivityForResult(Activity activity, Class<?> nextActivity, String[] keys, Serializable[] serializables, int requestCode) {
		Intent intent = new Intent(activity, nextActivity);
		int length = keys.length;
		for (int i = 0; i < length; i++) {
			intent.putExtra(keys[i], serializables[i]);
		}
		if (requestCode > 0)
			activity.startActivityForResult(intent, requestCode);
		else
			activity.startActivity(intent);
	}

	/**
	 * 跳转到另外一个activity
	 * 
	 * @param nextActivity
	 *            下一个Activity
	 *            数据包
	 */
	public static void startToNextActivity(Context context, Class<?> nextActivity, String key, Parcelable parcelable) {
		Intent intent = new Intent(context, nextActivity);
		intent.putExtra(key, parcelable);
		context.startActivity(intent);
	}

	/**
	 * 跳转到另外一个activity
	 * 
	 * @param nextActivity
	 *            下一个Activity
	 *            数据包
	 */
	public static void startToNextActivityForResult(Activity activity, Class<?> nextActivity, String key, Parcelable parcelable, int requestCode) {
		Intent intent = new Intent(activity, nextActivity);
		intent.putExtra(key, parcelable);
		if (requestCode > 0)
			activity.startActivityForResult(intent, requestCode);
		else
			activity.startActivity(intent);
	}

	/**
	 * 跳转到另外一个activity
	 * 
	 * @param nextActivity
	 *            下一个Activity
	 *            数据包
	 */
	public static void startToNextActivity(Context context, Class<?> nextActivity, String[] keys, Parcelable[] parcelables) {
		Intent intent = new Intent(context, nextActivity);
		int length = keys.length;
		for (int i = 0; i < length; i++) {
			intent.putExtra(keys[i], parcelables[i]);
		}
		context.startActivity(intent);
	}

	/**
	 * 跳转到另外一个activity
	 * 
	 * @param nextActivity
	 *            下一个Activity
	 *            数据包
	 */
	public static void startToNextActivityForResult(Activity activity, Class<?> nextActivity, String[] keys, Parcelable[] parcelables, int requestCode) {
		Intent intent = new Intent(activity, nextActivity);
		int length = keys.length;
		for (int i = 0; i < length; i++) {
			intent.putExtra(keys[i], parcelables[i]);
		}
		if (requestCode > 0)
			activity.startActivityForResult(intent, requestCode);
		else
			activity.startActivity(intent);
	}

	/**
	 * 拨打电话
	 * 
	 * @param context
	 * @param telPhone
	 * @return
	 */
	public static Intent actionCallPhone(Context context, String telPhone) {
		Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + telPhone));
		context.startActivity(intent);
		return intent;
	}

	/**
	 * 获取当前ListView的Adapter
	 * 
	 * @return
	 */
	public static ListAdapter getListAdapter(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter != null && listAdapter instanceof HeaderViewListAdapter) {
			listAdapter = ((HeaderViewListAdapter) listAdapter).getWrappedAdapter();
		}
		return listAdapter;
	}

	/**
	 * 随机生成颜色
	 * 
	 * @return 随机生成的十六进制颜色
	 */

	public static String RandomColor() {
		return "#" + Integer.toHexString((int) ((Math.random() * 16777216) * -1));
	}

	/**
	 * 随机生成RGB颜色
	 * 
	 * @return
	 */
	public static int[] RandomRgbColor() {
		Random r = new Random();
		int[] rgbs = new int[3];
		for (int i = 0; i < rgbs.length; i++) {
			rgbs[i] = r.nextInt(255);
		}
		return rgbs;
	}

	/**
	 * 随机生成RGB颜色
	 * 
	 * @return
	 */
	public static int RandomRgbColorInt() {
		return RandomRgbInt(256);
	}

	/**
	 * 随机生成带透明度RGB颜色
	 * 
	 * @return
	 */
	public static int RandomRgbColorInt(int alpha) {
		return RandomRgbColorInt(alpha, 256);
	}

	/**
	 * 随机生成RGB颜色
	 * 
	 * @param maxRGB
	 *            最大RGB值
	 * @return
	 */
	public static int RandomRgbInt(int maxRGB) {
		Random mRandom = new Random();
		int[] rgbs = new int[3];
		for (int i = 0; i < rgbs.length; i++) {
			rgbs[i] = mRandom.nextInt(maxRGB);
		}
		return Color.rgb(rgbs[0], rgbs[1], rgbs[2]);
	}

	/**
	 * 随机生成带透明度RGB颜色
	 * 
	 * @param maxRGB
	 *            最大RGB值
	 * @return
	 */
	public static int RandomRgbColorInt(int alpha, int maxRGB) {
		Random mRandom = new Random();
		int[] rgbs = new int[3];
		for (int i = 0; i < rgbs.length; i++) {
			rgbs[i] = mRandom.nextInt(maxRGB);
		}
		return Color.argb(alpha, rgbs[0], rgbs[1], rgbs[2]);
	}

	/*
	 * -----------------------------EditText Input
	 * Begin-----------------------------------
	 */

	static final Object COMPOSING = new NoCopySpan() {
	};

	/**
	 * The default implementation performs the deletion around the current
	 * selection position of the editable text.
	 * 
	 * @param beforeLength
	 *            光标前几位
	 * @param afterLength
	 *            光标后几位
	 */
	public static boolean deleteSurroundingText(EditText mEditText, int beforeLength, int afterLength) {
		final Editable content = mEditText.getText();
		if (content == null)
			return false;

		int a = Selection.getSelectionStart(content);
		int b = Selection.getSelectionEnd(content);

		if (a > b) {
			int tmp = a;
			a = b;
			b = tmp;
		}

		int deleted = 0;

		if (beforeLength > 0) {
			int start = a - beforeLength;
			if (start < 0)
				start = 0;
			content.delete(start, a);
			deleted = a - start;
		}

		if (afterLength > 0) {
			b = b - deleted;

			int end = b + afterLength;
			if (end > content.length())
				end = content.length();

			content.delete(b, end);
		}
		return true;
	}

	/**
	 * 输入文本
	 * 
	 * @param mEditText
	 * @param text
	 * @param newCursorPosition
	 *            为1从当前位置插入文本，
	 */
	public static void inputText(EditText mEditText, CharSequence text, int newCursorPosition) {
		final Editable content = mEditText.getText();
		if (content == null) {
			return;
		}

		// delete composing text set previously.
		int a = getComposingSpanStart(content);
		int b = getComposingSpanEnd(content);

		if (b < a) {
			int tmp = a;
			a = b;
			b = tmp;
		}

		if (a != -1 && b != -1) {
			removeComposingSpans(content);
		} else {
			a = Selection.getSelectionStart(content);
			b = Selection.getSelectionEnd(content);
			if (a < 0)
				a = 0;
			if (b < 0)
				b = 0;
			if (b < a) {
				int tmp = a;
				a = b;
				b = tmp;
			}
		}

		Spannable sp = null;
		if (!(text instanceof Spannable)) {
			sp = new SpannableStringBuilder(text);
			text = sp;
		} else {
			sp = (Spannable) text;
		}
		setComposingSpans(sp);

		// Position the cursor appropriately, so that after replacing the
		// desired range of text it will be located in the correct spot.
		// This allows us to deal with filters performing edits on the text
		// we are providing here.
		if (newCursorPosition > 0) {
			newCursorPosition += b - 1;
		} else {
			newCursorPosition += a;
		}
		if (newCursorPosition < 0)
			newCursorPosition = 0;
		if (newCursorPosition > content.length())
			newCursorPosition = content.length();
		Selection.setSelection(content, newCursorPosition);
		content.replace(newCursorPosition, b, text);
	}

	public static void setComposingSpans(Spannable text) {
		setComposingSpans(text, 0, text.length());
	}

	/** @hide */
	public static void setComposingSpans(Spannable text, int start, int end) {
		final Object[] sps = text.getSpans(start, end, Object.class);
		if (sps != null) {
			for (int i = sps.length - 1; i >= 0; i--) {
				final Object o = sps[i];
				if (o == COMPOSING) {
					text.removeSpan(o);
					continue;
				}

				final int fl = text.getSpanFlags(o);
				if ((fl & (Spanned.SPAN_COMPOSING | Spanned.SPAN_POINT_MARK_MASK)) != (Spanned.SPAN_COMPOSING | Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)) {
					text.setSpan(o, text.getSpanStart(o), text.getSpanEnd(o), (fl & ~Spanned.SPAN_POINT_MARK_MASK) | Spanned.SPAN_COMPOSING | Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
		}

		text.setSpan(COMPOSING, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE | Spanned.SPAN_COMPOSING);
	}

	public static int getComposingSpanStart(Spannable text) {
		return text.getSpanStart(COMPOSING);
	}

	public static int getComposingSpanEnd(Spannable text) {
		return text.getSpanEnd(COMPOSING);
	}

	public static final void removeComposingSpans(Spannable text) {
		text.removeSpan(COMPOSING);
		Object[] sps = text.getSpans(0, text.length(), Object.class);
		if (sps != null) {
			for (int i = sps.length - 1; i >= 0; i--) {
				Object o = sps[i];
				if ((text.getSpanFlags(o) & Spanned.SPAN_COMPOSING) != 0) {
					text.removeSpan(o);
				}
			}
		}
	}

	/*
	 * -----------------------------EditText Input
	 * End-----------------------------------
	 */


	/**
	 * 跳转WIFI
	 * 
	 * @param activity
	 */
	public static void startWifiSetting(Activity activity) {
		activity.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)); // 进入手机中的wifi网络设置界面
	}

	/**
	 * 跳转WIFI
	 * 
	 * @param activity
	 */
	public static void startWifiSetting(Activity activity, int requestCode) {
		activity.startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), requestCode); // 进入手机中的wifi网络设置界面
	}

	/**
	 * 选择联系人
	 * 
	 * @param activity
	 * @param requestCode
	 */
	public static void startContacts(Activity activity, int requestCode) {
		try {
			Intent it = new Intent();
			it.setAction(Intent.ACTION_PICK);
			it.setData(Contacts.CONTENT_URI);
			// it.setType(Contacts.CONTENT_TYPE);
			activity.startActivityForResult(it, requestCode);
		} catch (Exception e) {
		}
	}

	private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

	/**
	 * 获取VIewID
	 * 
	 * @return
	 */
	public static int getViewId() {
		for (;;) {
			final int result = sNextGeneratedId.get();
			// aapt-generated IDs have the high byte nonzero; clamp to the range
			// under that.
			int newValue = result + 1;
			if (newValue > 0x00FFFFFF)
				newValue = 1; // Roll over to 1, not 0.
			if (sNextGeneratedId.compareAndSet(result, newValue)) {
				return result;
			}
		}
	}
}
