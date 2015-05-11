package com.mtu.foundation.frame;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.mtu.foundation.AboutAppActivity;
import com.mtu.foundation.AboutFoundationActivity;
import com.mtu.foundation.AccountActivity;
import com.mtu.foundation.ContactActivity;
import com.mtu.foundation.ProcessActivity;
import com.mtu.foundation.R;
import com.mtu.foundation.WeiboActivity;

public class MoreFrame extends Fragment implements OnClickListener {
	private View view, vAccount, vContact, vWeibo, vProcess, vAbout, vApp;
	private Context context;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.tab_more, container, false);
		context = view.getContext();
		initView();
		return view;
	}

	private void initView() {
		vAccount = view.findViewById(R.id.account_lay);
		vAccount.setOnClickListener(this);

		vContact = view.findViewById(R.id.contact_lay);
		vContact.setOnClickListener(this);

		/*vWeibo = view.findViewById(R.id.weibo_lay);
		vWeibo.setOnClickListener(this);
		 */
		vProcess = view.findViewById(R.id.about_donate_lay);
		vProcess.setOnClickListener(this);

		vAbout = view.findViewById(R.id.about_lay);
		vAbout.setOnClickListener(this);

		vApp = view.findViewById(R.id.about_app_lay);
		vApp.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		if (vAccount == arg0) {
			switchTo(AccountActivity.class);
			return;
		}
		if (vContact == arg0) {
			switchTo(ContactActivity.class);
			return;
		}
		if (vWeibo == arg0) {
			switchTo(WeiboActivity.class);
			return;
		}
		if (vProcess == arg0) {
			switchTo(ProcessActivity.class);
			return;
		}
		if (vAbout == arg0) {
			switchTo(AboutFoundationActivity.class);
			return;
		}
		if (vApp == arg0) {
			switchTo(AboutAppActivity.class);
			return;
		}

	}

	private void switchTo(Class clazz) {
		Intent intent = new Intent(context, clazz);
		startActivity(intent);
	}

}
