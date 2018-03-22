/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.analyse;

import com.mycompany.aatr2.Observable;
import com.mycompany.aatr2.Observer;
import com.mycompany.aatr2.monitor.Monitor;
import com.mycompany.aatr2.monitor.data.StatisticsLog;
import java.util.ArrayList;
import java.util.function.UnaryOperator;

/**
 * Analyze class performs an analysis on the data received from the monitor
 * @author eric
 */
public class Analyser implements Observable, Observer{
    
    private final int anId;
    private final ArrayList<Observer> obs;
    private final ArrayList<Monitor> obvle;
    private final ArrayList<StatisticsLog> logs;
    
    public Analyser(int id) {
        this.anId = id;
        this.obs = new ArrayList<>();
        this.logs = new ArrayList<>();
        this.obvle = new ArrayList<>();
    }
    
    /**
     * Get the value of anId
     *
     * @return the value of anId
     */
    public int getAnId() {
        return anId;
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
    public void notifyObservers() {
        obs.forEach((ob) -> {
            ob.update();
        });
    }

    @Override
    public void notifyObservers(double metric) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @SuppressWarnings("unchecked")
	@Override
    public synchronized void update() {
        
        obvle.forEach((obv)->{
            this.logs.replaceAll((UnaryOperator<StatisticsLog>) obv.getStats());
        });
    }

    @Override
    public void update(String context, double metric) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public void setObservable(Observable ob) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
