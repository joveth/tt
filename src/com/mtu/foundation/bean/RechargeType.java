package com.mtu.foundation.bean;

/**
 * Created by jov on 2015/2/11.
 */
public class RechargeType {
	private String bankkey;
	private String bankname;
	private boolean checked;

	public String getBankkey() {
		return bankkey;
	}

	public void setBankkey(String bankkey) {
		this.bankkey = bankkey;
	}

	public String getBankname() {
		return bankname;
	}

	public void setBankname(String bankname) {
		this.bankname = bankname;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
}
