package sg.nyp.groupconnect.utilities;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import sg.nyp.groupconnect.Map;
import sg.nyp.groupconnect.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class VotingfPieChartBuilder extends Activity {
	/** Colors to be used for the pie slices. */
	private static int[] COLORS = new int[] { Color.GREEN, Color.BLUE,
			Color.MAGENTA, Color.CYAN, Color.RED, Color.YELLOW };
	/** The main series that will include all the data. */
	private CategorySeries mSeries = new CategorySeries("");
	/** The main renderer for the main dataset. */
	private DefaultRenderer mRenderer = new DefaultRenderer();
	/** The chart view that displays the data. */
	private GraphicalView mChartView;

	private Bundle extras;

	private String[] array = new String[5];
	private String[] places = new String[5];
	private Double[] percentage = new Double[5];

	private Button btnFinal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.voting_chart);

		extras = getIntent().getExtras();
		if (extras != null) {
			places = extras.getStringArray("PlacesArray");
			array = extras.getStringArray("PercentageArray");
		}

		for (int i = 0; i < percentage.length; i++) {
			percentage[i] = Double.parseDouble(array[i]);
		}

		TextView tw = (TextView) findViewById(R.id.Chart_Tittle);
		tw.setText("Voting Result");

		btnFinal = (Button) findViewById(R.id.btnFinal);
		btnFinal.setVisibility(View.VISIBLE);

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

		for (int i = 0; i < percentage.length; i++) {
			mSeries.add(places[i], percentage[i]);
			SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
			renderer.setColor(COLORS[(mSeries.getItemCount() - 1)
					% COLORS.length]);
			mRenderer.addSeriesRenderer(renderer);
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
						if (seriesSelection.getPointIndex() == 0) {
							Toast.makeText(
									VotingfPieChartBuilder.this,
									seriesSelection.getValue()
											+ " people voted for " + places[0]
											+ ".", Toast.LENGTH_SHORT).show();
						} else if (seriesSelection.getPointIndex() == 1) {
							Toast.makeText(
									VotingfPieChartBuilder.this,
									seriesSelection.getValue()
											+ " people voted for " + places[1]
											+ ".", Toast.LENGTH_SHORT).show();
						} else if (seriesSelection.getPointIndex() == 2) {
							Toast.makeText(
									VotingfPieChartBuilder.this,
									seriesSelection.getValue()
											+ " people voted for " + places[2]
											+ ".", Toast.LENGTH_SHORT).show();
						} else if (seriesSelection.getPointIndex() == 3) {
							Toast.makeText(
									VotingfPieChartBuilder.this,
									seriesSelection.getValue()
											+ " people voted for " + places[3]
											+ ".", Toast.LENGTH_SHORT).show();
						} else if (seriesSelection.getPointIndex() == 4) {
							Toast.makeText(
									VotingfPieChartBuilder.this,
									seriesSelection.getValue()
											+ " people voted for " + places[4]
											+ ".", Toast.LENGTH_SHORT).show();
						}
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
				.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialog,
									int id) {
								dialog.cancel();
							}
						})
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialog,
									int id) {

								double max = percentage[0];
								int pos = 0;

								for (int i = 0; i < percentage.length; i++) {
									if (percentage[i] > max) {
										max = percentage[i];
										pos = i;
									}
								}
								Map.mMap.clear();

								switch (pos) {
								case 0:
									LatLng coordinate1 = new LatLng(1.325424, 103.932386);

									Marker bcl = Map.mMap.addMarker(new MarkerOptions()
											.position(coordinate1)
											.title("Bedok Community Library")
											.icon(BitmapDescriptorFactory
													.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
									break;
								case 1:
									LatLng coordinate2 = new LatLng(1.333661, 103.854108);

									Marker tppl = Map.mMap.addMarker(new MarkerOptions()
											.position(coordinate2)
											.title("Toa Payoh Public Library")
											.icon(BitmapDescriptorFactory
													.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
									break;
								case 2:
									LatLng coordinate3 = new LatLng(1.378967, 103.849988);

									Marker amkpl = Map.mMap.addMarker(new MarkerOptions()
											.position(coordinate3)
											.title("Ang Mo Kio Public Library")
											.icon(BitmapDescriptorFactory
													.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

									break;
								case 3:
									LatLng coordinate4 = new LatLng(1.370730, 103.895307);

									Marker hgpl = Map.mMap.addMarker(new MarkerOptions()
											.position(coordinate4)
											.title("Cheng San Public Library")
											.icon(BitmapDescriptorFactory
													.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

									break;
								case 4:
									LatLng coordinate5 = new LatLng(1.429764, 103.840375);

									Marker ypl = Map.mMap.addMarker(new MarkerOptions()
											.position(coordinate5)
											.title("Yishun Public Library")
											.icon(BitmapDescriptorFactory
													.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
									break;
								}
								Map.mMap
										.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
											@Override
											public void onInfoWindowClick(final Marker marker) {

											}
										});
								VotingfPieChartBuilder.this.finish();
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
}
