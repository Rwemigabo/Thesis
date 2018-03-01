/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.monitor;

/**
 *To define the boundaries for a particular system property.
 * @author eric
 */
public class Threshold {
    private double lowerBound;
    private double upperBound;
    
    public Threshold(double upper, double lower){
        this.lowerBound = lower;
        this.upperBound = upper;
    }
    
    public void setUpperBound(double upper){
        this.upperBound = upper;
    }
    
    public void setLowerBound(double lower){
        this.lowerBound = lower;
    }
    
    public double getLowerBound(){
        return this.lowerBound;
    
    }
    
    public double getUpperBound(){
        return this.upperBound;
    }
    
}
