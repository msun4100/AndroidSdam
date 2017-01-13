package kr.me.sdam.autologin;

import java.io.IOException;

import kr.me.sdam.MainActivity;
import kr.me.sdam.NetworkManager;
import kr.me.sdam.NetworkManager.OnResultListener;
import kr.me.sdam.PermissionUtil;
import kr.me.sdam.PropertyManager;
import kr.me.sdam.R;
import kr.me.sdam.dialogs.UsingLocationDialogFragment;
import okhttp3.Request;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class SplashActivity extends Activity{

	private final static String TAG = SplashActivity.class.getSimpleName();

	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private static final String SENDER_ID = "451565793086"; //my app key

	Handler mHandler = new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		mLM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mProvider = LocationManager.NETWORK_PROVIDER;
	} // onCreate
	
	LocationManager mLM;
	String mProvider;
//	public String latitude="37.4762397";
//	public String longitude="126.9583907";
	public String latitude="";
	public String longitude="";

	LocationListener mListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			switch (status) {
			case LocationProvider.OUT_OF_SERVICE:
			case LocationProvider.TEMPORARILY_UNAVAILABLE: {
				// GPS였으면 네트워크로 바꿔야됨
				break;
			}
			case LocationProvider.AVAILABLE:
				break;
			}
		}

		@Override
		public void onProviderEnabled(String provider) {
			// 위치정보 기능 온오프 리스너
		}

		@Override
		public void onProviderDisabled(String provider) {
			// 위치정보 기능 온오프 리스너
		}

		@Override
		public void onLocationChanged(Location location) {
			// 수신될 위치가 넘어옴
			Log.e(TAG, "onLocationChanged: location: "+location );
			latitude=String.valueOf(location.getLatitude());
			longitude=String.valueOf(location.getLongitude());
			PropertyManager.getInstance().setLatitude(latitude);
			PropertyManager.getInstance().setLongitude(longitude);
		}
	};
	
	private boolean isFirstLogin = true;
	
	@Override
	protected void onStart() {
		super.onStart();
		// 3.어떤 조건을 만족하는 프로바이더를 얻어오기 - 조건을 기술하는 클래스 == criteria
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setCostAllowed(true);
//		mProvider = mLM.getBestProvider(criteria, true);
		mProvider = LocationManager.NETWORK_PROVIDER;
		// 3-1비용이 발생해도 좋은지 여부 false면 절대 비용발생 true인 걸로 검색하지 않음
		// 3-2true면 켜져있는GPS,네트웤으로만 false면 모든장치에서 찾아옴
		// 3-3현재까지는 GPS와 네트웤 두가지 밖에 없기 때문에 일반적으로getBestProvider()호출안하고
		// 지정하는 방식 == LocationManager.NETWORK_PROVIDER 사용

//		Log.i("mProvider", mProvider.toString());
//		Log.i("isProviderEn", ""+mLM.isProviderEnabled(mProvider));
//		Log.i("Passive", ""+mProvider.equals(LocationManager.PASSIVE_PROVIDER));
		if (mProvider == null || mProvider.equals(LocationManager.PASSIVE_PROVIDER) || !mLM.isProviderEnabled(mProvider)) {
			if (isFirstLogin) {
//				PropertyManager.getInstance().getUsingLocation() == 0
				if(PropertyManager.getInstance().getUsingLocation() == 2){
					PropertyManager.getInstance().setUsingLocation(2);
				} else {
					Toast.makeText(SplashActivity.this, "계속 사용하려면 위치 정보를 켜주세요.", Toast.LENGTH_SHORT).show();
					PropertyManager.getInstance().setUsingLocation(0);	
				}
//				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//				startActivity(intent);
				isFirstLogin = false;
			} else {
				Toast.makeText(SplashActivity.this, "this app need location setting.", Toast.LENGTH_SHORT).show();
//				"37.4762397",
//				"126.9583907",
//				PropertyManager.getInstance().setLatitude("37.4762397");
//				PropertyManager.getInstance().setLongitude("126.9583907");
				finish();
			}
			return;
		} else {
			if(PropertyManager.getInstance().getUsingLocation() == 2){
				PropertyManager.getInstance().setUsingLocation(2);
			} else {
				PropertyManager.getInstance().setUsingLocation(1);	
			}
		}

		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			// only for Marshmallow version and newer versions
			Log.e(TAG, "onStart: newer versions" );
			isPermissionOK = false; //기본값이 true임. 낮은 버전위해. updateLocation에서 requestPermission호출 후에 true로 바뀜.
			PropertyManager.getInstance().setUsingLocation(0);
			updateLocation();
		} else {
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
					Log.d(TAG, "onStart: below M 1");
				}
				Log.d(TAG, "onStart: below M 2");
			} else {
				Log.d(TAG, "onStart: below M 3");
			}

			Location location = mLM.getLastKnownLocation(mProvider);
			if (location != null) {
				mListener.onLocationChanged(location);
			}
