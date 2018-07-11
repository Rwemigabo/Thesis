package com.mycompany.aatr2.analyse;
import javax.swing.JFrame;
import org.math.plot.*;

public class GradientDescent{
	private double theta0;
	private double theta1;

	private double[] x;
	private double[] y;
	
	private int trendline;

	// Algorithm settings
    double alpha = 0.01;  // learning rate
    double tol = 1e-11;   // tolerance to determine convergence
    int maxiter = 9000;   // maximum number of iterations in case convergence is not reached
    int dispiter = 100;   // interval for displaying results during iterations
    int iters = 0;

    //track of results
    double[] theta0plot = new double[maxiter+1];
    double[] theta1plot = new double[maxiter+1];
    double[] tplot = new double[maxiter+1];

    //InitialData initial_data;

    Plot2DPanel plot;

    public GradientDescent(double[] xdata, double[] ydata){
		//initial guesses
    	this.theta0 = 0;
    	this.theta1 = 0;
    	this.x = xdata;
    	this.y = ydata;

    	plot = new Plot2DPanel();
    	plot.addScatterPlot("X-Y", x, y);
    	JFrame frame = new JFrame("Final X-Y Data");
    	frame.setContentPane(plot);
    	frame.setSize(600, 600);
    	frame.setVisible(true);
    }

    public void execute(){
    	do {

    		this.theta1 -= alpha * deriveTheta1();
    		this.theta0 -= alpha * deriveTheta0();

    		//used for plotting
    		tplot[iters] = iters;
    		theta0plot[iters] = theta0;
    		theta1plot[iters] = theta1;
    		iters++;

    		if (iters % dispiter == 0){
    			addTrendLine(plot, true);

    		}

    		if (iters > maxiter) break;
    	} while (Math.abs(theta1) > tol || Math.abs(theta0) > tol);
    	plot.addScatterPlot("X-Y", x, y);
    	System.out.println("theta0 = " + this.theta0 + " and theta1 = " + this.theta1);
    }

    public double hypothesisFunction(double x){
    	return this.theta1*x + theta0;
    }

    public double deriveTheta1(){
    	double sum = 0;

    	for (int j=0; j<x.length; j++){
    		sum += (y[j] - hypothesisFunction(x[j])) * x[j];
    	}
    	return -2 * sum / x.length;
    }

    public double deriveTheta0(){
    	double sum = 0;

    	for (int j=0; j<x.length; j++) {
    		sum += y[j] - hypothesisFunction(x[j]);
    	}
    	return -2 * sum / x.length;

    }

    public void addTrendLine(Plot2DPanel plot, boolean removePrev){
    	if (removePrev){
    		plot.removePlot(trendline);
    	}
    	double[] yEnd = new double[x.length];
    	for (int i=0; i<x.length; i++)
    		yEnd[i] = hypothesisFunction(x[i]);
    	trendline = plot.addLinePlot("final", x, yEnd);
    }


    public void printConvergence(){

      double[] theta0plot2 = new double[iters];
      double[] theta1plot2 = new double[iters];
      double[] tplot2 = new double[iters];
      System.arraycopy(theta0plot, 0, theta0plot2, 0, iters);
      System.arraycopy(theta1plot, 0, theta1plot2, 0, iters);
      System.arraycopy(tplot, 0, tplot2, 0, iters);
 		
      // Plot the convergence of data
      Plot2DPanel convPlot = new Plot2DPanel();
 
      // add a line plot to the PlotPanel
      convPlot.addLinePlot("theta0", tplot2, theta0plot2);
      convPlot.addLinePlot("theta1", tplot2, theta1plot2);
 
      // put the PlotPanel in a JFrame, as a JPanel
      JFrame frame2 = new JFrame("Convergence of parameters over time");
      frame2.setContentPane(convPlot);
      frame2.setSize(600, 600);
      frame2.setVisible(true);
    }
}