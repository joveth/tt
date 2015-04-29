package com.mtu.foundation.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mtu.foundation.bean.DonateBean;
import com.mtu.foundation.bean.NewsBean;
import com.mtu.foundation.bean.RechargeType;
import com.mtu.foundation.bean.ThankBean;
import com.mtu.foundation.util.CommonUtil;
import com.mtu.foundation.util.Constants;

public class HTMLParser {
	private StringBuilder builder;
	private String lastPager;

	public HTMLParser() {

	}

	public HTMLParser(String html) {
		builder = new StringBuilder(0);
		builder.append(html);
	}

	public String getHTMLStr() {
		return builder.toString();
	}

	public void setHTMLStr(String str) {
		if (builder == null) {
			builder = new StringBuilder(0);
		} else {
			builder.setLength(0);
		}
		builder.append(str);
	}

	public List<ThankBean> getThanks() {
		List<ThankBean> list = new ArrayList<ThankBean>();

		if (builder == null || builder.length() == 0) {
			return list;
		}

		Document doc = Jsoup.parse(builder.toString());
		Element rootEle = doc.getElementById(Constants.THANKS_PAGE_MAIN_ID);
		if (rootEle == null) {
			return list;
		}

		Elements table = rootEle.getElementsByTag(Constants.TAG_TABLE);
		if (table == null || table.size() == 0) {
			return list;
		}

		Elements units = table.get(0).getElementsByTag(Constants.TAG_TR);
		if (units == null || units.size() <= 1) {
			return list;
		}

		ThankBean bean = null;
		for (int i = 1; i < units.size(); i++) {
			bean = new ThankBean();
			Element unit = units.get(i);
			Elements uname = unit.getElementsByClass(Constants.CLASS_USERNAME);
			if (uname == null || uname.size() == 0) {
				continue;
			}
			bean.setUserName(uname.get(0).text());

			Elements amount = unit.getElementsByClass(Constants.CLASS_AMOUNT);
			if (amount != null && amount.size() > 0) {
				bean.setAmount(amount.get(0).text());
			}

			Elements item = unit.getElementsByClass(Constants.CLASS_ITEM);
			if (item != null && item.size() > 0) {
				bean.setProjectName(item.get(0).text());
			}

			Elements date = unit.getElementsByClass(Constants.CLASS_DONATETIME);
			if (date != null && date.size() > 0) {
				String datetime = date.get(0).text();
				bean.setDate(datetime);
				if (!CommonUtil.isEmpty(datetime)) {
					String nowYear = CommonUtil.getNowYear();
					if (datetime.indexOf("-") > -1) {
						datetime = datetime.replaceAll("-", "");
					}
					if (datetime.length() >= 6) {
						bean.setYear(datetime.substring(0, 4));
						bean.setMonth(datetime.substring(4, 6));
						if (bean.getYear().equals(nowYear)) {
							bean.setDate(bean.getMonth() + "-"
									+ datetime.substring(6));
						}
					}
				}

			}
			list.add(bean);
		}
		Elements pagers = rootEle.getElementsByClass(Constants.CLASS_LAST_PAGE);
		if (pagers != null && pagers.size() != 0) {
			String href = pagers.get(0).child(0).attr("href");
			if (!CommonUtil.isEmpty(href)) {
				int index = href.lastIndexOf("=");
				if (index > -1) {
					lastPager = href.substring(index + 1, href.length());
				}
			}
		}
		return list;
	}

	public String getLastPager() {
		return lastPager;
	}

