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
//import com.mycompany.aatr2.monitor.data.Statistic;
//import com.mycompany.aatr2.monitor.data.StatisticsLog;

//import flanagan.math.Gradient;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
//import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
//import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.math3.stat.regression.SimpleRegression;
//import org.apache.commons.math3.stat.regression.SimpleRegression;
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
	private ArrayList<Symptom> symplogs = new ArrayList<>();// to knowledge
	private final Cluster cluster;
	private long MINUTES_WINDOW = 1 * 30 * 1000;// how often to run analysis
	// private final ArrayList<Statistic> spikestats = new ArrayList<>();
	private int analysisCount = 0;
	private final static Logger LOGGER = Logger.getLogger(Analyser.class.getName());

	StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
	 private TreeMap<Long, Double> full_pred_data = new TreeMap<>();// to
	// knowledge
	// private final DynamicLineAndTimeSeriesChart dynamic_pred_chart = new
	// DynamicLineAndTimeSeriesChart("");
	// private Symptom latest;

	public Analyser(Cluster c) {
		this.anId = new RandomString(8).nextString();
		this.obs = new ArrayList<>();
		this.cluster = c;
		// this.obvle = new ArrayList<>();
	}

	/**
	 * testing purposes.
	 */
	public Analyser() {
		this.anId = new RandomString(8).nextString();
		this.obs = new ArrayList<>();
		this.cluster = null;
	}

	public void initiate() {

		setObservable(MonitorManager.getInstance().getMonitor(cluster));
	}

	/**
	 * Takes a list of the values from the cpu and the gradients from the memory and
	 * uses fuzzy logic analysis to estimate the containers needed to be added or
	 * removed to optimize the service
	 * 
	 * @param avrcpu
	 * @param avrmem
	 */
	public void diagnose(double[] cpugrad, double[] memgrad) {
		String fileName = "/Users/eric/Desktop/Msc_Computer_Science/Year2/Sem2/thesis/AATR2/src/main/java/com/mycompany/aatr2/analyse/SystAnalysis.fcl";
		FIS fis = FIS.load(fileName, true);
		List<Double> eval = new ArrayList<Double>(); // List of the evaluation results based on the data from the last
														// hour or half hour

		if (fis == null) {
			System.err.println("Can't load file: '" + fileName + "'");
			return;
		}

		// Set inputs and evaluate then create new symptom
		for (int i = 0, x = 0; i < cpugrad.length && x < memgrad.length; i++, x++) {
			// System.out.println(
			// "\n Memory value to be processed = " + memgrad[x] + " CPU value to be
			// processed = " + cpugrad[x]);
			fis.setVariable("MEM_load_delta", memgrad[x]);
			fis.setVariable("CPU_load_delta", cpugrad[i]);
			fis.evaluate();
			double result = fis.getVariable("replicas").getValue();
			eval.add(result);
			// System.out.println("Number of replicas required = " + result);
		}

		// double avg = calculateAverage(eval); // average of the number of containers
		// to be added or removed
		// createSymptom(avg);
		// System.out.println("Average Change = " + avg);

	}

	/**
	 * Takes a list of the prediction of the cpu metric and the prediction of the
	 * memory metric within the next set minutes and uses fuzzy logic analysis to
	 * estimate the containers needed to be added or removed to optimize the service
	 * 
	 * @param avrcpu
	 * @param avrmem
	 */
	public void diagnose(double cpupred, double mempred) {
		String fileName = "/Users/eric/Desktop/Msc_Computer_Science/Year2/Sem2/thesis/AATR2/src/main/java/com/mycompany/aatr2/analyse/SystAnalysis.fcl";
		FIS fis = FIS.load(fileName, true);
		if (fis == null) {
			System.err.println("Can't load file: '" + fileName + "'");
			return;
		}

		// Set inputs and evaluate then create new symptom
		System.out.println("\n Memory value to be processed = " + mempred + " CPU value to be processed = " + cpupred);
		fis.setVariable("MEM_load_delta", mempred);
		fis.setVariable("CPU_load_delta", cpupred);
		fis.evaluate();
		double result = fis.getVariable("replicas").getValue();
		//System.out.println("Number of replicas required = " + result);

		createSymptom(Math.round(result));
		LOGGER.log(Level.INFO, "Average = " + Math.round(result));

	}

	public void addSymptom(Symptom s) {
		this.symplogs.add(s);
	}

	// public void setLatest(Symptom s) {
	// this.latest = s;
	//
	// }

	// /**
	// * Use classic integral control to perform a diagnosis and request a topology
	// type
	// *
	// */
	// public void newDiagnosis(double[] cpugrad, double[] cy) {
	//
	// }
	//
	// public double integCalculate() {
	// double cont_count
	// }

	public double calculateAverage(List<Double> results) {
		double sum = 0;
		if (!results.isEmpty()) {
			for (double mark : results) {
				sum += mark;
			}
			return sum / results.size();
		}
		return sum;
	}

	 private void plotData(TreeMap<Long, Double> map, String title) {
	 Plot p = new Plot(this.cluster.getServName(), title,
	 Integer.toString(analysisCount));
	 long[] cx = new long[map.size()];
	 double[] cy = new double[map.size()];
	
	 int countCPU = 0;
	 if (countCPU < map.size()) {
	 for (Map.Entry<Long, Double> entry : map.entrySet()) {
	 cx[countCPU] = entry.getKey();
	 cy[countCPU] = entry.getValue();
	 countCPU++;
	 }
	 if (countCPU > 0) {
	 p.plot(cx, cy);
	 } else {
	 System.out.println("Nothing in the list");
	 }
	
	 // cpudataPlot.pack();
	 // cpudataPlot.setVisible(true);
	
	 }
	
	 // System.out.println(title+": Timestamps " + Arrays.toString(cx));
	 // System.out.println(title+" values: " + Arrays.toString(cy));
	
	 }

	/**
	 * Analyzes data in the given hashmap by calculating the gradients at each point
	 * in the dataset and finally calls for a diagnosis on the resulting gradients.
	 * 
	 * @param cpu
	 *            CPU statistics hashmap
	 * @param mem
	 *            Memory statistics Hashmap
	 */
	public void runWindowAnalysis(HashMap<Timestamp, Double> cpu, HashMap<Timestamp, Double> mem) {
		// double previous_entry = 0;
		TreeMap<Timestamp, Double> c_map = new TreeMap<>(cpu);// order by the timestamp
		TreeMap<Timestamp, Double> m_map = new TreeMap<>(mem);// order by the timestamp
		// Plot cpugradPlot = new Plot(this.cluster.getServName(), "CPU Gradient",
		// Integer.toString(analysisCount));
		// Plot memgradPlot = new Plot(this.cluster.getServName(), "Memory Gradient",
		// Integer.toString(analysisCount));
		Plot cpudataPlot = new Plot(this.cluster.getServName(), "CPU Data points", Integer.toString(analysisCount));
		Plot memdataPlot = new Plot(this.cluster.getServName(), "Memory Data points", Integer.toString(analysisCount));
		long[] cx = new long[c_map.size()];
		double[] cy = new double[c_map.size()];
		long[] mx = new long[m_map.size()];
		double[] my = new double[m_map.size()];
		int countCPU = 0;
		int countMEM = 0;
		if (countCPU < c_map.size()) {
			for (Map.Entry<Timestamp, Double> entry : c_map.entrySet()) {
				cx[countCPU] = entry.getKey().getTime();
				cy[countCPU] = entry.getValue();
				countCPU++;
			}
			cpudataPlot.plot(cx, cy);
			// cpudataPlot.pack();
			// cpudataPlot.setVisible(true);

		}

		if (countMEM < m_map.size()) {
			for (Map.Entry<Timestamp, Double> entry : m_map.entrySet()) {
				mx[countMEM] = entry.getKey().getTime();
				my[countMEM] = entry.getValue();
				countMEM++;
			}
			memdataPlot.plot(mx, my);
			// memdataPlot.pack();
			// memdataPlot.setVisible(true);

		}

		// System.out.println("CPU timestamps: " + Arrays.toString(cx));
		// System.out.println("CPU values: " + Arrays.toString(cy));
		// System.out.println("Memory values: " + Arrays.toString(my));
		// System.out.println("Memory timestamps: " + Arrays.toString(mx));
		// Gradient cgg = new Gradient(cx, cy);
		// Gradient mgg = new Gradient(mx, my);
		// GradientDescent cg = new GradientDescent(cx, cy);
		// GradientDescent mg = new GradientDescent(mx, my);
		// cg.execute();
		// mg.execute();
		// cg.printConvergence();
		// mg.printConvergence();
		//

		double[] min_mem_gradients = runMinuteMemoryAnalysis(mx, my, "Window Minute Memory Gradient");
		double[] min_cpu_gradients = runMinuteCPUAnalysis(cx, cy, "Window Minute CPU Gradient");
		double[] mem_gradients = gradient(mx, my);// deltas in memory usage
		double[] cpu_gradients = gradient(cx, cy);// deltas in cpu usage
		// memgradPlot.plot(mx, mem_gradients);
		// memgradPlot.pack();
		// memgradPlot.setVisible(true);

		// cpugradPlot.plot(cx, cpu_gradients);
		// cpugradPlot.pack();
		// cpugradPlot.setVisible(true);

		// System.out.println("cpu Grads: " + Arrays.toString(cpu_gradients));
		// System.out.println("Memory gradients: " + Arrays.toString(mem_gradients));
		analysisCount++;
		if (cpu_gradients != null && mem_gradients != null) {
			diagnose(cpu_gradients, mem_gradients);
		} else {
			System.out.println("NULL gradients");
		}
		if (min_cpu_gradients != null && min_mem_gradients != null) {
			diagnose(min_cpu_gradients, min_mem_gradients);
		} else {
			System.out.println("NULL minute gradients");
		}

	}

	/**
	 * runs an gradient calculations on each of the minutes in the set time frame of
	 * the memory data depending on the set window calls to plot the gradients
	 * 
	 * @param mx
	 * @param my
	 * @param graphtitle
	 * @return list of gradients
	 */
	private double[] runMinuteMemoryAnalysis(long[] mx, double[] my, String graphtitle) {
		// Plot minmemgradPlot = new Plot(this.cluster.getServName(), graphtitle,
		// Integer.toString(analysisCount));
		if (MINUTES_WINDOW >= 10 * 60 * 1000 && MINUTES_WINDOW <= 20 * 60 * 1000) {
			// include gradients for every 2 minutes
			double[] min_mem_gradients = gradient(mx, my, 2);
			// minmemgradPlot.plot(mx, min_mem_gradients);
			// memgradPlot.pack();
			// memgradPlot.setVisible(true);
			return min_mem_gradients;

		} else if (MINUTES_WINDOW >= 20 * 60 * 1000 && MINUTES_WINDOW <= 30 * 60 * 1000) {
			// include gradients for every 4 minutes
			double[] min_mem_gradients = gradient(mx, my, 4);

			// minmemgradPlot.plot(mx, min_mem_gradients);
			// memgradPlot.pack();
			// memgradPlot.setVisible(true);
			return min_mem_gradients;

		} else if (MINUTES_WINDOW >= 5 * 60 * 1000 && MINUTES_WINDOW < 10 * 60 * 1000) {
			// include gradients for every 1 minutes
			double[] min_mem_gradients = gradient(mx, my, 1);

			// minmemgradPlot.plot(mx, min_mem_gradients);
			// memgradPlot.pack();
			// memgradPlot.setVisible(true);
			return min_mem_gradients;
		} else {
			return null;
		}

	}

	/**
	 * runs an gradient calculations on each of the minutes in the set time frame of
	 * the cpu data depending on the set window calls to plot the gradients
	 * 
	 * @param cx
	 * @param cy
	 * @param graphtitle
	 * @return list of cpu gradients
	 */
	private double[] runMinuteCPUAnalysis(long[] cx, double[] cy, String graphtitle) {
		// Plot mincpugradPlot = new Plot(this.cluster.getServName(), graphtitle,
		// Integer.toString(analysisCount));
		if (MINUTES_WINDOW >= 10 * 60 * 1000 && MINUTES_WINDOW <= 20 * 60 * 1000) {
			// include gradients for every 2 minutes
			double[] min_cpu_gradients = gradient(cx, cy, 2);

			// mincpugradPlot.plot(cx, min_cpu_gradients);
			// cpugradPlot.pack();
			// cpugradPlot.setVisible(true);
			return min_cpu_gradients;

		} else if ((MINUTES_WINDOW >= 20 * 60 * 1000 && MINUTES_WINDOW <= 30 * 60 * 1000)) {
			// include gradients for every 4 minutes
			double[] min_cpu_gradients = gradient(cx, cy, 4);

			// mincpugradPlot.plot(cx, min_cpu_gradients);
			// cpugradPlot.pack();
			// cpugradPlot.setVisible(true);
			return min_cpu_gradients;

		} else if (MINUTES_WINDOW >= 5 * 60 * 1000 && MINUTES_WINDOW < 10 * 60 * 1000) {
			// include gradients for every 1 minutes
			double[] min_cpu_gradients = gradient(cx, cy, 1);

			// mincpugradPlot.plot(cx, min_cpu_gradients);
			// cpugradPlot.pack();
			// cpugradPlot.setVisible(true);
			return min_cpu_gradients;
		} else {
			return null;
		}

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
	 * the last analysis/ checkpoint and this current statistic's time stamp.
	 * 
	 */
	public void windowCheck() {
		cluster.getLogs().forEach((log) -> {
			cluster.writeToFile(log);
			if (log != null) {

				// System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n
				// Number of stats :"
				// + log.getMonitorstats().size() + " for container " + log.container());
				if (log.getminCheckpoint() == 0) {// if it's the first value to be recorded
					log.setminCheckpoint(System.currentTimeMillis());
					log.setHrCheckpoint(System.currentTimeMillis());
				} else {
					// System.out.println("latest log" + log.getLatest().getTimestamp());
					Timestamp latest = log.getLatest().getTimestamp();
					// Timestamp mincheckpt = new Timestamp(log.getminCheckpoint());
					// System.out.println("Minutes Timestamp" + mincheckpt);
					// Timestamp hrcheckpt = new Timestamp(log.getHrCheckpoint());

					long m_window = System.currentTimeMillis() - log.getminCheckpoint();
					long h_window = System.currentTimeMillis() - log.getHrCheckpoint();

					if (m_window >= MINUTES_WINDOW) {// if X mins have passed run short term analysis
						System.out.println("Checking window1");
						// runWindowAnalysis(log.getCPUStats(latest, mincheckpt),
						// log.getMemStats(latest, mincheckpt));
						 runFullDataAnalysis(log.getCPUStats(), log.getMemStats());
						//Analysis analyse = new Analysis(log.getCPUStats(), log.getMemStats(), this);
						//analyse.start();
						log.setminCheckpoint(latest.getTime());
					} else if (h_window >= MINUTES_WINDOW * 2) {// if 2X mins have passed run long term analysis
						System.out.println("Checking window2");
						// runWindowAnalysis(log.getCPUStats(latest, hrcheckpt), log.getMemStats(latest,
						// hrcheckpt));
						 runFullDataAnalysis(log.getCPUStats(), log.getMemStats());
						//Analysis analyse = new Analysis(log.getCPUStats(), log.getMemStats(), this);
						//analyse.start();
						log.setHrCheckpoint(latest.getTime());
					} else {

						// TBD check critical values (Reactive analysis)
						// check SLOs are followed using symptom repository method checkSLO()
						// if not, request adaptation from plan.
						// NB: consider writing the method in Statistics Log class and call it here.

					}
					log.removeProcessed();
					// }
					// } else {
					// System.out.println("\n No new values ");
					// }

				}

			} else {
				System.out.println("\n No logs found ");
			}
		});
	}
	
	/**
	 * Using the result from the diagnosis, a symptom is created and logged in the
	 * symptoms log list and if the result is more or less than 0.5 then the
	 * Analysis manager is notified and the current symptoms for the whole topology
	 * are logged in a new system state.
	 * 
	 * @param result
	 *            from the diagnosis
	 */
	public void createSymptom(double result) {
		System.out.println("Creating symptom?????? " + result);
		if (result >= 0 && result < 1) {
			Symptom nSymp = new Symptom(cluster.getServName(), "Add", result);
			addSymptom(nSymp);
			LOGGER.log(Level.INFO, "1 Notifying Analysis Manager!!!!!!!!!!!!!!!!!!!!!");
			notification();
			//AnalyseManager.getInstance().notified(this.cluster.getServName());
			
		} else if (result <= 0 && result > -1) {
			Symptom nSymp = new Symptom(cluster.getServName(), "Remove", result);		
			addSymptom(nSymp);
			LOGGER.log(Level.INFO, "2 Notifying Analysis Manager!!!!!!!!!!!!!!!!!!!!!");
			notification();
			
			

		} else if (result >= 1) {
			Symptom nSymp = new Symptom(cluster.getServName(), "Add", result);
			addSymptom(nSymp);
			LOGGER.log(Level.INFO, "3 Notifying Analysis Manager!!!!!!!!!!!!!!!!!!!!!");
			notification();
			//AnalyseManager.getInstance().notified(this.cluster.getServName());

		} else if (result <= -1) {
			Symptom nSymp = new Symptom(cluster.getServName(), "Remove", result);

			//ana.setLatest(nSymp);

			addSymptom(nSymp);
			LOGGER.log(Level.INFO, "4 Notifying Analysis Manager!!!!!!!!!!!!!!!!!!!!!");
			notification();
			//AnalyseManager.getInstance().notified(this.cluster.getServName());
			
		} 
	}

	/**
	 * Runs an analysis on the whole dataset since the first recorded data point
	 *
	 * @param cpuStats
	 * @param memStats
	 */
	 private void runFullDataAnalysis(HashMap<Timestamp, Double> cpuStats,HashMap<Timestamp, Double> memStats) {
	 TreeMap<Timestamp, Double> c_map = new TreeMap<>(cpuStats);// order by thetimestamp
	 TreeMap<Timestamp, Double> m_map = new TreeMap<>(memStats);// order by thetimestamp
	 Plot cpudataPlot = new Plot(this.cluster.getServName(), "Full CPU Datapoints",
	 Integer.toString(analysisCount));
	 Plot memdataPlot = new Plot(this.cluster.getServName(), "Full Memory Datapoints",
	 Integer.toString(analysisCount));
	 long[] cx = new long[c_map.size()];
	 double[] cy = new double[c_map.size()];
	 long[] mx = new long[m_map.size()];
	 double[] my = new double[m_map.size()];
	 int countCPU = 0;
	 int countMEM = 0;
	 if (countCPU < c_map.size()) {
	 for (Map.Entry<Timestamp, Double> entry : c_map.entrySet()) {
	 cx[countCPU] = entry.getKey().getTime();
	 cy[countCPU] = entry.getValue();
	 countCPU++;
	 }
	 cpudataPlot.plot(cx, cy);
	 }
	
	 if (countMEM < m_map.size()) {
	 for (Map.Entry<Timestamp, Double> entry : m_map.entrySet()) {
	 mx[countMEM] = entry.getKey().getTime();
	 my[countMEM] = entry.getValue();
	 countMEM++;
	 }
	 memdataPlot.plot(mx, my);
	 }
	
	 // System.out.println("Full CPU timestamps: " + Arrays.toString(cx));
	 // System.out.println("Full CPU values: " + Arrays.toString(cy));
	 // System.out.println("Full Memory values: " + Arrays.toString(my));
	 // System.out.println("Full Memory timestamps: " + Arrays.toString(mx));
	
	 // double[] min_mem_gradients = runMinuteMemoryAnalysis(mx, my, "Full Memory
	 // Gradient");
	 // double[] min_cpu_gradients = runMinuteCPUAnalysis(cx, cy, "Full CPU
	 // Gradient");
	
	 double CPU_prediction = makePrediction(c_map, "CPU");
	 double Mem_prediction = makePrediction(m_map, "Memory");
	
	 diagnose(CPU_prediction, Mem_prediction);
	
//	 System.out.println("Cpu prediction: " + CPU_prediction + " for " +
//	 this.cluster.getServName());
//	 System.out.println("Memory prediction: " + Mem_prediction + " for " +
//	 this.cluster.getServName());
	
	 }

	public ArrayList<Symptom> getSymplogs() {
		return symplogs;
	}

	public void setSymplogs(ArrayList<Symptom> symplogs) {
		this.symplogs = symplogs;
	}

	/**
	 * 
	 * @return latest system state
	 */
	public Symptom getLatest() {
		System.out.println("Number of Symptom logs = " + this.symplogs.size() + this.cluster.getServName());
		return this.symplogs.get(symplogs.size() - 1);
	}

	public Cluster getCluster() {
		return cluster;
	}

	/**
	 * calculate gradients and return the values at each given point in the list
	 * 
	 * @param x
	 *            time values
	 * @param y
	 *            metric values
	 * @return list of gradient results otherwise null
	 */
	public double[] gradient(long[] x, double[] y) {

		if (x.length != y.length) {
			System.out.println("ARRAYS X AND Y ARE OF DIFFERENT SIZE. CANNOT CALCULATE GRADIENT");
		} else {
			double[] grad = new double[x.length];
			double secs = 1000;
			for (int i = 0, j = 0; i <= x.length - 1 && j <= y.length - 1; i++, j++) {
				if (i != x.length - 1 || j != y.length - 1) {
					double thisgrad = (y[j + 1] - y[j]) / ((x[i + 1] - x[i]) / secs);
					grad[i] = thisgrad;
				} else {
					// System.out.println("\n LAST VALUES IN ARRAYS");
					double windowgrad = (y[j] - y[0]) / (x[i] - x[0]);
					grad[i] = windowgrad;
					// System.out.println("\n LAST VALUES IN ARRAYS, SETTING TIME WINDOW GRAD = " +
					// windowgrad);
					break;

				}
			}
			return grad;
		}
		return null;
	}

	/**
	 * calculate gradients and returns the values of the gradients between the
	 * required minutes in the dataset
	 * 
	 * @param x
	 *            time values
	 * @param y
	 *            metric values
	 * @param min
	 *            the requred minute diffrence
	 * @return list of gradient results otherwise null
	 */
	public double[] gradient(long[] x, double[] y, int min) {

		if (x.length != y.length) {
			System.out.println("ARRAYS X AND Y ARE OF DIFFERENT SIZE. CANNOT CALCULATE GRADIENT");
		} else {
			double[] grad = new double[x.length];
			for (int i = 0, j = 0; i <= x.length - 1 && j <= y.length - 1; i++, j++) {
				if (i != x.length - 1 || j != y.length - 1) {
					if (checkNext(i, x, min) > 0) {
						long nextX = x[checkNext(i, x, min)];
						double nextY = y[checkNext(i, x, min)];
						double thisgrad = (nextY - y[j]) / ((nextX - x[i]) / 1000);
						grad[i] = thisgrad;
					} else {
						// System.out.println("\n NO MORE VALUES FURTHER THAN " + min + " Minutes.
						// Breaking loop");
						double thisgrad = (y[y.length - 1] - y[j]) / ((x[i + 1] - x[i]) / 1000);
						grad[i] = thisgrad;
						break;
					}
				} else {
					// System.out.println("\n LAST VALUES IN ARRAYS");
					double windowgrad = (y[j] - y[y.length - 1]) / ((x[i] - x[x.length - 1]) / 1000);
					grad[i] = windowgrad;
					// System.out.println("\n LAST VALUES IN ARRAYS, SETTING TIME WINDOW GRAD = " +
					// windowgrad);
					break;

				}
			}
			return grad;
		}
		return null;
	}

	/**
	 * 
	 * @param x
	 *            the position of the value being checked
	 * @param xlist
	 *            list of timestamp values
	 * @param mins
	 *            required minute difference
	 * @return next position of the next minute required
	 */
	public int checkNext(int x, long[] xlist, int mins) {
		for (int i = x + 1; i <= xlist.length - 1; i++) {
			if (checkMinute(xlist[x], xlist[i], mins)) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * Check the difference between the times and tell if they are the required
	 * length apart
	 * 
	 * @param current_min
	 *            the current timestamp bbeing analysed
	 * @param next_min
	 *            the next required timestamp
	 * @param diff
	 *            how many minnutes apart are required
	 * @return true or false whether they are the required difference apart.
	 */
	public boolean checkMinute(long current_min, long next_min, int diff) {
		long difference = next_min - current_min;
		long reqired_diff = diff * 60 * 1000;
		if (difference >= reqired_diff) {
			return true;
		} else {
			return false;
		}
	}

	// /**
	// * call either short term or long term analysis based on the time passed
	// between
	// * the last analysis/ checkpoint and this current statistic's time stamp.
	// *
	// */
	// public void filewindowCheck(StatisticsLog sl) {
	//
	// if (sl != null) {
	//
	// System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n
	// Number of stats :"
	// + sl.getMonitorstats().size() + " for container " + sl.container());
	// runFullDataAnalysis(sl.getCPUStats(), sl.getMemStats());
	//
	// } else {
	// System.out.println("\n No logs found ");
	// }
	// // });
	// }

	 /**
	 * Performs a prediction for the next time Window, plots the predictions for
	 the
	 * next window and plots the predictions for all the full data including
	 * previous predictions.
	 *
	 * @param treemap
	 * of data to perform prediction on
	 * @return
	 */
	 private double makePrediction(TreeMap<Timestamp, Double> trendList, String
	 metric_nm) {
	 if (trendList.size() == 0) {
	 System.out.println("Nothing in list for prediction to be made");
	 return 0;
	 }
	 SimpleRegression regression = new SimpleRegression();
	 double prediction = 0;
	 List<Double> y_predicts = new ArrayList<>();
	 TreeMap<Long, Double> plot_data = new TreeMap<>();
	 for (Map.Entry<Timestamp, Double> entry : trendList.entrySet()) {
	 regression.addData(entry.getKey().getTime(), entry.getValue());
	 }
	
	 long frame = 10 * 1000;// 10 second frames
	 long lastvalue = trendList.lastEntry().getKey().getTime() + MINUTES_WINDOW;
	 // last timestamp in the prediction
	 // values
	 for (long afterlast = trendList.lastEntry().getKey().getTime()
	 + 100; afterlast <= lastvalue; afterlast += frame) {
	 double metric = regression.predict(afterlast);
	 y_predicts.add(metric);
	 plot_data.put(afterlast, metric);
	 full_pred_data.put(afterlast, metric);
	
	 }
	
	 prediction = Collections.max(y_predicts);
//	  //LOGGER.log(Level.INFO, "Max prediction for next time window = "+
//	 prediction+
//	  " for " +metric_nm);
	 if (plot_data.isEmpty()) {
	 System.out.println("Prediction plot data is empty");
	 } else {
	 plotData(plot_data, "Window Prediction for " + metric_nm);
	 //plotData(full_pred_data, "Full Prediction Data for " + metric_nm);
	 }
	
	 return prediction;
	 }

	public long getMinuteWindow() {
		return this.MINUTES_WINDOW;
	}

	public void notification() {
		
		boolean didI = AnalyseManager.getInstance().alreadyNotified(cluster.getServName());
		System.out.println("checking if i notified.........................." + didI);
		if(!didI) {
			AnalyseManager.getInstance().notified(this.cluster.getServName());
			notifyObservers();
			
		}
	}
}
