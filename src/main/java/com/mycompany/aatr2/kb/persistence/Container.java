/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.kb.persistence;

/**
 * container object
 * @author eric
 */
public class Container {
    private final String id;
    private final String image;
    private String status;
    private String port;
    private String name;
    
    public Container(String ID,String image, String status, String port, String name){
        this.id = ID;
        this.image = image;
        this.name = name;
        this.port = port;
        this.status = status;
    }
    
    public String getID(){
        return this.id;
    }
    
    public String getImage(){
        return this.image;
    }
    
    public String getStatus(){
        return this.status;
    }
    
    public String getPort(){
        return this.port;
    }
    
    public String getName(){
        return this.name;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public void setStatus(String status){
        this.status = status;
    }
    
    public void setPort(String port){
        this.port = port;
    }
}
