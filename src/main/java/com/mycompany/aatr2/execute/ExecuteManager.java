/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.execute;

import com.mycompany.aatr2.monitor.Cluster;

/**
 *Manages the various execute processes
 * @author eric
 */
public class ExecuteManager {

	private static final ExecuteManager inst = new ExecuteManager();
	
	
	public ExecuteManager() {
		
	}
	
	public static ExecuteManager getInstance() {
		return inst;
	}

	public void newExecutionManager(Cluster serv) {
		// TODO Auto-generated method stub
		
	}
    
}
