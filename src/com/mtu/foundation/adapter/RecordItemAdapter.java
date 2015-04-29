package com.mtu.foundation.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mtu.foundation.R;
import com.mtu.foundation.ToPayActivity;
import com.mtu.foundation.db.RecordBean;
import com.mtu.foundation.util.CommonUtil;

public class RecordItemAdapter extends BaseAdapter {
	private List<RecordBean> list;
	private Context context;

	public RecordItemAdapter(Context context, List<RecordBean> items) {
		this.context = context;
		this.list = items;
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
		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {

		final Holder hold;
		final RecordBean bean = list.get(i);
		if (view == null) {
			hold = new Holder();
			view = View.inflate(context, R.layout.records_item, null);
			hold.itemName = (TextView) view.findViewById(R.id.item_name);
			hold.itemAmount = (TextView) view.findViewById(R.id.item_money);
			hold.itemProject = (TextView) view.findViewById(R.id.item_project);
			hold.itemDate = (TextView) view.findViewById(R.id.item_date);
			hold.lay = view.findViewById(R.id.item_lay);
			view.setTag(hold);
		} else {
			hold = (Holder) view.getTag();
		}
		hold.itemName.setText(bean.getUsername());
		hold.itemAmount.setText(CommonUtil.decialStrFormat(bean.getAmount()));
		String temp = bean.getDate();
		if (temp != null && temp.length() >= 10) {
			temp = temp.substring(5, 11);
		}
		String pay = "alipay".equals(bean.getPaytype()) ? "支付宝付款" : "网银付款";
		hold.itemProject.setText(temp + " " + bean.getProject() + " " + pay);
		hold.lay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, ToPayActivity.class);
				intent.putExtra("item", bean.getProject());
				intent.putExtra("amount", bean.getAmount());
				intent.putExtra("comment", bean.getComment());
				intent.putExtra("username", bean.getUsername());
				intent.putExtra("gender", bean.getGender());
				intent.putExtra("is_alumni", bean.getIs_alumni());
				intent.putExtra("email", bean.getEmail());
				intent.putExtra("tel", bean.getTel());
				intent.putExtra("cellphone", bean.getCellphone());
				intent.putExtra("address", bean.getAddress());
				intent.putExtra("postcode", bean.getPostcode());
				intent.putExtra("company", bean.getCompany());
				intent.putExtra("is_anonymous", bean.getIs_anonymous());
				intent.putExtra("paytype", bean.getPaytype());
				if (!CommonUtil.isEmpty(bean.getBank())) {
					intent.putExtra("bank", bean.getBank());
				}
				context.startActivity(intent);
			}
		});

		return view;
	}

	static class Holder {
		TextView itemName;
		TextView itemAmount;
		TextView itemProject;
		TextView itemDate;
		View lay;
	}
}
