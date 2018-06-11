package com.mycompany.aatr2;

import java.util.ArrayList;
import java.util.HashMap;

import com.mycompany.aatr2.analyse.AdaptationRequest;

public class Topology {
	private ArrayList<Cluster> services;
	private ArrayList<AdaptationRequest> adapts = new ArrayList<>(); // statuses of each of the services at a time
	private String id;
	private HashMap<String, Double> service_conts;
	private String filename;

	public Topology(ArrayList<Cluster> servs) {
		this.id = new RandomString(8).nextString();
		this.services = servs;
		this.service_conts = new HashMap<>();
		this.setService_conts();
	}

	/*
	 * To define viable topologies by the user.
	 * 
	 */
	public Topology() {
		this.id = new RandomString(8).nextString();
		this.service_conts = new HashMap<>();
	}
	
	/*
	 * To define viable topologies by the user.
	 * 
	 */
	public Topology(String filename) {
		this.id = new RandomString(8).nextString();
		this.service_conts = new HashMap<>();
		this.filename = filename;
	}
	
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public HashMap<String, Double> getService_conts() {
		return service_conts;
	}

	/*
	 * sets the mapping of the services to number of containers using the list of
	 * services available.
	 * 
	 */
	public void setService_conts() {
		if (services != null) {
			for (Cluster serv : services) {
				double num = serv.getContainers().size();
				service_conts.put(serv.getServName(), num);
			}
		}
	}
	
	public void setService_conts(HashMap<String, Double> service) {
		this.service_conts = service;
	}

	public void addService(String serv_nm, double conts) {
		service_conts.put(serv_nm, conts);
	}

	public void addService(Cluster s) {
		this.services.add(s);
	}

	public void addSymptom(AdaptationRequest symp) {
		this.adapts.add(symp);
	}

	public ArrayList<Cluster> getServices() {
		return services;
	}

	public void setServices(ArrayList<Cluster> services) {
		this.services = services;
	}

	public ArrayList<AdaptationRequest> getSymptoms() {
		return adapts;
	}

	public void setSymptoms(ArrayList<AdaptationRequest> symptoms) {
		this.adapts = symptoms;
	}

	public String getID() {
		return id;
	}

	public void setName(String name) {
		this.id = name;
	}

	public void addRequest(AdaptationRequest ar) {
		this.adapts.add(ar);
	}

	public AdaptationRequest latestRequest() {
		int index = adapts.size() - 1;
		return this.adapts.get(index);
	}

	/*
	 * To check if the topologies are exactly the same in terms of number of
	 * containers. Outputs true or false First compares the cluster service names
	 * and the number of containers in these services if all are the same returns
	 * true and false otherwise.
	 */
	public boolean compare(Topology top) {
		for (Cluster c_ths : this.services) {
			for (Cluster c : top.getServices()) {
				if (!c_ths.compareCluster(c)) {
					return false;
				}
			}
		}
		return true;

	}
}
