/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.monitor.data;
import java.io.Serializable;
import java.sql.Timestamp;
/**
 *
 * @author eric
 */
public class Statistic implements Serializable{
	
	private static final long serialVersionUID = 1L;
    private final String containerID;
    private final String servicenm;
    private final double cpu;
    private final double memory;
    //private final int BIO;
    //private final int network;
    private final Timestamp date;
    private boolean SLO;
    
    /**
     * @param snme
     * @param cid
     * @param setcpu
     * @param setmemory
     */
    public Statistic(String snme, String cid, double setcpu, double setmemory){
        this.cpu = setcpu;
        //this.BIO = setBIO;
        this.memory = setmemory;
        //this.network = setnetwork;
        this.date = new Timestamp(System.currentTimeMillis());
        this.containerID = cid;
        this.servicenm = snme;
    }


    public double getCpu(){
        return this.cpu;
    }

    public double getMemory(){
        return this.memory;
    }

//    public int getBIO() {
//        return this.BIO;
//    }
//
//    public int getNetwork() {
//        return this.network;
//    }
//
    public Timestamp getTimestamp() {
        return this.date;
    }

	public String getServiceName() {
		return servicenm;
	}

	public String getContainerID() {
		return containerID;
	}

	public boolean isSLO() {
		return SLO;
	}

	public void setSLO(boolean sLO) {
		SLO = sLO;
		
	}
	
//	@Override
//	public String toString() {
//		return new StringBuffer(" Street : ")
//				.append(this.street).append(" Country : ")
//				.append(this.country).toString();
//	}
    
    
}
