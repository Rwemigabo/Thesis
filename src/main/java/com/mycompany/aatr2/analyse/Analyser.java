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

import flanagan.math.Gradient;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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
	private Cluster cluster;
	// private MonitorManager mm;
	//private Timestamp waittime = null;
	private static final long THIRTY_MINUTES = 30 * 60 * 1000;

	StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

	public Analyser(int id, Cluster c) {
		this.anId = id;
		this.obs = new ArrayList<>();
		this.cluster = c;
		// this.obvle = new ArrayList<>();

		// this.mm = MonitorManager.getInstance();
	}

	/**
	 * Takes a list of the gradients from the cpu and the gradients from the memory and uses fuzzy
	 * logic analysis to estimate the containers needed to optimize the service
	 * 
	 * @param avrcpu
	 * @param avrmem
	 */
	public void diagnose(double[] cpugrad, double[] memgrad) {
		String fileName = "./SystAnalysis.fcl";
		FIS fis = FIS.load(fileName, true);

		if (fis == null) {
			System.err.println("Can't load file: '" + fileName + "'");
			return;
		}

		// Show
		// JFuzzyChart.get().chart(functionBlock);

		// Set inputs
		for(int i = 0; i < cpugrad.length; i++) {
			fis.setVariable("CPU_load", cpugrad[i]);
		}
		for(int i = 0; i < memgrad.length; i++) {
			fis.setVariable("food", memgrad[i]);
		}
		// Evaluate
		fis.evaluate();

	}
	
	/**
	 * calculate the gradient of data in a given time window
	 */
	@SuppressWarnings("null")
	public void runAnalysis(HashMap<Timestamp, Double> cpu, HashMap<Timestamp, Double> mem) {
		//double previous_entry = 0;
		Map<Timestamp, Double> c_map = new TreeMap<>(cpu);//order by the timestamp
		Map<Timestamp, Double> m_map = new TreeMap<>(mem);// order by the timestamp
		double[] cx = null;
		double[] cy = null;
		double[] mx = null;
		double[] my = null;
		
		for(Map.Entry<Timestamp, Double> entry: c_map.entrySet()) {
			for(int i = 0; i < c_map.size(); i++) {
				cx[i] = entry.getKey().getTime();
				cy[i] = entry.getValue();
			}		
		}
		for(Map.Entry<Timestamp, Double> entry: m_map.entrySet()) {
			for(int i = 0; i < m_map.size(); i++) {
				mx[i] = entry.getKey().getTime();
				my[i] = entry.getValue();
			}
		}
		Gradient cg = new Gradient(cx,cy);
		Gradient mg = new Gradient(mx, my);
		
		double[] cpu_gradients = cg.numDeriv_1D_array();//changes in cpu usage
		double[] mem_gradients = mg.numDeriv_1D_array();//changes in memory usage
		diagnose(cpu_gradients, mem_gradients);

	}

	/**
	 * Get the value of analyser Id
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
		cluster.getLogs().forEach((log) -> {
			if (log != null) {
				Timestamp latest = log.getLatest().getTimestamp();
				Timestamp mincheckpt = new Timestamp(log.getminCheckpoint());
				Timestamp hrcheckpt = new Timestamp(log.getHrCheckpoint());
				
				long m_window = System.currentTimeMillis() - log.getminCheckpoint();
				long h_window = System.currentTimeMillis() - log.getHrCheckpoint();
				
				if (m_window > THIRTY_MINUTES) {//if X mins have passed run short term analysis
					runAnalysis(log.getCPUStats(latest, mincheckpt), log.getMemStats(latest, mincheckpt));
					log.setminCheckpoint(latest.getTime());
				}else {}
				if (h_window > THIRTY_MINUTES * 2) {//if 2X mins have passed run long term analysis
					runAnalysis(log.getCPUStats(latest, hrcheckpt), log.getMemStats(latest, hrcheckpt));
					log.setHrCheckpoint(latest.getTime());
				}else {
					//check critical values (Reactive analysis)
				}
			}else {System.out.println("\n No logs found ");}
		});
	}

}
