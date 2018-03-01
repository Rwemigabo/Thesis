/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2;


import com.mycompany.aatr2.analyse.Analyser;

import java.util.ArrayList;

/**
 *
 * @author eric
 */
public class AnalyseManager {

    private final ArrayList<Analyser> analysers = new ArrayList<>();
    private static final AnalyseManager inst = new AnalyseManager();
    
    
    public static AnalyseManager getInstance() {
        return inst;
    }
    
    private AnalyseManager(){
    
    }

    public ArrayList<Analyser> getAnalysers() {
        return analysers;
    }

    public static AnalyseManager getInst() {
        return inst;
    }  


}