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

import com.mtu.foundation.AboutFoundationActivity;
import com.mtu.foundation.AccountActivity;
import com.mtu.foundation.ContactActivity;
import com.mtu.foundation.DonateActivity;
import com.mtu.foundation.MessageActivity;
import com.mtu.foundation.MoreActivity;
import com.mtu.foundation.NewsActivity;
import com.mtu.foundation.ProcessActivity;
import com.mtu.foundation.R;
import com.mtu.foundation.RecentActivity;
import com.mtu.foundation.RecordsActivity;

public class HomeFrame extends Fragment implements OnClickListener {
	private View view, vNews, vRecent, vDoDonate, vAccount, vContact, vProcess,
			vAbout, vMessage, vMore, vRecords;
	private Context context;

	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.tab_home_v2, container, false);
		context = view.getContext();
		initView();
		return view;
	}

	private void initView() {
		vNews = view.findViewById(R.id.news_lay);
		vNews.setOnClickListener(this);
		vRecent = view.findViewById(R.id.recent_donate_lay);
		vRecent.setOnClickListener(this);
		vDoDonate = view.findViewById(R.id.donate_lay);
		vDoDonate.setOnClickListener(this);
		vAccount = view.findViewById(R.id.account_lay);
		vAccount.setOnClickListener(this);
		vContact = view.findViewById(R.id.contact_lay);
		vContact.setOnClickListener(this);
		vProcess = view.findViewById(R.id.process_lay);
		vProcess.setOnClickListener(this);
		vAbout = view.findViewById(R.id.about_lay);
		vAbout.setOnClickListener(this);
		vMessage = view.findViewById(R.id.message_lay);
		vMessage.setOnClickListener(this);
		vMore = view.findViewById(R.id.more_lay);
		vMore.setOnClickListener(this);
		vRecords = view.findViewById(R.id.mydonate_lay);
		vRecords.setOnClickListener(this);
	}

	private void switchTo(Class clazz) {
		Intent intent = new Intent(context, clazz);
		startActivity(intent);
	}

	@Override
	public void onClick(View arg0) {
		if (arg0 == vNews) {
			switchTo(NewsActivity.class);
			return;
		}

		if (arg0 == vRecent) {
			switchTo(RecentActivity.class);
			return;
		}
		if (arg0 == vRecords) {
			switchTo(RecordsActivity.class);
			return;
		}
		if (arg0 == vDoDonate) {
			switchTo(DonateActivity.class);
			return;
		}
		if (arg0 == vAccount) {
			switchTo(AccountActivity.class);
			return;
		}
		if (arg0 == vContact) {
			switchTo(ContactActivity.class);
			return;
		}
		if (arg0 == vProcess) {
			switchTo(ProcessActivity.class);
			return;
		}
		if (arg0 == vAbout) {
			switchTo(AboutFoundationActivity.class);
			return;
		}
		if (arg0 == vMessage) {
			switchTo(MessageActivity.class);
			return;
		}
		if (arg0 == vMore) {
			switchTo(MoreActivity.class);
			return;
		}
	}
}
