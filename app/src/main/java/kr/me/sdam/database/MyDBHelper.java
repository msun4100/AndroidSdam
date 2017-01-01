package kr.me.sdam.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "mydb";
	private static final int DB_VERSION = 3;
	
	public MyDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE "+DBConstant.PushTable.TABLE+"("
                + DBConstant.PushTable._ID+" integer PRIMARY KEY autoincrement, " 
                + DBConstant.PushTable.FIELD_NUM+" integer, "
                + DBConstant.PushTable.FIELD_CASE+" integer, "
                + DBConstant.PushTable.FIELD_CLICKED+" integer, "
                + DBConstant.PushTable.FIELD_DB_INDEX+" integer);" ;
		db.execSQL(sql);
	}
//create table pushTbl(
//_ID integer primary key autoincrement,
//num integer,
//case integer);
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
