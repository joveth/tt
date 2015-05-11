package com.mtu.foundation.net;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.os.Handler;
import android.os.Message;

import com.mtu.foundation.util.Constants;
import com.mtu.foundation.util.FileUtiles;

/**
 * 网络Get请求的线程
 * */
public class HttpGetThread implements Runnable {

	private String url;
	private Handler ret;

	public HttpGetThread(String res, Handler... handler) {
		url = res;
		if (handler != null && handler.length > 0) {
			ret = handler[0];
		}
	}

	@Override
	public void run() {
		try {

			if (FileUtiles.isHasImage(url)) {
				if (ret != null) {
					Message msg = ret.obtainMessage();
					msg.what = Constants.RESULT_OK;
					ret.sendMessage(msg);
				}
				return;
			}
			DownLoadImage.downImg(url);
			if (ret != null) {
				Message msg = ret.obtainMessage();
				msg.what = Constants.RESULT_OK;
				ret.sendMessage(msg);
			}
			return;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (ret != null) {
			Message msg = ret.obtainMessage();
			msg.what = Constants.RESULT_FAILED;
			ret.sendMessage(msg);
		}
	}
}
