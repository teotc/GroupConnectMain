package sg.nyp.groupconnect.db;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.Map;
import sg.nyp.groupconnect.entity.Subjects;
import sg.nyp.groupconnect.utilities.JSONParser;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.RadioButton;

public class RetrieveSubject extends AsyncTask<String, String, String> {

	/**
	 * Before starting background thread Show Progress Dialog
	 * */
	boolean failure = false;
	public int success;

	// Database
	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();

	private static final String SUBJECT_URL = "http://www.it3197Project.3eeweb.com/grpConnect/statics/retrieveSujects.php";

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_ARRAY = "posts";

	private static final String TAG_ID = "id";
	private static final String TAG_NAME = "name";

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pDialog = new ProgressDialog(Map.context);
		pDialog.setMessage("Setting up...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(true);
		pDialog.show();
	}

	@Override
	protected String doInBackground(String... args) {
		// Check for success tag

		try {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			// getting product details by making HTTP request
			JSONObject json = jsonParser.makeHttpRequest(SUBJECT_URL, "POST",
					params);

			// json success tag
			success = json.getInt(TAG_SUCCESS);

			for (int i = 0; i < json.getJSONArray(TAG_ARRAY).length(); i++) {

				JSONObject c = json.getJSONArray(TAG_ARRAY).getJSONObject(i);

				Subjects fs = new Subjects(c.getInt(TAG_ID),
						c.getString(TAG_NAME));
				Map.arraySubject.add(fs);
			}

			if (success == 1) {
				return json.getString(TAG_MESSAGE);
			} else {
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
		for (int i = 0; i < Map.arraySubject.size(); i++) {
			RadioButton rdbtn = new RadioButton(Map.context);
			rdbtn.setId(Map.arraySubject.get(i).getId());
			rdbtn.setText(Map.arraySubject.get(i).getName());
			rdbtn.setTextColor(Color.WHITE);
			rdbtn.setTextSize(20f);
			Map.rdGrp.addView(rdbtn);
		}
		pDialog.dismiss();

	}

}