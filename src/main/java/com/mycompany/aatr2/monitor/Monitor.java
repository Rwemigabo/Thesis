/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.monitor;

import com.mycompany.aatr2.Cluster;
//import com.mycompany.aatr2.DockerManager;
import com.mycompany.aatr2.Observable;
import com.mycompany.aatr2.Observer;
import com.mycompany.aatr2.SensorManager;
import com.mycompany.aatr2.monitor.data.StatisticsLog;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Capture and record new statistics to the database every set number of minutes
 * or seconds.
 *
 * @author eric
 */
public class Monitor implements Observer, Observable {

	private final ArrayList<StatisticsLog> stats;
	// private ArrayList<StatisticsLog> statsq;
	private final int ID;
	private final ArrayList<Sensor> sens;
	private final ArrayList<Observer> obs;
	private final List<Container> conts;
	private Cluster service;
	private SensorManager sm = SensorManager.getInstance();
	// private DockerManager dm = DockerManager.getInstance();

	/**
	 *
	 * @param id
	 *            an id for the new monitor
	 * @param s
	 *            service being monitored.
	 */
	public Monitor(int id, Cluster s) throws DockerException, InterruptedException {
		this.sens = new ArrayList<>();
		this.ID = id;
		this.service = s;
		// this.stats = new StatisticsLog(serv.getServName());
		this.obs = new ArrayList<>();
		this.conts = this.service.getContainers();
		this.stats = new ArrayList<>();
		// this.statsq = new ArrayList<>();

	}

	/**
	 * for each container; create a new statistics log for it using it's id and the
	 * name of the service, add sensors for CPU and Memory start monitoring the
	 * container
	 * 
	 * @throws DockerException
	 * @throws InterruptedException
	 */
	public void initiate() throws DockerException, InterruptedException {
		for (Container container : conts) {
			StatisticsLog sl = new StatisticsLog(container.id());
			stats.add(sl);
			this.sens.add(sm.newSensor("CPU", 0.00, 75.00, container.id()));
			this.sens.add(sm.newSensor("Memory", 0.00, 75.00, container.id()));
			if (container.state().equals("running")) { // && container.state().equals("running")
				System.out.print("\n Accessing sensors to initiate metric watch");
				startMonitoring(container.id());
			} else {
				System.out.print("\n Sorry container state: " + container.state());
			}
		}
		this.service.setLogs(this.stats);
		scheduleNotification();
	}

	@Override
	public synchronized void update() {
		System.out.println("\n Out  of scope statistic reported");
		newStatistic();
	}

	@Override
	public synchronized void update(String context, double metric) {
		double metric2 = 0;
		for (Sensor sen : sens) {
			String servId = this.service.getServName();
			if (!sen.sensorContext().equals(context)) {
				metric2 = sen.getLogValue();
			}
			if (context.equals("CPU")) {
				this.service.addStat(servId, metric, metric2);
				// sl.newStatistic(this.service.getServName(), sen.getContID(), metric,
				// metric2);

			} else {
				this.service.addStat(servId, metric2, metric);
				// sl.newStatistic(this.service.getServName(), sen.getContID(), metric2,
				// metric);
			}

		}
		notifyObservers();
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

	}

	/**
	 * 
	 * @param id
	 * @throws DockerException
	 * @throws InterruptedException
	 */
	public void startMonitoring(String id) throws DockerException, InterruptedException {

		sens.forEach((Sensor sen) -> {
			if (sen.getContID().equals(id)) {
				System.out.print("\n Initiating Sensor for " + sen.sensorContext() + " " + sen.getContID());
				setObservable(sen);
				sen.start();
			} else {
			}
		});
	}

	/**
	 * updates the log with a new statistic every 2 seconds.
	 */
	public void scheduleNotification() {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				newStatistic();
			}
		}, 1 * 2000, 1 * 2000);
	}

	/**
	 * Creates a new statistic if both sensors for the container are already up.
	 * Only notifies the observers/ creates new statistics if all containers in the
	 * service have sensors set up.
	 */
	public synchronized void newStatistic() {
		double metric1 = 0;
		double metric2 = 0;
		boolean notify = true;
		for (Container cont : conts) {
			if (sens.contains(getSensor(cont.id(), "CPU")) && sens.contains(getSensor(cont.id(), "Memory"))) {
				for (Sensor sen : this.sens) {
					if (sen.getContID().equals(cont.id())) {
						if (sen.getName().equals("CPU")) {
							metric1 = sen.getLogValue();
							Sensor s = getSensor(sen.getContID(), "Memory");
							metric2 = s.getLogValue();
						} else {
							metric2 = sen.getLogValue();
							Sensor s = getSensor(sen.getContID(), "CPU");
							metric1 = s.getLogValue();
						}
					}
				}
			} else {
				System.out.println("Only one sensor Started for this container");
				notify = false;
				break;
			}

			// create new stat and add it to the container's log
			this.service.addStat(cont.id(), metric2, metric1);
			System.out.println(
					"\n New Stat log from " + this.service.getServName() + " Memory " + metric2 + " CPU " + metric1);
		}
		if (notify) {
			notifyObservers();
		}
	}

	public int getID() {
		return this.ID;
	}

	@Override
	public void notifyObservers() {
		obs.forEach((ob) -> {
			ob.update();
		});
	}

	@Override
	public void setObservable(Observable ob) {
		ob.addObserver(this);
	}

	public ArrayList<StatisticsLog> getStats() {
		return stats;
	}

	public Cluster getService() {
		return service;
	}

	public void setService(Cluster serv) {
		this.service = serv;
	}

	/**
	 * Returns the sensor with the given params
	 * 
	 * @param cid
	 * @param context
	 * @return
	 */
	public Sensor getSensor(String cid, String context) {
		for (Sensor sen : this.sens) {
			if (sen.getContID().equals(cid) && sen.sensorContext().equals(context)) {
				return sen;
			}
		}
		System.out.println("Didn't find this sensor" + cid + context);
		return null;

	}
	

}
