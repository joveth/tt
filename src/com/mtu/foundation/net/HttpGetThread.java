package com.mtu.foundation.net;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.mtu.foundation.util.FileUtiles;

/**
 * 网络Get请求的线程
 * */
public class HttpGetThread implements Runnable {

	private String url;

	public HttpGetThread(String res) {
		url = res;
	}

	@Override
	public void run() {
		try {
			if (FileUtiles.isHasImage(url)) {
				return;
			}
			DownLoadImage.downImg(url);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
