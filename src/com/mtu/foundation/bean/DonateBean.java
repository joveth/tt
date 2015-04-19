package com.mtu.foundation.bean;

import java.util.List;
import java.util.Map;

public class DonateBean {
	private List<String> itemList;
	private Map<String, String> bankMap;

	public List<String> getItemList() {
		return itemList;
	}

	public void setItemList(List<String> itemList) {
		this.itemList = itemList;
	}

	public Map<String, String> getBankMap() {
		return bankMap;
	}

	public void setBankMap(Map<String, String> bankMap) {
		this.bankMap = bankMap;
	}

}
