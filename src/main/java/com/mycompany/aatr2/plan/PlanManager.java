/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.plan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	private ViableTopologies vt = ViableTopologies.getInstance();
	private Topology newT = null;
	private long setTime = 1 * 60 * 1000;
	private final static Logger LOGGER = Logger.getLogger(PlanManager.class.getName());

	public PlanManager() {
		this.obs = new ArrayList<>();
	}

	public static PlanManager getInstance() {
		return inst;
	}

	public void initiate() {
		System.out.println("Initiating plan manager");
		setObservable(AnalyseManager.getInstance());

	}

	// public void changePlan() {
	// // TODO Auto-generated method stub
	//
	// }

	@Override
	public void update() {
		System.out.println(
				"||||||||||||||||||||||||||||Notification received from Analysis|||||||||||||||||||||||||||||||");
		getRequest();
	}

	private void getRequest() {
		AdaptationRequest ar = DockerManager.getInstance().getCurrentTopology().latestRequest();
		// ar.listServices();
		 //vt.defineDynamicTopologies2();
		vt.defineTestTopologies();
		processRequest(ar);

	}

	/*
	 * compares viable topologies to adaptation request and returns the most
	 * relevant/ suitable topology (closest to the recommended adaptation) saves the
	 * recommendation to the list calls the notify observers method
	 * 
	 */
	public void processRequest(AdaptationRequest ar) {

		System.out
				.println("\n ||||||||||||||||||||||||||||PROCESSING ADAPTATION REQUEST|||||||||||||||||||||||||||||||");
		Topology r_top = ar.recommended(); // new recommended topology
		System.out.println("\n !!!!!!!!!!!!RECOMMENDED TOPOLOGY!!!!!!!!!!!!!!!!!!!!");
		for (Entry<String, Double> c : r_top.getService_conts().entrySet()) {

			System.out.println("Service: " + c.getKey() + " Containers: " + c.getValue());
		}
		Topology selected = null; // topology selected to replace currently running topology.
		HashMap<Topology, Integer> map = new HashMap<>();// list of the best topologies
		// double s_diff = 0;
		for (Topology top : vt.getTops()) {
			int points = 0;
			boolean badOption = false;

			if (top.getService_conts() != null) {
				for (Map.Entry<String, Double> rt_entry : r_top.getService_conts().entrySet()) {// iterates over the
					// entry sets of the
					if (!badOption) {
						for (Map.Entry<String, Double> vt_entry : top.getService_conts().entrySet()) {// iterates over
																										// the
							// entry sets of the
							// viable topology
							if (rt_entry.getKey().contains(vt_entry.getKey())) {
								double cont_diff = vt_entry.getValue() - rt_entry.getValue();
								//System.out.println(vt_entry.getValue() +" - "+ rt_entry.getValue()+ " = "+  cont_diff);
								if (cont_diff >= 0) {// 1 or 0
									if (cont_diff == 0) {// || cont_diff == -1) {
										points = points + 3;
									} else if (cont_diff == 1) {
										points = points + 2;
									} else if (cont_diff > 1) {
										points++;
									}
									// difference = difference + cont_diff;
									break;
								} else {
									badOption = true;
									break;
								}

							}else {}//System.out.println(rt_entry.getKey()+" | DOESN'T CONTAIN | "+ vt_entry.getKey());}
						}
					} else {
						break;
					}

				}
				if (!badOption) {
					map.put(top, points);
				}else {System.out.println(top.getFilename() +" Is a bad option");}
			} else {
				System.out.println("Mapping of cluster and number of containers not found.");
			}
			

			// if(vt.getTops().indexOf(top) == 0) {
			// selected = top;
			// s_diff = difference;
			// }else {
			// if(moreSimilar(s_diff, difference)) {
			// selected = top;
			// s_diff = difference;
			// }
			// }

		}
		System.out.println("List of options \n ");
		for (Entry<Topology, Integer> entry : map.entrySet()) {
			System.out.println(
					entry.getKey().getFilename() + " | " + entry.getValue() + " | " + entry.getKey().getPrice());
			for (Entry<String, Double> c : entry.getKey().getService_conts().entrySet()) {
				System.out.println("Service: " + c.getKey() + " Containers: " + c.getValue() + "\n");
			}

		}
		if (map.size() >= 1) {
			selected = costAnalysis(map);
			// dm.prepareForExecution(selected);
			LOGGER.log(Level.INFO, "..................................Topology Selected " + selected.getID());

			setNewT(selected);

		}else { 
		System.out.println("None of the topology options were good enough for the load");}

		
		// if(DockerManager.getInstance().getLastExecTime()!= null) {
		// long timepassed = System.currentTimeMillis() -
		// DockerManager.getInstance().getLastExecTime().getTime();
		// if(timepassed >= this.setTime) {
		// notifyObservers();
		// }
		//
		// }else {
		// notifyObservers();
		// }

	}

	Topology costAnalysis(HashMap<Topology, Integer> best) {
		Topology top = null;
		int top_score = 0;

		for (Map.Entry<Topology, Integer> entry : best.entrySet()) {
			Topology t1 = entry.getKey();
			int score = entry.getValue();
			if (top == null) {
				top = t1;
				top_score = score;
			} else {
				if (t1.getPrice() <= top.getPrice() && score >= top_score) {
					top = t1;
					top_score = score;
				} else if (t1.getPrice() >= top.getPrice() && score > top_score) {
					top = t1;
					top_score = score;
				}
			}

		}
		return top;
	}

	/*
	 * returns true if the new topology is more similar to the suggested one
	 * 
	 * @param cs is the currently selected topology's similarity score
	 * 
	 * @param s is the similarity score for the topology being checked.
	 */
	public boolean moreSimilar(double cs, double s) {
		double myNumber = 0;
		double champ = Math.abs(cs - myNumber);
		double contender = Math.abs(s - myNumber);
		if (champ > contender) {
			return true;
		} else {
			return false;
		}

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

	}

	public Topology getNewT() {
		return newT;
	}

	public void setNewT(Topology newT) {
		this.newT = newT;
		System.out.println("Topology to be deployed " + "\n" + newT.toString());
		vt.clearTops();
	}

	/**
	 * 
	 * @return wait time set before the next topology change can be done
	 */
	public long getSetTime() {
		return this.setTime;

	}

}
