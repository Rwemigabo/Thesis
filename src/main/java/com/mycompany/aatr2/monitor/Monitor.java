/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.monitor;

import com.mycompany.aatr2.Observable;
import com.mycompany.aatr2.Observer;
import com.mycompany.aatr2.SensorManager;
import com.mycompany.aatr2.monitor.data.StatisticsLog;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TBD monitor/ notify other monitor instances
 *
 * @author eric Capture and record new statistics to the database every set
 * number of minutes or seconds.
 */
public class Monitor implements Observer, Observable {

    private final StatisticsLog stats;
    private final int ID;
//    private final String id;
    private final ArrayList<Sensor> sens;
    private final ArrayList<Observer> obs;
    private final List<Container> conts;
    private Cluster serv;

    //private Observable obs = null;
    /**
     *
     * @param id an id for the new monitor
     * @param s service being monitored.
     */
    public Monitor(int id, Cluster s) throws DockerException, InterruptedException {
        this.sens = new ArrayList<>();
        this.ID = id;
//        this.id = ID;
        this.serv = s;
        this.stats = new StatisticsLog(serv.getServName());
        this.obs = new ArrayList<>();
        this.conts = this.serv.getContainers();
        initiate();
    }

    private void initiate() throws DockerException, InterruptedException {
        SensorManager sm = SensorManager.getInstance();
        for (Container container : conts) {

            addSensor(sm.newSensor("CPU", 0.00, 75.00, container.id()));
            addSensor(sm.newSensor("Memory", 0, 5, container.id()));
            if (container.state() != null && container.state().equals("running")) {
                System.out.print("\n Accessing sensors to initiate metric watch");
                startMonitoring(container.id());
                scheduleNotification();
            } else {
                System.out.print("Sorry container state " + container.state());
            }
        }
    }

    @Override
    public synchronized void update() {
        System.out.println("whaaatttt");
    }

    @Override
    public synchronized void update(String context, double metric) {
        double metric2 = 0;
        for (Sensor sen : sens) {
            if (!sen.sensorContext().equals(context)) {
                metric2 = sen.getLogValue();
            }
            if (context.equals("CPU")) {
                this.stats.newStatistic(this.serv.getServName(), sen.getContID(), metric, metric2);

            } else {
                this.stats.newStatistic(this.serv.getServName(), sen.getContID(), metric2, metric);
            }
            notifyObservers();
        }
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

    }

    public void startMonitoring(String id) throws DockerException, InterruptedException {
        sens.forEach((Sensor sen) -> {
            if (sen.getContID().equals(id)) {
                System.out.print("\n Initiating Sensor for " + sen.sensorContext() + " " + sen.getContID());
                setObservable(sen);
                sen.start();
            } else {
            }
        });
    }

    public void addSensor(Sensor s) {
        this.sens.add(s);
    }

    /**
     * Notifies observers every 30 seconds
     */
    public void scheduleNotification() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                newStatistic();
            }
        }, 1 * 10000, 1 * 10000);
    }

    public void newStatistic() {
        double metric1 = 0;
        double metric2 = 0;
        for (Container cont : this.conts) {
            for (Sensor sen : this.sens) {
                if (sen.getContID().equals(cont.id())) {

                    if (sen.getName().equals("CPU")) {
                        metric1 = sen.getLogValue();
                    } else {
                        metric2 = sen.getLogValue();
                    }
                }
            }this.stats.newStatistic(this.serv.getServName(), cont.id(), metric2, metric1);
        }
        notifyObservers();
        System.out.println("\n New Stat log from " + this.serv.getServName() + " Memory " + metric2 + " CPU " + metric1);
    }

    public int getID() {
        return this.ID;
    }

    @Override
    public void notifyObservers() {
        obs.forEach((ob) -> {
            ob.update();
        });
    }

    @Override
    public void setObservable(Observable obb) {
        obb.addObserver(this);
    }

    public StatisticsLog getStats() {
        return stats;
    }

    public Cluster getServ() {
        return serv;
    }

    public void setServ(Cluster serv) {
        this.serv = serv;
    }

}
