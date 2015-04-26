package com.mtu.foundation.util;

import java.util.Calendar;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CommonUtil {
	private static Calendar calendar = Calendar.getInstance();

	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}

	public static String getNowYear() {
		return String.valueOf(calendar.get(Calendar.YEAR));
	}

	public static boolean isNetWorkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	private static java.text.DecimalFormat decimalFormat = new java.text.DecimalFormat(
			"0.00");

	public static String decialStrFormat(String doubleStr) {
		try {
			double temp = Double.valueOf(doubleStr);
			return decimalFormat.format(temp);
		} catch (Exception e) {
			return null;
		}
	}
}
