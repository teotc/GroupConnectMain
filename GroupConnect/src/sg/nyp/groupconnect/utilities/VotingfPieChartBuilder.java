package sg.nyp.groupconnect.utilities;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.Map;
import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.data.RoomDbAdapter;
import sg.nyp.groupconnect.data.VoteLocationDbAdapter;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class VotingfPieChartBuilder extends Activity {
	/** Colors to be used for the pie slices. */
	private static int[] COLORS;;
	/** The main series that will include all the data. */
	private CategorySeries mSeries = new CategorySeries("");
	/** The main renderer for the main dataset. */
	private DefaultRenderer mRenderer = new DefaultRenderer();
	/** The chart view that displays the data. */
	private GraphicalView mChartView;

	private Bundle extras;

	private String[] places;
	private Double[] percentage;
	private String highLocation;
	private double highLocationLat;
	private double highLocationLng;
	private int highLocationId;
	private int confirm;
	private int num;

	private int stat;
	private String currentRoomId;

	private Button btnFinal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.voting_chart);

		extras = getIntent().getExtras();
		if (extras != null) {
			currentRoomId = extras.getString("CURRENT_ROOM_ID");
			stat = extras.getInt("CREATOR_STATUS");
		}

		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.back);
		actionBar.setHomeButtonEnabled(true);

		btnFinal = (Button) findViewById(R.id.btnFinal);

		if (stat != 1) {
			btnFinal.setVisibility(View.GONE);
		} else
			btnFinal.setVisibility(View.VISIBLE);

		TextView tw = (TextView) findViewById(R.id.Chart_Tittle);
		tw.setText("Voting Result");

		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setStartAngle(180);
		mRenderer.setDisplayValues(true);
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(Color.BLACK);
		mRenderer.setChartTitle("Total No. of Student in each grade.");
		mRenderer.setChartTitleTextSize(40f); // TODO
		mRenderer.setLegendTextSize(40f); // TODO
		mRenderer.setLabelsColor(Color.WHITE);
		mRenderer.setLabelsTextSize(30f); // TODO
		mRenderer.setPanEnabled(false);
		mRenderer.setZoomEnabled(false);

		new RetrieveResult().execute();

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
						Toast.makeText(
								VotingfPieChartBuilder.this,
								seriesSelection.getValue()
										+ "% of the people voted for " + places[0]
										+ ".", Toast.LENGTH_SHORT).show();

					}
				}
			});
			layout.addView(mChartView, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		} else {
			mChartView.repaint();
		}
	}

	public void FinalLocation(View v) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				VotingfPieChartBuilder.this);

		// set title
		alertDialogBuilder.setTitle("Finalize Location");

		// set dialog message
		alertDialogBuilder
				.setMessage("Are you sure you want to finalize the location?")
				.setCancelable(false)
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				})
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								new FinalizeLocation().execute();

							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	class RetrieveResult extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		boolean failure = false;
		public int success;

		// Database
		private ProgressDialog pDialog;

		private static final String TAG_LOCATIONID = "locationId";
		private static final String TAG_NAME = "name";
		private static final String TAG_COUNTVALUE = "countValue";
		private static final String TAG_STATUS = "status";
		private static final String TAG_LOCATION = "location";
		private static final String TAG_LATITUDE = "latitude";
		private static final String TAG_LONGITUDE = "longitude";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Map.context);
			pDialog.setMessage("Retreiving data...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {

			VoteLocationDbAdapter mDbHelper = new VoteLocationDbAdapter(
					VotingfPieChartBuilder.this);
			mDbHelper.open();

			Cursor mCursor = mDbHelper.fetchVoteResult(currentRoomId);

			places = new String[mCursor.getCount()];
			percentage = new Double[mCursor.getCount()];

			int max = 0;

			DecimalFormat df = new DecimalFormat("#.00");

			num = mCursor.getCount();
			
			if (mCursor.getCount() != 0) {
				mCursor.moveToFirst();
				int i = 0;

				places[i] = mCursor.getString(mCursor.getColumnIndex(TAG_NAME));

				double score = (Double.parseDouble(Integer.toString(mCursor
						.getInt(mCursor.getColumnIndex(TAG_COUNTVALUE)))))
						/ mCursor.getCount() * 100;

				percentage[i] = Double.parseDouble(df.format(score));

				if (mCursor.getString(mCursor.getColumnIndex(TAG_STATUS))
						.equals("final")) {
					confirm = 1;
				}

				if (mCursor.getInt(mCursor.getColumnIndex(TAG_COUNTVALUE)) > max) {
					highLocationId = mCursor.getInt(mCursor
							.getColumnIndex(TAG_LOCATIONID));
					max = mCursor
							.getInt(mCursor.getColumnIndex(TAG_COUNTVALUE));
					highLocation = mCursor.getString(mCursor
							.getColumnIndex(TAG_LOCATION));
					highLocationLat = mCursor.getDouble(mCursor
							.getColumnIndex(TAG_LATITUDE));
					highLocationLng = mCursor.getDouble(mCursor
							.getColumnIndex(TAG_LONGITUDE));
					highLocationId = mCursor.getInt(mCursor
							.getColumnIndex(TAG_LOCATIONID));
				}

				while (mCursor.moveToNext()) {
					i++;

					places[i] = mCursor.getString(mCursor
							.getColumnIndex(TAG_NAME));

					score = (Double.parseDouble(Integer.toString(mCursor
							.getInt(mCursor.getColumnIndex(TAG_COUNTVALUE)))))
							/ mCursor.getCount() * 100;

					percentage[i] = Double.parseDouble(df.format(score));

					if (mCursor.getString(mCursor.getColumnIndex(TAG_STATUS))
							.equals("final")) {
						confirm = 1;
					}

					if (mCursor.getInt(mCursor.getColumnIndex(TAG_COUNTVALUE)) > max) {
						highLocationId = mCursor.getInt(mCursor
								.getColumnIndex(TAG_LOCATIONID));
						max = mCursor.getInt(mCursor
								.getColumnIndex(TAG_COUNTVALUE));
						highLocation = mCursor.getString(mCursor
								.getColumnIndex(TAG_LOCATION));
						highLocationLat = mCursor.getDouble(mCursor
								.getColumnIndex(TAG_LATITUDE));
						highLocationLng = mCursor.getDouble(mCursor
								.getColumnIndex(TAG_LONGITUDE));
						highLocationId = mCursor.getInt(mCursor
								.getColumnIndex(TAG_LOCATIONID));
					}
				}
			}

			mCursor.close();
			mDbHelper.close();

			return null;

		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product deleted

			if (percentage.length == 1) {
				COLORS = new int[] { Color.rgb(38, 166, 91) };
			} else if (percentage.length == 2) {
				COLORS = new int[] { Color.rgb(38, 166, 91),
						Color.rgb(192, 57, 43) };
			} else if (percentage.length == 3) {
				COLORS = new int[] { Color.rgb(38, 166, 91),
						Color.rgb(247, 202, 24), Color.rgb(192, 57, 43) };
			} else if (percentage.length == 4) {
				COLORS = new int[] { Color.rgb(38, 166, 91),
						Color.rgb(247, 202, 24), Color.rgb(232, 126, 4),
						Color.rgb(192, 57, 43) };
			} else if (percentage.length == 5) {
				COLORS = new int[] { Color.rgb(38, 166, 91),
						Color.rgb(144, 198, 149), Color.rgb(247, 202, 24),
						Color.rgb(232, 126, 4), Color.rgb(192, 57, 43) };
			} else if (percentage.length == 6) {
				COLORS = new int[] { Color.rgb(38, 166, 91),
						Color.rgb(144, 198, 149), Color.rgb(247, 202, 24),
						Color.rgb(232, 126, 4), Color.rgb(192, 57, 43),
						Color.rgb(217, 30, 24) };
			} else if (percentage.length == 7) {
				COLORS = new int[] { Color.rgb(38, 166, 91),
						Color.rgb(144, 198, 149), Color.rgb(245, 215, 110),
						Color.rgb(247, 202, 24), Color.rgb(232, 126, 4),
						Color.rgb(192, 57, 43), Color.rgb(217, 30, 24) };
			} else if (percentage.length == 8) {
				COLORS = new int[] { Color.rgb(38, 166, 91),
						Color.rgb(3, 166, 120), Color.rgb(144, 198, 149),
						Color.rgb(245, 215, 110), Color.rgb(247, 202, 24),
						Color.rgb(232, 126, 4), Color.rgb(192, 57, 43),
						Color.rgb(217, 30, 24) };
			} else if (percentage.length == 9) {
				COLORS = new int[] { Color.rgb(38, 166, 91),
						Color.rgb(3, 166, 120), Color.rgb(144, 198, 149),
						Color.rgb(245, 215, 110), Color.rgb(247, 202, 24),
						Color.rgb(232, 126, 4), Color.rgb(231, 76, 60),
						Color.rgb(192, 57, 43), Color.rgb(217, 30, 24) };
			} else if (percentage.length == 10) {
				COLORS = new int[] { Color.rgb(38, 166, 91),
						Color.rgb(3, 166, 120), Color.rgb(144, 198, 149),
						Color.rgb(245, 215, 110), Color.rgb(247, 202, 24),
						Color.rgb(232, 126, 4), Color.rgb(242, 120, 75),
						Color.rgb(231, 76, 60), Color.rgb(192, 57, 43),
						Color.rgb(217, 30, 24) };
			}

			for (int i = 0; i < percentage.length; i++) {
				mSeries.add(places[i], percentage[i]);
				SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
				renderer.setColor(COLORS[(mSeries.getItemCount() - 1)
						% COLORS.length]);
				mRenderer.addSeriesRenderer(renderer);
			}
			mChartView.repaint();

			pDialog.dismiss();

			if (num != 0) {
				if (confirm == 1) {
					btnFinal.setVisibility(View.GONE);
				}

			} else {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						VotingfPieChartBuilder.this);

				// set dialog message
				alertDialogBuilder
						.setMessage("Sorry. Voting have not start.")
						.setCancelable(false)
						.setNegativeButton("Okay",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
										VotingfPieChartBuilder.this.finish();
									}
								});

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();
			}

		}
	}

	class FinalizeLocation extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		boolean failure = false;
		public int success;

		// Database
		private ProgressDialog pDialog;

		JSONParser jsonParser = new JSONParser();

		private static final String RETRIEVE_VOTE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/statics/updateVote.php";

		private static final String TAG_SUCCESS = "success";
		private static final String TAG_MESSAGE = "message";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Map.context);
			pDialog.setMessage("Finalizing...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// Check for success tag

			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("locationId", Integer
						.toString(highLocationId)));
				params.add(new BasicNameValuePair("location", highLocation));
				params.add(new BasicNameValuePair("roomId", currentRoomId));
				params.add(new BasicNameValuePair("latLng", highLocationLat
						+ "," + highLocationLng));

				// getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(RETRIEVE_VOTE_URL,
						"POST", params);

				// json success tag
				success = json.getInt(TAG_SUCCESS);

				if (success == 1) {

					VoteLocationDbAdapter mDbHelper = new VoteLocationDbAdapter(
							VotingfPieChartBuilder.this);
					mDbHelper.open();

					mDbHelper.updateVoteLocation(
							Integer.toString(highLocationId), currentRoomId);

					mDbHelper.close();

					RoomDbAdapter mDbHelper2 = new RoomDbAdapter(
							VotingfPieChartBuilder.this);
					mDbHelper2.open();

					mDbHelper2.updateRoom(highLocation, highLocationLat + ","
							+ highLocationLng, Integer.parseInt(currentRoomId));

					mDbHelper2.close();

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
			// dismiss the dialog once product deleted
			pDialog.dismiss();
			if (success == 1) {
				btnFinal.setVisibility(View.GONE);
				Toast.makeText(VotingfPieChartBuilder.this,
						"You have finalize the location.", Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			VotingfPieChartBuilder.this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
