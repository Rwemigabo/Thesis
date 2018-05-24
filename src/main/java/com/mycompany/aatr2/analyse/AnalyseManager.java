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
 */
public class AnalyseManager implements Observable{

    private final ArrayList<Analyser> analysers = new ArrayList<>();
    private static final AnalyseManager inst = new AnalyseManager();
    private final ArrayList<Observer> obs;
    private ArrayList<AdaptationRequest> ars;
    
    private AnalyseManager(){
    	this.obs = new ArrayList<>();
    	this.ars = new ArrayList<AdaptationRequest>();
    }
    
    public void newAnalyser(Cluster c){
        int newID = analysers.size() + 1;
        Analyser ana = new Analyser(newID, c);
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

	public ArrayList<AdaptationRequest> getArs() {
		return ars;
	}

	public void setArs(ArrayList<AdaptationRequest> ars) {
		this.ars = ars;
	}
	
	

}
