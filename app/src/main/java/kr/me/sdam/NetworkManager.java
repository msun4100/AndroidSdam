package kr.me.sdam;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import kr.me.sdam.autologin.EmailAuthInfo;
import kr.me.sdam.autologin.EmailSendInfo;
import kr.me.sdam.autologin.FindPWInfo;
import kr.me.sdam.autologin.LoginInfo;
import kr.me.sdam.autologin.RegisterInfo;
import kr.me.sdam.autologin.RegisterInfoData;
import kr.me.sdam.detail.Detail1Info;
import kr.me.sdam.detail.good.CancelGReplyInfo;
import kr.me.sdam.detail.good.GoodReplyInfo;
import kr.me.sdam.detail.reply.DelReplyInfo;
import kr.me.sdam.detail.reply.ReplyInfo;
import kr.me.sdam.good.GoodCancelInfo;
import kr.me.sdam.good.GoodInfo;
import kr.me.sdam.mypage.SettingInfo;
import kr.me.sdam.mypage.WithdrawInfo;
import kr.me.sdam.mypage.calendar.MyCalendarInfo;
import kr.me.sdam.mypage.favor.Favor1Info;
import kr.me.sdam.mypage.mylist.MyList0Hide;
import kr.me.sdam.mypage.mylist.MyList1Info;
import kr.me.sdam.report.ABanInfo;
import kr.me.sdam.report.ReportInfo;
import kr.me.sdam.report.ReportReplyInfo;
import kr.me.sdam.report.WBanInfo;
import kr.me.sdam.search.SearchInfo;
import kr.me.sdam.tabone.TabOneInfo;
import kr.me.sdam.tabthree.TabThreeInfo;
import kr.me.sdam.tabtwo.TabTwoInfo;
import kr.me.sdam.write.ArticleInfo;
import kr.me.sdam.write.ArticleParams;
import kr.me.sdam.write.DelArticleInfo;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Dispatcher;
import okhttp3.JavaNetCookieJar;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkManager {
	private static NetworkManager instance;
	public static NetworkManager getInstance() {
		if (instance == null) {
			instance = new NetworkManager();
		}
		return instance;
	}

	OkHttpClient mClient;
	private static final int MAX_CACHE_SIZE = 10 * 1024 * 1024;

	private NetworkManager() {
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		Context context = MyApplication.getContext();
		File cachefile = new File(context.getExternalCacheDir(), "mycache");
		if (!cachefile.exists()) {
			cachefile.mkdirs();
		}
		Cache cache = new Cache(cachefile, MAX_CACHE_SIZE);
		builder.cache(cache);

		CookieManager cookieManager = new CookieManager(new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL);
		builder.cookieJar(new JavaNetCookieJar(cookieManager));

//        disableCertificateValidation(context, builder);	// https 요청할때 주석 풀고 testSSL 요청

		mClient = builder.build();
	}

	static void disableCertificateValidation(Context context, OkHttpClient.Builder builder) {

		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			InputStream caInput = context.getResources().openRawResource(R.raw.ssdam);
			Certificate ca;
			try {
				ca = cf.generateCertificate(caInput);
				System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
			} finally {
				caInput.close();
			}
			String keyStoreType = KeyStore.getDefaultType();
			KeyStore keyStore = KeyStore.getInstance(keyStoreType);
			keyStore.load(null, null);
			keyStore.setCertificateEntry("ca", ca);

			String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
			tmf.init(keyStore);

			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, tmf.getTrustManagers(), null);
			HostnameVerifier hv = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};
			sc.init(null, tmf.getTrustManagers(), null);
			builder.sslSocketFactory(sc.getSocketFactory());
			builder.hostnameVerifier(hv);
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void cancelAll() {
		mClient.dispatcher().cancelAll();
	}

	public void cancelTag(Object tag) {
		Dispatcher dispatcher = mClient.dispatcher();
		List<Call> calls = dispatcher.queuedCalls();
		for (Call call : calls) {
			if (call.request().tag().equals(tag)) {
				call.cancel();
			}
		}
		calls = dispatcher.runningCalls();
		for (Call call : calls) {
			if (call.request().tag().equals(tag)) {
				call.cancel();
			}
		}
	}

	public interface OnResultListener<T> {
		public void onSuccess(Request request, T result);

		public void onFailure(Request request, int code, Throwable cause);
	}

	private static final int MESSAGE_SUCCESS = 0;
	private static final int MESSAGE_FAILURE = 1;

	static class NetworkHandler extends Handler {
		public NetworkHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			CallbackObject object = (CallbackObject) msg.obj;
			Request request = object.request;
			OnResultListener listener = object.listener;
			switch (msg.what) {
				case MESSAGE_SUCCESS:
					listener.onSuccess(request, object.result);
					break;
				case MESSAGE_FAILURE:
					listener.onFailure(request, -1, object.exception);
					break;
			}
		}
	}

	Handler mHandler = new NetworkHandler(Looper.getMainLooper());

	static class CallbackObject<T> {
		Request request;
		T result;
		IOException exception;
		OnResultListener<T> listener;
	}

	public void cancelAll(Object tag) {

	}

	private static final MediaType CONTENT_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
