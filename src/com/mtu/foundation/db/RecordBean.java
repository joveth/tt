package com.mtu.foundation.db;

public class RecordBean {
	private int id;
	private String amount;
	private String project;
	private String comment;
	private String username;
	private String gender;
	private String is_alumni;
	private String email;
	private String tel;
	private String cellphone;
	private String address;
	private String postcode;
	private String company;
	private String is_anonymous;
	private String paytype;
	private String bank;
	private String cardName;
	private String date;
	private String status;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getIs_alumni() {
		return is_alumni;
	}

	public void setIs_alumni(String is_alumni) {
		this.is_alumni = is_alumni;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getCellphone() {
		return cellphone;
	}

	public void setCellphone(String cellphone) {
		this.cellphone = cellphone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getIs_anonymous() {
		return is_anonymous;
	}

	public void setIs_anonymous(String is_anonymous) {
		this.is_anonymous = is_anonymous;
	}

	public String getPaytype() {
		return paytype;
	}

	public void setPaytype(String paytype) {
		this.paytype = paytype;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "RecordBean [id=" + id + ", amount=" + amount + ", project="
				+ project + ", comment=" + comment + ", username=" + username
				+ ", gender=" + gender + ", is_alumni=" + is_alumni
				+ ", email=" + email + ", tel=" + tel + ", cellphone="
				+ cellphone + ", address=" + address + ", postcode=" + postcode
				+ ", company=" + company + ", is_anonymous=" + is_anonymous
				+ ", paytype=" + paytype + ", bank=" + bank + ", cardName="
				+ cardName + ", date=" + date + ", status=" + status + "]";
	}

}
