package com.mycompany.aatr2.analyse;

import java.util.HashMap;

import com.mycompany.aatr2.Topology;

/*
 * Created when the analysis of the application confirm that the system is out of scope of it's parameters.
 */
public class AdaptationRequest {
	private HashMap<String, Double> adapt; //Analyser ID and number of suggested containers required.
	private long time;
	//private Topology newTop;
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
	
	/*
	 * creates and returns a topology from the analysis performed.
	 */
	public Topology recommended() {
		Topology n_top = new Topology();
		n_top.setService_conts(adapt);
		return n_top;
	}
	
}
