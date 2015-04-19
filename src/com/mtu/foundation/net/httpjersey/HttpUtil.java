package com.mtu.foundation.net.httpjersey;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class HttpUtil {

	public static HttpClient getHttpsClient(String url, int timeout) {
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeout * 1000);
		HttpConnectionParams.setSoTimeout(httpParameters, timeout * 1000);

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		if (url.startsWith("https")) {
			int port = 443;
			if (url.lastIndexOf(":") > -1) {
				if (url.substring(url.lastIndexOf(":")).contains("/")) {
					port = Integer.parseInt(url.substring(
							url.lastIndexOf(":") + 1)
							.substring(
									0,
									url.substring(url.lastIndexOf(":"))
											.indexOf("/") - 1));
				} else {
					port = Integer
							.parseInt(url.substring(url.lastIndexOf(":") + 1));
				}
			}
			schemeRegistry.register(new Scheme("https",
					new EasySSLSocketFactory(), port));
			ClientConnectionManager connManager = new ThreadSafeClientConnManager(
					httpParameters, schemeRegistry);
			return new DefaultHttpClient(connManager, httpParameters);
		} else {
			return new DefaultHttpClient(httpParameters);
		}

	}
}
