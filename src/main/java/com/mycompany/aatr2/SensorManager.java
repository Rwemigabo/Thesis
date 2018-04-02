/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2;

import com.mycompany.aatr2.monitor.Sensor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author eric
 */
public class SensorManager {
    private final ArrayList<Sensor> sensors = new ArrayList<>();
    private HashMap<String, ArrayList<Sensor>> cSens = new HashMap<>();
    
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
        System.out.println("\n New Sensor for " + cid +"'s "+ context);
        addToMap(cid, sens);
        return sens;
    }
    
    /**
     * checks the key in the map and adds the new sensor to an existing list or creates new key and list. 
     * @param cid container id
     * @param sens Sensor to be added to the array value
     */
    public void addToMap(String cid, Sensor sens) {
    	if(cSens.containsKey(cid)) {
    		cSens.get(cid).add(sens);
    	}
    	else {
    		ArrayList<Sensor> slist = new ArrayList<>();
    		slist.add(sens);
    		cSens.put(cid, slist);
    	}
    }
    
    public ArrayList<Sensor> getContainorSensors(String cid){
    	return cSens.get(cid);	
    }
    
    public void newSensor2(String context, float min, float max, String cid){
        int newID = sensors.size()+1;
        Sensor sens = new Sensor(newID, context, min, max, cid); 
        sensors.add(sens);
    }
    
    public List<Sensor> getAllSensors(){
        List<Sensor> senss = this.sensors;
        return senss;
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