//    private static final String SERVER_URL = "http://10.0.3.2:3000";
	private static final String SERVER = "http://52.78.114.166:3000";

	public Request testSSL(Context context, final OnResultListener<String> listener) {
		Request request = new Request.Builder().url("https://52.78.114.166:443").build();
		final CallbackObject<String> callbackObject = new CallbackObject<String>();

		callbackObject.request = request;
		callbackObject.listener = listener;
		mClient.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				callbackObject.exception = e;
				Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
				mHandler.sendMessage(msg);
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				callbackObject.result = response.body().string();
				Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
				mHandler.sendMessage(msg);
			}
		});

		return request;

	}

	public static final String REGISTER_URL = SERVER + "/register";
	public Request putSdamRegister(Context context, RegisterInfoData userData, final OnResultListener<RegisterInfo> listener) {
		try {
//            String url = String.format(URL_LOGIN, URLEncoder.encode(keyword, "utf-8")); //get method
			String url = REGISTER_URL;
			final CallbackObject<RegisterInfo> callbackObject = new CallbackObject<RegisterInfo>();

			JsonObject json = new JsonObject();
			json.addProperty("userId", userData.userId);
			json.addProperty("pw", userData.pw);
			json.addProperty("birth", userData.birth);
			json.addProperty("sex", userData.sex);
			json.addProperty("pushId", PropertyManager.getInstance().getRegistrationId());

			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json")
					.post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					RegisterInfo result = gson.fromJson(response.body().charStream(), RegisterInfo.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});

			return request;

		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static final String LOGIN_URL = SERVER + "/login";
	public Request putSdamLogin(Context context, String userId, String pw, String latitude, String logitude,
									   final OnResultListener<LoginInfo> listener) {
		try {
			String url = LOGIN_URL;
			final CallbackObject<LoginInfo> callbackObject = new CallbackObject<LoginInfo>();

			JsonObject json = new JsonObject();
			json.addProperty("userId", ""+userId);
			json.addProperty("pw", ""+pw);
			json.addProperty("latitude", ""+latitude);
			json.addProperty("logitude", ""+logitude);
			json.addProperty("pushId", PropertyManager.getInstance().getRegistrationId());

			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json")
					.post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					LoginInfo result = gson.fromJson(response.body().charStream(), LoginInfo.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});
			return request;
		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static final String EMAIL_SEND_URL = SERVER + "/emailsend";
	public Request putSdamEmailSend(Context context, String userId,
								final OnResultListener<EmailSendInfo> listener) {
		try {
			String url = LOGIN_URL;
			final CallbackObject<EmailSendInfo> callbackObject = new CallbackObject<EmailSendInfo>();

			JsonObject json = new JsonObject();
			json.addProperty("userId", ""+userId);

			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json")
					.post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					EmailSendInfo result = gson.fromJson(response.body().charStream(), EmailSendInfo.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});
			return request;
		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static final String EMAIL_AUTH_URL = SERVER + "emailauth";
	public Request getSdamEmailAuth(Context context, String userId,
									final OnResultListener<EmailAuthInfo> listener) {
		try {
			String url = EMAIL_AUTH_URL;
			final CallbackObject<EmailAuthInfo> callbackObject = new CallbackObject<EmailAuthInfo>();

			JsonObject json = new JsonObject();

			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json")
//					.post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					EmailAuthInfo result = gson.fromJson(response.body().charStream(), EmailAuthInfo.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});
			return request;
		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}


	public static final String TAB_ONE_URL = SERVER + "/around/";
	public Request getSdamAround(Context context, String keyword, final OnResultListener<TabOneInfo> listener) {
		try {
			String url = TAB_ONE_URL;
			final CallbackObject<TabOneInfo> callbackObject = new CallbackObject<TabOneInfo>();

			JsonObject json = new JsonObject();
			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json") //API전용 헤더인듯?
//                    .post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					TabOneInfo result = gson.fromJson(response.body().charStream(), TabOneInfo.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});
			return request;
		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static final String TAB_TWO_URL = SERVER + "/issue/";
	public Request getSdamIssue(Context context, String  start, final OnResultListener<TabTwoInfo> listener) {
		try {
			String url = TAB_TWO_URL;
			final CallbackObject<TabTwoInfo> callbackObject = new CallbackObject<TabTwoInfo>();

			JsonObject json = new JsonObject();
			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json") //API전용 헤더인듯?
//                    .post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					TabTwoInfo result = gson.fromJson(response.body().charStream(), TabTwoInfo.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});
			return request;
		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static final String TAB_THREE_URL = SERVER + "/new/:requestNum";
	public Request getSdamNew(Context context, String requestNum, final OnResultListener<TabThreeInfo> listener) {
		try {
			String url = TAB_THREE_URL.replace(":requestNum", requestNum);
			final CallbackObject<TabThreeInfo> callbackObject = new CallbackObject<TabThreeInfo>();

			JsonObject json = new JsonObject();
			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json") //API전용 헤더인듯?
//                    .post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					TabThreeInfo result = gson.fromJson(response.body().charStream(), TabThreeInfo.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});
			return request;
		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static final String READ_URL = SERVER + "/read/:num";
	public Request getSdamRead(Context context, int num, final OnResultListener<Detail1Info> listener) {
		try {
			String url = READ_URL.replace(":num", ""+num);
			final CallbackObject<Detail1Info> callbackObject = new CallbackObject<Detail1Info>();

			JsonObject json = new JsonObject();
			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json")
//                    .post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					Detail1Info result = gson.fromJson(response.body().charStream(), Detail1Info.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});
			return request;
		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static final String ARTICLE_URL = SERVER + "/article";
	public Request putSdamArticle(Context context, ArticleParams ap, final OnResultListener<ArticleInfo> listener) {
		try {
			String url = ARTICLE_URL;
			final CallbackObject<ArticleInfo> callbackObject = new CallbackObject<ArticleInfo>();

			JsonObject json = new JsonObject();
			json.addProperty("userId", ap.userId);
			json.addProperty("content", ap.content);
			json.addProperty("age", ap.age);
			json.addProperty("category", ap.category);
			json.addProperty("image", ap.image);
			json.addProperty("emotion", ap.emotion);
			json.addProperty("lock",""+ap.lock);
			json.addProperty("unlocate", ""+ap.unlocate);

			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json")
					.post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					ArticleInfo result = gson.fromJson(response.body().charStream(), ArticleInfo.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});
			return request;
		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static final String DEL_ARTICLE_URL = SERVER + "/delarticle";
	public Request putSdamDelArticle(Context context, int num, final OnResultListener<DelArticleInfo> listener) {
		try {
			String url = DEL_ARTICLE_URL;
			final CallbackObject<DelArticleInfo> callbackObject = new CallbackObject<DelArticleInfo>();

			JsonObject json = new JsonObject();
			json.addProperty("num", ""+num);

			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json")
					.post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					DelArticleInfo result = gson.fromJson(response.body().charStream(), DelArticleInfo.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});
			return request;
		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static final String REPLY_URL = SERVER + "/reply";
	public Request putSdamArticleReply(Context context,
									   int num, String content, String nickname, String writer,
									   String [] nicks, String[] ids, final OnResultListener<ReplyInfo> listener) {
		try {
			String url = REPLY_URL;
			final CallbackObject<ReplyInfo> callbackObject = new CallbackObject<ReplyInfo>();

			JsonObject json = new JsonObject();
			json.addProperty("num", ""+num);
			json.addProperty("content", ""+content);
			json.addProperty("nickname", ""+nickname);
			json.addProperty("writer", ""+writer);
			if(nicks != null && nicks.length > 0){
				json.addProperty("tagNicks", ""+nicks);
			}
			if(ids != null && ids.length > 0){
				json.addProperty("tagIds", ""+ids);
			}

			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json")
					.post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					ReplyInfo result = gson.fromJson(response.body().charStream(), ReplyInfo.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});
			return request;
		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static final String GOOD_URL = SERVER + "/good";
	public Request putSdamGood(Context context,
									   String num, String writer, final OnResultListener<GoodInfo> listener) {
		try {
			String url = GOOD_URL;
			final CallbackObject<GoodInfo> callbackObject = new CallbackObject<GoodInfo>();

			JsonObject json = new JsonObject();
			json.addProperty("num", ""+num);
			json.addProperty("writer", ""+writer);

			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json")
					.post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					GoodInfo result = gson.fromJson(response.body().charStream(), GoodInfo.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});
			return request;
		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static final String CANCEL_GOOD_URL = SERVER + "/cancelgood";
	public Request putSdamGoodCancel(Context context,
							   String num, final OnResultListener<GoodCancelInfo> listener) {
		try {
			String url = CANCEL_GOOD_URL;
			final CallbackObject<GoodCancelInfo> callbackObject = new CallbackObject<GoodCancelInfo>();

			JsonObject json = new JsonObject();
			json.addProperty("num", ""+num);

			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json")
					.post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					GoodCancelInfo result = gson.fromJson(response.body().charStream(), GoodCancelInfo.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});
			return request;
		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static final String GOOD_REPLY_URL = SERVER + "/goodreply";
	public Request putSdamGoodReply(Context context, int num, int repNum, String repAuthor,
									 final OnResultListener<GoodReplyInfo> listener) {
		try {
			String url = GOOD_REPLY_URL;
			final CallbackObject<GoodReplyInfo> callbackObject = new CallbackObject<GoodReplyInfo>();

			JsonObject json = new JsonObject();
			json.addProperty("num", ""+num);
			json.addProperty("repNum", ""+repNum);
			json.addProperty("repAuthor", ""+repAuthor);

			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json")
					.post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					GoodReplyInfo result = gson.fromJson(response.body().charStream(), GoodReplyInfo.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});
			return request;
		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static final String CANCEL_G_REPLY_URL = SERVER + "/cancelgreply";
	public Request putSdamCancelGoodReply(Context context, int num, int repNum,
									final OnResultListener<CancelGReplyInfo> listener) {
		try {
			String url = CANCEL_G_REPLY_URL;
			final CallbackObject<CancelGReplyInfo> callbackObject = new CallbackObject<CancelGReplyInfo>();

			JsonObject json = new JsonObject();
			json.addProperty("num", ""+num);
			json.addProperty("repNum", ""+repNum);

			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json")
					.post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					CancelGReplyInfo result = gson.fromJson(response.body().charStream(), CancelGReplyInfo.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});
			return request;
		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}


	public static final String REPORT_URL = SERVER + "/report";
	public Request putSdamReport(Context context, int num, int category, String writer,
										  final OnResultListener<ReportInfo> listener) {
		try {
			String url = REPORT_URL;
			final CallbackObject<ReportInfo> callbackObject = new CallbackObject<ReportInfo>();

			JsonObject json = new JsonObject();
			json.addProperty("num", ""+num);
			json.addProperty("category", ""+category);
			json.addProperty("writer", ""+writer);

			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json")
					.post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					ReportInfo result = gson.fromJson(response.body().charStream(), ReportInfo.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});
			return request;
		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static final String REPORT_REPLY_URL = SERVER + "/reportreply";
	public Request putSdamReportReply(Context context, int num, int repNum, int userId,
								 final OnResultListener<ReportReplyInfo> listener) {
		try {
			String url = REPORT_REPLY_URL;
			final CallbackObject<ReportReplyInfo> callbackObject = new CallbackObject<ReportReplyInfo>();

			JsonObject json = new JsonObject();
			json.addProperty("num", ""+num);
			json.addProperty("repNum", ""+repNum);
			json.addProperty("userId", ""+userId);

			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json")
					.post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					ReportReplyInfo result = gson.fromJson(response.body().charStream(), ReportReplyInfo.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});
			return request;
		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	//이 게시글 안보기
	public static final String ABAN_URL = SERVER + "/aban";
	public Request putSdamABan(Context context, int num, String writer,
									  final OnResultListener<ABanInfo> listener) {
		try {
			String url = ABAN_URL;
			final CallbackObject<ABanInfo> callbackObject = new CallbackObject<ABanInfo>();

			JsonObject json = new JsonObject();
			json.addProperty("num", ""+num);
			json.addProperty("writer", ""+writer);

			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json")
					.post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					ABanInfo result = gson.fromJson(response.body().charStream(), ABanInfo.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});
			return request;
		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	//이 사용자의 게시물 안 보기
	public static final String WBAN_URL = SERVER + "/wban";
	public Request putSdamWBan(Context context, int num, String writer,
							   final OnResultListener<WBanInfo> listener) {
		try {
			String url = WBAN_URL;
			final CallbackObject<WBanInfo> callbackObject = new CallbackObject<WBanInfo>();

			JsonObject json = new JsonObject();
			json.addProperty("num", ""+num);
			json.addProperty("writer", ""+writer);

			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json")
					.post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					WBanInfo result = gson.fromJson(response.body().charStream(), WBanInfo.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});
			return request;
		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	//내가 쓴 댓글 삭제
	public static final String DEL_REPLY_URL = SERVER + "/delreply";
	public Request putSdamDelReply(Context context, int num, int repNum,
							   final OnResultListener<DelReplyInfo> listener) {
		try {
			String url = DEL_REPLY_URL;
			final CallbackObject<DelReplyInfo> callbackObject = new CallbackObject<DelReplyInfo>();

			JsonObject json = new JsonObject();
			json.addProperty("num", ""+num);
			json.addProperty("repNum", ""+repNum);

			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json")
					.post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					DelReplyInfo result = gson.fromJson(response.body().charStream(), DelReplyInfo.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});
			return request;
		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	//	/distance 거리 정보 변경 요청
	public static final String DISTANCE_URL = SERVER + "/distance";
	public Request putSdamDistance(Context context, int distance,
								   final OnResultListener<DistanceInfo> listener) {
		try {
			String url = DISTANCE_URL;
			final CallbackObject<DistanceInfo> callbackObject = new CallbackObject<DistanceInfo>();

			JsonObject json = new JsonObject();
			json.addProperty("distance", ""+distance);

			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json")
					.post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					DistanceInfo result = gson.fromJson(response.body().charStream(), DistanceInfo.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});
			return request;
		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static final String MY_LIST_URL = SERVER + "/mylist/:requestNum";
	public Request getSdamMyList(Context context, int requestNum,
								 final OnResultListener<MyList1Info> listener) {
		try {
			String url = MY_LIST_URL.replace(":requestNum", ""+requestNum);
			final CallbackObject<MyList1Info> callbackObject = new CallbackObject<MyList1Info>();

			JsonObject json = new JsonObject();
			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json")
//                    .post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					MyList1Info result = gson.fromJson(response.body().charStream(), MyList1Info.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});
			return request;
		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}


	public static final String MY_FAVOR_URL = SERVER + "/myfavor/:requestNum";
	public Request getSdamMyFavor(Context context, int requestNum,
								 final OnResultListener<Favor1Info> listener) {
		try {
			String url = MY_FAVOR_URL.replace(":requestNum", ""+requestNum);
			final CallbackObject<Favor1Info> callbackObject = new CallbackObject<Favor1Info>();

			JsonObject json = new JsonObject();
			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json")
//                    .post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					Favor1Info result = gson.fromJson(response.body().charStream(), Favor1Info.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});
			return request;
		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static final String MY_CALENDAR_URL = SERVER + "/mycalendar/:date/";
	public Request getSdamCalendar(Context context, String date,
								  final OnResultListener<MyCalendarInfo> listener) {
		try {
			String url = MY_CALENDAR_URL.replace(":date", ""+date);
			final CallbackObject<MyCalendarInfo> callbackObject = new CallbackObject<MyCalendarInfo>();

			JsonObject json = new JsonObject();
			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json")
//                    .post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					MyCalendarInfo result = gson.fromJson(response.body().charStream(), MyCalendarInfo.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});
			return request;
		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static final String MY_DAY_URL = SERVER + "/myday/:date/";
	public Request getSdamMyDay(Context context, String date, //date == 20150203
									 final OnResultListener<MyList1Info> listener) {
		try {
			String url = MY_DAY_URL.replace(":date", ""+date);
			final CallbackObject<MyList1Info> callbackObject = new CallbackObject<MyList1Info>();

			JsonObject json = new JsonObject();
			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json")
//                    .post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					MyList1Info result = gson.fromJson(response.body().charStream(), MyList1Info.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});
			return request;
		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static final String MY_EMOTION = SERVER + "/myemotion/:emotion/:requestPageNum";///myemotion/#/1
	public Request getSdamMyEmotion(Context context, String emotion, int requestPageNum, //date == 20150203
								final OnResultListener<MyList1Info> listener) {
		try {
			String url = MY_EMOTION.replace(":emotion", ""+emotion).replace(":requestPageNum", ""+requestPageNum);
			final CallbackObject<MyList1Info> callbackObject = new CallbackObject<MyList1Info>();

			JsonObject json = new JsonObject();
			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json")
//                    .post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					MyList1Info result = gson.fromJson(response.body().charStream(), MyList1Info.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});
			return request;
		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static final String MY_SEARCH = SERVER + "/contentSearch/:contentValue/:num";///myemotion/#/1
	public Request getSdamSearch(Context context, String contentValue, int num, //date == 20150203
									final OnResultListener<SearchInfo> listener) {
		try {
			String url = MY_SEARCH.replace(":contentValue", ""+contentValue).replace(":num", ""+num);
			final CallbackObject<SearchInfo> callbackObject = new CallbackObject<SearchInfo>();

			JsonObject json = new JsonObject();
			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json")
//                    .post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					SearchInfo result = gson.fromJson(response.body().charStream(), SearchInfo.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});
			return request;
		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static final String EXIT_URL = SERVER + "/exit";
	public Request putSdamExit(Context context,
								   final OnResultListener<GoodInfo> listener) {
		try {
			String url = EXIT_URL;
			final CallbackObject<GoodInfo> callbackObject = new CallbackObject<GoodInfo>();

			JsonObject json = new JsonObject();

			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json")
					.post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					GoodInfo result = gson.fromJson(response.body().charStream(), GoodInfo.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});
			return request;
		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static final String SET_URL = SERVER + "/set";
	public Request putSdamSet(Context context, String startAge, String endAge, String distance,
							   final OnResultListener<SettingInfo> listener) {
		try {
			String url = SET_URL;
			final CallbackObject<SettingInfo> callbackObject = new CallbackObject<SettingInfo>();

			JsonObject json = new JsonObject();
			json.addProperty("startAge", ""+startAge);
			json.addProperty("endAge", ""+endAge);
			json.addProperty("distance", ""+distance);

			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json")
					.post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					SettingInfo result = gson.fromJson(response.body().charStream(), SettingInfo.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});
			return request;
		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static final String HIDE_URL = SERVER + "/myhide";
	public Request putSdamHide(Context context, int num,int locked,
							  final OnResultListener<MyList0Hide> listener) {
		try {
			String url = HIDE_URL;
			final CallbackObject<MyList0Hide> callbackObject = new CallbackObject<MyList0Hide>();

			JsonObject json = new JsonObject();
			json.addProperty("num", ""+num);
			json.addProperty("locked", ""+locked);

			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json")
					.post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					MyList0Hide result = gson.fromJson(response.body().charStream(), MyList0Hide.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});
			return request;
		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static final String WITHDRAW_URL = SERVER + "/withdraw";
	public Request putSdamWithdraw(Context context, String password,
							   final OnResultListener<WithdrawInfo> listener) {
		try {
			String url = WITHDRAW_URL;
			final CallbackObject<WithdrawInfo> callbackObject = new CallbackObject<WithdrawInfo>();

			JsonObject json = new JsonObject();
			json.addProperty("pw", ""+password);

			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json")
					.post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					WithdrawInfo result = gson.fromJson(response.body().charStream(), WithdrawInfo.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});
			return request;
		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	public static final String FIND_PW_URL = SERVER + "/findpw";
	public Request putSdamFindPW(Context context, String userId,
								   final OnResultListener<FindPWInfo> listener) {
		try {
			String url = FIND_PW_URL;
			final CallbackObject<FindPWInfo> callbackObject = new CallbackObject<FindPWInfo>();

			JsonObject json = new JsonObject();
			json.addProperty("userId", ""+userId);

			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json")
					.post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					FindPWInfo result = gson.fromJson(response.body().charStream(), FindPWInfo.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});
			return request;
		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static final String CHANGE_PW_URL = SERVER + "/changepw";
	public Request putSdamChangePW(Context context, String password,
								 final OnResultListener<FindPWInfo> listener) {
		try {
			String url = CHANGE_PW_URL;
			final CallbackObject<FindPWInfo> callbackObject = new CallbackObject<FindPWInfo>();

			JsonObject json = new JsonObject();
			json.addProperty("newPw", ""+password);

			String jsonString = json.toString();
			RequestBody body = RequestBody.create(CONTENT_TYPE_JSON, jsonString);
			Request request = new Request.Builder().url(url)
					.header("Accept", "application/json")
					.post(body)
					.tag(context)
					.build();

			callbackObject.request = request;
			callbackObject.listener = listener;
			mClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					callbackObject.exception = e;
					Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
					mHandler.sendMessage(msg);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Gson gson = new Gson();
					FindPWInfo result = gson.fromJson(response.body().charStream(), FindPWInfo.class);
					callbackObject.result = result;
					Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
					mHandler.sendMessage(msg);
				}
			});
			return request;
		} catch (JsonParseException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}



}

