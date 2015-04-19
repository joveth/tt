package com.mtu.foundation.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import com.mtu.foundation.util.FileUtiles;

public class DownLoadImage {
	public static String doGet(String url) throws ClientProtocolException,
			IOException {
		String result = null;
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader(
				"user-agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36");
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setIntParameter(
				HttpConnectionParams.CONNECTION_TIMEOUT, 5 * 1000);
		httpClient.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT,
				30 * 1000);
		HttpResponse httpResponse = httpClient.execute(httpGet);
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			result = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
		}
		return result;
	}

	public static void downImg(String imgurl) throws ClientProtocolException,
			IOException {
		URL url = new URL(imgurl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty(
				"user-agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36");
		conn.setConnectTimeout(15 * 1000);
		int resCode = conn.getResponseCode();
		if (resCode == 200) {
			FileOutputStream fos = null;
			BufferedOutputStream bos = null;
			BufferedInputStream bis = null;
			File imageFile = null;
			try {
				bis = new BufferedInputStream(conn.getInputStream());
				imageFile = new File(FileUtiles.getImagePath(imgurl));
				fos = new FileOutputStream(imageFile);
				bos = new BufferedOutputStream(fos);
				byte[] b = new byte[1024];
				int length;
				while ((length = bis.read(b)) != -1) {
					bos.write(b, 0, length);
					bos.flush();
				}
			} finally {
				try {
					if (bis != null) {
						bis.close();
					}
					if (bos != null) {
						bos.close();
					}
					if (conn != null) {
						conn.disconnect();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
