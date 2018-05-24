package com.mycompany.aatr2.analyse;

import java.util.HashMap;

import com.mycompany.aatr2.Cluster;
import com.mycompany.aatr2.DockerManager;

/**
 * Class to create adaptation requests by the Analysis components.
 * A symptom is added to the map if it is outside the parameters set.
 * 
 * @author eric
 *
 */
public class AdaptationRequest {
	
	private HashMap<Cluster, Symptom> adapt;
	private DockerManager dm = DockerManager.getInstance();
	
	public AdaptationRequest() {
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

	public HashMap<Cluster, Symptom> getAdapt() {
		return adapt;
	}

	public void setAdapt(HashMap<Cluster, Symptom> adapt) {
		this.adapt = adapt;
	}
	
	public void addSymptom(Cluster c, Symptom e) {
		adapt.put(c, e);
	}


}
