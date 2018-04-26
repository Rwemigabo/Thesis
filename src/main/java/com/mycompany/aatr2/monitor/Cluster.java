/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.monitor;

import com.mycompany.aatr2.monitor.data.StatisticsLog;
import com.spotify.docker.client.messages.Container;
import java.util.ArrayList;
import java.util.List;

/**
 *  Creates a cluster object with the containers that make up the functionality of a service
 * @author eric
 */
public class Cluster {
     private final List<Container> containers;
     private String servName;
     private ArrayList<StatisticsLog> logs = new ArrayList<>();
     private SLO slo = new SLO();
     
     public Cluster(String name){
         this.servName = name;
         this.containers = new ArrayList<>();
     }

    public List<Container> getContainers() {
        return this.containers;
    }

    public String getServName() {
        return servName;
    }

    public void addContainer(Container container) {
    	if(!this.containers.contains(container)) {
    		this.containers.add(container);
        }else {
        	System.out.println("container already exists");
        }
    }
    
    public void addStat(String cid, double mem, double cpu) {
    	for(StatisticsLog log: logs){
			if(log.getServiceName().equals(cid)) {
				//System.out.println("Found it");
				log.newStatistic(this.servName, cid, mem, cpu);
				break;
			}else {}
		}
    }

    public void setServName(String servName) {
        this.servName = servName;
    }
    
    public void setLogs(ArrayList<StatisticsLog> nlogs) {
    	this.logs = nlogs;
    }

	public ArrayList<StatisticsLog> getLogs() {
		return logs;
	}
	
	public StatisticsLog getLog(String sname) {
		for (StatisticsLog sl: logs) {
			if(sl.getServiceName().equals(sname)) {
				return sl;
			}
			break;
		}return null;
	}
    
    public boolean exists(Container c) {
    	if(containers.contains(c)) {
    		return true;
    	}else {
    		return false;
    	}
    }

	public SLO getSlo() {
		return slo;
	}

	public void setSlo(SLO slo) {
		this.slo = slo;
	}
    
    
     
}
