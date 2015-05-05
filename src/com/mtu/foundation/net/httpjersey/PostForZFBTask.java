package com.mtu.foundation.net.httpjersey;

/**
 * Created by linqing.he on 2015/1/9.
 */

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;

import android.os.AsyncTask;
import android.util.Log;

import com.mtu.foundation.util.alipay.AliPayUtil;
import com.mtu.foundation.util.alipay.AlipayCore;
import com.mtu.foundation.util.alipay.HttpProtocolHandler;
import com.mtu.foundation.util.alipay.HttpRequest;
import com.mtu.foundation.util.alipay.HttpResponse;
import com.mtu.foundation.util.alipay.HttpResultType;
import com.mtu.foundation.util.alipay.RSA;

public class PostForZFBTask extends AsyncTask<String, String, TransResp> {

	private final String url;
	private int timeout;
	private final Callback<TransResp> callback;
	private Map<String, String> paramsMap;

	PostForZFBTask(String url, Map<String, String> paramsMap, int timeout,
			Callback<TransResp> callback) {
		this.url = url;
		this.timeout = timeout;
		this.callback = callback;
		this.paramsMap = paramsMap;
	}

	@Override
	protected TransResp doInBackground(String... params) {
		TransResp resp = new TransResp();
		HttpRequest request = new HttpRequest(HttpResultType.BYTES);
		request.setCharset("UTF-8");
		request.setUrl(url);
		String signStr = AlipayCore.createLinkString(paramsMap);
		
		String sign = RSA.sign(signStr, AliPayUtil.RSA_PRIVATE,"UTF-8");
		paramsMap.put("sign", sign);
		request.setParameters(generatNameValuePair(paramsMap));
		HttpProtocolHandler httpprotocolhandler = HttpProtocolHandler
				.getInstance();
		HttpResponse res = null;
		String token;
		try {
			res = httpprotocolhandler.execute(request, "", "");
			String result = URLDecoder.decode(res.getStringResult(), "utf-8");
			Log.d("result",result);
			token = getRequestToken(result);
			System.out.println(token);
			resp.setRetjson(token);
			return resp;
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}

	private static NameValuePair[] generatNameValuePair(
			Map<String, String> properties) {
		NameValuePair[] nameValuePair = new NameValuePair[properties.size()];
		int i = 0;
		for (Map.Entry<String, String> entry : properties.entrySet()) {
			nameValuePair[i++] = new NameValuePair(entry.getKey(),
					entry.getValue());
		}

		return nameValuePair;
	}
	public static String getRequestToken(String text) throws Exception {
    	String request_token = "";
    	//以“&”字符切割字符串
    	String[] strSplitText = text.split("&");
    	//把切割后的字符串数组变成变量与数值组合的字典数组
    	Map<String, String> paraText = new HashMap<String, String>();
        for (int i = 0; i < strSplitText.length; i++) {

    		//获得第一个=字符的位置
        	int nPos = strSplitText[i].indexOf("=");
        	//获得字符串长度
    		int nLen = strSplitText[i].length();
    		//获得变量名
    		String strKey = strSplitText[i].substring(0, nPos);
    		//获得数值
    		String strValue = strSplitText[i].substring(nPos+1,nLen);
    		//放入MAP类中
    		paraText.put(strKey, strValue);
        }

    	if (paraText.get("res_data") != null) {
    		String res_data = paraText.get("res_data");
    		//解析加密部分字符串（RSA与MD5区别仅此一句）
    			res_data = RSA.decrypt(res_data, AliPayUtil.RSA_PRIVATE, "utf-8");
    		//token从res_data中解析出来（也就是说res_data中已经包含token的内容）
    			Log.d("res_data",res_data);
    		Document document = DocumentHelper.parseText(res_data);
    		request_token = document.selectSingleNode( "//direct_trade_create_res/request_token" ).getText();
    		Log.d("ret_token",request_token);
    	}
    	return request_token;
    }
	@Override
	protected void onPostExecute(TransResp result) {
		callback.callback(result);
		super.onPostExecute(result);
	}
}