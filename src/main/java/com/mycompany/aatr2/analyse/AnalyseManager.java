/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.analyse;


import java.util.ArrayList;

import com.mycompany.aatr2.Cluster;
import com.mycompany.aatr2.Observable;
import com.mycompany.aatr2.Observer;

/**
 * 
 * @author eric
 *
 */
public class AnalyseManager implements Observable, Observer{

    private final ArrayList<Analyser> analysers = new ArrayList<>();
    private static final AnalyseManager inst = new AnalyseManager();
    private final ArrayList<Observer> obs;
    private final ArrayList<SystemState> systState;
    private final ArrayList<AdaptationRequest> adaptationReq;
    
    private AnalyseManager(){
    	this.obs = new ArrayList<>();
    	this.adaptationReq = new ArrayList<>();
    	this.systState = new ArrayList<SystemState>();
    }
    
    public void newAnalyser(Cluster c){
        Analyser ana = new Analyser(c);
        setObservable(ana);
        ana.initiate();
        analysers.add(ana);
        
    }
    
    public static AnalyseManager getInstance() {
        return inst;
    }

    public ArrayList<Analyser> getAnalysers() {
        return analysers;
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
		throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
																		// Tools | Templates.
	}

	public ArrayList<SystemState> getArs() {
		return systState;
	}


	@Override
	public synchronized void update() {
		newSystemState();
	}

	@Override
	public void update(String context, double metric) {
		throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
																		// Tools | Templates.
	}

	@Override
	public void setObservable(Observable ob) {
		ob.addObserver(this);
	}
	
	/*
	 * Creates a new system state stores it in the list
	 */
	public void newSystemState() {
		SystemState state = new SystemState();
		for(Analyser ana: analysers) {
			Cluster s = ana.getCluster();
			state.addSymptom(s, ana.getLatest());
		}
		systState.add(state);// should be persisted.
		checkState(state);
	}
	
	/*
	 * Checks the new state to make sure that it is within parameters and if not creates an adaptation request
	 */
	public void checkState(SystemState state) {
		if(state.getState()) {
			AdaptationRequest ar = new AdaptationRequest();
			for(Analyser ana: analysers) {
				Cluster s = ana.getCluster();
				double conts = s.getContainers().size() + state.getAdapt().get(s).getCondition();
				ar.addItem(ana.getAnId(), conts);
			}
			this.adaptationReq.add(ar);
			notifyObservers();
		}
	}
	
	
}
