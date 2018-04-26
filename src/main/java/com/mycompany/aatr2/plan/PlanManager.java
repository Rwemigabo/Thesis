/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.plan;

import com.mycompany.aatr2.monitor.Cluster;

/**
 *Manages the various plan processes
 * @author eric
 */
public class PlanManager {
	
	private static final PlanManager inst = new PlanManager();
	
	
	public PlanManager() {
		
	}
	
	public static PlanManager getInstance() {
		return inst;
	}

	public void newPlanManager(Cluster serv) {
		// TODO Auto-generated method stub
		
	}
    
}
