/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package utilities;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import sg.nyp.groupconnect.R;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xy_chart);
		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setStartAngle(180);
		mRenderer.setDisplayValues(true);
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(Color.BLACK);
		mRenderer.setChartTitle("Total No. of Student in each grade");
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
			LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
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
									PieChartBuilder.this,
									seriesSelection.getValue()
											+ " people scored Grade A.",
									Toast.LENGTH_SHORT).show();
						} else if (seriesSelection.getPointIndex() == 1) {
							Toast.makeText(
									PieChartBuilder.this,
									seriesSelection.getValue()
											+ " people scored Grade B.",
									Toast.LENGTH_SHORT).show();
						} else if (seriesSelection.getPointIndex() == 2) {
							Toast.makeText(
									PieChartBuilder.this,
									seriesSelection.getValue()
											+ " people scored Grade C.",
									Toast.LENGTH_SHORT).show();
						} else if (seriesSelection.getPointIndex() == 3) {
							Toast.makeText(
									PieChartBuilder.this,
									seriesSelection.getValue()
											+ " people scored Grade D.",
									Toast.LENGTH_SHORT).show();
						} else if (seriesSelection.getPointIndex() == 4) {
							Toast.makeText(
									PieChartBuilder.this,
									seriesSelection.getValue()
											+ " people scored Grade E.",
									Toast.LENGTH_SHORT).show();
						} else if (seriesSelection.getPointIndex() == 5) {
							Toast.makeText(
									PieChartBuilder.this,
									seriesSelection.getValue()
											+ " people scored Grade F.",
									Toast.LENGTH_SHORT).show();
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
}
