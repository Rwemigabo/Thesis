/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.monitor;

import com.spotify.docker.client.exceptions.DockerException;
import java.util.ArrayList;

/**
 * Creates new monitors
 * Manages all the working monitors
 *
 * @author eric
 */
public class MonitorManager {

    private final ArrayList<Monitor> monitors = new ArrayList<>();
    //private final SensorManager sm;

    private static final MonitorManager inst = new MonitorManager();

    private MonitorManager() {
        //sm = SensorManager.getInstance();
    }

    public static MonitorManager getInstance() {
        return inst;
    }

    /**
     *New Monitor created for a service with
     * @param s the service going to be monitored
     * @throws com.spotify.docker.client.exceptions.DockerException
     * @throws java.lang.InterruptedException
     */
    public void newMonitor(Cluster serv) throws DockerException, InterruptedException {
        //DockerManager dm = DockerManager.getInstance();
        int newID = monitors.size() + 1;
        Monitor mon = new Monitor(newID, serv);
        mon.initiate();
        monitors.add(mon);
    }

	public ArrayList<Monitor> getMonitors() {
		return monitors;
	}
    
	/**
	 * 
	 * @param m
	 * @return monitor belonging to the cluster
	 */
    public Monitor getMonitor(Cluster c) {
    	Monitor mon = null;
    	for(Monitor m: this.monitors) {
    		if(m.getService().equals(c)) {
    			mon = m;
    			break;
    		}
    	}
		return mon;
    }

}
