package sg.nyp.groupconnect.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.MainActivity;
import sg.nyp.groupconnect.data.MemberGradeDbAdapter;
import sg.nyp.groupconnect.entity.MemberGrades;
import sg.nyp.groupconnect.utilities.JSONParser;
import android.os.AsyncTask;

public class MemberGradePullSvc extends AsyncTask<String, String, String> {
	private MemberGradeDbAdapter mDbHelper;

	// JSON parser class
	private JSONParser jsonParser = new JSONParser();
	private int success;

	private static final String RETRIEVE_LOCATION_URL = "http://www.it3197Project.3eeweb.com/grpConnect/statics/retrieveMemberGrades.php";

	private static final String TAG_SUCCESS = "success";
	// private static final String TAG_MESSAGE = "message";
	private static final String TAG_ARRAY = "posts";

	private static final String TAG_MEMBERID = "memberId";
	private static final String TAG_SUBJECTID = "subjectId";
	private static final String TAG_OLDGRADE = "oldGrade";
	private static final String TAG_NEWGRADE = "newGrade";

	private ArrayList<MemberGrades> memberGradeArray = new ArrayList<MemberGrades>();

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		mDbHelper = new MemberGradeDbAdapter(MainActivity.ct);
		mDbHelper.open();
	}

	@Override
	protected String doInBackground(String... args) {
		// Check for success tag

		try {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			// getting product details by making HTTP request
			JSONObject json = jsonParser.makeHttpRequest(RETRIEVE_LOCATION_URL,
					"POST", params);

			// json success tag
			success = json.getInt(TAG_SUCCESS);
			memberGradeArray.clear();

			for (int i = 0; i < json.getJSONArray(TAG_ARRAY).length(); i++) {

				JSONObject c = json.getJSONArray(TAG_ARRAY).getJSONObject(i);
				
				MemberGrades m = new MemberGrades(c.getString(TAG_MEMBERID),
						c.getString(TAG_SUBJECTID), c.getDouble(TAG_OLDGRADE),
						c.getDouble(TAG_NEWGRADE));

				memberGradeArray.add(m);

			}

			if (success == 1) {
				mDbHelper.checkMemberGrade(memberGradeArray);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(String file_url) {
		new CategoriesPullSvc().execute();
	}
}
