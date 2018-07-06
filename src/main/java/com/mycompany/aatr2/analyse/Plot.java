package com.mycompany.aatr2.analyse;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
//import org.jfree.ui.RefineryUtilities;
public class Plot extends ApplicationFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String sTitle;
	private XYSeries series ;
	/**
	 * 
	 * @param subtitle: Container ID
	 * @param Main title: Name of the Container Image
	 */
	public Plot(String main_title, String sub_title) {
		super(main_title);
		this.sTitle = sub_title;
		this.series = new XYSeries(main_title);
	}
	
	/**
	 * 
	 * @param x
	 *            axis data
	 * @param y
	 *            axis data
	 */
	public void plot(double[] x, double[] y) {
		
		for(int i=0, j =0; i<x.length && j<y.length; i++, j++) {
			
			series.add(x[i], y[j]);
		}
		XYSeriesCollection data = new XYSeriesCollection(series);
		JFreeChart chart = ChartFactory.createXYLineChart(sTitle, "X", "Y", data, PlotOrientation.VERTICAL,
				true, true, false);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
	    setContentPane(chartPanel);
	    
	}

	public String getThisTitle() {
		return sTitle;
	}

}
