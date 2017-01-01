package kr.me.sdam.database;

import java.util.ArrayList;

import kr.me.sdam.MyApplication;
import kr.me.sdam.alarm.AlarmData;
import kr.me.sdam.database.DBConstant.PushTable;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager implements PushTable {
	private static DBManager instance;

	public static DBManager getInstance() {
		if (instance == null) {
			instance = new DBManager();
		}
		return instance;
	}

	MyDBHelper mHelper;

	private DBManager() {
		mHelper = new MyDBHelper(MyApplication.getContext());
	}
	
	public int getPushCount(){
		int count = 0;
		ArrayList<AlarmData> items = getPushList();
		for(AlarmData ad : items){
			if(ad.clicked == 0){
				count++;
			}
		}
		return count;
	}
	
	public ArrayList<AlarmData> getPushList() {
		ArrayList<AlarmData> list = new ArrayList<AlarmData>();
		String sql = String.format("SELECT %s, %s, %s, %s, %s FROM %s ORDER BY _ID DESC" ,
				PushTable._ID, PushTable.FIELD_CASE, PushTable.FIELD_NUM, PushTable.FIELD_CLICKED, PushTable.FIELD_DB_INDEX,
				PushTable.TABLE);
		String[] columns = {_ID, FIELD_CASE, FIELD_NUM, FIELD_CLICKED, FIELD_DB_INDEX};	
		String orderBy = "ASC";
		SQLiteDatabase db = mHelper.getReadableDatabase();
		Cursor c = db.rawQuery(sql, null);
//		Cursor c = db.query(TABLE, columns, null,null, null, null, orderBy);
		while (c.moveToNext()) {
			AlarmData ad = new AlarmData();
			ad.num = c.getInt(c.getColumnIndex("num"));
			ad.alarmType = c.getInt(c.getColumnIndex("push_case"));
			ad.clicked = c.getInt(c.getColumnIndex("clicked"));
			list.add(ad);
		}
		c.close();
		return list;
	}
	
	public void addPushData(AlarmData ad){
//		String sql = String.format("INSERT INTO %s(%s,%s) values(?,?);",
//				PushTable.TABLE, PushTable.FIELD_CASE,
//				PushTable.FIELD_NUM);
//		String[] args = { ""+ad.alarmType, ""+ad.num };
		SQLiteDatabase db = mHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(FIELD_CASE, ad.alarmType);
		values.put(FIELD_NUM, ad.num);
		values.put(FIELD_CLICKED, ad.clicked);
		db.insert(TABLE, null, values);
//		db.execSQL(sql, args);
		db.close();
	}
	
	public void updatePushData(AlarmData ad){
		String[] columns = {FIELD_CLICKED };	
		
		SQLiteDatabase db = mHelper.getReadableDatabase();
		String selection = FIELD_CASE + " = ? AND " + FIELD_NUM + " = ?";
		String[] args = {""+ad.alarmType, ""+ad.num};
		ContentValues values = new ContentValues();
//		values.put(FIELD_CASE, ad.alarmType);
//		values.put(FIELD_NUM, ad.num);
		values.put(FIELD_CLICKED, ad.clicked);
		db.update(TABLE, values, selection, args);
//		Cursor c = db.query(TABLE, columns, selection ,args, null, null, null);
//		return c;
	}
	
	public void clearPushTable(){
		SQLiteDatabase db = mHelper.getReadableDatabase();
		
		String selection = FIELD_CASE + " > ? OR "+ FIELD_CASE + " = ?";
		String[] args = {""+0, ""+0}; //클릭된 아이템들 삭제
		
		db.delete(TABLE, selection, args);
	}
	public void deletePushData(){
		SQLiteDatabase db = mHelper.getReadableDatabase();
		
		String selection = FIELD_CLICKED + " = ?";
		String[] args = {""+1}; //클릭된 아이템들 삭제
		
		db.delete(TABLE, selection, args);
	}
	public void deletePushData(AlarmData ad){
		SQLiteDatabase db = mHelper.getReadableDatabase();
		
		String selection = FIELD_CASE + " = ? AND " + FIELD_NUM + " = ?";
		String[] args = {""+ad.alarmType, ""+ad.num};
		
		ContentValues values = new ContentValues();
//		values.put(FIELD_CASE, ad.alarmType);
//		values.put(FIELD_NUM, ad.num);
		values.put(FIELD_CLICKED, ad.clicked);
		db.delete(TABLE, selection, args);
//		db.update(TABLE, values, selection, args);
//		Cursor c = db.query(TABLE, columns, selection ,args, null, null, null);
//		return c;
	}
	
	
	public Cursor getPushCursor() {
		String[] columns = {_ID, FIELD_CASE, FIELD_NUM, FIELD_CLICKED };		
		SQLiteDatabase db = mHelper.getReadableDatabase();
//		String selection = FIELD_CASE + " > ? AND " + FIELD_NUM + " < ?";
//		String[] args = {"20","40"};
		Cursor c = db.query(TABLE, columns, null,null, null, null, null);
		return c;
	}
	
}
