package com.mycompany.aatr2;

import java.util.ArrayList;
import java.util.HashMap;

import com.mycompany.aatr2.analyse.AdaptationRequest;

public class Topology {
	private ArrayList<Cluster> services;
	private ArrayList<AdaptationRequest> adapts = new ArrayList<>(); //statuses of each of the services at a time
	private String id;
	private HashMap<String, Integer> service_conts = new HashMap<>();	
	
	public Topology(ArrayList<Cluster> servs) {
		this.id = new RandomString(8).nextString();
		this.services = servs;
		this.setService_conts();
	}
	
	
	public HashMap<String, Integer> getService_conts() {
		return service_conts;
	}

	public void setService_conts() {
		for(Cluster serv: services) {
			service_conts.put(serv.getServName(), serv.getContainers().size());
		}
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
	
	public AdaptationRequest latestRequest(){
		int index = adapts.size() -1;
		return this.adapts.get(index);
	}
	
	/*
	 * First compares the cluster service names and the number of containers in these services
	 * if all are the same returns true and false otherwise.
	 */
	public boolean compare(Topology top) {
		for (Cluster c_ths : this.services) {
			for (Cluster c : top.getServices()) {
				if(!c_ths.compareCluster(c)) {
					return false;
				}
			}
		}
		return true;
		
	}
}
