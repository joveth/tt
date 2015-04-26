package com.mtu.foundation;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mtu.foundation.db.DBHelper;
import com.mtu.foundation.db.RecordBean;
import com.mtu.foundation.net.httpjersey.NetworkHandler;
import com.mtu.foundation.util.CommonUtil;
import com.mtu.foundation.util.Constants;

public class PayRouteActivity extends BaseActivity {
	private NetworkHandler networkHandler;
	private String item, amount, comment, username, gender, is_alumni, email,
			tel, cellphone, address, postcode, company, is_anonymous,
			paytype = "alipay", bank, cardName;

	private TextView textView, vTypeName;
	private View vNextStep, vZhiFuBao, vZhiFuBaoImg, vZhiFuBaoWeb,
			vZhiFuBaoWebImg, vCardImg, vRechargeType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payroute);
		leftBtn = findViewById(R.id.left_btn);
		leftBtn.setOnClickListener(this);
		leftBtn.setVisibility(View.VISIBLE);
		title = (TextView) findViewById(R.id.top_title);
		title.setText("选择支付方式");
		textView = (TextView) findViewById(R.id.donate_amount);
		vZhiFuBao = findViewById(R.id.recharge_zhifubao);
		vZhiFuBao.setOnClickListener(this);
		vZhiFuBaoImg = findViewById(R.id.recharge_zhifubao_img);

		vZhiFuBaoWeb = findViewById(R.id.recharge_type_zhifubao_web);
		vZhiFuBaoWeb.setOnClickListener(this);
		vZhiFuBaoWebImg = findViewById(R.id.recharge_zhifubao_web_img);
		vCardImg = findViewById(R.id.recharge_card_img);
		vRechargeType = findViewById(R.id.recharge_type_lay);
		vRechargeType.setOnClickListener(this);
		vNextStep = findViewById(R.id.next_step);
		vNextStep.setOnClickListener(this);
		vTypeName = (TextView) findViewById(R.id.charge_type_name);
		initData();
	}

	private void initData() {
		networkHandler = NetworkHandler.getInstance();
		Intent intent = getIntent();
		if (intent == null) {
			finish();
			return;
		}
		item = intent.getStringExtra("item");
		amount = intent.getStringExtra("amount");
		textView.setText(amount);
		comment = intent.getStringExtra("comment");
		username = intent.getStringExtra("username");
		gender = intent.getStringExtra("gender");
		is_alumni = intent.getStringExtra("is_alumni");
		email = intent.getStringExtra("email");
		tel = intent.getStringExtra("tel");
		cellphone = intent.getStringExtra("cellphone");
		address = intent.getStringExtra("address");
		postcode = intent.getStringExtra("postcode");
		company = intent.getStringExtra("company");
		is_anonymous = intent.getStringExtra("is_anonymous");

	}

	private void toPay() {
		Intent intent = new Intent(this, ToPayActivity.class);
		intent.putExtra("item", item);
		intent.putExtra("amount", amount);
		intent.putExtra("comment", comment);
		intent.putExtra("username", username);
		intent.putExtra("gender", gender);
		intent.putExtra("is_alumni", is_alumni);
		intent.putExtra("email", email);
		intent.putExtra("tel", tel);
		intent.putExtra("cellphone", cellphone);
		intent.putExtra("address", address);
		intent.putExtra("postcode", postcode);
		intent.putExtra("company", company);
		intent.putExtra("is_anonymous", is_anonymous);
		intent.putExtra("paytype", paytype);
		if (!CommonUtil.isEmpty(bank)) {
			intent.putExtra("bank", bank);
		}
		startActivityForResult(intent, Constants.REQUEST_CODE_110);
	}

	@Override
	public void onClick(View arg0) {
		if (arg0 == leftBtn) {
			finish();
			return;
		}
		if (arg0 == vNextStep) {
			toPay();
			return;
		}
		if (arg0 == vRechargeType) {
			Intent intent = new Intent(this, RechargeTypeActivity.class);
			if (!CommonUtil.isEmpty(bank)) {
				intent.putExtra("checkedcard", bank);
			}
			startActivityForResult(intent, Constants.REQUEST_CODE);
			return;
		}
		if (arg0 == vZhiFuBao) {
			vZhiFuBaoImg.setVisibility(View.VISIBLE);
			vZhiFuBaoWebImg.setVisibility(View.INVISIBLE);
			vCardImg.setVisibility(View.INVISIBLE);
			return;
		}
		if (arg0 == vZhiFuBaoWeb) {
			vZhiFuBaoImg.setVisibility(View.INVISIBLE);
			vZhiFuBaoWebImg.setVisibility(View.VISIBLE);
			vCardImg.setVisibility(View.INVISIBLE);
			paytype = "alipay";
			return;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.REQUEST_CODE) {
			if (resultCode == Constants.RESULT_OK) {
				vZhiFuBaoImg.setVisibility(View.INVISIBLE);
				vZhiFuBaoWebImg.setVisibility(View.INVISIBLE);
				vCardImg.setVisibility(View.VISIBLE);

				cardName = data.getStringExtra("cardName");
				bank = data.getStringExtra("cardkey");
				if (!CommonUtil.isEmpty(bank)) {
					vTypeName.setText(cardName);
					paytype = "bank";
				}
			} else {
				if (!CommonUtil.isEmpty(bank)) {
					paytype = "bank";
					vZhiFuBaoImg.setVisibility(View.INVISIBLE);
					vZhiFuBaoWebImg.setVisibility(View.INVISIBLE);
					vCardImg.setVisibility(View.VISIBLE);
				}
			}
		} else if (requestCode == Constants.REQUEST_CODE_110) {
			if (resultCode == Constants.RESULT_OK) {
				if (dbHelper == null) {
					dbHelper = DBHelper.getInstance(this);
				}
				RecordBean bean = new RecordBean();
				bean.setAddress(address);
				bean.setAmount(amount);
				bean.setBank(bank);
				bean.setCardName(cardName);
				bean.setCellphone(cellphone);
				bean.setComment(comment);
				bean.setCompany(company);
				bean.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(Calendar.getInstance().getTime()));
				bean.setEmail(email);
				bean.setGender(gender);
				bean.setIs_alumni(is_alumni);
				bean.setIs_anonymous(is_anonymous);
				bean.setPaytype(paytype);
				bean.setPostcode(postcode);
				bean.setProject(item);
				bean.setTel(tel);
				bean.setUsername(username);
				dbHelper.saveRecords(bean);
				dbHelper.saveLastRecord(bean);
				finish();
			}
		}
	}
}
