package sg.nyp.groupconnect.utilities;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.entity.fakeMember;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PieChartBuilder extends Activity {
	/** Colors to be used for the pie slices. */
	private static int[] COLORS = new int[] { Color.GREEN, Color.BLUE,
			Color.MAGENTA, Color.CYAN, Color.RED, Color.YELLOW };
	/** The main series that will include all the data. */
	private CategorySeries mSeries = new CategorySeries("");
	/** The main renderer for the main dataset. */
	private DefaultRenderer mRenderer = new DefaultRenderer();
	/** The chart view that displays the data. */
	private GraphicalView mChartView;

	private String category, schoolName;
	private Bundle extras;
	private ArrayList<fakeMember> arrayFakeMember = new ArrayList<fakeMember>();
	private Intent intent = null;
	
	// Database
	private ProgressDialog pDialog;

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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chart);

		extras = getIntent().getExtras();
		if (extras != null) {
			category = extras.getString("Category");
			schoolName = extras.getString("School_Name");
		}

		TextView tw = (TextView) findViewById(R.id.Chart_Tittle);
		tw.setText(schoolName + " - " + category);

		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setStartAngle(180);
		mRenderer.setDisplayValues(true);
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(Color.BLACK);
		mRenderer.setChartTitle("Total No. of Student in each grade.");
		mRenderer.setChartTitleTextSize(25f);
		mRenderer.setLegendTextSize(25f);
		mRenderer.setLabelsColor(Color.WHITE);
		mRenderer.setLabelsTextSize(20f);

		Double[] values = new Double[] { 40.0, 50.0, 60.0, 70.0, 80.0, 90.0 };
		String[] grades = new String[] { "Grade A", "Grade B", "Grade C",
				"Grade D", "Grade E", "Grade F" };

		for (int i = 0; i < values.length; i++) {
			mSeries.add(grades[i], values[i]);
			SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
			renderer.setColor(COLORS[(mSeries.getItemCount() - 1)
					% COLORS.length]);
			mRenderer.addSeriesRenderer(renderer);
			// mChartView.repaint();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mChartView == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.Chart_layout);
			mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);
			mRenderer.setClickEnabled(true);
			mChartView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SeriesSelection seriesSelection = mChartView
							.getCurrentSeriesAndPoint();
					if (seriesSelection == null) {

					} else {
						for (int i = 0; i < mSeries.getItemCount(); i++) {
							mRenderer.getSeriesRendererAt(i).setHighlighted(
									i == seriesSelection.getPointIndex());
						}
						mChartView.repaint();
						// if (seriesSelection.getPointIndex() == 0) {
						// Toast.makeText(
						// PieChartBuilder.this,
						// seriesSelection.getValue()
						// + " people scored Grade A.",
						// Toast.LENGTH_SHORT).show();
						// } else if (seriesSelection.getPointIndex() == 1) {
						// Toast.makeText(
						// PieChartBuilder.this,
						// seriesSelection.getValue()
						// + " people scored Grade B.",
						// Toast.LENGTH_SHORT).show();
						// } else if (seriesSelection.getPointIndex() == 2) {
						// Toast.makeText(
						// PieChartBuilder.this,
						// seriesSelection.getValue()
						// + " people scored Grade C.",
						// Toast.LENGTH_SHORT).show();
						// } else if (seriesSelection.getPointIndex() == 3) {
						// Toast.makeText(
						// PieChartBuilder.this,
						// seriesSelection.getValue()
						// + " people scored Grade D.",
						// Toast.LENGTH_SHORT).show();
						// } else if (seriesSelection.getPointIndex() == 4) {
						// Toast.makeText(
						// PieChartBuilder.this,
						// seriesSelection.getValue()
						// + " people scored Grade E.",
						// Toast.LENGTH_SHORT).show();
						// } else if (seriesSelection.getPointIndex() == 5) {
						// Toast.makeText(
						// PieChartBuilder.this,
						// seriesSelection.getValue()
						// + " people scored Grade F.",
						// Toast.LENGTH_SHORT).show();
						// }
						new AttemptMember().execute();
					}
				}
			});
			layout.addView(mChartView, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		} else {
			mChartView.repaint();
		}
	}

	class AttemptMember extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		boolean failure = false;
		public int success;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(PieChartBuilder.this);
			pDialog.setMessage("Retreiving data...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Check for success tag

			try {
				// Building Parameters
				List<NameValuePair> params1 = new ArrayList<NameValuePair>();

				// getting product details by making HTTP request
				JSONObject json1 = jsonParser.makeHttpRequest(MEMBER_URL,
						"POST", params1);

				// json success tag
				success = json1.getInt(TAG_SUCCESS);
				arrayFakeMember.clear();

				for (int i = 0; i < json1.getJSONArray(TAG_ARRAY).length(); i++) {

					JSONObject c = json1.getJSONArray(TAG_ARRAY).getJSONObject(
							i);

					fakeMember fm = new fakeMember(c.getInt(TAG_ID),
							c.getString(TAG_NAME), c.getString(TAG_LOCATION),
							c.getDouble(TAG_LATITUDE),
							c.getDouble(TAG_LONGITUDE), c.getString(TAG_GENDER));
					arrayFakeMember.add(fm);
				}
				if (success == 1) {
					return json1.getString(TAG_MESSAGE);
				} else {
					return json1.getString(TAG_MESSAGE);

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
			pDialog.dismiss();
			alertDialog();
		}
	}
	
	private void alertDialog() {
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
		builderSingle.setTitle("Select A Name: ");
		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				getBaseContext(), android.R.layout.select_dialog_singlechoice);

		for (int i = 0; i < arrayFakeMember.size(); i++) {
			arrayAdapter.add(arrayFakeMember.get(i).getName());
		}

		builderSingle.setNegativeButton("cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builderSingle.setAdapter(arrayAdapter,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String strName = arrayAdapter.getItem(which);

						for (int i = 0; i < arrayFakeMember.size(); i++) {
							if (arrayFakeMember.get(i).getName().equals(strName)) {
								
								final int nameId = arrayFakeMember.get(i).getId();
								
								AlertDialog.Builder builderInner = new AlertDialog.Builder(
										PieChartBuilder.this);
								builderInner.setMessage(arrayFakeMember.get(i).getName() + " living in " + arrayFakeMember.get(i).getLocation());
								builderInner.setTitle("You have selected");
								builderInner.setPositiveButton("Yes",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												 intent = new Intent(getBaseContext(), BarChartBuilder.class);
												 intent.putExtra("NameId", nameId);
												 intent.putExtra("Category", category);
												 startActivity(intent);
											}
										});
								builderInner.setNegativeButton("No",
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int id) {
												// if this button is clicked, just close
												// the dialog box and do nothing
												dialog.cancel();
											}
										});
								builderInner.show();
							}
						}
					}
				});
		builderSingle.show();
	}
}
