package sg.nyp.groupconnect.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.MainActivity;
import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.data.CategoriesDbAdapter;
import sg.nyp.groupconnect.utilities.JSONParser;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.widget.Toast;

@SuppressLint("NewApi")
public class fragment_profile extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {

	private String interestedSub;
	MultiSelectListPreference mslpInterests;
	private EditTextPreference etpName;
	EditTextPreference et_interestedSub;
	CategoriesDbAdapter mDbHelper;
	private static final String defMsg = "Tap to choose interests";

	private static final String TAG = "fragment_profile";

	SharedPreferences sp;
	private String name;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);

		sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		interestedSub = sp.getString("interestedSub", "No interests");
		id = sp.getString("id", "0");
		name = sp.getString("name", "John Doe");
		Log.d(TAG, "IntrsSub: " + interestedSub);

		mDbHelper = new CategoriesDbAdapter(getActivity());
		mDbHelper.open();

	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume Fired");
		new LoadCategories().execute();
	}

	// @Override
	// public void onPause() {
	// super.onPause();
	// Log.d(TAG, "onPause Fired");
	// if (!userCategList.isEmpty()) {
	// if (!userCategList.equals(userCategListCom)) {
	// Log.d(TAG, "Firing MemInterestUpdate");
	// new MemInterestUpdate();
	// }
	// }
	// }

//	@Override
//	public void onDestroyView() {
//		super.onDestroyView();
//		Log.d(TAG, "onDestroyView Fired");
//		if (!userCategList.isEmpty()) {
//			Log.d(TAG, "oDV userCategList !Empty");
//			// if (!userCategList.equals(userCategListCom)) {
//			// Log.d(TAG, "oDV userCategList !Empty");
//			// Log.d(TAG, "Firing MemInterestUpdate");
//			// new MemInterestUpdate().execute();
//			// }
//			// if(nonOverLap(userCategList, userCategListCom).size()>1){
//			// Log.d(TAG, "oDV userCategList !Empty");
//			// Log.d(TAG, "Firing MemInterestUpdate");
//			new MemInterestUpdate().execute();
//			// }
//		}
//	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
		Log.d(TAG, "onSharedPreferenceChanged Fired");
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
	private ArrayList<String> userCategListCom;
	private String userInterestedCategStr;

	private boolean noInterest;

	class LoadCategories extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			mslpInterests = (MultiSelectListPreference) findPreference("mslp_listSub");
			etpName = (EditTextPreference) findPreference("ppf_pref_profname");
			etpName.setText(name);
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
				userCategListCom = userCategList = new ArrayList<String>();

				if (!interestedSub.equalsIgnoreCase("No interests")) {
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
						userCategListCom = userCategList;
					}
				} else {
					noInterest = true;
					userInterestedCategStr = defMsg;
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

		Set<String> ins = null;

		if (!noInterest) {
			ins = new HashSet<String>(userCategList);
			// Sets default ticked values i.e. a
			// subset of the full list
			mslpInterests.setValues(ins);
		}
		// mslpInterests.setPersistent(false);

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
						Log.d("GC - prefchange", newValue.toString());
						Log.d("GC - interestedSub", interestedSub);
						interestedSub = newValue.toString().replace("[", "")
								.replace("]", "").replace(" ", "");
						Log.d("GC - interestedSub - c", interestedSub);
						userInterestedCategStr = "";
						userCategList.clear();

						if (interestedSub.equalsIgnoreCase("")) {
							Log.d(TAG, "interestedSub empty");

							interestedSub = defMsg;
						} else {
							String[] parts = interestedSub.split(",");
							if (parts.length != 0) {
								for (int z = 0; z < parts.length; z++) {
									String sub = parts[z];
									userCategList.add(sub.trim());
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
						}
						mslpInterests.setSummary(interestedSub);
						new MemInterestUpdate().execute();
						return true;
					}
				});
		mslpInterests.setEnabled(true);

	}

	JSONParser jsonParser = new JSONParser();
	private static final String CAT_UPD_URL = MainActivity.GCLINK
			+ "menUpdateInterest.php";

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	
	private String id;

	class MemInterestUpdate extends AsyncTask<String, String, String> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... args) {
			// Check for success tag
			int success;
			String interest = interestedSub;
			Log.d(TAG, "New Update Values: " + interest);
			Log.d(TAG, "Update URL: " + CAT_UPD_URL);
			Log.d(TAG, "ID: " + id);
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("interestedSub", interest));
				params.add(new BasicNameValuePair("id", id ));

				Log.d(TAG, "Attempting Update");

				// Posting user data to script
				JSONObject json = jsonParser.makeHttpRequest(CAT_UPD_URL,
						"POST", params);

				// full json response
				Log.d(TAG, json.toString());

				// json success element
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					Log.d(TAG, json.toString());
					
					Editor edit = sp.edit();

					edit.putString("interestedSub", interest);
					edit.commit();
					
					interestedSub = sp.getString("interestedSub", "No interests");
					Log.d(TAG, "New IntrsSub: " + interestedSub);

					return json.getString(TAG_MESSAGE);
				} else {
					Log.d(TAG, json.getString(TAG_MESSAGE));
					return json.getString(TAG_MESSAGE);

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return null;

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			//broadcastIntent();
		}
	}

	// public void broadcastIntent() {
	// Log.d(TAG, "Intent - Categ Update");
	// Intent intent = new Intent();
	// intent.setAction("sg.nyp.groupconnect.DATADONE");
	// Context con = getActivity().getApplication();
	// con.sendBroadcast(intent);
	// }

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

	// ArrayList<String> test;

	ArrayList<String> nonOverLap(Collection<String> coll1,
			Collection<String> coll2) {
		Collection<String> result = union(coll1, coll2);
		// test = new ArrayList<String>(result);
		result.removeAll(intersect(coll1, coll2));

		return new ArrayList<String>(result);
	}
}
