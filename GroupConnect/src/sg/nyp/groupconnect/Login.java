package sg.nyp.groupconnect;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.notification.AppServices;
import sg.nyp.groupconnect.utilities.JSONParser;

import com.google.android.gcm.GCMRegistrar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.preference.PreferenceManager;
import android.view.View.OnClickListener;

public class Login extends Activity implements OnClickListener {
	
	private static final String TAG = "Login";

	EditText etUser, etPass;
	Button btnLogin;
	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	// testing from a real server:
	private static final String LOGIN_URL = "http://www.it3197Project.3eeweb.com/grpConnect/login.php";

	// JSON element ids from repsonse of php script:
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_TYPE = "type";
	private static final String TAG_ID = "id";
	private static final String TAG_UUID = "device";
	private static final String TAG_HOME = "location";
	private static final String TAG_LAT = "latitude";
	private static final String TAG_LNG = "longitude";
	private static final String TAG_SCHID = "schoolId";

	private static final String MEM_UPDATE_DEVICE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/memUpdate.php";
	static String deviceUUID = "";

	public static void setDeviceUUID(String uuid) {
		deviceUUID = uuid;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// setup input fields
		etUser = (EditText) findViewById(R.id.activity_login_etUser);
		etPass = (EditText) findViewById(R.id.activity_login_etPwd);

		// setup buttons
		btnLogin = (Button) findViewById(R.id.activity_login_btnLogin);

		// To prepare for push notification START

		// this is a hack to force AsyncTask to be initialized on main thread.
		// Without this things
		// won't work correctly on older versions of Android (2.2, apilevel=8)

		try {
			Class.forName("android.os.AsyncTask");
		} catch (Exception ignored) {
		}

		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);

		AppServices.loginAndRegisterForPush(this);

		// To prepare for push notification END

		// register listeners
		btnLogin.setOnClickListener(this);

	}

	public void onClick(View v) {

		int itemId = v.getId();
		if (itemId == R.id.activity_login_btnLogin) {
			new AttemptLogin().execute();

		}
	}

	class AttemptLogin extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Login.this);
			pDialog.setMessage("Attempting login...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Check for success tag
			int success;
			String username = etUser.getText().toString();
			String password = etPass.getText().toString();
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("name", username));
				params.add(new BasicNameValuePair("password", password));

				Log.d("request!", "starting");
				// getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST",
						params);

				// check your log for json response
				Log.d("Login attempt", json.toString());

				// json success tag
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					Log.d("Login Successful!", json.toString());
					// save user data
					SharedPreferences sp = PreferenceManager
							.getDefaultSharedPreferences(Login.this);
					Editor edit = sp.edit();
					String id = json.getString(TAG_ID);
					edit.putString("id", id);

					edit.putString("username", username);
					Log.d(TAG, "username: "+username);

					String homeLocation = json.getString(TAG_HOME);
					edit.putString("home", homeLocation);

					String lat = json.getString(TAG_LAT);
					String lng = json.getString(TAG_LNG);
					edit.putString("homeLat", lat);
					edit.putString("homeLng", lng);

					String schId = json.getString(TAG_SCHID);
					edit.putString("schoolId", schId);

					String type = json.getString(TAG_TYPE);
					edit.putString("type", type);

					String uuid = json.getString(TAG_UUID);

					edit.putString("interestedSub",
							json.getString("interestedSub"));

					// edit.putString("device", uuid);
					edit.commit();
					// if (uuid.equals(""))
					// {
					updateUserDevice();
					Log.i("Login",
							"Hello: " + sp.getString("device", "No Device"));
					// }
					// else
					// {
					edit.commit();
					// }

					Intent i = new Intent(Login.this, MainActivity.class);
					finish();
					startActivity(i);

					return json.getString(TAG_MESSAGE);
				} else {
					Log.d("Login Failure!", json.getString(TAG_MESSAGE));
					return json.getString(TAG_MESSAGE);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;

		}

		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product deleted
			pDialog.dismiss();
			if (file_url != null) {
				Toast.makeText(Login.this, file_url, Toast.LENGTH_LONG).show();
			}

		}

	}

	public void updateUserDevice() {

		new MemDeviceUpdate().execute();
		SharedPreferences sp1 = PreferenceManager
				.getDefaultSharedPreferences(Login.this);
		String hello = sp1.getString("device", "No Device");
		Log.i("Login", "Hello: " + hello);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_magiclogin_tc) {
			etUser.setText("tc");
			etPass.setText("tc");
			new AttemptLogin().execute();

		} else if (id == R.id.action_magiclogin_alfred) {
			etUser.setText("alfred");
			etPass.setText("chan");
			new AttemptLogin().execute();
			
		} else if (id == R.id.action_magiclogin_nyp) {
			etUser.setText("NYP");
			etPass.setText("NYP");
			new AttemptLogin().execute();
		}
		return super.onOptionsItemSelected(item);
	}

	class MemDeviceUpdate extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			/*
			 * pDialog = new ProgressDialog(ManageRoom.this);
			 * pDialog.setMessage("Updating...");
			 * pDialog.setIndeterminate(false); pDialog.setCancelable(true);
			 * pDialog.show();
			 */
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Check for success tag
			int success;
			SharedPreferences sp = PreferenceManager
					.getDefaultSharedPreferences(Login.this);
			Editor edit = sp.edit();
			String post_memId = sp.getString("id", "No Id");
			String post_device = deviceUUID;
			Log.i("LoginUpdate", post_memId + " / " + post_device);

			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("id", post_memId));
				params.add(new BasicNameValuePair("device", post_device));

				Log.d("request!", "starting");

				// Posting user data to script
				JSONObject json = jsonParser.makeHttpRequest(
						MEM_UPDATE_DEVICE_URL, "POST", params);

				// full json response
				Log.d("Post Comment attempt", json.toString());

				// json success element
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					Log.d("Comment Added!", json.toString());

					edit.putString("device", post_device);
					edit.commit();
					return json.getString(TAG_MESSAGE);
				} else {
					Log.d("Comment Failure!", json.getString(TAG_MESSAGE));
					return json.getString(TAG_MESSAGE);

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;

		}

		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product deleted
			// pDialog.dismiss();
			if (file_url != null) {
				// Toast.makeText(ManageRoom.this, file_url,
				// Toast.LENGTH_LONG).show();
			}

		}

	}

}