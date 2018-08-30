/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.execute;


import java.io.IOException;
import java.sql.Timestamp;

import com.mycompany.aatr2.DockerManager;
import com.mycompany.aatr2.Observable;
import com.mycompany.aatr2.Observer;
import com.mycompany.aatr2.Topology;
import com.mycompany.aatr2.plan.PlanManager;

/**
 *Manages the various execute processes
 * @author eric
 */
public class ExecuteManager implements Observer{

	private static final ExecuteManager inst = new ExecuteManager();
	
	public ExecuteManager() {
	}
	
	public static ExecuteManager getInstance() {
		return inst;
	}
	
	public void initiate() {
		System.out.println("Initiating Execute manager");
		setObservable(PlanManager.getInstance());

	}


	@Override
	public void update() {
		try {
			executePlan();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	private void executePlan() throws InterruptedException {
		PlanManager pm = PlanManager.getInstance();
		Topology ntop = pm.getNewT();
		String fname = ntop.getFilename();
		Runtime rt = Runtime.getRuntime();
		try {
			Process pr = rt.exec(fname + "up -d");
			pr.waitFor();
			DockerManager.getInstance().setLastExecTime(new Timestamp(System.currentTimeMillis()));
		} catch (IOException e) {
			e.printStackTrace();
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
	
    
}
