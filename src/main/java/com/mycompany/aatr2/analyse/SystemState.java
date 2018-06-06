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
	
	private HashMap<Cluster, Symptom> adapt;
	private DockerManager dm = DockerManager.getInstance();
	private boolean state = false;
	
	public SystemState() {
		this.adapt = new HashMap<Cluster, Symptom>();
		initiate();
		
	}

	private void initiate() {
		for(Cluster c : dm.getAppServices()) {
			if(!adapt.containsKey(c)) {
				adapt.put(c, null);
			}
		}
		
	}
	/*
	 * Map of cluster  to symptom
	 */
	public HashMap<Cluster, Symptom> getAdapt() {
		return adapt;
	}

	public void setAdapt(HashMap<Cluster, Symptom> adapt) {
		this.adapt = adapt;
	}
	
	public void addSymptom(Cluster c, Symptom e) {
		adapt.put(c, e);
	}
	
	/*
	 * Method to indicate the current state of the system by counting the number of reported symptoms by the analysis process
	 * if the number of values that need adjustment is more than half, then the state is set to true.
	 * true state means the system needs to adapt to the current situation
	 */
	public void setState() {
		int count = 0;
		for(Symptom value : adapt.values()) {
			if(value.getCondition() >= 1 || value.getCondition() <= 1) {
				count++;
			}
		}
		
		if(count > adapt.size()/2) {
			this.state = true;
		}
	}
	
	public boolean getState() {
		setState();
		return this.state;
	}
}
