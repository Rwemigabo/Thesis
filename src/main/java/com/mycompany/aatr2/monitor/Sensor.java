/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.monitor;

import com.mycompany.aatr2.DockerManager;
import com.mycompany.aatr2.Observable;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.CpuStats;
import com.spotify.docker.client.messages.MemoryStats;
import java.util.ArrayList;
import com.mycompany.aatr2.Observer;
//import com.spotify.docker.client.messages.NetworkStats;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A sensor observes the context element specified to it and notifies it's
 * subscribers of any changes. A context element name has to be provided and
 * that property will be set to be watched.
 *
 * @author eric
 */
public class Sensor extends Thread implements Observable {

	private final int sensId;
	private final ArrayList<Observer> obs = new ArrayList<>();
	private ContextElement property = null;
	private String name;
	private CpuStats cpu;
	// private CpuStats pcpu;
	private MemoryStats mem;
	// private NetworkStats net;
	private final String contID;
	private double cpuPerc;
	private double free;
	private final DockerManager dm = DockerManager.getInstance();
	private final String contNm;
	private long preCpu = 0;
	private long preSystem = 0;

	/**
	 * 
	 * @param ID
	 *            sensor's id
	 * @param context
	 *            what is being monitored (CPU/ memory)
	 * @param min
	 *            min threshold
	 * @param max
	 *            max threshold
	 * @param cid
	 *            container name
	 */
	public Sensor(int ID, String context, double min, double max, String cid) {
		this.sensId = ID;
		this.name = context;
		this.contID = cid;
		this.property = new ContextElement(max, min, context);
		this.contNm = dm.getContainer(cid).image();
		try {
			this.preCpu =dm.getContainerStats(this.contID).precpuStats().cpuUsage().totalUsage();
			this.preSystem = dm.getContainerStats(this.contID).precpuStats().systemCpuUsage();
		} catch (DockerException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Notify if the CPU threshold is crossed .
	 * 
	 * @throws DockerException
	 * @throws InterruptedException
	 */
	public void watchCPU() throws DockerException, InterruptedException {
		this.cpu = dm.getContainerStats(this.contID).cpuStats();

		if (dm.getContainer(this.contID).state() != null) {// .contains("running")
			this.cpuPerc = calculateCPU(this.cpu.cpuUsage().totalUsage(),
					this.preCpu, this.cpu.systemCpuUsage(),
					this.preSystem,
					this.cpu.cpuUsage().percpuUsage().size());
			checkThreshold(this.cpuPerc, contNm);

		} else {
			System.out.print("Not running anymore");
		}
	}

	/**
	 * 
	 * @return container's id
	 */
	public String getContID() {
		return contID;
	}

	/**
	 * Notify if the Memory threshold is crossed .
	 * 
	 * @throws DockerException
	 * @throws InterruptedException
	 */
	public void watchMemory() throws DockerException, InterruptedException {
		this.mem = dm.getContainerStats(this.contID).memoryStats();
		if (dm.getContainer(this.contID).state().contains("running")) {
			this.free = memoryStat(this.mem.limit(), this.mem.usage());
			checkThreshold(free, contNm);
		}
	}

	@Override
	/**
	 * checks the current metric every 2 seconds and returns the statistic if the
	 * threshold has been crossed.
	 */
	public void run() {
		if (this.name.equals("CPU")) {
			System.out.print("\n Monitoring CPU of conitainer " + contNm);
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					try {
						watchCPU();
					} catch (DockerException | InterruptedException ex) {
						Logger.getLogger(Sensor.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}, 1, 1 * 2000);
		} else if (this.name.equals("Memory")) {
			System.out.print("\n Monitoring Memory of conitainer " + contNm);
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					try {
						watchMemory();
					} catch (DockerException | InterruptedException ex) {
						Logger.getLogger(Sensor.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}, 1, 1 * 2000);
		}
	}

	/**
	 * Method to monitor the threshold of the given metric notify the observers if
	 * value is over or under threshold
	 *
	 * @param metric
	 *            the metric being monitored by the sensor
	 * @param cont
	 *            container name
	 */
	public void checkThreshold(final double metric, final String cont) {
		if (metric > this.property.getThreshold().getUpperBound()
				|| metric < this.property.getThreshold().getLowerBound()) {
			System.out.print("\n Notifying monitor of container " + cont + " about " + this.name + " " + metric + "%");
			// notifyObservers(metric);
			notifyObservers();
		} else {
			// System.out.print("");
		}

	}

	/**
	 * Calculates the percentage CPU being used.
	 *
	 * @param totalUsage
	 *            current total CPU usage of the container
	 * @param prevCPU
	 *            previous CPU usage
	 * @param totalSystUse
	 *            current Total system CPU usage
	 * @param prevSystem
	 *            previous total system CPU usage
	 * @param perCpuUsage
	 *            number of cores
	 * @return the CPU percentage being used
	 */
	public double calculateCPU(long totalUsage, long prevCPU, long totalSystUse, long prevSystem, int perCpuUsage) {
		
		double cpuP = 0;
		float cpuDelta = (float) totalUsage - (float) prevCPU;
		float systemDelta = (float) totalSystUse - (float) prevSystem;
		System.out.println("\n cpuDelta "+ cpuDelta+"systemDelta "+ systemDelta  + " Container "+ this.contID);
		if (systemDelta > 0.0 && cpuDelta > 0.0) {
			cpuP = ((cpuDelta / systemDelta) * (perCpuUsage)) * 100;
		}
		System.out.println("\n CPU percentage "+ cpuP + " Container "+ this.contID);
		return cpuP;

	}

	/**
	 *
	 * @param limit
	 * @param usage
	 * @return percentage of the memory used.
	 */
	public double memoryStat(long limit, long usage) {
		double memMetric = ((double) usage / (double) limit) * 100;
		return memMetric;
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
	public void notifyObservers(double metric) {
		obs.forEach((ob) -> {
			ob.update(this.name, metric);
		});
	}

	public int getID() {
		return this.sensId;
	}

	public String sensorContext() {
		return this.name;
	}

	public void setContext(String ctxt, long min, long max) {
		this.name = ctxt;
	}

	public double getLogValue() {
		if (this.name.equals("Memory")) {
			System.out.println("Returned Memory" + free);
			return this.free;

		} else if (this.name.equals("CPU")) {
			System.out.println("Returned CPU" + this.cpuPerc);
			return this.cpuPerc;
		}
		System.out.println("Returned NULL");
		return 0;
	}

	@Override
	public void notifyObservers() {
		obs.forEach((ob) -> {
			ob.update();
		});
	}

}
