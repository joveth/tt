package com.mtu.foundation.net.httpjersey;

import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.params.HttpParams;

public class NetworkHandler {

	private static NetworkHandler instance;

	public static synchronized NetworkHandler getInstance() {
		if (instance == null) {
			instance = new NetworkHandler();
		}
		return instance;
	}

	public void get(final String url, final HttpParams queryParams,
			int timeout, final Callback<TransResp> callback) {
		new GetTask(url, queryParams, timeout, callback).execute();
	}

	public void post(final String url, List<NameValuePair> paramspost,
			int timeout, final Callback<TransResp> callback) {
		new PostTask(url, paramspost, timeout, callback).execute();
	}
	public void postForZfb(final String url, Map<String,String> paramsMap,
			int timeout, final Callback<TransResp> callback) {
		new PostForZFBTask(url, paramsMap, timeout, callback).execute();
	}
}