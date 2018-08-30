package com.mycompany.aatr2.analyse;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import net.sourceforge.jFuzzyLogic.FIS;
//import net.sourceforge.jFuzzyLogic.rule.Rule;

public class Analysis extends Thread {
	private HashMap<Timestamp, Double> cpuStats;
	private HashMap<Timestamp, Double> memStats;
	private Analyser ana;
	private int analysisCount;

	public Analysis(HashMap<Timestamp, Double> cpuStats, HashMap<Timestamp, Double> memStats, Analyser ana) {
		this.memStats = memStats;
		this.cpuStats = cpuStats;
		this.ana = ana;
	}

	/**
	 * Runs an analysis on the whole dataset since the first recorded data point
	 * 
	 * @param cpuStats
	 * @param memStats
	 */
	private void runFullDataAnalysis() {
		TreeMap<Timestamp, Double> c_map = new TreeMap<>(this.cpuStats);// order by the timestamp
		TreeMap<Timestamp, Double> m_map = new TreeMap<>(this.memStats);// order by the timestamp
		Plot cpudataPlot = new Plot(ana.getCluster().getServName(), "Full CPU Data points",
				Integer.toString(analysisCount));
		Plot memdataPlot = new Plot(ana.getCluster().getServName(), "Full Memory Data points",
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

		System.out.println("Cpu prediction: " + CPU_prediction + " for " + ana.getCluster().getServName());
		System.out.println("Memory prediction: " + Mem_prediction + " for " + ana.getCluster().getServName());

	}

	/**
	 * Performs a prediction for the next time Window, plots the predictions for the
	 * next window and plots the predictions for all the full data including
	 * previous predictions.
	 * 
	 * @param treemap
	 *            of data to perform prediction on
	 * @return
	 */
	private double makePrediction(TreeMap<Timestamp, Double> trendList, String metric_nm) {
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
		long lastvalue = trendList.lastEntry().getKey().getTime() + ana.getMinuteWindow(); // last timestamp in the
																							// prediction
		// values
		for (long afterlast = trendList.lastEntry().getKey().getTime()
				+ 100; afterlast <= lastvalue; afterlast += frame) {
			double metric = regression.predict(afterlast);
			y_predicts.add(metric);
			plot_data.put(afterlast, metric);
			// full_pred_data.put(afterlast, metric);

		}

		prediction = Collections.max(y_predicts);
		// LOGGER.log(Level.INFO, "Max prediction for next time window = "+ prediction+
		// " for " +metric_nm);
		if (plot_data.isEmpty()) {
			System.out.println("Prediction plot data is empty");
		} else {
			plotData(plot_data, "Window Prediction for " + metric_nm);
			// plotData(full_pred_data, "Full Prediction Data for " + metric_nm);
		}

		return prediction;
	}

	private void plotData(TreeMap<Long, Double> map, String title) {
		Plot p = new Plot(ana.getCluster().getServName(), title, Integer.toString(analysisCount));
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
		System.out.println("Number of replicas required = " + result);

		createSymptom(Math.round(result));
		// LOGGER.log(Level.INFO, "Average = " + result);
		// for (Rule r :
		// fis.getFunctionBlock("recommend").getFuzzyRuleBlock("first").getRules())
		// System.out.println(r);

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
		if (result > 0 && result < 1) {
			Symptom nSymp = new Symptom(ana.getCluster().getServName(), "Add", result);
			System.out.println("Here 1");
			//ana.setLatest(nSymp);
			System.out.println("Here 2");
			ana.addSymptom(nSymp);
			System.out.println("finished");
			ana.notification();
			System.out.println("Here 4");
		} else if (result < 0 && result > -1) {
			Symptom nSymp = new Symptom(ana.getCluster().getServName(), "Remove", result);
			System.out.println("Here 1");
			//ana.setLatest(nSymp);
			System.out.println("Here 2");
			ana.addSymptom(nSymp);
			System.out.println("Here 3");
			ana.notification();
			System.out.println("Here 4");
		} else if (result >= 1) {
			Symptom nSymp = new Symptom(ana.getCluster().getServName(), "Add", result);
			System.out.println("Here 1");
			//ana.setLatest(nSymp);
			System.out.println("Here 2");
			ana.addSymptom(nSymp);
			System.out.println("Here 3");
			ana.notification();
			System.out.println("Here 4");

		} else if (result <= -1) {
			Symptom nSymp = new Symptom(ana.getCluster().getServName(), "Remove", result);
			System.out.println("Here 1");
			//ana.setLatest(nSymp);
			System.out.println("Here 2");
			ana.addSymptom(nSymp);
			System.out.println("Here 3");
			ana.notification();
			System.out.println("Here 4");
		} 
	}

	@Override
	public void run() {
		runFullDataAnalysis();
	}
}