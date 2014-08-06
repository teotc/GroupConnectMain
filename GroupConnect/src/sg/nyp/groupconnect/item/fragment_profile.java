package sg.nyp.groupconnect.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.Login;
import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.room.CreateRmStep2;
import sg.nyp.groupconnect.utilities.JSONParser;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;

@SuppressLint("NewApi")
public class fragment_profile extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {

	// @Override
	// public View onCreateView(LayoutInflater inflater, ViewGroup container,
	// Bundle savedInstanceState) {
	// View rootView = inflater.inflate(R.layout.fragment_profile, container,
	// false);
	// return rootView;
	// }

	private String interestedSub;
	MultiSelectListPreference mslpInterests;
	EditTextPreference et_interestedSub;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		interestedSub = sp.getString("interestedSub", "No categories");

		new getCateFromWS().execute();

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
		Log.d("GC", "Fired");
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

	ArrayList<String> mInterestList;

	// class setProfileItems extends AsyncTask<String, String, String> {
	//
	// @Override
	// protected String doInBackground(String... args) {
	//
	// String interest = "";
	//
	// Log.d("GC - interestedSub:", interestedSub);
	//
	// mInterestList = new ArrayList<String>();
	//
	// String[] parts = interestedSub.split(",");
	// if (parts.length != 0) {
	// for (int z = 0; z < parts.length; z++) {
	// String sub = parts[z];
	// mInterestList.add(sub);
	// if (z % 2 == 1) {
	// interest += sub + " ";
	// } else {
	// interest += sub + ", ";
	// }
	// }
	// mslpInterests.setEntries(mInterestList
	// .toArray(new CharSequence[mInterestList.size()]));
	// mslpInterests.setEntryValues(mInterestList
	// .toArray(new CharSequence[mInterestList.size()]));
	// mslpInterests.setSummary(interest);
	// }
	// return null;
	// }
	//
	// }

	String URL = "http://www.it3197Project.3eeweb.com/grpConnect/categRetrieveAll.php";

	private static final String TAG_POSTS = "posts";
	// private static final String TAG_CATEID = "id";
	private static final String TAG = "name";
	// private static final String TAG_TYPE_ID = "typeId";
	private ArrayList<String> mCateList;
	private JSONArray mCategories;

	String interest;

