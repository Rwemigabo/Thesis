/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2;

import com.mycompany.aatr2.monitor.Monitor;
import com.mycompany.aatr2.monitor.Cluster;
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
    private final SensorManager sm;

    private static final MonitorManager inst = new MonitorManager();

    private MonitorManager() {
        sm = SensorManager.getInstance();
    }

    public static MonitorManager getInstance() {
        return inst;
    }

    /**
     *New Monitor created for a container with
     * @param s the service going to be monitored
     * @throws com.spotify.docker.client.exceptions.DockerException
     * @throws java.lang.InterruptedException
     */
    public void newMonitor(Cluster s) throws DockerException, InterruptedException {
        DockerManager dm = DockerManager.getInstance();
        int newID = monitors.size() + 1;
        Monitor mon = new Monitor(newID, s);
        monitors.add(mon);
//        mon.addSensor(this.sm.newSensor("CPU",  0.00, 75.00, ID));
//        mon.addSensor(this.sm.newSensor("Memory", 0, 5, ID));
        
//        if(dm.getContainer(ID).state() != null && dm.getContainer(ID).state().equals("running")){
//            System.out.print("\n Accessing sensors to initiate metric watch");
//            startMonitor(newID);
//        }else{System.out.print("Sorry container state " + dm.getContainer(ID).state());}
    }

//    public void startMonitor(int ID) throws DockerException, InterruptedException {
//        for (Monitor monitor : monitors) {
//            if (monitor.getID() == ID) {
//                monitor.startMonitoring(container.id());
//            }
//        }
//    }

//    public void startMonitors() throws DockerException, InterruptedException {
//        for (Monitor monitor : monitors) {
//            monitor.startMonitoring();
//        }
//    }

}
