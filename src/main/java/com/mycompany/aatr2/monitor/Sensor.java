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
    private ContextElement property = null;
    private String name;
    private CpuStats cpu;
    private CpuStats pcpu;
    private MemoryStats mem;
    private NetworkStats net;
    private final String contID;
    private double cpuPerc;
    private double free;
    private final DockerManager dm = DockerManager.getInstance();
    private final String contNm;
    private double minimum;
    private double maximum;
    private long uptime;

    public Sensor(int ID, String context, double min, double max, String cid) {
        this.sensId = ID;
        this.name = context;
        this.maximum = max;
        this.minimum = min;
        this.contID = cid;
        this.property = new ContextElement(max, min, context);
        this.contNm = dm.getContainer(cid).image();
        uptime = dm.getContainer(cid).created();
    }

    /**
     * Notify if the CPU threshold is crossed .
     * 
     * @throws DockerException
     * @throws InterruptedException
     */
    public void watchCPU() throws DockerException, InterruptedException {
        this.net = dm.getContainerStats(contID).network();
        this.cpu = dm.getContainerStats(this.contID).cpuStats();

        if (dm.getContainer(this.contID).state().contains("running")) {
            this.cpuPerc = calculateCPU(cpu.cpuUsage().totalUsage(), 
                    dm.getContainerStats(this.contID).precpuStats().cpuUsage().totalUsage(),
                    cpu.systemCpuUsage(), dm.getContainerStats(this.contID).precpuStats().systemCpuUsage(),
                    cpu.cpuUsage().percpuUsage().size());
            checkThreshold(this.cpuPerc, contNm);
//                System.gc();

        }
    }

    public String getContID() {
        return contID;
    }
    
    
    /**
     * Notify if the Memory threshold is crossed .
     * 
     * @throws DockerException
     * @throws InterruptedException
     */
    public void watchMemory() throws DockerException, InterruptedException {
        this.mem = dm.getContainerStats(this.contID).memoryStats();
        if (dm.getContainer(this.contID).state().contains("running")) {
            this.free = memoryStat(this.mem.limit(), this.mem.usage());
            checkThreshold(free, contNm);
        }
    }

    @Override
    public void run() {
        if (this.name.equals("CPU")) {
            
            System.out.print("\n Monitoring CPU of conitainer " + contNm);
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {
                    try {
                        watchCPU();
                    } catch (DockerException | InterruptedException ex) {
                        Logger.getLogger(Sensor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }, 1, 1 * 2000);
        }else if (this.name.equals("Memory")) {
//            this.property = new Memory(this.maximum, this.minimum, this.name, this.mem);
            System.out.print("\n Monitoring Memory of conitainer " + contNm);
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {
                    try {
                        watchMemory();
                    } catch (DockerException | InterruptedException ex) {
                        Logger.getLogger(Sensor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }, 1, 1 * 2000);
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

    private void setWatchedMetric() {

    }
}
