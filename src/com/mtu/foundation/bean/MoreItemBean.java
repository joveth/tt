package com.mtu.foundation.bean;


/**
 * Created by jov on 2015/1/29.
 */
public class MoreItemBean {
	private int imgId;
	private String text;
	private String position;
	private String processActivity;
	private boolean display;
	private int x = 1;
	private int y = 1;

	public int getImgId() {
		return imgId;
	}

	public void setImgId(int imgId) {
		this.imgId = imgId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getProcessActivity() {
		return processActivity;
	}

	public void setProcessActivity(String processActivity) {
		this.processActivity = processActivity;
	}

	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}
