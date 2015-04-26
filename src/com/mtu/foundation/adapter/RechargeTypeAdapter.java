package com.mtu.foundation.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mtu.foundation.PayRouteActivity;
import com.mtu.foundation.R;
import com.mtu.foundation.bean.RechargeType;
import com.mtu.foundation.util.Constants;

/**
 * Created by jov on 2015/2/11.
 */
public class RechargeTypeAdapter extends BaseAdapter {
	private Context context;
	private List<RechargeType> list;
	private Intent intent;

	public RechargeTypeAdapter(Context context, List<RechargeType> list) {
		this.context = context;
		this.list = list;
		intent = new Intent(context, PayRouteActivity.class);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int i) {
		return list.get(i);
	}

	@Override
	public long getItemId(int i) {
		return 0;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		final Holder hold;
		if (view == null) {
			hold = new Holder();
			view = View.inflate(context, R.layout.recharge_type_item, null);
			hold.itemTxt = (TextView) view.findViewById(R.id.item_name);
			hold.lay = view.findViewById(R.id.item_lay);
			view.setTag(hold);
		} else {
			hold = (Holder) view.getTag();
		}
		final String typeName = list.get(i).getBankname();
		final String cardkey = list.get(i).getBankkey();
		hold.itemTxt.setText(typeName);
		if (list.get(i).isChecked()) {
			Drawable d = context.getResources().getDrawable(
					R.drawable.iconfont_selected);
			d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
			hold.itemTxt.setCompoundDrawables(null, null, d, null);
		}

		hold.lay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				intent.putExtra("cardName", typeName);
				intent.putExtra("cardkey", cardkey);
				Activity temp = ((Activity) context);
				temp.setResult(Constants.RESULT_OK, intent);
				temp.finish();
			}
		});
		return view;
	}

	static class Holder {
		TextView itemTxt;
		View lay;
	}
}
