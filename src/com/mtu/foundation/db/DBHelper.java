package com.mtu.foundation.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mtu.foundation.util.CommonUtil;

public class DBHelper extends SQLiteOpenHelper {
	/**
	 * table
	 */
	public static final String TABLE_NAME_RECORDS = "tb_records";
	public static final String TABLE_NAME_KEYVALUE = "tb_mapkey";
	private static final String DB_NAME = "com.mtu.foundation";
	/**
	 * version
	 */
	private static final int VERSION = 1;
	/**
	 * SQL for create table
	 */
	private static final String CREATE_TABLE_KEYVALUE = "create table if not exists "
			+ TABLE_NAME_KEYVALUE
			+ "("
			+ BeanPropEnum.KeyValue.tKey
			+ " varchar(40) primary key ,"
			+ BeanPropEnum.KeyValue.tValue
			+ " varchar(300) ,"
			+ BeanPropEnum.KeyValue.tType
			+ " varchar(10) )";

	private static final String CREATE_TABLE_RECORD = "create table if not exists "
			+ TABLE_NAME_RECORDS
			+ "("
			+ BeanPropEnum.RecordProp.id
			+ " integer primary key autoincrement ,"
			+ BeanPropEnum.RecordProp.address
			+ " varchar(300) ,"
			+ BeanPropEnum.RecordProp.amount
			+ " varchar(20), "
			+ BeanPropEnum.RecordProp.bank
			+ " varchar(20), "
			+ BeanPropEnum.RecordProp.cardName
			+ " varchar(60), "
			+ BeanPropEnum.RecordProp.cellphone
			+ " varchar(20), "
			+ BeanPropEnum.RecordProp.comment
			+ " varchar(200), "
			+ BeanPropEnum.RecordProp.company
			+ " varchar(200), "
			+ BeanPropEnum.RecordProp.email
			+ " varchar(100), "
			+ BeanPropEnum.RecordProp.gender
			+ " varchar(10), "
			+ BeanPropEnum.RecordProp.is_alumni
			+ " varchar(10), "
			+ BeanPropEnum.RecordProp.is_anonymous
			+ " varchar(10), "
			+ BeanPropEnum.RecordProp.paytype
			+ " varchar(10), "
			+ BeanPropEnum.RecordProp.postcode
			+ " varchar(10), "
			+ BeanPropEnum.RecordProp.project
			+ " varchar(10), "
			+ BeanPropEnum.RecordProp.tel
			+ " varchar(20), "
			+ BeanPropEnum.RecordProp.username
			+ " varchar(100),"
			+ BeanPropEnum.RecordProp.status
			+ " varchar(50),"
			+ BeanPropEnum.RecordProp.date + " varchar(20)  )";
	private static DBHelper instance = null;

