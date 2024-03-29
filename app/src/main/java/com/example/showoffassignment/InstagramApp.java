package com.example.showoffassignment;


import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.example.showoffassignment.lazyload.Utils;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 
 * @author Anamika Chavan
 * 
 */
public class InstagramApp {

	private InstagramSession mSession;
	private InstagramDialog mDialog;
	;
	private OAuthAuthenticationListener mListener;
	private ProgressDialog mProgress;
	private HashMap<String, String> userInfo = new HashMap<String, String>();
	private String mAuthUrl;
	private String mTokenUrl;
	private String mAccessToken;
	private Context mCtx;

	private String mClientId;
	private String mClientSecret;
	String username;

	static int WHAT_FINALIZE = 0;
	static int WHAT_ERROR = 1;
	private static int WHAT_FETCH_INFO = 2;

	/**
	 * Callback url, as set in 'Manage OAuth Costumers' page
	 * (https://developer.github.com/)
	 */

	public static String mCallbackUrl = "";
	private static final String AUTH_URL = "https://api.instagram.com/oauth/authorize/";
	private static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";
	private static final String API_URL = "https://graph.instagram.com/me";
	private static final String API_URL_Media = "https://graph.instagram.com/me/media";


	private static final String TAG = "InstagramAPI";

	public static final String TAG_DATA = "data";
	public static final String TAG_ID = "id";
	public static final String TAG_PROFILE_PICTURE = "profile_picture";
	public static final String TAG_USERNAME = "username";
	public static final String TAG_BIO = "bio";
	public static final String TAG_WEBSITE = "website";
	public static final String TAG_COUNTS = "counts";
	public static final String TAG_FOLLOWS = "follows";
	public static final String TAG_FOLLOWED_BY = "followed_by";
	public static final String TAG_MEDIA = "media";
	public static final String TAG_FULL_NAME = "full_name";
	public static final String TAG_META = "meta";
	public static final String TAG_CODE = "code";


	public InstagramApp(Context context, String clientId, String clientSecret,
			String callbackUrl) {

		mClientId = clientId;
		mClientSecret = clientSecret;
		mCtx = context;
		mSession = new InstagramSession(context);
		mAccessToken = mSession.getAccessToken();
		mCallbackUrl = callbackUrl;
		mTokenUrl = TOKEN_URL + "?app_id=" + clientId + "&app_secret="
				+ clientSecret
				+ "&grant_type=authorization_code"
				+ "&redirect_uri=" + mCallbackUrl;
		mAuthUrl = AUTH_URL
				+ "?app_id="
				+ clientId
				+ "&redirect_uri="
				+ mCallbackUrl
				+"&scope=user_profile,user_media"
				+ "&response_type=code";


		InstagramDialog.OAuthDialogListener listener = new InstagramDialog.OAuthDialogListener() {
			@Override
			public void onComplete(String code) {
				getAccessToken(code);
			}

			@Override
			public void onError(String error) {
				mListener.onFail("Authorization failed");
			}
		};

		mDialog = new InstagramDialog(context, mAuthUrl, listener);
		mProgress = new ProgressDialog(context);
		mProgress.setCancelable(false);
	}

	private void getAccessToken(final String code) {

		mProgress.setMessage("Getting access token ...");
		mProgress.show();

		new Thread() {
			@Override
			public void run() {
				Log.i(TAG, "Getting access token");
				int what = WHAT_FETCH_INFO;
				try {
					URL url = new URL(TOKEN_URL);
					Log.v("Authcode","code"+code);
					String[] separated = code.split("#");
					String finalCode= separated[0];

					Log.v("Authcode","code"+finalCode);
					// URL url = new URL(mTokenUrl + "&code=" + code);
					Log.i(TAG, "Opening Token URL " + url.toString());
					HttpURLConnection urlConnection = (HttpURLConnection) url
							.openConnection();
					urlConnection.setRequestMethod("POST");
					urlConnection.setDoInput(true);
					urlConnection.setDoOutput(true);
					// urlConnection.connect();
					OutputStreamWriter writer = new OutputStreamWriter(
							urlConnection.getOutputStream());
					writer.write("app_id=" + mClientId + "&app_secret="
							+ mClientSecret + "&grant_type=authorization_code"
							+ "&redirect_uri=" +mCallbackUrl + "&code=" +finalCode);
					writer.flush();
					Log.v("ConnectionRequest",""+urlConnection
							.getInputStream());
					String response = Utils.streamToString(urlConnection
							.getInputStream());
					Log.i(TAG, "response " + response);
					JSONObject jsonObj = (JSONObject) new JSONTokener(response)
							.nextValue();

					mAccessToken = jsonObj.getString("access_token");
					Log.i(TAG, "Got access token: " + mAccessToken);

					String id = jsonObj.getString("user_id");
					mSession.storeAccessToken(mAccessToken, id, "", "");

				} catch (Exception ex) {
					what = WHAT_ERROR;
					ex.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0));
			}
		}.start();
	}

	public void fetchUserName(final Handler handler) {
		mProgress = new ProgressDialog(mCtx);
		mProgress.setMessage("Loading ...");
		mProgress.show();

		new Thread() {
			@Override
			public void run() {
				Log.i(TAG, "Fetching user info");
				int what = WHAT_FINALIZE;
				try {
//					URL url = new URL(API_URL + "/users/" + mSession.getId()
//							+ "/?access_token=" + mAccessToken);

					URL url = new URL(API_URL + "?fields=id,username,media"
							+ "&access_token=" + mAccessToken);

					Log.d(TAG, "Opening URL " + url.toString());
					HttpURLConnection urlConnection = (HttpURLConnection) url
							.openConnection();
					urlConnection.setRequestMethod("GET");
					urlConnection.setDoInput(true);
					urlConnection.connect();
					String response = Utils.streamToString(urlConnection
							.getInputStream());
					System.out.println(response);
					JSONObject jsonObj = (JSONObject) new JSONTokener(response)
							.nextValue();
					//String username=jsonObj.getString("username");
					mSession.storeUserNAme(username);
					username=jsonObj.getString("username");

				} catch (Exception ex) {
					what = WHAT_ERROR;
					ex.printStackTrace();
				}
				mProgress.dismiss();
				handler.sendMessage(handler.obtainMessage(what, 2, 0));
			}
		}.start();

	}



	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == WHAT_ERROR) {
				mProgress.dismiss();
				if (msg.arg1 == 1) {
					mListener.onFail("Failed to get access token");
				} else if (msg.arg1 == 2) {
					mListener.onFail("Failed to get user information");
				}
			} else if (msg.what == WHAT_FETCH_INFO) {
				// fetchUserName();
				mProgress.dismiss();
				mListener.onSuccess();
			}
		}
	};

	public HashMap<String, String> getUserInfo() {
		return userInfo;
	}

	public boolean hasAccessToken() {
		return (mAccessToken == null) ? false : true;
	}

	public void setListener(OAuthAuthenticationListener listener) {
		mListener = listener;
	}

	public String getUserName() {
		return username;
	}

	public String getId() {
		return mSession.getId();
	}

	public String getName() {
		return mSession.getName();
	}
	public String getTOken() {
		return mSession.getAccessToken();
	}
	public void authorize() {

		mDialog.show();
	}

	

	public void resetAccessToken() {
		if (mAccessToken != null) {
			mSession.resetAccessToken();
			mAccessToken = null;
		}
	}

	public interface OAuthAuthenticationListener {
		public abstract void onSuccess();

		public abstract void onFail(String error);
	}

	
}