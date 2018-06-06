/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.analyse;

import com.mycompany.aatr2.Cluster;
//import com.mycompany.aatr2.MonitorManager;
import com.mycompany.aatr2.Observable;
import com.mycompany.aatr2.Observer;
import com.mycompany.aatr2.RandomString;
import com.mycompany.aatr2.monitor.MonitorManager;

import flanagan.math.Gradient;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import net.sourceforge.jFuzzyLogic.FIS;

/**
 * Analyze class performs an analysis on the data received from the monitor an
 * analyzer is created for each of the services in order to perform an analysis
 * on the data recorded on that service
 * 
 * @author eric
 */
public class Analyser implements Observer, Observable {

	private final String anId;
	private final ArrayList<Observer> obs;
	
	// private Observable obvle = null;
	private ArrayList<Symptom> symplogs = new ArrayList<>();
	private final Cluster cluster;
	private MonitorManager mm;
	private static final long THIRTY_MINUTES = 1 * 60 * 1000;

	StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

	public Analyser(Cluster c) {
		this.anId = new RandomString(8).nextString();
		this.obs = new ArrayList<>();
		this.cluster = c;
		// this.obvle = new ArrayList<>();

		this.mm = MonitorManager.getInstance();
	}

	public void initiate() {
		setObservable(mm.getMonitor(cluster));
	}

	/**
	 * Takes a list of the gradients from the cpu and the gradients from the memory
	 * and uses fuzzy logic analysis to estimate the containers needed to be added
	 * or removed to optimize the service
	 * 
	 * @param avrcpu
	 * @param avrmem
	 */
	public void diagnose(double[] cpugrad, double[] memgrad) {
		String fileName = "/Users/eric/Desktop/Msc_Computer_Science/Year2/Sem2/thesis/AATR2/src/main/java/com/mycompany/aatr2/analyse/SystAnalysis.fcl";
		FIS fis = FIS.load(fileName, true);
		List<Double> eval = new ArrayList<Double>(); //List of the evaluation results based on the data from the last hour or half hour

		if (fis == null) {
			System.err.println("Can't load file: '" + fileName + "'");
			return;
		}

		// Set inputs and evaluate then create new symptom
		for (int i=0, x = 0; i < cpugrad.length && x < memgrad.length; i++, x++) {
			
			//for (int x = 0; x < memgrad.length; x++) {
				fis.setVariable("MEM_load_delta", memgrad[x]);
				fis.setVariable("CPU_load_delta", cpugrad[i]);
				fis.evaluate();
				double result = fis.getVariable("replicas").getValue();
				eval.add(result);
				System.out.println("Number of replicas required = " + result);
			//}
		}

		double avg = calculateAverage(eval); //average of the number of containers to be added or removed
		System.out.println("Average = " + avg);

	}

	private double calculateAverage(List<Double> results) {
		double sum = 0;
		if (!results.isEmpty()) {
			for (double mark : results) {
				sum += mark;
			}
			return sum / results.size();
		}
		return sum;
	}

	/**
	 * TBD: Add sampling for large arrays of values calculate the gradient of data
	 * in a given time window
	 */

	public void runAnalysis(HashMap<Timestamp, Double> cpu, HashMap<Timestamp, Double> mem) {
		// double previous_entry = 0;
		Map<Timestamp, Double> c_map = new TreeMap<>(cpu);// order by the timestamp
		Map<Timestamp, Double> m_map = new TreeMap<>(mem);// order by the timestamp
		double[] cx = new double[c_map.size()];
		double[] cy = new double[c_map.size()];
		double[] mx = new double[m_map.size()];
		double[] my = new double[m_map.size()];

		for (Map.Entry<Timestamp, Double> entry : c_map.entrySet()) {
			for (int i = 0; i < c_map.size(); i++) {
				cx[i] = entry.getKey().getTime();
				cy[i] = entry.getValue();
			}
		}
		for (Map.Entry<Timestamp, Double> entry : m_map.entrySet()) {
			for (int i = 0; i < m_map.size(); i++) {
				mx[i] = entry.getKey().getTime();
				my[i] = entry.getValue();
			}
		}
		Gradient cg = new Gradient(cx, cy);
		Gradient mg = new Gradient(mx, my);

		double[] cpu_gradients = cg.numDeriv_1D_array();// deltas in cpu usage
		System.out.println("CPU gradients: " + cpu_gradients.length);
		double[] mem_gradients = mg.numDeriv_1D_array();// deltas in memory usage
		System.out.println("Memory gradients: " + mem_gradients.length);
		diagnose(cpu_gradients, mem_gradients);

	}

