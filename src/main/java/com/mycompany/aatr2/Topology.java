package com.mycompany.aatr2;

import java.util.ArrayList;

import com.mycompany.aatr2.analyse.Symptom;

public class Topology {
	private ArrayList<Cluster> services = new ArrayList<>();
	private ArrayList<Symptom> symptoms = new ArrayList<>(); //statuses of each of the services at a time
	private String name;
	
	public Topology(String nm) {
		this.name = nm;
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
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
