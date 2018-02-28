/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.monitor;

import com.mycompany.aatr2.DockerManager;
import com.mycompany.aatr2.Observable;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.CpuStats;
import com.spotify.docker.client.messages.MemoryStats;
import java.util.ArrayList;
import com.mycompany.aatr2.Observer;
import com.spotify.docker.client.messages.NetworkStats;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A sensor observes the context element specified to it and notifies it's
 * subscribers of any changes. A context element name has to be provided and
 * that property will be set to be watched.
 *
 * @author eric
 */
public class Sensor extends Thread implements Observable {

    private final int sensId;
    private final ArrayList<Observer> obs = new ArrayList<>();
    private final ContextElement property;
    private String name;
    private CpuStats cpu;
    private MemoryStats mem;
    private NetworkStats net;
    private final String contID;
    private boolean now;
    private double cpuPerc;
    private double free;

    public Sensor(int ID, String context, double min, double max, String cid) {
        this.sensId = ID;
        this.name = context;
        this.property = new ContextElement(context);
        this.contID = cid;
        this.property.setThreshold(max, min);
        this.now = false;
    }

    /**
     * Notify the observers every 30 seconds of the current status or notify if
     * the threshold is crossed .
     *
     * @throws DockerException
     * @throws InterruptedException
     */
    public void watchContainer() throws DockerException, InterruptedException {
        DockerManager dm = DockerManager.getInstance();
        this.net = dm.getContainerStats(contID).network();
        this.cpu = dm.getContainerStats(this.contID).cpuStats();
        this.mem = dm.getContainerStats(this.contID).memoryStats();
        String contNm = dm.getContainer(this.contID).image();

        if (this.property.getName().equals("CPU")) {
            System.out.print("\n Monitoring CPU of conitainer " + contNm);
            long cpuUsage;
            int perCpu;
            long prevCPU;
            long prevSystem;
            long systemUsage;
            
            
            scheduleNotification();

            while (dm.getContainer(this.contID).state().contains("running")) {
                cpuUsage = cpu.cpuUsage().totalUsage();
                perCpu = cpu.cpuUsage().percpuUsage().size();
                prevCPU = dm.getContainerStats(this.contID).precpuStats().cpuUsage().totalUsage();
                prevSystem = dm.getContainerStats(this.contID).precpuStats().systemCpuUsage();
                systemUsage = cpu.systemCpuUsage();
                this.cpuPerc = calculateCPU(cpuUsage, prevCPU, systemUsage, prevSystem, perCpu);
                checkThreshold(this.cpuPerc, contNm);

            }
        } else if (this.property.getName().equals("Memory")) {
            System.out.print("\n Monitoring Memory of conitainer " + contNm);
            long limit;
            long used;

            scheduleNotification();
            while (dm.getContainer(this.contID).state().contains("running")) {
                limit = this.mem.limit();
                used = this.mem.usage();
                this.free = memoryStat(limit, used);
                checkThreshold(free, contNm);
                
            }
        }
    }

    @Override
    public void run() {
        try {
            watchContainer();
        } catch (DockerException | InterruptedException ex) {
            Logger.getLogger(Sensor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Method to monitor the threshold of the given metric notify the observers
     * if value is over or under threshold
     *
     * @param metric the metric being monitored by the sensor
     * @param cont container name
     */
    public void checkThreshold(final double metric, final String cont) {
        if (metric > this.property.getThreshold().getUpperBound() || metric < this.property.getThreshold().getLowerBound()) {
            System.out.print("\n Notifying monitor of container " + cont + " " + this.property.getName() + " " + metric + "%");
            notifyObservers(metric);
        } else {
        }

    }

    /**
     * Calculates the percentage CPU being used.
     *
     * @param totalUsage current total CPU usage of the container
     * @param prevCPU previous CPU usage
     * @param totalSystUse current Total system CPU usage
     * @param prevSystem previous total system CPU usage
     * @param perCpuUsage number of cores
     * @return the CPU percentage being used
     */
    public double calculateCPU(long totalUsage, long prevCPU, long totalSystUse, long prevSystem, int perCpuUsage) {

        double cpuP = 0;
        long cpuDelta = totalUsage - prevCPU;
        long systemDelta = totalSystUse - prevSystem;

        if (systemDelta > 0.0 && cpuDelta > 0.0) {
            cpuP = ((cpuDelta / systemDelta) * (perCpuUsage)) * 100;
        }

        return cpuP;

    }

    /**
     *
     * @param limit
     * @param usage
     * @return percentage of the memory used.
     */
    public double memoryStat(long limit, long usage) {

        double memMetric = (usage / limit) * 100;
        //double memMetric = (limit-usage);
        return memMetric;
    }

    /**
     * Notifies observers every 30 seconds
     */
    public void scheduleNotification() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                setWhen(true);
            }
        }, 1, 1 * 5000);
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
    public void notifyObservers(double metric) {
        obs.forEach((ob) -> {
            ob.update(this.name, metric);
        });
    }

    public int getID() {
        return this.sensId;
    }

    public String sensorContext() {
        return this.name;
    }

    public void setContext(String ctxt, long min, long max) {
        this.name = ctxt;
    }

    public double getLogValue() {
        
        if (this.name.equals("Memory")) {
            return this.free;
        } else if (this.name.equals("CPU")) {
            return this.cpuPerc;
        }
        return 0;
    }

    @Override
    public void notifyObservers() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void setWhen(boolean nw) {
        this.now = nw;
    }
}
