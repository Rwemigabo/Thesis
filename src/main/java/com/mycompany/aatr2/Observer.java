/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2;

/**
 *
 * @author eric
 */
public interface Observer {
    public void update();
    public void update(String context, double metric);
    public void setObservable(Observable ob);
}
