/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.plan;

import java.util.ArrayList;

import com.mycompany.aatr2.Observable;
import com.mycompany.aatr2.Observer;
import com.mycompany.aatr2.analyse.AnalyseManager;

/**
 *Manages the various plan processes
 * @author eric
 */
public class PlanManager implements Observable, Observer{
	
	private static final PlanManager inst = new PlanManager();
	private final ArrayList<Observer> obs;
	private AnalyseManager am;
	
	
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

	public void changePlan() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public synchronized void update() {
		// TODO call to check adaptation requests.
		
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
	
	
    
}