//			mLM.requestLocationUpdates(mProvider, 1000*60*60, 5.0f, mListener);// 프로바이더,2000==2초,// 5미터
			mLM.requestLocationUpdates(mProvider, 2000, 5.0f, mListener);
		}
	}	//onStart

	private boolean isPermissionOK = true;	//학교사람들에서 쓰는 변수

	private void updateLocation() {
		if (!mLM.isProviderEnabled(mProvider)) {
			return;
		}
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			requestPermission();
			return;
		}
		isPermissionOK = true;
		PropertyManager.getInstance().setUsingLocation(1); //isPerm.이랑 2개씩 세트로 true면 1 false면 0
		Location location = mLM.getLastKnownLocation(mProvider);
		if (location != null) {
			displayLocation(location);
			mListener.onLocationChanged(location);
		}
		mLM.requestLocationUpdates(mProvider, 5000, 5, mListener);
	}
	private void displayLocation(Location location) {
		Log.d(TAG, "displayLocation: \n" + "lat : " + location.getLatitude() + " lng : " + location.getLongitude() );
	}

	private static final int RC_COARSE_LOCATION = 100;
	private static final int RC_COARSE_LOCATION_ON_ACTIVITY_RESULT = 101;
	private void requestPermission() {
		ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, RC_COARSE_LOCATION);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == RC_COARSE_LOCATION) {
			if (permissions != null && permissions.length > 0) {
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				if(PermissionUtil.verifyPermissions(grantResults)){
					isPermissionOK = true;	//퍼미션 트루를 해야 넥스트액션을 할 수 있음
					PropertyManager.getInstance().setUsingLocation(1);
					updateLocation();
				} else {
					// 권한을 얻지 못했다. Show Rational Dialog
					isPermissionOK = false;  //퍼미션 트루를 해야 넥스트액션을 할 수 있음
					PropertyManager.getInstance().setUsingLocation(0);
					Context mContext = SplashActivity.this;
					String message = PermissionUtil.getRationalMessage(mContext, PermissionUtil.PERMISSION_LOCATION);
//                    PermissionUtil.showRationalDialog(mContext, message); //이걸로 호출하면 ForResult를 못함.
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle("");
					builder.setMessage(message);
					builder.setCancelable(false);
					builder.setNegativeButton(getString(R.string.word_close), new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							PropertyManager.getInstance().setUsingLocation(0);
							Log.d(TAG, "onClick: Alert cancel");
						}
					});
					builder.setPositiveButton(getString(R.string.word_settings), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							try {
								Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + getPackageName()));
								startActivityForResult(intent, RC_COARSE_LOCATION_ON_ACTIVITY_RESULT);
							} catch (ActivityNotFoundException e) {
								e.printStackTrace();
								Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
								startActivityForResult(intent, RC_COARSE_LOCATION_ON_ACTIVITY_RESULT);
							}
						}
					});
					builder.create().show();
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "onActivityResult: resultCode " + resultCode + " requestCode  "+ requestCode );
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case RC_COARSE_LOCATION_ON_ACTIVITY_RESULT:
					int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
					if(permissionCheck== PackageManager.PERMISSION_DENIED){
						// 권한 없음
						isPermissionOK = false;
						PropertyManager.getInstance().setUsingLocation(0);
					}else{
						// 권한 있음
						isPermissionOK = true;
						PropertyManager.getInstance().setUsingLocation(1);
						updateLocation();
					}
					break;
			}
		}
	}


	@Override
	protected void onStop() {
		super.onStop();
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			mLM.removeUpdates(mListener);
			return;
		}
		mLM.removeUpdates(mListener);
	}
	//=========위치정보 세팅===========

	boolean mIsFirst = true;

	@Override
	protected void onResume() {
		super.onResume();
//		Toast.makeText(SplashActivity.this, "onresume", Toast.LENGTH_SHORT).show();
		if (checkPlayServices()) {
			String regId = PropertyManager.getInstance().getRegistrationId();
			if (!regId.equals("")) {
				if(isPermissionOK == true){
					runOnUiThread(nextAction);
				}
			} else {
				registerInBackground();
			}
		} else {
			if (mIsFirst) {
				mIsFirst = false;
			} else {
				// Toast...
				Toast.makeText(SplashActivity.this, "onResume GCM else clause", Toast.LENGTH_SHORT).show();
			}
		}
	}

	Runnable nextAction = new Runnable() {

		@Override
		public void run() {
//			mLM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//			mProvider = LocationManager.NETWORK_PROVIDER;
			
			String userId = PropertyManager.getInstance().getUserId();
			if (!userId.equals("")) {
				String password = PropertyManager.getInstance().getPassword();
				NetworkManager.getInstance().putSdamLogin(SplashActivity.this, userId,password,
//						"37.4762397",
//						"126.9583907",
						PropertyManager.getInstance().getLatitude(),
						PropertyManager.getInstance().getLongitude(),
						new OnResultListener<LoginInfo>() {
							@Override
							public void onSuccess(Request request, LoginInfo result) {
								Intent intent = new Intent(SplashActivity.this, MainActivity.class);
								startActivity(intent);
//								Toast.makeText(SplashActivity.this, PropertyManager.getInstance().getRegistrationId(), Toast.LENGTH_SHORT).show();
								finish();
							}

							@Override
							public void onFailure(Request request, int code, Throwable cause) {
								Toast.makeText(SplashActivity.this, "서버에 연결할 수 없습니다. #" + code, Toast.LENGTH_LONG).show();
							}
						});
			} else {
				mHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						Intent intent = new Intent(SplashActivity.this, SplashBActivity.class);
						startActivity(intent);
						finish();
					}
				}, 2000);
			}	
		} //run()
	};

	GoogleCloudMessaging gcm;

	private void registerInBackground() {
		new AsyncTask<Void, Integer, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging
								.getInstance(SplashActivity.this);
					}
					String regid = gcm.register(SENDER_ID);
					Log.i(TAG, "doInBackground: regid: "+regid );
					PropertyManager.getInstance().setRegistrationId(regid);

				} catch (IOException ex) {
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				if(isPermissionOK == true){
					runOnUiThread(nextAction);
				}

			}
		}.execute(null, null, null);
	}

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
						resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST);
				dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {

					}
				});
				dialog.show();
			} else {
				// To Do...
				finish();
			}
			return false;
		}
		return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
