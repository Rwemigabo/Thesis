/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.analyse;

//import com.mycompany.aatr2.MonitorManager;
import com.mycompany.aatr2.Observable;
import com.mycompany.aatr2.Observer;
import com.mycompany.aatr2.monitor.Cluster;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
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
	// private Observable obvle = null;
	// private ArrayList<StatisticsLog> logs = new ArrayList<>();
	private Cluster clst;
	// private MonitorManager mm;
	//private Timestamp waittime = null;
	private static final long THIRTY_MINUTES = 30 * 60 * 1000;

	StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

	public Analyser(int id, Cluster c) {
		this.anId = id;
		this.obs = new ArrayList<>();
		this.clst = c;
		// this.obvle = new ArrayList<>();

		// this.mm = MonitorManager.getInstance();
	}

	/**
	 * Takes the average memory and cup statistics for a service and uses fuzzy
	 * logic analysis to estimate the containers needed to optimize the service
	 * 
	 * @param avrcpu
	 * @param avrmem
	 */
	public void diagnose(double cpugrad, double memgrad) {
		String fileName = "./SystAnalysis.fcl";
		FIS fis = FIS.load(fileName, true);

		if (fis == null) {
			System.err.println("Can't load file: '" + fileName + "'");
			return;
		}

		// Show
		// JFuzzyChart.get().chart(functionBlock);

		// Set inputs
		fis.setVariable("CPU_load", cpugrad);
		fis.setVariable("food", memgrad);

		// Evaluate
		fis.evaluate();

	}
	
	/**
	 * perform analysis on data for the given time window
	 */
	public void runAnalysis(HashMap<Timestamp, Double> cpu, HashMap<Timestamp, Double> mem) {
		

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
		windowCheck();
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

	/**
	 * call either short term or long term analysis based on the time passed between
	 * the last analysis/ checkpoint and this current statistic's timestamp.
	 */
	public void windowCheck() {
		clst.getLogs().forEach((log) -> {
			if (log != null) {
				Timestamp latest = log.getLatest().getTimestamp();
				Timestamp mincheckpt = new Timestamp(log.getminCheckpoint());
				Timestamp hrcheckpt = new Timestamp(log.getHrCheckpoint());
				
				long m_window = System.currentTimeMillis() - log.getminCheckpoint();
				long h_window = System.currentTimeMillis() - log.getHrCheckpoint();
				
				if (m_window > THIRTY_MINUTES) {
					runAnalysis(log.getCPUStats(latest, mincheckpt), log.getMemStats(latest, mincheckpt));
					log.setminCheckpoint(latest.getTime());
				}else {}
				if (h_window > THIRTY_MINUTES * 2) {
					runAnalysis(log.getCPUStats(latest, hrcheckpt), log.getMemStats(latest, hrcheckpt));
					log.setHrCheckpoint(latest.getTime());
				}else {
					//check critical values (Reactive analysis)
				}
			}else {System.out.println("\n No logs found ");}
		});
	}

}
