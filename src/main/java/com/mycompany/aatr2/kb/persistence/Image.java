/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.kb.persistence;

/**
 *
 * @author eric
 */
public class Image {
    private final String id;
    private String tag;
    private String created;
    private int  size;
    private String repository;
    
    public Image(String ID,String tag, String created, int size, String repo){
        this.id = ID;
        this.tag = tag;
        this.repository = repo;
        this.size = size;
        this.created = created;
    }
    
    public String getID(){
        return this.id;
    }
    
    public String getRepository(){
        return this.repository;
    }
    
    public String getTag(){
        return this.tag;
    }
    
    /**
     * 
     * @return the size of the image
    */
    public int getSize(){
        return this.size;
    }
    
    public String getCreated(){
        return this.created;
    }
    
    public void setRepository(String reponame){
        this.repository = reponame;
    }
    
    public void setCreated(String created){
        this.created = created;
    }
    
    public void setTag(String tag){
        this.tag = tag;
    }
    
    public void setSize(int size){
        this.size = size;
    }
}
