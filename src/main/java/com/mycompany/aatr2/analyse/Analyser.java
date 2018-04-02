/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.analyse;

import com.mycompany.aatr2.Observable;
import com.mycompany.aatr2.Observer;
import com.mycompany.aatr2.monitor.data.Statistic;
import com.mycompany.aatr2.monitor.data.StatisticsLog;
import java.util.ArrayList;
import net.sourceforge.jFuzzyLogic.FIS;

/**
 * Analyze class performs an analysis on the data received from the monitor a
 * thread is created for each of the services in order to perform an analysis on
 * the data recorded on that service
 * 
 * @author eric
 */
public class Analyser implements Observable, Observer {

	private final int anId;
	private final ArrayList<Observer> obs;
	//private Observable obvle = null;
	private final ArrayList<StatisticsLog> logs;

	public Analyser(int id) {
		this.anId = id;
		this.obs = new ArrayList<>();
		this.logs = new ArrayList<>();
		// this.obvle = new ArrayList<>();
	}

	/**
	 * Takes the average memory and cup statistics for a service and uses fuzzy
	 * logic analysis to estimate the containers needed to optimize the service
	 * 
	 * @param avrcpu
	 * @param avrmem
	 */
	public void diagnoseService(double avrcpu, double avrmem) {
		String fileName = "./SystAnalysis.fcl";
		FIS fis = FIS.load(fileName, true);

		if (fis == null) {
			System.err.println("Can't load file: '" + fileName + "'");
			return;
		}

		// Show
		// JFuzzyChart.get().chart(functionBlock);

		// Set inputs
		fis.setVariable("CPU_load", avrcpu);
		fis.setVariable("food", avrmem);

		// Evaluate
		fis.evaluate();

	}

	/**
	 * perform analysis on 'historical data'
	 */
	public void analyseHistory(ArrayList<Statistic> stats) {

	}

	/**
	 * Get the value of anId
	 *
	 * @return the value of anId
	 */
	public int getAnId() {
		return anId;
	}

	@Override
	public void addObserver(Observer o) {
		obs.add(o);
	}

	@Override
	public void removeObserver(Observer o) {
		obs.remove(o);
	}

	@Override
	public void notifyObservers() {
		obs.forEach((Observer ob) -> {
			ob.update();
		});
	}

	@Override
	public void notifyObservers(double metric) {
		throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
																		// Tools | Templates.
	}

	@Override
	public synchronized void update() {

		logs.forEach((stat) -> {
			
		});
	}

	@Override
	public void update(String context, double metric) {
		throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
																		// Tools | Templates.
	}

	@Override
	public void setObservable(Observable ob) {
		ob.addObserver(this);
	}

}
