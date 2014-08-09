package sg.nyp.groupconnect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sg.nyp.groupconnect.data.CategoriesDbAdapter;
import sg.nyp.groupconnect.utilities.ExpandableListAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EditRoom1 extends Activity {
	// Variable
	EditText etTitle, etDesc;
	TextView tvCategoryChosen, tvCategoryTypeChosen;
	Button btnCategoryDialog;
	Spinner spMaxLearner;
	ArrayList<String> listOfNo = new ArrayList<String>();
	ArrayAdapter listOfNoAdapter;
	ListView lvCategory;

	// For Retrieving all the subjects for the existing expandablelistview
	private ArrayList<HashMap<String, String>> mCategoryList;
	private static final String TAG_NAME = "name";
	private static final String TAG_TYPENAME = "typeName";
	String name, typeName;
	ArrayList<String> schSubList = new ArrayList<String>();
	ArrayList<String> musicList = new ArrayList<String>();
	ArrayList<String> computerList = new ArrayList<String>();
	ArrayList<String> otherList = new ArrayList<String>();

	// For ShowCustomDialog
	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;
	RadioGroup rg_existNew;
	RadioButton rBtnExistNew;
	TextView tvCategory;
	Spinner spCategoryType;
	EditText etCategoryDialog;
	ArrayAdapter categoryTypeAdapter;
	ArrayList<String> categoryTypeList = new ArrayList<String>();
	Button btnConfirm;
	String categoryMethod;

	// Dialog Method
	AlertDialog dialog;
	private static final int FIELDEMPTY_ALERT = 1;

	// For CategoryListView
	ArrayList<String> categoryNameChosen = new ArrayList<String>();
	ArrayList<String> categoryTypeChosen = new ArrayList<String>();

	Integer[] imageId = { R.drawable.ic_launcher, R.drawable.ic_launcher,
			R.drawable.ic_launcher, R.drawable.ic_launcher

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_rm);

		setTitle("Create Room Step 1");

		etTitle = (EditText) findViewById(R.id.etTitle);
		btnCategoryDialog = (Button) findViewById(R.id.btnCategoryDialog);
		etDesc = (EditText) findViewById(R.id.etDesc);
		spMaxLearner = (Spinner) findViewById(R.id.spMaxLearner);
		tvCategoryChosen = (TextView) findViewById(R.id.tvCategoryChosen);
		tvCategoryTypeChosen = (TextView) findViewById(R.id.tvCategoryTypeChosen);
		// lvCategory = (ListView) findViewById (R.id.lvCategory);

		categoryNameChosen.add("None");
		categoryTypeChosen.add("None");

		btnCategoryDialog.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				showCustomDialog(tvCategoryChosen, tvCategoryTypeChosen);

			}
		});

		prepareListData();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_rm, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.next) {

			// String message;
			boolean success = true;

			// Check for any empty EditText
			if (etTitle.getText().toString().length() <= 0) {
				etTitle.setError("Enter Title");
				success = false;
			}

			if (tvCategoryChosen.getText().toString().equals("None")) {

				success = false;
			}

			if (etDesc.getText().toString().length() <= 0) {
				etDesc.setText("None");
			}

			if (success == true) // If all fields are filled
			{

				Intent myIntent = new Intent(EditRoom1.this, EditRoom2.class);
				myIntent.putExtra("title", etTitle.getText().toString());
				myIntent.putExtra("category", tvCategoryChosen.getText()
						.toString());
				myIntent.putExtra("categoryType", tvCategoryTypeChosen
						.getText().toString());
				myIntent.putExtra("desc", etDesc.getText().toString());
				myIntent.putExtra("maxLearner", spMaxLearner.getSelectedItem()
						.toString());
				myIntent.putExtra("categoryMethod", categoryMethod);
				startActivityForResult(myIntent, 1);

			}
			return true;
		} else if (id == R.id.back) {
			setResult(RESULT_CANCELED);
			finish();

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void showCustomDialog(final TextView tvCategoryChosenM,
			final TextView tvCategoryTypeChosenM) {
		// TODO Auto-generated method stub
		final Dialog dialog = new Dialog(EditRoom1.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.custom_category_dialog);

		// Exist
		tvCategory = (TextView) dialog.findViewById(R.id.tvCategory);
		expListView = (ExpandableListView) dialog.findViewById(R.id.lvExpExist);
		// New
		etCategoryDialog = (EditText) dialog
				.findViewById(R.id.etCategoryDialog);
		spCategoryType = (Spinner) dialog.findViewById(R.id.spCategoryType);
		btnConfirm = (Button) dialog.findViewById(R.id.btnConfirm);

		categoryTypeAdapter = new ArrayAdapter<String>(dialog.getContext(),
				android.R.layout.simple_spinner_dropdown_item, categoryTypeList);
		spCategoryType.setAdapter(categoryTypeAdapter);

		rg_existNew = (RadioGroup) dialog.findViewById(R.id.rg_existNew);
		rg_existNew.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				// get selected radio button from radioGroup
				// rg_existNew.getCheckedRadioButtonId() == arg1
				int selectedId = arg1;
				// find the radiobutton by returned id
				rBtnExistNew = (RadioButton) dialog.findViewById(selectedId);
				String rbtnSelected = rBtnExistNew.getText().toString();

				if (rbtnSelected.equals("Exist")) {
					tvCategory.setText("Choose one existing category below");
					expListView.setVisibility(View.VISIBLE);
					etCategoryDialog.setVisibility(View.GONE);
					spCategoryType.setVisibility(View.GONE);
					btnConfirm.setVisibility(View.GONE);
				} else if (rbtnSelected.equals("New")) {
					tvCategory
							.setText("Type a new category name and choose the type");
					expListView.setVisibility(View.GONE);
					etCategoryDialog.setVisibility(View.VISIBLE);
					spCategoryType.setVisibility(View.VISIBLE);
					btnConfirm.setVisibility(View.VISIBLE);
				}

			}
		});

		// preparing list data
		// prepareListData();
		listAdapter = new ExpandableListAdapter(dialog.getContext(),
				listDataHeader, listDataChild);
		// setting list adapter
		expListView.setAdapter(listAdapter);

		expListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {

				tvCategoryChosenM.setText(listDataChild.get(
						listDataHeader.get(groupPosition)).get(childPosition));

				tvCategoryTypeChosenM.setText(listDataHeader.get(groupPosition));

				categoryMethod = "Exist";

				dialog.dismiss();
				Toast.makeText(
						getApplicationContext(),
						listDataHeader.get(groupPosition)
								+ " : "
								+ listDataChild.get(
										listDataHeader.get(groupPosition)).get(
										childPosition), Toast.LENGTH_SHORT)
						.show();
				return false;
			}
		});

		/*
		 * final EditText editText =
		 * (EditText)dialog.findViewById(R.id.editText1); Button button =
		 * (Button)dialog.findViewById(R.id.button1);
		 */
		btnConfirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tvCategoryChosenM
						.setText(etCategoryDialog.getText().toString());
				String selectedType = categoryTypeList.get((int) spCategoryType
						.getSelectedItemId());

				tvCategoryTypeChosenM.setText(selectedType);

				categoryMethod = "New";
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	/*
	 * Preparing the list data
	 */
	private void prepareListData() {
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();

		// Adding child data
		listDataHeader.add("School Subjects");
		listDataHeader.add("Music");
		listDataHeader.add("Computer-related");
		listDataHeader.add("Others");

		// Adding categoryType for "New" Spinner
		categoryTypeList.add("School Subjects");
		categoryTypeList.add("Music");
		categoryTypeList.add("Computer-related");
		categoryTypeList.add("Others");

		// Set up the list of No. to choose max learner
		listOfNoAdapter = ArrayAdapter.createFromResource(this,
				R.array.listOfNo, android.R.layout.simple_list_item_1);
		spMaxLearner.setAdapter(listOfNoAdapter);

		new retrieveAllSubWithType().execute();

	}

	protected Dialog onCreateDialog(int id) {
		Log.i("sg.nyp.groupconnect", "onCreateDialog");

		switch (id) {
		case FIELDEMPTY_ALERT:
			Log.i("sg.nyp.groupconnect", "onCreateDialog - CREATERM_ALERT");

			Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Some or All Fields are empty. Please enter required fields.");
			builder.setCancelable(true);
			builder.setPositiveButton("Okay", new OkOnClickListener());
			dialog = builder.create();
			dialog.show();

		}

		return super.onCreateDialog(id);
	}

	private final class OkOnClickListener implements
			DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}
	}

	/*
	 * Retrieves recent post data from the server.
	 */
	public void updateJSONdata() {

		mCategoryList = new ArrayList<HashMap<String, String>>();

		CategoriesDbAdapter mDbHelper = new CategoriesDbAdapter(EditRoom1.this);
		mDbHelper.open();

		Cursor mCursor = mDbHelper.fetchSubjectsWithTypeAll();

		if (mCursor.getCount() != 0) {
			// mRMCursor.moveToFirst();
			Log.d("GrpRmPullService",
					"filldata(): count: " + mCursor.getCount());
			while (mCursor.moveToNext()) {

				// gets the content of each tag
				name = mCursor.getString(mCursor.getColumnIndex(TAG_NAME));
				typeName = mCursor.getString(mCursor
						.getColumnIndex(TAG_TYPENAME));

				// creating new HashMap and store all data
				HashMap<String, String> map = new HashMap<String, String>();

				map.put(TAG_NAME, name);
				map.put(TAG_TYPENAME, typeName);

				// adding HashList to ArrayList
				mCategoryList.add(map);
			}
		}
	}

	private void updateMap() {
		Log.i("sg.nyp.groupconnect", "updateMap");
		// To retrieve everything from Hashmap (mCommentList) and display all
		// rooms
		if (mCategoryList != null) {
			for (int i = 0; i < mCategoryList.size(); i++) {

				name = mCategoryList.get(i).get(TAG_NAME);
				typeName = mCategoryList.get(i).get(TAG_TYPENAME);

				if (typeName.equals("School Subjects")) {
					schSubList.add(name);
				} else if (typeName.equals("Music")) {
					musicList.add(name);
				} else if (typeName.equals("Computer-related")) {
					computerList.add(name);
				} else if (typeName.equals("Others")) {
					otherList.add(name);
				}

				listDataChild.put(listDataHeader.get(0), schSubList); // Header,
																		// Child
																		// data
				listDataChild.put(listDataHeader.get(1), musicList);
				listDataChild.put(listDataHeader.get(2), computerList);
				listDataChild.put(listDataHeader.get(3), otherList);

			}

		}

	}

	public class retrieveAllSubWithType extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.i("sg.nyp.groupconnect", "LoadRoom - Preexecute");
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			Log.i("sg.nyp.groupconnect", "LoadRoom - doInBackground");
			updateJSONdata();
			return null;

		}

		@Override
		protected void onPostExecute(Boolean result) {
			Log.i("sg.nyp.groupconnect", "LoadRoom - onPostExecute");
			super.onPostExecute(result);
			updateMap();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {

				Intent i = new Intent();
				i.putExtra("RoomId", data.getExtras().getInt("RoomId"));
				i.putExtra("RoomName", data.getExtras().getString("RoomName"));
				setResult(RESULT_OK, i);

				finish();
			}
		}
	}
}
