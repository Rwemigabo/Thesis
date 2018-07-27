package com.mycompany.aatr2.analyse;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;

//import org.jfree.ui.RefineryUtilities;
public class Plot extends ApplicationFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String sTitle;
	private final String titleExt;
	private TimeSeries series;

	/**
	 * 
	 * @param subtitle:
	 *            Container ID
	 * @param Main
	 *            title: Name of the Container Image
	 */
	public Plot(String main_title, String mtitle_ext, String sub_title) {
		super(main_title);
		this.titleExt = mtitle_ext;
		this.sTitle = sub_title;
		this.series = new TimeSeries(main_title, Second.class);
	}

	/**
	 * 
	 * @param x
	 *            axis data
	 * @param y
	 *            axis data
	 */
	public void plot(long[] x, double[] y) {

		for (int i = 0, j = 0; i < x.length && j < y.length; i++, j++) {
			Second time1 = new Second(new Date((long) x[i]));
			series.addOrUpdate(time1, y[j]);
		}
		XYDataset data = new TimeSeriesCollection(series);
		JFreeChart chart = ChartFactory.createTimeSeriesChart(this.getTitle()+" " + titleExt, "X", "Y", data, true, true, false);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(560, 370));
		setContentPane(chartPanel);

		File n_Chart = new File("../Plots/"+ getTitle()+ "_"+titleExt+"_"+sTitle+".jpeg");
		try {
			ChartUtilities.saveChartAsJPEG(n_Chart, chart, 560, 370);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String getThisTitle() {
		return sTitle;
	}

}
