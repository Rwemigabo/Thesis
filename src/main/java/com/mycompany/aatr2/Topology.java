package com.mycompany.aatr2;

import java.util.ArrayList;
import java.util.HashMap;

import com.mycompany.aatr2.analyse.Symptom;

public class Topology {
	private ArrayList<Cluster> services = new ArrayList<>();
	private ArrayList<Symptom> symptoms = new ArrayList<>(); //statuses of each of the services at a time
	private String id;
	private HashMap<String, Integer> service_conts = new HashMap<>();	
	
	public Topology() {
		this.id = new RandomString(8).nextString();
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
	
	public void addSymptom(Symptom symp) {
		this.symptoms.add(symp);
	}

	public ArrayList<Cluster> getServices() {
		return services;
	}

	public void setServices(ArrayList<Cluster> services) {
		this.services = services;
	}

	public ArrayList<Symptom> getSymptoms() {
		return symptoms;
	}

	public void setSymptoms(ArrayList<Symptom> symptoms) {
		this.symptoms = symptoms;
	}

	public String getName() {
		return id;
	}

	public void setName(String name) {
		this.id = name;
	}
	
	/**
	 * Find get the file name form the config file and save it as the name of the topology
	 */
	public void topology() {
		
	}
	
	
}
