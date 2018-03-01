/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.monitor;

/**
 * TBD: Apply inheritance relationship for the unique properties.
 *to define the system properties with monitoring interest and assign or 
 * reassign them a threshold. 
 * 
 * This monitors a stream of data from one of the selected docker container properties(cpu usage, blockI/O, etc)
 * @author eric
 */
public class ContextElement {
    private final Threshold thresh;
    final String name;

    
    public ContextElement(double upper, double lower, String nm){
        this.thresh = new Threshold(upper, lower);
        this.name = nm;
        
    }
    
    public void setThreshold(double upper, double lower){
        this.thresh.setLowerBound(lower);
        this.thresh.setUpperBound(upper);
    }
    
    public Threshold getThreshold(){
        return this.thresh;
    }
    
    public String getName(){
        System.out.println("Wrong get name");
        return "";
    }
    
    public double calculateCPU(){
        System.out.println("Wrong get cpu");
        return 0;
    }
    
    public double getMemoryPerc() {
        System.out.println("Wrong get memory");
        return 0;
    }
    
}
