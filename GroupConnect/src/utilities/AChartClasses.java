package utilities;

import java.util.Random;

import org.achartengine.chart.*;
import org.achartengine.model.*;
import org.achartengine.renderer.*;
import android.graphics.Color;

public class AChartClasses {

	public XYMultipleSeriesDataset getDemoDataset(int SERIES_NR) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		final int nr = 5;
		Random r = new Random();
		for (int i = 0; i < SERIES_NR; i++) {
			XYSeries series = new XYSeries("Demo series " + (i + 1));
			for (int k = 0; k < nr; k++) {
				series.add(k, 20 + r.nextInt() % 100);
			}
			dataset.addSeries(series);
		}
		return dataset;
	}

	public XYMultipleSeriesDataset getBarDemoDataset(int SERIES_NR) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		final int nr = 5;
		Random r = new Random();
		for (int i = 0; i < SERIES_NR; i++) {
			CategorySeries series = new CategorySeries("Demo series " + (i + 1));
			for (int k = 0; k < nr; k++) {
				series.add(100 + r.nextInt() % 100);
			}
			dataset.addSeries(series.toXYSeries());
		}
		return dataset;
	}

	public XYMultipleSeriesRenderer getDemoRenderer() {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(20);
		renderer.setChartTitleTextSize(25);
		renderer.setLabelsTextSize(20);
		renderer.setLegendTextSize(25);
		renderer.setPointSize(5f);
		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(Color.BLACK);
		renderer.setMargins(new int[] { 20, 30, 15, 0 });
		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(Color.BLUE);
		r.setPointStyle(PointStyle.SQUARE);
		r.setFillPoints(true);
		renderer.addSeriesRenderer(r);
		r = new XYSeriesRenderer();
		r.setPointStyle(PointStyle.CIRCLE);
		r.setColor(Color.GREEN);
		r.setFillPoints(true);
		renderer.addSeriesRenderer(r);
		renderer.setAxesColor(Color.DKGRAY);
		renderer.setLabelsColor(Color.LTGRAY);
		return renderer;
	}

	public XYMultipleSeriesRenderer getBarDemoRenderer() {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(20);
		renderer.setChartTitleTextSize(25);
		renderer.setLabelsTextSize(20);
		renderer.setLegendTextSize(25);
		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(Color.BLACK);
		renderer.setMargins(new int[] { 20, 30, 15, 0 });
		SimpleSeriesRenderer r = new SimpleSeriesRenderer();
		r.setColor(Color.BLUE);
		renderer.addSeriesRenderer(r);
		r = new SimpleSeriesRenderer();
		r.setColor(Color.GREEN);
		renderer.addSeriesRenderer(r);
		return renderer;
	}

	public void setChartSettings(XYMultipleSeriesRenderer renderer) {
		renderer.setChartTitle("Chart demo");
		renderer.setXTitle("x values");
		renderer.setYTitle("y values");
		renderer.setXAxisMin(0.5);
		renderer.setXAxisMax(6);
		renderer.setYAxisMin(0);
		renderer.setYAxisMax(210);
	}
}
