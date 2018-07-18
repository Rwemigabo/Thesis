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

//import flanagan.math.Gradient;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
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
	private long MINUTES_WINDOW = 20 * 60 * 1000;
	// private final ArrayList<Statistic> spikestats = new ArrayList<>();
	private int analysisCount = 0;

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
		List<Double> eval = new ArrayList<Double>(); // List of the evaluation results based on the data from the last
														// hour or half hour

		if (fis == null) {
			System.err.println("Can't load file: '" + fileName + "'");
			return;
		}

		// Set inputs and evaluate then create new symptom
		for (int i = 0, x = 0; i < cpugrad.length && x < memgrad.length; i++, x++) {
			System.out.println(
					"\n Memory value to be processed = " + memgrad[x] + " CPU value to be processed = " + cpugrad[x]);
			fis.setVariable("MEM_load_delta", memgrad[x]);
			fis.setVariable("CPU_load_delta", cpugrad[i]);
			fis.evaluate();
			double result = fis.getVariable("replicas").getValue();
			eval.add(result);
			System.out.println("Number of replicas required = " + result);
		}

		double avg = calculateAverage(eval); // average of the number of containers to be added or removed
		createSymptom(avg);
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
 		Map<Timestamp, Double> c_map = new TreeMap<>(cpu);// order by the timestamp
		Map<Timestamp, Double> m_map = new TreeMap<>(mem);// order by the timestamp
		//Plot cpugradPlot = new Plot(this.cluster.getServName(), "CPU Gradient", Integer.toString(analysisCount));
		//Plot memgradPlot = new Plot(this.cluster.getServName(), "Memory Gradient", Integer.toString(analysisCount));
		Plot cpudataPlot = new Plot(this.cluster.getServName(), "CPU Data points", Integer.toString(analysisCount));
		Plot memdataPlot = new Plot(this.cluster.getServName(), "Memory Data points", Integer.toString(analysisCount));
		double[] cx = new double[c_map.size()];
		double[] cy = new double[c_map.size()];
		double[] mx = new double[m_map.size()];
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

		System.out.println("CPU timestamps: " + Arrays.toString(cx));
		System.out.println("CPU values: " + Arrays.toString(cy));
		System.out.println("Memory values: " + Arrays.toString(my));
		System.out.println("Memory timestamps: " + Arrays.toString(mx));
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
		double[] min_cpu_gradients =runMinuteCPUAnalysis(cx, cy, "Window Minute CPU Gradient");
		double[] mem_gradients = gradient(mx, my);// deltas in memory usage
		double[] cpu_gradients = gradient(cx, cy);// deltas in cpu usage
		//memgradPlot.plot(mx, mem_gradients);
		// memgradPlot.pack();
		// memgradPlot.setVisible(true);

		//cpugradPlot.plot(cx, cpu_gradients);
		// cpugradPlot.pack();
		// cpugradPlot.setVisible(true);

		System.out.println("cpu Grads: " + Arrays.toString(cpu_gradients));
		System.out.println("Memory gradients: " + Arrays.toString(mem_gradients));
		analysisCount++;
		if(cpu_gradients != null && cpu_gradients != null) {
			diagnose(cpu_gradients, mem_gradients);
		}else {System.out.println("NULL gradients");}
		if(min_cpu_gradients != null && min_cpu_gradients != null) {
			diagnose(min_cpu_gradients ,min_mem_gradients);
		}else {System.out.println("NULL minute gradients");}
		

	}

	/**
	 * runs an gradient calculations on each of the minutes in the set time frame of the memory data depending on the set window
	 * calls to plot the gradients
	 * @param mx
	 * @param my
	 * @param graphtitle
	 * @return list of gradients
	 */
	private double[] runMinuteMemoryAnalysis(double[] mx, double[] my, String graphtitle) {
		Plot minmemgradPlot = new Plot(this.cluster.getServName(), graphtitle, Integer.toString(analysisCount));
		if(MINUTES_WINDOW >= 10* 60 * 1000 && MINUTES_WINDOW <= 20* 60 * 1000) {
			//include gradients for every 2 minutes
			double[] min_mem_gradients = gradient(mx, my, 2);
			minmemgradPlot.plot(mx, min_mem_gradients);
			// memgradPlot.pack();
			// memgradPlot.setVisible(true);
			return min_mem_gradients;

		}else if(MINUTES_WINDOW >= 20 * 60 * 1000 && MINUTES_WINDOW <= 30* 60 * 1000) {
			//include gradients for every 4 minutes
			double[] min_mem_gradients = gradient(mx, my, 4);
			
			minmemgradPlot.plot(mx, min_mem_gradients);
			// memgradPlot.pack();
			// memgradPlot.setVisible(true);
			return min_mem_gradients;

		}else if(MINUTES_WINDOW >= 5 * 60 * 1000&& MINUTES_WINDOW < 10* 60 * 1000) {
			//include gradients for every 1 minutes
			double[] min_mem_gradients = gradient(mx, my, 1);
			
			minmemgradPlot.plot(mx, min_mem_gradients);
			// memgradPlot.pack();
			// memgradPlot.setVisible(true);
			return min_mem_gradients;
		}else {return null;}
		
	}

	/**
	 * runs an gradient calculations on each of the minutes in the set time frame of the cpu data depending on the set window
	 * calls to plot the gradients
	 * @param cx
	 * @param cy
	 * @param graphtitle
	 * @return list of cpu gradients
	 */
	private double[] runMinuteCPUAnalysis(double[] cx, double[] cy, String graphtitle) {
		Plot mincpugradPlot = new Plot(this.cluster.getServName(), graphtitle, Integer.toString(analysisCount));
		if(MINUTES_WINDOW >= 10* 60 * 1000 && MINUTES_WINDOW <= 20* 60 * 1000) {
			//include gradients for every 2 minutes
			double[] min_cpu_gradients = gradient(cx, cy, 2);
			
			mincpugradPlot.plot(cx, min_cpu_gradients);
			// cpugradPlot.pack();
			// cpugradPlot.setVisible(true);
			return min_cpu_gradients;

		}else if((MINUTES_WINDOW >= 20 * 60 * 1000 && MINUTES_WINDOW <= 30* 60 * 1000)) {
			//include gradients for every 4 minutes
			double[] min_cpu_gradients = gradient(cx, cy, 4);
			
			mincpugradPlot.plot(cx, min_cpu_gradients);
			// cpugradPlot.pack();
			// cpugradPlot.setVisible(true);
			return min_cpu_gradients;

		}else if(MINUTES_WINDOW >= 5 * 60 * 1000 && MINUTES_WINDOW < 10* 60 * 1000) {
			//include gradients for every 1 minutes
			double[] min_cpu_gradients = gradient(cx, cy, 1);
			
			mincpugradPlot.plot(cx, min_cpu_gradients);
			// cpugradPlot.pack();
			// cpugradPlot.setVisible(true);
			return min_cpu_gradients;
		}else {
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

			if (log != null) {

				System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n Number of stats :"
						+ log.getMonitorstats().size() + " for container " + log.container());
				if (log.getminCheckpoint() == 0) {// if it's the first value to be recorded
					log.setminCheckpoint(System.currentTimeMillis());
					log.setHrCheckpoint(System.currentTimeMillis());
				} else {
					System.out.println("latest log" + log.getLatest().getTimestamp());
					Timestamp latest = log.getLatest().getTimestamp();
					Timestamp mincheckpt = new Timestamp(log.getminCheckpoint());
					//System.out.println("Minutes Timestamp" + mincheckpt);
					Timestamp hrcheckpt = new Timestamp(log.getHrCheckpoint());

					long m_window = System.currentTimeMillis() - log.getminCheckpoint();
					long h_window = System.currentTimeMillis() - log.getHrCheckpoint();

					if (m_window > MINUTES_WINDOW) {// if X mins have passed run short term analysis
						System.out.println("Checking 1 minute window");
						runWindowAnalysis(log.getCPUStats(latest, mincheckpt), log.getMemStats(latest, mincheckpt));
						runFullDataAnalysis(log.getCPUStats(), log.getMemStats());
						log.setminCheckpoint(latest.getTime());
					} else if (h_window > MINUTES_WINDOW * 2) {// if 2X mins have passed run long term analysis
						System.out.println("Checking 1 minute window");
						runWindowAnalysis(log.getCPUStats(latest, hrcheckpt), log.getMemStats(latest, hrcheckpt));
						runFullDataAnalysis(log.getCPUStats(), log.getMemStats());
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
	 * Runs a fill analysis on the whole dataset since the first recorded data point
	 * @param cpuStats
	 * @param memStats
	 */
	private void runFullDataAnalysis(HashMap<Timestamp, Double> cpuStats, HashMap<Timestamp, Double> memStats) {
		Map<Timestamp, Double> c_map = new TreeMap<>(cpuStats);// order by the timestamp
		Map<Timestamp, Double> m_map = new TreeMap<>(memStats);// order by the timestamp
		Plot cpudataPlot = new Plot(this.cluster.getServName(), "Full CPU Data points", Integer.toString(analysisCount));
		Plot memdataPlot = new Plot(this.cluster.getServName(), "Full Memory Data points", Integer.toString(analysisCount));
		double[] cx = new double[c_map.size()];
		double[] cy = new double[c_map.size()];
		double[] mx = new double[m_map.size()];
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

		System.out.println("Full CPU timestamps: " + Arrays.toString(cx));
		System.out.println("Full CPU values: " + Arrays.toString(cy));
		System.out.println("Full Memory values: " + Arrays.toString(my));
		System.out.println("Full Memory timestamps: " + Arrays.toString(mx));
		
		double[] min_mem_gradients = runMinuteMemoryAnalysis(mx, my, "Full Minute Memory Gradient");
		double[] min_cpu_gradients =runMinuteCPUAnalysis(cx, cy, "Full Minute CPU Gradient");
		//double[] mem_gradients = gradient(mx, my);// deltas in memory usage
		//double[] cpu_gradients = gradient(cx, cy);// deltas in cpu usage

		//System.out.println("Full cpu Grads: " + Arrays.toString(cpu_gradients));
		//System.out.println("Full Memory gradients: " + Arrays.toString(mem_gradients));
		System.out.println("Full cpu Grads: " + Arrays.toString(min_cpu_gradients));
		System.out.println("Full Memory gradients: " + Arrays.toString(min_mem_gradients));
		
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
	 * Using the result, a symptom is created and logged in the symptoms log list
	 * and if the result is more or less than 0.5 then the Analysis manager is
	 * notified and the current symptoms for the whole topology are logged in a new
	 * system state.
	 * 
	 * @param result
	 *            from the diagnosis
	 */
	public void createSymptom(double result) {
		if (result > 0 && result < 0.5) {
			Symptom nSymp = new Symptom(cluster.getServName(), "Add", result);
			symplogs.add(nSymp);
		} else if (result < 0 && result > -0.5) {
			Symptom nSymp = new Symptom(cluster.getServName(), "Remove", result);
			symplogs.add(nSymp);
		} else if (result > 0.5) {
			Symptom nSymp = new Symptom(cluster.getServName(), "Add", result);
			symplogs.add(nSymp);
			notifyObservers();

		} else if (result < -0.5) {
			Symptom nSymp = new Symptom(cluster.getServName(), "Remove", result);
			symplogs.add(nSymp);
			notifyObservers();
		}
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
	public double[] gradient(double[] x, double[] y) {

		if (x.length != y.length) {
			System.out.println("ARRAYS X AND Y ARE OF DIFFERENT SIZE. CANNOT CALCULATE GRADIENT");
		} else {
			double[] grad = new double[x.length];
			for (int i = 0, j = 0; i <= x.length - 1 && j <= y.length - 1; i++, j++) {
				if (i != x.length - 1 || j != y.length - 1) {
					double thisgrad = (y[j + 1] - y[j]) / (x[i + 1] - x[i]);
					grad[i] = thisgrad;
				} else {
					System.out.println("\n LAST VALUES IN ARRAYS");
					double windowgrad = (y[j] - y[0]) / (x[i] - x[0]);
					grad[i] = windowgrad;
					System.out.println("\n LAST VALUES IN ARRAYS, SETTING TIME WINDOW GRAD = " + windowgrad);
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
	public double[] gradient(double[] x, double[] y, int min) {

		if (x.length != y.length) {
			System.out.println("ARRAYS X AND Y ARE OF DIFFERENT SIZE. CANNOT CALCULATE GRADIENT");
		} else {
			double[] grad = new double[x.length];
			for (int i = 0, j = 0; i <= x.length - 1 && j <= y.length - 1; i++, j++) {
				if (i != x.length - 1 || j != y.length - 1) {
					if (checkNext(i, x, min) > 0) {
						double nextX = x[checkNext(i, x, min)];
						double nextY = y[checkNext(i, x, min)];
						double thisgrad = (nextY - y[j]) / (nextX - x[i]);
						grad[i] = thisgrad;
					}else {
						System.out.println("\n NO MORE VALUES FURTHER THAN " + min + " Minutes. Breaking loop");
						double thisgrad = (y[y.length-1] - y[j]) / (x[x.length-1] - x[i]);
						grad[i] = thisgrad;
						break;
					}
				} else {
					System.out.println("\n LAST VALUES IN ARRAYS");
					double windowgrad = (y[j] - y[0]) / (x[i] - x[0]);
					grad[i] = windowgrad;
					System.out.println("\n LAST VALUES IN ARRAYS, SETTING TIME WINDOW GRAD = " + windowgrad);
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
	public int checkNext(int x, double[] xlist, int mins) {
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
	public boolean checkMinute(double current_min, double next_min, int diff) {
		long difference = (long) next_min - (long) current_min;
		long reqired_diff = diff * 60 * 1000;
		if (difference >= reqired_diff) {
			return true;
		} else {
			return false;
		}
	}
}
