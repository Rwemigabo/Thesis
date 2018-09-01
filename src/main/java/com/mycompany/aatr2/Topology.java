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
	private int vms;
	private double price;// Price of the topology
	private ArrayList<VirtualMachine> VMS = new ArrayList<>();

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
		System.out.println("Topology " + filename + " created");
	}

	/*
	 * To define viable topologies by the user.
	 * 
	 */
	public Topology(String filename) {
		this.id = new RandomString(8).nextString();
		this.service_conts = new HashMap<>();
		this.filename = filename;
		System.out.println("Topology " + id + " created");
	}

	public int getVms() {
		return vms;
	}

	public void setVms(int vms) {
		this.vms = vms;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	void addVM(VirtualMachine vm) {
		this.VMS.add(vm);
		updatePrice();
		System.out.println("VMs added to" + this.toString());
	}

	// update the price of the topology (Per hour)
	void updatePrice() {
		double totalprice = 0;
		for (VirtualMachine vm : VMS) {
			totalprice = totalprice + vm.getPrice();
		}
		this.price = totalprice;
	}

	public ArrayList<AdaptationRequest> getAdapts() {
		return adapts;
	}

	public void setAdapts(ArrayList<AdaptationRequest> adapts) {
		this.adapts = adapts;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArrayList<VirtualMachine> getVMS() {
		return VMS;
	}

	public void setVMS(ArrayList<VirtualMachine> vMS) {
		VMS = vMS;
	}

	public String getFilename() {
		if (filename != null) {
			return filename;
		} else {
			System.out.println("Topology had no filename");
			this.setFilename("Filename");
		}
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public HashMap<String, Double> getService_conts() {
		return service_conts;
	}

	// public void setPrice(double price) {
	// this.price = price;
	// }
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

	/**
	 * 
	 * @return last adaptation request added by the analysis component manager.
	 */
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

	/**
	 * total price of using the topology per month
	 * 
	 * @return the price
	 */
	public double getPrice() {
		return this.price;
	}
	
	public String toString() {
		return "Topology " + this.filename + " | Virtual machines = " + this.VMS.size() + " | Price ="  + this.price;
	}

}
