package sg.nyp.groupconnect;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import utilities.JSONParser;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends Activity implements OnClickListener {

	private EditText fname, lname, user, email, address, postcode, pass;
	private Button mRegister;

	private String status;

	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	// php login script

	// localhost :
	// testing on your device
	// put your local ip instead, on windows, run CMD > ipconfig
	// or in mac's terminal type ifconfig and look for the ip under en0 or en1
	// private static final String LOGIN_URL =
	// "http://xxx.xxx.x.x:1234/webservice/register.php";

	// testing on Emulator:
	// private static final String LOGIN_URL =
	// "http://10.0.2.2:1234/webservice/register.php";
	private static final String LOGIN_URL = "http://"
			+ "www.it3197Project.3eeweb.com/webservice/registerTC.php";

	// testing from a real server:
	// private static final String LOGIN_URL =
	// "http://www.yourdomain.com/webservice/register.php";

	// ids
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		fname = (EditText) findViewById(R.id.regFname);
		lname = (EditText) findViewById(R.id.regLname);
		user = (EditText) findViewById(R.id.regUsername);
		email = (EditText) findViewById(R.id.regEmail);
		address = (EditText) findViewById(R.id.regAddress);
		postcode = (EditText) findViewById(R.id.regPostalcode);
		pass = (EditText) findViewById(R.id.regPassword);

		mRegister = (Button) findViewById(R.id.regRegisterBn);
		mRegister.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		new CreateUser().execute();
	}

	class CreateUser extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		boolean failure = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Register.this);
			pDialog.setMessage("Creating User...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Check for success tag
			int success;

			String username = user.getText().toString();
			String password = pass.getText().toString();

			if (TextUtils.isEmpty(username)) {
				Toast.makeText(Register.this, "Please Enter Your User Name",
						Toast.LENGTH_SHORT).show();
				return null;
			} else if (TextUtils.isEmpty(password)) {
				Toast.makeText(Register.this, "Please Enter Your Password",
						Toast.LENGTH_SHORT).show();
				return null;
			}

			String name = lname.getText().toString() + " "
					+ fname.getText().toString();
			String emailaddress = email.getText().toString();
			String streetaddress = address.getText().toString() + " Singapore "
					+ postcode.getText().toString();

			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("username", username));
				params.add(new BasicNameValuePair("password", password));

				params.add(new BasicNameValuePair("name", name));
				params.add(new BasicNameValuePair("emailaddress", emailaddress));
				params.add(new BasicNameValuePair("streetaddress",
						streetaddress));

				Log.d("request!", "starting");

				// Posting user data to script
				JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST",
						params);

				// full json response
				Log.d("Login attempt", json.toString());

				// json success element
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					status = "success";
					Log.d("User Created!", json.toString());
					finish();
					return json.getString(TAG_MESSAGE);
				} else {
					status = "failed";
					Log.d("Login Failure!", json.getString(TAG_MESSAGE));
					return json.getString(TAG_MESSAGE);

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;

		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product deleted
			pDialog.dismiss();
			if (file_url != null) {
				Toast.makeText(Register.this, file_url, Toast.LENGTH_LONG)
						.show();
			}

			if (status == "success") {
				finish();
			}
		}

	}

}