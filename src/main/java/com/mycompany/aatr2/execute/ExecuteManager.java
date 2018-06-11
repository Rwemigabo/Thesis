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

/**
 *Manages the various execute processes
 * @author eric
 */
public class ExecuteManager implements Observer{

	private static final ExecuteManager inst = new ExecuteManager();
	private DockerManager dm = DockerManager.getInstance();
	
	public ExecuteManager() {
		
	}
	
	public static ExecuteManager getInstance() {
		return inst;
	}

	public void newExecutionManager() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		try {
			executePlan();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void executePlan() throws InterruptedException {

		Topology ntop = dm.getPendingExecution();
		String fname = ntop.getFilename();
		Runtime rt = Runtime.getRuntime();
		try {
			Process pr = rt.exec(fname + "up -d");
			pr.waitFor();
			dm.setLastExecTime(new Timestamp(System.currentTimeMillis()));
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
		// TODO Auto-generated method stub
		ob.addObserver(this);
	}
    
}
