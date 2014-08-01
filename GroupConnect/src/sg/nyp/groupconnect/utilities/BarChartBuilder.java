package sg.nyp.groupconnect.utilities;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import sg.nyp.groupconnect.R;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BarChartBuilder extends Activity {
	private GraphicalView mChart;

	private String[] mMonth = new String[] { "result" };

	private Bundle extras;
	private String category, name;
	private double resultBefore = 0, resultAfter = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chart);

		extras = getIntent().getExtras();
		if (extras != null) {
			name = extras.getString("Name");
			category = extras.getString("Category");
			resultBefore= extras.getDouble("oldGrade");
			resultAfter= extras.getDouble("newGrade");
		}
		
		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.back);
		actionBar.setHomeButtonEnabled(true);
		
		TextView tw = (TextView) findViewById(R.id.Chart_Tittle);
		tw.setText(name + "'s "
				+ category + " Result");
		
		OpenChart();
		
		Button btn = (Button) findViewById(R.id.btnGroup);
		btn.setVisibility(View.GONE);
	}

	private void OpenChart() {
		// Define the number of elements you want in the chart.
		int z[] = { 0 };

		double x[] = { resultBefore };
		double y[] = { resultAfter };

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

		mRenderer.setZoomEnabled(false, false);
		
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
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            BarChartBuilder.this.finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}