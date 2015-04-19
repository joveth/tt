package com.mtu.foundation.frame;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mtu.foundation.R;

public class DonateFrame extends Fragment {
	private View view;
	private Context context;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.tab_donate, container, false);
		context = view.getContext();
		return view;
	}
}
