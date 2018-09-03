/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import com.mycompany.aatr2.monitor.SLO;
import com.mycompany.aatr2.monitor.data.StatisticsLog;
import com.spotify.docker.client.messages.Container;
import java.util.ArrayList;
import java.util.List;
//import java.util.Random;

/**
 * Creates a cluster object with the containers that make up the functionality
 * of a service
 * 
 * @author eric
 */
public class Cluster {
	private final List<Container> containers;
	private String servName;
	private ArrayList<StatisticsLog> logs = new ArrayList<>();
	private SLO slo = new SLO();
	private int containerCount;

	public Cluster(String name) {
		this.servName = name;
		this.containers = new ArrayList<>();
		this.containerCount = containers.size();
	}

	// public int getRank() {
	// return rank;
	// }
	//
	// public void setRank(int rank) {
	// this.rank = rank;
	// }

	public List<Container> getContainers() {
		return this.containers;
	}

	public String getServName() {
		return servName;
	}

	public void addContainer(Container container) {
		if (!this.containers.contains(container)) {
			this.containers.add(container);
			this.containerCount = this.containers.size();
		} else {
			System.out.println("container already exists");
		}
	}

	public int getContainerCount() {
		return containerCount;
	}

	public void setContainerCount(int containerCount) {
		this.containerCount = containerCount;
	}

	public void addStat(String cid, double mem, double cpu) {
		for (StatisticsLog log : logs) {
			if (log.getServiceName().equals(cid)) {
				// System.out.println("Found it");
				log.newStatistic(this.servName, cid, mem, cpu);
				break;
			} else {
			}
		}
	}

	public void setServName(String servName) {
		this.servName = servName;
	}

	public void setLogs(ArrayList<StatisticsLog> nlogs) {
		this.logs = nlogs;
	}

	public ArrayList<StatisticsLog> getLogs() {
		return this.logs;
	}

	public StatisticsLog getLog(String sname) {
		for (StatisticsLog sl : logs) {
			if (sl.getServiceName().equals(sname)) {
				return sl;
			}
			break;
		}
		return null;
	}

	public boolean exists(Container c) {
		if (containers.contains(c)) {
			return true;
		} else {
			return false;
		}
	}

	public SLO getSlo() {
		return slo;
	}

	public void setSlo(SLO slo) {
		this.slo = slo;
	}

	/*
	 * Returns true if the clusters have the same name and number of containers
	 * false otherwise.
	 * 
	 */
	public boolean compareCluster(Cluster c) {
		if (c.getServName().equals(this.servName) && this.containers.size() == c.getContainers().size()) {
			return true;
		}
		return false;
	}

	/*
	 * Returns how closely related the clusters are 0 being the closest.
	 * 
	 */
	public int compare(int c) {
		int relation = 0;
		if (this.containers.size() == c) {
			return relation;
		} else {
			relation = c - this.containers.size();
			return relation;
		}

	}

	public void writeToFile(StatisticsLog s) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(s.getstatLogFile()))) {

			oos.writeObject(s);
			// System.out.println("Done");

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
