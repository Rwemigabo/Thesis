/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.analyse;

/**
 * this class creates a symptom object, which indicates how many containers need to be added or removed in order to
 * optimize the application's service.
 * 
 * @author eric
 */
public class Symptom {
    private final String name;
    private final String event;
    private final double condition;//number of containers

    public Symptom(String name, String event, double condition) {
        this.name = name;
        this.event = event;
        this.condition = condition;
        System.out.println("New Symptom for "+ this.name + " = " + condition);
    }

    
    
    public String getName() {
        return name;
    }

    public String getEvent() {
        return event;
    }

    public double getCondition() {
        return condition;
    }

}
