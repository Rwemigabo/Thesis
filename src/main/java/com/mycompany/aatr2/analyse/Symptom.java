/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.analyse;

/**
 * this class indicates to what level the Service level objectives are being met
 * 
 * @author eric
 */
public class Symptom {
    private final String name;
    private String event;
    private String condition;

    public Symptom(String name, String event, String condition) {
        this.name = name;
        this.event = event;
        this.condition = condition;
    }

    public String getName() {
        return name;
    }

    public String getEvent() {
        return event;
    }

    public String getCondition() {
        return condition;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
    
    
    
}
