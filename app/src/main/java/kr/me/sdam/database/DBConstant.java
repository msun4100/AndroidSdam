package kr.me.sdam.database;

import android.provider.BaseColumns;

public class DBConstant {
	public interface PushTable extends BaseColumns {
		public static final String TABLE = "PushTbl";
		public static final String FIELD_CASE = "push_case";
		public static final String FIELD_NUM = "num";
		public static final String FIELD_CLICKED = "clicked";
		public static final String FIELD_DB_INDEX ="dbindex";
	}
}
