package com.meijialife.simi.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;

import com.meijialife.simi.R;

/**
 * Created by Xu wenQiang on 14-5-20.
 */
public class AlertWindow {

	/**
	 * 获取AlertDialog
	 *
	 * @param context
	 *            上下文
	 * @param title
	 *            标题
	 * @param message
	 *            消息
	 * @return
	 */
	public static AlertDialog.Builder getAlertDialog(Context context,
			String title, String message) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		if (!StringUtils.isEmpty(title)) {
			builder.setTitle(title);
		}
		builder.setMessage(message);
		builder.setCancelable(false);
		builder.setInverseBackgroundForced(false);
		return builder;
	}

	/**
	 * 弹窗提示
	 *
	 * @param context
	 * @param title
	 *            弹窗标题
	 * @param message
	 *            弹窗消息
	 * @param onClickListener
	 *            点击确定后的回调
	 */
	public static void dialog(Context context, String title, String message,
			OnClickListener onClickListener) {
		AlertDialog.Builder builder = getAlertDialog(context, title, message);
		builder.setPositiveButton("确定", onClickListener).show();
	}

	/**
	 * 弹窗提示
	 *
	 * @param context
	 * @param title
	 *            弹窗标题
	 * @param message
	 *            弹窗消息
	 * @param positiveButtonText
	 *            确定按钮文字
	 * @param onClickListener
	 *            点击确定后的回调
	 */
	public static void dialog(Context context, String title, String message,
			String positiveButtonTextString, OnClickListener onClickListener) {
		AlertDialog.Builder builder = getAlertDialog(context, title, message);
		builder.setPositiveButton(positiveButtonTextString, onClickListener)
				.show();
	}

	/**
	 * 弹窗提示
	 *
	 * @param context
	 * @param title
	 *            弹窗标题
	 * @param message
	 *            弹窗消息
	 * @param onClickListenerOk
	 *            点击确定后的回调
	 * @param onClickListenerCancel
	 */
	public static void dialog(Context context, String title, String message,
			OnClickListener onClickListenerOk,
			OnClickListener onClickListenerCancel) {
		AlertDialog.Builder builder = getAlertDialog(context, title, message);
		builder.setPositiveButton("确定", onClickListenerOk);
		builder.setNegativeButton("取消", onClickListenerCancel);
		builder.show();
	}

	/**
	 * 弹窗提示
	 *
	 * @param context
	 * @param title
	 *            弹窗标题
	 * @param message
	 *            弹窗消息
	 * @param positiveButtonText
	 *            确定按钮文字
	 * @param negativeButtonText
	 *            取消那妞文字
	 * @param onClickListenerOk
	 *            点击确定后的回调
	 * @param onClickListenerCancel
	 */
	public static void dialog(Context context, String title, String message,
			String positiveButtonText, String negativeButtonTextString,
			OnClickListener onClickListenerOk,
			OnClickListener onClickListenerCancel) {
		AlertDialog.Builder builder = getAlertDialog(context, title, message);
		builder.setPositiveButton(positiveButtonText, onClickListenerOk);
		builder.setNegativeButton(negativeButtonTextString,
				onClickListenerCancel);
		builder.show();
	}

	/**
	 * 格式验证错误弹窗提示
	 *
	 * @param context
	 * @param message
	 *            提示信息
	 * @param onClickListener
	 *            点击确定后的回调
	 */
	public static void formatCheckErrorDialog(Context context, String message,
			OnClickListener onClickListener) {
		dialog(context,
				context.getResources().getString(
						R.string.format_check_result_error_title), message,
				onClickListener);
	}

	/**
	 * 未填写弹窗提示
	 *
	 * @param context
	 * @param message
	 *            提示信息
	 * @param onClickListener
	 *            点击确定后的回调
	 */
	public static void isNullDialog(Context context, String message,
			OnClickListener onClickListener) {
		dialog(context,
				context.getResources().getString(
						R.string.is_null_check_result_title), message,
				onClickListener);
	}

	/**
	 * HTTP请求出现异常提示
	 *
	 * @param context
	 * @param message
	 *            提示信息
	 * @param onClickListener
	 *            点击确定后的回调
	 */
	public static void httpRequestError(Context context, String message,
			OnClickListener onClickListener) {
		dialog(context,
				context.getResources().getString(
						R.string.http_request_error_title), message,
				onClickListener);
	}

	/**
	 * HTTP请求结果为NULL
	 *
	 * @param context
	 * @param message
	 *            提示信息
	 * @param onClickListener
	 *            点击确定后的回调
	 */
	public static void httpRequestNullError(Context context, String message,
			OnClickListener onClickListener) {
		dialog(context,
				context.getResources().getString(
						R.string.http_request_error_title), message,
				onClickListener);
	}

	/**
	 * HTTP请求出现异常提示
	 *
	 * @param context
	 * @param message
	 *            提示信息
	 * @param onClickListener
	 *            点击确定后的回调
	 */
	public static void httpRequestNullError(Context context,
			OnClickListener onClickListener) {
		dialog(context,
				context.getResources().getString(
						R.string.http_request_error_title),
				context.getResources().getString(
						R.string.http_request_fail_error), onClickListener);
	}

	/**
	 * 登录出错提示
	 *
	 * @param context
	 * @param message
	 *            提示信息
	 * @param onClickListener
	 *            点击确定后的回调
	 */
	public static void loginError(Context context, String message,
			OnClickListener onClickListener) {
		dialog(context,
				context.getResources().getString(R.string.login_fail_title),
				message, onClickListener);
	}

	/**
	 * 未选择错误提示
	 *
	 * @param context
	 * @param message
	 *            提示信息
	 * @param onClickListener
	 *            点击确定后的回调
	 */
	public static void uncheckedError(Context context, String message,
			OnClickListener onClickListener) {
		dialog(context,
				context.getResources()
						.getString(R.string.unchecked_error_title), message,
				onClickListener);
	}

}