	class getCateFromWS extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			mslpInterests = (MultiSelectListPreference) findPreference("mslp_listSub");
			mslpInterests.setEnabled(false);
		}

		String cate_name;

		@Override
		protected String doInBackground(String... args) {
			try {
				// mCateList = new ArrayList<HashMap<String, String>>();
				mCateList = new ArrayList<String>();

				JSONParser jParser = new JSONParser();
				JSONObject json = jParser.getJSONFromUrl(URL);

				if (json.getString("success").equals("1")) {
					try {

						mCategories = json.getJSONArray(TAG_POSTS);

						for (int i = 0; i < mCategories.length(); i++) {
							JSONObject c = mCategories.getJSONObject(i);

							// cate_id = c.getString(TAG_CATEID);
							// Log.d("TC", "userid:" + cate_id);
							cate_name = c.getString(TAG);
							Log.d("TC", "cate_name:" + cate_name);
							// cate_typeId = c.getString(TAG_TYPE_ID);

							// HashMap<String, String> map = new HashMap<String,
							// String>();

							// map.put(TAG_CATEID, cate_id);
							// map.put(TAG_CATE_NAME, cate_name);
							// map.put(TAG_TYPE_ID, cate_typeId);

							// mCateList.add(map);
							mCateList.add(cate_name);
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}

					interest = "";

					// mInterestList = mCateList;
					// mCateList2 = new ArrayList<String>();

					ArrayList<String> insSubArr = new ArrayList<String>();

					String[] parts = interestedSub.split(",");
					if (parts.length != 0) {
						for (int z = 0; z < parts.length; z++) {
							String sub = parts[z];

							insSubArr.add(sub);

							// for (String s : mCateList) {
							// if (!sub.equals(s)) {
							// Log.d("TC", "cat: " + s + "sub: " + sub);
							// mCateList2.add(s);
							// }
							// }
							// mInterestList.add(sub);
							if (z % 2 == 1) {
								interest += sub + " ";
							} else {
								interest += sub + ", ";
							}

						}
						// mCateList2 = nonOverLap(mCateList, insSubArr);
						insSelected = intersectArr(mCateList, insSubArr);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			interestSetUp();
		}

	}

	ArrayList<String> insSelected;

	private void interestSetUp() {

//		mslpInterests = (MultiSelectListPreference) this
//				.findPreference("mslp_listSub");
		mslpInterests.setPersistent(false);

		// Object defaultValue = insSelected;

		for (String s : insSelected) {
			Log.d(TAG, "insSelected: " + s);
		}

		// Log.d(TAG, "mslpInterests: " + mslpInterests.getValues());

		for (String s : mCateList) {
			Log.d(TAG, "mCatelist: " + s);
		}
		mslpInterests.setDefaultValue(insSelected);
		mslpInterests.setEntries(mCateList.toArray(new CharSequence[mCateList
				.size()]));
		mslpInterests.setEntryValues(mCateList
				.toArray(new CharSequence[mCateList.size()]));

		// Log.d(TAG, "mslpInterests: " + mslpInterests.getValues());
		mslpInterests.setSummary(interest);

		mslpInterests
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						// TODO Auto-generated method stub
						Log.d("GC - prefchange", newValue.toString());
						setInterest(newValue);
						return false;
					}
				});
		mslpInterests.setEnabled(true);

	}

	private void setInterest(Object newValue) {
		// TODO Auto-generated method stub

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

	Spinner spUnit;

	public void show() {

		final Dialog d = new Dialog(getActivity());
		d.setTitle("Choose a boundary");
		d.setContentView(R.layout.dialog_distpicker);
		Button dist_set = (Button) d.findViewById(R.id.diag_dist_set);
		Button dist_cancel = (Button) d.findViewById(R.id.diag_dist_cancel);
		spUnit = (Spinner) d.findViewById(R.id.diag_dist_spUnit);

		// String[] unitArray = new String[] { "M", "KM" };

		// Selection of the spinner
		spUnit = (Spinner) d.findViewById(R.id.diag_dist_spUnit);

		// Application of the Array to the Spinner
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
				d.getContext(), android.R.layout.simple_spinner_item,
				getResources().getStringArray(R.array.spDistUnits));
		spinnerArrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spUnit.setAdapter(spinnerArrayAdapter);

		// String[] unitArray = getActivity().getResources().getStringArray(
		// R.array.spDistUnits);
		// ArrayList<String> units = new ArrayList<String>(
		// Arrays.asList(unitArray));
		//
		// spUnit = new Spinner(getActivity());
		// ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
		// getActivity(), android.R.layout.simple_spinner_item,
		// units);
		// spinnerArrayAdapter
		// .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// spUnit.setAdapter(spinnerArrayAdapter);

		// spUnit.setSelection(DISTPREF_UNIT);

		// Set up Numberpicker
		final NumberPicker np = (NumberPicker) d
				.findViewById(R.id.diag_dist_npDist);
		np.setMaxValue(9999);
		np.setMinValue(1); // min value 1
		np.setWrapSelectorWheel(false);
		// np.setValue(DISTPREF);
		// np.setOnValueChangedListener(this);

		dist_set.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// tv.setText(String.valueOf(np.getValue())); //set the value to
				// textview

				// SharedPreferences sp = PreferenceManager
				// .getDefaultSharedPreferences(getActivity());
				// Editor edit = sp.edit();
				// edit.putInt("DISTPREF", np.getValue());
				// edit.putInt("DISTPREF_UNIT",
				// spUnit.getSelectedItemPosition());
				// edit.commit();
				d.dismiss();
			}
		});
		dist_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				d.dismiss(); // dismiss the dialog
			}
		});
		d.show();

	}

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
		insSelected = new ArrayList<String>(result);
		result.removeAll(intersect(coll1, coll2));

		return new ArrayList<String>(result);
	}
}
