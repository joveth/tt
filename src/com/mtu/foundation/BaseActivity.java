package com.mtu.foundation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.mtu.foundation.db.DBHelper;
import com.mtu.foundation.net.httpjersey.NetworkHandler;
import com.mtu.foundation.util.ExitAppUtil;
import com.mtu.foundation.view.CustomProgressDialog;

public abstract class BaseActivity extends Activity implements OnClickListener {
	protected TextView title, rightBtn;
	protected View leftBtn;
	protected CustomProgressDialog progressDialog;
	protected NetworkHandler networkHandler;
	protected DBHelper dbHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitAppUtil.add(this);
	}

	protected void switchTo(Class clazz) {
		Intent intent = new Intent(this, clazz);
		startActivity(intent);
	}

	protected void switchTo(Class clazz, Bundle bundle) {
		Intent intent = new Intent(this, clazz);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	protected void hiddenInput() {
		View view = getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View v = getCurrentFocus();
			if (isShouldHideInput(v, ev)) {
				hiddenInput();
			}
			return super.dispatchTouchEvent(ev);
		}
		if (getWindow().superDispatchTouchEvent(ev)) {
			return true;
		}
		return onTouchEvent(ev);
	}

	public boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] leftTop = { 0, 0 };
			// 获取输入框当前的location位置
			v.getLocationInWindow(leftTop);
			int left = leftTop[0];
			int top = leftTop[1];
			int bottom = top + v.getHeight();
			int right = left + v.getWidth();
			if (event.getX() > left && event.getX() < right
					&& event.getY() > top && event.getY() < bottom) {
				// 点击的是输入框区域，保留点击EditText的事件
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	protected void showSimpleMessageDialog(String msg) {
		if (isFinishing()) {
			return;
		}
		new AlertDialog.Builder(this).setTitle(null)
				.setNegativeButton("确定", null).setMessage(msg).show();
	}

	protected void showMsgDialogWithCallback(String msg) {
		new AlertDialog.Builder(this).setTitle(null)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
						finish();
					}
				}).setCancelable(false).setMessage(msg).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ExitAppUtil.remove(this);
	}
}
