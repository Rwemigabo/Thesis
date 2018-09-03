package com.mycompany.aatr2.analyse;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import com.mycompany.aatr2.Topology;

/*
 * Created when the analysis of the application confirm that the system is out of scope of it's parameters.
 */
public class AdaptationRequest {
	private HashMap<String, Double> adapt; // container image name and number of suggested containers required.
	private long time;

	// private Topology newTop;
	public AdaptationRequest() {
		this.adapt = new HashMap<>();
		this.time = System.currentTimeMillis();
	}

	public HashMap<String, Double> getAdapt() {
		return adapt;
	}

	public long getTime() {
		return time;
	}

	public void addItem(String id, double conts) {
		this.adapt.put(id, conts);
	}
	
	int randomNumber(int x, int y) {
		Random r = new Random();
		int Low = x;
		int High = y;
		int Result = r.nextInt(High-Low) + Low;
		return Result;
	}

	/**
	 * creates a new topology from the analysis performed
	 * @return a topology recommendation from the analysis performed.
	 */
	public Topology recommended() {
		Topology n_top = new Topology();
		n_top.setService_conts(adapt);
		return n_top;
	}
	
//	public String toString() {
//		return "Time of request = " + this.time + listServices();
//	}

	public void listServices() {
		for(Entry<String, Double> entry : adapt.entrySet()) {
			System.out.println("Service: " + entry.getKey() + " | Contaier count = " + entry.getValue() + "\n");
		}
	}

}
