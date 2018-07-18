/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.monitor.data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author eric
 */
public class StatisticsLog {

	private ArrayList<Statistic> monitorstats;
	private ArrayList<Statistic> queue;
	private final String container;
	private long min_checkpoint = 0;
	private long hr_checkpoint = 0;

	public StatisticsLog(String snm) {
		this.container = snm;
		this.monitorstats = new ArrayList<>();
		this.queue = new ArrayList<>();
	}

	public ArrayList<Statistic> getMonitorstats() {
		return monitorstats;
	}

	public String getServiceName() {
		return container;
	}

	public Statistic getSystemStat(Timestamp time) {
		for (Statistic stat : monitorstats) {
			if (stat.getTimestamp().equals(time)) {
				return stat;
			}
		}
		return null;
	}

	public ArrayList<Statistic> getSystemStats(Timestamp t1, Timestamp t2) {

		ArrayList<Statistic> s = new ArrayList<>();
		for (Statistic stat : monitorstats) {
			if (stat.getTimestamp().after(t1) && stat.getTimestamp().before(t2)) {
				s.add(stat);
			}
		}
		return s;
	}

	public void newStatistic(String snme, String contid, double mem, double cpu) {
		Statistic s = new Statistic(snme, contid, cpu, mem);
		this.queue.add(s);
		this.monitorstats.add(s);
	}

	public void setMonitorStats(ArrayList<Statistic> stats) {
		this.monitorstats = stats;
	}

	public Statistic getLatest() {
		System.out.println("\n Queue size : " + queue.size() + " for " + container);
		Statistic s = null;
		if (this.queue.size() > 0) {
			s = this.queue.get(0);
		} else {
		}
		return s;
	}

	public void removeProcessed() {
		this.queue.remove(0);
	}

	/*
	 * returns cpu stats between the given timestamps.
	 */
	public HashMap<Timestamp, Double> getCPUStats(Timestamp t, Timestamp t1) {
		HashMap<Timestamp, Double> cpu1 = new HashMap<>();
		ArrayList<Statistic> s = getSystemStats(t1, t);
		System.out.println("Number of statistics between the timestamps: " + s.size());
		for (Statistic stat : s) {
			Timestamp t2 = stat.getTimestamp();
			double c = stat.getCpu();
			cpu1.put(t2, c);
		}
		return cpu1;
	}

	/*
	 * returns memory stats between the given timestamps.
	 */
	public HashMap<Timestamp, Double> getMemStats(Timestamp t, Timestamp t1) {
		HashMap<Timestamp, Double> mem = new HashMap<>();
		ArrayList<Statistic> s = getSystemStats(t1, t);
		for (Statistic stat : s) {
			Timestamp t2 = stat.getTimestamp();
			double m = stat.getMemory();
			mem.put(t2, m);
		}
		return mem;
	}

	public void setminCheckpoint(long chk) {
		this.min_checkpoint = chk;
	}

	public long getminCheckpoint() {
		return min_checkpoint;
	}

	public void setHrCheckpoint(long chk) {
		this.hr_checkpoint = chk;
	}

	public long getHrCheckpoint() {
		return hr_checkpoint;
	}

	public String container() {
		return this.container;
	}

	public HashMap<Timestamp, Double> getMemStats() {
		// TODO Auto-generated method stub
		HashMap<Timestamp, Double> mem = new HashMap<>();
		for (Statistic stat : this.monitorstats) {
			Timestamp t2 = stat.getTimestamp();
			double m = stat.getMemory();
			mem.put(t2, m);
		}
		return mem;
	}

	public HashMap<Timestamp, Double> getCPUStats() {
		HashMap<Timestamp, Double> cpu = new HashMap<>();
		for (Statistic stat : this.monitorstats) {
			Timestamp t2 = stat.getTimestamp();
			double m = stat.getCpu();
			cpu.put(t2, m);
		}
		return cpu;
	}
}
