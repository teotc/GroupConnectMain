package sg.nyp.groupconnect.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.data.CategoriesDbAdapter;
import sg.nyp.groupconnect.data.GrpRoomDbAdapter;
import sg.nyp.groupconnect.utilities.JSONParser;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

@SuppressLint("NewApi")
public class fragment_profile extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {

	private String interestedSub;
	MultiSelectListPreference mslpInterests;
	EditTextPreference et_interestedSub;
	CategoriesDbAdapter mDbHelper;

	private static final String TAG = "fragment_profile";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		interestedSub = sp.getString("interestedSub", "No categories");
		Log.d(TAG, "IntrsSub: " + interestedSub);

		mDbHelper = new CategoriesDbAdapter(getActivity());
		mDbHelper.open();

		new LoadCategories().execute();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
		Log.d("GC", "onSharedPreferenceChanged Fired");
		Preference pref = findPreference(key);
		// if (pref instanceof EditTextPreference) {
		// EditTextPreference etp = (EditTextPreference) pref;
		// if (pref.getKey().equals("et_interestedSub")) {
		// interestedSub += etp.getText() + ",";
		// Log.d("GC", "IntrsSub: " + interestedSub);
		// }
		// }
		if (pref instanceof MultiSelectListPreference) {
			MultiSelectListPreference mslp = (MultiSelectListPreference) pref;
			if (pref.getKey().equals("mslp_listSub")) {
				Log.d("GC", mslp.getEntryValues().toString());
			}
		}
	}

	private ArrayList<String> mFullCategList;
	private ArrayList<String> userCategList;
	private String userInterestedCategStr;

	class LoadCategories extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			mslpInterests = (MultiSelectListPreference) findPreference("mslp_listSub");
			mslpInterests.setEnabled(false);
		}

		@Override
		protected String doInBackground(String... args) {
			String category;
			mFullCategList = new ArrayList<String>();

			try {

				Cursor mCursor = mDbHelper.fetchAll();
				if (mCursor.getCount() != 0) {
					while (mCursor.moveToNext()) {
						category = CategoriesDbAdapter.getString(mCursor,
								CategoriesDbAdapter.KEY_NAME);
						Log.d(TAG, "Category(DB): " + category);
						mFullCategList.add(category);
					}
				}

				userInterestedCategStr = "";
				userCategList = new ArrayList<String>();

				String[] parts = interestedSub.split(",");
				if (parts.length != 0) {
					for (int z = 0; z < parts.length; z++) {
						String sub = parts[z];

						userCategList.add(sub);

						if (z % 2 == 1) {
							userInterestedCategStr += sub + " ";
						} else {
							userInterestedCategStr += sub + ", ";
						}

					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			interestSetUp();
		}

	}

	private void interestSetUp() {

		for (String s : userCategList) {
			Log.d(TAG, "intCatArr:" + s);
		}

		Set<String> ins = new HashSet<String>(userCategList);

		mslpInterests.setPersistent(false);
		// Sets default ticked values i.e. a
		// subset of the full list
		mslpInterests.setValues(ins);
		// Sets the full list to select from
		mslpInterests.setEntries(mFullCategList
				.toArray(new CharSequence[mFullCategList.size()]));
		mslpInterests.setEntryValues(mFullCategList
				.toArray(new CharSequence[mFullCategList.size()]));
		mslpInterests.setSummary(userInterestedCategStr);

		mslpInterests
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference pref,
							Object newValue) {
						// TODO Auto-generated method stub
						Log.d("GC - prefchange", newValue.toString());
						Log.d("GC - interestedSub", interestedSub);
						interestedSub = newValue.toString().replace("[", "").replace("]", "");
						Log.d("GC - interestedSub - c", interestedSub);
						userInterestedCategStr = "";
						userCategList.clear();
						
						String[] parts = interestedSub.split(",");
						if (parts.length != 0) {
							for (int z = 0; z < parts.length; z++) {
								String sub = parts[z];
								userCategList.add(sub);
								// if (z % 2 == 1) {
								// userInterestedCategStr += sub + " ";
								// } else {
								// userInterestedCategStr += sub + ", ";
								// }
								// Log.d("GC - prefchange catgstr:",
								// userInterestedCategStr);
							}
						}
						Set<String> ins = new HashSet<String>(userCategList);
						mslpInterests.setValues(ins);
						mslpInterests.setSummary(interestedSub);

						return true;
					}
				});
		mslpInterests.setEnabled(true);

	}

	JSONParser jsonParser = new JSONParser();
	private static final String CAT_UPD_URL = "";

	// class createRoom extends AsyncTask<String, String, String> {
	// @Override
	// protected String doInBackground(String... args) {
	// int success;
	// String pCategory = "";
	//
	// try {
	// // Building Parameters
	// List<NameValuePair> params = new ArrayList<NameValuePair>();
	// params.add(new BasicNameValuePair("category", pCategory));
	//
	// Log.d("request!", "starting");
	//
	// // Posting user data to script
	// JSONObject json = jsonParser.makeHttpRequest(CAT_UPD_URL,
	// "POST", params);
	//
	// // full json response
	// Log.d("Post Comment attempt", json.toString());
	//
	// // json success element
	// success = json.getInt(TAG_SUCCESS);
	// if (success == 1) {
	// Log.d("Comment Added!", json.toString());
	//
	// return json.getString(TAG_MESSAGE);
	// } else {
	// Log.d("Comment Failure!", json.getString(TAG_MESSAGE));
	// successAll = false;
	// return json.getString(TAG_MESSAGE);
	//
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	//
	// return null;
	//
	// }
	// }

	// DISTPREF = sp.getInt("DISTPREF", 5000); // DISTPREF_UNIT
	// DISTPREF_UNIT = sp.getInt("DISTPREF", 0);

	// bnSetDist.setOnClickListener(new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// // show();
	// }
	// });
	// new CompareRoomDistance().execute();

	Collection<String> union(Collection<String> coll1, Collection<String> coll2) {
		Set<String> union = new HashSet<String>(coll1);
		union.addAll(new HashSet<String>(coll2));
		return union;
	}

	Collection<String> intersect(Collection<String> coll1,
			Collection<String> coll2) {
		Set<String> intersection = new HashSet<String>(coll1);
		intersection.retainAll(new HashSet<String>(coll2));
		return intersection;
	}

	ArrayList<String> intersectArr(Collection<String> coll1,
			Collection<String> coll2) {
		Set<String> intersection = new HashSet<String>(coll1);
		intersection.retainAll(new HashSet<String>(coll2));
		return new ArrayList<String>(intersection);
	}

	ArrayList<String> nonOverLap(Collection<String> coll1,
			Collection<String> coll2) {
		Collection<String> result = union(coll1, coll2);
		userCategList = new ArrayList<String>(result);
		result.removeAll(intersect(coll1, coll2));

		return new ArrayList<String>(result);
	}
}
