package kr.me.sdam;

import android.content.Context;
import android.content.SharedPreferences;

public class PropertyManager {
	private static PropertyManager instance;

	public static PropertyManager getInstance() {
		if (instance == null) {
			instance = new PropertyManager();
		}
		return instance;
	}

	public void clearProperties() {
		// des의 참조값을 없애서 가비지컬렉션의 대상이 되게 한다
		instance = null;
		// 가비지컬렉션 호출
		System.gc();
		// [출처] JAVA 소멸자|작성자 wofmaker
	}

	private static final String PREF_NAME = "mypref";
	SharedPreferences mPrefs;
	SharedPreferences.Editor mEditor;

	private PropertyManager() {
		mPrefs = MyApplication.getContext().getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		mEditor = mPrefs.edit();
	}

	private static final String FIELD_USER_ID = "userId";
	private String mUserId;

	public String getUserId() {
		if (mUserId == null) {
			mUserId = mPrefs.getString(FIELD_USER_ID, "");
		}
		return mUserId;
	}

	public void setUserId(String userId) {
		mUserId = userId;
		mEditor.putString(FIELD_USER_ID, userId);
		mEditor.commit();
	}

	private static final String FIELD_PASSWORD = "pw";
	private String mPW;

	public String getPassword() {
		if (mPW == null) {
			mPW = mPrefs.getString(FIELD_PASSWORD, "");
		}
		return mPW;
	}

	public void setPassword(String pw) {
		mPW = pw;
		mEditor.putString(FIELD_PASSWORD, pw);
		mEditor.commit();
	}

	private static final String FIELD_SEX_MALE = "1";
	private static final String FIELD_SEX_FEMALE = "2";
	private int mSex;

	public int getSex() {
		if (mSex == 0) {
			mSex = mPrefs.getInt(FIELD_SEX_MALE, 0);
		}
		return mSex;
	}

	public void setSex(int sex) {
		switch (sex) {
		case 1:
			mSex = sex;
			mEditor.putInt(FIELD_SEX_MALE, sex);
			mEditor.commit();
			break;
		case 2:
			mSex = sex;
			mEditor.putInt(FIELD_SEX_FEMALE, sex);
			mEditor.commit();
		}
	}

	private static final String FIELD_BIRTH_YEAR = "birth";
	private int mBirth;

	public int getBirth() {
		if (mBirth == 0) {
			mBirth = mPrefs.getInt(FIELD_BIRTH_YEAR, 0);
		}
		return mBirth;
	}

	public void setBirth(int birth) {
		mBirth = birth;
		mEditor.putInt(FIELD_BIRTH_YEAR, birth);
		mEditor.commit();
	}

	private static final String FIELD_PUSH_ID = "pushId";
	private String mPushId;

	public String getPushId() {
		if (mPushId == null) {
			mPushId = mPrefs.getString(FIELD_PUSH_ID, "");
		}
		return mPushId;
	}

	public void setPushId(String pushId) {
		mPushId = pushId;
		mEditor.putString(FIELD_PUSH_ID, pushId);
		mEditor.commit();
	}

	// ===========signupinfo============
	private static final String FIELD_ALARM = "alarm";
	private int mAlarm;

	public int getAlarm() {
		if (mAlarm == 0) {
			mAlarm = mPrefs.getInt(FIELD_ALARM, 1);
		}
		return mAlarm;
	}

	public void setAlarm(int alarm) {
		mAlarm = alarm;
		mEditor.putInt(FIELD_ALARM, alarm);
		mEditor.commit();
	}

	private static final String FIELD_FEED_MAX_AGE = "maxage";
	private int mMaxAge;

	public int getMaxAge() {
		if (mMaxAge == 0) {
			mMaxAge = mPrefs.getInt(FIELD_FEED_MAX_AGE, 100);
		}
		return mMaxAge;
	}

	public void setMaxAge(int age) {
		mMaxAge = age;
		mEditor.putInt(FIELD_FEED_MAX_AGE, age);
		mEditor.commit();
	}

	private static final String FIELD_FEED_MIN_AGE = "minage";
	private int mMinAge;