	private DBHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_KEYVALUE);
		db.execSQL(CREATE_TABLE_RECORD);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public static DBHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DBHelper(context);
		}
		return instance;
	}

	public void saveRecords(RecordBean bean) {
		SQLiteDatabase db = getReadableDatabase();
		if (bean == null) {
			return;
		}
		try {
			// check if in
			String sql = "insert into  " + TABLE_NAME_RECORDS + "( "
					+ BeanPropEnum.RecordProp.id + " ,"
					+ BeanPropEnum.RecordProp.address + " ,"
					+ BeanPropEnum.RecordProp.amount + " , "
					+ BeanPropEnum.RecordProp.bank + " , "
					+ BeanPropEnum.RecordProp.cardName + " , "
					+ BeanPropEnum.RecordProp.cellphone + " , "
					+ BeanPropEnum.RecordProp.comment + " , "
					+ BeanPropEnum.RecordProp.company + " , "
					+ BeanPropEnum.RecordProp.email + " , "
					+ BeanPropEnum.RecordProp.gender + " , "
					+ BeanPropEnum.RecordProp.is_alumni + " , "
					+ BeanPropEnum.RecordProp.is_anonymous + " , "
					+ BeanPropEnum.RecordProp.paytype + " , "
					+ BeanPropEnum.RecordProp.postcode + " , "
					+ BeanPropEnum.RecordProp.project + " , "
					+ BeanPropEnum.RecordProp.tel + " , "
					+ BeanPropEnum.RecordProp.username + " ,"
					+ BeanPropEnum.RecordProp.status + " ,"
					+ BeanPropEnum.RecordProp.date
					+ ") values(null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			db.execSQL(
					sql,
					new String[] { bean.getAddress(), bean.getAmount(),
							bean.getBank(), bean.getCardName(),
							bean.getCellphone(), bean.getComment(),
							bean.getCompany(), bean.getEmail(),
							bean.getGender(), bean.getIs_alumni(),
							bean.getIs_anonymous(), bean.getPaytype(),
							bean.getPostcode(), bean.getProject(),
							bean.getTel(), bean.getUsername(),
							bean.getStatus(), bean.getDate() });

		} catch (Exception e) {

		} finally {
			db.close();
		}
	}

	public List<RecordBean> getRecords() {
		SQLiteDatabase db = getReadableDatabase();
		String sql = " select  * from " + TABLE_NAME_RECORDS
				+ " order by id desc limit 50 offset 0";
		Cursor cursor = db.rawQuery(sql, null);

		RecordBean bean = null;
		List<RecordBean> list = new ArrayList<RecordBean>(0);
		while (cursor != null && cursor.moveToNext()) {
			bean = new RecordBean();
			bean.setId(cursor.getInt(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.id.toString())));
			bean.setAddress(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.address.toString())));

			bean.setAmount(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.amount.toString())));
			bean.setBank(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.bank.toString())));
			bean.setCardName(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.cardName.toString())));

			bean.setCellphone(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.cellphone
							.toString())));
			bean.setComment(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.comment.toString())));
			bean.setCompany(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.company.toString())));
			bean.setDate(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.date.toString())));
			bean.setEmail(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.email.toString())));
			bean.setGender(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.gender.toString())));

			bean.setIs_alumni(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.is_alumni
							.toString())));

			bean.setIs_anonymous(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.is_anonymous
							.toString())));

			bean.setPaytype(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.paytype.toString())));

			bean.setPostcode(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.postcode.toString())));

			bean.setProject(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.project.toString())));
			bean.setStatus(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.status.toString())));
			bean.setTel(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.tel.toString())));
			bean.setUsername(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.username.toString())));

			list.add(bean);
		}
		db.close();
		return list;
	}

	public void deleteRecord(Integer id) {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(TABLE_NAME_RECORDS, "id=?", new String[] { id.toString() });
		db.close();
	}

	public void deleteAllRecord() {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(TABLE_NAME_RECORDS, null, null);
		db.close();
	}

	public RecordBean getRecordById(Integer id) {
		SQLiteDatabase db = getReadableDatabase();
		String sql = " select  * from " + TABLE_NAME_RECORDS + " where "
				+ BeanPropEnum.RecordProp.id + "=?";
		Cursor cursor = db.rawQuery(sql, new String[] { id.toString() });

		RecordBean bean = null;
		if (cursor != null && cursor.moveToNext()) {
			bean = new RecordBean();
			bean.setId(cursor.getInt(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.id.toString())));
			bean.setAddress(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.address.toString())));

			bean.setAmount(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.amount.toString())));
			bean.setBank(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.bank.toString())));
			bean.setCardName(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.cardName.toString())));

			bean.setCellphone(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.cellphone
							.toString())));
			bean.setComment(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.comment.toString())));
			bean.setCompany(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.company.toString())));
			bean.setDate(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.date.toString())));
			bean.setEmail(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.email.toString())));
			bean.setGender(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.gender.toString())));

			bean.setIs_alumni(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.is_alumni
							.toString())));

			bean.setIs_anonymous(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.is_anonymous
							.toString())));

			bean.setPaytype(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.paytype.toString())));

			bean.setPostcode(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.postcode.toString())));

			bean.setProject(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.project.toString())));
			bean.setStatus(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.status.toString())));
			bean.setTel(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.tel.toString())));
			bean.setUsername(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.username.toString())));
		}
		db.close();
		return bean;
	}

	public RecordBean getLastUser() {
		SQLiteDatabase db = getReadableDatabase();
		String sql = " select  * from " + TABLE_NAME_KEYVALUE + " where "
				+ BeanPropEnum.KeyValue.tKey + "=?";
		Cursor cursor = db.rawQuery(sql,
				new String[] { BeanPropEnum.RecordProp.username.toString() });

		RecordBean bean = new RecordBean();
		if (cursor != null && cursor.moveToNext()) {
			bean.setUsername(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.KeyValue.tValue.toString())));
		}
		cursor = db.rawQuery(sql,
				new String[] { BeanPropEnum.RecordProp.email.toString() });

		if (cursor != null && cursor.moveToNext()) {
			bean.setEmail(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.KeyValue.tValue.toString())));
		}

		cursor = db.rawQuery(sql,
				new String[] { BeanPropEnum.RecordProp.cellphone.toString() });

		if (cursor != null && cursor.moveToNext()) {
			bean.setCellphone(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.KeyValue.tValue.toString())));
		}
		cursor.close();

		db.close();
		return bean;
	}

	public void saveLastRecord(RecordBean bean) {
		if (bean == null) {
			return;
		}
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = null;
		StringBuilder sqlSelect = new StringBuilder(" select "
				+ BeanPropEnum.KeyValue.tValue + ","
				+ BeanPropEnum.KeyValue.tType + " from "
				+ DBHelper.TABLE_NAME_KEYVALUE + " where "
				+ BeanPropEnum.KeyValue.tKey + "=?");
		StringBuilder sqlInsert = new StringBuilder(" insert into  "
				+ DBHelper.TABLE_NAME_KEYVALUE + "( "
				+ BeanPropEnum.KeyValue.tKey + ","
				+ BeanPropEnum.KeyValue.tValue + ") values(?,?)");
		String value = null;
		if (!CommonUtil.isEmpty(bean.getUsername())) {
			cursor = db
					.rawQuery(sqlSelect.toString(),
							new String[] { BeanPropEnum.RecordProp.username
									.toString() });
			if (cursor != null && cursor.moveToNext()) {
				value = cursor
						.getString(cursor
								.getColumnIndex(BeanPropEnum.KeyValue.tValue
										.toString()));
				if (value == null || !value.equals(bean.getUsername())) {
					ContentValues values = new ContentValues();
					values.put(BeanPropEnum.KeyValue.tValue.toString(),
							bean.getUsername());
					db.update(DBHelper.TABLE_NAME_KEYVALUE, values,
							BeanPropEnum.KeyValue.tKey + "=?",
							new String[] { BeanPropEnum.RecordProp.username
									.toString() });
				}
				cursor.close();
			} else {
				db.execSQL(
						sqlInsert.toString(),
						new String[] {
								BeanPropEnum.RecordProp.username.toString(),
								bean.getUsername() });
			}
		}
		if (!CommonUtil.isEmpty(bean.getEmail())) {
			cursor = db.rawQuery(sqlSelect.toString(),
					new String[] { BeanPropEnum.RecordProp.email.toString() });
			if (cursor != null && cursor.moveToNext()) {
				value = cursor
						.getString(cursor
								.getColumnIndex(BeanPropEnum.KeyValue.tValue
										.toString()));
				if (value == null || !value.equals(bean.getEmail())) {
					ContentValues values = new ContentValues();
					values.put(BeanPropEnum.KeyValue.tValue.toString(),
							bean.getEmail());
					db.update(DBHelper.TABLE_NAME_KEYVALUE, values,
							BeanPropEnum.KeyValue.tKey + "=?",
							new String[] { BeanPropEnum.RecordProp.email
									.toString() });
				}
				cursor.close();
			} else {
				db.execSQL(
						sqlInsert.toString(),
						new String[] {
								BeanPropEnum.RecordProp.email.toString(),
								bean.getEmail() });
			}
		}
		if (!CommonUtil.isEmpty(bean.getCellphone())) {
			cursor = db
					.rawQuery(sqlSelect.toString(),
							new String[] { BeanPropEnum.RecordProp.cellphone
									.toString() });
			if (cursor != null && cursor.moveToNext()) {
				value = cursor
						.getString(cursor
								.getColumnIndex(BeanPropEnum.KeyValue.tValue
										.toString()));
				if (value == null || !value.equals(bean.getCellphone())) {
					ContentValues values = new ContentValues();
					values.put(BeanPropEnum.KeyValue.tValue.toString(),
							bean.getCellphone());
					db.update(DBHelper.TABLE_NAME_KEYVALUE, values,
							BeanPropEnum.KeyValue.tKey + "=?",
							new String[] { BeanPropEnum.RecordProp.cellphone
									.toString() });
				}
				cursor.close();
			} else {
				db.execSQL(
						sqlInsert.toString(),
						new String[] {
								BeanPropEnum.RecordProp.cellphone.toString(),
								bean.getCellphone() });
			}
		}
	}

	public String getVersion() {
		SQLiteDatabase db = getReadableDatabase();
		String sql = " select  * from " + TABLE_NAME_KEYVALUE + " where "
				+ BeanPropEnum.KeyValue.tKey + "=?";
		Cursor cursor = db.rawQuery(sql,
				new String[] { BeanPropEnum.CommonProp.version.toString() });
		String version = null;
		if (cursor != null && cursor.moveToNext()) {
			version = cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.KeyValue.tValue.toString()));
		}
		return version;
	}

	public void saveVersion(String version) {
		SQLiteDatabase db = getReadableDatabase();
		String sql = " insert into  " + DBHelper.TABLE_NAME_KEYVALUE + "( "
				+ BeanPropEnum.KeyValue.tKey + ","
				+ BeanPropEnum.KeyValue.tValue + ") values(?,?)";
		db.execSQL(sql,
				new String[] { BeanPropEnum.CommonProp.version.toString(),
						version });
	}

	public RecordBean getLastRecord() {
		SQLiteDatabase db = getReadableDatabase();
		String sql = " select  * from " + TABLE_NAME_RECORDS
				+ " order by id desc limit 1 offset 0";
		Cursor cursor = db.rawQuery(sql, null);
		RecordBean bean = null;
		if (cursor != null && cursor.moveToNext()) {
			bean = new RecordBean();
			bean.setId(cursor.getInt(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.id.toString())));
			bean.setAddress(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.address.toString())));

			bean.setAmount(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.amount.toString())));
			bean.setBank(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.bank.toString())));
			bean.setCardName(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.cardName.toString())));

			bean.setCellphone(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.cellphone
							.toString())));
			bean.setComment(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.comment.toString())));
			bean.setCompany(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.company.toString())));
			bean.setDate(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.date.toString())));
			bean.setEmail(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.email.toString())));
			bean.setGender(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.gender.toString())));

			bean.setIs_alumni(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.is_alumni
							.toString())));

			bean.setIs_anonymous(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.is_anonymous
							.toString())));

			bean.setPaytype(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.paytype.toString())));

			bean.setPostcode(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.postcode.toString())));

			bean.setProject(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.project.toString())));
			bean.setStatus(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.status.toString())));
			bean.setTel(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.tel.toString())));
			bean.setUsername(cursor.getString(cursor
					.getColumnIndex(BeanPropEnum.RecordProp.username.toString())));
		}
		db.close();
		return bean;
	}
}
