package com.mycompany.aatr2.analyse;

import java.util.HashMap;
import com.mycompany.aatr2.Cluster;
import com.mycompany.aatr2.DockerManager;

/**
 * Creates an instance of the current system state, which indicates if an adaptation is necessary.
 * A symptom is added to the hashmap if it is outside the set parameters.
 * 
 * @author eric
 *
 */
public class SystemState {
	
	private HashMap<Cluster, Symptom> current_reqs;
	private boolean state = false;
	
	public SystemState() {
		this.current_reqs = new HashMap<Cluster, Symptom>();
		initiate();
		
	}

	private void initiate() {
		for(Cluster c : DockerManager.getInstance().getAppServices()) {
			if(!current_reqs.containsKey(c)) {
				current_reqs.put(c, null);
			}
		}
		
	}
	/*
	 * Map of cluster  to symptom
	 */
	public HashMap<Cluster, Symptom> getcurrent_reqs() {
		return current_reqs;
	}

	public void setcurrent_reqs(HashMap<Cluster, Symptom> current_reqs) {
		this.current_reqs = current_reqs;
	}
	
	public Symptom getSymptom(Cluster c) {
		return this.current_reqs.get(c);
	}
	
	public void addSymptom(Cluster c, Symptom e) {
		current_reqs.put(c, e);
	}
	
	/*
	 * Method to indicate the current state of the system by counting the number of reported symptoms by the analysis process
	 * if the number of values that need adjustment is more than half, then the state is set to true.
	 * true state means the system needs a new adaptation plan to be formed to follow the slos
	 */
	public void setState() {
		int count = 0;
		for(Symptom symp : current_reqs.values()) {
			if(symp.getCondition() >= 1 || symp.getCondition() <= -1) {
				count++;
			}
		}
		
		if(count > Math.round(0.25 * current_reqs.values().size())) {
			this.state = true;
		}
	}
	
	/**
	 * calls the set state function to set the state of the system.
	 * 
	 * @return true state meaning the system needs a new adaptation plan and false otherwise
	 */
	public boolean getState() {
		setState();
		return this.state;
	}
}
