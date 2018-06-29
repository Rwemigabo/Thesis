/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.plan;

import java.util.ArrayList;
import java.util.Map;

import com.mycompany.aatr2.DockerManager;
import com.mycompany.aatr2.Observable;
import com.mycompany.aatr2.Observer;
import com.mycompany.aatr2.Topology;
import com.mycompany.aatr2.ViableTopologies;
import com.mycompany.aatr2.analyse.AdaptationRequest;
import com.mycompany.aatr2.analyse.AnalyseManager;

/**
 * Manages the various plan processes
 * 
 * @author eric
 */
public class PlanManager implements Observable, Observer {

	private static final PlanManager inst = new PlanManager();
	private final ArrayList<Observer> obs;
	private AnalyseManager am;
	private DockerManager dm = DockerManager.getInstance();
	private ViableTopologies vt = ViableTopologies.getInstance();
	private Topology newT = null;

	public PlanManager() {
		this.obs = new ArrayList<>();
		this.am = AnalyseManager.getInstance();
	}

	public static PlanManager getInstance() {
		return inst;
	}

	public void initiate() {
		setObservable(am);

	}

	// public void changePlan() {
	// // TODO Auto-generated method stub
	//
	// }

	@Override
	public void update() {
		getRequest();
	}

	private void getRequest() {
		AdaptationRequest ar = dm.getCurrentTopology().latestRequest();
		vt.defineTestTopologies();
		processRequest(ar);

	}

	/*
	 * compares viable topologies to adaptation request and returns the most
	 * relevant/ suitable topology (closest to the recommended adaptation)
	 * saves the recommendation to the 
	 * calls the notify observers method
	 * 
	 */
	public void processRequest(AdaptationRequest ar) {
		Topology r_top = ar.recommended(); // new recommended topology
		Topology selected = null; // topology selected to replace currently running topology.
		double s_diff = 0;
		for (Topology top : vt.getTops()) {
			double difference = 0;// value differentiating the topology from the recommended one
			if (top.getService_conts() != null) {
				for (Map.Entry<String, Double> rt_entry : r_top.getService_conts().entrySet()) {// iterates over the
																								// entry sets of the
																								// recommended topology
					for (Map.Entry<String, Double> t_entry : top.getService_conts().entrySet()) {// iterates over the
																									// entry sets of the
																									// viable topology
						if (rt_entry.getKey().equals(t_entry.getKey())) {// if the same service name, find difference
																			// between the # of containers
							double cont_diff = t_entry.getValue() - rt_entry.getValue();
							difference = difference + cont_diff;
							break;
						}
					}
				}
			} else {
				System.out.println("Mapping of cluster and number of containers not found.");
			}
			if(vt.getTops().indexOf(top) == 0) {
				selected = top;
				s_diff = difference;
			}else { 
				if(moreSimilar(s_diff, difference)) {
					selected = top;
					s_diff = difference;
				}
			}

		}
		//dm.prepareForExecution(selected);
		setNewT(selected);
		//notifyObservers();
	}
	
	/*
	 * returns true if the new topology is more similar to the suggested one
	 * @param cs is the currently selected topology's similarity score
	 * @param s is the similarity score for the topology being checked.
	 */
	public boolean moreSimilar(double cs, double s) {
		double myNumber = 0;
		double distance = Math.abs(cs - myNumber);
		double cdistance = Math.abs(s - myNumber);
		if (cdistance > distance) {
			return true;
		}else { return false;}
	
	}

	@Override
	public void update(String context, double metric) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setObservable(Observable ob) {
		ob.addObserver(this);

	}

	@Override
	public void addObserver(Observer o) {
		obs.add(o);

	}

	@Override
	public void removeObserver(Observer o) {
		obs.remove(o);

	}

	@Override
	public void notifyObservers() {
		obs.forEach((Observer ob) -> {
			ob.update();
		});

	}

	@Override
	public void notifyObservers(double metric) {
		// TODO Auto-generated method stub

	}

	public Topology getNewT() {
		return newT;
	}

	public void setNewT(Topology newT) {
		this.newT = newT;
	}
	
	

}
