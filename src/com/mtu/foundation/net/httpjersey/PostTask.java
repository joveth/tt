package com.mtu.foundation.net.httpjersey;

/**
 * Created by linqing.he on 2015/1/9.
 */

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;

public class PostTask extends AsyncTask<String, String, TransResp> {

	private final String url;
	private int timeout;
	private final Callback<TransResp> callback;
	private List<NameValuePair> paramspost;

	PostTask(String url, List<NameValuePair> paramspost, int timeout,
			Callback<TransResp> callback) {
		this.url = url;
		this.timeout = timeout;
		this.callback = callback;
		this.paramspost = paramspost;
	}

	@Override
	protected TransResp doInBackground(String... params) {
		TransResp resp = new TransResp();
		HttpPost post = new HttpPost(url);
		HttpResponse httpResponse;
		try {
			if (paramspost != null) {
				post.setEntity((new UrlEncodedFormEntity(paramspost, HTTP.UTF_8)));
			}
			
			Log.d("request.url", url);
			post.setHeader("Content-Type", "application/x-www-form-urlencoded; text/html; charset=utf-8");	
			//post.setHeader("User-Agent", "Mozilla/4.0");
			HttpClient httpClient = HttpUtil.getHttpsClient(url, timeout);
			httpResponse = httpClient.execute(post);

			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				resp.setRetcode(httpResponse.getStatusLine().getStatusCode());
				HttpEntity obj = httpResponse.getEntity();
				Header contentEncoding = httpResponse
						.getFirstHeader("Content-Encoding");
				String retjson = "";
				if (contentEncoding != null
						&& contentEncoding.getValue().equalsIgnoreCase("gzip")) {
					Log.d("gizp", contentEncoding.getValue());
					InputStream is = obj.getContent();
					is = new GZIPInputStream(new BufferedInputStream(is));
					InputStreamReader reader = new InputStreamReader(is,
							"utf-8");
					char[] data = new char[100];
					int readSize;
					StringBuffer sb = new StringBuffer();
					while ((readSize = reader.read(data)) > 0) {
						sb.append(data, 0, readSize);
					}
					retjson = sb.toString();
					reader.close();
					is.close();
				} else {
					retjson = EntityUtils.toString(obj,"utf-8");
				}
				Log.d("retpost", retjson + "");
				resp.setRetjson(retjson);
			} else {
				resp.setRetcode(httpResponse.getStatusLine().getStatusCode());
				if (httpResponse.getEntity() != null) {
					String retmsg = EntityUtils.toString(httpResponse
							.getEntity());
					resp.setRetmsg(retmsg);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}

	@Override
	protected void onPostExecute(TransResp result) {
		callback.callback(result);
		super.onPostExecute(result);
	}
}