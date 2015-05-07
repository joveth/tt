package com.mtu.foundation.net.httpjersey;

/**
 * Created by linqing.he on 2015/1/9.
 */

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.http.HttpStatus;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import android.os.AsyncTask;

import com.mtu.foundation.util.alipay.AliPayUtil;
import com.mtu.foundation.util.alipay.sign.RSA;
import com.mtu.foundation.util.alipay.util.AlipayCore;
import com.mtu.foundation.util.alipay.util.httpClient.HttpProtocolHandler;
import com.mtu.foundation.util.alipay.util.httpClient.HttpRequest;
import com.mtu.foundation.util.alipay.util.httpClient.HttpResponse;
import com.mtu.foundation.util.alipay.util.httpClient.HttpResultType;

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
		/*HttpRequest request = new HttpRequest(HttpResultType.BYTES);
		request.setCharset("UTF-8");
		request.setUrl(url);
		
		 * String signStr = AlipayCore.createLinkString(paramsMap);
		 * 
		 * String sign = RSA.sign(signStr, AliPayUtil.RSA_PRIVATE,"UTF-8");
		 * paramsMap.put("sign", sign);
		 
		request.setParameters(generatNameValuePair(paramsMap));
		HttpProtocolHandler httpprotocolhandler = HttpProtocolHandler
				.getInstance();
		HttpResponse res = null;
		String token;
		try {
			res = httpprotocolhandler.execute(request, "", "");
			String result = URLDecoder.decode(res.getStringResult(), "utf-8");
			Log.d("result", result);
			token = RSA.getRequestToken(result);
			System.out.println(token);
			resp.setRetjson(token);
			resp.setRetcode(HttpStatus.SC_OK);
			return resp;
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		resp.setRetcode(HttpStatus.SC_OK);
		resp.setRetjson(getZfbWebUrl());
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

	private static final String SUCCESS_URL = "http://source.supwisdom.com:9090";

	public String getZfbRespToken() {
		String token = "";

		String url = "http://wappaygw.alipay.com/service/rest.htm";
		String backurl = SUCCESS_URL + "/services/zfb/mpay/zfbweb";
		String notifyurl = SUCCESS_URL + "/services/zfb/mpay/zfbweb"
				+ "/services/zfb/mpay/webnotify";

		String rep_date = createRepDate("test", "1", "0.01",
				"wenjuan.guan@supwisdom.com", backurl, notifyurl, "jo");

		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", "alipay.wap.trade.create.direct");
		sParaTemp.put("partner", "2088711150346785");
		sParaTemp.put("sec_id", "0001");
		sParaTemp.put("format", "xml");
		sParaTemp.put("v", "2.0");
		sParaTemp.put("req_id", "123");
		sParaTemp.put("req_data", rep_date);
		String signStr = AlipayCore.createLinkString(sParaTemp);
		// System.out.println(signStr);

		String sign = RSA.sign(signStr, AliPayUtil.RSA_PRIVATE, "UTF-8");
		sParaTemp.put("sign", sign);

		HttpRequest request = new HttpRequest(HttpResultType.BYTES);
		request.setCharset("UTF-8");
		request.setUrl(url);
		request.setParameters(generatNameValuePair(sParaTemp));

		HttpProtocolHandler httpprotocolhandler = HttpProtocolHandler
				.getInstance();
		HttpResponse resp = null;
		try {
			resp = httpprotocolhandler.execute(request, "", "");
			String result = URLDecoder.decode(resp.getStringResult(), "utf-8");
			token = getRequestToken(result, AliPayUtil.RSA_PRIVATE);
			System.out.println(token);
			return token;
		} catch (HttpException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public String getZfbWebUrl() {
		String url = "http://wappaygw.alipay.com/service/rest.htm?";

		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", "alipay.wap.auth.authAndExecute");
		sParaTemp.put("partner", "2088711150346785");
		sParaTemp.put("sec_id", "0001");
		sParaTemp.put("format", "xml");
		sParaTemp.put("v", "2.0");

		String token = getZfbRespToken();
		String rep_data = "";
		if (null != token && !"".equals(token)) {
			rep_data = "<auth_and_execute_req><request_token>" + token
					+ "</request_token></auth_and_execute_req>";
		} else {
			return null;
		}
		sParaTemp.put("req_data", rep_data);
		String signStr = AlipayCore.createLinkString(sParaTemp);
		// System.out.println(signStr);
		String sign = RSA.sign(signStr, AliPayUtil.RSA_PRIVATE, "UTF-8");
		// try {
		// // 仅需对sign 做URL编码
		// sign = URLEncoder.encode(sign, "UTF-8");
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }
		// sParaTemp.put("sign", sign);
		String weburl = "";
		try {
			weburl = url + AlipayCore.createLinkString(sParaTemp) + "&sign="
					+ URLEncoder.encode(sign, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		if (null != weburl && !"".equals(weburl)) {
			System.out.println("weburl="+weburl);
			return weburl;
		}

		return null;
	}

	private String createRepDate(String subject, String refno, String amount,
			String seller, String backurl, String notifyurl, String stumpno) {
		String orderInfor = "<direct_trade_create_req><su"
				+ "bject>"
				+ subject
				+ "</subject><out_trade_no>"
				+ refno
				+ "</out_trade_no><total_fee>"
				+ amount
				+ "</total_fee><seller_account_name>"
				+ seller
				+ "</seller_account_name><call_back_url>"
				+ backurl
				+ "</call_back_url><notify_url>"
				+ notifyurl
				+ "</notify_url><out_user>"
				+ stumpno
				+ "</out_user><pay_expire>3600</pay_expire></direct_trade_create_req>";
		return orderInfor;
	}

	/**
	 * 解析远程模拟提交后返回的信息，获得token
	 * 
	 * @param text
	 *            要解析的字符串
	 * @return 解析结果
	 * @throws Exception
	 */
	public static String getRequestToken(String text, String key)
			throws Exception {
		String request_token = "";
		// 以“&”字符切割字符串
		String[] strSplitText = text.split("&");
		// 把切割后的字符串数组变成变量与数值组合的字典数组
		Map<String, String> paraText = new HashMap<String, String>();
		for (int i = 0; i < strSplitText.length; i++) {

			// 获得第一个=字符的位置
			int nPos = strSplitText[i].indexOf("=");
			// 获得字符串长度
			int nLen = strSplitText[i].length();
			// 获得变量名
			String strKey = strSplitText[i].substring(0, nPos);
			// 获得数值
			String strValue = strSplitText[i].substring(nPos + 1, nLen);
			// 放入MAP类中
			paraText.put(strKey, strValue);
		}

		if (paraText.get("res_data") != null) {
			String res_data = paraText.get("res_data");
			// 解析加密部分字符串（RSA与MD5区别仅此一句）
			res_data = RSA.decrypt(res_data, key, "utf-8");
			System.out.println(res_data);
			// token从res_data中解析出来（也就是说res_data中已经包含token的内容）
			
			/*//TODO waring that there has bad decode format str for the under codes,if you got this code and running an app on your phone please debug these code.
			request_token = res_data
					.substring(res_data.indexOf("<?"));
			int startIndex = request_token.indexOf("<request_token>");
			if (startIndex > -1) {
				int temp = "<request_token>".length();
				String start = request_token.substring(temp + startIndex, temp
						+ startIndex + 6);
				Log.d("start", start);
				int endIndex = request_token.indexOf("</request_token>");
				String end = request_token.substring(endIndex - 34, endIndex);
				Log.d("end", end);
				return start + end;
			}*/
			Document document = DocumentHelper.parseText(res_data);
			Element root = document.getRootElement();
			request_token = root.element("request_token").getText();
		}
		return request_token;
	}

	@Override
	protected void onPostExecute(TransResp result) {
		callback.callback(result);
		super.onPostExecute(result);
	}
}