	/**
	 * Get the value of analyzer Id
	 *
	 * @return the value of anId
	 */
	public String getAnId() {
		return anId;
	}

	
	@Override

	public void update() {
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

	/**
	 * call either short term or long term analysis based on the time passed between
	 * the last analysis/ checkpoint and this current statistic's timestamp.
	 */
	public void windowCheck() {
		cluster.getLogs().forEach((log) -> {
			
			if (log != null) {
				System.out.println("Number of stats :"+ log.getMonitorstats().size());
				System.out.println("Checking window");
				if (log.getminCheckpoint() == 0) {// if it's the first value to be recorded
					log.setminCheckpoint(System.currentTimeMillis());
					log.setHrCheckpoint(System.currentTimeMillis());
				} else {
						System.out.println("latest log" + log.getLatest());
						Timestamp latest = log.getLatest().getTimestamp();
						Timestamp mincheckpt = new Timestamp(log.getminCheckpoint());
						Timestamp hrcheckpt = new Timestamp(log.getHrCheckpoint());

						long m_window = System.currentTimeMillis() - log.getminCheckpoint();
						long h_window = System.currentTimeMillis() - log.getHrCheckpoint();
						
						if (m_window > THIRTY_MINUTES) {// if X mins have passed run short term analysis
							System.out.println("\n 30 min window");
							runAnalysis(log.getCPUStats(latest, mincheckpt), log.getMemStats(latest, mincheckpt));
							log.setminCheckpoint(latest.getTime());
						} else {
						}
						
						if (h_window > THIRTY_MINUTES * 2) {// if 2X mins have passed run long term analysis
							System.out.println("\n 1 hr window");
							runAnalysis(log.getCPUStats(latest, hrcheckpt), log.getMemStats(latest, hrcheckpt));
							log.setHrCheckpoint(latest.getTime());
						} else {
							
							//TBD check critical values (Reactive analysis)
							//check SLOs are followed using symptom repository method checkSLO()
							//if not, request adaptation from plan.
							//NB: consider writing the method in Statistics Log class and call it here.
							
						}
						log.removeProcessed();
						// }
//					} else {
//						System.out.println("\n No new values ");
//					}

				}

			} else {
				System.out.println("\n No logs found ");
			}
		});
	}

	public ArrayList<Symptom> getSymplogs() {
		return symplogs;
	}

	public void setSymplogs(ArrayList<Symptom> symplogs) {
		this.symplogs = symplogs;
	}
	
	public Symptom getLatest() {
		return this.symplogs.get(symplogs.size() - 1);
	}

	/**
	 * Using the result, a symptom is created and logged in the symptoms log list and if the result is more or less than 0.5 then the 
	 * Analysis manager is notified and the current symptoms for the whole topology are logged in a new system state.
	 * 
	 * @param result from the diagnosis
	 */
	public void createSymptom(double result) {
		if(result > 0 && result < 0.5) {
			Symptom nSymp = new Symptom(cluster.getServName(), "Add", result);
			symplogs.add(nSymp);
		}
		else if(result < 0  && result > -0.5 ){
			Symptom nSymp = new Symptom(cluster.getServName(), "Remove", result);
			symplogs.add(nSymp);
		}else if (result > 0.5) {
			Symptom nSymp = new Symptom(cluster.getServName(), "Add", result);
			symplogs.add(nSymp);
			notifyObservers();
			
		}else if (result < -0.5) {
			Symptom nSymp = new Symptom(cluster.getServName(), "Remove", result);
			symplogs.add(nSymp);
			notifyObservers();
		}
	}

	public Cluster getCluster() {
		return cluster;
	}

	
	
}
