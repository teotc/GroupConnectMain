package sg.nyp.groupconnect.db;

import sg.nyp.groupconnect.utilities.JSONParser;
import sg.nyp.groupconnect.utilities.PieChartBuilder;
import android.app.ProgressDialog;
import android.os.AsyncTask;

public class AttemptMember extends AsyncTask<String, String, String> {

	/**
	 * Before starting background thread Show Progress Dialog
	 * */
	boolean failure = false;
	public int success;
	
	// Database
	public static ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();

	private static final String MEMBER_URL = "http://www.it3197Project.3eeweb.com/grpConnect/retrieveMemberRecord.php";

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_ARRAY = "posts";

	private static final String TAG_ID = "id";
	private static final String TAG_NAME = "name";
	private static final String TAG_LATITUDE = "latitude";
	private static final String TAG_LONGITUDE = "longitude";
	private static final String TAG_LOCATION = "location";
	private static final String TAG_GENDER = "gender";
	private static final String TAG_SCHOOLID = "schoolId";

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pDialog = new ProgressDialog(PieChartBuilder.context);
		pDialog.setMessage("Retreiving data...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(true);
		pDialog.show();
	}

	@Override
	protected String doInBackground(String... args) {
		// TODO Auto-generated method stub
		// Check for success tag

//		try {
//			// Building Parameters
//			List<NameValuePair> params1 = new ArrayList<NameValuePair>();
//
//			// getting product details by making HTTP request
//			JSONObject json1 = jsonParser.makeHttpRequest(MEMBER_URL, "POST",
//					params1);
//
//			// json success tag
//			success = json1.getInt(TAG_SUCCESS);
//			PieChartBuilder.arrayFakeMember.clear();
//
//			for (int i = 0; i < json1.getJSONArray(TAG_ARRAY).length(); i++) {
//
//				JSONObject c = json1.getJSONArray(TAG_ARRAY).getJSONObject(i);
//
//				Member fm = new Member(c.getInt(TAG_ID), c.getString(TAG_NAME),
//						c.getString(TAG_LOCATION), c.getDouble(TAG_LATITUDE),
//						c.getDouble(TAG_LONGITUDE), c.getString(TAG_GENDER),
//						c.getInt(TAG_SCHOOLID));
//				PieChartBuilder.arrayFakeMember.add(fm);
//			}
//			if (success == 1) {
//				return json1.getString(TAG_MESSAGE);
//			} else {
//				return json1.getString(TAG_MESSAGE);
//
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}

		return null;

	}

	/**
	 * After completing background task Dismiss the progress dialog
	 * **/
	public void onPostExecute(String file_url) {
		pDialog.dismiss();
//		alertDialog();
	}
}