package com.mtu.foundation.frame;

import com.mtu.foundation.R;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class HomeFrame extends Fragment implements OnClickListener {
	private View view;

	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.tab_home, container, false);
		return view;
	}

	@Override
	public void onClick(View arg0) {

	}

}
