package sg.nyp.groupconnect;

import java.util.*;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.*;

import sg.nyp.groupconnect.utilities.*;

import android.app.*;
import android.content.*;
import android.content.SharedPreferences.*;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.preference.PreferenceManager;
import android.view.View.OnClickListener;

public class Login extends Activity implements OnClickListener {

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(Login.this);
		String username = sp.getString("username", null);
		String password = sp.getString("password", null);

		// if (username != null && password != null) {
		// Intent i = new Intent(Login.this, MainActivity.class);
		// finish();
		// startActivity(i);
		// }

		// setup input fields
		etUser = (EditText) findViewById(R.id.activity_login_etUser);
		etPass = (EditText) findViewById(R.id.activity_login_etPwd);

		// setup buttons
		btnLogin = (Button) findViewById(R.id.activity_login_btnLogin);

		// register listeners
		btnLogin.setOnClickListener(this);

	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.activity_login_btnLogin:
			new AttemptLogin().execute();
			break;

		default:
			break;
		}
	}

	class AttemptLogin extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Login.this);
			pDialog.setMessage("Attempting login...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
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
				params.add(new BasicNameValuePair("username", username));
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
					edit.putString("password", password);
					edit.putString("username", username);
					String type = json.getString(TAG_TYPE);
					edit.putString("type", type);
					String id = json.getString(TAG_ID);
					edit.putString("id", id);
					
					edit.putString("interestedSub", json.getString("interestedSub"));
					edit.putString("homeLocation", json.getString("homeLocation"));
					
					edit.commit();
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
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