	public List<NewsBean> getNews() {
		List<NewsBean> list = new ArrayList<NewsBean>();
		if (builder == null || builder.length() == 0) {
			return list;
		}

		Document doc = Jsoup.parse(builder.toString());
		Element rootEle = doc.getElementById(Constants.THANKS_PAGE_MAIN_ID);
		if (rootEle == null) {
			return list;
		}

		Elements content = rootEle
				.getElementsByClass(Constants.CLASS_NEWS_CONTENT);
		if (content == null || content.size() == 0) {
			return list;
		}

		Elements units = content.get(0).getElementsByTag(Constants.TAG_LI);
		if (units == null || units.size() <= 1) {
			return list;
		}

		NewsBean bean = null;
		for (int i = 0; i < units.size(); i++) {
			bean = new NewsBean();
			Element unit = units.get(i);
			Elements titles = unit.getElementsByClass("title");
			if (titles != null && titles.size() > 0) {
				Elements a_ele = titles.get(0)
						.getElementsByTag(Constants.TAG_A);
				if (a_ele == null || a_ele.size() == 0) {
					continue;
				}
				bean.setTitle(a_ele.get(0).text());
				bean.setUrl(a_ele.get(0).attr("href"));
			}

			Elements imgs = unit.getElementsByClass("thumb");
			if (imgs != null && imgs.size() != 0) {
				Elements a_ele = imgs.get(0).getElementsByTag(Constants.TAG_A);
				if (a_ele != null && a_ele.size() != 0) {
					Elements img_ele = a_ele.get(0).getElementsByTag(
							Constants.TAG_IMG);
					if (img_ele != null && img_ele.size() != 0) {
						String imsrc = img_ele.get(0).attr("src");
						int ind = imsrc.indexOf("?");
						if (ind > -1) {
							imsrc = imsrc.substring(0, ind);
						}
						bean.setImg(imsrc);
						ThreadPoolUtils.execute(new HttpGetThread(imsrc));
					}
				}
			}

			Elements date = unit.getElementsByClass("date");
			if (date != null && date.size() > 0) {
				String datetime = date.get(0).text();
				bean.setDate(datetime);
			}
			list.add(bean);
		}
		Elements pagers = rootEle.getElementsByClass(Constants.CLASS_LAST_PAGE);
		if (pagers != null && pagers.size() != 0) {
			String href = pagers.get(0).child(0).attr("href");
			if (!CommonUtil.isEmpty(href)) {
				int index = href.lastIndexOf("=");
				if (index > -1) {
					lastPager = href.substring(index + 1, href.length());
				}
			}
		}
		return list;
	}

	public DonateBean getInitDonateBean() {
		DonateBean bean = null;
		Document doc = Jsoup.parse(builder.toString());
		Element rootEle = doc.getElementById(Constants.THANKS_PAGE_MAIN_ID);
		if (rootEle == null) {
			return bean;
		}
		Element form_ele = rootEle.getElementById("donate-form");
		if (form_ele == null) {
			return bean;
		}
		Element item = form_ele.getElementById("edit-item");
		if (item == null) {
			return bean;
		}
		Elements items = item.getElementsByTag("option");
		if (items == null || items.size() == 0) {
			return bean;
		}
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < items.size(); i++) {
			Element obj = items.get(i);
			list.add(obj.attr("value"));
		}
		bean = new DonateBean();
		bean.setItemList(list);
		/*
		 * Element bank = form_ele.getElementById("edit-bank"); if (bank ==
		 * null) { return bean; } Elements banks = bank.getElementsByTag("div");
		 * if (banks == null || banks.size() == 0) { return bean; } Map<String,
		 * String> map = new HashMap<String, String>(); for (int i = 0; i <
		 * banks.size(); i++) { Element obj = banks.get(i); Elements input_ele =
		 * obj.getElementsByTag("input"); if (input_ele == null ||
		 * input_ele.size() == 0) { continue; } String bankKey =
		 * input_ele.attr("value"); if (CommonUtil.isEmpty(bankKey)) { continue;
		 * } Elements label_ele = obj.getElementsByTag("label"); if (label_ele
		 * == null || label_ele.size() == 0) { continue; } map.put(bankKey,
		 * label_ele.text()); } bean.setBankMap(map);
		 */
		return bean;
	}

	public List<RechargeType> getInitDonateBankBean() {

		Document doc = Jsoup.parse(builder.toString());
		List<RechargeType> list = new ArrayList<RechargeType>();
		Element rootEle = doc.getElementById(Constants.THANKS_PAGE_MAIN_ID);
		if (rootEle == null) {
			return list;
		}
		Element form_ele = rootEle.getElementById("donate-form");
		if (form_ele == null) {
			return list;
		}
		Element bank = form_ele.getElementById("edit-bank");
		if (bank == null) {
			return list;
		}
		Elements banks = bank.getElementsByClass("form-item");
		if (banks == null || banks.size() == 0) {
			return list;
		}
		RechargeType bean = null;
		for (int i = 0; i < banks.size(); i++) {
			bean = new RechargeType();
			Element obj = banks.get(i);
			Elements input_ele = obj.getElementsByTag("input");
			if (input_ele == null || input_ele.size() == 0) {
				continue;
			}
			String bankKey = input_ele.attr("value");
			if (CommonUtil.isEmpty(bankKey)) {
				continue;
			}
			Elements label_ele = obj.getElementsByTag("label");
			if (label_ele == null || label_ele.size() == 0) {
				continue;
			}
			bean.setBankkey(bankKey);
			bean.setBankname(label_ele.text());
			list.add(bean);
		}
		return list;
	}

	public String getHtmlContent() {
		Document doc = Jsoup.parse(builder.toString());
		Element rootEle = doc.getElementById(Constants.THANKS_PAGE_MAIN_ID);
		if (rootEle == null) {
			return null;
		}
		return rootEle.html();
	}
}
