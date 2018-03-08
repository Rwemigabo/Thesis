/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.monitor;

import com.spotify.docker.client.messages.Container;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *  Creates a service object with the containers that make up the functionality of the service
 * @author eric
 */
public class Service {
     private final List<Container> containers;
     private String servName;
     
     public Service(String name){
         this.servName = name;
         this.containers = new ArrayList<>();
     }

    public List<Container> getContainers() {
        return Collections.unmodifiableList(containers);
    }

    public String getServName() {
        return servName;
    }

    public void addContainer(Container container) {
        this.containers.add(container);
    }

    public void setServName(String servName) {
        this.servName = servName;
    }
    
    
     
}
