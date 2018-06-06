package com.mycompany.aatr2.analyse;

import java.util.HashMap;

/*
 * Created when the analysis of the application confirm that the system is out of scope of it's parameters.
 */
public class AdaptationRequest {
	private HashMap<String, Double> adapt; //Analyser ID and number of suggested containers to be used
	private long time;
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
	
}
