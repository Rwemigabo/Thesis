/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2;

import com.mycompany.aatr2.monitor.Sensor;
import java.util.ArrayList;

/**
 *
 * @author eric
 */
public class SensorManager {
    private final ArrayList<Sensor> sensors = new ArrayList<>();
    
    
    private static final SensorManager instance = new SensorManager();
    
    public SensorManager(){
    }
    
    public static SensorManager getInstance(){
        return instance;
    }
    
    public Sensor newSensor(String context, double min, double max, String cid){
        int newID = sensors.size()+1;
        Sensor sens = new Sensor(newID, context, min, max, cid); 
        sensors.add(sens);
        return sens;
    }
    
    public void newSensor2(String context, float min, float max, String cid){
        int newID = sensors.size()+1;
        Sensor sens = new Sensor(newID, context, min, max, cid); 
        sensors.add(sens);
    }
    
    public ArrayList<Sensor> getAllSensors(){
        return this.sensors;
    }
    
    public Sensor getSeneor(int ID){
        for (Sensor sen : sensors) {
            if (sen.getID() == ID){
                return sen;
            }
        }return null;
    }
    
//    public ArrayList<Sensor> getSystemStats(Timestamp t1, Timestamp t2){
//        
//        ArrayList<Statistic> s = new ArrayList<>();
//        for (Statistic stat : monitorstats) {
//            if (stat.getTimestamp().after(t1) && stat.getTimestamp().before(t2)){
//                s.add(stat);
//            }
//        }return s;
//    }

}
