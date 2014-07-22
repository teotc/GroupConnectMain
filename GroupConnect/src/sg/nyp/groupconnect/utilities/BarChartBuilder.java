package sg.nyp.groupconnect.utilities;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.entity.fakeGrades;
import sg.nyp.groupconnect.entity.fakeMember;
import sg.nyp.groupconnect.entity.fakeSubjects;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BarChartBuilder extends Activity {
	private GraphicalView mChart;

	private String[] mMonth = new String[] { "result" };

	private int nameId = 0;
	private Bundle extras;
	private ArrayList<fakeMember> arrayFakeMember = new ArrayList<fakeMember>();
	private ArrayList<fakeGrades> arrayFakeGrade = new ArrayList<fakeGrades>();
	private ArrayList<fakeSubjects> arrayFakeSubject = new ArrayList<fakeSubjects>();
	private String category;
	private int resultBefore = 0, resultAfter = 0, categoryId = 0;

	// Database
	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();

	private static final String SUBJECT_URL = "http://www.it3197Project.3eeweb.com/grpConnect/retrieveSujects.php";
	private static final String MEMBER_URL = "http://www.it3197Project.3eeweb.com/grpConnect/retrieveMemberRecord.php";
	private static final String GRADE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/retrieveMemberGrade.php";

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_ARRAY = "posts";

	private static final String TAG_ID = "id";
	private static final String TAG_NAME = "name";
	private static final String TAG_LATITUDE = "latitude";
	private static final String TAG_LONGITUDE = "longitude";
	private static final String TAG_LOCATION = "location";
	private static final String TAG_GENDER = "gender";

	private static final String TAG_MEMBERID = "memberId";
	private static final String TAG_SUBJECTID = "subjectId";
	private static final String TAG_OLDGRADE = "oldGrade";
	private static final String TAG_NEWGRADE = "newGrade";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chart);

		extras = getIntent().getExtras();
		if (extras != null) {
			nameId = extras.getInt("NameId");
			category = extras.getString("Category");
		}

		new AttemptDetails().execute();
	}

	private void OpenChart() {
		// Define the number of elements you want in the chart.
		int z[] = { 0 };

		int x[] = { resultBefore };
		int y[] = { resultAfter };

		// Create XY Series for X Series.
		CategorySeries xSeries = new CategorySeries("Before");
		CategorySeries ySeries = new CategorySeries("Now");

		// Adding data to the X Series.
		for (int i = 0; i < z.length; i++) {
			xSeries.add(x[i]);
			ySeries.add(y[i]);
		}

		// Create a Dataset to hold the XSeries.

		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

		// Add X series to the Dataset.
		dataset.addSeries(xSeries.toXYSeries());
		dataset.addSeries(ySeries.toXYSeries());

		// Create XYSeriesRenderer to customize XSeries

		XYSeriesRenderer Xrenderer = new XYSeriesRenderer();
		Xrenderer.setColor(Color.GREEN);
		Xrenderer.setDisplayChartValues(true);
		Xrenderer.setChartValuesTextSize(25);

		XYSeriesRenderer Yrenderer = new XYSeriesRenderer();
		Yrenderer.setColor(Color.CYAN);
		Yrenderer.setDisplayChartValues(true);
		Yrenderer.setChartValuesTextSize(25);

		// Create XYMultipleSeriesRenderer to customize the whole chart

		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

		// mRenderer.setChartTitle("X Vs Y Chart");
		mRenderer.setXTitle("");
		mRenderer.setYTitle("Scores");
		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setXLabels(0);
		mRenderer.setPanEnabled(false);
		mRenderer.setMargins(new int[] { 20, 30, 15, 0 });

		mRenderer.setXAxisMin(0.5);
		mRenderer.setXAxisMax(1.5);
		mRenderer.setYAxisMin(0);
		mRenderer.setYAxisMax(100);
		mRenderer.setBarWidth(250f);

		mRenderer.setAxisTitleTextSize(20f);
		mRenderer.setChartTitleTextSize(18f);
		mRenderer.setLegendTextSize(20f);
		mRenderer.setLabelsTextSize(15f);
		mRenderer.setXLabelsPadding(20f);
		mRenderer.setYLabelsPadding(20f);

		mRenderer.setShowGrid(true);

		mRenderer.setClickEnabled(true);

		for (int i = 0; i < z.length; i++) {
			mRenderer.addXTextLabel(i + 1, mMonth[i]);
		}

		// Adding the XSeriesRenderer to the MultipleRenderer.
		mRenderer.addSeriesRenderer(Xrenderer);
		mRenderer.addSeriesRenderer(Yrenderer);

		LinearLayout chart_container = (LinearLayout) findViewById(R.id.Chart_layout);

		// Creating an intent to plot line chart using dataset and
		// multipleRenderer

		mChart = (GraphicalView) ChartFactory.getBarChartView(getBaseContext(),
				dataset, mRenderer, Type.DEFAULT);

		// Adding click event to the Line Chart.

		mChart.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				SeriesSelection series_selection = mChart
						.getCurrentSeriesAndPoint();

				if (series_selection != null) {
					int series_index = series_selection.getSeriesIndex();

					String select_series = "Before";
					if (series_index == 0) {
						select_series = "Before";
					} else {
						select_series = "Now";
					}

					int amount = (int) series_selection.getValue();

					Toast.makeText(getBaseContext(),
							select_series + " result: " + amount,
							Toast.LENGTH_LONG).show();
				}
			}
		});

		// Add the graphical view mChart object into the Linear layout .
		chart_container.addView(mChart);

	}

	private void alertDialog(String title, String message) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set title
		alertDialogBuilder.setTitle(title);

		// set dialog message
		alertDialogBuilder
				.setMessage(message)
				.setCancelable(false)
				.setNegativeButton("Okay",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, just close
								// the dialog box and do nothing
								BarChartBuilder.this.finish();
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	class AttemptDetails extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		boolean failure = false;
		public int success;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(BarChartBuilder.this);
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
				List<NameValuePair> params = new ArrayList<NameValuePair>();

				// getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(SUBJECT_URL,
						"POST", params);

				// json success tag
				success = json.getInt(TAG_SUCCESS);
				arrayFakeSubject.clear();

				for (int i = 0; i < json.getJSONArray(TAG_ARRAY).length(); i++) {

					JSONObject c = json.getJSONArray(TAG_ARRAY)
							.getJSONObject(i);

					fakeSubjects fs = new fakeSubjects(c.getInt(TAG_ID),
							c.getString(TAG_NAME));
					arrayFakeSubject.add(fs);
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
			new AttemptMember().execute();
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
			new AttemptGrade().execute();
		}
	}

	class AttemptGrade extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		boolean failure = false;
		public int success;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Check for success tag

			try {
				// Building Parameters
				List<NameValuePair> params2 = new ArrayList<NameValuePair>();

				// getting product details by making HTTP
				// request
				JSONObject json2 = jsonParser.makeHttpRequest(GRADE_URL,
						"POST", params2);

				// json success tag
				success = json2.getInt(TAG_SUCCESS);
				arrayFakeGrade.clear();

				for (int i = 0; i < json2.getJSONArray(TAG_ARRAY).length(); i++) {

					JSONObject c = json2.getJSONArray(TAG_ARRAY).getJSONObject(
							i);

					fakeGrades fg = new fakeGrades(c.getString(TAG_MEMBERID),
							c.getString(TAG_SUBJECTID), c.getInt(TAG_OLDGRADE),
							c.getInt(TAG_NEWGRADE));
					arrayFakeGrade.add(fg);
				}
				if (success == 1) {
					return json2.getString(TAG_MESSAGE);
				} else {
					return json2.getString(TAG_MESSAGE);

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
			for (int i = 0; i < arrayFakeSubject.size(); i++) {
				if (arrayFakeSubject.get(i).getName().equals(category)) {
					categoryId = arrayFakeSubject.get(i).getId();
				}
			}
			for (int j = 0; j < arrayFakeMember.size(); j++) {
				if (arrayFakeMember.get(j).getId() == nameId) {
					TextView tw = (TextView) findViewById(R.id.Chart_Tittle);
					tw.setText(arrayFakeMember.get(j).getName() + "'s "
							+ category + " Result");
				}
			}

			for (int k = 0; k < arrayFakeGrade.size(); k++) {
				if (arrayFakeGrade.get(k).getMemberId()
						.equals(Integer.toString(nameId))
						&& arrayFakeGrade.get(k).getSubjectId()
								.equals(Integer.toString(categoryId))) {

					resultBefore = arrayFakeGrade.get(k).getOldGrade();
					resultAfter = arrayFakeGrade.get(k).getNewGrade();
				}
			}

			if (resultBefore != 0 && resultAfter != 0) {
				OpenChart();
			} else
				alertDialog("Sorry", "The person you selected did not study "
						+ category);
		}

	}

}