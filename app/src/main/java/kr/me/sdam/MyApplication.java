package kr.me.sdam;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class MyApplication extends Application {
	private static Context mContext;
	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
	}
	public static Context getContext() {
		return mContext;
	}

	public static String getCurrentTimeStampString(){
//		2017-01-03 19:20:01
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREAN);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		String currentDateandTime = sdf.format(new Date());
		return currentDateandTime;	//서버 시간으로 리턴
	}
	
	/**
	 * Enum used to identify the tracker that needs to be used for tracking.
	 *
	 * A single tracker is usually enough for most purposes. In case you do need multiple trackers,
	 * storing them all in Application object helps ensure that they are created only once per
	 * application instance.
	 */
	
	
	private static final String PROPERTY_ID = "UA-82788098-1";
	public enum TrackerName {
	  APP_TRACKER, // Tracker used only in this app.
	  GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
	  ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
	}

	HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
	
	public synchronized Tracker getTracker(TrackerName trackerId) {
		  if (!mTrackers.containsKey(trackerId)) {

		    GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
		    Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
		        : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker)
		            : analytics.newTracker(R.xml.ecommerse_tracker);
		    mTrackers.put(trackerId, t);

		  }
		  return mTrackers.get(trackerId);
		}
}
