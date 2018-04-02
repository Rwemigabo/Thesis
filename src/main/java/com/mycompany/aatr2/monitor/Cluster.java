/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.monitor;

import com.spotify.docker.client.messages.Container;
import java.util.ArrayList;
import java.util.List;

/**
 *  Creates a service object with the containers that make up the functionality of the service
 * @author eric
 */
public class Cluster {
     private final List<Container> containers;
     private String servName;
     
     public Cluster(String name){
         this.servName = name;
         this.containers = new ArrayList<>();
     }

    public List<Container> getContainers() {
        return this.containers;
    }

    public String getServName() {
        return servName;
    }

    public void addContainer(Container container) {
    	if(!this.containers.contains(container)) {
    		this.containers.add(container);
        }else {
        	System.out.println("container already exists");
        }
    }

    public void setServName(String servName) {
        this.servName = servName;
    }
    
    
     
}