	public int getMinAge() {
		if (mMinAge == 0) {
			mMinAge = mPrefs.getInt(FIELD_FEED_MIN_AGE, 0);
		}
		return mMinAge;
	}

	public void setMinAge(int age) {
		mMinAge = age;
		mEditor.putInt(FIELD_FEED_MIN_AGE, age);
		mEditor.commit();
	}

	private static final String FIELD_FEED_DISTANCE = "distance";
	private int mDistance;

	public int getDistance() {
		if (mDistance == 0) {
			mDistance = mPrefs.getInt(FIELD_FEED_DISTANCE, 200);
		}
		return mDistance;
	}

	public void setDistance(int distance) {
		mDistance = distance;
		mEditor.putInt(FIELD_FEED_DISTANCE, distance);
		mEditor.commit();
	}

	// ==============setting info===============
	// int latitude;
	// int longitude;
	private static final String FIELD_LOCATION_LATITUDE = "latitude";
	private String mLatitude;

	public String getLatitude() {
		if (mLatitude == null) {
			mLatitude = mPrefs.getString(FIELD_LOCATION_LATITUDE, null);
		}
		return mLatitude;
	}

	public void setLatitude(String latitude) {
		mLatitude = latitude;
		mEditor.putString(FIELD_LOCATION_LATITUDE, latitude);
		mEditor.commit();
	}

	private static final String FIELD_LOCATION_LONGITUDE = "longitude";
	private String mLongitude;

	public String getLongitude() {
		if (mLongitude == null) {
			mLongitude = mPrefs.getString(FIELD_LOCATION_LONGITUDE, null);
		}
		return mLongitude;
	}

	public void setLongitude(String longitude) {
		mLongitude = longitude;
		mEditor.putString(FIELD_LOCATION_LONGITUDE, longitude);
		mEditor.commit();
	}

	// ================Location

	private static final String FIELD_REGISTRATION_ID = "regid";
	private String regid;

	public void setRegistrationId(String regid) {

		this.regid = regid;
		mEditor.putString(FIELD_REGISTRATION_ID, regid);
		mEditor.commit();
	}

	public String getRegistrationId() {
		if (regid == null) {
			regid = mPrefs.getString(FIELD_REGISTRATION_ID, "");
		}
		return regid;
	}

	// ======================
	private static final String FIELD_LOCK = "LOCK";
	private int mLock;
	public int getLock() {
		if (mLock == 0) {
			mLock = mPrefs.getInt(FIELD_LOCK, 0);
		}
		return mLock;
	}

	public void setLock(int lock) {
		mLock = lock;
		mEditor.putInt(FIELD_LOCK, lock);
		mEditor.commit();
	}

	private static final String FIELD_LOCK_PASSWORD = "lockpassword";
	private String mLockPassword;

	public String getLockPassword() {
		if (mLockPassword == null) {
			mLockPassword = mPrefs.getString(FIELD_LOCK_PASSWORD, "");
		}
		return mLockPassword;
	}

	public void setLockPassword(String lockPassword) {
		mLockPassword = lockPassword;
		mEditor.putString(FIELD_LOCK_PASSWORD, lockPassword);
		mEditor.commit();
	}
	
	private static final String FIELD_FIRST_VISIT = "firstvisit";
	private int mVisit;

	public int getFirstVisit() {
		if (mVisit == 0) {
			mVisit = mPrefs.getInt(FIELD_FIRST_VISIT, 0);
		}
		return mVisit;
	}

	public void setFirstVisit(int visit) {
		mVisit = visit;
		mEditor.putInt(FIELD_FIRST_VISIT, visit);
		mEditor.commit();
	}
	
	private static final String FIELD_USING_LOCATION = "usinglocation";
	private int mUsingLocation; // 0안씀 1사용 2다시보지않기

	public int getUsingLocation() {
		if (mUsingLocation == 0) {
			mUsingLocation = mPrefs.getInt(FIELD_USING_LOCATION, 0);
		}
		return mUsingLocation;
	}

	public void setUsingLocation(int usingLocation) {
		mUsingLocation = usingLocation;
		mEditor.putInt(FIELD_USING_LOCATION, usingLocation);
		mEditor.commit();
	}
	
}
